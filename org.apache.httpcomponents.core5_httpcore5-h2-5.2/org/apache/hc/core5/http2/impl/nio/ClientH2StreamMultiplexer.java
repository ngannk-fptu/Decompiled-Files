/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.command.ExecutableCommand
 *  org.apache.hc.core5.http.nio.command.RequestExecutionCommand
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 */
package org.apache.hc.core5.http2.impl.nio;

import java.io.IOException;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.impl.BasicHttpConnectionMetrics;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ExecutableCommand;
import org.apache.hc.core5.http.nio.command.RequestExecutionCommand;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http2.H2ConnectionException;
import org.apache.hc.core5.http2.H2Error;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.frame.DefaultFrameFactory;
import org.apache.hc.core5.http2.frame.FrameFactory;
import org.apache.hc.core5.http2.frame.StreamIdGenerator;
import org.apache.hc.core5.http2.impl.nio.AbstractH2StreamMultiplexer;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.ClientPushH2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.H2StreamChannel;
import org.apache.hc.core5.http2.impl.nio.H2StreamHandler;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.reactor.ProtocolIOSession;

@Internal
public class ClientH2StreamMultiplexer
extends AbstractH2StreamMultiplexer {
    private final HandlerFactory<AsyncPushConsumer> pushHandlerFactory;

    public ClientH2StreamMultiplexer(ProtocolIOSession ioSession, FrameFactory frameFactory, HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, H2Config h2Config, CharCodingConfig charCodingConfig, H2StreamListener streamListener) {
        super(ioSession, frameFactory, StreamIdGenerator.ODD, httpProcessor, charCodingConfig, h2Config, streamListener);
        this.pushHandlerFactory = pushHandlerFactory;
    }

    public ClientH2StreamMultiplexer(ProtocolIOSession ioSession, HttpProcessor httpProcessor, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, H2Config h2Config, CharCodingConfig charCodingConfig) {
        this(ioSession, DefaultFrameFactory.INSTANCE, httpProcessor, pushHandlerFactory, h2Config, charCodingConfig, null);
    }

    public ClientH2StreamMultiplexer(ProtocolIOSession ioSession, HttpProcessor httpProcessor, H2Config h2Config, CharCodingConfig charCodingConfig) {
        this(ioSession, httpProcessor, null, h2Config, charCodingConfig);
    }

    @Override
    void acceptHeaderFrame() throws H2ConnectionException {
        throw new H2ConnectionException(H2Error.PROTOCOL_ERROR, "Illegal HEADERS frame");
    }

    @Override
    void acceptPushFrame() throws H2ConnectionException {
    }

    @Override
    void acceptPushRequest() throws H2ConnectionException {
        throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Illegal attempt to push a response");
    }

    @Override
    H2StreamHandler createLocallyInitiatedStream(ExecutableCommand command, H2StreamChannel channel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics) throws IOException {
        if (command instanceof RequestExecutionCommand) {
            RequestExecutionCommand executionCommand = (RequestExecutionCommand)command;
            AsyncClientExchangeHandler exchangeHandler = executionCommand.getExchangeHandler();
            HandlerFactory<AsyncPushConsumer> pushHandlerFactory = executionCommand.getPushHandlerFactory();
            HttpCoreContext context = HttpCoreContext.adapt((HttpContext)executionCommand.getContext());
            context.setAttribute("http.ssl-session", (Object)this.getSSLSession());
            context.setAttribute("http.connection-endpoint", (Object)this.getEndpointDetails());
            return new ClientH2StreamHandler(channel, httpProcessor, connMetrics, exchangeHandler, pushHandlerFactory != null ? pushHandlerFactory : this.pushHandlerFactory, context);
        }
        throw new H2ConnectionException(H2Error.INTERNAL_ERROR, "Unexpected executable command");
    }

    @Override
    H2StreamHandler createRemotelyInitiatedStream(H2StreamChannel channel, HttpProcessor httpProcessor, BasicHttpConnectionMetrics connMetrics, HandlerFactory<AsyncPushConsumer> pushHandlerFactory) throws IOException {
        HttpCoreContext context = HttpCoreContext.create();
        context.setAttribute("http.ssl-session", (Object)this.getSSLSession());
        context.setAttribute("http.connection-endpoint", (Object)this.getEndpointDetails());
        return new ClientPushH2StreamHandler(channel, httpProcessor, connMetrics, pushHandlerFactory != null ? pushHandlerFactory : this.pushHandlerFactory, context);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        this.appendState(buf);
        buf.append("]");
        return buf.toString();
    }
}

