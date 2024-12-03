/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.HttpException;
import javax.annotation.Nullable;

public class HttpServiceException
extends HttpException {
    private final int statusCode;
    private final Integer errorCode;

    public HttpServiceException(String message, int statusCode) {
        this(message, statusCode, null);
    }

    public HttpServiceException(String message, int statusCode, @Nullable Integer errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    @Nullable
    public Integer getErrorCode() {
        return this.errorCode;
    }
}

