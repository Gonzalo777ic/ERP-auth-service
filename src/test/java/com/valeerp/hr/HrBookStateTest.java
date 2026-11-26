package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Transiciones de libros RRHH con fixtures literales. Sin Spring ni Auth0.
 */
class HrBookStateTest {

    @Test
    void contractActivatesCompletesAndRejectsDeletePath() {
        EmploymentContract contract = EmploymentContract.draft(
                10L,
                1L,
                ContractType.INDEFINITE,
                LocalDate.parse("2026-01-01"),
                null,
                Money.pen(new BigDecimal("2500.00")),
                Instant.parse("2026-01-01T10:00:00Z")
        );
        assertEquals(ContractStatus.DRAFT, contract.getStatus());

        contract.activate(Instant.parse("2026-01-02T10:00:00Z"));
        assertEquals(ContractStatus.ACTIVE, contract.getStatus());

        contract.complete(Instant.parse("2026-12-31T10:00:00Z"), LocalDate.parse("2026-12-31"));
        assertEquals(ContractStatus.COMPLETED, contract.getStatus());

        assertThrows(
                DomainException.class,
                () -> contract.cancel("admin", Instant.parse("2027-01-01T10:00:00Z"), "tarde")
        );
    }

    @Test
    void absenceApproveCompleteAndCancelDraft() {
        Absence absence = Absence.draft(
                10L,
                1L,
                AbsenceType.VACATION,
                LocalDate.parse("2026-07-01"),
                LocalDate.parse("2026-07-15"),
                "Vacaciones anuales",
                Instant.parse("2026-06-01T10:00:00Z")
        );
        absence.approve(Instant.parse("2026-06-02T10:00:00Z"));
        assertEquals(AbsenceStatus.APPROVED, absence.getStatus());
        absence.complete(Instant.parse("2026-07-16T10:00:00Z"));
        assertEquals(AbsenceStatus.COMPLETED, absence.getStatus());

        Absence draft = Absence.draft(
                11L,
                1L,
                AbsenceType.SICK,
                LocalDate.parse("2026-08-01"),
                LocalDate.parse("2026-08-03"),
                null,
                Instant.parse("2026-08-01T08:00:00Z")
        );
        draft.cancel("hr", Instant.parse("2026-08-01T09:00:00Z"), "Error de registro");
        assertEquals(AbsenceStatus.CANCELLED, draft.getStatus());
    }

    @Test
    void employeeIsDistinctFromIdentityUserLink() {
        Employee employee = Employee.register(
                1L,
                IdentityDocument.parse(IdentityDocumentType.DNI, "45678912"),
                "Pérez",
                "Ana",
                LocalDate.parse("1990-05-10"),
                Sex.FEMALE,
                0,
                "ana.personal@correo.pe",
                "ana@empresa.pe",
                "ABC123456789",
                HomeAddress.of("Av. Lima 123", "Miraflores", "Lima"),
                List.of(EmployeePhone.of(PhoneUse.PERSONAL, "999888777")),
                LocalDate.parse("2026-11-25")
        );
        assertEquals(null, employee.getUserId());
        assertEquals(36, employee.ageAt(LocalDate.parse("2026-11-25")));
        assertEquals("45678912", employee.getDocumentNumber());
        employee.linkUser(99L);
        assertEquals(99L, employee.getUserId());
        employee.unlinkUser();
        assertEquals(null, employee.getUserId());
    }

    @Test
    void employeeRejectsFutureBirthAndInvalidDocument() {
        assertThrows(DomainException.class, () -> Employee.register(
                1L,
                IdentityDocument.parse(IdentityDocumentType.DNI, "45678912"),
                "Pérez",
                "Ana",
                LocalDate.parse("2027-01-01"),
                Sex.FEMALE,
                0,
                null,
                null,
                null,
                HomeAddress.of("Av. Lima 123", null, null),
                List.of(),
                LocalDate.parse("2026-11-25")
        ));
        assertThrows(DomainException.class, () -> IdentityDocument.parse(IdentityDocumentType.DNI, "123"));
        assertThrows(DomainException.class, () -> Cuspp.normalize("CORTO"));
    }

    @Test
    void banksFollowSunatCodesFromTheOpenCatalog() {
        assertEquals("02", PayrollBank.BCP.sunatCode());
        assertEquals("03", PayrollBank.INTERBANK.sunatCode());
        assertEquals("11", PayrollBank.BBVA.sunatCode());
        assertEquals("18", PayrollBank.NACION.sunatCode());
    }

    @Test
    void activeContractCeasesWithoutDeletingTheBook() {
        EmploymentContract contract = EmploymentContract.draft(
                10L,
                1L,
                ContractType.INDEFINITE,
                LocalDate.parse("2014-09-08"),
                null,
                Money.pen(new BigDecimal("1800.00")),
                Instant.parse("2014-09-08T15:00:00Z")
        );
        contract.activate(Instant.parse("2014-09-08T15:05:00Z"));
        contract.cease(LocalDate.parse("2026-08-03"), "Renuncia", Instant.parse("2026-08-03T15:00:00Z"));
        assertEquals(ContractStatus.COMPLETED, contract.getStatus());
        assertEquals(LocalDate.parse("2026-08-03"), contract.getProfile().getCessationOn());
        assertThrows(DomainException.class, () -> BankAccount.of(PayrollBank.BBVA, BankAccountType.SAVINGS, "123", "PEN"));
    }
}
