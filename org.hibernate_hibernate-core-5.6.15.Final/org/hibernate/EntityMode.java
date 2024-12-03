/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Locale;

public enum EntityMode {
    POJO("pojo"),
    MAP("dynamic-map");

    private final String externalName;

    private EntityMode(String externalName) {
        this.externalName = externalName;
    }

    public String getExternalName() {
        return this.externalName;
    }

    public String toString() {
        return this.externalName;
    }

    public static EntityMode parse(String entityMode) {
        if (entityMode == null) {
            return POJO;
        }
        if (EntityMode.MAP.externalName.equalsIgnoreCase(entityMode)) {
            return MAP;
        }
        return EntityMode.valueOf(entityMode.toUpperCase(Locale.ENGLISH));
    }
}

