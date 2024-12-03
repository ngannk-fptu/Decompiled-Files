/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseException;

public class ResponseTransportException
extends ResponseException {
    public ResponseTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseTransportException(String message) {
        super(message);
    }

    public ResponseTransportException(Throwable cause) {
        super(cause);
    }
}

