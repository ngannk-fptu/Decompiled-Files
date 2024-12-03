/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

public class CRMFRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CRMFRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

