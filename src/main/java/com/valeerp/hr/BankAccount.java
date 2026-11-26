package com.valeerp.hr;

import com.valeerp.kernel.Currency;
import com.valeerp.kernel.DomainException;

/**
 * Cuenta donde se abona la remuneración o la CTS. No es un maestro.
 */
public final class BankAccount {

    public static final int NUMBER_MIN = 8;
    public static final int NUMBER_MAX = 20;

    private final PayrollBank bank;
    private final BankAccountType type;
    private final String number;
    private final String currencyCode;

    private BankAccount(PayrollBank bank, BankAccountType type, String number, String currencyCode) {
        this.bank = bank;
        this.type = type;
        this.number = number;
        this.currencyCode = currencyCode;
    }

    public static BankAccount of(PayrollBank bank, BankAccountType type, String rawNumber, String currencyCode) {
        if (bank == null) {
            throw new DomainException("El banco es obligatorio");
        }
        if (type == null) {
            throw new DomainException("El tipo de cuenta es obligatorio");
        }
        String number = digits(rawNumber);
        Currency currency = Currency.of(currencyCode);
        return new BankAccount(bank, type, number, currency.code());
    }

    private static String digits(String rawNumber) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new DomainException("El número de cuenta es obligatorio");
        }
        String number = rawNumber.replace(" ", "").replace("-", "");
        if (!number.chars().allMatch(Character::isDigit)
                || number.length() < NUMBER_MIN
                || number.length() > NUMBER_MAX) {
            throw new DomainException(
                    "El número de cuenta debe tener entre " + NUMBER_MIN + " y " + NUMBER_MAX + " dígitos"
            );
        }
        return number;
    }

    public PayrollBank bank() {
        return bank;
    }

    public BankAccountType type() {
        return type;
    }

    public String number() {
        return number;
    }

    public String currencyCode() {
        return currencyCode;
    }
}
