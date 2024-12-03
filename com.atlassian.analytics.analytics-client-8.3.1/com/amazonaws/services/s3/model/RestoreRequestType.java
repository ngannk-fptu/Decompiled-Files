/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum RestoreRequestType {
    SELECT("SELECT");

    private final String type;

    private RestoreRequestType(String type) {
        this.type = type;
    }

    public String toString() {
        return this.type;
    }

    public static RestoreRequestType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (RestoreRequestType enumEntry : RestoreRequestType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

