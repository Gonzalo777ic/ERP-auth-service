package com.valeerp.inventory.auth.controller;

import com.valeerp.hr.HrCatalog;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hr/catalog")
public class HrCatalogController {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_AUDITOR')")
    public Map<String, List<HrCatalog.Option>> catalog() {
        return Map.ofEntries(
                Map.entry("banks", HrCatalog.banks()),
                Map.entry("workerKinds", HrCatalog.workerKinds()),
                Map.entry("pensionRegimes", HrCatalog.pensionRegimes()),
                Map.entry("laborRegimes", HrCatalog.laborRegimes()),
                Map.entry("paymentMethods", HrCatalog.paymentMethods()),
                Map.entry("accountTypes", HrCatalog.accountTypes()),
                Map.entry("contractTypes", HrCatalog.contractTypes()),
                Map.entry("affiliationKinds", HrCatalog.affiliationKinds()),
                Map.entry("retentionModes", HrCatalog.retentionModes()),
                Map.entry("contractStatuses", HrCatalog.contractStatuses()),
                Map.entry("wageStatuses", HrCatalog.wageStatuses())
        );
    }
}
