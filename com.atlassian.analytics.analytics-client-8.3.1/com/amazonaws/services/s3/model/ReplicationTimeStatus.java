/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum ReplicationTimeStatus {
    Enabled("Enabled"),
    Disabled("Disabled");

    private final String status;

    private ReplicationTimeStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return this.status;
    }

    public static ReplicationTimeStatus fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (ReplicationTimeStatus enumEntry : ReplicationTimeStatus.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

