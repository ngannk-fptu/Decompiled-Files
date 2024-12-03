/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

public class PKCSException
extends Exception {
    private Throwable cause;

    public PKCSException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public PKCSException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

