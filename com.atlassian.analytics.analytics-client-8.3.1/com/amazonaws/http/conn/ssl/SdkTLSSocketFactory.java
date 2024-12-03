/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpHost
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 *  org.apache.http.protocol.HttpContext
 */
package com.amazonaws.http.conn.ssl;

import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.http.apache.utils.HttpContextUtils;
import com.amazonaws.http.conn.ssl.MasterSecretValidators;
import com.amazonaws.http.conn.ssl.ShouldClearSslSessionPredicate;
import com.amazonaws.http.conn.ssl.TLSProtocol;
import com.amazonaws.internal.SdkMetricsSocket;
import com.amazonaws.internal.SdkSSLMetricsSocket;
import com.amazonaws.internal.SdkSSLSocket;
import com.amazonaws.internal.SdkSocket;
import com.amazonaws.metrics.AwsSdkMetrics;
import com.amazonaws.util.JavaVersionParser;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

@ThreadSafe
public class SdkTLSSocketFactory
extends SSLConnectionSocketFactory {
    private static final Log LOG = LogFactory.getLog(SdkTLSSocketFactory.class);
    private final SSLContext sslContext;
    private final MasterSecretValidators.MasterSecretValidator masterSecretValidator;
    private final ShouldClearSslSessionPredicate shouldClearSslSessionsPredicate;

    public SdkTLSSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        super(sslContext, hostnameVerifier);
        if (sslContext == null) {
            throw new IllegalArgumentException("sslContext must not be null. Use SSLContext.getDefault() if you are unsure.");
        }
        this.sslContext = sslContext;
        this.masterSecretValidator = MasterSecretValidators.getMasterSecretValidator();
        this.shouldClearSslSessionsPredicate = new ShouldClearSslSessionPredicate(JavaVersionParser.getCurrentJavaVersion());
    }

    public Socket createSocket(HttpContext ctx) throws IOException {
        if (HttpContextUtils.disableSocketProxy(ctx)) {
            return new Socket(Proxy.NO_PROXY);
        }
        return super.createSocket(ctx);
    }

    protected final void prepareSocket(SSLSocket socket) {
        Object[] supported = socket.getSupportedProtocols();
        Object[] enabled = socket.getEnabledProtocols();
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("socket.getSupportedProtocols(): " + Arrays.toString(supported) + ", socket.getEnabledProtocols(): " + Arrays.toString(enabled)));
        }
        ArrayList<Object> target = new ArrayList<Object>();
        if (supported != null) {
            TLSProtocol[] tLSProtocolArray = TLSProtocol.values();
            for (int i = 0; i < tLSProtocolArray.length; ++i) {
                String pname = tLSProtocolArray[i].getProtocolName();
                if (!this.existsIn(pname, (String[])supported)) continue;
                target.add(pname);
            }
        }
        if (enabled != null) {
            for (Object object : enabled) {
                if (target.contains(object)) continue;
                target.add(object);
            }
        }
        if (target.size() > 0) {
            Object[] objectArray = target.toArray(new String[target.size()]);
            socket.setEnabledProtocols((String[])objectArray);
            if (LOG.isDebugEnabled()) {
                LOG.debug((Object)("TLS protocol enabled for SSL handshake: " + Arrays.toString(objectArray)));
            }
        }
    }

    private boolean existsIn(String element, String[] a) {
        for (String s : a) {
            if (!element.equals(s)) continue;
            return true;
        }
        return false;
    }

    public Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
        Socket connectedSocket;
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)("connecting to " + remoteAddress.getAddress() + ":" + remoteAddress.getPort()));
        }
        try {
            connectedSocket = super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
            if (!this.masterSecretValidator.isMasterSecretValid(connectedSocket)) {
                throw this.log(new IllegalStateException("Invalid SSL master secret"));
            }
        }
        catch (SSLException sslEx) {
            if (this.shouldClearSslSessionsPredicate.test(sslEx)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug((Object)"connection failed due to SSL error, clearing TLS session cache", (Throwable)sslEx);
                }
                this.clearSessionCache(this.sslContext.getClientSessionContext(), remoteAddress);
            }
            throw sslEx;
        }
        if (connectedSocket instanceof SSLSocket) {
            SdkSSLSocket sslSocket = new SdkSSLSocket((SSLSocket)connectedSocket);
            return AwsSdkMetrics.isHttpSocketReadMetricEnabled() ? new SdkSSLMetricsSocket(sslSocket) : sslSocket;
        }
        SdkSocket sdkSocket = new SdkSocket(connectedSocket);
        return AwsSdkMetrics.isHttpSocketReadMetricEnabled() ? new SdkMetricsSocket(sdkSocket) : sdkSocket;
    }

    private void clearSessionCache(SSLSessionContext sessionContext, InetSocketAddress remoteAddress) {
        String hostName = remoteAddress.getHostName();
        int port = remoteAddress.getPort();
        Enumeration<byte[]> ids = sessionContext.getIds();
        if (ids == null) {
            return;
        }
        while (ids.hasMoreElements()) {
            byte[] id = ids.nextElement();
            SSLSession session = sessionContext.getSession(id);
            if (session == null || session.getPeerHost() == null || !session.getPeerHost().equalsIgnoreCase(hostName) || session.getPeerPort() != port) continue;
            session.invalidate();
            if (!LOG.isDebugEnabled()) continue;
            LOG.debug((Object)("Invalidated session " + session));
        }
    }

    private <T extends Throwable> T log(T t) {
        if (LOG.isDebugEnabled()) {
            LOG.debug((Object)"", t);
        }
        return t;
    }
}

