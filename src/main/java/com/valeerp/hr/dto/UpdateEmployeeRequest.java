package com.valeerp.hr.dto;

import com.valeerp.hr.IdentityDocumentType;
import com.valeerp.hr.Sex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UpdateEmployeeRequest {

    @NotNull(message = "El documento es obligatorio")
    private IdentityDocumentType documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 32)
    private String documentNumber;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 120)
    private String surnames;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 120)
    private String givenNames;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    @NotNull(message = "El sexo es obligatorio")
    private Sex sex;

    @NotNull(message = "La cantidad de hijos es obligatoria")
    private Integer childrenCount;

    @Size(max = 180)
    private String personalEmail;

    @Size(max = 180)
    private String workEmail;

    @Size(max = 12)
    private String cuspp;

    @NotNull(message = "La dirección de domicilio es obligatoria")
    @Valid
    private EmployeeAddressRequest address;

    @Valid
    private List<EmployeePhoneRequest> phones = new ArrayList<>();

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(IdentityDocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Integer childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getCuspp() {
        return cuspp;
    }

    public void setCuspp(String cuspp) {
        this.cuspp = cuspp;
    }

    public EmployeeAddressRequest getAddress() {
        return address;
    }

    public void setAddress(EmployeeAddressRequest address) {
        this.address = address;
    }

    public List<EmployeePhoneRequest> getPhones() {
        return phones;
    }

    public void setPhones(List<EmployeePhoneRequest> phones) {
        this.phones = phones;
    }
}
