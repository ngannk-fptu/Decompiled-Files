/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum MultiRegionKeyType {
    PRIMARY("PRIMARY"),
    REPLICA("REPLICA");

    private String value;

    private MultiRegionKeyType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static MultiRegionKeyType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (MultiRegionKeyType enumEntry : MultiRegionKeyType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

