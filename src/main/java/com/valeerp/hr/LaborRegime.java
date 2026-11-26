package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum LaborRegime {

    PRIVATE_GENERAL("Privado general"),
    MICRO_SMALL("Micro y pequeña empresa"),
    AGRARIAN("Agrario"),
    OTHER("Otro");

    private final String label;

    LaborRegime(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static LaborRegime require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El régimen laboral es obligatorio");
        }
        try {
            return LaborRegime.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Régimen laboral no soportado: " + raw);
        }
    }
}
