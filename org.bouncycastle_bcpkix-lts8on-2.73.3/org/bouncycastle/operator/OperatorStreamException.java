/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException
extends IOException {
    private Throwable cause;

    public OperatorStreamException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

