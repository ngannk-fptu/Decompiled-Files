/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ServerHttpProtocolNegotiator;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ServerHttpProtocolNegotiatorFactory
implements IOEventHandlerFactory {
    private final ServerHttp1StreamDuplexerFactory http1StreamDuplexerFactory;
    private final ServerH2StreamMultiplexerFactory http2StreamMultiplexerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final TlsStrategy tlsStrategy;
    private final Timeout handshakeTimeout;

    public ServerHttpProtocolNegotiatorFactory(ServerHttp1StreamDuplexerFactory http1StreamDuplexerFactory, ServerH2StreamMultiplexerFactory http2StreamMultiplexerFactory, HttpVersionPolicy versionPolicy, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        this.http1StreamDuplexerFactory = Args.notNull(http1StreamDuplexerFactory, "HTTP/1.1 stream handler factory");
        this.http2StreamMultiplexerFactory = Args.notNull(http2StreamMultiplexerFactory, "HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.tlsStrategy = tlsStrategy;
        this.handshakeTimeout = handshakeTimeout;
    }

    @Override
    public ServerHttpProtocolNegotiator createHandler(ProtocolIOSession ioSession, Object attachment) {
        HttpVersionPolicy endpointPolicy = this.versionPolicy;
        if (attachment instanceof EndpointParameters) {
            EndpointParameters params = (EndpointParameters)attachment;
            if (URIScheme.HTTPS.same(params.getScheme())) {
                Asserts.notNull(this.tlsStrategy, "TLS strategy");
                this.tlsStrategy.upgrade(ioSession, null, ioSession.getLocalAddress(), ioSession.getRemoteAddress(), params.getAttachment(), this.handshakeTimeout);
            }
            if (params.getAttachment() instanceof HttpVersionPolicy) {
                endpointPolicy = (HttpVersionPolicy)((Object)params.getAttachment());
            }
        }
        return new ServerHttpProtocolNegotiator(ioSession, this.http1StreamDuplexerFactory, this.http2StreamMultiplexerFactory, endpointPolicy);
    }
}

