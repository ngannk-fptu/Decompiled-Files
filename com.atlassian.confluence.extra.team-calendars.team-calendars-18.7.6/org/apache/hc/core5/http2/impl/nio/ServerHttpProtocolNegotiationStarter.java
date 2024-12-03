/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler;
import org.apache.hc.core5.http.impl.nio.ServerHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.HttpProtocolNegotiator;
import org.apache.hc.core5.http2.impl.nio.ServerH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ServerH2UpgradeHandler;
import org.apache.hc.core5.http2.impl.nio.ServerHttp1UpgradeHandler;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ServerHttpProtocolNegotiationStarter
implements IOEventHandlerFactory {
    private final ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory;
    private final ServerH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final TlsStrategy tlsStrategy;
    private final Timeout handshakeTimeout;

    public ServerHttpProtocolNegotiationStarter(ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory, ServerH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.tlsStrategy = tlsStrategy;
        this.handshakeTimeout = handshakeTimeout;
    }

    @Override
    public HttpConnectionEventHandler createHandler(ProtocolIOSession ioSession, Object attachment) {
        HttpVersionPolicy endpointPolicy = this.versionPolicy;
        URIScheme uriScheme = URIScheme.HTTP;
        if (attachment instanceof EndpointParameters) {
            EndpointParameters params = (EndpointParameters)attachment;
            if (this.tlsStrategy != null && URIScheme.HTTPS.same(params.getScheme())) {
                uriScheme = URIScheme.HTTPS;
                this.tlsStrategy.upgrade(ioSession, params, params.getAttachment(), this.handshakeTimeout, null);
            }
            if (params.getAttachment() instanceof HttpVersionPolicy) {
                endpointPolicy = (HttpVersionPolicy)((Object)params.getAttachment());
            }
        }
        ioSession.registerProtocol(ApplicationProtocol.HTTP_1_1.id, new ServerHttp1UpgradeHandler(this.http1StreamHandlerFactory));
        ioSession.registerProtocol(ApplicationProtocol.HTTP_2.id, new ServerH2UpgradeHandler(this.http2StreamHandlerFactory));
        switch (endpointPolicy) {
            case FORCE_HTTP_2: {
                return new ServerH2PrefaceHandler(ioSession, this.http2StreamHandlerFactory);
            }
            case FORCE_HTTP_1: {
                return new ServerHttp1IOEventHandler(this.http1StreamHandlerFactory.create(uriScheme.id, ioSession));
            }
        }
        return new HttpProtocolNegotiator(ioSession, null);
    }
}

