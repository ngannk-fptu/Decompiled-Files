/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

public enum IntelligentTieringStatus {
    Enabled("Enabled"),
    Disabled("Disabled");

    private final String intelligentTieringStatus;

    private IntelligentTieringStatus(String intelligentTieringStatus) {
        this.intelligentTieringStatus = intelligentTieringStatus;
    }

    public String toString() {
        return this.intelligentTieringStatus;
    }

    public static IntelligentTieringStatus fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (IntelligentTieringStatus enumEntry : IntelligentTieringStatus.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

