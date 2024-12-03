/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import net.jcip.annotations.Immutable;

@Immutable
public final class ISO3166_3CountryCode
extends CountryCode {
    private static final long serialVersionUID = 614967184722743546L;
    public static final ISO3166_3CountryCode BQAQ = new ISO3166_3CountryCode("BQAQ");
    public static final ISO3166_3CountryCode BUMM = new ISO3166_3CountryCode("BUMM");
    public static final ISO3166_3CountryCode BYAA = new ISO3166_3CountryCode("BYAA");
    public static final ISO3166_3CountryCode CTKI = new ISO3166_3CountryCode("CTKI");
    public static final ISO3166_3CountryCode CSHH = new ISO3166_3CountryCode("CSHH");
    public static final ISO3166_3CountryCode DYBJ = new ISO3166_3CountryCode("DYBJ");
    public static final ISO3166_3CountryCode NQAQ = new ISO3166_3CountryCode("NQAQ");
    public static final ISO3166_3CountryCode TPTL = new ISO3166_3CountryCode("TPTL");
    public static final ISO3166_3CountryCode FXFR = new ISO3166_3CountryCode("FXFR");
    public static final ISO3166_3CountryCode AIDJ = new ISO3166_3CountryCode("AIDJ");
    public static final ISO3166_3CountryCode FQHH = new ISO3166_3CountryCode("FQHH");
    public static final ISO3166_3CountryCode DDDE = new ISO3166_3CountryCode("DDDE");
    public static final ISO3166_3CountryCode GEHH = new ISO3166_3CountryCode("GEHH");
    public static final ISO3166_3CountryCode JTUM = new ISO3166_3CountryCode("JTUM");
    public static final ISO3166_3CountryCode MIUM = new ISO3166_3CountryCode("MIUM");
    public static final ISO3166_3CountryCode ANHH = new ISO3166_3CountryCode("ANHH");
    public static final ISO3166_3CountryCode NTHH = new ISO3166_3CountryCode("NTHH");
    public static final ISO3166_3CountryCode NHVU = new ISO3166_3CountryCode("NHVU");
    public static final ISO3166_3CountryCode PCHH = new ISO3166_3CountryCode("PCHH");
    public static final ISO3166_3CountryCode PZPA = new ISO3166_3CountryCode("PZPA");
    public static final ISO3166_3CountryCode CSXX = new ISO3166_3CountryCode("CSXX");
    public static final ISO3166_3CountryCode SKIN = new ISO3166_3CountryCode("SKIN");
    public static final ISO3166_3CountryCode RHZW = new ISO3166_3CountryCode("RHZW");
    public static final ISO3166_3CountryCode PUUM = new ISO3166_3CountryCode("PUUM");
    public static final ISO3166_3CountryCode HVBF = new ISO3166_3CountryCode("HVBF");
    public static final ISO3166_3CountryCode SUHH = new ISO3166_3CountryCode("SUHH");
    public static final ISO3166_3CountryCode VDVN = new ISO3166_3CountryCode("VDVN");
    public static final ISO3166_3CountryCode WKUM = new ISO3166_3CountryCode("WKUM");
    public static final ISO3166_3CountryCode YDYE = new ISO3166_3CountryCode("YDYE");
    public static final ISO3166_3CountryCode YUCS = new ISO3166_3CountryCode("YUCS");
    public static final ISO3166_3CountryCode ZRCD = new ISO3166_3CountryCode("ZRCD");
    private static final Properties CODES_RESOURCE = new Properties();

    public ISO3166_3CountryCode(String value) {
        super(value.toUpperCase());
        if (value.length() != 4 || !StringUtils.isAlpha(value)) {
            throw new IllegalArgumentException("The ISO 3166-3 country code must be 4 letters");
        }
    }

    public ISO3166_1Alpha2CountryCode getFormerCode() {
        return new ISO3166_1Alpha2CountryCode(this.getFirstComponentString());
    }

    public ISO3166_1Alpha2CountryCode getNewCode() {
        if ("HH".equals(this.getSecondComponentString()) || "XX".equals(this.getSecondComponentString())) {
            return null;
        }
        return new ISO3166_1Alpha2CountryCode(this.getSecondComponentString());
    }

    public String getFirstComponentString() {
        return this.getValue().substring(0, 2);
    }

    public String getSecondComponentString() {
        return this.getValue().substring(2, 4);
    }

    public String getCountryName() {
        if (CODES_RESOURCE.isEmpty()) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("iso3166_3-codes.properties");
            try {
                CODES_RESOURCE.load(is);
            }
            catch (IOException e) {
                return null;
            }
        }
        return CODES_RESOURCE.getProperty(this.getValue());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ISO3166_3CountryCode && this.toString().equals(object.toString());
    }

    public static ISO3166_3CountryCode parse(String s) throws ParseException {
        try {
            return new ISO3166_3CountryCode(s);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

