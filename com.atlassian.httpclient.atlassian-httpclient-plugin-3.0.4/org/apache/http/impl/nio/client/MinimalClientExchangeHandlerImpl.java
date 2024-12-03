/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpExecutionAware;
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
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;

class MinimalClientExchangeHandlerImpl<T>
extends AbstractClientExchangeHandler {
    private final HttpAsyncRequestProducer requestProducer;
    private final HttpAsyncResponseConsumer<T> responseConsumer;
    private final HttpClientContext localContext;
    private final BasicFuture<T> resultFuture;
    private final HttpProcessor httpProcessor;

    public MinimalClientExchangeHandlerImpl(Log log, HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpClientContext localContext, BasicFuture<T> resultFuture, NHttpClientConnectionManager connmgr, HttpProcessor httpProcessor, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy) {
        super(log, localContext, connmgr, connReuseStrategy, keepaliveStrategy);
        this.requestProducer = requestProducer;
        this.responseConsumer = responseConsumer;
        this.localContext = localContext;
        this.resultFuture = resultFuture;
        this.httpProcessor = httpProcessor;
    }

    @Override
    void releaseResources() {
        try {
            this.requestProducer.close();
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing request producer", ex);
        }
        try {
            this.responseConsumer.close();
        }
        catch (IOException ex) {
            this.log.debug("I/O error closing response consumer", ex);
        }
    }

    @Override
    void executionFailed(Exception ex) {
        this.requestProducer.failed(ex);
        this.responseConsumer.failed(ex);
    }

    @Override
    boolean executionCancelled() {
        boolean cancelled = this.responseConsumer.cancel();
        T result = this.responseConsumer.getResult();
        Exception ex = this.responseConsumer.getException();
        if (ex != null) {
            this.resultFuture.failed(ex);
        } else if (result != null) {
            this.resultFuture.completed(result);
        } else {
            this.resultFuture.cancel();
        }
        return cancelled;
    }

    @Override
    public void start() throws HttpException, IOException {
        RequestConfig config;
        HttpHost target = this.requestProducer.getTarget();
        HttpRequest original = this.requestProducer.generateRequest();
        if (original instanceof HttpExecutionAware) {
            ((HttpExecutionAware)((Object)original)).setCancellable(this);
        }
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] start execution");
        }
        if (original instanceof Configurable && (config = ((Configurable)((Object)original)).getConfig()) != null) {
            this.localContext.setRequestConfig(config);
        }
        HttpRequestWrapper request = HttpRequestWrapper.wrap(original);
        HttpRoute route = new HttpRoute(target);
        this.setCurrentRequest(request);
        this.setRoute(route);
        this.localContext.setAttribute("http.request", request);
        this.localContext.setAttribute("http.target_host", target);
        this.localContext.setAttribute("http.route", route);
        this.httpProcessor.process(request, (HttpContext)this.localContext);
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
        RequestConfig config = this.localContext.getRequestConfig();
        if (config.getSocketTimeout() > 0) {
            localConn.setSocketTimeout(config.getSocketTimeout());
        }
        return this.getCurrentRequest();
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] produce content");
        }
        this.requestProducer.produceContent(encoder, ioControl);
        if (encoder.isCompleted()) {
            this.requestProducer.resetRequest();
        }
    }

    @Override
    public void requestCompleted() {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Request completed");
        }
        this.requestProducer.requestCompleted(this.localContext);
    }

    @Override
    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Response received " + response.getStatusLine());
        }
        this.localContext.setAttribute("http.response", response);
        this.httpProcessor.process(response, (HttpContext)this.localContext);
        this.setCurrentResponse(response);
        this.responseConsumer.responseReceived(response);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Consume content");
        }
        this.responseConsumer.consumeContent(decoder, ioControl);
        if (!decoder.isCompleted() && this.responseConsumer.isDone()) {
            this.markConnectionNonReusable();
            try {
                this.markCompleted();
                this.releaseConnection();
                this.resultFuture.cancel();
            }
            finally {
                this.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void responseCompleted() throws IOException, HttpException {
        this.manageConnectionPersistence();
        this.responseConsumer.responseCompleted(this.localContext);
        if (this.log.isDebugEnabled()) {
            this.log.debug("[exchange: " + this.getId() + "] Response processed");
        }
        try {
            this.markCompleted();
            this.releaseConnection();
            T result = this.responseConsumer.getResult();
            Exception ex = this.responseConsumer.getException();
            if (ex == null) {
                this.resultFuture.completed(result);
            } else {
                this.resultFuture.failed(ex);
            }
        }
        finally {
            this.close();
        }
    }

    @Override
    public void inputTerminated() {
        this.close();
    }

    public void abortConnection() {
        this.discardConnection();
    }
}

