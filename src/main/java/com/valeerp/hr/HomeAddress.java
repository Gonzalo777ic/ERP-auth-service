package com.valeerp.hr;

import com.valeerp.kernel.DomainException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Dirección de domicilio del trabajador.
 */
@Embeddable
public class HomeAddress {

    public static final int LINE_MAX = 240;
    public static final int PLACE_MAX = 120;

    @Column(name = "home_line", length = 240)
    private String line;

    @Column(name = "home_district", length = 120)
    private String district;

    @Column(name = "home_city", length = 120)
    private String city;

    protected HomeAddress() {
    }

    public static HomeAddress of(String line, String district, String city) {
        if (line == null || line.isBlank()) {
            throw new DomainException("La dirección de domicilio es obligatoria");
        }
        if (line.trim().length() > LINE_MAX) {
            throw new DomainException("La dirección de domicilio es demasiado larga");
        }
        HomeAddress address = new HomeAddress();
        address.line = line.trim();
        address.district = place(district);
        address.city = place(city);
        return address;
    }

    private static String place(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String value = raw.trim();
        if (value.length() > PLACE_MAX) {
            throw new DomainException("El distrito o la ciudad es demasiado largo");
        }
        return value;
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
