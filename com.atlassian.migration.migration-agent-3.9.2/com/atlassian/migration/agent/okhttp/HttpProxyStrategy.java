/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.okhttp.OkHttpClientSingleton;
import com.atlassian.migration.agent.okhttp.ProxyStrategy;
import com.atlassian.migration.agent.okhttp.ProxyType;

public class HttpProxyStrategy
extends ProxyStrategy {
    private static final String HTTP_PROXY_HOST = "http.proxyHost";
    private static final String HTTP_PROXY_PORT = "http.proxyPort";
    private static final String HTTP_PROXY_USER = "http.proxyUser";
    private static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";
    private static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";

    public HttpProxyStrategy(OkHttpClientSingleton okHttpClientSingleton) {
        this.okHttpClientSingleton = okHttpClientSingleton;
        this.initFromProperty(HTTP_PROXY_HOST, HTTP_PROXY_PORT, HTTP_PROXY_USER, HTTP_PROXY_PASSWORD, HTTP_NON_PROXY_HOSTS);
    }

    @Override
    public ProxyType getProxyType() {
        return ProxyType.HTTP;
    }
}

