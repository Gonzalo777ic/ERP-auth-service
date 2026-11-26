package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum WagePaymentMethod {

    BANK_ACCOUNT("Cuenta bancaria"),
    CASH("Efectivo");

    private final String label;

    WagePaymentMethod(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static WagePaymentMethod require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El tipo de pago es obligatorio");
        }
        try {
            return WagePaymentMethod.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Tipo de pago no soportado: " + raw);
        }
    }
}
