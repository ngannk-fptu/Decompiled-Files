/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.ocsp;

public class OCSPException
extends Exception {
    private Throwable cause;

    public OCSPException(String name) {
        super(name);
    }

    public OCSPException(String name, Throwable cause) {
        super(name);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

