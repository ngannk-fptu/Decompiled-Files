/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.Closeable;
import java.net.URI;
import java.util.concurrent.Future;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.SAFE)
public abstract class CloseableHttpAsyncClient
implements HttpAsyncClient,
Closeable {
    public abstract boolean isRunning();

    public abstract void start();

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, (HttpContext)HttpClientContext.create(), callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        return this.execute(HttpAsyncMethods.create(target, request), HttpAsyncMethods.createConsumer(), context, callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpHost target, HttpRequest request, FutureCallback<HttpResponse> callback) {
        return this.execute(target, request, (HttpContext)HttpClientContext.create(), callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpUriRequest request, FutureCallback<HttpResponse> callback) {
        return this.execute(request, HttpClientContext.create(), callback);
    }

    @Override
    public Future<HttpResponse> execute(HttpUriRequest request, HttpContext context, FutureCallback<HttpResponse> callback) {
        HttpHost target;
        try {
            target = this.determineTarget(request);
        }
        catch (ClientProtocolException ex) {
            BasicFuture<HttpResponse> future = new BasicFuture<HttpResponse>(callback);
            future.failed(ex);
            return future;
        }
        return this.execute(target, request, context, callback);
    }

    private HttpHost determineTarget(HttpUriRequest request) throws ClientProtocolException {
        Args.notNull(request, "HTTP request");
        HttpHost target = null;
        URI requestURI = request.getURI();
        if (requestURI.isAbsolute() && (target = URIUtils.extractHost(requestURI)) == null) {
            throw new ClientProtocolException("URI does not specify a valid host name: " + requestURI);
        }
        return target;
    }
}

