/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client;

import com.sun.jersey.api.client.ClientResponse;

public class UniformInterfaceException
extends RuntimeException {
    private final transient ClientResponse r;

    public UniformInterfaceException(ClientResponse r) {
        this(r, true);
    }

    public UniformInterfaceException(ClientResponse r, boolean bufferResponseEntity) {
        super(r.toString());
        if (bufferResponseEntity) {
            r.bufferEntity();
        }
        this.r = r;
    }

    public UniformInterfaceException(String message, ClientResponse r) {
        this(message, r, true);
    }

    public UniformInterfaceException(String message, ClientResponse r, boolean bufferResponseEntity) {
        super(message);
        if (bufferResponseEntity) {
            r.bufferEntity();
        }
        this.r = r;
    }

    public ClientResponse getResponse() {
        return this.r;
    }
}

