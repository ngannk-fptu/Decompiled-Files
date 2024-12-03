/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.io;

public class FeedException
extends Exception {
    public FeedException(String msg) {
        super(msg);
    }

    public FeedException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}

