/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect;

public enum ResultColumnReferenceStrategy {
    SOURCE,
    ALIAS,
    ORDINAL;


    public static ResultColumnReferenceStrategy resolveByName(String name) {
        if (ALIAS.name().equalsIgnoreCase(name)) {
            return ALIAS;
        }
        if (ORDINAL.name().equalsIgnoreCase(name)) {
            return ORDINAL;
        }
        return SOURCE;
    }
}

