package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum AffiliationAmountKind {

    FIXED("Monto fijo"),
    VARIABLE("Monto variable");

    private final String label;

    AffiliationAmountKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static AffiliationAmountKind require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El tipo de afiliación es obligatorio");
        }
        try {
            return AffiliationAmountKind.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Tipo de afiliación no soportado: " + raw);
        }
    }
}
