/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.path;

public class CertPathValidationException
extends Exception {
    private final Exception cause;

    public CertPathValidationException(String msg) {
        this(msg, null);
    }

    public CertPathValidationException(String msg, Exception cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

