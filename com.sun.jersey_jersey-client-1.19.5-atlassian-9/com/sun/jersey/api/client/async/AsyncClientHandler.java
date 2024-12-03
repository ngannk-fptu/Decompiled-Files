/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.api.client.async;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.FutureListener;
import java.util.concurrent.Future;

public interface AsyncClientHandler {
    public Future<ClientResponse> handle(ClientRequest var1, FutureListener<ClientResponse> var2);
}

