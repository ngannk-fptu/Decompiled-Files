/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

public class TSPException
extends Exception {
    Throwable underlyingException;

    public TSPException(String message) {
        super(message);
    }

    public TSPException(String message, Throwable e) {
        super(message);
        this.underlyingException = e;
    }

    public Exception getUnderlyingException() {
        return (Exception)this.underlyingException;
    }

    @Override
    public Throwable getCause() {
        return this.underlyingException;
    }
}

