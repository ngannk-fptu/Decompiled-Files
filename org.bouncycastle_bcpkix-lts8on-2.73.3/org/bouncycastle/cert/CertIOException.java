/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

import java.io.IOException;

public class CertIOException
extends IOException {
    private Throwable cause;

    public CertIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public CertIOException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

