/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.apache.http.Header
 *  org.apache.http.HttpHost
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.auth.AuthScope
 *  org.apache.http.auth.Credentials
 *  org.apache.http.auth.MalformedChallengeException
 *  org.apache.http.auth.UsernamePasswordCredentials
 *  org.apache.http.client.AuthCache
 *  org.apache.http.client.CredentialsProvider
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.protocol.HttpClientContext
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.auth.BasicScheme
 *  org.apache.http.impl.client.BasicCredentialsProvider
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.message.BasicHeader
 *  org.apache.http.protocol.HttpRequestExecutor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.core.net.AllPortsAuthCache;
import com.atlassian.sal.core.net.HttpClientRequest;
import com.atlassian.sal.core.net.ProxyConfig;
import com.atlassian.sal.core.net.ProxyRoutePlanner;
import com.atlassian.sal.core.net.ProxyUtil;
import com.atlassian.sal.core.net.SystemPropertiesProxyConfig;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpRequestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientRequestFactory
implements NonMarshallingRequestFactory<HttpClientRequest<?, ?>> {
    private static final Logger log = LoggerFactory.getLogger(HttpClientRequestFactory.class);
    private final Supplier<ProxyConfig> proxyConfigSupplier;

    public HttpClientRequestFactory() {
        this.proxyConfigSupplier = Suppliers.memoize(SystemPropertiesProxyConfig::new);
    }

    public HttpClientRequestFactory(ProxyConfig proxyConfig) {
        this.proxyConfigSupplier = () -> proxyConfig;
    }

    public HttpClientRequest createRequest(Request.MethodType methodType, String url) {
        log.debug("Creating HttpClientRequest with proxy config:", this.proxyConfigSupplier.get());
        CloseableHttpClient httpClient = this.createHttpClient();
        boolean requiresAuthentication = ProxyUtil.requiresAuthentication((ProxyConfig)this.proxyConfigSupplier.get(), url);
        HttpClientContext clientContext = this.createClientContext(requiresAuthentication);
        return new HttpClientRequest(httpClient, clientContext, methodType, url);
    }

    protected CloseableHttpClient createHttpClient() {
        return HttpClients.custom().useSystemProperties().setRoutePlanner(this.getRoutePlanner()).setRequestExecutor(this.getRequestExecutor()).setConnectionManager(this.getConnectionManager()).setDefaultRequestConfig(RequestConfig.custom().setCookieSpec("standard").build()).build();
    }

    protected HttpClientContext createClientContext() {
        return this.createClientContext(((ProxyConfig)this.proxyConfigSupplier.get()).requiresAuthentication());
    }

    protected HttpClientContext createClientContext(boolean requiresAuthentication) {
        HttpClientContext httpClientContext = HttpClientContext.create();
        AllPortsAuthCache authCache = new AllPortsAuthCache();
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        ProxyConfig proxyConfig = (ProxyConfig)this.proxyConfigSupplier.get();
        if (requiresAuthentication) {
            HttpHost proxyHost = new HttpHost(proxyConfig.getHost(), proxyConfig.getPort());
            AuthScope proxyAuthScope = new AuthScope(proxyHost);
            UsernamePasswordCredentials proxyCredentials = new UsernamePasswordCredentials(proxyConfig.getUser(), proxyConfig.getPassword());
            basicCredentialsProvider.setCredentials(proxyAuthScope, (Credentials)proxyCredentials);
            BasicScheme proxyScheme = new BasicScheme();
            try {
                proxyScheme.processChallenge((Header)new BasicHeader("Proxy-Authenticate", "Basic "));
            }
            catch (MalformedChallengeException e) {
                throw new IllegalStateException(e);
            }
            authCache.put(proxyHost, (AuthScheme)proxyScheme);
        }
        httpClientContext.setCredentialsProvider((CredentialsProvider)basicCredentialsProvider);
        httpClientContext.setAuthCache((AuthCache)authCache);
        return httpClientContext;
    }

    public boolean supportsHeader() {
        return true;
    }

    protected HttpRoutePlanner getRoutePlanner() {
        return ((ProxyConfig)this.proxyConfigSupplier.get()).isSet() ? new ProxyRoutePlanner((ProxyConfig)this.proxyConfigSupplier.get()) : null;
    }

    protected HttpRequestExecutor getRequestExecutor() {
        return null;
    }

    protected HttpClientConnectionManager getConnectionManager() {
        return null;
    }
}

