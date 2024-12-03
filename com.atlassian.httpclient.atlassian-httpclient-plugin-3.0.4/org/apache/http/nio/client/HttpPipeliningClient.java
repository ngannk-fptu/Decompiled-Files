/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.client;

import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public interface HttpPipeliningClient
extends HttpAsyncClient {
    public <T> Future<List<T>> execute(HttpHost var1, List<? extends HttpAsyncRequestProducer> var2, List<? extends HttpAsyncResponseConsumer<T>> var3, HttpContext var4, FutureCallback<List<T>> var5);

    public <T> Future<List<T>> execute(HttpHost var1, List<? extends HttpAsyncRequestProducer> var2, List<? extends HttpAsyncResponseConsumer<T>> var3, FutureCallback<List<T>> var4);

    public Future<List<HttpResponse>> execute(HttpHost var1, List<HttpRequest> var2, HttpContext var3, FutureCallback<List<HttpResponse>> var4);

    public Future<List<HttpResponse>> execute(HttpHost var1, List<HttpRequest> var2, FutureCallback<List<HttpResponse>> var3);
}

