/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.protocol.BasicAsyncClientExchangeHandler;
import org.apache.http.nio.protocol.HttpAsyncClientExchangeHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.PipeliningClientExchangeHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.ConnPool;
import org.apache.http.pool.PoolEntry;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpAsyncRequester {
    private final HttpProcessor httpprocessor;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final ExceptionLogger exceptionLogger;

    @Deprecated
    public HttpAsyncRequester(HttpProcessor httpprocessor, ConnectionReuseStrategy reuseStrategy, HttpParams params) {
        this(httpprocessor, reuseStrategy);
    }

    public HttpAsyncRequester(HttpProcessor httpprocessor, ConnectionReuseStrategy connReuseStrategy, ExceptionLogger exceptionLogger) {
        this.httpprocessor = Args.notNull(httpprocessor, "HTTP processor");
        this.connReuseStrategy = connReuseStrategy != null ? connReuseStrategy : DefaultConnectionReuseStrategy.INSTANCE;
        this.exceptionLogger = exceptionLogger != null ? exceptionLogger : ExceptionLogger.NO_OP;
    }

    public HttpAsyncRequester(HttpProcessor httpprocessor, ConnectionReuseStrategy connReuseStrategy) {
        this(httpprocessor, connReuseStrategy, (ExceptionLogger)null);
    }

    public HttpAsyncRequester(HttpProcessor httpprocessor) {
        this(httpprocessor, null);
    }

    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, NHttpClientConnection conn, HttpContext context, FutureCallback<T> callback) {
        Args.notNull(requestProducer, "HTTP request producer");
        Args.notNull(responseConsumer, "HTTP response consumer");
        Args.notNull(conn, "HTTP connection");
        Args.notNull(context, "HTTP context");
        BasicAsyncClientExchangeHandler<T> handler = new BasicAsyncClientExchangeHandler<T>(requestProducer, responseConsumer, callback, context, conn, this.httpprocessor, this.connReuseStrategy);
        this.initExecution(handler, conn);
        return handler.getFuture();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initExecution(HttpAsyncClientExchangeHandler handler, NHttpClientConnection conn) {
        HttpContext context;
        HttpContext httpContext = context = conn.getContext();
        synchronized (httpContext) {
            context.setAttribute("http.nio.exchange-handler", handler);
            if (!conn.isOpen()) {
                handler.failed(new ConnectionClosedException());
            } else {
                conn.requestOutput();
            }
        }
        if (handler.isDone()) {
            try {
                handler.close();
            }
            catch (IOException ex) {
                this.log(ex);
            }
        }
    }

    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, NHttpClientConnection conn, HttpContext context) {
        return this.execute(requestProducer, responseConsumer, conn, context, null);
    }

    public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, NHttpClientConnection conn) {
        return this.execute(requestProducer, responseConsumer, conn, (HttpContext)new BasicHttpContext());
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, ConnPool<HttpHost, E> connPool, HttpContext context, FutureCallback<T> callback) {
        Args.notNull(requestProducer, "HTTP request producer");
        Args.notNull(responseConsumer, "HTTP response consumer");
        Args.notNull(connPool, "HTTP connection pool");
        Args.notNull(context, "HTTP context");
        BasicFuture<T> future = new BasicFuture<T>(callback);
        HttpHost target = requestProducer.getTarget();
        connPool.lease(target, null, new ConnRequestCallback<T, E>(future, requestProducer, responseConsumer, connPool, context));
        return future;
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<List<T>> executePipelined(HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, ConnPool<HttpHost, E> connPool, HttpContext context, FutureCallback<List<T>> callback) {
        Args.notNull(target, "HTTP target");
        Args.notEmpty(requestProducers, "Request producer list");
        Args.notEmpty(responseConsumers, "Response consumer list");
        Args.notNull(connPool, "HTTP connection pool");
        Args.notNull(context, "HTTP context");
        BasicFuture<List<T>> future = new BasicFuture<List<T>>(callback);
        connPool.lease(target, null, new ConnPipelinedRequestCallback<T, E>(future, requestProducers, responseConsumers, connPool, context));
        return future;
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, E poolEntry, ConnPool<HttpHost, E> connPool, HttpContext context, FutureCallback<T> callback) {
        Args.notNull(requestProducer, "HTTP request producer");
        Args.notNull(responseConsumer, "HTTP response consumer");
        Args.notNull(connPool, "HTTP connection pool");
        Args.notNull(poolEntry, "Pool entry");
        Args.notNull(context, "HTTP context");
        BasicFuture<T> future = new BasicFuture<T>(callback);
        NHttpClientConnection conn = poolEntry.getConnection();
        BasicAsyncClientExchangeHandler<T> handler = new BasicAsyncClientExchangeHandler<T>(requestProducer, responseConsumer, new RequestExecutionCallback(this, future, poolEntry, connPool), context, conn, this.httpprocessor, this.connReuseStrategy);
        this.initExecution(handler, conn);
        return future;
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<List<T>> executePipelined(List<HttpAsyncRequestProducer> requestProducers, List<HttpAsyncResponseConsumer<T>> responseConsumers, E poolEntry, ConnPool<HttpHost, E> connPool, HttpContext context, FutureCallback<List<T>> callback) {
        Args.notEmpty(requestProducers, "Request producer list");
        Args.notEmpty(responseConsumers, "Response consumer list");
        Args.notNull(connPool, "HTTP connection pool");
        Args.notNull(poolEntry, "Pool entry");
        Args.notNull(context, "HTTP context");
        BasicFuture<List<T>> future = new BasicFuture<List<T>>(callback);
        NHttpClientConnection conn = poolEntry.getConnection();
        PipeliningClientExchangeHandler<T> handler = new PipeliningClientExchangeHandler<T>(requestProducers, responseConsumers, new RequestExecutionCallback(this, future, poolEntry, connPool), context, conn, this.httpprocessor, this.connReuseStrategy);
        this.initExecution(handler, conn);
        return future;
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, ConnPool<HttpHost, E> connPool, HttpContext context) {
        return this.execute(requestProducer, responseConsumer, connPool, context, null);
    }

    public <T, E extends PoolEntry<HttpHost, NHttpClientConnection>> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, ConnPool<HttpHost, E> connPool) {
        return this.execute(requestProducer, responseConsumer, connPool, (HttpContext)new BasicHttpContext());
    }

    protected void log(Exception ex) {
        this.exceptionLogger.log(ex);
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        }
        catch (IOException ex) {
            this.log(ex);
        }
    }

    static class RequestExecutionCallback<T, E extends PoolEntry<HttpHost, NHttpClientConnection>>
    implements FutureCallback<T> {
        private final BasicFuture<T> future;
        private final E poolEntry;
        private final ConnPool<HttpHost, E> connPool;
        final /* synthetic */ HttpAsyncRequester this$0;

        RequestExecutionCallback(BasicFuture<T> future, E poolEntry, ConnPool<HttpHost, E> connPool) {
            this.this$0 = var1_1;
            this.future = future;
            this.poolEntry = poolEntry;
            this.connPool = connPool;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void completed(T result) {
            try {
                this.connPool.release(this.poolEntry, true);
            }
            finally {
                this.future.completed(result);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void failed(Exception ex) {
            try {
                this.connPool.release(this.poolEntry, false);
            }
            finally {
                this.future.failed(ex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void cancelled() {
            try {
                this.connPool.release(this.poolEntry, false);
            }
            finally {
                this.future.cancel(true);
            }
        }
    }

    class ConnPipelinedRequestCallback<T, E extends PoolEntry<HttpHost, NHttpClientConnection>>
    implements FutureCallback<E> {
        private final BasicFuture<List<T>> requestFuture;
        private final List<? extends HttpAsyncRequestProducer> requestProducers;
        private final List<? extends HttpAsyncResponseConsumer<T>> responseConsumers;
        private final ConnPool<HttpHost, E> connPool;
        private final HttpContext context;

        ConnPipelinedRequestCallback(BasicFuture<List<T>> requestFuture, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, ConnPool<HttpHost, E> connPool, HttpContext context) {
            this.requestFuture = requestFuture;
            this.requestProducers = requestProducers;
            this.responseConsumers = responseConsumers;
            this.connPool = connPool;
            this.context = context;
        }

        @Override
        public void completed(E result) {
            if (this.requestFuture.isDone()) {
                this.connPool.release(result, true);
                return;
            }
            NHttpClientConnection conn = (NHttpClientConnection)((PoolEntry)result).getConnection();
            PipeliningClientExchangeHandler handler = new PipeliningClientExchangeHandler(this.requestProducers, this.responseConsumers, new RequestExecutionCallback(HttpAsyncRequester.this, this.requestFuture, result, this.connPool), this.context, conn, HttpAsyncRequester.this.httpprocessor, HttpAsyncRequester.this.connReuseStrategy);
            HttpAsyncRequester.this.initExecution(handler, conn);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void failed(Exception ex) {
            try {
                try {
                    for (HttpAsyncResponseConsumer<T> responseConsumer : this.responseConsumers) {
                        responseConsumer.failed(ex);
                    }
                }
                finally {
                    this.releaseResources();
                }
            }
            finally {
                this.requestFuture.failed(ex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void cancelled() {
            try {
                try {
                    for (HttpAsyncResponseConsumer<T> responseConsumer : this.responseConsumers) {
                        responseConsumer.cancel();
                    }
                }
                finally {
                    this.releaseResources();
                }
            }
            finally {
                this.requestFuture.cancel(true);
            }
        }

        public void releaseResources() {
            for (HttpAsyncRequestProducer httpAsyncRequestProducer : this.requestProducers) {
                HttpAsyncRequester.this.close(httpAsyncRequestProducer);
            }
            for (HttpAsyncResponseConsumer httpAsyncResponseConsumer : this.responseConsumers) {
                HttpAsyncRequester.this.close(httpAsyncResponseConsumer);
            }
        }
    }

    class ConnRequestCallback<T, E extends PoolEntry<HttpHost, NHttpClientConnection>>
    implements FutureCallback<E> {
        private final BasicFuture<T> requestFuture;
        private final HttpAsyncRequestProducer requestProducer;
        private final HttpAsyncResponseConsumer<T> responseConsumer;
        private final ConnPool<HttpHost, E> connPool;
        private final HttpContext context;

        ConnRequestCallback(BasicFuture<T> requestFuture, HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, ConnPool<HttpHost, E> connPool, HttpContext context) {
            this.requestFuture = requestFuture;
            this.requestProducer = requestProducer;
            this.responseConsumer = responseConsumer;
            this.connPool = connPool;
            this.context = context;
        }

        @Override
        public void completed(E result) {
            if (this.requestFuture.isDone()) {
                this.connPool.release(result, true);
                return;
            }
            NHttpClientConnection conn = (NHttpClientConnection)((PoolEntry)result).getConnection();
            BasicAsyncClientExchangeHandler<T> handler = new BasicAsyncClientExchangeHandler<T>(this.requestProducer, this.responseConsumer, new RequestExecutionCallback(HttpAsyncRequester.this, this.requestFuture, result, this.connPool), this.context, conn, HttpAsyncRequester.this.httpprocessor, HttpAsyncRequester.this.connReuseStrategy);
            HttpAsyncRequester.this.initExecution(handler, conn);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void failed(Exception ex) {
            try {
                try {
                    this.responseConsumer.failed(ex);
                }
                finally {
                    this.releaseResources();
                }
            }
            finally {
                this.requestFuture.failed(ex);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void cancelled() {
            try {
                try {
                    this.responseConsumer.cancel();
                }
                finally {
                    this.releaseResources();
                }
            }
            finally {
                this.requestFuture.cancel(true);
            }
        }

        public void releaseResources() {
            HttpAsyncRequester.this.close(this.requestProducer);
            HttpAsyncRequester.this.close(this.responseConsumer);
        }
    }
}

