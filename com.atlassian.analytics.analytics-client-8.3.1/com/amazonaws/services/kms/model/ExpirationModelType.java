/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum ExpirationModelType {
    KEY_MATERIAL_EXPIRES("KEY_MATERIAL_EXPIRES"),
    KEY_MATERIAL_DOES_NOT_EXPIRE("KEY_MATERIAL_DOES_NOT_EXPIRE");

    private String value;

    private ExpirationModelType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static ExpirationModelType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (ExpirationModelType enumEntry : ExpirationModelType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

