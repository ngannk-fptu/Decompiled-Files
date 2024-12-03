/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.client.proxy;

import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.async.AsyncClientHandler;
import java.util.concurrent.Future;

public interface ViewProxy<T> {
    public T view(Class<T> var1, ClientRequest var2, ClientHandler var3);

    public T view(T var1, ClientRequest var2, ClientHandler var3);

    public Future<T> asyncView(Class<T> var1, ClientRequest var2, AsyncClientHandler var3);

    public Future<T> asyncView(T var1, ClientRequest var2, AsyncClientHandler var3);

    public T view(Class<T> var1, ClientResponse var2);

    public T view(T var1, ClientResponse var2);
}

