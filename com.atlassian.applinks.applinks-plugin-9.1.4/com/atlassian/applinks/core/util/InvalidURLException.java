/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.core.util;

public class InvalidURLException
extends Exception {
    private final String invalidURL;
    private final String field;

    public InvalidURLException(String invalidURL, String field) {
        this.invalidURL = invalidURL;
        this.field = field;
    }

    public InvalidURLException(String invalidURL, String field, Throwable cause) {
        super(cause);
        this.invalidURL = invalidURL;
        this.field = field;
    }

    public String getInvalidURL() {
        return this.invalidURL;
    }

    public String getField() {
        return this.field;
    }
}

