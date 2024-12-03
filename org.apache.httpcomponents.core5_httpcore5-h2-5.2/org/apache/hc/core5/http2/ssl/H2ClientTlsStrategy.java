/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.URIScheme
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
import org.apache.hc.core5.http.URIScheme;
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

public class H2ClientTlsStrategy
implements TlsStrategy {
    private final SSLContext sslContext;
    private final SSLBufferMode sslBufferMode;
    private final SSLSessionInitializer initializer;
    private final SSLSessionVerifier verifier;

    public H2ClientTlsStrategy(SSLContext sslContext, SSLBufferMode sslBufferMode, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.sslBufferMode = sslBufferMode;
        this.initializer = initializer;
        this.verifier = verifier;
    }

    public H2ClientTlsStrategy(SSLContext sslContext, SSLSessionInitializer initializer, SSLSessionVerifier verifier) {
        this(sslContext, null, initializer, verifier);
    }

    public H2ClientTlsStrategy(SSLContext sslContext, SSLSessionVerifier verifier) {
        this(sslContext, null, null, verifier);
    }

    public H2ClientTlsStrategy(SSLContext sslContext) {
        this(sslContext, null, null, null);
    }

    public H2ClientTlsStrategy() {
        this(SSLContexts.createSystemDefault());
    }

    public H2ClientTlsStrategy(SSLSessionVerifier verifier) {
        this(SSLContexts.createSystemDefault(), null, null, verifier);
    }

    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferMode, H2TlsSupport.enforceRequirements(attachment, this.initializer), this.verifier, handshakeTimeout, callback);
    }

    @Deprecated
    public boolean upgrade(TransportSecurityLayer tlsSession, HttpHost host, SocketAddress localAddress, SocketAddress remoteAddress, Object attachment, Timeout handshakeTimeout) {
        String scheme;
        String string = scheme = host != null ? host.getSchemeName() : null;
        if (URIScheme.HTTPS.same(scheme)) {
            this.upgrade(tlsSession, (NamedEndpoint)host, attachment, handshakeTimeout, null);
            return true;
        }
        return false;
    }
}

