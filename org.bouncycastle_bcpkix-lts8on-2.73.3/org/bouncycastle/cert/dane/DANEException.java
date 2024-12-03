/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

public class DANEException
extends Exception {
    private Throwable cause;

    public DANEException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public DANEException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

