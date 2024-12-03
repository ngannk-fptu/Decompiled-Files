/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.io;

public class FeedException
extends Exception {
    private static final long serialVersionUID = 1L;

    public FeedException(String msg) {
        super(msg);
    }

    public FeedException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

