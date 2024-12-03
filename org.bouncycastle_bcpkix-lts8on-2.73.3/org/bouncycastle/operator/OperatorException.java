/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

public class OperatorException
extends Exception {
    private Throwable cause;

    public OperatorException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    public OperatorException(String msg) {
        super(msg);
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

