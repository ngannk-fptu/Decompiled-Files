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
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics
 *  org.apache.hc.core5.http.impl.nio.MessageState
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncPushProducer
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.ResponseChannel
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
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
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.impl.nio.MessageState;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncPushProducer;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.ResponseChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.impl.DefaultH2RequestConverter;
import org.apache.hc.core5.http2.impl.DefaultH2ResponseConverter;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;

class ServerPushH2StreamHandler
implements H2StreamHandler {
    private final H2StreamChannel outputChannel;
    private final DataStreamChannel dataChannel;
    private final HttpProcessor httpProcessor;
    private final BasicHttpConnectionMetrics connMetrics;
    private final AsyncPushProducer pushProducer;
    private final HttpCoreContext context;
    private final AtomicBoolean responseCommitted;
    private final AtomicBoolean failed;
    private final AtomicBoolean done;
    private volatile MessageState requestState;
    private volatile MessageState responseState;

    ServerPushH2StreamHandler(final H2StreamChannel outputChannel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, AsyncPushProducer pushProducer, HttpCoreContext context) {
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
                ServerPushH2StreamHandler.this.responseState = MessageState.COMPLETE;
            }

            public void endStream() throws IOException {
                outputChannel.endStream();
                ServerPushH2StreamHandler.this.responseState = MessageState.COMPLETE;
            }
        };
        this.httpProcessor = httpProcessor;
        this.connMetrics = connMetrics;
        this.pushProducer = pushProducer;
        this.context = context;
        this.responseCommitted = new AtomicBoolean(false);
        this.failed = new AtomicBoolean(false);
        this.done = new AtomicBoolean(false);
        this.requestState = MessageState.COMPLETE;
        this.responseState = MessageState.IDLE;
    }

    @Override
    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
        return null;
    }

    @Override
    public void consumePromise(List<Header> headers) throws HttpException, IOException {
        throw new ProtocolException("Unexpected message promise");
    }

    @Override
    public void consumeHeader(List<Header> requestHeaders, boolean requestEndStream) throws HttpException, IOException {
        throw new ProtocolException("Unexpected message headers");
    }

    @Override
    public void updateInputCapacity() throws IOException {
    }

    @Override
    public void consumeData(ByteBuffer src, boolean endStream) throws HttpException, IOException {
        throw new ProtocolException("Unexpected message data");
    }

    @Override
    public boolean isOutputReady() {
        switch (this.responseState) {
            case IDLE: {
                return true;
            }
            case BODY: {
                return this.pushProducer.available() > 0;
            }
        }
        return false;
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
            this.context.setProtocolVersion((ProtocolVersion)HttpVersion.HTTP_2);
            this.context.setAttribute("http.response", (Object)response);
            this.httpProcessor.process(response, responseEntityDetails, (HttpContext)this.context);
            List<Header> headers = DefaultH2ResponseConverter.INSTANCE.convert(response);
            this.outputChannel.submit(headers, responseEntityDetails == null);
            this.connMetrics.incrementResponseCount();
            if (responseEntityDetails == null) {
                this.responseState = MessageState.COMPLETE;
            } else {
                this.responseState = MessageState.BODY;
                this.pushProducer.produce((DataStreamChannel)this.outputChannel);
            }
        }
    }

    private void commitPromise(HttpRequest promise, AsyncPushProducer pushProducer) throws HttpException, IOException {
        this.context.setProtocolVersion((ProtocolVersion)HttpVersion.HTTP_2);
        this.context.setAttribute("http.request", (Object)promise);
        this.httpProcessor.process(promise, null, (HttpContext)this.context);
        List<Header> headers = DefaultH2RequestConverter.INSTANCE.convert(promise);
        this.outputChannel.push(headers, pushProducer);
        this.connMetrics.incrementRequestCount();
    }

    @Override
    public void produceOutput() throws HttpException, IOException {
        switch (this.responseState) {
            case IDLE: {
                this.responseState = MessageState.HEADERS;
                this.pushProducer.produceResponse(new ResponseChannel(){

                    public void sendInformation(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
                        ServerPushH2StreamHandler.this.commitInformation(response);
                    }

                    public void sendResponse(HttpResponse response, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
                        ServerPushH2StreamHandler.this.commitResponse(response, entityDetails);
                    }

                    public void pushPromise(HttpRequest promise, AsyncPushProducer pushProducer, HttpContext httpContext) throws HttpException, IOException {
                        ServerPushH2StreamHandler.this.commitPromise(promise, pushProducer);
                    }
                }, (HttpContext)this.context);
                break;
            }
            case BODY: {
                this.pushProducer.produce(this.dataChannel);
            }
        }
    }

    @Override
    public void handle(HttpException ex, boolean endStream) throws HttpException, IOException {
        throw ex;
    }

    @Override
    public void failed(Exception cause) {
        try {
            if (this.failed.compareAndSet(false, true)) {
                this.pushProducer.failed(cause);
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
            this.pushProducer.releaseResources();
        }
    }

    public String toString() {
        return "[requestState=" + this.requestState + ", responseState=" + this.responseState + ']';
    }
}

