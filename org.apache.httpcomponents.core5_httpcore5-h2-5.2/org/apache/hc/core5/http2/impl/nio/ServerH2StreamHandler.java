/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.HttpVersion
 *  org.apache.hc.core5.http.MessageHeaders
 *  org.apache.hc.core5.http.Method
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics
 *  org.apache.hc.core5.http.impl.IncomingEntityDetails
 *  org.apache.hc.core5.http.impl.ServerSupport
 *  org.apache.hc.core5.http.impl.nio.MessageState
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncPushProducer
 *  org.apache.hc.core5.http.nio.AsyncResponseProducer
 *  org.apache.hc.core5.http.nio.AsyncServerExchangeHandler
 *  org.apache.hc.core5.http.nio.CapacityChannel
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.ResponseChannel
 *  org.apache.hc.core5.http.nio.support.BasicResponseProducer
 *  org.apache.hc.core5.http.nio.support.ImmediateResponseExchangeHandler
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.util.Asserts
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.impl.IncomingEntityDetails;
import org.apache.hc.core5.http.impl.ServerSupport;
import org.apache.hc.core5.http.impl.nio.MessageState;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncPushProducer;
import org.apache.hc.core5.http.nio.AsyncResponseProducer;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.ResponseChannel;
import org.apache.hc.core5.http.nio.support.BasicResponseProducer;
import org.apache.hc.core5.http.nio.support.ImmediateResponseExchangeHandler;
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
import org.apache.hc.core5.util.Asserts;

