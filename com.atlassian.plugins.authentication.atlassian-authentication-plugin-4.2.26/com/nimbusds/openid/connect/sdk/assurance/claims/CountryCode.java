/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode;

public abstract class CountryCode
extends Identifier {
    protected CountryCode(String value) {
        super(value);
    }

    public ISO3166_1Alpha2CountryCode toISO3166_1Alpha2CountryCode() {
        return (ISO3166_1Alpha2CountryCode)this;
    }

    public static CountryCode parse(String s) throws ParseException {
        return ISO3166_1Alpha2CountryCode.parse(s);
    }

    @Override
    public abstract boolean equals(Object var1);
}

