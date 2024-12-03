/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.client.ClientProperties
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.http.HttpHost
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.config.Registry
 *  org.apache.http.config.RegistryBuilder
 *  org.apache.http.config.SocketConfig
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.UnsupportedSchemeException
 *  org.apache.http.conn.routing.HttpRoute
 *  org.apache.http.conn.socket.ConnectionSocketFactory
 *  org.apache.http.conn.socket.PlainConnectionSocketFactory
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.client.cache.CacheConfig
 *  org.apache.http.impl.client.cache.CachingHttpClients
 *  org.apache.http.impl.conn.DefaultSchemePortResolver
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 */
package com.atlassian.crowd.integration.rest.service;

import com.atlassian.crowd.integration.rest.service.HttpClientProvider;
import com.atlassian.crowd.service.client.ClientProperties;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClients;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class DefaultHttpClientProvider
implements HttpClientProvider {
    protected static final int MAX_CACHE_ENTRIES = 10;
    protected static final int MAX_OBJECT_SIZE = 16384;
    protected static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
    static final int DEFAULT_HTTP_TIMEOUT_MS = 5000;
    private static final int DEFAULT_SOCKET_TIMEOUT_MS = 600000;

    @Override
    public CloseableHttpClient getClient(ClientProperties clientProperties) {
        Preconditions.checkNotNull((Object)clientProperties, (Object)"clientProperties is required");
        HttpRoute httpRoute = this.routeFor(clientProperties);
        PoolingHttpClientConnectionManager connectionManager = this.getHttpClientConnectionManager(clientProperties, httpRoute);
        RequestConfig requestConfig = this.getRequestConfig(clientProperties, connectionManager, httpRoute);
        return this.getHttpClientBuilder((HttpClientConnectionManager)connectionManager, requestConfig).build();
    }

    protected Registry<ConnectionSocketFactory> getConnectionSocketFactories() {
        return RegistryBuilder.create().register("http", (Object)PlainConnectionSocketFactory.getSocketFactory()).register("https", (Object)SSLConnectionSocketFactory.getSystemSocketFactory()).build();
    }

    protected PoolingHttpClientConnectionManager getHttpClientConnectionManager(ClientProperties clientProperties, HttpRoute httpRoute) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(this.getConnectionSocketFactories(), null, null);
        connectionManager.setMaxTotal(NumberUtils.toInt((String)clientProperties.getHttpMaxConnections(), (int)20));
        connectionManager.setMaxPerRoute(httpRoute, NumberUtils.toInt((String)clientProperties.getHttpMaxConnections(), (int)connectionManager.getMaxTotal()));
        connectionManager.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(NumberUtils.toInt((String)clientProperties.getHttpTimeout(), (int)5000)).build());
        return connectionManager;
    }

    protected HttpClientBuilder getHttpClientBuilder(HttpClientConnectionManager connectionManager, RequestConfig requestConfig) {
        return CachingHttpClients.custom().setCacheConfig(this.getCacheConfig()).setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).useSystemProperties();
    }

    protected CacheConfig getCacheConfig() {
        return CacheConfig.custom().setMaxCacheEntries(10).setMaxObjectSize(16384L).setHeuristicCachingEnabled(false).setSharedCache(false).setAsynchronousWorkersMax(0).build();
    }

    protected RequestConfig getRequestConfig(ClientProperties clientProperties, PoolingHttpClientConnectionManager connectionManager, HttpRoute httpRoute) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        this.setRequestConfig(requestConfigBuilder, clientProperties, connectionManager, httpRoute);
        this.initProxyConfiguration(requestConfigBuilder, clientProperties);
        return requestConfigBuilder.build();
    }

    protected HttpRoute routeFor(ClientProperties clientProperties) {
        try {
            URI uri = new URI(clientProperties.getBaseURL());
            HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            int port = DefaultSchemePortResolver.INSTANCE.resolve(host);
            HttpHost portedHost = new HttpHost(host.getHostName(), port, host.getSchemeName());
            return new HttpRoute(portedHost, null, "https".equalsIgnoreCase(portedHost.getSchemeName()));
        }
        catch (URISyntaxException | UnsupportedSchemeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void setRequestConfig(RequestConfig.Builder builder, ClientProperties clientProperties, PoolingHttpClientConnectionManager connectionManager, HttpRoute httpRoute) {
        builder.setConnectTimeout(NumberUtils.toInt((String)clientProperties.getHttpTimeout(), (int)5000));
        builder.setSocketTimeout(NumberUtils.toInt((String)clientProperties.getSocketTimeout(), (int)600000));
        builder.setCookieSpec("standard");
        connectionManager.setMaxTotal(NumberUtils.toInt((String)clientProperties.getHttpMaxConnections(), (int)20));
        connectionManager.setMaxPerRoute(httpRoute, NumberUtils.toInt((String)clientProperties.getHttpMaxConnections(), (int)connectionManager.getMaxTotal()));
    }

    private void initProxyConfiguration(RequestConfig.Builder builder, ClientProperties clientProperties) {
        if (clientProperties.getHttpProxyHost() != null) {
            HttpHost proxy = new HttpHost(clientProperties.getHttpProxyHost(), NumberUtils.toInt((String)clientProperties.getHttpProxyPort(), (int)-1));
            builder.setProxy(proxy);
        }
    }
}

