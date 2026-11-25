package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.SoftDeletable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Trabajador de la entidad legal. No es un {@code User} de identidad.
 * La clave de negocio es el documento (tipo + número) dentro de la entidad legal.
 * El id técnico existe para los libros (contrato, ausencia) y para reutilizar el documento tras la baja.
 */
@Entity
@Table(name = "hr_employees")
public class Employee extends SoftDeletable {

    public static final int NAME_MAX = 120;
    public static final int CHILDREN_MAX = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Id de la entidad legal empleadora. No es un usuario.
     * La columna conserva el nombre histórico {@code company_id}.
     */
    @Column(name = "company_id", nullable = false)
    private Long legalEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 16)
    private IdentityDocumentType documentType;

    @Column(name = "document_number", nullable = false, length = 32)
    private String documentNumber;

    @Column(nullable = false, length = 80)
    private String code;

    @Column(nullable = false, length = NAME_MAX)
    private String surnames;

    @Column(name = "given_names", nullable = false, length = NAME_MAX)
    private String givenNames;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Sex sex;

    @Column(name = "children_count", nullable = false)
    private int childrenCount;

    @Column(name = "personal_email", length = 180)
    private String personalEmail;

    @Column(name = "work_email", length = 180)
    private String workEmail;

    @Column(length = 12)
    private String cuspp;

    @Embedded
    private HomeAddress homeAddress;

    @ElementCollection
    @CollectionTable(name = "hr_employee_phones", joinColumns = @JoinColumn(name = "employee_id"))
    @OrderColumn(name = "phone_order")
    private List<EmployeePhone> phones = new ArrayList<>();

    protected Employee() {
    }

    public static Employee register(
            Long legalEntityId,
            IdentityDocument document,
            String surnames,
            String givenNames,
            LocalDate birthDate,
            Sex sex,
            int childrenCount,
            String personalEmail,
            String workEmail,
            String cuspp,
            HomeAddress homeAddress,
            List<EmployeePhone> phones,
            LocalDate today
    ) {
        Employee employee = new Employee();
        employee.bindLegalEntity(legalEntityId);
        employee.assignDocument(document);
        employee.rename(surnames, givenNames);
        employee.changeBirthDate(birthDate, today);
        employee.changeSex(sex);
        employee.changeChildrenCount(childrenCount);
        employee.changeEmails(personalEmail, workEmail);
        employee.changeCuspp(cuspp);
        employee.changeHomeAddress(homeAddress);
        employee.replacePhones(phones);
        return employee;
    }

    public void correctProfile(
            IdentityDocument document,
            String surnames,
            String givenNames,
            LocalDate birthDate,
            Sex sex,
            int childrenCount,
            String personalEmail,
            String workEmail,
            String cuspp,
            HomeAddress homeAddress,
            List<EmployeePhone> phones,
            LocalDate today
    ) {
        assignDocument(document);
        rename(surnames, givenNames);
        changeBirthDate(birthDate, today);
        changeSex(sex);
        changeChildrenCount(childrenCount);
        changeEmails(personalEmail, workEmail);
        changeCuspp(cuspp);
        changeHomeAddress(homeAddress);
        replacePhones(phones);
    }

    public void bindLegalEntity(Long legalEntityId) {
        if (legalEntityId == null) {
            throw new DomainException("La entidad legal es obligatoria");
        }
        this.legalEntityId = legalEntityId;
    }

    public void assignDocument(IdentityDocument document) {
        if (document == null) {
            throw new DomainException("El documento es obligatorio");
        }
        this.documentType = document.type();
        this.documentNumber = document.number();
        this.code = document.type().name() + "-" + document.number();
    }

    public void rename(String surnames, String givenNames) {
        this.surnames = requiredName(surnames, "Los apellidos son obligatorios");
        this.givenNames = requiredName(givenNames, "Los nombres son obligatorios");
        this.fullName = displayName();
    }

    public void changeBirthDate(LocalDate birthDate, LocalDate today) {
        if (birthDate == null) {
            throw new DomainException("La fecha de nacimiento es obligatoria");
        }
        if (today == null) {
            throw new DomainException("La fecha de referencia es obligatoria");
        }
        if (birthDate.isAfter(today)) {
            throw new DomainException("La fecha de nacimiento no puede ser futura");
        }
        this.birthDate = birthDate;
    }

    public int ageAt(LocalDate today) {
        if (birthDate == null || today == null || today.isBefore(birthDate)) {
            throw new DomainException("No se puede calcular la edad");
        }
        return Period.between(birthDate, today).getYears();
    }

    public void changeSex(Sex sex) {
        if (sex == null) {
            throw new DomainException("El sexo es obligatorio");
        }
        this.sex = sex;
    }

    public void changeChildrenCount(int childrenCount) {
        if (childrenCount < 0 || childrenCount > CHILDREN_MAX) {
            throw new DomainException("La cantidad de hijos no es válida");
        }
        this.childrenCount = childrenCount;
    }

    public void changeEmails(String personalEmail, String workEmail) {
        this.personalEmail = ContactEmail.normalize(personalEmail, "correo personal");
        this.workEmail = ContactEmail.normalize(workEmail, "correo laboral");
    }

    public void changeCuspp(String cuspp) {
        this.cuspp = Cuspp.normalize(cuspp);
    }

    public void changeHomeAddress(HomeAddress homeAddress) {
        if (homeAddress == null || homeAddress.getLine() == null || homeAddress.getLine().isBlank()) {
            throw new DomainException("La dirección de domicilio es obligatoria");
        }
        this.homeAddress = homeAddress;
    }

    public void replacePhones(List<EmployeePhone> incoming) {
        List<EmployeePhone> next = incoming == null ? List.of() : incoming;
        if (next.size() > EmployeePhone.MAX_PER_EMPLOYEE) {
            throw new DomainException("Un trabajador admite como máximo " + EmployeePhone.MAX_PER_EMPLOYEE + " teléfonos");
        }
        Set<String> seen = new HashSet<>();
        for (EmployeePhone phone : next) {
            if (phone == null || phone.getNumber() == null) {
                throw new DomainException("El teléfono es obligatorio");
            }
            if (!seen.add(phone.getNumber())) {
                throw new DomainException("El teléfono está repetido");
            }
        }
        this.phones.clear();
        this.phones.addAll(next);
    }

    public void linkUser(Long userId) {
        this.userId = userId;
    }

    public void unlinkUser() {
        this.userId = null;
    }

    public String displayName() {
        return surnames + " " + givenNames;
    }

    private static String requiredName(String raw, String message) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException(message);
        }
        String value = raw.trim();
        if (value.length() > NAME_MAX) {
            throw new DomainException("El nombre supera el máximo permitido");
        }
        return value;
    }

    public Long getId() {
        return id;
    }

    public Long getLegalEntityId() {
        return legalEntityId;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getSurnames() {
        return surnames;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public String getCuspp() {
        return cuspp;
    }

    public HomeAddress getHomeAddress() {
        return homeAddress;
    }

    public List<EmployeePhone> getPhones() {
        return List.copyOf(phones);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Employee employee)) {
            return false;
        }
        return Objects.equals(legalEntityId, employee.legalEntityId)
                && documentType == employee.documentType
                && Objects.equals(documentNumber, employee.documentNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(legalEntityId, documentType, documentNumber);
    }
}
