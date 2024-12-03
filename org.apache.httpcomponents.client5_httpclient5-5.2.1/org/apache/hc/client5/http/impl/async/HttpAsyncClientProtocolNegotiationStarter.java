/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpConnection
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.impl.Http1StreamListener
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpRequestWriterFactory
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpResponseParserFactory
 *  org.apache.hc.core5.http.message.RequestLine
 *  org.apache.hc.core5.http.message.StatusLine
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.NHttpMessageParserFactory
 *  org.apache.hc.core5.http.nio.NHttpMessageWriterFactory
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http2.HttpVersionPolicy
 *  org.apache.hc.core5.http2.config.H2Config
 *  org.apache.hc.core5.http2.frame.FramePrinter
 *  org.apache.hc.core5.http2.frame.RawFrame
 *  org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler
 *  org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory
 *  org.apache.hc.core5.http2.impl.nio.ClientH2UpgradeHandler
 *  org.apache.hc.core5.http2.impl.nio.ClientHttp1UpgradeHandler
 *  org.apache.hc.core5.http2.impl.nio.H2StreamListener
 *  org.apache.hc.core5.http2.impl.nio.HttpProtocolNegotiator
 *  org.apache.hc.core5.http2.ssl.ApplicationProtocol
 *  org.apache.hc.core5.reactor.IOEventHandler
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ProtocolUpgradeHandler
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.async.InternalHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.LogAppendable;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.impl.nio.DefaultHttpRequestWriterFactory;
import org.apache.hc.core5.http.impl.nio.DefaultHttpResponseParserFactory;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.NHttpMessageParserFactory;
import org.apache.hc.core5.http.nio.NHttpMessageWriterFactory;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.FramePrinter;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ClientH2UpgradeHandler;
import org.apache.hc.core5.http2.impl.nio.ClientHttp1UpgradeHandler;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.HttpProtocolNegotiator;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpAsyncClientProtocolNegotiationStarter
implements IOEventHandlerFactory {
    private static final Logger STREAM_LOG = LoggerFactory.getLogger(InternalHttpAsyncClient.class);
    private static final Logger HEADER_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http.headers");
    private static final Logger FRAME_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.frame");
    private static final Logger FRAME_PAYLOAD_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.frame.payload");
    private static final Logger FLOW_CTRL_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.flow");
    private final HttpProcessor httpProcessor;
    private final HandlerFactory<AsyncPushConsumer> exchangeHandlerFactory;
    private final H2Config h2Config;
    private final Http1Config h1Config;
    private final CharCodingConfig charCodingConfig;
    private final ConnectionReuseStrategy http1ConnectionReuseStrategy;
    private final NHttpMessageParserFactory<HttpResponse> http1ResponseParserFactory;
    private final NHttpMessageWriterFactory<HttpRequest> http1RequestWriterFactory;

    HttpAsyncClientProtocolNegotiationStarter(HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> exchangeHandlerFactory, H2Config h2Config, Http1Config h1Config, CharCodingConfig charCodingConfig, ConnectionReuseStrategy connectionReuseStrategy) {
        this.httpProcessor = (HttpProcessor)Args.notNull((Object)httpProcessor, (String)"HTTP processor");
        this.exchangeHandlerFactory = exchangeHandlerFactory;
        this.h2Config = h2Config != null ? h2Config : H2Config.DEFAULT;
        this.h1Config = h1Config != null ? h1Config : Http1Config.DEFAULT;
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
        this.http1ConnectionReuseStrategy = connectionReuseStrategy != null ? connectionReuseStrategy : DefaultClientConnectionReuseStrategy.INSTANCE;
        this.http1ResponseParserFactory = new DefaultHttpResponseParserFactory(h1Config);
        this.http1RequestWriterFactory = DefaultHttpRequestWriterFactory.INSTANCE;
    }

    public IOEventHandler createHandler(ProtocolIOSession ioSession, Object attachment) {
        ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;
        ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;
        if (STREAM_LOG.isDebugEnabled() || HEADER_LOG.isDebugEnabled() || FRAME_LOG.isDebugEnabled() || FRAME_PAYLOAD_LOG.isDebugEnabled() || FLOW_CTRL_LOG.isDebugEnabled()) {
            final String id = ioSession.getId();
            http1StreamHandlerFactory = new ClientHttp1StreamDuplexerFactory(this.httpProcessor, this.h1Config, this.charCodingConfig, this.http1ConnectionReuseStrategy, this.http1ResponseParserFactory, this.http1RequestWriterFactory, new Http1StreamListener(){

                public void onRequestHead(HttpConnection connection, HttpRequest request) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        HEADER_LOG.debug("{} >> {}", (Object)id, (Object)new RequestLine(request));
                        Iterator it = request.headerIterator();
                        while (it.hasNext()) {
                            HEADER_LOG.debug("{} >> {}", (Object)id, it.next());
                        }
                    }
                }

                public void onResponseHead(HttpConnection connection, HttpResponse response) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        HEADER_LOG.debug("{} << {}", (Object)id, (Object)new StatusLine(response));
                        Iterator it = response.headerIterator();
                        while (it.hasNext()) {
                            HEADER_LOG.debug("{} << {}", (Object)id, it.next());
                        }
                    }
                }

                public void onExchangeComplete(HttpConnection connection, boolean keepAlive) {
                    if (STREAM_LOG.isDebugEnabled()) {
                        if (keepAlive) {
                            STREAM_LOG.debug("{} Connection is kept alive", (Object)id);
                        } else {
                            STREAM_LOG.debug("{} Connection is not kept alive", (Object)id);
                        }
                    }
                }
            });
            http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor, this.exchangeHandlerFactory, this.h2Config, this.charCodingConfig, new H2StreamListener(){
                final FramePrinter framePrinter = new FramePrinter();

                private void logFrameInfo(String prefix, RawFrame frame) {
                    try {
                        LogAppendable logAppendable = new LogAppendable(FRAME_LOG, prefix);
                        this.framePrinter.printFrameInfo(frame, (Appendable)logAppendable);
                        logAppendable.flush();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }

                private void logFramePayload(String prefix, RawFrame frame) {
                    try {
                        LogAppendable logAppendable = new LogAppendable(FRAME_PAYLOAD_LOG, prefix);
                        this.framePrinter.printPayload(frame, (Appendable)logAppendable);
                        logAppendable.flush();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }

                private void logFlowControl(String prefix, int streamId, int delta, int actualSize) {
                    FLOW_CTRL_LOG.debug("{} stream {} flow control {} -> {}", new Object[]{prefix, streamId, delta, actualSize});
                }

                public void onHeaderInput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        for (int i = 0; i < headers.size(); ++i) {
                            HEADER_LOG.debug("{} << {}", (Object)id, (Object)headers.get(i));
                        }
                    }
                }

                public void onHeaderOutput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        for (int i = 0; i < headers.size(); ++i) {
                            HEADER_LOG.debug("{} >> {}", (Object)id, (Object)headers.get(i));
                        }
                    }
                }

                public void onFrameInput(HttpConnection connection, int streamId, RawFrame frame) {
                    if (FRAME_LOG.isDebugEnabled()) {
                        this.logFrameInfo(id + " <<", frame);
                    }
                    if (FRAME_PAYLOAD_LOG.isDebugEnabled()) {
                        this.logFramePayload(id + " <<", frame);
                    }
                }

                public void onFrameOutput(HttpConnection connection, int streamId, RawFrame frame) {
                    if (FRAME_LOG.isDebugEnabled()) {
                        this.logFrameInfo(id + " >>", frame);
                    }
                    if (FRAME_PAYLOAD_LOG.isDebugEnabled()) {
                        this.logFramePayload(id + " >>", frame);
                    }
                }

                public void onInputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                    if (FLOW_CTRL_LOG.isDebugEnabled()) {
                        this.logFlowControl(id + " <<", streamId, delta, actualSize);
                    }
                }

                public void onOutputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                    if (FLOW_CTRL_LOG.isDebugEnabled()) {
                        this.logFlowControl(id + " >>", streamId, delta, actualSize);
                    }
                }
            });
        } else {
            http1StreamHandlerFactory = new ClientHttp1StreamDuplexerFactory(this.httpProcessor, this.h1Config, this.charCodingConfig, this.http1ConnectionReuseStrategy, this.http1ResponseParserFactory, this.http1RequestWriterFactory, null);
            http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor, this.exchangeHandlerFactory, this.h2Config, this.charCodingConfig, null);
        }
        ioSession.registerProtocol(ApplicationProtocol.HTTP_1_1.id, (ProtocolUpgradeHandler)new ClientHttp1UpgradeHandler(http1StreamHandlerFactory));
        ioSession.registerProtocol(ApplicationProtocol.HTTP_2.id, (ProtocolUpgradeHandler)new ClientH2UpgradeHandler(http2StreamHandlerFactory));
        HttpVersionPolicy versionPolicy = attachment instanceof HttpVersionPolicy ? (HttpVersionPolicy)attachment : HttpVersionPolicy.NEGOTIATE;
        switch (versionPolicy) {
            case FORCE_HTTP_2: {
                return new ClientH2PrefaceHandler(ioSession, http2StreamHandlerFactory, false);
            }
            case FORCE_HTTP_1: {
                return new ClientHttp1IOEventHandler(http1StreamHandlerFactory.create(ioSession));
            }
        }
        return new HttpProtocolNegotiator(ioSession, null);
    }
}

