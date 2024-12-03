/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.io.InterruptedIOException;
import org.apache.hc.core5.http.HttpException;

public class ConnectionRequestTimeoutException
extends InterruptedIOException {
    private static final long serialVersionUID = 1L;

    public ConnectionRequestTimeoutException() {
    }

    public ConnectionRequestTimeoutException(String message) {
        super(HttpException.clean(message));
    }
}

