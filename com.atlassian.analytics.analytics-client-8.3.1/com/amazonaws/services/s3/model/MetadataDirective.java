/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum MetadataDirective {
    COPY("COPY"),
    REPLACE("REPLACE");

    private String value;

    private MetadataDirective(String value) {
        this.value = value;
    }

    public static MetadataDirective fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (MetadataDirective enumEntry : MetadataDirective.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

