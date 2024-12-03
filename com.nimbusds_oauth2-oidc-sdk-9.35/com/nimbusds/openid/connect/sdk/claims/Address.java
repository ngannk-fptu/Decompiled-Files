/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public class Address
extends ClaimsSet {
    public static final String FORMATTED_CLAIM_NAME = "formatted";
    public static final String STREET_ADDRESS_CLAIM_NAME = "street_address";
    public static final String LOCALITY_CLAIM_NAME = "locality";
    public static final String REGION_CLAIM_NAME = "region";
    public static final String POSTAL_CODE_CLAIM_NAME = "postal_code";
    public static final String COUNTRY_CLAIM_NAME = "country";
    public static final String COUNTRY_CODE_CLAIM_NAME = "country_code";
    private static final Set<String> stdClaimNames = new LinkedHashSet<String>();

    public static Set<String> getStandardClaimNames() {
        return Collections.unmodifiableSet(stdClaimNames);
    }

    public Address() {
    }

    public Address(JSONObject jsonObject) {
        super(jsonObject);
    }

    public void setFormatted(String formatted) {
        this.setClaim(FORMATTED_CLAIM_NAME, formatted);
    }

    public String getFormatted() {
        return this.getStringClaim(FORMATTED_CLAIM_NAME);
    }

    public void setStreetAddress(String streetAddress) {
        this.setClaim(STREET_ADDRESS_CLAIM_NAME, streetAddress);
    }

    public String getStreetAddress() {
        return this.getStringClaim(STREET_ADDRESS_CLAIM_NAME);
    }

    public void setLocality(String locality) {
        this.setClaim(LOCALITY_CLAIM_NAME, locality);
    }

    public String getLocality() {
        return this.getStringClaim(LOCALITY_CLAIM_NAME);
    }

    public void setRegion(String region) {
        this.setClaim(REGION_CLAIM_NAME, region);
    }

    public String getRegion() {
        return this.getStringClaim(REGION_CLAIM_NAME);
    }

    public void setPostalCode(String postalCode) {
        this.setClaim(POSTAL_CODE_CLAIM_NAME, postalCode);
    }

    public String getPostalCode() {
        return this.getStringClaim(POSTAL_CODE_CLAIM_NAME);
    }

    public void setCountry(String country) {
        this.setClaim(COUNTRY_CLAIM_NAME, country);
    }

    public String getCountry() {
        return this.getStringClaim(COUNTRY_CLAIM_NAME);
    }

    public void setCountryCode(CountryCode countryCode) {
        String value = countryCode != null ? countryCode.getValue() : null;
        this.setClaim(COUNTRY_CODE_CLAIM_NAME, value);
    }

    public CountryCode getCountryCode() {
        String value = this.getStringClaim(COUNTRY_CODE_CLAIM_NAME);
        if (value == null) {
            return null;
        }
        try {
            return CountryCode.parse(value);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static Address parse(String json) throws ParseException {
        JSONObject jsonObject = JSONObjectUtils.parse(json);
        try {
            return new Address(jsonObject);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    static {
        stdClaimNames.add(FORMATTED_CLAIM_NAME);
        stdClaimNames.add(STREET_ADDRESS_CLAIM_NAME);
        stdClaimNames.add(LOCALITY_CLAIM_NAME);
        stdClaimNames.add(REGION_CLAIM_NAME);
        stdClaimNames.add(POSTAL_CODE_CLAIM_NAME);
        stdClaimNames.add(COUNTRY_CLAIM_NAME);
        stdClaimNames.add(COUNTRY_CODE_CLAIM_NAME);
    }
}

