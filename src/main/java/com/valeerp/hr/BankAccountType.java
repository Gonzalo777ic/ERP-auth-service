package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum BankAccountType {

    SAVINGS("Ahorros"),
    CHECKING("Corriente");

    private final String label;

    BankAccountType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static BankAccountType require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El tipo de cuenta es obligatorio");
        }
        try {
            return BankAccountType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Tipo de cuenta no soportado: " + raw);
        }
    }
}
