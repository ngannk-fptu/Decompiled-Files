/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.agent.rest.ErrorResponseCode;

public class ErrorResponse {
    public final ErrorResponseCode code;
    public final String message;

    public ErrorResponse(ErrorResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }
}

