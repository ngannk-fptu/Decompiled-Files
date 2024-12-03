/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.io.IOException;

public class HttpStreamResetException
extends IOException {
    private static final long serialVersionUID = 1L;

    public HttpStreamResetException(String message) {
        super(message);
    }

    public HttpStreamResetException(String message, Throwable cause) {
        super(message, cause);
    }
}

