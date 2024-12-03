/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseTimeoutException;

public class ResponseConnectTimeoutException
extends ResponseTimeoutException {
    public ResponseConnectTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseConnectTimeoutException(String message) {
        super(message);
    }

    public ResponseConnectTimeoutException(Throwable cause) {
        super(cause);
    }
}

