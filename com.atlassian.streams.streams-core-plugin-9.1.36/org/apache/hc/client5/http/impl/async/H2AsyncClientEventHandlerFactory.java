/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import java.io.IOException;
import java.util.List;
import org.apache.hc.client5.http.impl.async.LogAppendable;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.FramePrinter;
import org.apache.hc.core5.http2.frame.RawFrame;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.H2OnlyClientProtocolNegotiator;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class H2AsyncClientEventHandlerFactory
implements IOEventHandlerFactory {
    private static final Logger HEADER_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http.headers");
    private static final Logger FRAME_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.frame");
    private static final Logger FRAME_PAYLOAD_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.frame.payload");
    private static final Logger FLOW_CTRL_LOG = LoggerFactory.getLogger((String)"org.apache.hc.client5.http2.flow");
    private final HttpProcessor httpProcessor;
    private final HandlerFactory<AsyncPushConsumer> exchangeHandlerFactory;
    private final H2Config h2Config;
    private final CharCodingConfig charCodingConfig;

    H2AsyncClientEventHandlerFactory(HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> exchangeHandlerFactory, H2Config h2Config, CharCodingConfig charCodingConfig) {
        this.httpProcessor = Args.notNull(httpProcessor, "HTTP processor");
        this.exchangeHandlerFactory = exchangeHandlerFactory;
        this.h2Config = h2Config != null ? h2Config : H2Config.DEFAULT;
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
    }

    @Override
    public IOEventHandler createHandler(ProtocolIOSession ioSession, Object attachment) {
        if (HEADER_LOG.isDebugEnabled() || FRAME_LOG.isDebugEnabled() || FRAME_PAYLOAD_LOG.isDebugEnabled() || FLOW_CTRL_LOG.isDebugEnabled()) {
            final String id = ioSession.getId();
            ClientH2StreamMultiplexerFactory http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor, this.exchangeHandlerFactory, this.h2Config, this.charCodingConfig, new H2StreamListener(){
                final FramePrinter framePrinter = new FramePrinter();

                private void logFrameInfo(String prefix, RawFrame frame) {
                    try {
                        LogAppendable logAppendable = new LogAppendable(FRAME_LOG, prefix);
                        this.framePrinter.printFrameInfo(frame, logAppendable);
                        logAppendable.flush();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }

                private void logFramePayload(String prefix, RawFrame frame) {
                    try {
                        LogAppendable logAppendable = new LogAppendable(FRAME_PAYLOAD_LOG, prefix);
                        this.framePrinter.printPayload(frame, logAppendable);
                        logAppendable.flush();
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }

                private void logFlowControl(String prefix, int streamId, int delta, int actualSize) {
                    FLOW_CTRL_LOG.debug("{} stream {} flow control {} -> {}", new Object[]{prefix, streamId, delta, actualSize});
                }

                @Override
                public void onHeaderInput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        for (int i = 0; i < headers.size(); ++i) {
                            HEADER_LOG.debug("{} << {}", (Object)id, (Object)headers.get(i));
                        }
                    }
                }

                @Override
                public void onHeaderOutput(HttpConnection connection, int streamId, List<? extends Header> headers) {
                    if (HEADER_LOG.isDebugEnabled()) {
                        for (int i = 0; i < headers.size(); ++i) {
                            HEADER_LOG.debug("{} >> {}", (Object)id, (Object)headers.get(i));
                        }
                    }
                }

                @Override
                public void onFrameInput(HttpConnection connection, int streamId, RawFrame frame) {
                    if (FRAME_LOG.isDebugEnabled()) {
                        this.logFrameInfo(id + " <<", frame);
                    }
                    if (FRAME_PAYLOAD_LOG.isDebugEnabled()) {
                        this.logFramePayload(id + " <<", frame);
                    }
                }

                @Override
                public void onFrameOutput(HttpConnection connection, int streamId, RawFrame frame) {
                    if (FRAME_LOG.isDebugEnabled()) {
                        this.logFrameInfo(id + " >>", frame);
                    }
                    if (FRAME_PAYLOAD_LOG.isDebugEnabled()) {
                        this.logFramePayload(id + " >>", frame);
                    }
                }

                @Override
                public void onInputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                    if (FLOW_CTRL_LOG.isDebugEnabled()) {
                        this.logFlowControl(id + " <<", streamId, delta, actualSize);
                    }
                }

                @Override
                public void onOutputFlowControl(HttpConnection connection, int streamId, int delta, int actualSize) {
                    if (FLOW_CTRL_LOG.isDebugEnabled()) {
                        this.logFlowControl(id + " >>", streamId, delta, actualSize);
                    }
                }
            });
            return new H2OnlyClientProtocolNegotiator(ioSession, http2StreamHandlerFactory, false);
        }
        ClientH2StreamMultiplexerFactory http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor, this.exchangeHandlerFactory, this.h2Config, this.charCodingConfig, null);
        return new H2OnlyClientProtocolNegotiator(ioSession, http2StreamHandlerFactory, false);
    }
}

