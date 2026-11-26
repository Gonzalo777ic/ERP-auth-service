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

/** Concepto de planilla afiliado al contrato. Se desactiva; no se borra. */
@Entity
@Table(name = "hr_contract_affiliations")
public class ContractAffiliation {

    public static final int NAME_MAX = 120;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(nullable = false, length = 120)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "amount_kind", nullable = false, length = 16)
    private AffiliationAmountKind amountKind;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 19, scale = 2)),
            @AttributeOverride(name = "currencyCode", column = @Column(name = "currency_code", length = 3))
    })
    private Money amount;

    @Column(nullable = false)
    private boolean active;

    protected ContractAffiliation() {
    }

    public static ContractAffiliation open(Long contractId, String name, AffiliationAmountKind kind, Money amount) {
        if (contractId == null) {
            throw new DomainException("La afiliación necesita un contrato");
        }
        if (name == null || name.isBlank() || name.trim().length() > NAME_MAX) {
            throw new DomainException("El nombre de la afiliación es obligatorio");
        }
        if (kind == null) {
            throw new DomainException("El tipo de afiliación es obligatorio");
        }
        if (kind == AffiliationAmountKind.FIXED && amount == null) {
            throw new DomainException("La afiliación de monto fijo necesita importe");
        }
        ContractAffiliation affiliation = new ContractAffiliation();
        affiliation.contractId = contractId;
        affiliation.name = name.trim();
        affiliation.amountKind = kind;
        affiliation.amount = amount;
        affiliation.active = true;
        return affiliation;
    }

    public void deactivate() {
        if (!active) {
            throw new DomainException("La afiliación ya está inactiva");
        }
        this.active = false;
    }

    public Long getId() {
        return id;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getName() {
        return name;
    }

    public AffiliationAmountKind getAmountKind() {
        return amountKind;
    }

    public Money getAmount() {
        return amount;
    }

    public boolean isActive() {
        return active;
    }
}
