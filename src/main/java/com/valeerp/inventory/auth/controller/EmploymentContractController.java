package com.valeerp.inventory.auth.controller;

import com.valeerp.hr.ContractHistoryService;
import com.valeerp.hr.EmploymentContractService;
import com.valeerp.hr.dto.CancelBookRequest;
import com.valeerp.hr.dto.CompleteContractRequest;
import com.valeerp.hr.dto.ContractCommands;
import com.valeerp.hr.dto.ContractReadModel;
import com.valeerp.hr.dto.ContractResponse;
import com.valeerp.hr.dto.CreateContractRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Libro de contratos. Sin DELETE HTTP que borre filas: anular/completar por estado.
 */
@RestController
@RequestMapping("/api/hr/contracts")
public class EmploymentContractController {

    private final EmploymentContractService contractService;
    private final ContractHistoryService historyService;

    public EmploymentContractController(EmploymentContractService contractService, ContractHistoryService historyService) {
        this.contractService = contractService;
        this.historyService = historyService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUDITOR')")
    public ResponseEntity<List<ContractResponse>> list(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) Long companyId
    ) {
        if (employeeId != null) {
            return ResponseEntity.ok(contractService.listByEmployee(employeeId));
        }
        if (companyId != null) {
            return ResponseEntity.ok(contractService.listByCompany(companyId));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/roster")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUDITOR')")
    public ResponseEntity<List<ContractReadModel.Summary>> roster(
            @RequestParam Long legalEntityId,
            @RequestParam(required = false) String document,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        return ResponseEntity.ok(contractService.roster(legalEntityId, document, name, activeOnly));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUDITOR')")
    public ResponseEntity<ContractReadModel.Detail> get(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.detail(id));
    }

    @PutMapping("/{id}/terms")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.Terms> updateTerms(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Terms request
    ) {
        return ResponseEntity.ok(contractService.updateTerms(id, request));
    }

    @PostMapping("/{id}/cease")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractResponse> cease(@PathVariable Long id, @Valid @RequestBody ContractCommands.Cease request) {
        return ResponseEntity.ok(contractService.cease(id, request));
    }

    @PostMapping("/{id}/wages")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.WageRow> proposeWage(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Wage request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.proposeWage(id, request));
    }

    @PostMapping("/{id}/wages/{wageId}/approve")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.WageRow> approveWage(@PathVariable Long id, @PathVariable Long wageId) {
        return ResponseEntity.ok(historyService.approveWage(id, wageId));
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.RoleRow> addRole(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Role request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addRole(id, request));
    }

    @PostMapping("/{id}/shifts")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.ShiftRow> addShift(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Shift request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addShift(id, request));
    }

    @PostMapping("/{id}/affiliations")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.AffiliationRow> addAffiliation(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Affiliation request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addAffiliation(id, request));
    }

    @PostMapping("/{id}/affiliations/{affiliationId}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.AffiliationRow> deactivateAffiliation(
            @PathVariable Long id,
            @PathVariable Long affiliationId
    ) {
        return ResponseEntity.ok(historyService.deactivateAffiliation(id, affiliationId));
    }

    @PostMapping("/{id}/renewals")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.RenewalRow> renew(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Renewal request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.renew(id, request));
    }

    @PostMapping("/{id}/annexes")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractReadModel.AnnexRow> addAnnex(
            @PathVariable Long id,
            @Valid @RequestBody ContractCommands.Annex request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.addAnnex(id, request));
    }

    @GetMapping("/{id}/statements/{kind}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUDITOR')")
    public ResponseEntity<ContractReadModel.Statement> statement(@PathVariable Long id, @PathVariable String kind) {
        return ResponseEntity.ok(historyService.statement(id, kind));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractResponse> createDraft(@Valid @RequestBody CreateContractRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createDraft(request));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.activate(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractResponse> complete(
            @PathVariable Long id,
            @RequestBody(required = false) CompleteContractRequest request
    ) {
        return ResponseEntity.ok(contractService.complete(id, request));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ContractResponse> cancel(
            @PathVariable Long id,
            @RequestBody(required = false) CancelBookRequest request
    ) {
        return ResponseEntity.ok(contractService.cancel(id, request));
    }
}
