/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import org.apache.hc.core5.http.HttpException;

public class ProtocolException
extends HttpException {
    private static final long serialVersionUID = -2143571074341228994L;

    public ProtocolException() {
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(String format, Object ... args) {
        super(format, args);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}

