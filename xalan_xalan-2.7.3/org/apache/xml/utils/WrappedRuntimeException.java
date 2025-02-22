/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

public class WrappedRuntimeException
extends RuntimeException {
    static final long serialVersionUID = 7140414456714658073L;
    private Exception m_exception;

    public WrappedRuntimeException(Exception e) {
        super(e.getMessage());
        this.m_exception = e;
    }

    public WrappedRuntimeException(String msg, Exception e) {
        super(msg);
        this.m_exception = e;
    }

    public Exception getException() {
        return this.m_exception;
    }
}

