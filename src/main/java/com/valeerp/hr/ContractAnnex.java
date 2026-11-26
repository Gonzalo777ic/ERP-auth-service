package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Anexo del contrato. Guarda el nombre y el comentario.
 * El binario no vive en este servicio.
 */
@Entity
@Table(name = "hr_contract_annexes")
public class ContractAnnex {

    public static final int FILE_NAME_MAX = 255;
    public static final int COMMENT_MAX = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(length = 500)
    private String comment;

    @Column(name = "recorded_by", nullable = false, length = 120)
    private String recordedBy;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected ContractAnnex() {
    }

    public static ContractAnnex record(Long contractId, String fileName, String comment, String actor, Instant when) {
        if (contractId == null || when == null) {
            throw new DomainException("El anexo necesita contrato y fecha");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new DomainException("El nombre del archivo es obligatorio");
        }
        String name = fileName.trim();
        if (name.length() > FILE_NAME_MAX) {
            throw new DomainException("El nombre del archivo es demasiado largo");
        }
        String note = comment == null || comment.isBlank() ? null : comment.trim();
        if (note != null && note.length() > COMMENT_MAX) {
            throw new DomainException("El comentario del anexo es demasiado largo");
        }
        ContractAnnex annex = new ContractAnnex();
        annex.contractId = contractId;
        annex.fileName = name;
        annex.comment = note;
        annex.recordedBy = actor == null || actor.isBlank() ? "sistema" : actor.trim();
        annex.recordedAt = when;
        return annex;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getComment() {
        return comment;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }
}
