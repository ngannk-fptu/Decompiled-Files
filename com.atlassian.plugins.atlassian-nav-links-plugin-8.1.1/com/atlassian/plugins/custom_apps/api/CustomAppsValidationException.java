/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.custom_apps.api;

public class CustomAppsValidationException
extends Exception {
    private final String field;
    private final String message;

    public CustomAppsValidationException(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return this.field;
    }

    public String getValidationError() {
        return this.message;
    }
}

