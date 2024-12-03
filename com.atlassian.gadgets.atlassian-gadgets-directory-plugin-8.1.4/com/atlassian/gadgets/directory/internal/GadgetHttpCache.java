/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.util.HttpTimeoutsProvider
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.annotations.VisibleForTesting
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 *  org.apache.http.impl.conn.SystemDefaultRoutePlanner
 *  org.codehaus.httpcache4j.HTTPRequest
 *  org.codehaus.httpcache4j.HTTPResponse
 *  org.codehaus.httpcache4j.cache.CacheStorage
 *  org.codehaus.httpcache4j.cache.HTTPCache
 *  org.codehaus.httpcache4j.cache.MemoryCacheStorage
 *  org.codehaus.httpcache4j.resolver.HTTPClientResponseResolver
 *  org.codehaus.httpcache4j.resolver.ResponseResolver
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.util.HttpTimeoutsProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.annotations.VisibleForTesting;
import java.net.ProxySelector;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.codehaus.httpcache4j.HTTPRequest;
import org.codehaus.httpcache4j.HTTPResponse;
import org.codehaus.httpcache4j.cache.CacheStorage;
import org.codehaus.httpcache4j.cache.HTTPCache;
import org.codehaus.httpcache4j.cache.MemoryCacheStorage;
import org.codehaus.httpcache4j.resolver.HTTPClientResponseResolver;
import org.codehaus.httpcache4j.resolver.ResponseResolver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class GadgetHttpCache
implements DisposableBean {
    private final HTTPCache delegate;
    private final HttpTimeoutsProvider httpTimeoutsProvider;

    @Autowired
    public GadgetHttpCache(@ComponentImport ApplicationProperties applicationProperties) {
        this.httpTimeoutsProvider = new HttpTimeoutsProvider(applicationProperties);
        this.delegate = new HTTPCache((CacheStorage)new MemoryCacheStorage(), (ResponseResolver)new HTTPClientResponseResolver(this.createHttpClient()));
    }

    public HTTPResponse execute(HTTPRequest request) {
        return this.delegate.execute(request);
    }

    @VisibleForTesting
    CloseableHttpClient createHttpClient() {
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(this.httpTimeoutsProvider.getSocketTimeout()).setTcpNoDelay(true).build();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(15000).setConnectionRequestTimeout(5000).setCookieSpec("standard").build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(){

            protected void finalize() throws Throwable {
            }
        };
        return HttpClients.custom().setUserAgent("Atlassian-Gadgets-HttpClient").setRoutePlanner((HttpRoutePlanner)routePlanner).setDefaultSocketConfig(socketConfig).setDefaultRequestConfig(requestConfig).setConnectionManager((HttpClientConnectionManager)cm).build();
    }

    public void destroy() {
        this.delegate.shutdown();
    }
}

