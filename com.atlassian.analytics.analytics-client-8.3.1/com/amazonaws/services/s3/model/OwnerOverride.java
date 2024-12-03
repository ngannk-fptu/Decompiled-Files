/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum OwnerOverride {
    DESTINATION("Destination");

    private final String id;

    private OwnerOverride(String id) {
        this.id = id;
    }

    public String toString() {
        return this.id;
    }

    public static OwnerOverride fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (OwnerOverride enumEntry : OwnerOverride.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

