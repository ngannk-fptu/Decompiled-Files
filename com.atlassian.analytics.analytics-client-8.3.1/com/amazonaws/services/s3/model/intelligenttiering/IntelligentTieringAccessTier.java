/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model.intelligenttiering;

public enum IntelligentTieringAccessTier {
    ARCHIVE_ACCESS("ARCHIVE_ACCESS"),
    DEEP_ARCHIVE_ACCESS("DEEP_ARCHIVE_ACCESS");

    private final String intelligentTieringAccessTier;

    private IntelligentTieringAccessTier(String intelligentTieringAccessTier) {
        this.intelligentTieringAccessTier = intelligentTieringAccessTier;
    }

    public String toString() {
        return this.intelligentTieringAccessTier;
    }

    public static IntelligentTieringAccessTier fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (IntelligentTieringAccessTier enumEntry : IntelligentTieringAccessTier.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

