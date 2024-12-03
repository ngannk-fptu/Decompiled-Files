/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

public class CertException
extends Exception {
    private Throwable cause;

    public CertException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

