/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum MetricsStatus {
    Enabled("Enabled"),
    Disabled("Disabled");

    private final String value;

    private MetricsStatus(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static MetricsStatus fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (MetricsStatus enumEntry : MetricsStatus.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

