/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.impl;

import java.security.Principal;
import javax.net.ssl.SSLSession;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

@Contract(threading=ThreadingBehavior.STATELESS)
public class DefaultUserTokenHandler
implements UserTokenHandler {
    public static final DefaultUserTokenHandler INSTANCE = new DefaultUserTokenHandler();

    @Override
    public Object getUserToken(HttpRoute route, HttpContext context) {
        return this.getUserToken(route, null, context);
    }

    @Override
    public Object getUserToken(HttpRoute route, HttpRequest request, HttpContext context) {
        Principal authPrincipal;
        AuthExchange proxyAuthExchange;
        Principal authPrincipal2;
        HttpHost target;
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        AuthExchange targetAuthExchange = clientContext.getAuthExchange(target = request != null ? new HttpHost(request.getScheme(), request.getAuthority()) : route.getTargetHost());
        if (targetAuthExchange != null && (authPrincipal2 = DefaultUserTokenHandler.getAuthPrincipal(targetAuthExchange)) != null) {
            return authPrincipal2;
        }
        HttpHost proxy = route.getProxyHost();
        if (proxy != null && (proxyAuthExchange = clientContext.getAuthExchange(proxy)) != null && (authPrincipal = DefaultUserTokenHandler.getAuthPrincipal(proxyAuthExchange)) != null) {
            return authPrincipal;
        }
        SSLSession sslSession = clientContext.getSSLSession();
        if (sslSession != null) {
            return sslSession.getLocalPrincipal();
        }
        return null;
    }

    private static Principal getAuthPrincipal(AuthExchange authExchange) {
        AuthScheme scheme = authExchange.getAuthScheme();
        if (scheme != null && scheme.isConnectionBased()) {
            return scheme.getPrincipal();
        }
        return null;
    }
}

