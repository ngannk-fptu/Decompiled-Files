/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

public class RuntimeOperatorException
extends RuntimeException {
    private Throwable cause;

    public RuntimeOperatorException(String msg) {
        super(msg);
    }

    public RuntimeOperatorException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

