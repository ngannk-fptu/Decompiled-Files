/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.filter;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;

public abstract class ClientFilter
implements ClientHandler {
    private ClientHandler next;

    final void setNext(ClientHandler next) {
        this.next = next;
    }

    public final ClientHandler getNext() {
        return this.next;
    }

    @Override
    public abstract ClientResponse handle(ClientRequest var1) throws ClientHandlerException;
}

