/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.HttpPipeliningClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public abstract class CloseableHttpPipeliningClient
extends CloseableHttpAsyncClient
implements HttpPipeliningClient {
    @Override
    public <T> Future<List<T>> execute(HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, FutureCallback<List<T>> callback) {
        return this.execute(target, requestProducers, responseConsumers, HttpClientContext.create(), callback);
    }

    @Override
    public Future<List<HttpResponse>> execute(HttpHost target, List<HttpRequest> requests, FutureCallback<List<HttpResponse>> callback) {
        return this.execute(target, requests, (HttpContext)HttpClientContext.create(), callback);
    }

    @Override
    public Future<List<HttpResponse>> execute(HttpHost target, List<HttpRequest> requests, HttpContext context, FutureCallback<List<HttpResponse>> callback) {
        Args.notEmpty(requests, "HTTP request list");
        ArrayList<HttpAsyncRequestProducer> requestProducers = new ArrayList<HttpAsyncRequestProducer>(requests.size());
        ArrayList<HttpAsyncResponseConsumer<HttpResponse>> responseConsumers = new ArrayList<HttpAsyncResponseConsumer<HttpResponse>>(requests.size());
        for (int i = 0; i < requests.size(); ++i) {
            HttpRequest request = requests.get(i);
            requestProducers.add(HttpAsyncMethods.create(target, request));
            responseConsumers.add(HttpAsyncMethods.createConsumer());
        }
        return this.execute(target, requestProducers, responseConsumers, context, callback);
    }
}

