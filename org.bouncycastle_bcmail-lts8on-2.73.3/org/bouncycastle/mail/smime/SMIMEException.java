/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mail.smime;

public class SMIMEException
extends Exception {
    Exception e;

    public SMIMEException(String name) {
        super(name);
    }

    public SMIMEException(String name, Exception e) {
        super(name);
        this.e = e;
    }

    public Exception getUnderlyingException() {
        return this.e;
    }

    @Override
    public Throwable getCause() {
        return this.e;
    }
}

