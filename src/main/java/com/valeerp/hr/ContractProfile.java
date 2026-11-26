package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ficha del contrato: tipo de trabajador, regímenes, abono y cese.
 * La locación no forma parte del modelo.
 */
@Embeddable
public class ContractProfile {

    public static final int GROUP_MAX = 80;
    public static final int NOTE_MAX = 500;
    public static final int BENEFICIARY_MAX = 160;

    @Enumerated(EnumType.STRING)
    @Column(name = "worker_kind", length = 16)
    private WorkerKind workerKind;

    @Column(name = "payroll_group", length = 80)
    private String payrollGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "pension_regime", length = 16)
    private PensionRegime pensionRegime;

    @Enumerated(EnumType.STRING)
    @Column(name = "labor_regime", length = 32)
    private LaborRegime laborRegime;

    @Column(name = "allow_salary_advance", nullable = false)
    private boolean allowSalaryAdvance;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_payment_method", length = 16)
    private WagePaymentMethod wagePaymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_bank", length = 32)
    private PayrollBank wageBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_account_type", length = 16)
    private BankAccountType wageAccountType;

    @Column(name = "wage_account_number", length = 20)
    private String wageAccountNumber;

    @Column(name = "wage_account_currency", length = 3)
    private String wageAccountCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "cts_payment_method", length = 16)
    private WagePaymentMethod ctsPaymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "cts_bank", length = 32)
    private PayrollBank ctsBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "cts_account_type", length = 16)
    private BankAccountType ctsAccountType;

    @Column(name = "cts_account_number", length = 20)
    private String ctsAccountNumber;

    @Column(name = "cts_account_currency", length = 3)
    private String ctsAccountCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "retention_mode", length = 16)
    private RetentionMode retentionMode;

    @Column(name = "retention_value", precision = 19, scale = 2)
    private BigDecimal retentionValue;

    @Column(name = "retention_beneficiary", length = 160)
    private String retentionBeneficiary;

    @Column(name = "cessation_on")
    private LocalDate cessationOn;

    @Column(name = "cessation_reason", length = 255)
    private String cessationReason;

    @Column(length = 500)
    private String notes;

    protected ContractProfile() {
    }

    public static ContractProfile empty() {
        ContractProfile profile = new ContractProfile();
        profile.retentionMode = RetentionMode.NONE;
        return profile;
    }

    public void describe(
            WorkerKind workerKind,
            String payrollGroup,
            PensionRegime pensionRegime,
            LaborRegime laborRegime,
            boolean allowSalaryAdvance,
            String notes
    ) {
        if (workerKind == null || pensionRegime == null || laborRegime == null) {
            throw new DomainException("El tipo de trabajador y los regímenes son obligatorios");
        }
        this.workerKind = workerKind;
        this.payrollGroup = requiredGroup(payrollGroup);
        this.pensionRegime = pensionRegime;
        this.laborRegime = laborRegime;
        this.allowSalaryAdvance = allowSalaryAdvance;
        this.notes = optional(notes, NOTE_MAX, "Las observaciones");
        if (this.retentionMode == null) {
            this.retentionMode = RetentionMode.NONE;
        }
    }

    public void payWages(WagePaymentMethod method, BankAccount account) {
        this.wagePaymentMethod = method;
        this.wageBank = account == null ? null : account.bank();
        this.wageAccountType = account == null ? null : account.type();
        this.wageAccountNumber = account == null ? null : account.number();
        this.wageAccountCurrency = account == null ? null : account.currencyCode();
        requireAccount(method, account, "de haberes");
    }

    public void payCts(WagePaymentMethod method, BankAccount account) {
        this.ctsPaymentMethod = method;
        this.ctsBank = account == null ? null : account.bank();
        this.ctsAccountType = account == null ? null : account.type();
        this.ctsAccountNumber = account == null ? null : account.number();
        this.ctsAccountCurrency = account == null ? null : account.currencyCode();
        requireAccount(method, account, "de CTS");
    }

    public void retain(RetentionMode mode, BigDecimal value, String beneficiary) {
        RetentionMode resolved = mode == null ? RetentionMode.NONE : mode;
        if (resolved == RetentionMode.NONE) {
            this.retentionMode = RetentionMode.NONE;
            this.retentionValue = null;
            this.retentionBeneficiary = null;
            return;
        }
        if (value == null || value.signum() <= 0) {
            throw new DomainException("La retención necesita un valor mayor que cero");
        }
        if (resolved == RetentionMode.PERCENT && value.compareTo(new BigDecimal("100")) > 0) {
            throw new DomainException("El porcentaje de retención no puede pasar de 100");
        }
        this.retentionMode = resolved;
        this.retentionValue = value;
        this.retentionBeneficiary = optional(beneficiary, BENEFICIARY_MAX, "El beneficiario");
    }

    public void cease(LocalDate on, String reason) {
        if (on == null) {
            throw new DomainException("La fecha de cese es obligatoria");
        }
        if (reason == null || reason.isBlank()) {
            throw new DomainException("El motivo de cese es obligatorio");
        }
        this.cessationOn = on;
        this.cessationReason = reason.trim();
    }

    private static void requireAccount(WagePaymentMethod method, BankAccount account, String label) {
        if (method == null) {
            throw new DomainException("El tipo de pago " + label + " es obligatorio");
        }
        if (method == WagePaymentMethod.BANK_ACCOUNT && account == null) {
            throw new DomainException("La cuenta " + label + " es obligatoria");
        }
        if (method == WagePaymentMethod.CASH && account != null) {
            throw new DomainException("El pago en efectivo " + label + " no lleva cuenta");
        }
    }

    private static String requiredGroup(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainException("El grupo de planilla es obligatorio");
        }
        String value = raw.trim();
        if (value.length() > GROUP_MAX) {
            throw new DomainException("El grupo de planilla es demasiado largo");
        }
        return value;
    }

    private static String optional(String raw, int max, String label) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim();
        if (value.length() > max) {
            throw new DomainException(label + " supera el máximo permitido");
        }
        return value;
    }

    public WorkerKind getWorkerKind() {
        return workerKind;
    }

    public String getPayrollGroup() {
        return payrollGroup;
    }

    public PensionRegime getPensionRegime() {
        return pensionRegime;
    }

    public LaborRegime getLaborRegime() {
        return laborRegime;
    }

    public boolean isAllowSalaryAdvance() {
        return allowSalaryAdvance;
    }

    public WagePaymentMethod getWagePaymentMethod() {
        return wagePaymentMethod;
    }

    public PayrollBank getWageBank() {
        return wageBank;
    }

    public BankAccountType getWageAccountType() {
        return wageAccountType;
    }

    public String getWageAccountNumber() {
        return wageAccountNumber;
    }

    public String getWageAccountCurrency() {
        return wageAccountCurrency;
    }

    public WagePaymentMethod getCtsPaymentMethod() {
        return ctsPaymentMethod;
    }

    public PayrollBank getCtsBank() {
        return ctsBank;
    }

    public BankAccountType getCtsAccountType() {
        return ctsAccountType;
    }

    public String getCtsAccountNumber() {
        return ctsAccountNumber;
    }

    public String getCtsAccountCurrency() {
        return ctsAccountCurrency;
    }

    public RetentionMode getRetentionMode() {
        return retentionMode;
    }

    public BigDecimal getRetentionValue() {
        return retentionValue;
    }

    public String getRetentionBeneficiary() {
        return retentionBeneficiary;
    }

    public LocalDate getCessationOn() {
        return cessationOn;
    }

    public String getCessationReason() {
        return cessationReason;
    }

    public String getNotes() {
        return notes;
    }
}
