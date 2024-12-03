/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.johnson;

public class JohnsonException
extends RuntimeException {
    public JohnsonException(String s) {
        super(s);
    }

    public JohnsonException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public JohnsonException(Throwable throwable) {
        super(throwable);
    }
}

