/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.service;

public class ServiceConfigurationError
extends Error {
    ServiceConfigurationError(Throwable throwable) {
        super(throwable);
    }

    ServiceConfigurationError(String string) {
        super(string);
    }

    ServiceConfigurationError(String string, Throwable throwable) {
        super(string, throwable);
    }
}

