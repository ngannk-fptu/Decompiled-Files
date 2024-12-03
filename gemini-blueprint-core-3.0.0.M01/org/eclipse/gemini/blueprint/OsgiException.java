/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint;

public class OsgiException
extends RuntimeException {
    private static final long serialVersionUID = -2484573525557843394L;

    public OsgiException() {
    }

    public OsgiException(String message, Throwable cause) {
        super(message, cause);
    }

    public OsgiException(String message) {
        super(message);
    }

    public OsgiException(Throwable cause) {
        super(cause);
    }
}

