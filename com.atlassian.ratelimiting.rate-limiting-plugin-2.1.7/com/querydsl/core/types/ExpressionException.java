/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

public class ExpressionException
extends RuntimeException {
    private static final long serialVersionUID = 6031724386976562965L;

    public ExpressionException(String msg) {
        super(msg);
    }

    public ExpressionException(String msg, Throwable t) {
        super(msg, t);
    }

    public ExpressionException(Throwable t) {
        super(t);
    }
}

