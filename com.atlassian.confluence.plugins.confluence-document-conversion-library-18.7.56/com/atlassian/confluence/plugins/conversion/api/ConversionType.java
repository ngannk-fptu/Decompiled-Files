/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.api;

public enum ConversionType {
    THUMBNAIL,
    DOCUMENT,
    POSTER,
    DOCUMENT_HD(true),
    POSTER_HD(true);

    private final boolean optional;

    private ConversionType(boolean optional) {
        this.optional = optional;
    }

    private ConversionType() {
        this.optional = false;
    }

    public boolean isOptional() {
        return this.optional;
    }
}

