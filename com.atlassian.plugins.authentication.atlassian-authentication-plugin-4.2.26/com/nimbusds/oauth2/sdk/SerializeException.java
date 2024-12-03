/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

public class SerializeException
extends RuntimeException {
    public SerializeException(String message) {
        this(message, null);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}

