package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public final class ContactEmail {

    public static final int MAX_LENGTH = 180;

    private ContactEmail() {
    }

    public static String normalize(String raw, String label) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim().toLowerCase();
        if (value.length() > MAX_LENGTH || value.contains(" ") || !value.matches("[^@]+@[^@]+\\.[^@]+")) {
            throw new DomainException("El " + label + " no es válido");
        }
        return value;
    }
}
