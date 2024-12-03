/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.http.HttpHost
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.params.HttpConnectionParams
 *  org.apache.http.params.HttpParams
 *  org.apache.http.protocol.HttpContext
 *  org.apache.http.util.Args
 *  org.apache.http.util.Asserts
 */
package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.Asserts;

@Deprecated
@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public class DefaultClientConnectionOperator
implements ClientConnectionOperator {
    private final Log log = LogFactory.getLog(this.getClass());
    protected final SchemeRegistry schemeRegistry;
    protected final DnsResolver dnsResolver;

    public DefaultClientConnectionOperator(SchemeRegistry schemes) {
        Args.notNull((Object)schemes, (String)"Scheme registry");
        this.schemeRegistry = schemes;
        this.dnsResolver = new SystemDefaultDnsResolver();
    }

    public DefaultClientConnectionOperator(SchemeRegistry schemes, DnsResolver dnsResolver) {
        Args.notNull((Object)schemes, (String)"Scheme registry");
        Args.notNull((Object)dnsResolver, (String)"DNS resolver");
        this.schemeRegistry = schemes;
        this.dnsResolver = dnsResolver;
    }

    @Override
    public OperatedClientConnection createConnection() {
        return new DefaultClientConnection();
    }

    private SchemeRegistry getSchemeRegistry(HttpContext context) {
        SchemeRegistry reg = (SchemeRegistry)context.getAttribute("http.scheme-registry");
        if (reg == null) {
            reg = this.schemeRegistry;
        }
        return reg;
    }

    @Override
    public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params) throws IOException {
        Args.notNull((Object)conn, (String)"Connection");
        Args.notNull((Object)target, (String)"Target host");
        Args.notNull((Object)params, (String)"HTTP parameters");
        Asserts.check((!conn.isOpen() ? 1 : 0) != 0, (String)"Connection must not be open");
        SchemeRegistry registry = this.getSchemeRegistry(context);
        Scheme schm = registry.getScheme(target.getSchemeName());
        SchemeSocketFactory sf = schm.getSchemeSocketFactory();
        InetAddress[] addresses = this.resolveHostname(target.getHostName());
        int port = schm.resolvePort(target.getPort());
        for (int i = 0; i < addresses.length; ++i) {
            HttpInetSocketAddress remoteAddress;
            block8: {
                InetAddress address = addresses[i];
                boolean last = i == addresses.length - 1;
                Socket sock = sf.createSocket(params);
                conn.opening(sock, target);
                remoteAddress = new HttpInetSocketAddress(target, address, port);
                InetSocketAddress localAddress = null;
                if (local != null) {
                    localAddress = new InetSocketAddress(local, 0);
                }
                if (this.log.isDebugEnabled()) {
                    this.log.debug((Object)("Connecting to " + remoteAddress));
                }
                try {
                    Socket connsock = sf.connectSocket(sock, remoteAddress, localAddress, params);
                    if (sock != connsock) {
                        sock = connsock;
                        conn.opening(sock, target);
                    }
                    this.prepareSocket(sock, context, params);
                    conn.openCompleted(sf.isSecure(sock), params);
                    return;
                }
                catch (ConnectException ex) {
                    if (last) {
                        throw ex;
                    }
                }
                catch (ConnectTimeoutException ex) {
                    if (!last) break block8;
                    throw ex;
                }
            }
            if (!this.log.isDebugEnabled()) continue;
            this.log.debug((Object)("Connect to " + remoteAddress + " timed out. " + "Connection will be retried using another IP address"));
        }
    }

    @Override
    public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params) throws IOException {
        Args.notNull((Object)conn, (String)"Connection");
        Args.notNull((Object)target, (String)"Target host");
        Args.notNull((Object)params, (String)"Parameters");
        Asserts.check((boolean)conn.isOpen(), (String)"Connection must be open");
        SchemeRegistry registry = this.getSchemeRegistry(context);
        Scheme schm = registry.getScheme(target.getSchemeName());
        Asserts.check((boolean)(schm.getSchemeSocketFactory() instanceof SchemeLayeredSocketFactory), (String)"Socket factory must implement SchemeLayeredSocketFactory");
        SchemeLayeredSocketFactory lsf = (SchemeLayeredSocketFactory)schm.getSchemeSocketFactory();
        Socket sock = lsf.createLayeredSocket(conn.getSocket(), target.getHostName(), schm.resolvePort(target.getPort()), params);
        this.prepareSocket(sock, context, params);
        conn.update(sock, target, lsf.isSecure(sock), params);
    }

    protected void prepareSocket(Socket sock, HttpContext context, HttpParams params) throws IOException {
        sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay((HttpParams)params));
        sock.setSoTimeout(HttpConnectionParams.getSoTimeout((HttpParams)params));
        int linger = HttpConnectionParams.getLinger((HttpParams)params);
        if (linger >= 0) {
            sock.setSoLinger(linger > 0, linger);
        }
    }

    protected InetAddress[] resolveHostname(String host) throws UnknownHostException {
        return this.dnsResolver.resolve(host);
    }
}

