/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.ssl;

import java.net.SocketAddress;
import javax.net.ssl.SSLContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.SecurePortStrategy;
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

public class BasicServerTlsStrategy
implements TlsStrategy {
    private final SSLContext sslContext;
    private final SecurePortStrategy securePortStrategy;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    @Deprecated
    public BasicServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = Args.notNull(sslContext, "SSL context");
        this.securePortStrategy = securePortStrategy;
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    @Deprecated
    public BasicServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, initializer, verifier);
    }

    @Deprecated
    public BasicServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, null, verifier);
    }

    @Deprecated
    public BasicServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy) {
        this(sslContext, securePortStrategy, null, null, null);
    }

    @Deprecated
    public BasicServerTlsStrategy(SecurePortStrategy securePortStrategy) {
        this(SSLContexts.createSystemDefault(), securePortStrategy);
    }

    public BasicServerTlsStrategy(SSLSessionVerifier verifier) {
        this(SSLContexts.createSystemDefault(), verifier);
    }

    public BasicServerTlsStrategy(SSLContext sslContext, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = Args.notNull(sslContext, "SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
        this.securePortStrategy = null;
    }

    public BasicServerTlsStrategy(SSLContext sslContext, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, initializer, verifier);
    }

    public BasicServerTlsStrategy(SSLContext sslContext, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, null, verifier);
    }

    public BasicServerTlsStrategy(SSLContext sslContext) {
        this(sslContext, null, null, null, null);
    }

    public BasicServerTlsStrategy() {
        this(SSLContexts.createSystemDefault());
    }

    private boolean isApplicable(SocketAddress localAddress) {
        return this.securePortStrategy == null || this.securePortStrategy.isSecure(localAddress);
    }

    @Override
    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferMode, TlsSupport.enforceStrongSecurity(this.initializer), this.verifier, handshakeTimeout, callback);
    }

    @Override
    @Deprecated
    public boolean upgrade(TransportSecurityLayer tlsSession, HttpHost host, SocketAddress localAddress, SocketAddress remoteAddress, Object attachment, Timeout handshakeTimeout) {
        if (this.isApplicable(localAddress)) {
            this.upgrade(tlsSession, host, attachment, handshakeTimeout, null);
            return true;
        }
        return false;
    }
}

