/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

public class StreamParsingException
extends Exception {
    Throwable _e;

    public StreamParsingException(String message, Throwable e) {
        super(message);
        this._e = e;
    }

    @Override
    public Throwable getCause() {
        return this._e;
    }
}

