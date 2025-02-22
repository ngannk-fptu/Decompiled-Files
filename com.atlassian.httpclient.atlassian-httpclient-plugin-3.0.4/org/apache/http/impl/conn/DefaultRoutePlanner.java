/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.conn;

import java.net.InetAddress;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(threading=ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultRoutePlanner
implements HttpRoutePlanner {
    private final SchemePortResolver schemePortResolver;

    public DefaultRoutePlanner(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
    }

    @Override
    public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
        HttpHost target;
        Args.notNull(request, "Request");
        if (host == null) {
            throw new ProtocolException("Target host is not specified");
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        RequestConfig config = clientContext.getRequestConfig();
        InetAddress local = config.getLocalAddress();
        HttpHost proxy = config.getProxy();
        if (proxy == null) {
            proxy = this.determineProxy(host, request, context);
        }
        if (host.getPort() <= 0) {
            try {
                target = new HttpHost(host.getHostName(), this.schemePortResolver.resolve(host), host.getSchemeName());
            }
            catch (UnsupportedSchemeException ex) {
                throw new HttpException(ex.getMessage());
            }
        } else {
            target = host;
        }
        boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        return proxy == null ? new HttpRoute(target, local, secure) : new HttpRoute(target, local, proxy, secure);
    }

    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        return null;
    }
}

