package com.valeerp.hr.dto;

import com.valeerp.hr.ContractAffiliation;
import com.valeerp.hr.ContractAnnex;
import com.valeerp.hr.ContractProfile;
import com.valeerp.hr.ContractRenewal;
import com.valeerp.hr.ContractRole;
import com.valeerp.hr.ContractShift;
import com.valeerp.hr.ContractWage;
import com.valeerp.hr.Employee;
import com.valeerp.hr.EmploymentContract;
import com.valeerp.hr.PayrollBank;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class ContractReadModel {

    private ContractReadModel() {
    }

    public static Summary summary(EmploymentContract contract, Employee employee) {
        ContractProfile profile = contract.getProfile();
        return new Summary(
                contract.getId(),
                contract.getEmployeeId(),
                contract.getCompanyId(),
                employee.getDocumentType().name(),
                employee.getDocumentNumber(),
                employee.displayName(),
                profile == null || profile.getWorkerKind() == null ? null : profile.getWorkerKind().name(),
                profile == null ? null : profile.getPayrollGroup(),
                contract.getContractType().name(),
                contract.getStatus().name(),
                contract.getStartsOn(),
                contract.getEndsOn(),
                profile == null ? null : profile.getCessationOn()
        );
    }

    public static Terms terms(EmploymentContract contract) {
        ContractProfile profile = contract.getProfile();
        if (profile == null) {
            return Terms.empty();
        }
        return new Terms(
                profile.getWorkerKind() == null ? null : profile.getWorkerKind().name(),
                profile.getPayrollGroup(),
                profile.getPensionRegime() == null ? null : profile.getPensionRegime().name(),
                profile.getLaborRegime() == null ? null : profile.getLaborRegime().name(),
                profile.isAllowSalaryAdvance(),
                profile.getNotes(),
                account(profile.getWagePaymentMethod() == null ? null : profile.getWagePaymentMethod().name(),
                        profile.getWageBank(), profile.getWageAccountType() == null ? null : profile.getWageAccountType().name(),
                        profile.getWageAccountNumber(), profile.getWageAccountCurrency()),
                account(profile.getCtsPaymentMethod() == null ? null : profile.getCtsPaymentMethod().name(),
                        profile.getCtsBank(), profile.getCtsAccountType() == null ? null : profile.getCtsAccountType().name(),
                        profile.getCtsAccountNumber(), profile.getCtsAccountCurrency()),
                profile.getRetentionMode() == null ? null : profile.getRetentionMode().name(),
                profile.getRetentionValue(),
                profile.getRetentionBeneficiary(),
                profile.getCessationOn(),
                profile.getCessationReason()
        );
    }

    private static Account account(String method, PayrollBank bank, String type, String number, String currency) {
        return new Account(
                method,
                bank == null ? null : bank.name(),
                bank == null ? null : bank.sunatCode(),
                bank == null ? null : bank.label(),
                type,
                number,
                currency
        );
    }

    public static final class Summary {
        private final Long id;
        private final Long employeeId;
        private final Long legalEntityId;
        private final String documentType;
        private final String documentNumber;
        private final String displayName;
        private final String workerKind;
        private final String payrollGroup;
        private final String contractType;
        private final String status;
        private final LocalDate startsOn;
        private final LocalDate endsOn;
        private final LocalDate cessationOn;

        private Summary(Long id, Long employeeId, Long legalEntityId, String documentType, String documentNumber,
                        String displayName, String workerKind, String payrollGroup, String contractType, String status,
                        LocalDate startsOn, LocalDate endsOn, LocalDate cessationOn) {
            this.id = id;
            this.employeeId = employeeId;
            this.legalEntityId = legalEntityId;
            this.documentType = documentType;
            this.documentNumber = documentNumber;
            this.displayName = displayName;
            this.workerKind = workerKind;
            this.payrollGroup = payrollGroup;
            this.contractType = contractType;
            this.status = status;
            this.startsOn = startsOn;
            this.endsOn = endsOn;
            this.cessationOn = cessationOn;
        }

        public Long getId() { return id; }
        public Long getEmployeeId() { return employeeId; }
        public Long getLegalEntityId() { return legalEntityId; }
        public String getDocumentType() { return documentType; }
        public String getDocumentNumber() { return documentNumber; }
        public String getDisplayName() { return displayName; }
        public String getWorkerKind() { return workerKind; }
        public String getPayrollGroup() { return payrollGroup; }
        public String getContractType() { return contractType; }
        public String getStatus() { return status; }
        public LocalDate getStartsOn() { return startsOn; }
        public LocalDate getEndsOn() { return endsOn; }
        public LocalDate getCessationOn() { return cessationOn; }
    }

    public static final class Account {
        private final String method;
        private final String bank;
        private final String sunatCode;
        private final String bankLabel;
        private final String accountType;
        private final String number;
        private final String currency;

        private Account(String method, String bank, String sunatCode, String bankLabel, String accountType, String number, String currency) {
            this.method = method;
            this.bank = bank;
            this.sunatCode = sunatCode;
            this.bankLabel = bankLabel;
            this.accountType = accountType;
            this.number = number;
            this.currency = currency;
        }

        public String getMethod() { return method; }
        public String getBank() { return bank; }
        public String getSunatCode() { return sunatCode; }
        public String getBankLabel() { return bankLabel; }
        public String getAccountType() { return accountType; }
        public String getNumber() { return number; }
        public String getCurrency() { return currency; }
    }

    public static final class Terms {
        private final String workerKind;
        private final String payrollGroup;
        private final String pensionRegime;
        private final String laborRegime;
        private final boolean allowSalaryAdvance;
        private final String notes;
        private final Account wages;
        private final Account cts;
        private final String retentionMode;
        private final BigDecimal retentionValue;
        private final String retentionBeneficiary;
        private final LocalDate cessationOn;
        private final String cessationReason;

        private Terms(String workerKind, String payrollGroup, String pensionRegime, String laborRegime,
                      boolean allowSalaryAdvance, String notes, Account wages, Account cts, String retentionMode,
                      BigDecimal retentionValue, String retentionBeneficiary, LocalDate cessationOn, String cessationReason) {
            this.workerKind = workerKind;
            this.payrollGroup = payrollGroup;
            this.pensionRegime = pensionRegime;
            this.laborRegime = laborRegime;
            this.allowSalaryAdvance = allowSalaryAdvance;
            this.notes = notes;
            this.wages = wages;
            this.cts = cts;
            this.retentionMode = retentionMode;
            this.retentionValue = retentionValue;
            this.retentionBeneficiary = retentionBeneficiary;
            this.cessationOn = cessationOn;
            this.cessationReason = cessationReason;
        }

        static Terms empty() {
            return new Terms(null, null, null, null, false, null, null, null, null, null, null, null, null);
        }

        public String getWorkerKind() { return workerKind; }
        public String getPayrollGroup() { return payrollGroup; }
        public String getPensionRegime() { return pensionRegime; }
        public String getLaborRegime() { return laborRegime; }
        public boolean isAllowSalaryAdvance() { return allowSalaryAdvance; }
        public String getNotes() { return notes; }
        public Account getWages() { return wages; }
        public Account getCts() { return cts; }
        public String getRetentionMode() { return retentionMode; }
        public BigDecimal getRetentionValue() { return retentionValue; }
        public String getRetentionBeneficiary() { return retentionBeneficiary; }
        public LocalDate getCessationOn() { return cessationOn; }
        public String getCessationReason() { return cessationReason; }
    }

    public static final class WageRow {
        private final Long id;
        private final MoneyDto amount;
        private final LocalDate validFrom;
        private final String status;
        private final String approvedBy;
        private final Instant approvedAt;

        private WageRow(ContractWage wage) {
            this.id = wage.getId();
            this.amount = MoneyDto.from(wage.getAmount());
            this.validFrom = wage.getValidFrom();
            this.status = wage.getStatus().name();
            this.approvedBy = wage.getApprovedBy();
            this.approvedAt = wage.getApprovedAt();
        }

        public static WageRow from(ContractWage wage) { return new WageRow(wage); }
        public Long getId() { return id; }
        public MoneyDto getAmount() { return amount; }
        public LocalDate getValidFrom() { return validFrom; }
        public String getStatus() { return status; }
        public String getApprovedBy() { return approvedBy; }
        public Instant getApprovedAt() { return approvedAt; }
    }

    public static final class RoleRow {
        private final Long id;
        private final String title;
        private final LocalDate startsOn;
        private final LocalDate endsOn;

        private RoleRow(ContractRole role) {
            this.id = role.getId();
            this.title = role.getTitle();
            this.startsOn = role.getStartsOn();
            this.endsOn = role.getEndsOn();
        }

        public static RoleRow from(ContractRole role) { return new RoleRow(role); }
        public Long getId() { return id; }
        public String getTitle() { return title; }
        public LocalDate getStartsOn() { return startsOn; }
        public LocalDate getEndsOn() { return endsOn; }
    }

    public static final class ShiftRow {
        private final Long id;
        private final String name;
        private final LocalDate startsOn;

        private ShiftRow(ContractShift shift) {
            this.id = shift.getId();
            this.name = shift.getName();
            this.startsOn = shift.getStartsOn();
        }

        public static ShiftRow from(ContractShift shift) { return new ShiftRow(shift); }
        public Long getId() { return id; }
        public String getName() { return name; }
        public LocalDate getStartsOn() { return startsOn; }
    }

    public static final class AffiliationRow {
        private final Long id;
        private final String name;
        private final String amountKind;
        private final MoneyDto amount;
        private final boolean active;

        private AffiliationRow(ContractAffiliation affiliation) {
            this.id = affiliation.getId();
            this.name = affiliation.getName();
            this.amountKind = affiliation.getAmountKind().name();
            this.amount = affiliation.getAmount() == null ? null : MoneyDto.from(affiliation.getAmount());
            this.active = affiliation.isActive();
        }

        public static AffiliationRow from(ContractAffiliation affiliation) { return new AffiliationRow(affiliation); }
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getAmountKind() { return amountKind; }
        public MoneyDto getAmount() { return amount; }
        public boolean isActive() { return active; }
    }

    public static final class RenewalRow {
        private final Long id;
        private final LocalDate startsOn;
        private final LocalDate endsOn;
        private final String notes;

        private RenewalRow(ContractRenewal renewal) {
            this.id = renewal.getId();
            this.startsOn = renewal.getStartsOn();
            this.endsOn = renewal.getEndsOn();
            this.notes = renewal.getNotes();
        }

        public static RenewalRow from(ContractRenewal renewal) { return new RenewalRow(renewal); }
        public Long getId() { return id; }
        public LocalDate getStartsOn() { return startsOn; }
        public LocalDate getEndsOn() { return endsOn; }
        public String getNotes() { return notes; }
    }

    public static final class AnnexRow {
        private final Long id;
        private final String fileName;
        private final String comment;
        private final String recordedBy;
        private final Instant recordedAt;

        private AnnexRow(ContractAnnex annex) {
            this.id = annex.getId();
            this.fileName = annex.getFileName();
            this.comment = annex.getComment();
            this.recordedBy = annex.getRecordedBy();
            this.recordedAt = annex.getRecordedAt();
        }

        public static AnnexRow from(ContractAnnex annex) { return new AnnexRow(annex); }
        public Long getId() { return id; }
        public String getFileName() { return fileName; }
        public String getComment() { return comment; }
        public String getRecordedBy() { return recordedBy; }
        public Instant getRecordedAt() { return recordedAt; }
    }

    public static final class Detail {
        private final Summary summary;
        private final Terms terms;
        private final MoneyDto wage;
        private final List<WageRow> wages;
        private final List<RoleRow> roles;
        private final List<ShiftRow> shifts;
        private final List<AffiliationRow> affiliations;
        private final List<RenewalRow> renewals;
        private final List<AnnexRow> annexes;

        public Detail(Summary summary, Terms terms, MoneyDto wage, List<WageRow> wages, List<RoleRow> roles,
                      List<ShiftRow> shifts, List<AffiliationRow> affiliations, List<RenewalRow> renewals, List<AnnexRow> annexes) {
            this.summary = summary;
            this.terms = terms;
            this.wage = wage;
            this.wages = wages;
            this.roles = roles;
            this.shifts = shifts;
            this.affiliations = affiliations;
            this.renewals = renewals;
            this.annexes = annexes;
        }

        public Summary getSummary() { return summary; }
        public Terms getTerms() { return terms; }
        public MoneyDto getWage() { return wage; }
        public List<WageRow> getWages() { return wages; }
        public List<RoleRow> getRoles() { return roles; }
        public List<ShiftRow> getShifts() { return shifts; }
        public List<AffiliationRow> getAffiliations() { return affiliations; }
        public List<RenewalRow> getRenewals() { return renewals; }
        public List<AnnexRow> getAnnexes() { return annexes; }
    }

    public static final class Statement {
        private final String kind;
        private final String text;

        public Statement(String kind, String text) {
            this.kind = kind;
            this.text = text;
        }

        public String getKind() { return kind; }
        public String getText() { return text; }
    }
}
