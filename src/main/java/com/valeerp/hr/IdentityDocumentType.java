package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

/**
 * Documento de identidad del trabajador. La política de formato vive aquí, no en el controlador.
 */
public enum IdentityDocumentType {

    DNI("\\d{8}", "El DNI debe tener 8 dígitos"),
    CE("\\d{9}", "El carné de extranjería debe tener 9 dígitos"),
    PASSPORT("[A-Z0-9]{6,12}", "El pasaporte debe tener entre 6 y 12 caracteres alfanuméricos"),
    PTP("[A-Z0-9]{6,15}", "El PTP debe tener entre 6 y 15 caracteres alfanuméricos");

    private final String pattern;
    private final String message;

    IdentityDocumentType(String pattern, String message) {
        this.pattern = pattern;
        this.message = message;
    }

    public void checkNumber(String number) {
        if (number == null || !number.matches(pattern)) {
            throw new DomainException(message);
        }
    }

    public static IdentityDocumentType require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El documento es obligatorio");
        }
        try {
            return IdentityDocumentType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Documento no soportado: " + raw);
        }
    }
}
