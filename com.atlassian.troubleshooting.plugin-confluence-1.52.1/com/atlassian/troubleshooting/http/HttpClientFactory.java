/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.conn.SystemDefaultRoutePlanner
 *  org.apache.http.message.BasicHeader
 */
package com.atlassian.troubleshooting.http;

import com.google.common.collect.ImmutableSet;
import java.net.ProxySelector;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.message.BasicHeader;

public class HttpClientFactory {
    private static final int DEFAULT_MAX_REDIRECTS = 50;

    @Nonnull
    public HttpClient newHttpClient(int timeout) {
        return this.newHttpClient(50, timeout);
    }

    @Nonnull
    public HttpClient newHttpClient(int maxRedirects, int timeout) {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(timeout).build();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        httpClientBuilder.setRoutePlanner((HttpRoutePlanner)routePlanner);
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setDefaultHeaders((Collection)ImmutableSet.of((Object)new BasicHeader("Accept-Encoding", "gzip, deflate")));
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setMaxRedirects(maxRedirects).setConnectTimeout(timeout).setSocketTimeout(socketConfig.getSoTimeout());
        httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        httpClientBuilder.useSystemProperties();
        return httpClientBuilder.build();
    }
}

