/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.assurance.claims.CountryCode;
import net.jcip.annotations.Immutable;

@Immutable
public final class ISO3166_1Alpha2CountryCode
extends CountryCode {
    public ISO3166_1Alpha2CountryCode(String value) {
        super(value.toUpperCase());
        if (value.length() != 2) {
            throw new IllegalArgumentException("The ISO 3166-1 alpha-2 country code must be two letters");
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof ISO3166_1Alpha2CountryCode && this.toString().equals(object.toString());
    }

    public static ISO3166_1Alpha2CountryCode parse(String s) throws ParseException {
        try {
            return new ISO3166_1Alpha2CountryCode(s);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

