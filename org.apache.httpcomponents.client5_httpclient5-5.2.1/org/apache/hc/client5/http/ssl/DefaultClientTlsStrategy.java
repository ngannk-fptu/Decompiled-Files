/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.function.Factory
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.reactor.ssl.SSLBufferMode
 *  org.apache.hc.core5.reactor.ssl.TlsDetails
 *  org.apache.hc.core5.ssl.SSLContexts
 */
package org.apache.hc.client5.http.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import org.apache.hc.client5.http.ssl.AbstractClientTlsStrategy;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.function.Factory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.ssl.SSLBufferMode;
import org.apache.hc.core5.reactor.ssl.TlsDetails;
import org.apache.hc.core5.ssl.SSLContexts;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultClientTlsStrategy
extends AbstractClientTlsStrategy {
    @Deprecated
    private Factory<SSLEngine, TlsDetails> tlsDetailsFactory;

    public static TlsStrategy getDefault() {
        return new DefaultClientTlsStrategy(SSLContexts.createDefault(), HttpsSupport.getDefaultHostnameVerifier());
    }

    public static TlsStrategy getSystemDefault() {
        return new DefaultClientTlsStrategy(SSLContexts.createSystemDefault(), HttpsSupport.getSystemProtocols(), HttpsSupport.getSystemCipherSuits(), SSLBufferMode.STATIC, HttpsSupport.getDefaultHostnameVerifier());
    }

    @Deprecated
    public DefaultClientTlsStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, SSLBufferMode sslBufferManagement, HostnameVerifier hostnameVerifier, Factory<SSLEngine, TlsDetails> tlsDetailsFactory) {
        super(sslContext, supportedProtocols, supportedCipherSuites, sslBufferManagement, hostnameVerifier);
        this.tlsDetailsFactory = tlsDetailsFactory;
    }

    public DefaultClientTlsStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, SSLBufferMode sslBufferManagement, HostnameVerifier hostnameVerifier) {
        super(sslContext, supportedProtocols, supportedCipherSuites, sslBufferManagement, hostnameVerifier);
    }

    public DefaultClientTlsStrategy(SSLContext sslcontext, HostnameVerifier hostnameVerifier) {
        this(sslcontext, null, null, SSLBufferMode.STATIC, hostnameVerifier);
    }

    public DefaultClientTlsStrategy(SSLContext sslcontext) {
        this(sslcontext, HttpsSupport.getDefaultHostnameVerifier());
    }

    @Override
    void applyParameters(SSLEngine sslEngine, SSLParameters sslParameters, String[] appProtocols) {
        sslParameters.setApplicationProtocols(appProtocols);
        sslEngine.setSSLParameters(sslParameters);
    }

    @Override
    TlsDetails createTlsDetails(SSLEngine sslEngine) {
        return this.tlsDetailsFactory != null ? (TlsDetails)this.tlsDetailsFactory.create((Object)sslEngine) : null;
    }
}

