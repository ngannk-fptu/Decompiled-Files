/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.io;

import java.io.IOException;

public class CipherIOException
extends IOException {
    private static final long serialVersionUID = 1L;
    private final Throwable cause;

    public CipherIOException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

