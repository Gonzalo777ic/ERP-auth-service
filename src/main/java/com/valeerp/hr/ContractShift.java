package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Horario de trabajo del contrato. Sin locación. */
@Entity
@Table(name = "hr_contract_shifts")
public class ContractShift {

    public static final int NAME_MAX = 80;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(name = "starts_on", nullable = false)
    private LocalDate startsOn;

    protected ContractShift() {
    }

    public static ContractShift assign(Long contractId, String name, LocalDate startsOn) {
        if (contractId == null || startsOn == null) {
            throw new DomainException("El horario necesita contrato y fecha desde");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("El horario es obligatorio");
        }
        String value = name.trim();
        if (value.length() > NAME_MAX) {
            throw new DomainException("El horario es demasiado largo");
        }
        ContractShift shift = new ContractShift();
        shift.contractId = contractId;
        shift.name = value;
        shift.startsOn = startsOn;
        return shift;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartsOn() {
        return startsOn;
    }
}
