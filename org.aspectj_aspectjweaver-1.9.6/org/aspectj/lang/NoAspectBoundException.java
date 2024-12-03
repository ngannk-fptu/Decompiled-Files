/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang;

public class NoAspectBoundException
extends RuntimeException {
    Throwable cause;

    public NoAspectBoundException(String aspectName, Throwable inner) {
        super(inner == null ? aspectName : "Exception while initializing " + aspectName + ": " + inner);
        this.cause = inner;
    }

    public NoAspectBoundException() {
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

