/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

public class ProviderConfigurationException
extends Exception {
    private Throwable cause;

    public ProviderConfigurationException() {
    }

    public ProviderConfigurationException(String msg) {
        super(msg);
    }

    public ProviderConfigurationException(Throwable cause) {
        this.cause = cause;
    }

    public ProviderConfigurationException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

