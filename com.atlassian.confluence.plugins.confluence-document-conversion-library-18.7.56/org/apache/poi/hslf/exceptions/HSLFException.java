/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.exceptions;

public final class HSLFException
extends RuntimeException {
    public HSLFException() {
    }

    public HSLFException(String message) {
        super(message);
    }

    public HSLFException(String message, Throwable cause) {
        super(message, cause);
    }

    public HSLFException(Throwable cause) {
        super(cause);
    }
}

