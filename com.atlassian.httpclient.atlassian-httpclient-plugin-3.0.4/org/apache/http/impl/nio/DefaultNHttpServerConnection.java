/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.DisallowIdentityContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.nio.NHttpConnectionBase;
import org.apache.http.impl.nio.NHttpServerEventHandlerAdaptor;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestParser;
import org.apache.http.impl.nio.codecs.DefaultHttpRequestParserFactory;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseWriter;
import org.apache.http.impl.nio.codecs.DefaultHttpResponseWriterFactory;
import org.apache.http.nio.NHttpMessageParser;
import org.apache.http.nio.NHttpMessageParserFactory;
import org.apache.http.nio.NHttpMessageWriter;
import org.apache.http.nio.NHttpMessageWriterFactory;
import org.apache.http.nio.NHttpServerEventHandler;
import org.apache.http.nio.NHttpServerIOTarget;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.nio.reactor.SessionOutputBuffer;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

public class DefaultNHttpServerConnection
extends NHttpConnectionBase
implements NHttpServerIOTarget {
    protected final NHttpMessageParser<HttpRequest> requestParser;
    protected final NHttpMessageWriter<HttpResponse> responseWriter;

    @Deprecated
    public DefaultNHttpServerConnection(IOSession session, HttpRequestFactory requestFactory, ByteBufferAllocator allocator, HttpParams params) {
        super(session, allocator, params);
        Args.notNull(requestFactory, "Request factory");
        this.requestParser = this.createRequestParser(this.inbuf, requestFactory, params);
        this.responseWriter = this.createResponseWriter(this.outbuf, params);
    }

    public DefaultNHttpServerConnection(IOSession session, int bufferSize, int fragmentSizeHint, ByteBufferAllocator allocator, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints, ContentLengthStrategy incomingContentStrategy, ContentLengthStrategy outgoingContentStrategy, NHttpMessageParserFactory<HttpRequest> requestParserFactory, NHttpMessageWriterFactory<HttpResponse> responseWriterFactory) {
        super(session, bufferSize, fragmentSizeHint, allocator, charDecoder, charEncoder, constraints, incomingContentStrategy != null ? incomingContentStrategy : DisallowIdentityContentLengthStrategy.INSTANCE, outgoingContentStrategy != null ? outgoingContentStrategy : StrictContentLengthStrategy.INSTANCE);
        this.requestParser = (requestParserFactory != null ? requestParserFactory : DefaultHttpRequestParserFactory.INSTANCE).create(this.inbuf, constraints);
        this.responseWriter = (responseWriterFactory != null ? responseWriterFactory : DefaultHttpResponseWriterFactory.INSTANCE).create(this.outbuf);
    }

    public DefaultNHttpServerConnection(IOSession session, int bufferSize, CharsetDecoder charDecoder, CharsetEncoder charEncoder, MessageConstraints constraints) {
        this(session, bufferSize, bufferSize, null, charDecoder, charEncoder, constraints, null, null, null, null);
    }

    public DefaultNHttpServerConnection(IOSession session, int bufferSize) {
        this(session, bufferSize, bufferSize, null, null, null, null, null, null, null, null);
    }

    @Override
    @Deprecated
    protected ContentLengthStrategy createIncomingContentStrategy() {
        return new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0));
    }

    @Deprecated
    protected NHttpMessageParser<HttpRequest> createRequestParser(SessionInputBuffer buffer, HttpRequestFactory requestFactory, HttpParams params) {
        MessageConstraints constraints = HttpParamConfig.getMessageConstraints(params);
        return new DefaultHttpRequestParser(buffer, null, requestFactory, constraints);
    }

    @Deprecated
    protected NHttpMessageWriter<HttpResponse> createResponseWriter(SessionOutputBuffer buffer, HttpParams params) {
        return new DefaultHttpResponseWriter(buffer, null);
    }

    protected void onRequestReceived(HttpRequest request) {
    }

    protected void onResponseSubmitted(HttpResponse response) {
    }

    @Override
    public void resetInput() {
        this.request = null;
        this.contentDecoder = null;
        this.requestParser.reset();
    }

    @Override
    public void resetOutput() {
        this.response = null;
        this.contentEncoder = null;
        this.responseWriter.reset();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void consumeInput(NHttpServerEventHandler handler) {
        if (this.status != 0) {
            this.session.clearEvent(1);
            return;
        }
        try {
            if (this.request == null) {
                int bytesRead;
                do {
                    if ((bytesRead = this.requestParser.fillBuffer(this.session.channel())) > 0) {
                        this.inTransportMetrics.incrementBytesTransferred(bytesRead);
                    }
                    this.request = this.requestParser.parse();
                } while (bytesRead > 0 && this.request == null);
                if (this.request != null) {
                    if (this.request instanceof HttpEntityEnclosingRequest) {
                        HttpEntity entity = this.prepareDecoder(this.request);
                        ((HttpEntityEnclosingRequest)this.request).setEntity(entity);
                    }
                    this.connMetrics.incrementRequestCount();
                    this.hasBufferedInput = this.inbuf.hasData();
                    this.onRequestReceived(this.request);
                    handler.requestReceived(this);
                    if (this.contentDecoder == null) {
                        this.resetInput();
                    }
                }
                if (bytesRead == -1 && !this.inbuf.hasData()) {
                    handler.endOfInput(this);
                }
            }
            if (this.contentDecoder != null && (this.session.getEventMask() & 1) > 0) {
                handler.inputReady(this, this.contentDecoder);
                if (this.contentDecoder.isCompleted()) {
                    this.resetInput();
                }
            }
        }
        catch (HttpException ex) {
            handler.exception(this, ex);
        }
        catch (Exception ex) {
            handler.exception(this, ex);
        }
        finally {
            this.hasBufferedInput = this.inbuf.hasData();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void produceOutput(NHttpServerEventHandler handler) {
        try {
            int bytesWritten;
            if (this.status == 0) {
                if (this.contentEncoder == null && !this.outbuf.hasData()) {
                    handler.responseReady(this);
                }
                if (this.contentEncoder != null) {
                    handler.outputReady(this, this.contentEncoder);
                    if (this.contentEncoder.isCompleted()) {
                        this.resetOutput();
                    }
                }
            }
            if (this.outbuf.hasData() && (bytesWritten = this.outbuf.flush(this.session.channel())) > 0) {
                this.outTransportMetrics.incrementBytesTransferred(bytesWritten);
            }
            if (!this.outbuf.hasData() && this.status == 1) {
                this.session.close();
                this.status = 2;
                this.resetOutput();
            }
        }
        catch (Exception ex) {
            handler.exception(this, ex);
        }
        finally {
            this.hasBufferedOutput = this.outbuf.hasData();
        }
    }

    @Override
    public void submitResponse(HttpResponse response) throws IOException, HttpException {
        Args.notNull(response, "HTTP response");
        this.assertNotClosed();
        if (this.response != null) {
            throw new HttpException("Response already submitted");
        }
        this.onResponseSubmitted(response);
        this.responseWriter.write(response);
        this.hasBufferedOutput = this.outbuf.hasData();
        if (response.getStatusLine().getStatusCode() >= 200) {
            this.connMetrics.incrementResponseCount();
            if (response.getEntity() != null) {
                this.response = response;
                this.prepareEncoder(response);
            }
        }
        this.session.setEvent(4);
    }

    @Override
    public boolean isResponseSubmitted() {
        return this.response != null;
    }

    @Override
    public void consumeInput(NHttpServiceHandler handler) {
        this.consumeInput(new NHttpServerEventHandlerAdaptor(handler));
    }

    @Override
    public void produceOutput(NHttpServiceHandler handler) {
        this.produceOutput(new NHttpServerEventHandlerAdaptor(handler));
    }
}

