/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.URIScheme
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory
 *  org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.EndpointParameters
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.ProtocolIOSession
 *  org.apache.hc.core5.reactor.ProtocolUpgradeHandler
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.ClientHttp1IOEventHandler;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.impl.nio.HttpConnectionEventHandler;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ClientH2UpgradeHandler;
import org.apache.hc.core5.http2.impl.nio.ClientHttp1UpgradeHandler;
import org.apache.hc.core5.http2.impl.nio.HttpProtocolNegotiator;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.reactor.ProtocolUpgradeHandler;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientHttpProtocolNegotiationStarter
implements IOEventHandlerFactory {
    private final ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final TlsStrategy tlsStrategy;
    private final Timeout handshakeTimeout;

    public ClientHttpProtocolNegotiationStarter(ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        this.http1StreamHandlerFactory = (ClientHttp1StreamDuplexerFactory)Args.notNull((Object)http1StreamHandlerFactory, (String)"HTTP/1.1 stream handler factory");
        this.http2StreamHandlerFactory = (ClientH2StreamMultiplexerFactory)Args.notNull((Object)http2StreamHandlerFactory, (String)"HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.tlsStrategy = tlsStrategy;
        this.handshakeTimeout = handshakeTimeout;
    }

    public HttpConnectionEventHandler createHandler(ProtocolIOSession ioSession, Object attachment) {
        HttpVersionPolicy endpointPolicy = this.versionPolicy;
        if (attachment instanceof EndpointParameters) {
            EndpointParameters params = (EndpointParameters)attachment;
            if (this.tlsStrategy != null && URIScheme.HTTPS.same(params.getScheme())) {
                this.tlsStrategy.upgrade((TransportSecurityLayer)ioSession, (NamedEndpoint)params, params.getAttachment(), this.handshakeTimeout, null);
            }
            if (params.getAttachment() instanceof HttpVersionPolicy) {
                endpointPolicy = (HttpVersionPolicy)((Object)params.getAttachment());
            }
        }
        ioSession.registerProtocol(ApplicationProtocol.HTTP_1_1.id, (ProtocolUpgradeHandler)new ClientHttp1UpgradeHandler(this.http1StreamHandlerFactory));
        ioSession.registerProtocol(ApplicationProtocol.HTTP_2.id, (ProtocolUpgradeHandler)new ClientH2UpgradeHandler(this.http2StreamHandlerFactory));
        switch (endpointPolicy) {
            case FORCE_HTTP_2: {
                return new ClientH2PrefaceHandler(ioSession, this.http2StreamHandlerFactory, false);
            }
            case FORCE_HTTP_1: {
                return new ClientHttp1IOEventHandler(this.http1StreamHandlerFactory.create(ioSession));
            }
        }
        return new HttpProtocolNegotiator(ioSession, null);
    }
}

