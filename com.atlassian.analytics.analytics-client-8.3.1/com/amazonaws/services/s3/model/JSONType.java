/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum JSONType {
    DOCUMENT("DOCUMENT"),
    LINES("LINES");

    private final String jsonType;

    private JSONType(String jsonType) {
        this.jsonType = jsonType;
    }

    public String toString() {
        return this.jsonType;
    }

    public static JSONType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (JSONType enumEntry : JSONType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

