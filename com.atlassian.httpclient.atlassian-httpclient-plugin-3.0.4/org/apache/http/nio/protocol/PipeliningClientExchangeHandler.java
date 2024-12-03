/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
import org.apache.http.nio.protocol.Pipelined;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Pipelined
public class PipeliningClientExchangeHandler<T>
implements HttpAsyncClientExchangeHandler {
    private final Queue<HttpAsyncRequestProducer> requestProducerQueue;
    private final Queue<HttpAsyncResponseConsumer<T>> responseConsumerQueue;
    private final Queue<HttpRequest> requestQueue;
    private final Queue<T> resultQueue;
    private final BasicFuture<List<T>> future;
    private final HttpContext localContext;
    private final NHttpClientConnection conn;
    private final HttpProcessor httpPocessor;
    private final ConnectionReuseStrategy connReuseStrategy;
    private final AtomicReference<HttpAsyncRequestProducer> requestProducerRef;
    private final AtomicReference<HttpAsyncResponseConsumer<T>> responseConsumerRef;
    private final AtomicBoolean keepAlive;
    private final AtomicBoolean closed;

    public PipeliningClientExchangeHandler(List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, FutureCallback<List<T>> callback, HttpContext localContext, NHttpClientConnection conn, HttpProcessor httpPocessor, ConnectionReuseStrategy connReuseStrategy) {
        Args.notEmpty(requestProducers, "Request producer list");
        Args.notEmpty(responseConsumers, "Response consumer list");
        Args.check(requestProducers.size() == responseConsumers.size(), "Number of request producers does not match that of response consumers");
        this.requestProducerQueue = new ConcurrentLinkedQueue<HttpAsyncRequestProducer>(requestProducers);
        this.responseConsumerQueue = new ConcurrentLinkedQueue<HttpAsyncResponseConsumer<T>>(responseConsumers);
        this.requestQueue = new ConcurrentLinkedQueue<HttpRequest>();
        this.resultQueue = new ConcurrentLinkedQueue<T>();
        this.future = new BasicFuture<List<List<T>>>(callback);
        this.localContext = Args.notNull(localContext, "HTTP context");
        this.conn = Args.notNull(conn, "HTTP connection");
        this.httpPocessor = Args.notNull(httpPocessor, "HTTP processor");
        this.connReuseStrategy = connReuseStrategy != null ? connReuseStrategy : DefaultConnectionReuseStrategy.INSTANCE;
        this.localContext.setAttribute("http.connection", this.conn);
        this.requestProducerRef = new AtomicReference<Object>(null);
        this.responseConsumerRef = new AtomicReference<Object>(null);
        this.keepAlive = new AtomicBoolean(false);
        this.closed = new AtomicBoolean(false);
    }

    public PipeliningClientExchangeHandler(List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, HttpContext localContext, NHttpClientConnection conn, HttpProcessor httpPocessor) {
        this(requestProducers, responseConsumers, null, localContext, conn, httpPocessor, null);
    }

    public Future<List<T>> getFuture() {
        return this.future;
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private void releaseResources() {
        PipeliningClientExchangeHandler.closeQuietly(this.requestProducerRef.getAndSet(null));
        PipeliningClientExchangeHandler.closeQuietly(this.responseConsumerRef.getAndSet(null));
        while (!this.requestProducerQueue.isEmpty()) {
            PipeliningClientExchangeHandler.closeQuietly(this.requestProducerQueue.remove());
        }
        while (!this.responseConsumerQueue.isEmpty()) {
            PipeliningClientExchangeHandler.closeQuietly(this.responseConsumerQueue.remove());
        }
        this.requestQueue.clear();
        this.resultQueue.clear();
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
        Asserts.check(this.requestProducerRef.get() == null, "Inconsistent state: request producer is not null");
        HttpAsyncRequestProducer requestProducer = this.requestProducerQueue.poll();
        if (requestProducer == null) {
            return null;
        }
        this.requestProducerRef.set(requestProducer);
        HttpRequest request = requestProducer.generateRequest();
        this.httpPocessor.process(request, this.localContext);
        this.requestQueue.add(request);
        return request;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        HttpAsyncRequestProducer requestProducer = this.requestProducerRef.get();
        Asserts.check(requestProducer != null, "Inconsistent state: request producer is null");
        requestProducer.produceContent(encoder, ioControl);
    }

    @Override
    public void requestCompleted() {
        HttpAsyncRequestProducer requestProducer = this.requestProducerRef.getAndSet(null);
        Asserts.check(requestProducer != null, "Inconsistent state: request producer is null");
        requestProducer.requestCompleted(this.localContext);
    }

    @Override
    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        Asserts.check(this.responseConsumerRef.get() == null, "Inconsistent state: response consumer is not null");
        HttpAsyncResponseConsumer<T> responseConsumer = this.responseConsumerQueue.poll();
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer queue is empty");
        this.responseConsumerRef.set(responseConsumer);
        HttpRequest request = this.requestQueue.poll();
        Asserts.check(request != null, "Inconsistent state: request queue is empty");
        this.localContext.setAttribute("http.request", request);
        this.localContext.setAttribute("http.response", response);
        this.httpPocessor.process(response, this.localContext);
        responseConsumer.responseReceived(response);
        this.keepAlive.set(this.connReuseStrategy.keepAlive(response, this.localContext));
    }

    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        HttpAsyncResponseConsumer<T> responseConsumer = this.responseConsumerRef.get();
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer is null");
        responseConsumer.consumeContent(decoder, ioControl);
    }

    @Override
    public void responseCompleted() throws IOException {
        HttpAsyncResponseConsumer responseConsumer = this.responseConsumerRef.getAndSet(null);
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer is null");
        try {
            if (!this.keepAlive.get()) {
                this.conn.close();
            }
            responseConsumer.responseCompleted(this.localContext);
            Object result = responseConsumer.getResult();
            Exception ex = responseConsumer.getException();
            if (result != null) {
                this.resultQueue.add(result);
            } else {
                this.future.failed(ex);
                this.conn.shutdown();
            }
            if (!this.conn.isOpen() && this.closed.compareAndSet(false, true)) {
                this.releaseResources();
            }
            if (!this.future.isDone() && this.responseConsumerQueue.isEmpty()) {
                this.future.completed(new ArrayList<T>(this.resultQueue));
                this.resultQueue.clear();
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
                HttpAsyncResponseConsumer<T> responseConsumer;
                HttpAsyncRequestProducer requestProducer = this.requestProducerRef.get();
                if (requestProducer != null) {
                    requestProducer.failed(ex);
                }
                if ((responseConsumer = this.responseConsumerRef.get()) != null) {
                    responseConsumer.failed(ex);
                }
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
                    HttpAsyncResponseConsumer<T> responseConsumer = this.responseConsumerRef.get();
                    bl = responseConsumer != null && responseConsumer.cancel();
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
        return this.future.isDone();
    }
}

