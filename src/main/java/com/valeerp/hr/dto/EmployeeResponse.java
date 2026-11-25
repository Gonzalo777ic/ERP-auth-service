package com.valeerp.hr.dto;

import com.valeerp.hr.Employee;
import com.valeerp.hr.EmployeePhone;
import com.valeerp.hr.HomeAddress;
import com.valeerp.hr.IdentityDocumentType;
import com.valeerp.hr.PhoneUse;
import com.valeerp.hr.Sex;

import java.time.LocalDate;
import java.util.List;

public class EmployeeResponse {

    private final Long id;
    private final Long legalEntityId;
    private final IdentityDocumentType documentType;
    private final String documentNumber;
    private final String surnames;
    private final String givenNames;
    private final String displayName;
    private final String personalEmail;
    private final String workEmail;
    private final String cuspp;
    private final List<PhoneView> phones;
    private final LocalDate birthDate;
    private final int ageYears;
    private final int childrenCount;
    private final Sex sex;
    private final AddressView address;

    private EmployeeResponse(
            Long id,
            Long legalEntityId,
            IdentityDocumentType documentType,
            String documentNumber,
            String surnames,
            String givenNames,
            String displayName,
            String personalEmail,
            String workEmail,
            String cuspp,
            List<PhoneView> phones,
            LocalDate birthDate,
            int ageYears,
            int childrenCount,
            Sex sex,
            AddressView address
    ) {
        this.id = id;
        this.legalEntityId = legalEntityId;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.surnames = surnames;
        this.givenNames = givenNames;
        this.displayName = displayName;
        this.personalEmail = personalEmail;
        this.workEmail = workEmail;
        this.cuspp = cuspp;
        this.phones = phones;
        this.birthDate = birthDate;
        this.ageYears = ageYears;
        this.childrenCount = childrenCount;
        this.sex = sex;
        this.address = address;
    }

    public static EmployeeResponse from(Employee employee, LocalDate today) {
        HomeAddress home = employee.getHomeAddress();
        return new EmployeeResponse(
                employee.getId(),
                employee.getLegalEntityId(),
                employee.getDocumentType(),
                employee.getDocumentNumber(),
                employee.getSurnames(),
                employee.getGivenNames(),
                employee.displayName(),
                employee.getPersonalEmail(),
                employee.getWorkEmail(),
                employee.getCuspp(),
                employee.getPhones().stream().map(PhoneView::from).toList(),
                employee.getBirthDate(),
                employee.ageAt(today),
                employee.getChildrenCount(),
                employee.getSex(),
                home == null ? null : AddressView.from(home)
        );
    }

    public Long getId() {
        return id;
    }

    public Long getLegalEntityId() {
        return legalEntityId;
    }

    public IdentityDocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getSurnames() {
        return surnames;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public String getCuspp() {
        return cuspp;
    }

    public List<PhoneView> getPhones() {
        return phones;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public int getAgeYears() {
        return ageYears;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public Sex getSex() {
        return sex;
    }

    public AddressView getAddress() {
        return address;
    }

    public static final class PhoneView {
        private final PhoneUse use;
        private final String number;

        private PhoneView(PhoneUse use, String number) {
            this.use = use;
            this.number = number;
        }

        static PhoneView from(EmployeePhone phone) {
            return new PhoneView(phone.getUse(), phone.getNumber());
        }

        public PhoneUse getUse() {
            return use;
        }

        public String getNumber() {
            return number;
        }
    }

    public static final class AddressView {
        private final String line;
        private final String district;
        private final String city;

        private AddressView(String line, String district, String city) {
            this.line = line;
            this.district = district;
            this.city = city;
        }

        static AddressView from(HomeAddress address) {
            return new AddressView(address.getLine(), address.getDistrict(), address.getCity());
        }

        public String getLine() {
            return line;
        }

        public String getDistrict() {
            return district;
        }

        public String getCity() {
            return city;
        }
    }
}
