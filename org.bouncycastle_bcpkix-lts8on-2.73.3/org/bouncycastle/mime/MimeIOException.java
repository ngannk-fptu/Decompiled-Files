/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;

public class MimeIOException
extends IOException {
    private Throwable cause;

    public MimeIOException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public MimeIOException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

