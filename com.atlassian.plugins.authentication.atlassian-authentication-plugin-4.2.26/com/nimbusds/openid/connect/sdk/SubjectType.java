/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;

public enum SubjectType {
    PAIRWISE,
    PUBLIC;


    public String toString() {
        return super.toString().toLowerCase();
    }

    public static SubjectType parse(String s) throws ParseException {
        if (s == null || s.trim().isEmpty()) {
            throw new ParseException("Null or empty subject type string");
        }
        if ("pairwise".equals(s)) {
            return PAIRWISE;
        }
        if ("public".equals(s)) {
            return PUBLIC;
        }
        throw new ParseException("Unknown subject type: " + s);
    }
}

