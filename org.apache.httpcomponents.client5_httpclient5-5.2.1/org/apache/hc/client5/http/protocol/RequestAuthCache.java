/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.util.Args
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.protocol;

import java.io.IOException;
import org.apache.hc.client5.http.RouteInfo;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.impl.RequestSupport;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.util.Args;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@Contract(threading=ThreadingBehavior.STATELESS)
public class RequestAuthCache
implements HttpRequestInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(RequestAuthCache.class);

    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        AuthScheme authScheme;
        AuthExchange proxyAuthExchange;
        HttpHost proxy;
        String pathPrefix;
        AuthScheme authScheme2;
        Args.notNull((Object)request, (String)"HTTP request");
        Args.notNull((Object)context, (String)"HTTP context");
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        String exchangeId = clientContext.getExchangeId();
        AuthCache authCache = clientContext.getAuthCache();
        if (authCache == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Auth cache not set in the context", (Object)exchangeId);
            }
            return;
        }
        CredentialsProvider credsProvider = clientContext.getCredentialsProvider();
        if (credsProvider == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Credentials provider not set in the context", (Object)exchangeId);
            }
            return;
        }
        RouteInfo route = clientContext.getHttpRoute();
        if (route == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Route info not set in the context", (Object)exchangeId);
            }
            return;
        }
        HttpHost target = new HttpHost(request.getScheme(), (NamedEndpoint)request.getAuthority());
        AuthExchange targetAuthExchange = clientContext.getAuthExchange(target);
        if (targetAuthExchange.getState() == AuthExchange.State.UNCHALLENGED && (authScheme2 = authCache.get(target, pathPrefix = RequestSupport.extractPathPrefix(request))) != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Re-using cached '{}' auth scheme for {}", new Object[]{exchangeId, authScheme2.getName(), target});
            }
            targetAuthExchange.select(authScheme2);
        }
        if ((proxy = route.getProxyHost()) != null && (proxyAuthExchange = clientContext.getAuthExchange(proxy)).getState() == AuthExchange.State.UNCHALLENGED && (authScheme = authCache.get(proxy, null)) != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} Re-using cached '{}' auth scheme for {}", new Object[]{exchangeId, authScheme.getName(), proxy});
            }
            proxyAuthExchange.select(authScheme);
        }
    }
}

