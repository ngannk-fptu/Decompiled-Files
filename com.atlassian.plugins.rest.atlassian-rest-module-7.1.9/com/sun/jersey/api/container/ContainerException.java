/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.container;

public class ContainerException
extends RuntimeException {
    public ContainerException() {
    }

    public ContainerException(String message) {
        super(message);
    }

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerException(Throwable cause) {
        super(cause);
    }
}

