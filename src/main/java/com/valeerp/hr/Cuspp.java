package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

/**
 * Código Único del Sistema Privado de Pensiones. Vacío si el trabajador no tiene uno.
 */
public final class Cuspp {

    public static final int LENGTH = 12;

    private Cuspp() {
    }

    public static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim().toUpperCase().replace(" ", "");
        if (value.length() != LENGTH || !value.chars().allMatch(Character::isLetterOrDigit)) {
            throw new DomainException("El CUSPP debe tener " + LENGTH + " caracteres alfanuméricos");
        }
        return value;
    }
}
