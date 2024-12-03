/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import com.atlassian.voorhees.ErrorCode;
import com.atlassian.voorhees.JsonError;

class JsonRpcException
extends Exception {
    private final ErrorCode errorCode;
    private final String message;

    public JsonRpcException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public JsonError toJsonError() {
        return new JsonError(this.errorCode.intValue(), this.message);
    }
}

