/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.RequestHeaderFieldsTooLargeException
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncServerExchangeHandler
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.command.ExecutableCommand
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.RequestHeaderFieldsTooLargeException;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ExecutableCommand;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.DefaultFrameFactory;
import org.apache.hc.core5.http2.frame.FrameFactory;
import org.apache.hc.core5.http2.frame.StreamIdGenerator;
import org.apache.hc.core5.http2.hpack.HeaderListConstraintException;
import org.apache.hc.core5.http2.impl.nio.AbstractH2StreamMultiplexer;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamHandler;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;

@Internal
public class ServerH2StreamMultiplexer
extends AbstractH2StreamMultiplexer {
    private final HandlerFactory<AsyncServerExchangeHandler> exchangeHandlerFactory;

    public ServerH2StreamMultiplexer(ProtocolIOSession ioSession, FrameFactory frameFactory, HttpProcessor httpProcessor, HandlerFactory<AsyncServerExchangeHandler> exchangeHandlerFactory, CharCodingConfig charCodingConfig, H2Config h2Config, H2StreamListener streamListener) {
        super(ioSession, frameFactory, StreamIdGenerator.EVEN, httpProcessor, charCodingConfig, h2Config, streamListener);
        this.exchangeHandlerFactory = (HandlerFactory)Args.notNull(exchangeHandlerFactory, (String)"Handler factory");
    }

    public ServerH2StreamMultiplexer(ProtocolIOSession ioSession, HttpProcessor httpProcessor, HandlerFactory<AsyncServerExchangeHandler> exchangeHandlerFactory, CharCodingConfig charCodingConfig, H2Config h2Config) {
        this(ioSession, DefaultFrameFactory.INSTANCE, httpProcessor, exchangeHandlerFactory, charCodingConfig, h2Config, null);
    }

    @Override
    void acceptHeaderFrame() throws H2ConnectionException {
    }

    @Override
    void acceptPushRequest() throws H2ConnectionException {
    }

    @Override
    void acceptPushFrame() throws H2ConnectionException {
        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Push not supported");
    }

    @Override
    H2StreamHandler createRemotelyInitiatedStream(H2StreamChannel channel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, HandlerFactory<AsyncPushConsumer> pushHandlerFactory) throws IOException {
        HttpCoreContext context = HttpCoreContext.create();
        context.setAttribute("http.ssl-session", (Object)this.getSSLSession());
        context.setAttribute("http.connection-endpoint", (Object)this.getEndpointDetails());
        return new ServerH2StreamHandler(channel, httpProcessor, connMetrics, this.exchangeHandlerFactory, context);
    }

    @Override
    H2StreamHandler createLocallyInitiatedStream(ExecutableCommand command, H2StreamChannel channel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics) throws IOException {
        throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Illegal attempt to execute a request");
    }

    @Override
    List<Header> decodeHeaders(ByteBuffer payload) throws HttpException {
        try {
            return super.decodeHeaders(payload);
        }
        catch (HeaderListConstraintException ex) {
            throw new RequestHeaderFieldsTooLargeException(ex.getMessage(), (Throwable)((Object)ex));
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        this.appendState(buf);
        buf.append("]");
        return buf.toString();
    }
}

