package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum PhoneUse {

    PERSONAL,
    WORK,
    OTHER;

    public static PhoneUse require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El uso del teléfono es obligatorio");
        }
        try {
            return PhoneUse.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Uso de teléfono no soportado: " + raw);
        }
    }
}
