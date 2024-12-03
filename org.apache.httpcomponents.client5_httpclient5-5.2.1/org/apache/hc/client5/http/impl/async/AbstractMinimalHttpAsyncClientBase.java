/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.Cancellable
 *  org.apache.hc.core5.concurrent.ComplexFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncRequestProducer
 *  org.apache.hc.core5.http.nio.AsyncResponseConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.reactor.DefaultConnectingIOReactor
 */
package org.apache.hc.client5.http.impl.async;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apache.hc.client5.http.impl.async.AbstractHttpAsyncClientBase;
import org.apache.hc.client5.http.impl.async.AsyncPushConsumerRegistry;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;

abstract class AbstractMinimalHttpAsyncClientBase
extends AbstractHttpAsyncClientBase {
    AbstractMinimalHttpAsyncClientBase(DefaultConnectingIOReactor ioReactor, AsyncPushConsumerRegistry pushConsumerRegistry, ThreadFactory threadFactory) {
        super(ioReactor, pushConsumerRegistry, threadFactory);
    }

    @Override
    protected <T> Future<T> doExecute(HttpHost httpHost, AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context, FutureCallback<T> callback) {
        final ComplexFuture future = new ComplexFuture(callback);
        future.setDependency(this.execute((AsyncClientExchangeHandler)new BasicClientExchangeHandler(requestProducer, responseConsumer, new FutureCallback<T>(){

            public void completed(T result) {
                future.completed(result);
            }

            public void failed(Exception ex) {
                future.failed(ex);
            }

            public void cancelled() {
                future.cancel();
            }
        }), pushHandlerFactory, context));
        return future;
    }

    public final Cancellable execute(AsyncClientExchangeHandler exchangeHandler) {
        return this.execute(exchangeHandler, null, (HttpContext)HttpClientContext.create());
    }

    public abstract Cancellable execute(AsyncClientExchangeHandler var1, HandlerFactory<AsyncPushConsumer> var2, HttpContext var3);
}

