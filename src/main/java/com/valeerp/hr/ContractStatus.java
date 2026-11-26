package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

/**
 * Estados del libro de contratos. Sin delete físico: cierre/anulación por estado.
 */
public enum ContractStatus {

    DRAFT("Borrador"),
    ACTIVE("Activo"),
    COMPLETED("Cerrado"),
    CANCELLED("Anulado");

    private final String label;

    ContractStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static ContractStatus require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El estado de contrato es obligatorio");
        }
        try {
            return ContractStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Estado de contrato desconocido: " + raw);
        }
    }
}
