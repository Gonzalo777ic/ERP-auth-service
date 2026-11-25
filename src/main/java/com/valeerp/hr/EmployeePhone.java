package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;

/**
 * Teléfono del trabajador. Valor del agregado, no un maestro propio.
 */
@Embeddable
public class EmployeePhone {

    public static final int MIN_DIGITS = 6;
    public static final int MAX_DIGITS = 15;
    public static final int MAX_PER_EMPLOYEE = 10;

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_use", nullable = false, length = 16)
    private PhoneUse use;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String number;

    protected EmployeePhone() {
    }

    private EmployeePhone(PhoneUse use, String number) {
        this.use = use;
        this.number = number;
    }

    public static EmployeePhone of(PhoneUse use, String rawNumber) {
        if (use == null) {
            throw new DomainException("El uso del teléfono es obligatorio");
        }
        return new EmployeePhone(use, normalize(rawNumber));
    }

    public static String normalize(String rawNumber) {
        if (rawNumber == null || rawNumber.isBlank()) {
            throw new DomainException("El teléfono es obligatorio");
        }
        String compact = rawNumber.trim().replace(" ", "").replace("-", "");
        String digits = compact.startsWith("+") ? compact.substring(1) : compact;
        if (!digits.chars().allMatch(Character::isDigit) || digits.length() < MIN_DIGITS || digits.length() > MAX_DIGITS) {
            throw new DomainException("El teléfono debe tener entre " + MIN_DIGITS + " y " + MAX_DIGITS + " dígitos");
        }
        return compact;
    }

    public PhoneUse getUse() {
        return use;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EmployeePhone phone)) {
            return false;
        }
        return use == phone.use && Objects.equals(number, phone.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(use, number);
    }
}
