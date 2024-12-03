/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

public class CMPRuntimeException
extends RuntimeException {
    private Throwable cause;

    public CMPRuntimeException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

