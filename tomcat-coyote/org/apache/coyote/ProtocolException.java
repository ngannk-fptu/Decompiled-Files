/*
 * Decompiled with CFR 0.152.
 */
package org.apache.coyote;

public class ProtocolException
extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProtocolException() {
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }
}

