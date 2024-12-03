/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum ObjectLockRetentionMode {
    GOVERNANCE("GOVERNANCE"),
    COMPLIANCE("COMPLIANCE");

    private final String objectLockRetentionMode;

    private ObjectLockRetentionMode(String objectLockRetentionMode) {
        this.objectLockRetentionMode = objectLockRetentionMode;
    }

    public static ObjectLockRetentionMode fromString(String objectLockRetentionModeString) {
        for (ObjectLockRetentionMode v : ObjectLockRetentionMode.values()) {
            if (!v.toString().equals(objectLockRetentionModeString)) continue;
            return v;
        }
        throw new IllegalArgumentException("Cannot create enum from " + objectLockRetentionModeString + " value!");
    }

    public String toString() {
        return this.objectLockRetentionMode;
    }
}

