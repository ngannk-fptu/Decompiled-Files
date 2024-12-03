/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class MSISDN
extends Identifier {
    private static final long serialVersionUID = 6919844477369481587L;
    public static final int MAX_LENGTH = 15;

    public MSISDN(String value) {
        super(value);
        if (!StringUtils.isNumeric(value)) {
            throw new IllegalArgumentException("The MSISDN must be a numeric string");
        }
        if (value.length() > 15) {
            throw new IllegalArgumentException("The MSISDN must not contain more than 15 digits");
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MSISDN && this.toString().equals(object.toString());
    }

    public static MSISDN parse(String s) throws ParseException {
        try {
            return new MSISDN(s);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage());
        }
    }
}

