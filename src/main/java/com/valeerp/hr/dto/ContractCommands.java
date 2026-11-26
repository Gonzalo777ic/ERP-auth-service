package com.valeerp.hr.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Cuerpos HTTP del libro de contratos. La regla sigue en el agregado. */
public final class ContractCommands {

    private ContractCommands() {
    }

    public static class Terms {
        @NotBlank private String workerKind;
        @NotBlank private String payrollGroup;
        @NotBlank private String pensionRegime;
        @NotBlank private String laborRegime;
        private boolean allowSalaryAdvance;
        private String notes;
        private String wagePaymentMethod;
        private String wageBank;
        private String wageAccountType;
        private String wageAccountNumber;
        private String wageAccountCurrency;
        private String ctsPaymentMethod;
        private String ctsBank;
        private String ctsAccountType;
        private String ctsAccountNumber;
        private String ctsAccountCurrency;
        private String retentionMode;
        private BigDecimal retentionValue;
        private String retentionBeneficiary;

        public String getWorkerKind() { return workerKind; }
        public void setWorkerKind(String workerKind) { this.workerKind = workerKind; }
        public String getPayrollGroup() { return payrollGroup; }
        public void setPayrollGroup(String payrollGroup) { this.payrollGroup = payrollGroup; }
        public String getPensionRegime() { return pensionRegime; }
        public void setPensionRegime(String pensionRegime) { this.pensionRegime = pensionRegime; }
        public String getLaborRegime() { return laborRegime; }
        public void setLaborRegime(String laborRegime) { this.laborRegime = laborRegime; }
        public boolean isAllowSalaryAdvance() { return allowSalaryAdvance; }
        public void setAllowSalaryAdvance(boolean allowSalaryAdvance) { this.allowSalaryAdvance = allowSalaryAdvance; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        public String getWagePaymentMethod() { return wagePaymentMethod; }
        public void setWagePaymentMethod(String wagePaymentMethod) { this.wagePaymentMethod = wagePaymentMethod; }
        public String getWageBank() { return wageBank; }
        public void setWageBank(String wageBank) { this.wageBank = wageBank; }
        public String getWageAccountType() { return wageAccountType; }
        public void setWageAccountType(String wageAccountType) { this.wageAccountType = wageAccountType; }
        public String getWageAccountNumber() { return wageAccountNumber; }
        public void setWageAccountNumber(String wageAccountNumber) { this.wageAccountNumber = wageAccountNumber; }
        public String getWageAccountCurrency() { return wageAccountCurrency; }
        public void setWageAccountCurrency(String wageAccountCurrency) { this.wageAccountCurrency = wageAccountCurrency; }
        public String getCtsPaymentMethod() { return ctsPaymentMethod; }
        public void setCtsPaymentMethod(String ctsPaymentMethod) { this.ctsPaymentMethod = ctsPaymentMethod; }
        public String getCtsBank() { return ctsBank; }
        public void setCtsBank(String ctsBank) { this.ctsBank = ctsBank; }
        public String getCtsAccountType() { return ctsAccountType; }
        public void setCtsAccountType(String ctsAccountType) { this.ctsAccountType = ctsAccountType; }
        public String getCtsAccountNumber() { return ctsAccountNumber; }
        public void setCtsAccountNumber(String ctsAccountNumber) { this.ctsAccountNumber = ctsAccountNumber; }
        public String getCtsAccountCurrency() { return ctsAccountCurrency; }
        public void setCtsAccountCurrency(String ctsAccountCurrency) { this.ctsAccountCurrency = ctsAccountCurrency; }
        public String getRetentionMode() { return retentionMode; }
        public void setRetentionMode(String retentionMode) { this.retentionMode = retentionMode; }
        public BigDecimal getRetentionValue() { return retentionValue; }
        public void setRetentionValue(BigDecimal retentionValue) { this.retentionValue = retentionValue; }
        public String getRetentionBeneficiary() { return retentionBeneficiary; }
        public void setRetentionBeneficiary(String retentionBeneficiary) { this.retentionBeneficiary = retentionBeneficiary; }
    }

    public static class Wage {
        @NotNull @Valid private MoneyDto amount;
        @NotNull private LocalDate validFrom;
        public MoneyDto getAmount() { return amount; }
        public void setAmount(MoneyDto amount) { this.amount = amount; }
        public LocalDate getValidFrom() { return validFrom; }
        public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    }

    public static class Role {
        @NotBlank private String title;
        @NotNull private LocalDate startsOn;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public LocalDate getStartsOn() { return startsOn; }
        public void setStartsOn(LocalDate startsOn) { this.startsOn = startsOn; }
    }

    public static class Shift {
        @NotBlank private String name;
        @NotNull private LocalDate startsOn;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public LocalDate getStartsOn() { return startsOn; }
        public void setStartsOn(LocalDate startsOn) { this.startsOn = startsOn; }
    }

    public static class Affiliation {
        @NotBlank private String name;
        @NotBlank private String amountKind;
        @Valid private MoneyDto amount;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAmountKind() { return amountKind; }
        public void setAmountKind(String amountKind) { this.amountKind = amountKind; }
        public MoneyDto getAmount() { return amount; }
        public void setAmount(MoneyDto amount) { this.amount = amount; }
    }

    public static class Renewal {
        @NotNull private LocalDate startsOn;
        @NotNull private LocalDate endsOn;
        private String notes;
        public LocalDate getStartsOn() { return startsOn; }
        public void setStartsOn(LocalDate startsOn) { this.startsOn = startsOn; }
        public LocalDate getEndsOn() { return endsOn; }
        public void setEndsOn(LocalDate endsOn) { this.endsOn = endsOn; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class Annex {
        @NotBlank private String fileName;
        private String comment;
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class Cease {
        @NotNull private LocalDate on;
        @NotBlank private String reason;
        public LocalDate getOn() { return on; }
        public void setOn(LocalDate on) { this.on = on; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
