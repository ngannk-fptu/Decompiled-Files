/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.io.pem;

import java.io.IOException;

public class PemGenerationException
extends IOException {
    private Throwable cause;

    public PemGenerationException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public PemGenerationException(String message) {
        super(message);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

