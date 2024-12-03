/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp;

import java.io.IOException;

public class TSPIOException
extends IOException {
    Throwable underlyingException;

    public TSPIOException(String message) {
        super(message);
    }

    public TSPIOException(String message, Throwable e) {
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

