/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import java.io.IOException;

public class PKCSIOException
extends IOException {
    private Throwable cause;

    public PKCSIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public PKCSIOException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

