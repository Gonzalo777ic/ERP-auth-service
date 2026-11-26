package com.valeerp.hr;

import com.valeerp.hr.dto.ContractCommands;
import com.valeerp.hr.dto.ContractReadModel;
import com.valeerp.hr.dto.MoneyDto;
import com.valeerp.kernel.Actors;
import com.valeerp.kernel.Currency;
import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Líneas del libro de contrato: sueldo, cargo, horario, afiliación, renovación y anexo.
 */
@Service
public class ContractHistoryService {

    private final EmploymentContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractWageRepository wageRepository;
    private final ContractRoleRepository roleRepository;
    private final ContractShiftRepository shiftRepository;
    private final ContractAffiliationRepository affiliationRepository;
    private final ContractRenewalRepository renewalRepository;
    private final ContractAnnexRepository annexRepository;
    private final HrProperties properties;

    public ContractHistoryService(
            EmploymentContractRepository contractRepository,
            EmployeeRepository employeeRepository,
            ContractWageRepository wageRepository,
            ContractRoleRepository roleRepository,
            ContractShiftRepository shiftRepository,
            ContractAffiliationRepository affiliationRepository,
            ContractRenewalRepository renewalRepository,
            ContractAnnexRepository annexRepository,
            HrProperties properties
    ) {
        this.contractRepository = contractRepository;
        this.employeeRepository = employeeRepository;
        this.wageRepository = wageRepository;
        this.roleRepository = roleRepository;
        this.shiftRepository = shiftRepository;
        this.affiliationRepository = affiliationRepository;
        this.renewalRepository = renewalRepository;
        this.annexRepository = annexRepository;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public ContractReadModel.Detail detail(Long contractId) {
        EmploymentContract contract = requireContract(contractId);
        Employee employee = requireEmployee(contract.getEmployeeId());
        return new ContractReadModel.Detail(
                ContractReadModel.summary(contract, employee),
                ContractReadModel.terms(contract),
                MoneyDto.from(contract.getWage()),
                wageRepository.findByContractIdOrderByValidFromDesc(contractId).stream().map(ContractReadModel.WageRow::from).toList(),
                roleRepository.findByContractIdOrderByStartsOnDesc(contractId).stream().map(ContractReadModel.RoleRow::from).toList(),
                shiftRepository.findByContractIdOrderByStartsOnDesc(contractId).stream().map(ContractReadModel.ShiftRow::from).toList(),
                affiliationRepository.findByContractIdOrderByIdAsc(contractId).stream().map(ContractReadModel.AffiliationRow::from).toList(),
                renewalRepository.findByContractIdOrderByStartsOnDesc(contractId).stream().map(ContractReadModel.RenewalRow::from).toList(),
                annexRepository.findByContractIdOrderByRecordedAtDesc(contractId).stream().map(ContractReadModel.AnnexRow::from).toList()
        );
    }

    @Transactional
    public ContractReadModel.WageRow proposeWage(Long contractId, ContractCommands.Wage request) {
        EmploymentContract contract = requireOpen(contractId);
        if (request.getValidFrom().isBefore(contract.getStartsOn())) {
            throw new DomainException("La vigencia del sueldo no puede ser anterior al inicio del contrato");
        }
        Money amount = money(request.getAmount());
        ContractWage wage = wageRepository.save(ContractWage.propose(contractId, amount, request.getValidFrom(), Instant.now()));
        return ContractReadModel.WageRow.from(wage);
    }

    @Transactional
    public ContractReadModel.WageRow approveWage(Long contractId, Long wageId) {
        EmploymentContract contract = requireOpen(contractId);
        ContractWage wage = requireWage(contractId, wageId);
        Instant now = Instant.now();
        wage.approve(Actors.current(), now);
        wageRepository.findByContractIdOrderByValidFromDesc(contractId).stream()
                .filter(other -> !other.getId().equals(wage.getId()))
                .filter(other -> other.getStatus() == WageChangeStatus.APPROVED)
                .forEach(ContractWage::supersede);
        contract.replaceWage(wage.getAmount());
        contractRepository.save(contract);
        return ContractReadModel.WageRow.from(wageRepository.save(wage));
    }

    @Transactional
    public void recordOpeningWage(EmploymentContract contract, Money amount, String actor) {
        wageRepository.save(ContractWage.approved(contract.getId(), amount, contract.getStartsOn(), Instant.now(), actor));
    }

    @Transactional
    public ContractReadModel.RoleRow addRole(Long contractId, ContractCommands.Role request) {
        EmploymentContract contract = requireOpen(contractId);
        if (request.getStartsOn().isBefore(contract.getStartsOn())) {
            throw new DomainException("El cargo no puede empezar antes que el contrato");
        }
        roleRepository.findByContractIdOrderByStartsOnDesc(contractId).stream()
                .filter(role -> role.getEndsOn() == null)
                .findFirst()
                .ifPresent(open -> {
                    if (!request.getStartsOn().isAfter(open.getStartsOn())) {
                        throw new DomainException("El cargo nuevo debe empezar después del cargo vigente");
                    }
                    open.close(request.getStartsOn().minusDays(1));
                });
        return ContractReadModel.RoleRow.from(
                roleRepository.save(ContractRole.open(contractId, request.getTitle(), request.getStartsOn()))
        );
    }

    @Transactional
    public ContractReadModel.ShiftRow addShift(Long contractId, ContractCommands.Shift request) {
        EmploymentContract contract = requireOpen(contractId);
        if (request.getStartsOn().isBefore(contract.getStartsOn())) {
            throw new DomainException("El horario no puede empezar antes que el contrato");
        }
        return ContractReadModel.ShiftRow.from(
                shiftRepository.save(ContractShift.assign(contractId, request.getName(), request.getStartsOn()))
        );
    }

    @Transactional
    public ContractReadModel.AffiliationRow addAffiliation(Long contractId, ContractCommands.Affiliation request) {
        requireOpen(contractId);
        AffiliationAmountKind kind = AffiliationAmountKind.require(request.getAmountKind());
        Money amount = request.getAmount() == null || request.getAmount().getAmount() == null
                ? null
                : money(request.getAmount());
        return ContractReadModel.AffiliationRow.from(
                affiliationRepository.save(ContractAffiliation.open(contractId, request.getName(), kind, amount))
        );
    }

    @Transactional
    public ContractReadModel.AffiliationRow deactivateAffiliation(Long contractId, Long affiliationId) {
        requireContract(contractId);
        ContractAffiliation affiliation = affiliationRepository.findById(affiliationId)
                .filter(item -> contractId.equals(item.getContractId()))
                .orElseThrow(() -> new DomainException("Afiliación no encontrada"));
        affiliation.deactivate();
        return ContractReadModel.AffiliationRow.from(affiliationRepository.save(affiliation));
    }

    @Transactional
    public ContractReadModel.RenewalRow renew(Long contractId, ContractCommands.Renewal request) {
        EmploymentContract contract = requireOpen(contractId);
        if (request.getEndsOn().isBefore(contract.getStartsOn())) {
            throw new DomainException("El vencimiento no puede ser anterior al inicio del contrato");
        }
        contract.extendTo(request.getEndsOn());
        contractRepository.save(contract);
        return ContractReadModel.RenewalRow.from(renewalRepository.save(
                ContractRenewal.record(contractId, request.getStartsOn(), request.getEndsOn(), request.getNotes(), Instant.now())
        ));
    }

    @Transactional
    public ContractReadModel.AnnexRow addAnnex(Long contractId, ContractCommands.Annex request) {
        requireContract(contractId);
        return ContractReadModel.AnnexRow.from(annexRepository.save(
                ContractAnnex.record(contractId, request.getFileName(), request.getComment(), Actors.current(), Instant.now())
        ));
    }

    @Transactional(readOnly = true)
    public ContractReadModel.Statement statement(Long contractId, String kind) {
        EmploymentContract contract = requireContract(contractId);
        Employee employee = requireEmployee(contract.getEmployeeId());
        String title = roleRepository.findByContractIdOrderByStartsOnDesc(contractId).stream()
                .max(Comparator.comparing(ContractRole::getStartsOn))
                .map(ContractRole::getTitle)
                .orElse(null);
        String text = "cts".equalsIgnoreCase(kind)
                ? WorkStatement.cts(employee, contract, title)
                : WorkStatement.work(employee, contract, title);
        return new ContractReadModel.Statement(kind, text);
    }

    private Money money(MoneyDto dto) {
        if (dto == null || dto.getAmount() == null) {
            throw new DomainException("El importe es obligatorio");
        }
        Currency currency = (dto.getCurrency() == null || dto.getCurrency().isBlank())
                ? properties.getDefaultWageCurrency()
                : Currency.of(dto.getCurrency());
        return Money.of(dto.getAmount(), currency);
    }

    private EmploymentContract requireOpen(Long contractId) {
        EmploymentContract contract = requireContract(contractId);
        if (contract.getStatus() == ContractStatus.CANCELLED || contract.getStatus() == ContractStatus.COMPLETED) {
            throw new DomainException("El contrato está cerrado");
        }
        return contract;
    }

    private ContractWage requireWage(Long contractId, Long wageId) {
        return wageRepository.findById(wageId)
                .filter(wage -> contractId.equals(wage.getContractId()))
                .orElseThrow(() -> new DomainException("Sueldo no encontrado"));
    }

    private EmploymentContract requireContract(Long id) {
        if (id == null) {
            throw new DomainException("El contrato es obligatorio");
        }
        return contractRepository.findById(id).orElseThrow(() -> new DomainException("Contrato no encontrado"));
    }

    private Employee requireEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(() -> new DomainException("Empleado no encontrado"));
    }
}
