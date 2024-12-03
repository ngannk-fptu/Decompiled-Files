/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.config;

public class InsecureUrlException
extends RuntimeException {
    public static final String FIELD_BASE_URL = "base-url";
    private final String fieldName;

    public InsecureUrlException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }
}

