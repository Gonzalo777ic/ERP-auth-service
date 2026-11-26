package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum PensionRegime {

    ONP("ONP"),
    AFP_FLOW("AFP flujo"),
    AFP_MIXED("AFP mixta"),
    NONE("Sin régimen");

    private final String label;

    PensionRegime(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static PensionRegime require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El régimen pensionario es obligatorio");
        }
        try {
            return PensionRegime.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Régimen pensionario no soportado: " + raw);
        }
    }
}
