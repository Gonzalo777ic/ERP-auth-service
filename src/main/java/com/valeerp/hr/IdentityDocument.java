package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

import java.util.Objects;

/**
 * Clave de negocio del trabajador: tipo de documento + número.
 * El número se normaliza; la unicidad vigente la garantiza el índice parcial.
 */
public final class IdentityDocument {

    private final IdentityDocumentType type;
    private final String number;

    private IdentityDocument(IdentityDocumentType type, String number) {
        this.type = type;
        this.number = number;
    }

    public static IdentityDocument parse(IdentityDocumentType type, String rawNumber) {
        if (type == null) {
            throw new DomainException("El documento es obligatorio");
        }
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new DomainException("El número de documento es obligatorio");
        }
        String number = rawNumber.trim().toUpperCase().replace(" ", "");
        type.checkNumber(number);
        return new IdentityDocument(type, number);
    }

    public IdentityDocumentType type() {
        return type;
    }

    public String number() {
        return number;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IdentityDocument document)) {
            return false;
        }
        return type == document.type && number.equals(document.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, number);
    }
}
