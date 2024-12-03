/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

public class CMSRuntimeException
extends RuntimeException {
    Exception e;

    public CMSRuntimeException(String name) {
        super(name);
    }

    public CMSRuntimeException(String name, Exception e) {
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

