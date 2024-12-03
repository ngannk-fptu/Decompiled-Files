/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.HttpException;

public class ResponseParsingHttpException
extends HttpException {
    public ResponseParsingHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseParsingHttpException(String message) {
        super(message);
    }
}

