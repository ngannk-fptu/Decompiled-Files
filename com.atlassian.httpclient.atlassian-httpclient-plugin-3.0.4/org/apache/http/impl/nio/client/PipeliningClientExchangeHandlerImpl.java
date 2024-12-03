/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.logging.Log;
import org.apache.http.ConnectionClosedException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.nio.client.AbstractClientExchangeHandler;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.nio.protocol.Pipelined;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Pipelined
class PipeliningClientExchangeHandlerImpl<T>
extends AbstractClientExchangeHandler {
    private final HttpHost target;
    private final Queue<HttpAsyncRequestProducer> requestProducerQueue;
    private final Queue<HttpAsyncResponseConsumer<T>> responseConsumerQueue;
    private final Queue<HttpRequest> requestQueue;
    private final Queue<T> resultQueue;
    private final HttpClientContext localContext;
    private final BasicFuture<List<T>> resultFuture;
    private final HttpProcessor httpProcessor;
    private final AtomicReference<HttpAsyncRequestProducer> requestProducerRef;
    private final AtomicReference<HttpAsyncResponseConsumer<T>> responseConsumerRef;

    public PipeliningClientExchangeHandlerImpl(Log log, HttpHost target, List<? extends HttpAsyncRequestProducer> requestProducers, List<? extends HttpAsyncResponseConsumer<T>> responseConsumers, HttpClientContext localContext, BasicFuture<List<T>> resultFuture, NHttpClientConnectionManager connmgr, HttpProcessor httpProcessor, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy) {
        super(log, localContext, connmgr, connReuseStrategy, keepaliveStrategy);
        Args.notNull(target, "HTTP target");
        Args.notEmpty(requestProducers, "Request producer list");
        Args.notEmpty(responseConsumers, "Response consumer list");
        Args.check(requestProducers.size() == responseConsumers.size(), "Number of request producers does not match that of response consumers");
        this.target = target;
        this.requestProducerQueue = new ConcurrentLinkedQueue<HttpAsyncRequestProducer>(requestProducers);
        this.responseConsumerQueue = new ConcurrentLinkedQueue<HttpAsyncResponseConsumer<T>>(responseConsumers);
        this.requestQueue = new ConcurrentLinkedQueue<HttpRequest>();
        this.resultQueue = new ConcurrentLinkedQueue<T>();
        this.localContext = localContext;
        this.resultFuture = resultFuture;
        this.httpProcessor = httpProcessor;
        this.requestProducerRef = new AtomicReference<Object>(null);
        this.responseConsumerRef = new AtomicReference<Object>(null);
    }

    private void closeProducer(HttpAsyncRequestProducer requestProducer) {
        if (requestProducer != null) {
            try {
                requestProducer.close();
            }
            catch (IOException ex) {
                this.log.debug("I/O error closing request producer", ex);
            }
        }
    }

    private void closeConsumer(HttpAsyncResponseConsumer<?> responseConsumer) {
        if (responseConsumer != null) {
            try {
                responseConsumer.close();
            }
            catch (IOException ex) {
                this.log.debug("I/O error closing response consumer", ex);
            }
        }
    }

    @Override
    void releaseResources() {
        this.closeProducer(this.requestProducerRef.getAndSet(null));
        this.closeConsumer(this.responseConsumerRef.getAndSet(null));
        while (!this.requestProducerQueue.isEmpty()) {
            this.closeProducer(this.requestProducerQueue.remove());
        }
        while (!this.responseConsumerQueue.isEmpty()) {
            this.closeConsumer(this.responseConsumerQueue.remove());
        }
        this.requestQueue.clear();
        this.resultQueue.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void executionFailed(Exception ex) {
        try {
            HttpAsyncResponseConsumer<T> responseConsumer;
            HttpAsyncRequestProducer requestProducer = this.requestProducerRef.get();
            if (requestProducer != null) {
                requestProducer.failed(ex);
            }
            if ((responseConsumer = this.responseConsumerRef.get()) != null) {
                responseConsumer.failed(ex);
            }
            for (HttpAsyncResponseConsumer httpAsyncResponseConsumer : this.responseConsumerQueue) {
                httpAsyncResponseConsumer.cancel();
            }
        }
        finally {
            this.resultFuture.failed(ex);
        }
    }

    @Override
    boolean executionCancelled() {
        HttpAsyncResponseConsumer<T> responseConsumer = this.responseConsumerRef.get();
        boolean cancelled = responseConsumer != null && responseConsumer.cancel();
        this.resultFuture.cancel();
        return cancelled;
    }

    @Override
    public void start() throws HttpException, IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] start execution");
        }
        HttpRoute route = new HttpRoute(this.target);
        this.setRoute(route);
        this.localContext.setAttribute("http.target_host", this.target);
        this.localContext.setAttribute("http.route", route);
        this.requestConnection();
    }

    @Override
    public HttpRequest generateRequest() throws IOException, HttpException {
        this.verifytRoute();
        if (!this.isRouteEstablished()) {
            this.onRouteToTarget();
            this.onRouteComplete();
        }
        NHttpClientConnection localConn = this.getConnection();
        this.localContext.setAttribute("http.connection", localConn);
        Asserts.check(this.requestProducerRef.get() == null, "Inconsistent state: currentRequest producer is not null");
        HttpAsyncRequestProducer requestProducer = this.requestProducerQueue.poll();
        if (requestProducer == null) {
            return null;
        }
        this.requestProducerRef.set(requestProducer);
        HttpRequest original = requestProducer.generateRequest();
        HttpRequestWrapper currentRequest = HttpRequestWrapper.wrap(original);
        RequestConfig config = this.localContext.getRequestConfig();
        if (config.getSocketTimeout() > 0) {
            localConn.setSocketTimeout(config.getSocketTimeout());
        }
        this.httpProcessor.process(currentRequest, (HttpContext)this.localContext);
        this.requestQueue.add(currentRequest);
        this.setCurrentRequest(currentRequest);
        return currentRequest;
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        HttpAsyncRequestProducer requestProducer;
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] produce content");
        }
        Asserts.check((requestProducer = this.requestProducerRef.get()) != null, "Inconsistent state: request producer is null");
        requestProducer.produceContent(encoder, ioControl);
        if (encoder.isCompleted()) {
            requestProducer.resetRequest();
        }
    }

    @Override
    public void requestCompleted() {
        HttpAsyncRequestProducer requestProducer;
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Request completed");
        }
        Asserts.check((requestProducer = (HttpAsyncRequestProducer)this.requestProducerRef.getAndSet(null)) != null, "Inconsistent state: request producer is null");
        requestProducer.requestCompleted(this.localContext);
        try {
            requestProducer.close();
        }
        catch (IOException ioex) {
            this.log.debug(ioex.getMessage(), ioex);
        }
    }

    @Override
    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Response received " + response.getStatusLine());
        }
        Asserts.check(this.responseConsumerRef.get() == null, "Inconsistent state: response consumer is not null");
        HttpAsyncResponseConsumer<T> responseConsumer = this.responseConsumerQueue.poll();
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer queue is empty");
        this.responseConsumerRef.set(responseConsumer);
        HttpRequest request = this.requestQueue.poll();
        Asserts.check(request != null, "Inconsistent state: request queue is empty");
        this.localContext.setAttribute("http.request", request);
        this.localContext.setAttribute("http.response", response);
        this.httpProcessor.process(response, (HttpContext)this.localContext);
        responseConsumer.responseReceived(response);
        this.setCurrentResponse(response);
    }

    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        HttpAsyncResponseConsumer<T> responseConsumer;
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Consume content");
        }
        Asserts.check((responseConsumer = this.responseConsumerRef.get()) != null, "Inconsistent state: response consumer is null");
        responseConsumer.consumeContent(decoder, ioControl);
    }

    @Override
    public void responseCompleted() throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Response processed");
        }
        boolean keepAlive = this.manageConnectionPersistence();
        HttpAsyncResponseConsumer responseConsumer = this.responseConsumerRef.getAndSet(null);
        Asserts.check(responseConsumer != null, "Inconsistent state: response consumer is null");
        try {
            responseConsumer.responseCompleted(this.localContext);
            Object result = responseConsumer.getResult();
            Exception ex = responseConsumer.getException();
            try {
                responseConsumer.close();
            }
            catch (IOException ioex) {
                this.log.debug(ioex.getMessage(), ioex);
            }
            if (result != null) {
                this.resultQueue.add(result);
            } else {
                this.failed(ex);
            }
            if (!this.resultFuture.isDone() && this.responseConsumerQueue.isEmpty()) {
                this.resultFuture.completed(new ArrayList<T>(this.resultQueue));
                this.resultQueue.clear();
            }
            if (this.resultFuture.isDone()) {
                this.close();
            } else if (!keepAlive) {
                this.failed(new ConnectionClosedException("Connection closed"));
            } else {
                NHttpClientConnection localConn = this.getConnection();
                if (localConn != null) {
                    localConn.requestOutput();
                } else {
                    this.requestConnection();
                }
            }
        }
        catch (RuntimeException ex) {
            this.failed(ex);
            throw ex;
        }
    }

    @Override
    public void inputTerminated() {
        this.failed(new ConnectionClosedException("Connection closed"));
    }

    public void abortConnection() {
        this.discardConnection();
    }
}

