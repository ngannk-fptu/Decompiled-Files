/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum DeleteMarkerReplicationStatus {
    ENABLED("Enabled"),
    DISABLED("Disabled");

    private final String value;

    private DeleteMarkerReplicationStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static DeleteMarkerReplicationStatus fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (DeleteMarkerReplicationStatus enumEntry : DeleteMarkerReplicationStatus.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