class ServerH2StreamHandler
implements H2StreamHandler {
    private final H2StreamChannel outputChannel;
    private final DataStreamChannel dataChannel;
    private final ResponseChannel responseChannel;
    private final HttpProcessor httpProcessor;
    private final BasicHttpConnectionMetrics connMetrics;
    private final HandlerFactory<AsyncServerExchangeHandler> exchangeHandlerFactory;
    private final HttpCoreContext context;
    private final AtomicBoolean responseCommitted;
    private final AtomicBoolean failed;
    private final AtomicBoolean done;
    private volatile AsyncServerExchangeHandler exchangeHandler;
    private volatile HttpRequest receivedRequest;
    private volatile MessageState requestState;
    private volatile MessageState responseState;

    ServerH2StreamHandler(final H2StreamChannel outputChannel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, HandlerFactory<AsyncServerExchangeHandler> exchangeHandlerFactory, HttpCoreContext context) {
        this.outputChannel = outputChannel;
        this.dataChannel = new DataStreamChannel(){

            public void requestOutput() {
                outputChannel.requestOutput();
            }

            public int write(ByteBuffer src) throws IOException {
                return outputChannel.write(src);
            }

            public void endStream(List<? extends Header> trailers) throws IOException {
                outputChannel.endStream(trailers);
                ServerH2StreamHandler.this.responseState = MessageState.COMPLETE;
            }

            public void endStream() throws IOException {
                outputChannel.endStream();
                ServerH2StreamHandler.this.responseState = MessageState.COMPLETE;
            }
        };
        this.responseChannel = new ResponseChannel(){

            public void sendInformation(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
                ServerH2StreamHandler.this.commitInformation(response);
            }

            public void sendResponse(HttpResponse response, EntityDetails responseEntityDetails, HttpContext httpContext) throws HttpException, IOException {
                ServerSupport.validateResponse((HttpResponse)response, (EntityDetails)responseEntityDetails);
                ServerH2StreamHandler.this.commitResponse(response, responseEntityDetails);
            }

            public void pushPromise(HttpRequest promise, AsyncPushProducer pushProducer, HttpContext httpContext) throws HttpException, IOException {
                ServerH2StreamHandler.this.commitPromise(promise, pushProducer);
            }
        };
        this.httpProcessor = httpProcessor;
        this.connMetrics = connMetrics;
        this.exchangeHandlerFactory = exchangeHandlerFactory;
        this.context = context;
        this.responseCommitted = new AtomicBoolean(false);
        this.failed = new AtomicBoolean(false);
        this.done = new AtomicBoolean(false);
        this.requestState = MessageState.HEADERS;
        this.responseState = MessageState.IDLE;
    }

    @Override
    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
        return null;
    }

    private void commitInformation(HttpResponse response) throws IOException, HttpException {
        if (this.responseCommitted.get()) {
            throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Response already committed");
        }
        int status = response.getCode();
        if (status < 100 || status >= 200) {
            throw new HttpException("Invalid intermediate response: " + status);
        }
        List<Header> responseHeaders = DefaultH2ResponseConverter.INSTANCE.convert(response);
        this.outputChannel.submit(responseHeaders, false);
    }

    private void commitResponse(HttpResponse response, EntityDetails responseEntityDetails) throws HttpException, IOException {
        if (this.responseCommitted.compareAndSet(false, true)) {
            int status = response.getCode();
            if (status < 200) {
                throw new HttpException("Invalid response: " + status);
            }
            this.context.setAttribute("http.response", (Object)response);
            this.httpProcessor.process(response, responseEntityDetails, (HttpContext)this.context);
            List<Header> responseHeaders = DefaultH2ResponseConverter.INSTANCE.convert(response);
            boolean endStream = responseEntityDetails == null || this.receivedRequest != null && Method.HEAD.isSame(this.receivedRequest.getMethod());
            this.outputChannel.submit(responseHeaders, endStream);
            this.connMetrics.incrementResponseCount();
            if (responseEntityDetails == null) {
                this.responseState = MessageState.COMPLETE;
            } else {
                this.responseState = MessageState.BODY;
                this.exchangeHandler.produce((DataStreamChannel)this.outputChannel);
            }
        } else {
            throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Response already committed");
        }
    }

    private void commitPromise(HttpRequest promise, AsyncPushProducer pushProducer) throws HttpException, IOException {
        this.httpProcessor.process(promise, null, (HttpContext)this.context);
        List<Header> promiseHeaders = DefaultH2RequestConverter.INSTANCE.convert(promise);
        this.outputChannel.push(promiseHeaders, pushProducer);
        this.connMetrics.incrementRequestCount();
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
        switch (this.requestState) {
            case HEADERS: {
                AsyncServerExchangeHandler handler;
                this.requestState = endStream ? MessageState.COMPLETE : MessageState.BODY;
                HttpRequest request = DefaultH2RequestConverter.INSTANCE.convert(headers);
                IncomingEntityDetails requestEntityDetails = endStream ? null : new IncomingEntityDetails((MessageHeaders)request, -1L);
                try {
                    handler = this.exchangeHandlerFactory != null ? (AsyncServerExchangeHandler)this.exchangeHandlerFactory.create(request, (HttpContext)this.context) : null;
                }
                catch (ProtocolException ex) {
                    throw new H2StreamResetException(H2Error.PROTOCOL_ERROR, ex.getMessage());
                }
                if (handler == null) {
                    throw new H2StreamResetException(H2Error.REFUSED_STREAM, "Stream refused");
                }
                this.exchangeHandler = handler;
                this.context.setProtocolVersion((ProtocolVersion)HttpVersion.HTTP_2);
                this.context.setAttribute("http.request", (Object)request);
                try {
                    this.httpProcessor.process(request, (EntityDetails)requestEntityDetails, (HttpContext)this.context);
                    this.connMetrics.incrementRequestCount();
                    this.receivedRequest = request;
                    this.exchangeHandler.handleRequest(request, (EntityDetails)requestEntityDetails, this.responseChannel, (HttpContext)this.context);
                    break;
                }
                catch (HttpException ex) {
                    if (!this.responseCommitted.get()) {
                        BasicResponseProducer responseProducer = new BasicResponseProducer(ServerSupport.toStatusCode((Exception)((Object)ex)), ServerSupport.toErrorMessage((Exception)((Object)ex)));
                        this.exchangeHandler = new ImmediateResponseExchangeHandler((AsyncResponseProducer)responseProducer);
                        this.exchangeHandler.handleRequest(request, (EntityDetails)requestEntityDetails, this.responseChannel, (HttpContext)this.context);
                        break;
                    }
                    throw ex;
                }
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
        Asserts.notNull((Object)this.exchangeHandler, (String)"Exchange handler");
        this.exchangeHandler.updateCapacity((CapacityChannel)this.outputChannel);
    }

    @Override
    public void consumeData(ByteBuffer src, boolean endStream) throws HttpException, IOException {
        if (this.done.get() || this.requestState != MessageState.BODY) {
            throw new ProtocolException("Unexpected message data");
        }
        Asserts.notNull((Object)this.exchangeHandler, (String)"Exchange handler");
        if (src != null) {
            this.exchangeHandler.consume(src);
        }
        if (endStream) {
            this.requestState = MessageState.COMPLETE;
            this.exchangeHandler.streamEnd(null);
        }
    }

    @Override
    public boolean isOutputReady() {
        return this.responseState == MessageState.BODY && this.exchangeHandler != null && this.exchangeHandler.available() > 0;
    }

    @Override
    public void produceOutput() throws HttpException, IOException {
        if (this.responseState == MessageState.BODY) {
            Asserts.notNull((Object)this.exchangeHandler, (String)"Exchange handler");
            this.exchangeHandler.produce(this.dataChannel);
        }
    }

    @Override
    public void handle(HttpException ex, boolean endStream) throws HttpException, IOException {
        if (this.done.get()) {
            throw ex;
        }
        switch (this.requestState) {
            case HEADERS: {
                MessageState messageState = this.requestState = endStream ? MessageState.COMPLETE : MessageState.BODY;
                if (!this.responseCommitted.get()) {
                    BasicResponseProducer responseProducer = new BasicResponseProducer(ServerSupport.toStatusCode((Exception)((Object)ex)), ServerSupport.toErrorMessage((Exception)((Object)ex)));
                    this.exchangeHandler = new ImmediateResponseExchangeHandler((AsyncResponseProducer)responseProducer);
                    this.exchangeHandler.handleRequest(null, null, this.responseChannel, (HttpContext)this.context);
                    break;
                }
                throw ex;
            }
            case BODY: {
                this.responseState = MessageState.COMPLETE;
            }
            default: {
                throw ex;
            }
        }
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

    public void releaseResources() {
        if (this.done.compareAndSet(false, true)) {
            this.requestState = MessageState.COMPLETE;
            this.responseState = MessageState.COMPLETE;
            if (this.exchangeHandler != null) {
                this.exchangeHandler.releaseResources();
            }
        }
    }

    public String toString() {
        return "[requestState=" + this.requestState + ", responseState=" + this.responseState + ']';
    }
}

