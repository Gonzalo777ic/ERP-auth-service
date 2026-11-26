package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

public enum WorkerKind {

    STAFF("Empleado"),
    LABORER("Obrero");

    private final String label;

    WorkerKind(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static WorkerKind require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El tipo de trabajador es obligatorio");
        }
        try {
            return WorkerKind.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Tipo de trabajador no soportado: " + raw);
        }
    }
}
