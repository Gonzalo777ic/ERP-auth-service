package com.valeerp.hr;

import java.util.Arrays;
import java.util.List;

/**
 * Opciones que la pantalla muestra. Los textos viven en los enums, no en el controlador.
 */
public final class HrCatalog {

    private HrCatalog() {
    }

    public static List<Option> banks() {
        return Arrays.stream(PayrollBank.values())
                .map(bank -> new Option(bank.name(), bank.label(), bank.sunatCode()))
                .toList();
    }

    public static List<Option> workerKinds() {
        return Arrays.stream(WorkerKind.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> pensionRegimes() {
        return Arrays.stream(PensionRegime.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> laborRegimes() {
        return Arrays.stream(LaborRegime.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> paymentMethods() {
        return Arrays.stream(WagePaymentMethod.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> accountTypes() {
        return Arrays.stream(BankAccountType.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> contractTypes() {
        return Arrays.stream(ContractType.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> affiliationKinds() {
        return Arrays.stream(AffiliationAmountKind.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> retentionModes() {
        return Arrays.stream(RetentionMode.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> contractStatuses() {
        return Arrays.stream(ContractStatus.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public static List<Option> wageStatuses() {
        return Arrays.stream(WageChangeStatus.values()).map(item -> new Option(item.name(), item.label(), null)).toList();
    }

    public record Option(String code, String label, String sunatCode) {
    }
}
