/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

public class CryptoException
extends Exception {
    private Throwable cause;

    public CryptoException() {
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

