/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ClientHttpProtocolNegotiator;
import org.apache.hc.core5.reactor.EndpointParameters;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.ProtocolIOSession;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
@Internal
public class ClientHttpProtocolNegotiatorFactory
implements IOEventHandlerFactory {
    private final ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory;
    private final ClientH2StreamMultiplexerFactory http2StreamHandlerFactory;
    private final HttpVersionPolicy versionPolicy;
    private final TlsStrategy tlsStrategy;
    private final Timeout handshakeTimeout;

    public ClientHttpProtocolNegotiatorFactory(ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory, ClientH2StreamMultiplexerFactory http2StreamHandlerFactory, HttpVersionPolicy versionPolicy, TlsStrategy tlsStrategy, Timeout handshakeTimeout) {
        this.http1StreamHandlerFactory = Args.notNull(http1StreamHandlerFactory, "HTTP/1.1 stream handler factory");
        this.http2StreamHandlerFactory = Args.notNull(http2StreamHandlerFactory, "HTTP/2 stream handler factory");
        this.versionPolicy = versionPolicy != null ? versionPolicy : HttpVersionPolicy.NEGOTIATE;
        this.tlsStrategy = tlsStrategy;
        this.handshakeTimeout = handshakeTimeout;
    }

    @Override
    public ClientHttpProtocolNegotiator createHandler(ProtocolIOSession ioSession, Object attachment) {
        HttpVersionPolicy endpointPolicy = this.versionPolicy;
        if (attachment instanceof EndpointParameters) {
            EndpointParameters params = (EndpointParameters)attachment;
            if (URIScheme.HTTPS.same(params.getScheme())) {
                Asserts.notNull(this.tlsStrategy, "TLS strategy");
                HttpHost host = new HttpHost(params.getScheme(), params.getHostName(), params.getPort());
                this.tlsStrategy.upgrade(ioSession, host, ioSession.getLocalAddress(), ioSession.getRemoteAddress(), params.getAttachment(), this.handshakeTimeout);
            }
            if (params.getAttachment() instanceof HttpVersionPolicy) {
                endpointPolicy = (HttpVersionPolicy)((Object)params.getAttachment());
            }
        }
        return new ClientHttpProtocolNegotiator(ioSession, this.http1StreamHandlerFactory, this.http2StreamHandlerFactory, endpointPolicy);
    }
}

