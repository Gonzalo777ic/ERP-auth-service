package com.valeerp.hr;

import com.valeerp.kernel.DomainException;

/**
 * Bancos con código SUNAT (PLE tabla 3, columna {@code l10n_pe_edi_code}).
 * Fuente: odoo/odoo {@code addons/l10n_pe/data/res.bank.csv} (rama 19.0).
 * Se omiten las filas de ese archivo que no traen código.
 */
public enum PayrollBank {

    BCP("02", "Banco De Credito Del Peru"),
    INTERBANK("03", "Banco Internacional Del Peru"),
    CITIBANK("07", "Citibank Del Peru Sa"),
    SCOTIABANK("09", "Scotiabank Peru"),
    BBVA("11", "Banco Continental"),
    NACION("18", "Banco de la nación"),
    COMERCIO("23", "Banco De Comercio"),
    BANBIF("38", "Banco Interamericano De Finanzas"),
    SANTANDER("56", "Banco Santander Peru S.a.");

    private final String sunatCode;
    private final String label;

    PayrollBank(String sunatCode, String label) {
        this.sunatCode = sunatCode;
        this.label = label;
    }

    public String sunatCode() {
        return sunatCode;
    }

    public String label() {
        return label;
    }

    public static PayrollBank require(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El banco es obligatorio");
        }
        try {
            return PayrollBank.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new DomainException("Banco no soportado: " + raw);
        }
    }
}
