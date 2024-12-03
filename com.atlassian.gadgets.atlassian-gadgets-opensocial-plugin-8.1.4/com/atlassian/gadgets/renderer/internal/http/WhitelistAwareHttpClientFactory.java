/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.opensocial.spi.Whitelist
 *  com.atlassian.gadgets.util.HttpTimeoutsProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  javax.inject.Inject
 *  javax.inject.Singleton
 *  org.apache.http.Header
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.RedirectStrategy
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.message.BasicHeader
 */
package com.atlassian.gadgets.renderer.internal.http;

import com.atlassian.gadgets.opensocial.spi.Whitelist;
import com.atlassian.gadgets.renderer.internal.http.HttpClientSpec;
import com.atlassian.gadgets.renderer.internal.http.WhitelistAwareHttpClient;
import com.atlassian.gadgets.util.HttpTimeoutsProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

@Singleton
public class WhitelistAwareHttpClientFactory {
    private static final Set<Header> DEFAULT_REQUEST_HEADERS = ImmutableSet.of((Object)new BasicHeader("Accept-Encoding", "gzip, deflate"));
    private final RedirectStrategy redirectStrategy;
    private final HttpTimeoutsProvider httpTimeoutsProvider;
    private final SocketConfig socketConfig;
    private final HttpRoutePlanner routePlanner;
    private final Whitelist whitelist;
    private final UserManager userManager;

    @Inject
    public WhitelistAwareHttpClientFactory(RedirectStrategy redirectStrategy, ApplicationProperties applicationProperties, HttpRoutePlanner routePlanner, Whitelist whitelist, UserManager userManager) {
        this.redirectStrategy = redirectStrategy;
        this.httpTimeoutsProvider = new HttpTimeoutsProvider(applicationProperties);
        this.routePlanner = routePlanner;
        this.whitelist = whitelist;
        this.socketConfig = this.buildSocketConfig();
        this.userManager = userManager;
    }

    public HttpClient getClient(HttpClientSpec spec) {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultHeaders(DEFAULT_REQUEST_HEADERS);
        builder.setRoutePlanner(this.routePlanner);
        builder.setDefaultSocketConfig(this.socketConfig);
        builder.setRedirectStrategy(this.redirectStrategy);
        int httpConnectionTimeout = this.httpTimeoutsProvider.getConnectionTimeout();
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(httpConnectionTimeout).setConnectTimeout(httpConnectionTimeout).setCookieSpec("standard");
        if (spec.isFollowRedirects()) {
            requestConfigBuilder.setMaxRedirects(3);
        } else {
            builder.disableRedirectHandling();
        }
        builder.setDefaultRequestConfig(requestConfigBuilder.build());
        return new WhitelistAwareHttpClient((HttpClient)builder.build(), this.whitelist, this.userManager);
    }

    private SocketConfig buildSocketConfig() {
        return SocketConfig.custom().setSoTimeout(this.httpTimeoutsProvider.getSocketTimeout()).build();
    }
}

