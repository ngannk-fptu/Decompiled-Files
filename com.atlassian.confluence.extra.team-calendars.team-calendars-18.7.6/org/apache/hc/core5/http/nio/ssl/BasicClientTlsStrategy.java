/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import java.net.SocketAddress;
import javax.net.ssl.SSLContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.nio.ssl.TlsSupport;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

public class BasicClientTlsStrategy
implements TlsStrategy {
    private final SSLContext sslContext;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    public BasicClientTlsStrategy(SSLContext sslContext, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = Args.notNull(sslContext, "SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    public BasicClientTlsStrategy(SSLContext sslContext, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, null, initializer, verifier);
    }

    public BasicClientTlsStrategy(SSLContext sslContext, SSLSessionVerifier verifier) {
        this(sslContext, null, null, verifier);
    }

    public BasicClientTlsStrategy(SSLContext sslContext) {
        this(sslContext, null, null, null);
    }

    public BasicClientTlsStrategy() {
        this(SSLContexts.createSystemDefault());
    }

    public BasicClientTlsStrategy(SSLSessionVerifier verifier) {
        this(SSLContexts.createSystemDefault(), verifier);
    }

    @Override
    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferMode, TlsSupport.enforceStrongSecurity(this.initializer), this.verifier, handshakeTimeout, callback);
    }

    @Override
    @Deprecated
    public boolean upgrade(TransportSecurityLayer tlsSession, HttpHost host, SocketAddress localAddress, SocketAddress remoteAddress, Object attachment, Timeout handshakeTimeout) {
        String scheme;
        String string = scheme = host != null ? host.getSchemeName() : null;
        if (URIScheme.HTTPS.same(scheme)) {
            this.upgrade(tlsSession, host, attachment, handshakeTimeout, null);
            return true;
        }
        return false;
    }
}

