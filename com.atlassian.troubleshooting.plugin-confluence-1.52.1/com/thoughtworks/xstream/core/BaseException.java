/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core;

public abstract class BaseException
extends RuntimeException {
    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

