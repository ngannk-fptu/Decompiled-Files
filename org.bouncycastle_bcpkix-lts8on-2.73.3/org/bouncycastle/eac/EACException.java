/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac;

public class EACException
extends Exception {
    private Throwable cause;

    public EACException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public EACException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

