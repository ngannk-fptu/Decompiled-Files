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
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.RequestChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.impl.DefaultH2RequestConverter;
import org.apache.hc.core5.http2.impl.DefaultH2ResponseConverter;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;

class ClientH2StreamHandler
implements H2StreamHandler {
    private final H2StreamChannel outputChannel;
    private final DataStreamChannel dataChannel;
    private final HttpProcessor httpProcessor;
    private final BasicHttpConnectionMetrics connMetrics;
    private final AsyncClientExchangeHandler exchangeHandler;
    private final HandlerFactory<AsyncPushConsumer> pushHandlerFactory;
    private final HttpCoreContext context;
    private final AtomicBoolean requestCommitted;
    private final AtomicBoolean failed;
    private final AtomicBoolean done;
    private volatile MessageState requestState;
    private volatile MessageState responseState;

    ClientH2StreamHandler(final H2StreamChannel outputChannel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpCoreContext context) {
        this.outputChannel = outputChannel;
        this.dataChannel = new DataStreamChannel(){

            @Override
            public void requestOutput() {
                outputChannel.requestOutput();
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                return outputChannel.write(src);
            }

            @Override
            public void endStream(List<? extends Header> trailers) throws IOException {
                outputChannel.endStream(trailers);
                ClientH2StreamHandler.this.requestState = MessageState.COMPLETE;
            }

            @Override
            public void endStream() throws IOException {
                outputChannel.endStream();
                ClientH2StreamHandler.this.requestState = MessageState.COMPLETE;
            }
        };
        this.httpProcessor = httpProcessor;
        this.connMetrics = connMetrics;
        this.exchangeHandler = exchangeHandler;
        this.pushHandlerFactory = pushHandlerFactory;
        this.context = context;
        this.requestCommitted = new AtomicBoolean(false);
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
        switch (this.requestState) {
            case HEADERS: {
                return true;
            }
            case BODY: {
                return this.exchangeHandler.available() > 0;
            }
        }
        return false;
    }

    private void commitRequest(HttpRequest request, EntityDetails entityDetails) throws HttpException, IOException {
        if (this.requestCommitted.compareAndSet(false, true)) {
            this.context.setProtocolVersion(HttpVersion.HTTP_2);
            this.context.setAttribute("http.request", request);
            this.httpProcessor.process(request, entityDetails, (HttpContext)this.context);
            List<Header> headers = DefaultH2RequestConverter.INSTANCE.convert(request);
            this.outputChannel.submit(headers, entityDetails == null);
            this.connMetrics.incrementRequestCount();
            if (entityDetails == null) {
                this.requestState = MessageState.COMPLETE;
            } else {
                boolean expectContinue;
                Header h = request.getFirstHeader("Expect");
                boolean bl = expectContinue = h != null && "100-continue".equalsIgnoreCase(h.getValue());
                if (expectContinue) {
                    this.requestState = MessageState.ACK;
                } else {
                    this.requestState = MessageState.BODY;
                    this.exchangeHandler.produce(this.dataChannel);
                }
            }
        } else {
            throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Request already committed");
        }
    }

    @Override
    public void produceOutput() throws HttpException, IOException {
        switch (this.requestState) {
            case HEADERS: {
                this.exchangeHandler.produceRequest(new RequestChannel(){

                    @Override
                    public void sendRequest(HttpRequest request, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
                        ClientH2StreamHandler.this.commitRequest(request, entityDetails);
                    }
                }, this.context);
                break;
            }
            case BODY: {
                this.exchangeHandler.produce(this.dataChannel);
            }
        }
    }

    @Override
    public void consumePromise(List<Header> headers) throws HttpException, IOException {
        throw new ProtocolException("Unexpected message promise");
    }

    @Override
    public void consumeHeader(List<Header> headers, boolean endStream) throws HttpException, IOException {
        if (this.done.get()) {
            throw new ProtocolException("Unexpected message headers");
        }
        switch (this.responseState) {
            case HEADERS: {
                HttpMessage response = DefaultH2ResponseConverter.INSTANCE.convert((List)headers);
                int status = response.getCode();
                if (status < 100) {
                    throw new ProtocolException("Invalid response: " + new StatusLine((HttpResponse)response));
                }
                if (status > 100 && status < 200) {
                    this.exchangeHandler.consumeInformation((HttpResponse)response, this.context);
                }
                if (this.requestState == MessageState.ACK && (status == 100 || status >= 200)) {
                    this.requestState = MessageState.BODY;
                    this.exchangeHandler.produce(this.dataChannel);
                }
                if (status < 200) {
                    return;
                }
                IncomingEntityDetails entityDetails = endStream ? null : new IncomingEntityDetails(response, -1L);
                this.context.setAttribute("http.response", response);
                this.httpProcessor.process((HttpResponse)response, (EntityDetails)entityDetails, (HttpContext)this.context);
                this.connMetrics.incrementResponseCount();
                this.exchangeHandler.consumeResponse((HttpResponse)response, entityDetails, this.context);
                this.responseState = endStream ? MessageState.COMPLETE : MessageState.BODY;
                break;
            }
            case BODY: {
                this.responseState = MessageState.COMPLETE;
                this.exchangeHandler.streamEnd(headers);
                break;
            }
            default: {
                throw new ProtocolException("Unexpected message headers");
            }
        }
    }

    @Override
    public void updateInputCapacity() throws IOException {
        this.exchangeHandler.updateCapacity(this.outputChannel);
    }

    @Override
    public void consumeData(ByteBuffer src, boolean endStream) throws HttpException, IOException {
        if (this.done.get() || this.responseState != MessageState.BODY) {
            throw new ProtocolException("Unexpected message data");
        }
        if (src != null) {
            this.exchangeHandler.consume(src);
        }
        if (endStream) {
            this.responseState = MessageState.COMPLETE;
            this.exchangeHandler.streamEnd(null);
        }
    }

    @Override
    public void handle(HttpException ex, boolean endStream) throws HttpException, IOException {
        throw ex;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
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
    public void releaseResources() {
        if (this.done.compareAndSet(false, true)) {
            this.responseState = MessageState.COMPLETE;
            this.requestState = MessageState.COMPLETE;
            this.exchangeHandler.releaseResources();
        }
    }

    public String toString() {
        return "[requestState=" + (Object)((Object)this.requestState) + ", responseState=" + (Object)((Object)this.responseState) + ']';
    }
}

