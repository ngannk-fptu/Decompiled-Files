/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum QuoteFields {
    ALWAYS("ALWAYS"),
    ASNEEDED("ASNEEDED");

    private final String value;

    private QuoteFields(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static QuoteFields fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (QuoteFields enumEntry : QuoteFields.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

