package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

/** Renovación del vencimiento. Libro: no se oculta ni se borra. Sin locación. */
@Entity
@Table(name = "hr_contract_renewals")
public class ContractRenewal {

    public static final int NOTES_MAX = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "starts_on", nullable = false)
    private LocalDate startsOn;

    @Column(name = "ends_on", nullable = false)
    private LocalDate endsOn;

    @Column(length = 500)
    private String notes;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    protected ContractRenewal() {
    }

    public static ContractRenewal record(
            Long contractId,
            LocalDate startsOn,
            LocalDate endsOn,
            String notes,
            Instant when
    ) {
        if (contractId == null || startsOn == null || endsOn == null || when == null) {
            throw new DomainException("La renovación necesita fechas y contrato");
        }
        if (endsOn.isBefore(startsOn)) {
            throw new DomainException("El vencimiento no puede ser anterior al inicio de la renovación");
        }
        ContractRenewal renewal = new ContractRenewal();
        renewal.contractId = contractId;
        renewal.startsOn = startsOn;
        renewal.endsOn = endsOn;
        renewal.notes = notes == null || notes.isBlank() ? null : notes.trim();
        if (renewal.notes != null && renewal.notes.length() > NOTES_MAX) {
            throw new DomainException("Las observaciones de la renovación son demasiado largas");
        }
        renewal.registeredAt = when;
        return renewal;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }

    public LocalDate getEndsOn() {
        return endsOn;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
