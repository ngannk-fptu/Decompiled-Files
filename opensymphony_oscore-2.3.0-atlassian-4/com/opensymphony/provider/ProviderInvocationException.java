/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

public class ProviderInvocationException
extends Exception {
    private Throwable cause;

    public ProviderInvocationException(Throwable cause) {
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

