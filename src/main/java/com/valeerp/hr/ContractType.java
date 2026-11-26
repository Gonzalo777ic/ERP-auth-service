package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

/**
 * Política de tipos de contrato laboral. Catálogo local RRHH.
 */
public enum ContractType {

    INDEFINITE("Plazo indeterminado"),
    FIXED_TERM("Plazo fijo"),
    INTERNSHIP("Prácticas"),
    SERVICES("Prestación de servicios");

    private final String label;

    ContractType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static ContractType require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El tipo de contrato es obligatorio");
        }
        try {
            return ContractType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Tipo de contrato no soportado: " + raw);
        }
    }
}
