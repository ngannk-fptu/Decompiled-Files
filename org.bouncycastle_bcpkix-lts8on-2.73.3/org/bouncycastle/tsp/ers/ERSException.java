/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

public class ERSException
extends Exception {
    private final Throwable cause;

    public ERSException(String message) {
        this(message, null);
    }

    public ERSException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

