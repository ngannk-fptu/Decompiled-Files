/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.ssl.TLS
 *  org.apache.hc.core5.http.ssl.TlsCiphers
 *  org.apache.hc.core5.io.Closer
 *  org.apache.hc.core5.ssl.SSLContexts
 *  org.apache.hc.core5.ssl.SSLInitializationException
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Asserts
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.ssl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.HttpsSupport;
import org.apache.hc.client5.http.ssl.TlsSessionValidator;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.http.ssl.TlsCiphers;
import org.apache.hc.core5.io.Closer;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.SSLInitializationException;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.STATELESS)
public class SSLConnectionSocketFactory
implements LayeredConnectionSocketFactory {
    private static final String WEAK_KEY_EXCHANGES = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
    private static final String WEAK_CIPHERS = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";
    private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList(Arrays.asList(Pattern.compile("^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)", 2), Pattern.compile("^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)", 2)));
    private static final Logger LOG = LoggerFactory.getLogger(SSLConnectionSocketFactory.class);
    private final SSLSocketFactory socketFactory;
    private final HostnameVerifier hostnameVerifier;
    private final String[] supportedProtocols;
    private final String[] supportedCipherSuites;
    private final TlsSessionValidator tlsSessionValidator;

    public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory(SSLContexts.createDefault(), HttpsSupport.getDefaultHostnameVerifier());
    }

    public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
        return new SSLConnectionSocketFactory((SSLSocketFactory)SSLSocketFactory.getDefault(), HttpsSupport.getSystemProtocols(), HttpsSupport.getSystemCipherSuits(), HttpsSupport.getDefaultHostnameVerifier());
    }

    static boolean isWeakCipherSuite(String cipherSuite) {
        for (Pattern pattern : WEAK_CIPHER_SUITE_PATTERNS) {
            if (!pattern.matcher(cipherSuite).matches()) continue;
            return true;
        }
        return false;
    }

    public SSLConnectionSocketFactory(SSLContext sslContext) {
        this(sslContext, HttpsSupport.getDefaultHostnameVerifier());
    }

    public SSLConnectionSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, (String)"SSL context")).getSocketFactory(), null, null, hostnameVerifier);
    }

    public SSLConnectionSocketFactory(SSLContext sslContext, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        this(((SSLContext)Args.notNull((Object)sslContext, (String)"SSL context")).getSocketFactory(), supportedProtocols, supportedCipherSuites, hostnameVerifier);
    }

    public SSLConnectionSocketFactory(SSLSocketFactory socketFactory, HostnameVerifier hostnameVerifier) {
        this(socketFactory, null, null, hostnameVerifier);
    }

    public SSLConnectionSocketFactory(SSLSocketFactory socketFactory, String[] supportedProtocols, String[] supportedCipherSuites, HostnameVerifier hostnameVerifier) {
        this.socketFactory = (SSLSocketFactory)Args.notNull((Object)socketFactory, (String)"SSL socket factory");
        this.supportedProtocols = supportedProtocols;
        this.supportedCipherSuites = supportedCipherSuites;
        this.hostnameVerifier = hostnameVerifier != null ? hostnameVerifier : HttpsSupport.getDefaultHostnameVerifier();
        this.tlsSessionValidator = new TlsSessionValidator(LOG);
    }

    protected void prepareSocket(SSLSocket socket) throws IOException {
    }

    @Override
    public Socket createSocket(HttpContext context) throws IOException {
        return SocketFactory.getDefault().createSocket();
    }

    @Override
    public Socket connectSocket(TimeValue connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        Timeout timeout = connectTimeout != null ? Timeout.of((long)connectTimeout.getDuration(), (TimeUnit)connectTimeout.getTimeUnit()) : null;
        return this.connectSocket(socket, host, remoteAddress, localAddress, timeout, timeout, context);
    }

    @Override
    public Socket connectSocket(Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, Timeout connectTimeout, Object attachment, HttpContext context) throws IOException {
        Socket sock;
        Args.notNull((Object)host, (String)"HTTP host");
        Args.notNull((Object)remoteAddress, (String)"Remote address");
        Socket socket2 = sock = socket != null ? socket : this.createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Connecting socket to {} with timeout {}", (Object)remoteAddress, (Object)connectTimeout);
            }
            try {
                AccessController.doPrivileged(() -> {
                    sock.connect(remoteAddress, Timeout.defaultsToDisabled((Timeout)connectTimeout).toMillisecondsIntBound());
                    return null;
                });
            }
            catch (PrivilegedActionException e) {
                Asserts.check((boolean)(e.getCause() instanceof IOException), (String)("method contract violation only checked exceptions are wrapped: " + e.getCause()));
                throw (IOException)e.getCause();
            }
        }
        catch (IOException ex) {
            Closer.closeQuietly((Closeable)sock);
            throw ex;
        }
        if (sock instanceof SSLSocket) {
            SSLSocket sslsock = (SSLSocket)sock;
            this.executeHandshake(sslsock, host.getHostName(), attachment);
            return sock;
        }
        return this.createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), attachment, context);
    }

    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
        return this.createLayeredSocket(socket, target, port, null, context);
    }

    @Override
    public Socket createLayeredSocket(Socket socket, String target, int port, Object attachment, HttpContext context) throws IOException {
        SSLSocket sslsock = (SSLSocket)this.socketFactory.createSocket(socket, target, port, true);
        this.executeHandshake(sslsock, target, attachment);
        return sslsock;
    }

    private void executeHandshake(SSLSocket sslsock, String target, Object attachment) throws IOException {
        TlsConfig tlsConfig;
        TlsConfig tlsConfig2 = tlsConfig = attachment instanceof TlsConfig ? (TlsConfig)attachment : TlsConfig.DEFAULT;
        if (this.supportedProtocols != null) {
            sslsock.setEnabledProtocols(this.supportedProtocols);
        } else {
            sslsock.setEnabledProtocols(TLS.excludeWeak((String[])sslsock.getEnabledProtocols()));
        }
        if (this.supportedCipherSuites != null) {
            sslsock.setEnabledCipherSuites(this.supportedCipherSuites);
        } else {
            sslsock.setEnabledCipherSuites(TlsCiphers.excludeWeak((String[])sslsock.getEnabledCipherSuites()));
        }
        Timeout handshakeTimeout = tlsConfig.getHandshakeTimeout();
        if (handshakeTimeout != null) {
            sslsock.setSoTimeout(handshakeTimeout.toMillisecondsIntBound());
        }
        this.prepareSocket(sslsock);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Enabled protocols: {}", (Object)sslsock.getEnabledProtocols());
            LOG.debug("Enabled cipher suites: {}", (Object)sslsock.getEnabledCipherSuites());
            LOG.debug("Starting handshake ({})", (Object)handshakeTimeout);
        }
        sslsock.startHandshake();
        this.verifyHostname(sslsock, target);
    }

    private void verifyHostname(SSLSocket sslsock, String hostname) throws IOException {
        try {
            SSLSession session = sslsock.getSession();
            if (session == null) {
                InputStream in = sslsock.getInputStream();
                in.available();
                session = sslsock.getSession();
                if (session == null) {
                    sslsock.startHandshake();
                    session = sslsock.getSession();
                }
            }
            if (session == null) {
                throw new SSLHandshakeException("SSL session not available");
            }
            this.verifySession(hostname, session);
        }
        catch (IOException iox) {
            Closer.closeQuietly((Closeable)sslsock);
            throw iox;
        }
    }

    protected void verifySession(String hostname, SSLSession sslSession) throws SSLException {
        this.tlsSessionValidator.verifySession(hostname, sslSession, this.hostnameVerifier);
    }
}

