/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseTimeoutException;

public class ResponseReadTimeoutException
extends ResponseTimeoutException {
    public ResponseReadTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseReadTimeoutException(String message) {
        super(message);
    }

    public ResponseReadTimeoutException(Throwable cause) {
        super(cause);
    }
}

