/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.ParseException;

public enum ClaimType {
    NORMAL,
    AGGREGATED,
    DISTRIBUTED;


    public String toString() {
        return super.toString().toLowerCase();
    }

    public static ClaimType parse(String s) throws ParseException {
        if (s.equals("normal")) {
            return NORMAL;
        }
        if (s.equals("aggregated")) {
            return AGGREGATED;
        }
        if (s.equals("distributed")) {
            return DISTRIBUTED;
        }
        throw new ParseException("Unknow claim type: " + s);
    }
}

