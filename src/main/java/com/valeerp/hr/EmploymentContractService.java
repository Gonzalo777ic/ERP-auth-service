package com.valeerp.hr;

import com.valeerp.hr.dto.CancelBookRequest;
import com.valeerp.hr.dto.CompleteContractRequest;
import com.valeerp.hr.dto.ContractCommands;
import com.valeerp.hr.dto.ContractReadModel;
import com.valeerp.hr.dto.ContractResponse;
import com.valeerp.hr.dto.CreateContractRequest;
import com.valeerp.hr.dto.MoneyDto;
import com.valeerp.kernel.Actors;
import com.valeerp.kernel.Currency;
import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Libro de contratos. Sin delete físico: activar / completar / anular por estado.
 */
@Service
public class EmploymentContractService {

    private final EmploymentContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final HrProperties properties;
    private final ContractHistoryService historyService;

    public EmploymentContractService(
            EmploymentContractRepository contractRepository,
            EmployeeRepository employeeRepository,
            HrProperties properties,
            ContractHistoryService historyService
    ) {
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
        this.properties = properties;
        this.historyService = historyService;
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listByEmployee(Long employeeId) {
        requireEmployee(employeeId);
        return contractRepository.findByEmployeeId(employeeId).stream().map(ContractResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listByCompany(Long companyId) {
        if (companyId == null) {
            throw new DomainException("La compañía es obligatoria");
        }
        return contractRepository.findByCompanyId(companyId).stream().map(ContractResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ContractReadModel.Summary> roster(Long legalEntityId, String document, String name, boolean activeOnly) {
        if (legalEntityId == null) {
            throw new DomainException("La entidad legal es obligatoria");
        }
        String documentQuery = normalize(document);
        String nameQuery = normalize(name);
        Map<Long, Employee> people = employeeRepository.findByLegalEntityId(legalEntityId).stream()
                .filter(employee -> matches(employee.getDocumentNumber(), documentQuery))
                .filter(employee -> matches(employee.displayName(), nameQuery))
                .collect(Collectors.toMap(Employee::getId, Function.identity()));
        if (people.isEmpty()) {
            return List.of();
        }
        return contractRepository.findByCompanyIdAndEmployeeIdIn(legalEntityId, people.keySet()).stream()
                .filter(contract -> !activeOnly || contract.getStatus() == ContractStatus.ACTIVE)
                .map(contract -> ContractReadModel.summary(contract, people.get(contract.getEmployeeId())))
                .toList();
    }

    @Transactional
    public ContractReadModel.Terms updateTerms(Long id, ContractCommands.Terms request) {
        EmploymentContract contract = requireMutable(id);
        contract.describeProfile(
                WorkerKind.require(request.getWorkerKind()),
                request.getPayrollGroup(),
                PensionRegime.require(request.getPensionRegime()),
                LaborRegime.require(request.getLaborRegime()),
                request.isAllowSalaryAdvance(),
                request.getNotes()
        );
        applyPayment(contract, true, request);
        applyPayment(contract, false, request);
        contract.retain(
                RetentionMode.require(request.getRetentionMode()),
                request.getRetentionValue(),
                request.getRetentionBeneficiary()
        );
        contractRepository.save(contract);
        return ContractReadModel.terms(contract);
    }

    @Transactional
    public ContractResponse cease(Long id, ContractCommands.Cease request) {
        EmploymentContract contract = requireMutable(id);
        contract.cease(request.getOn(), request.getReason(), Instant.now());
        return ContractResponse.from(contractRepository.save(contract));
    }

    @Transactional(readOnly = true)
    public ContractResponse get(Long id) {
        return ContractResponse.from(requireContract(id));
    }

    @Transactional
    public ContractResponse createDraft(CreateContractRequest request) {
        Employee employee = requireEmployee(request.getEmployeeId());
        ContractType type = resolveContractType(request.getContractType());
        Money wage = resolveWage(request.getWage());
        EmploymentContract contract = EmploymentContract.draft(
                employee.getId(),
                employee.getLegalEntityId(),
                type,
                request.getStartsOn(),
                request.getEndsOn(),
                wage,
                Instant.now()
        );
        EmploymentContract saved = contractRepository.save(contract);
        historyService.recordOpeningWage(saved, wage, Actors.current());
        return ContractResponse.from(saved);
    }

    @Transactional
    public ContractResponse activate(Long id) {
        EmploymentContract contract = requireContract(id);
        contract.activate(Instant.now());
        return ContractResponse.from(contractRepository.save(contract));
    }

    @Transactional
    public ContractResponse complete(Long id, CompleteContractRequest request) {
        EmploymentContract contract = requireContract(id);
        contract.complete(Instant.now(), request == null ? null : request.getEndsOn());
        return ContractResponse.from(contractRepository.save(contract));
    }

    @Transactional
    public ContractResponse cancel(Long id, CancelBookRequest request) {
        EmploymentContract contract = requireContract(id);
        String reason = request == null ? null : request.getReason();
        contract.cancel(Actors.current(), Instant.now(), reason);
        return ContractResponse.from(contractRepository.save(contract));
    }

    private ContractType resolveContractType(String raw) {
        if (raw == null || raw.isBlank()) {
            return properties.getDefaultContractType();
        }
        return ContractType.require(raw);
    }

    private Money resolveWage(MoneyDto dto) {
        if (dto == null || dto.getAmount() == null) {
            throw new DomainException("La remuneración es obligatoria");
        }
        Currency currency = (dto.getCurrency() == null || dto.getCurrency().isBlank())
                ? properties.getDefaultWageCurrency()
                : Currency.of(dto.getCurrency());
        return Money.of(dto.getAmount(), currency);
    }

    private void applyPayment(EmploymentContract contract, boolean wages, ContractCommands.Terms request) {
        String methodRaw = wages ? request.getWagePaymentMethod() : request.getCtsPaymentMethod();
        if (methodRaw == null || methodRaw.isBlank()) {
            return;
        }
        WagePaymentMethod method = WagePaymentMethod.require(methodRaw);
        BankAccount account = null;
        if (method == WagePaymentMethod.BANK_ACCOUNT) {
            String currency = wages ? request.getWageAccountCurrency() : request.getCtsAccountCurrency();
            account = BankAccount.of(
                    PayrollBank.require(wages ? request.getWageBank() : request.getCtsBank()),
                    BankAccountType.require(wages ? request.getWageAccountType() : request.getCtsAccountType()),
                    wages ? request.getWageAccountNumber() : request.getCtsAccountNumber(),
                    (currency == null || currency.isBlank()) ? properties.getDefaultWageCurrency().code() : currency
            );
        }
        if (wages) {
            contract.payWages(method, account);
        } else {
            contract.payCts(method, account);
        }
    }

    private EmploymentContract requireMutable(Long id) {
        EmploymentContract contract = requireContract(id);
        if (contract.getStatus() == ContractStatus.CANCELLED) {
            throw new DomainException("No se modifica un contrato anulado");
        }
        return contract;
    }

    private static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean matches(String value, String query) {
        return query.isEmpty() || (value != null && value.toLowerCase(Locale.ROOT).contains(query));
    }

    private Employee requireEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new DomainException("El empleado es obligatorio");
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("Empleado no encontrado"));
    }

    private EmploymentContract requireContract(Long id) {
        if (id == null) {
            throw new DomainException("El contrato es obligatorio");
        }
        return contractRepository.findById(id)
                .orElseThrow(() -> new DomainException("Contrato no encontrado"));
    }
}
