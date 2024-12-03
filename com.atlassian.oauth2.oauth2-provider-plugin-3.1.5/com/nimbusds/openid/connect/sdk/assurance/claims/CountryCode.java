/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha3CountryCode;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_3CountryCode;

public abstract class CountryCode
extends Identifier {
    private static final long serialVersionUID = -6171424661935191539L;

    protected CountryCode(String value) {
        super(value);
    }

    public int length() {
        return this.getValue().length();
    }

    public ISO3166_1Alpha2CountryCode toISO3166_1Alpha2CountryCode() {
        return (ISO3166_1Alpha2CountryCode)this;
    }

    public ISO3166_1Alpha3CountryCode toISO3166_1Alpha3CountryCode() {
        return (ISO3166_1Alpha3CountryCode)this;
    }

    public ISO3166_3CountryCode toISO3166_3CountryCode() {
        return (ISO3166_3CountryCode)this;
    }

    public static CountryCode parse(String s) throws ParseException {
        if (3 == s.length()) {
            return ISO3166_1Alpha3CountryCode.parse(s);
        }
        if (2 == s.length()) {
            return ISO3166_1Alpha2CountryCode.parse(s);
        }
        if (4 == s.length()) {
            return ISO3166_3CountryCode.parse(s);
        }
        throw new ParseException("The country code must be 3, 2 or 4 letters");
    }

    @Override
    public abstract boolean equals(Object var1);
}

