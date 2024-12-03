/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

public class CMSException
extends Exception {
    Exception e;

    public CMSException(String msg) {
        super(msg);
    }

    public CMSException(String msg, Exception e) {
        super(msg);
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

