package com.valeerp.hr;

import com.valeerp.hr.dto.CreateEmployeeRequest;
import com.valeerp.hr.dto.EmployeeAddressRequest;
import com.valeerp.hr.dto.EmployeePhoneRequest;
import com.valeerp.hr.dto.EmployeeResponse;
import com.valeerp.hr.dto.LinkEmployeeUserRequest;
import com.valeerp.hr.dto.UpdateEmployeeRequest;
import com.valeerp.inventory.auth.repository.UserRepository;
import com.valeerp.kernel.Actors;
import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.company.CompanyProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Caso de uso de la ficha del trabajador. La entidad legal entra solo como id.
 * La edad se calcula con la zona horaria de {@code valeerp.company.default-time-zone}.
 */
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final CompanyProperties companyProperties;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            UserRepository userRepository,
            CompanyProperties companyProperties
    ) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.companyProperties = companyProperties;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listByLegalEntity(Long legalEntityId) {
        requireLegalEntity(legalEntityId);
        LocalDate today = today();
        return employeeRepository.findByLegalEntityId(legalEntityId).stream()
                .map(employee -> EmployeeResponse.from(employee, today))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        Employee employee = requireEmployee(id);
        return EmployeeResponse.from(employee, today());
    }

    @Transactional
    public EmployeeResponse create(CreateEmployeeRequest request) {
        requireLegalEntity(request.getLegalEntityId());
        IdentityDocument document = IdentityDocument.parse(request.getDocumentType(), request.getDocumentNumber());
        ensureDocumentAvailable(request.getLegalEntityId(), document, null);
        String cuspp = Cuspp.normalize(request.getCuspp());
        ensureCusppAvailable(request.getLegalEntityId(), cuspp, null);
        LocalDate today = today();
        Employee employee = Employee.register(
                request.getLegalEntityId(),
                document,
                request.getSurnames(),
                request.getGivenNames(),
                request.getBirthDate(),
                request.getSex(),
                request.getChildrenCount() == null ? 0 : request.getChildrenCount(),
                request.getPersonalEmail(),
                request.getWorkEmail(),
                cuspp,
                addressOf(request.getAddress()),
                phonesOf(request.getPhones()),
                today
        );
        return EmployeeResponse.from(employeeRepository.save(employee), today);
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest request) {
        Employee employee = requireEmployee(id);
        IdentityDocument document = IdentityDocument.parse(request.getDocumentType(), request.getDocumentNumber());
        ensureDocumentAvailable(employee.getLegalEntityId(), document, id);
        String cuspp = Cuspp.normalize(request.getCuspp());
        ensureCusppAvailable(employee.getLegalEntityId(), cuspp, id);
        LocalDate today = today();
        employee.correctProfile(
                document,
                request.getSurnames(),
                request.getGivenNames(),
                request.getBirthDate(),
                request.getSex(),
                request.getChildrenCount() == null ? 0 : request.getChildrenCount(),
                request.getPersonalEmail(),
                request.getWorkEmail(),
                cuspp,
                addressOf(request.getAddress()),
                phonesOf(request.getPhones()),
                today
        );
        return EmployeeResponse.from(employeeRepository.save(employee), today);
    }

    @Transactional
    public EmployeeResponse linkUser(Long id, LinkEmployeeUserRequest request) {
        Employee employee = requireEmployee(id);
        requireIdentityUser(request.getUserId());
        if (employeeRepository.existsByUserId(request.getUserId())
                && (employee.getUserId() == null || !employee.getUserId().equals(request.getUserId()))) {
            throw new DomainException("El usuario de identidad ya está vinculado a un empleado");
        }
        employee.linkUser(request.getUserId());
        return EmployeeResponse.from(employeeRepository.save(employee), today());
    }

    @Transactional
    public EmployeeResponse unlinkUser(Long id) {
        Employee employee = requireEmployee(id);
        employee.unlinkUser();
        return EmployeeResponse.from(employeeRepository.save(employee), today());
    }

    @Transactional
    public void retire(Long id) {
        Employee employee = requireEmployee(id);
        employee.retire(Actors.current(), Instant.now());
        employeeRepository.save(employee);
    }

    private void ensureDocumentAvailable(Long legalEntityId, IdentityDocument document, Long currentId) {
        employeeRepository
                .findByLegalEntityIdAndDocumentTypeAndDocumentNumber(legalEntityId, document.type(), document.number())
                .filter(other -> currentId == null || !currentId.equals(other.getId()))
                .ifPresent(other -> {
                    throw new DomainException("Ya existe un empleado vigente con ese documento en la entidad legal");
                });
    }

    private void ensureCusppAvailable(Long legalEntityId, String cuspp, Long currentId) {
        if (cuspp == null) {
            return;
        }
        employeeRepository.findByLegalEntityIdAndCuspp(legalEntityId, cuspp)
                .filter(other -> currentId == null || !currentId.equals(other.getId()))
                .ifPresent(other -> {
                    throw new DomainException("Ya existe un empleado vigente con ese CUSPP en la entidad legal");
                });
    }

    private List<EmployeePhone> phonesOf(List<EmployeePhoneRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }
        return requests.stream()
                .map(request -> EmployeePhone.of(request.getUse(), request.getNumber()))
                .toList();
    }

    private HomeAddress addressOf(EmployeeAddressRequest request) {
        if (request == null) {
            throw new DomainException("La dirección de domicilio es obligatoria");
        }
        return HomeAddress.of(request.getLine(), request.getDistrict(), request.getCity());
    }

    private LocalDate today() {
        return LocalDate.now(companyProperties.getDefaultTimeZone().toZoneId());
    }

    private void requireLegalEntity(Long legalEntityId) {
        if (legalEntityId == null) {
            throw new DomainException("La entidad legal es obligatoria");
        }
    }

    private void requireIdentityUser(Long userId) {
        if (userId == null) {
            throw new DomainException("El userId es obligatorio");
        }
        if (!userRepository.existsById(userId)) {
            throw new DomainException("Usuario de identidad no encontrado");
        }
    }

    private Employee requireEmployee(Long id) {
        if (id == null) {
            throw new DomainException("El empleado es obligatorio");
        }
        return employeeRepository.findById(id)
                .orElseThrow(() -> new DomainException("Empleado no encontrado"));
    }
}
