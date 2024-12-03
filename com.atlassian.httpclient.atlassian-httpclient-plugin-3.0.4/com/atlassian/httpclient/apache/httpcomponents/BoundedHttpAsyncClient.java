/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 */
package com.atlassian.httpclient.apache.httpcomponents;

import com.atlassian.httpclient.apache.httpcomponents.BoundedAsyncResponseConsumer;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.util.concurrent.Future;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;

public class BoundedHttpAsyncClient
extends CloseableHttpAsyncClient {
    private final CloseableHttpAsyncClient delegate;
    private final int maxEntitySize;

    public BoundedHttpAsyncClient(CloseableHttpAsyncClient delegate, int maxEntitySize) {
        this.delegate = delegate;
        this.maxEntitySize = maxEntitySize;
    }

    @Override
    public boolean isRunning() {
        return this.delegate.isRunning();
    }

    @Override
    public void start() {
        this.delegate.start();
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        return this.delegate.execute(requestProducer, responseConsumer, context, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        BoundedAsyncResponseConsumer consumer = new BoundedAsyncResponseConsumer(Ints.saturatedCast((long)this.maxEntitySize));
        return this.delegate.execute(HttpAsyncMethods.create(target, request), consumer, context, callback);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
}

