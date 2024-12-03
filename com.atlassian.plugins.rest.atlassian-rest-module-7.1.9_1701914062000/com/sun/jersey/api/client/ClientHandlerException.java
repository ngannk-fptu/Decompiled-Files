/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

public class ClientHandlerException
extends RuntimeException {
    public ClientHandlerException() {
    }

    public ClientHandlerException(String message) {
        super(message);
    }

    public ClientHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientHandlerException(Throwable cause) {
        super(cause);
    }
}

