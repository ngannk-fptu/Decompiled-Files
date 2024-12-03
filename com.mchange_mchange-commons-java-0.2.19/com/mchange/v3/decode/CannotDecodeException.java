/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.decode;

public class CannotDecodeException
extends Exception {
    public CannotDecodeException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public CannotDecodeException(String string) {
        super(string);
    }

    public CannotDecodeException(Throwable throwable) {
        super(throwable);
    }

    public CannotDecodeException() {
    }
}

