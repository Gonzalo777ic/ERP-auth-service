package com.valeerp.hr;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Texto de constancia. No es un PDF ni un correo: el caso de uso devuelve el párrafo.
 */
public final class WorkStatement {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-PE"));

    private WorkStatement() {
    }

    public static String work(Employee employee, EmploymentContract contract, String jobTitle) {
        return paragraph(employee, contract, jobTitle, "constancia de trabajo");
    }

    public static String cts(Employee employee, EmploymentContract contract, String jobTitle) {
        return paragraph(employee, contract, jobTitle, "retiro de CTS");
    }

    private static String paragraph(Employee employee, EmploymentContract contract, String jobTitle, String purpose) {
        String treatment = employee.getSex() == Sex.FEMALE ? "La trabajadora" : "El trabajador";
        String role = jobTitle == null || jobTitle.isBlank() ? "sin cargo registrado" : jobTitle;
        String end = endOf(contract);
        return treatment + " " + employee.displayName()
                + ", identificado con " + employee.getDocumentType().name()
                + " Nro. " + employee.getDocumentNumber()
                + ", " + end
                + " en el cargo de " + role
                + ". Se extiende la presente para fines de " + purpose + ".";
    }

    private static String endOf(EmploymentContract contract) {
        LocalDate start = contract.getStartsOn();
        LocalDate cessation = contract.getProfile() == null ? null : contract.getProfile().getCessationOn();
        if (cessation == null) {
            return "labora desde el " + DATE.format(start);
        }
        return "laboró desde el " + DATE.format(start) + " hasta el " + DATE.format(cessation);
    }
}
