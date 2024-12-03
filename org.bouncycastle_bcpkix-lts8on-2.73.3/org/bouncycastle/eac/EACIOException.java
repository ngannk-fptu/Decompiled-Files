/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac;

import java.io.IOException;

public class EACIOException
extends IOException {
    private Throwable cause;

    public EACIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public EACIOException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

