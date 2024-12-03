/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.config.Lookup
 *  org.apache.hc.core5.http.protocol.BasicHttpContext
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Lookup;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

public class ContextBuilder {
    private final SchemePortResolver schemePortResolver;
    private Lookup<CookieSpecFactory> cookieSpecRegistry;
    private Lookup<AuthSchemeFactory> authSchemeRegistry;
    private CookieStore cookieStore;
    private CredentialsProvider credentialsProvider;
    private AuthCache authCache;
    private Map<HttpHost, AuthScheme> authSchemeMap;

    ContextBuilder(SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
    }

    public static ContextBuilder create(SchemePortResolver schemePortResolver) {
        return new ContextBuilder(schemePortResolver);
    }

    public static ContextBuilder create() {
        return new ContextBuilder(DefaultSchemePortResolver.INSTANCE);
    }

    public ContextBuilder useCookieSpecRegistry(Lookup<CookieSpecFactory> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
        return this;
    }

    public ContextBuilder useAuthSchemeRegistry(Lookup<AuthSchemeFactory> authSchemeRegistry) {
        this.authSchemeRegistry = authSchemeRegistry;
        return this;
    }

    public ContextBuilder useCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        return this;
    }

    public ContextBuilder useCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
        return this;
    }

    public ContextBuilder useAuthCache(AuthCache authCache) {
        this.authCache = authCache;
        return this;
    }

    public ContextBuilder preemptiveAuth(HttpHost host, AuthScheme authScheme) {
        Args.notNull((Object)host, (String)"HTTP host");
        if (this.authSchemeMap == null) {
            this.authSchemeMap = new HashMap<HttpHost, AuthScheme>();
        }
        this.authSchemeMap.put(RoutingSupport.normalize(host, this.schemePortResolver), authScheme);
        return this;
    }

    public ContextBuilder preemptiveBasicAuth(HttpHost host, UsernamePasswordCredentials credentials) {
        Args.notNull((Object)host, (String)"HTTP host");
        BasicScheme authScheme = new BasicScheme(StandardCharsets.UTF_8);
        authScheme.initPreemptive(credentials);
        this.preemptiveAuth(host, authScheme);
        return this;
    }

    public HttpClientContext build() {
        HttpClientContext context = new HttpClientContext((HttpContext)new BasicHttpContext());
        context.setCookieSpecRegistry(this.cookieSpecRegistry);
        context.setAuthSchemeRegistry(this.authSchemeRegistry);
        context.setCookieStore(this.cookieStore);
        context.setCredentialsProvider(this.credentialsProvider);
        context.setAuthCache(this.authCache);
        if (this.authSchemeMap != null) {
            for (Map.Entry<HttpHost, AuthScheme> entry : this.authSchemeMap.entrySet()) {
                context.resetAuthExchange(entry.getKey(), entry.getValue());
            }
        }
        return context;
    }
}

