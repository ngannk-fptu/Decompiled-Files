/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.execchain;

import java.io.InterruptedIOException;

public class RequestAbortedException
extends InterruptedIOException {
    private static final long serialVersionUID = 4973849966012490112L;

    public RequestAbortedException(String message) {
        super(message);
    }

    public RequestAbortedException(String message, Throwable cause) {
        super(message);
        if (cause != null) {
            this.initCause(cause);
        }
    }
}

