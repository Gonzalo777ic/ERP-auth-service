package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Cargo desempeñado en el contrato. El cierre pone fecha hasta; no hay baja física. */
@Entity
@Table(name = "hr_contract_roles")
public class ContractRole {

    public static final int TITLE_MAX = 160;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(name = "starts_on", nullable = false)
    private LocalDate startsOn;

    @Column(name = "ends_on")
    private LocalDate endsOn;

    protected ContractRole() {
    }

    public static ContractRole open(Long contractId, String title, LocalDate startsOn) {
        if (contractId == null || startsOn == null) {
            throw new DomainException("El cargo necesita contrato y fecha de inicio");
        }
        if (title == null || title.isBlank()) {
            throw new DomainException("El cargo es obligatorio");
        }
        String value = title.trim();
        if (value.length() > TITLE_MAX) {
            throw new DomainException("El cargo es demasiado largo");
        }
        ContractRole role = new ContractRole();
        role.contractId = contractId;
        role.title = value;
        role.startsOn = startsOn;
        return role;
    }

    public void close(LocalDate until) {
        if (endsOn != null) {
            throw new DomainException("El cargo ya tiene fecha de fin");
        }
        if (until == null || until.isBefore(startsOn)) {
            throw new DomainException("La fecha hasta del cargo no puede ser anterior al inicio");
        }
        this.endsOn = until;
    }

    public Long getId() {
        return id;
    }

    public Long getContractId() {
        return contractId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public LocalDate getEndsOn() {
        return endsOn;
    }
}
