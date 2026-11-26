package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum RetentionMode {

    NONE("Sin retención"),
    AMOUNT("Monto"),
    PERCENT("Porcentaje");

    private final String label;

    RetentionMode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static RetentionMode require(String raw) {
        if (raw == null || raw.isBlank()) {
            return NONE;
        }
        try {
            return RetentionMode.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Retención no soportada: " + raw);
        }
    }
}
