/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.HttpException;

public class IOHttpException
extends HttpException {
    public IOHttpException(String message, Throwable cause) {
        super(message, cause);
    }
}

