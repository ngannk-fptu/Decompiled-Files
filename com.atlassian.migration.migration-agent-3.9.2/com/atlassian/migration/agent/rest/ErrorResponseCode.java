/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.migration.agent.rest;

import org.codehaus.jackson.annotate.JsonValue;

public enum ErrorResponseCode {
    GENERIC(101, "Unhandled error"),
    INVALID_PARAMETER(400, "Invalid parameter"),
    FORBIDDEN(403, "Permissions error");

    private final int code;
    private final String message;

    private ErrorResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @JsonValue
    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return String.format("%s: %s", this.code, this.message);
    }
}

