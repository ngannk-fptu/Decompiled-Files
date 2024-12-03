/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.auth;

import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthExchange;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthStateCacheable;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
@Contract(threading=ThreadingBehavior.STATELESS)
public final class AuthCacheKeeper {
    private static final Logger LOG = LoggerFactory.getLogger(AuthCacheKeeper.class);
    private final SchemePortResolver schemePortResolver;

    public AuthCacheKeeper(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
    }

    public void updateOnChallenge(HttpHost host, String pathPrefix, AuthExchange authExchange, HttpContext context) {
        this.clearCache(host, pathPrefix, HttpClientContext.adapt(context));
    }

    public void updateOnNoChallenge(HttpHost host, String pathPrefix, AuthExchange authExchange, HttpContext context) {
        if (authExchange.getState() == AuthExchange.State.SUCCESS) {
            this.updateCache(host, pathPrefix, authExchange.getAuthScheme(), HttpClientContext.adapt(context));
        }
    }

    public void updateOnResponse(HttpHost host, String pathPrefix, AuthExchange authExchange, HttpContext context) {
        if (authExchange.getState() == AuthExchange.State.FAILURE) {
            this.clearCache(host, pathPrefix, HttpClientContext.adapt(context));
        }
    }

    public void loadPreemptively(HttpHost host, String pathPrefix, AuthExchange authExchange, HttpContext context) {
        if (authExchange.getState() == AuthExchange.State.UNCHALLENGED) {
            AuthScheme authScheme = this.loadFromCache(host, pathPrefix, HttpClientContext.adapt(context));
            if (authScheme == null && pathPrefix != null) {
                authScheme = this.loadFromCache(host, null, HttpClientContext.adapt(context));
            }
            if (authScheme != null) {
                authExchange.select(authScheme);
            }
        }
    }

    private AuthScheme loadFromCache(HttpHost host, String pathPrefix, HttpClientContext clientContext) {
        AuthScheme authScheme;
        AuthCache authCache = clientContext.getAuthCache();
        if (authCache != null && (authScheme = authCache.get(host, pathPrefix)) != null) {
            if (LOG.isDebugEnabled()) {
                String exchangeId = clientContext.getExchangeId();
                LOG.debug("{} Re-using cached '{}' auth scheme for {}{}", new Object[]{exchangeId, authScheme.getName(), host, pathPrefix != null ? pathPrefix : ""});
            }
            return authScheme;
        }
        return null;
    }

    private void updateCache(HttpHost host, String pathPrefix, AuthScheme authScheme, HttpClientContext clientContext) {
        boolean cacheable;
        boolean bl = cacheable = authScheme.getClass().getAnnotation(AuthStateCacheable.class) != null;
        if (cacheable) {
            AuthCache authCache = clientContext.getAuthCache();
            if (authCache == null) {
                authCache = new BasicAuthCache(this.schemePortResolver);
                clientContext.setAuthCache(authCache);
            }
            if (LOG.isDebugEnabled()) {
                String exchangeId = clientContext.getExchangeId();
                LOG.debug("{} Caching '{}' auth scheme for {}{}", new Object[]{exchangeId, authScheme.getName(), host, pathPrefix != null ? pathPrefix : ""});
            }
            authCache.put(host, pathPrefix, authScheme);
        }
    }

    private void clearCache(HttpHost host, String pathPrefix, HttpClientContext clientContext) {
        AuthCache authCache = clientContext.getAuthCache();
        if (authCache != null) {
            if (LOG.isDebugEnabled()) {
                String exchangeId = clientContext.getExchangeId();
                LOG.debug("{} Clearing cached auth scheme for {}{}", new Object[]{exchangeId, host, pathPrefix != null ? pathPrefix : ""});
            }
            authCache.remove(host, pathPrefix);
        }
    }
}

