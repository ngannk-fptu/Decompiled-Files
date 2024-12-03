/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum SseKmsEncryptedObjectsStatus {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    private SseKmsEncryptedObjectsStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static SseKmsEncryptedObjectsStatus fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (SseKmsEncryptedObjectsStatus enumEntry : SseKmsEncryptedObjectsStatus.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

