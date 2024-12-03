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
import org.apache.hc.core5.http2.ssl.H2TlsSupport;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.SSLSessionInitializer;
import org.apache.hc.core5.reactor.ssl.SSLSessionVerifier;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

public class H2ServerTlsStrategy
implements TlsStrategy {
    private final SSLContext sslContext;
    private final SecurePortStrategy securePortStrategy;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    @Deprecated
    public H2ServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.securePortStrategy = securePortStrategy;
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    @Deprecated
    public H2ServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, initializer, verifier);
    }

    @Deprecated
    public H2ServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy, SSLSessionVerifier verifier) {
        this(sslContext, securePortStrategy, null, null, verifier);
    }

    @Deprecated
    public H2ServerTlsStrategy(SSLContext sslContext, SecurePortStrategy securePortStrategy) {
        this(sslContext, securePortStrategy, null, null, null);
    }

    @Deprecated
    public H2ServerTlsStrategy(int ... securePorts) {
        this(SSLContexts.createSystemDefault(), (SecurePortStrategy)new FixedPortStrategy(securePorts));
    }

    public H2ServerTlsStrategy(SSLContext sslContext, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
        this.securePortStrategy = null;
    }

    public H2ServerTlsStrategy(SSLContext sslContext, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, initializer, verifier);
    }

    public H2ServerTlsStrategy(SSLContext sslContext, SSLSessionVerifier verifier) {
        this(sslContext, (SSLBufferMode)null, null, verifier);
    }

    public H2ServerTlsStrategy(SSLContext sslContext) {
        this(sslContext, (SSLBufferMode)null, null, null);
    }

    public H2ServerTlsStrategy() {
        this(SSLContexts.createSystemDefault());
    }

    private boolean isApplicable(SocketAddress localAddress) {
        return this.securePortStrategy == null || this.securePortStrategy.isSecure(localAddress);
    }

    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferMode, H2TlsSupport.enforceRequirements(attachment, this.initializer), this.verifier, handshakeTimeout, callback);
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

