/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.OkHttpClient$Builder
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.DelegatingSocketFactory;
import com.atlassian.migration.agent.okhttp.OkHttpClientSingleton;
import com.atlassian.migration.agent.okhttp.ProxyStrategy;
import com.atlassian.migration.agent.okhttp.ProxyType;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.OkHttpClient;

public class HttpsProxyStrategy
extends ProxyStrategy {
    private static final String HTTPS_PROXY_HOST = "https.proxyHost";
    private static final String HTTPS_PROXY_PORT = "https.proxyPort";
    private static final String HTTPS_PROXY_USER = "https.proxyUser";
    private static final String HTTPS_PROXY_PASSWORD = "https.proxyPassword";
    private static final String HTTPS_NON_PROXY_HOSTS = "https.nonProxyHosts";

    public HttpsProxyStrategy(OkHttpClientSingleton okHttpClientSingleton) {
        this.okHttpClientSingleton = okHttpClientSingleton;
        this.initFromProperty(HTTPS_PROXY_HOST, HTTPS_PROXY_PORT, HTTPS_PROXY_USER, HTTPS_PROXY_PASSWORD, HTTPS_NON_PROXY_HOSTS);
    }

    @Override
    public ProxyType getProxyType() {
        return ProxyType.HTTPS;
    }

    @Override
    public OkHttpClient.Builder getProxyBuilder() {
        return super.getProxyBuilder().socketFactory((SocketFactory)new DelegatingSocketFactory(SSLSocketFactory.getDefault()));
    }
}

