/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model;

public enum TruthValue {
    TRUE,
    FALSE,
    UNKNOWN;


    public boolean toBoolean(boolean defaultValue) {
        if (this == TRUE) {
            return true;
        }
        if (this == FALSE) {
            return false;
        }
        return defaultValue;
    }

    public static boolean toBoolean(TruthValue value, boolean defaultValue) {
        return value != null ? value.toBoolean(defaultValue) : defaultValue;
    }
}

