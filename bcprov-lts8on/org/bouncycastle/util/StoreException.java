/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class StoreException
extends RuntimeException {
    private Throwable _e;

    public StoreException(String msg, Throwable cause) {
        super(msg);
        this._e = cause;
    }

    @Override
    public Throwable getCause() {
        return this._e;
    }
}

