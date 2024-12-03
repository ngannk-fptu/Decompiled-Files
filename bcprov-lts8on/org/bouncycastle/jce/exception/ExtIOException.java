/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.exception;

import java.io.IOException;
import org.bouncycastle.jce.exception.ExtException;

public class ExtIOException
extends IOException
implements ExtException {
    private Throwable cause;

    public ExtIOException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

