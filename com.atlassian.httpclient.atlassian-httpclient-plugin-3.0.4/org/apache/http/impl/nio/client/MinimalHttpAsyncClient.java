/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClientBase;
import org.apache.http.impl.nio.client.FutureWrapper;
import org.apache.http.impl.nio.client.MinimalClientExchangeHandlerImpl;
import org.apache.http.impl.nio.client.PipeliningClientExchangeHandlerImpl;
import org.apache.http.nio.NHttpClientEventHandler;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

class MinimalHttpAsyncClient
extends CloseableHttpAsyncClientBase {
    private final Log log = LogFactory.getLog(this.getClass());
    private final NHttpClientConnectionManager connmgr;
    private final HttpProcessor httpProcessor;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final ConnectionKeepAliveStrategy keepaliveStrategy;

    public MinimalHttpAsyncClient(NHttpClientConnectionManager connmgr, ThreadFactory threadFactory, NHttpClientEventHandler eventHandler, HttpProcessor httpProcessor, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy) {
        super(connmgr, threadFactory, eventHandler);
        this.connmgr = connmgr;
        this.httpProcessor = httpProcessor;
        this.connReuseStrategy = connReuseStrategy;
        this.keepaliveStrategy = keepaliveStrategy;
    }

    public MinimalHttpAsyncClient(NHttpClientConnectionManager connmgr, HttpProcessor httpProcessor) {
        this(connmgr, Executors.defaultThreadFactory(), new HttpAsyncRequestExecutor(), httpProcessor, DefaultConnectionReuseStrategy.INSTANCE, DefaultConnectionKeepAliveStrategy.INSTANCE);
    }

    @Override
    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        BasicFuture<T> future = new BasicFuture<T>(callback);
        HttpClientContext localcontext = HttpClientContext.adapt(context != null ? context : new BasicHttpContext());
        MinimalClientExchangeHandlerImpl<T> handler = new MinimalClientExchangeHandlerImpl<T>(this.log, requestProducer, responseConsumer, localcontext, future, this.connmgr, this.httpProcessor, this.connReuseStrategy, this.keepaliveStrategy);
        this.execute(handler);
        return new FutureWrapper<T>(future, handler);
    }

    @Override
    public <T> Future<List<T>> execute(HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, HttpContext context, FutureCallback<List<T>> callback) {
        BasicFuture<List<T>> future = new BasicFuture<List<T>>(callback);
        HttpClientContext localcontext = HttpClientContext.adapt(context != null ? context : new BasicHttpContext());
        PipeliningClientExchangeHandlerImpl<T> handler = new PipeliningClientExchangeHandlerImpl<T>(this.log, target, requestProducers, responseConsumers, localcontext, future, this.connmgr, this.httpProcessor, this.connReuseStrategy, this.keepaliveStrategy);
        this.execute(handler);
        return new FutureWrapper<List<T>>(future, handler);
    }
}

