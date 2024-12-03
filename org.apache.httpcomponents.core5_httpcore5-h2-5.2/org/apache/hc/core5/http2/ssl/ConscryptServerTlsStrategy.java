/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.nio.ssl.FixedPortStrategy
 *  org.apache.hc.core5.http.nio.ssl.SecurePortStrategy
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.ssl.SSLBufferMode
 *  org.apache.hc.core5.reactor.ssl.SSLSessionInitializer
 *  org.apache.hc.core5.reactor.ssl.SSLSessionVerifier
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.ssl.SSLContexts
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.ssl;

import java.net.SocketAddress;
import javax.net.ssl.SSLContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.FixedPortStrategy;
import org.apache.hc.core5.http.nio.ssl.SecurePortStrategy;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http2.ssl.ConscryptSupport;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

public class ConscryptServerTlsStrategy
implements TlsStrategy {
    private final SSLContext sslContext;
    private final SecurePortStrategy securePortStrategy;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    @Deprecated
    public ConscryptServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.securePortStrategy = securePortStrategy;
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    @Deprecated
    public ConscryptServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, initializer, verifier);
    }

    @Deprecated
    public ConscryptServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, null, verifier);
    }

    @Deprecated
    public ConscryptServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy) {
        this(sslContext, securePortStrategy, null, null, null);
    }

    @Deprecated
    public ConscryptServerTlsStrategy(SSLContext sslContext, int ... securePorts) {
        this(sslContext, (SecurePortStrategy)new FixedPortStrategy(securePorts));
    }

    public ConscryptServerTlsStrategy(SSLContext sslContext, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
        this.securePortStrategy = null;
    }

    public ConscryptServerTlsStrategy(SSLContext sslContext, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, initializer, verifier);
    }

    public ConscryptServerTlsStrategy(SSLContext sslContext, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, null, verifier);
    }

    public ConscryptServerTlsStrategy(SSLContext sslContext) {
        this(sslContext, (SSLBufferMode)null, null, null);
    }

    public ConscryptServerTlsStrategy() {
        this(SSLContexts.createSystemDefault(), (SSLBufferMode)null, null, null);
    }

    public ConscryptServerTlsStrategy(SSLSessionVerifier verifier) {
        this(SSLContexts.createSystemDefault(), (SSLBufferMode)null, null, verifier);
    }

    private boolean isApplicable(SocketAddress localAddress) {
        return this.securePortStrategy == null || this.securePortStrategy.isSecure(localAddress);
    }

    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferMode, ConscryptSupport.initialize(attachment, this.initializer), ConscryptSupport.verify(this.verifier), handshakeTimeout, callback);
    }

    @Deprecated
    public boolean upgrade(TransportSecurityLayer tlsSession, HttpHost host, SocketAddress localAddress, SocketAddress remoteAddress, Object attachment, Timeout handshakeTimeout) {
        if (this.isApplicable(localAddress)) {
            this.upgrade(tlsSession, (NamedEndpoint)host, attachment, handshakeTimeout, null);
            return true;
        }
        return false;
    }
}

