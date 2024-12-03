/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum InclusionCriteria {
    ANY(""),
    ALL("+"),
    NONE("-");

    private final String token;
    private static final Map<String, InclusionCriteria> lookup;

    private InclusionCriteria(String token) {
        this.token = token;
    }

    String getToken() {
        return this.token;
    }

    public static InclusionCriteria get(String token) {
        return lookup.get(token);
    }

    static {
        lookup = new HashMap<String, InclusionCriteria>(3);
        for (InclusionCriteria value : EnumSet.allOf(InclusionCriteria.class)) {
            lookup.put(value.getToken(), value);
        }
    }
}

