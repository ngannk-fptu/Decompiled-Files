/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.service;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SpaceCategoryEnum {
    ALL("conf_all"),
    FAVOURITES("conf_favorites"),
    GLOBAL("conf_global"),
    PERSONAL("conf_personal");

    private static final Map<String, SpaceCategoryEnum> lookup;
    private final String rerepresentation;

    private SpaceCategoryEnum(String str) {
        this.rerepresentation = str;
    }

    public String getRepresentation() {
        return this.rerepresentation;
    }

    public String toString() {
        return this.getRepresentation();
    }

    public static SpaceCategoryEnum get(String rep) {
        return lookup.get(rep);
    }

    static {
        lookup = new HashMap<String, SpaceCategoryEnum>(SpaceCategoryEnum.values().length);
        for (SpaceCategoryEnum cat : EnumSet.allOf(SpaceCategoryEnum.class)) {
            lookup.put(cat.getRepresentation(), cat);
        }
    }
}

