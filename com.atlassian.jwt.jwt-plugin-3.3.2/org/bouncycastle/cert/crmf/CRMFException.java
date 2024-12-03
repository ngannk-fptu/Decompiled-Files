/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

public class CRMFException
extends Exception {
    private Throwable cause;

    public CRMFException(String string) {
        this(string, null);
    }

    public CRMFException(String string, Throwable throwable) {
        super(string);
        this.cause = throwable;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

