/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum Tier {
    Standard("Standard"),
    Bulk("Bulk"),
    Expedited("Expedited");

    private final String value;

    private Tier(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static Tier fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (Tier enumEntry : Tier.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

