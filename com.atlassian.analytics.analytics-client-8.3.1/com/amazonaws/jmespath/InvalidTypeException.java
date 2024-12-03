/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.jmespath;

public class InvalidTypeException
extends RuntimeException {
    public InvalidTypeException(String message, Throwable t) {
        super(message, t);
    }

    public InvalidTypeException(String message) {
        super(message);
    }

    public InvalidTypeException(Throwable t) {
        super(t);
    }
}

