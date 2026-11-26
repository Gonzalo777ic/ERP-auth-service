package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import com.valeerp.kernel.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Libro de remuneraciones del contrato. No se borra: un sueldo nuevo deja el anterior en SUPERSEDED.
 */
@Entity
@Table(name = "hr_contract_wages")
public class ContractWage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false, precision = 19, scale = 2)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency_code", nullable = false, length = 3))
    })
    private Money amount;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private WageChangeStatus status;

    @Column(name = "proposed_at", nullable = false)
    private Instant proposedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "approved_by", length = 120)
    private String approvedBy;

    protected ContractWage() {
    }

    public static ContractWage propose(Long contractId, Money amount, LocalDate validFrom, Instant when) {
        return open(contractId, amount, validFrom, when, false, null);
    }

    public static ContractWage approved(Long contractId, Money amount, LocalDate validFrom, Instant when, String actor) {
        return open(contractId, amount, validFrom, when, true, actor);
    }

    private static ContractWage open(
            Long contractId,
            Money amount,
            LocalDate validFrom,
            Instant when,
            boolean approvedNow,
            String actor
    ) {
        if (contractId == null || amount == null || validFrom == null || when == null) {
            throw new DomainException("El sueldo necesita contrato, monto y vigencia");
        }
        ContractWage wage = new ContractWage();
        wage.contractId = contractId;
        wage.amount = amount;
        wage.validFrom = validFrom;
        wage.proposedAt = when;
        wage.status = WageChangeStatus.PROPOSED;
        if (approvedNow) {
            wage.approve(actor, when);
        }
        return wage;
    }

    public void approve(String actor, Instant when) {
        if (status != WageChangeStatus.PROPOSED) {
            throw new DomainException("Solo un sueldo propuesto se aprueba");
        }
        if (when == null) {
            throw new DomainException("El instante de aprobación es obligatorio");
        }
        this.status = WageChangeStatus.APPROVED;
        this.approvedAt = when;
        this.approvedBy = actor == null || actor.isBlank() ? "sistema" : actor.trim();
    }

    public void supersede() {
        if (status != WageChangeStatus.APPROVED) {
            throw new DomainException("Solo el sueldo aprobado vigente pasa a modificado");
        }
        this.status = WageChangeStatus.SUPERSEDED;
    }

    public Long getId() {
        return id;
    }

    public Long getContractId() {
        return contractId;
    }

    public Money getAmount() {
        return amount;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public WageChangeStatus getStatus() {
        return status;
    }

    public Instant getProposedAt() {
        return proposedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public String getApprovedBy() {
        return approvedBy;
    }
}
