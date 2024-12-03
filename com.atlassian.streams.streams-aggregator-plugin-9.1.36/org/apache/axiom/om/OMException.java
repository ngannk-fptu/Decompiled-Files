/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

public class OMException
extends RuntimeException {
    private static final long serialVersionUID = -730218408325095333L;

    public OMException() {
    }

    public OMException(String message) {
        super(message);
    }

    public OMException(String message, Throwable cause) {
        super(message, cause);
    }

    public OMException(Throwable cause) {
        super(cause);
    }
}

