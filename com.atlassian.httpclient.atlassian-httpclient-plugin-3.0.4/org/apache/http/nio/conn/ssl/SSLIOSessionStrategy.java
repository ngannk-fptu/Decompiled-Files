/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.conn.ssl;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.auth.x500.X500Principal;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.nio.reactor.ssl.SSLMode;
import org.apache.http.nio.reactor.ssl.SSLSetupHandler;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;
import org.apache.http.util.TextUtils;

public class SSLIOSessionStrategy
implements SchemeIOSessionStrategy {
    @Deprecated
    public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
    @Deprecated
    public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
    @Deprecated
    public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
    private final SSLContext sslContext;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    private final HostnameVerifier hostnameVerifier;

    private static String[] split(String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }

    public static HostnameVerifier getDefaultHostnameVerifier() {
        return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
    }

    public static SSLIOSessionStrategy getDefaultStrategy() {
        return new SSLIOSessionStrategy(SSLContexts.createDefault(), SSLIOSessionStrategy.getDefaultHostnameVerifier());
    }

    public static SSLIOSessionStrategy getSystemDefaultStrategy() {
        return new SSLIOSessionStrategy(SSLContexts.createSystemDefault(), SSLIOSessionStrategy.split(System.getProperty("https.protocols")), SSLIOSessionStrategy.split(System.getProperty("https.cipherSuites")), SSLIOSessionStrategy.getDefaultHostnameVerifier());
    }

    @Deprecated
    public SSLIOSessionStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, X509HostnameVerifier hostnameVerifier) {
        this(sslContext, supportedProtocols, supportedCipherSuites, (HostnameVerifier)hostnameVerifier);
    }

    @Deprecated
    public SSLIOSessionStrategy(SSLContext sslcontext, X509HostnameVerifier hostnameVerifier) {
        this(sslcontext, null, null, (HostnameVerifier)hostnameVerifier);
    }

    public SSLIOSessionStrategy(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        this.sslContext = Args.notNull(sslContext, "SSL context");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.hostnameVerifier = hostnameVerifier != null ? hostnameVerifier : SSLIOSessionStrategy.getDefaultHostnameVerifier();
    }

    public SSLIOSessionStrategy(SSLContext sslcontext, HostnameVerifier hostnameVerifier) {
        this(sslcontext, null, null, hostnameVerifier);
    }

    public SSLIOSessionStrategy(SSLContext sslcontext) {
        this(sslcontext, null, null, SSLIOSessionStrategy.getDefaultHostnameVerifier());
    }

    @Override
    public SSLIOSession upgrade(final HttpHost host, IOSession ioSession) throws IOException {
        Asserts.check(!(ioSession instanceof SSLIOSession), "I/O session is already upgraded to TLS/SSL");
        SSLIOSession sslioSession = new SSLIOSession(ioSession, SSLMode.CLIENT, host, this.sslContext, new SSLSetupHandler(){

            @Override
            public void initalize(SSLEngine sslengine) throws SSLException {
                if (SSLIOSessionStrategy.this.supportedProtocols != null) {
                    sslengine.setEnabledProtocols(SSLIOSessionStrategy.this.supportedProtocols);
                }
                if (SSLIOSessionStrategy.this.supportedCipherSuites != null) {
                    sslengine.setEnabledCipherSuites(SSLIOSessionStrategy.this.supportedCipherSuites);
                }
                SSLIOSessionStrategy.this.initializeEngine(sslengine);
            }

            @Override
            public void verify(IOSession ioSession, SSLSession sslsession) throws SSLException {
                SSLIOSessionStrategy.this.verifySession(host, ioSession, sslsession);
            }
        });
        ioSession.setAttribute("http.session.ssl", sslioSession);
        sslioSession.initialize();
        return sslioSession;
    }

    protected void initializeEngine(SSLEngine engine) {
    }

    protected void verifySession(HttpHost host, IOSession ioSession, SSLSession sslsession) throws SSLException {
        if (!this.hostnameVerifier.verify(host.getHostName(), sslsession)) {
            Certificate[] certs = sslsession.getPeerCertificates();
            X509Certificate x509 = (X509Certificate)certs[0];
            X500Principal x500Principal = x509.getSubjectX500Principal();
            throw new SSLPeerUnverifiedException("Host name '" + host.getHostName() + "' does not match " + "the certificate subject provided by the peer (" + x500Principal.toString() + ")");
        }
    }

    @Override
    public boolean isLayeringRequired() {
        return true;
    }
}

