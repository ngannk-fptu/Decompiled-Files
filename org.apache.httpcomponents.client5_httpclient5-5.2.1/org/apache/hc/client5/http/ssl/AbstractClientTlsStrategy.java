/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.ssl.TLS
 *  org.apache.hc.core5.http.ssl.TlsCiphers
 *  org.apache.hc.core5.http2.HttpVersionPolicy
 *  org.apache.hc.core5.http2.ssl.ApplicationProtocol
 *  org.apache.hc.core5.http2.ssl.H2TlsSupport
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.ssl.SSLBufferMode
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.reactor.ssl.TransportSecurityLayer
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.ssl;

import java.net.SocketAddress;
import java.util.Arrays;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.client5.http.ssl.TlsSessionValidator;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.ssl.ApplicationProtocol;
import org.apache.hc.core5.http2.ssl.H2TlsSupport;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.reactor.ssl.TransportSecurityLayer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
abstract class AbstractClientTlsStrategy
implements TlsStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractClientTlsStrategy.class);
    private final SSLContext sslContext;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    private final SSLBufferMode sslBufferManagement;
    private final HostnameVerifier hostnameVerifier;
    private final TlsSessionValidator tlsSessionValidator;

    AbstractClientTlsStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, SSLBufferMode sslBufferManagement, HostnameVerifier hostnameVerifier) {
        this.sslContext = (SSLContext)Args.notNull((Object)sslContext, (String)"SSL context");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.sslBufferManagement = sslBufferManagement != null ? sslBufferManagement : SSLBufferMode.STATIC;
        this.hostnameVerifier = hostnameVerifier != null ? hostnameVerifier : HttpsSupport.getDefaultHostnameVerifier();
        this.tlsSessionValidator = new TlsSessionValidator(LOG);
    }

    @Deprecated
    public boolean upgrade(TransportSecurityLayer tlsSession, HttpHost host, SocketAddress localAddress, SocketAddress remoteAddress, Object attachment, Timeout handshakeTimeout) {
        this.upgrade(tlsSession, (NamedEndpoint)host, attachment, handshakeTimeout, null);
        return true;
    }

    public void upgrade(TransportSecurityLayer tlsSession, NamedEndpoint endpoint, Object attachment, Timeout handshakeTimeout, FutureCallback<TransportSecurityLayer> callback) {
        tlsSession.startTls(this.sslContext, endpoint, this.sslBufferManagement, (e, sslEngine) -> {
            TlsConfig tlsConfig = attachment instanceof TlsConfig ? (TlsConfig)attachment : TlsConfig.DEFAULT;
            HttpVersionPolicy versionPolicy = tlsConfig.getHttpVersionPolicy();
            SSLParameters sslParameters = sslEngine.getSSLParameters();
            String[] supportedProtocols = tlsConfig.getSupportedProtocols();
            if (supportedProtocols != null) {
                sslParameters.setProtocols(supportedProtocols);
            } else if (this.supportedProtocols != null) {
                sslParameters.setProtocols(this.supportedProtocols);
            } else if (versionPolicy != HttpVersionPolicy.FORCE_HTTP_1) {
                sslParameters.setProtocols(TLS.excludeWeak((String[])sslParameters.getProtocols()));
            }
            String[] supportedCipherSuites = tlsConfig.getSupportedCipherSuites();
            if (supportedCipherSuites != null) {
                sslParameters.setCipherSuites(supportedCipherSuites);
            } else if (this.supportedCipherSuites != null) {
                sslParameters.setCipherSuites(this.supportedCipherSuites);
            } else if (versionPolicy == HttpVersionPolicy.FORCE_HTTP_2) {
                sslParameters.setCipherSuites(TlsCiphers.excludeH2Blacklisted((String[])sslParameters.getCipherSuites()));
            }
            if (versionPolicy != HttpVersionPolicy.FORCE_HTTP_1) {
                H2TlsSupport.setEnableRetransmissions((SSLParameters)sslParameters, (boolean)false);
            }
            this.applyParameters(sslEngine, sslParameters, H2TlsSupport.selectApplicationProtocols((Object)versionPolicy));
            this.initializeEngine(sslEngine);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Enabled protocols: {}", Arrays.asList(sslEngine.getEnabledProtocols()));
                LOG.debug("Enabled cipher suites:{}", Arrays.asList(sslEngine.getEnabledCipherSuites()));
                LOG.debug("Starting handshake ({})", (Object)handshakeTimeout);
            }
        }, (e, sslEngine) -> {
            this.verifySession(endpoint.getHostName(), sslEngine.getSession());
            TlsDetails tlsDetails = this.createTlsDetails(sslEngine);
            String negotiatedCipherSuite = sslEngine.getSession().getCipherSuite();
            if (tlsDetails != null && ApplicationProtocol.HTTP_2.id.equals(tlsDetails.getApplicationProtocol()) && TlsCiphers.isH2Blacklisted((String)negotiatedCipherSuite)) {
                throw new SSLHandshakeException("Cipher suite `" + negotiatedCipherSuite + "` does not provide adequate security for HTTP/2");
            }
            return tlsDetails;
        }, handshakeTimeout, callback);
    }

    abstract void applyParameters(SSLEngine var1, SSLParameters var2, String[] var3);

    abstract TlsDetails createTlsDetails(SSLEngine var1);

    protected void initializeEngine(SSLEngine sslEngine) {
    }

    protected void verifySession(String hostname, SSLSession sslsession) throws SSLException {
        this.tlsSessionValidator.verifySession(hostname, sslsession, this.hostnameVerifier);
    }
}

