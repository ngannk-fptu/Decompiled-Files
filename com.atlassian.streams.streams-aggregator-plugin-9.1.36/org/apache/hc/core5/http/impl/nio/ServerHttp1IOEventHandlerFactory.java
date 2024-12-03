/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.ServerHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandler;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class ServerHttp1IOEventHandlerFactory
implements IOEventHandlerFactory {
    private final ServerHttp1StreamDuplexerFactory streamDuplexerFactory;
    private final TlsStrategy tlsStrategy;
    private final Timeout handshakeTimeout;

    public ServerHttp1IOEventHandlerFactory(ServerHttp1StreamDuplexerFactory streamDuplexerFactory, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        this.streamDuplexerFactory = Args.notNull(streamDuplexerFactory, "Stream duplexer factory");
        this.tlsStrategy = tlsStrategy;
        this.handshakeTimeout = handshakeTimeout;
    }

    @Override
    public IOEventHandler createHandler(ProtocolIOSession ioSession, Object attachment) {
        String endpointScheme = URIScheme.HTTP.id;
        if (attachment instanceof EndpointParameters) {
            EndpointParameters params = (EndpointParameters)attachment;
            endpointScheme = params.getScheme();
            if (this.tlsStrategy != null && URIScheme.HTTPS.same(endpointScheme)) {
                this.tlsStrategy.upgrade(ioSession, null, ioSession.getLocalAddress(), ioSession.getRemoteAddress(), params.getAttachment(), this.handshakeTimeout);
            }
        } else if (this.tlsStrategy != null) {
            this.tlsStrategy.upgrade(ioSession, null, ioSession.getLocalAddress(), ioSession.getRemoteAddress(), attachment, this.handshakeTimeout);
        }
        return new ServerHttp1IOEventHandler(this.streamDuplexerFactory.create(endpointScheme, ioSession));
    }
}

