package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum WageChangeStatus {

    PROPOSED("Propuesto"),
    APPROVED("Aprobado"),
    SUPERSEDED("Modificado");

    private final String label;

    WageChangeStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static WageChangeStatus require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El estado del sueldo es obligatorio");
        }
        try {
            return WageChangeStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Estado de sueldo no soportado: " + raw);
        }
    }
}
