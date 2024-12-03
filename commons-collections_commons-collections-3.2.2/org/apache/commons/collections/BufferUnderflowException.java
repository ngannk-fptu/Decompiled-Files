/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.NoSuchElementException;

public class BufferUnderflowException
extends NoSuchElementException {
    private final Throwable throwable;

    public BufferUnderflowException() {
        this.throwable = null;
    }

    public BufferUnderflowException(String message) {
        this(message, null);
    }

    public BufferUnderflowException(String message, Throwable exception) {
        super(message);
        this.throwable = exception;
    }

    public final Throwable getCause() {
        return this.throwable;
    }
}

