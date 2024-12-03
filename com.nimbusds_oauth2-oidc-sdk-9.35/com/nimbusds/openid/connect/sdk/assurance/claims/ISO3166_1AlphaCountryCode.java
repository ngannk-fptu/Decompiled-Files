/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;

public abstract class ISO3166_1AlphaCountryCode
extends CountryCode {
    private static final long serialVersionUID = -3383887427716306419L;

    public ISO3166_1AlphaCountryCode(String value) {
        super(value.toUpperCase());
        if (!StringUtils.isAlpha(value)) {
            throw new IllegalArgumentException("The ISO 3166-1 alpha country code must consist of letters");
        }
    }

    public abstract String getCountryName();
}

