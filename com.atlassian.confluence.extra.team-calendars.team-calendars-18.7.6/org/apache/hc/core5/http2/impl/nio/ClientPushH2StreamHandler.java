/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.impl.IncomingEntityDetails;
import org.apache.hc.core5.http.impl.nio.MessageState;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.H2StreamResetException;
import org.apache.hc.core5.http2.impl.DefaultH2RequestConverter;
import org.apache.hc.core5.http2.impl.DefaultH2ResponseConverter;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.NoopAsyncPushHandler;
import org.apache.hc.core5.util.Asserts;

class ClientPushH2StreamHandler
implements H2StreamHandler {
    private final H2StreamChannel internalOutputChannel;
    private final HttpProcessor httpProcessor;
    private final BasicHttpConnectionMetrics connMetrics;
    private final HandlerFactory<AsyncPushConsumer> pushHandlerFactory;
    private final HttpCoreContext context;
    private final AtomicBoolean failed;
    private final AtomicBoolean done;
    private volatile HttpRequest request;
    private volatile AsyncPushConsumer exchangeHandler;
    private volatile MessageState requestState;
    private volatile MessageState responseState;

    ClientPushH2StreamHandler(H2StreamChannel outputChannel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpCoreContext context) {
        this.internalOutputChannel = outputChannel;
        this.httpProcessor = httpProcessor;
        this.connMetrics = connMetrics;
        this.pushHandlerFactory = pushHandlerFactory;
        this.context = context;
        this.failed = new AtomicBoolean(false);
        this.done = new AtomicBoolean(false);
        this.requestState = MessageState.HEADERS;
        this.responseState = MessageState.HEADERS;
    }

    @Override
    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
        return this.pushHandlerFactory;
    }

    @Override
    public boolean isOutputReady() {
        return false;
    }

    @Override
    public void produceOutput() throws HttpException, IOException {
    }

    @Override
    public void consumePromise(List<Header> headers) throws HttpException, IOException {
        if (this.requestState == MessageState.HEADERS) {
            this.request = DefaultH2RequestConverter.INSTANCE.convert((List)headers);
            try {
                this.exchangeHandler = this.pushHandlerFactory != null ? this.pushHandlerFactory.create(this.request, this.context) : null;
            }
            catch (ProtocolException ex) {
                this.exchangeHandler = new NoopAsyncPushHandler();
                throw new H2StreamResetException(H2Error.PROTOCOL_ERROR, ex.getMessage());
            }
            if (this.exchangeHandler == null) {
                this.exchangeHandler = new NoopAsyncPushHandler();
                throw new H2StreamResetException(H2Error.REFUSED_STREAM, "Stream refused");
            }
        } else {
            throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Unexpected promise");
        }
        this.context.setProtocolVersion(HttpVersion.HTTP_2);
        this.context.setAttribute("http.request", this.request);
        this.httpProcessor.process(this.request, null, (HttpContext)this.context);
        this.connMetrics.incrementRequestCount();
        this.requestState = MessageState.COMPLETE;
    }

    @Override
    public void consumeHeader(List<Header> headers, boolean endStream) throws HttpException, IOException {
        if (this.responseState == MessageState.HEADERS) {
            Asserts.notNull(this.request, "Request");
            Asserts.notNull(this.exchangeHandler, "Exchange handler");
            HttpMessage response = DefaultH2ResponseConverter.INSTANCE.convert((List)headers);
            IncomingEntityDetails entityDetails = endStream ? null : new IncomingEntityDetails(this.request, -1L);
            this.context.setAttribute("http.response", response);
            this.httpProcessor.process((HttpResponse)response, (EntityDetails)entityDetails, (HttpContext)this.context);
            this.connMetrics.incrementResponseCount();
            this.exchangeHandler.consumePromise(this.request, (HttpResponse)response, entityDetails, this.context);
            if (endStream) {
                this.responseState = MessageState.COMPLETE;
                this.exchangeHandler.streamEnd(null);
            } else {
                this.responseState = MessageState.BODY;
            }
        } else {
            throw new ProtocolException("Unexpected message headers");
        }
    }

    @Override
    public void updateInputCapacity() throws IOException {
        Asserts.notNull(this.exchangeHandler, "Exchange handler");
        this.exchangeHandler.updateCapacity(this.internalOutputChannel);
    }

    @Override
    public void consumeData(ByteBuffer src, boolean endStream) throws HttpException, IOException {
        if (this.responseState != MessageState.BODY) {
            throw new ProtocolException("Unexpected message data");
        }
        Asserts.notNull(this.exchangeHandler, "Exchange handler");
        if (src != null) {
            this.exchangeHandler.consume(src);
        }
        if (endStream) {
            this.responseState = MessageState.COMPLETE;
            this.exchangeHandler.streamEnd(null);
        }
    }

    public boolean isDone() {
        return this.responseState == MessageState.COMPLETE;
    }

    @Override
    public void failed(Exception cause) {
        try {
            if (this.failed.compareAndSet(false, true) && this.exchangeHandler != null) {
                this.exchangeHandler.failed(cause);
            }
        }
        finally {
            this.releaseResources();
        }
    }

    @Override
    public void handle(HttpException ex, boolean endStream) throws HttpException {
        throw ex;
    }

    @Override
    public void releaseResources() {
        if (this.done.compareAndSet(false, true)) {
            this.responseState = MessageState.COMPLETE;
            this.requestState = MessageState.COMPLETE;
            if (this.exchangeHandler != null) {
                this.exchangeHandler.releaseResources();
            }
        }
    }

    public String toString() {
        return "[requestState=" + (Object)((Object)this.requestState) + ", responseState=" + (Object)((Object)this.responseState) + ']';
    }
}

