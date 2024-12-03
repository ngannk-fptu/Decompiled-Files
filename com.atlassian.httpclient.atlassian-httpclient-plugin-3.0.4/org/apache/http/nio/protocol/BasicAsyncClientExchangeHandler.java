/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.protocol.HttpAsyncClientExchangeHandler;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;

public class BasicAsyncClientExchangeHandler<T>
implements HttpAsyncClientExchangeHandler {
    private final HttpAsyncRequestProducer requestProducer;
    private final HttpAsyncResponseConsumer<T> responseConsumer;
    private final BasicFuture<T> future;
    private final HttpContext localContext;
    private final NHttpClientConnection conn;
    private final HttpProcessor httpPocessor;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final AtomicBoolean requestSent;
    private final AtomicBoolean keepAlive;
    private final AtomicBoolean closed;

    public BasicAsyncClientExchangeHandler(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback, HttpContext localContext, NHttpClientConnection conn, HttpProcessor httpPocessor, ConnectionReuseStrategy connReuseStrategy) {
        this.requestProducer = Args.notNull(requestProducer, "Request producer");
        this.responseConsumer = Args.notNull(responseConsumer, "Response consumer");
        this.future = new BasicFuture<T>(callback);
        this.localContext = Args.notNull(localContext, "HTTP context");
        this.conn = Args.notNull(conn, "HTTP connection");
        this.httpPocessor = Args.notNull(httpPocessor, "HTTP processor");
        this.connReuseStrategy = connReuseStrategy != null ? connReuseStrategy : DefaultConnectionReuseStrategy.INSTANCE;
        this.requestSent = new AtomicBoolean(false);
        this.keepAlive = new AtomicBoolean(false);
        this.closed = new AtomicBoolean(false);
    }

    public BasicAsyncClientExchangeHandler(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext localContext, NHttpClientConnection conn, HttpProcessor httpPocessor) {
        this(requestProducer, responseConsumer, null, localContext, conn, httpPocessor, null);
    }

    public Future<T> getFuture() {
        return this.future;
    }

    private void releaseResources() {
        try {
            this.responseConsumer.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            this.requestProducer.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public void close() throws IOException {
        if (this.closed.compareAndSet(false, true)) {
            this.releaseResources();
            if (!this.future.isDone()) {
                this.future.cancel();
            }
        }
    }

    @Override
    public HttpRequest generateRequest() throws IOException, HttpException {
        if (this.isDone()) {
            return null;
        }
        HttpRequest request = this.requestProducer.generateRequest();
        this.localContext.setAttribute("http.request", request);
        this.localContext.setAttribute("http.connection", this.conn);
        this.httpPocessor.process(request, this.localContext);
        return request;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        this.requestProducer.produceContent(encoder, ioControl);
    }

    @Override
    public void requestCompleted() {
        this.requestProducer.requestCompleted(this.localContext);
        this.requestSent.set(true);
    }

    @Override
    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        this.localContext.setAttribute("http.response", response);
        this.httpPocessor.process(response, this.localContext);
        this.responseConsumer.responseReceived(response);
        this.keepAlive.set(this.connReuseStrategy.keepAlive(response, this.localContext));
    }

    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        this.responseConsumer.consumeContent(decoder, ioControl);
    }

    @Override
    public void responseCompleted() throws IOException {
        try {
            if (!this.keepAlive.get()) {
                this.conn.close();
            }
            this.responseConsumer.responseCompleted(this.localContext);
            T result = this.responseConsumer.getResult();
            Exception ex = this.responseConsumer.getException();
            if (result != null) {
                this.future.completed(result);
            } else {
                this.future.failed(ex);
            }
            if (this.closed.compareAndSet(false, true)) {
                this.releaseResources();
            }
        }
        catch (RuntimeException ex) {
            this.failed(ex);
            throw ex;
        }
    }

    @Override
    public void inputTerminated() {
        this.failed(new ConnectionClosedException());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void failed(Exception ex) {
        if (this.closed.compareAndSet(false, true)) {
            try {
                if (!this.requestSent.get()) {
                    this.requestProducer.failed(ex);
                }
                this.responseConsumer.failed(ex);
            }
            finally {
                try {
                    this.future.failed(ex);
                }
                finally {
                    this.releaseResources();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean cancel() {
        if (this.closed.compareAndSet(false, true)) {
            try {
                boolean bl;
                try {
                    bl = this.responseConsumer.cancel();
                    this.future.cancel();
                }
                catch (Throwable throwable) {
                    this.future.cancel();
                    throw throwable;
                }
                return bl;
            }
            finally {
                this.releaseResources();
            }
        }
        return false;
    }

    @Override
    public boolean isDone() {
        return this.responseConsumer.isDone();
    }
}

