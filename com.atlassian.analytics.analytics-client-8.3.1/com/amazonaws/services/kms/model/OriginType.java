/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

public enum OriginType {
    AWS_KMS("AWS_KMS"),
    EXTERNAL("EXTERNAL"),
    AWS_CLOUDHSM("AWS_CLOUDHSM");

    private String value;

    private OriginType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static OriginType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (OriginType enumEntry : OriginType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

