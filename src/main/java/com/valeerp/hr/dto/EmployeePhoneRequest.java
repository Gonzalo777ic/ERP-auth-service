package com.valeerp.hr.dto;

import com.valeerp.hr.PhoneUse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EmployeePhoneRequest {

    @NotNull(message = "El uso del teléfono es obligatorio")
    private PhoneUse use;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(max = 20)
    private String number;

    public PhoneUse getUse() {
        return use;
    }

    public void setUse(PhoneUse use) {
        this.use = use;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
