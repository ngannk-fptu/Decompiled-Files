/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

public class SerializeException
extends RuntimeException {
    private static final long serialVersionUID = -1441994426154259304L;

    public SerializeException(String message) {
        this(message, null);
    }

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }
}

