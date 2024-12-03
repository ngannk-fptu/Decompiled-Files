/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

public class CMPException
extends Exception {
    private Throwable cause;

    public CMPException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CMPException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

