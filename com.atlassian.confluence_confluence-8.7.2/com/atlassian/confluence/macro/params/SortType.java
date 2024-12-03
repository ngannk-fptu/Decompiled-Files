/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.params;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SortType {
    TITLE("title"),
    NATURAL("natural"),
    CREATION("creation"),
    MODIFIED("modified");

    private static final Map<String, SortType> lookup;
    final String type;

    private SortType(String type) {
        this.type = type;
    }

    public final String getType() {
        return this.type;
    }

    public static SortType get(String name) {
        return lookup.get(name);
    }

    static {
        lookup = new HashMap<String, SortType>(4);
        for (SortType type : EnumSet.allOf(SortType.class)) {
            lookup.put(type.getType(), type);
        }
    }
}

