/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.api.net;

import com.atlassian.sal.api.net.ResponseException;

public class ResponseProtocolException
extends ResponseException {
    public ResponseProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseProtocolException(String message) {
        super(message);
    }

    public ResponseProtocolException(Throwable cause) {
        super(cause);
    }
}

