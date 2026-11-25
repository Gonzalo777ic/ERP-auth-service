package com.valeerp.hr.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmployeeAddressRequest {

    @NotBlank(message = "La dirección de domicilio es obligatoria")
    @Size(max = 240)
    private String line;

    @Size(max = 120)
    private String district;

    @Size(max = 120)
    private String city;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
