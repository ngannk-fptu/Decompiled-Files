/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseException;

public class ResponseTimeoutException
extends ResponseException {
    public ResponseTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseTimeoutException(String message) {
        super(message);
    }

    public ResponseTimeoutException(Throwable cause) {
        super(cause);
    }
}

