/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient;

import org.apache.commons.httpclient.ConnectTimeoutException;

public class ConnectionPoolTimeoutException
extends ConnectTimeoutException {
    public ConnectionPoolTimeoutException() {
    }

    public ConnectionPoolTimeoutException(String message) {
        super(message);
    }

    public ConnectionPoolTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

