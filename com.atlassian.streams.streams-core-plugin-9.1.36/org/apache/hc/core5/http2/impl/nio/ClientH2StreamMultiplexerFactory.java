/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.DefaultFrameFactory;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexer;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public final class ClientH2StreamMultiplexerFactory {
    private final HttpProcessor httpProcessor;
    private final HandlerFactory<AsyncPushConsumer> pushHandlerFactory;
    private final H2Config h2Config;
    private final CharCodingConfig charCodingConfig;
    private final H2StreamListener streamListener;

    public ClientH2StreamMultiplexerFactory(HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, H2Config h2Config, CharCodingConfig charCodingConfig, H2StreamListener streamListener) {
        this.httpProcessor = Args.notNull(httpProcessor, "HTTP processor");
        this.pushHandlerFactory = pushHandlerFactory;
        this.h2Config = h2Config != null ? h2Config : H2Config.DEFAULT;
        this.charCodingConfig = charCodingConfig != null ? charCodingConfig : CharCodingConfig.DEFAULT;
        this.streamListener = streamListener;
    }

    public ClientH2StreamMultiplexerFactory(HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, H2StreamListener streamListener) {
        this(httpProcessor, pushHandlerFactory, null, null, streamListener);
    }

    public ClientH2StreamMultiplexerFactory(HttpProcessor httpProcessor, H2StreamListener streamListener) {
        this(httpProcessor, null, streamListener);
    }

    public ClientH2StreamMultiplexer create(ProtocolIOSession ioSession) {
        return new ClientH2StreamMultiplexer(ioSession, DefaultFrameFactory.INSTANCE, this.httpProcessor, this.pushHandlerFactory, this.h2Config, this.charCodingConfig, this.streamListener);
    }
}

