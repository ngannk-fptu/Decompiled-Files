/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

public class CertRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CertRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

