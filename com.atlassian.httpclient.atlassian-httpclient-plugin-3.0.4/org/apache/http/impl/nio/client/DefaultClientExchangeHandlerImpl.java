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
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.concurrent.BasicFuture;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.AbstractClientExchangeHandler;
import org.apache.http.impl.nio.client.InternalClientExec;
import org.apache.http.impl.nio.client.InternalState;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.ContentEncoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.NHttpClientConnection;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;

class DefaultClientExchangeHandlerImpl<T>
extends AbstractClientExchangeHandler {
    private final HttpAsyncRequestProducer requestProducer;
    private final HttpAsyncResponseConsumer<T> responseConsumer;
    private final BasicFuture<T> resultFuture;
    private final InternalClientExec exec;
    private final InternalState state;

    public DefaultClientExchangeHandlerImpl(Log log, HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpClientContext localContext, BasicFuture<T> resultFuture, NHttpClientConnectionManager connmgr, ConnectionReuseStrategy connReuseStrategy, ConnectionKeepAliveStrategy keepaliveStrategy, InternalClientExec exec) {
        super(log, localContext, connmgr, connReuseStrategy, keepaliveStrategy);
        this.requestProducer = requestProducer;
        this.responseConsumer = responseConsumer;
        this.resultFuture = resultFuture;
        this.exec = exec;
        this.state = new InternalState(this.getId(), requestProducer, responseConsumer, localContext);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    void executionFailed(Exception ex) {
        try {
            this.requestProducer.failed(ex);
            this.responseConsumer.failed(ex);
        }
        finally {
            this.resultFuture.failed(ex);
        }
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
        HttpHost target = this.requestProducer.getTarget();
        HttpRequest original = this.requestProducer.generateRequest();
        if (original instanceof HttpExecutionAware) {
            ((HttpExecutionAware)((Object)original)).setCancellable(this);
        }
        this.exec.prepare(target, original, this.state, this);
        this.requestConnection();
    }

    @Override
    public HttpRequest generateRequest() throws IOException, HttpException {
        return this.exec.generateRequest(this.state, this);
    }

    @Override
    public void produceContent(ContentEncoder encoder, IOControl ioControl) throws IOException {
        this.exec.produceContent(this.state, encoder, ioControl);
    }

    @Override
    public void requestCompleted() {
        this.exec.requestCompleted(this.state, this);
    }

    @Override
    public void responseReceived(HttpResponse response) throws IOException, HttpException {
        this.exec.responseReceived(response, this.state, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void consumeContent(ContentDecoder decoder, IOControl ioControl) throws IOException {
        this.exec.consumeContent(this.state, decoder, ioControl);
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
        this.exec.responseCompleted(this.state, this);
        if (this.state.getFinalResponse() != null || this.resultFuture.isDone()) {
            try {
                this.markCompleted();
                this.releaseConnection();
                T result = this.responseConsumer.getResult();
                Exception ex = this.responseConsumer.getException();
                if (ex == null) {
                    this.resultFuture.completed(result);
                }
                this.resultFuture.failed(ex);
            }
            finally {
                this.close();
            }
        } else {
            NHttpClientConnection localConn = this.getConnection();
            if (localConn != null && !localConn.isOpen()) {
                this.releaseConnection();
                localConn = null;
            }
            if (localConn != null) {
                localConn.requestOutput();
            } else {
                this.requestConnection();
            }
        }
    }

    @Override
    public void inputTerminated() {
        if (!this.isCompleted()) {
            this.requestConnection();
        } else {
            this.close();
        }
    }

    public void abortConnection() {
        this.discardConnection();
    }
}

