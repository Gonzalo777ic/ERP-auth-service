package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum Sex {

    FEMALE,
    MALE;

    public static Sex require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El sexo es obligatorio");
        }
        try {
            return Sex.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Sexo no soportado: " + raw);
        }
    }
}
