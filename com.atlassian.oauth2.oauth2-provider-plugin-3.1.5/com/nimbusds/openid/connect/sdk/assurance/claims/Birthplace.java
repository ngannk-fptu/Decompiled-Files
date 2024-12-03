/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public final class Birthplace
extends ClaimsSet {
    public static final String COUNTRY_CLAIM_NAME = "country";
    public static final String REGION_CLAIM_NAME = "region";
    public static final String LOCALITY_CLAIM_NAME = "locality";
    private static final Set<String> stdClaimNames = new LinkedHashSet<String>();

    public static Set<String> getStandardClaimNames() {
        return Collections.unmodifiableSet(stdClaimNames);
    }

    public Birthplace(CountryCode countryCode, String region, String locality) {
        if (countryCode != null) {
            this.setClaim(COUNTRY_CLAIM_NAME, countryCode.getValue());
        }
        this.setClaim(REGION_CLAIM_NAME, region);
        this.setClaim(LOCALITY_CLAIM_NAME, locality);
    }

    public Birthplace(JSONObject jsonObject) {
        super(jsonObject);
    }

    public CountryCode getCountry() {
        String code = this.getStringClaim(COUNTRY_CLAIM_NAME);
        if (code == null) {
            return null;
        }
        try {
            return CountryCode.parse(code);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setCountry(CountryCode country) {
        if (country != null) {
            this.setClaim(COUNTRY_CLAIM_NAME, country.getValue());
        } else {
            this.setClaim(COUNTRY_CLAIM_NAME, null);
        }
    }

    public String getRegion() {
        return this.getStringClaim(REGION_CLAIM_NAME);
    }

    public void setRegion(String region) {
        this.setClaim(REGION_CLAIM_NAME, region);
    }

    public String getLocality() {
        return this.getStringClaim(LOCALITY_CLAIM_NAME);
    }

    public void setLocality(String locality) {
        this.setClaim(LOCALITY_CLAIM_NAME, locality);
    }

    static {
        stdClaimNames.add(LOCALITY_CLAIM_NAME);
        stdClaimNames.add(REGION_CLAIM_NAME);
        stdClaimNames.add(COUNTRY_CLAIM_NAME);
    }
}

