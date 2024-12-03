/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.dvcs;

public class DVCSException
extends Exception {
    private static final long serialVersionUID = 389345256020131488L;
    private Throwable cause;

    public DVCSException(String message) {
        super(message);
    }

    public DVCSException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

