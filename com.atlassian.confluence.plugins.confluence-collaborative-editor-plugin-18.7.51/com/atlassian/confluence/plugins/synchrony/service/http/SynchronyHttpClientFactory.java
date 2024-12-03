/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.impl.conn.PoolingHttpClientConnectionManager
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony.service.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component(value="synchrony-http-client-factory")
public class SynchronyHttpClientFactory
implements DisposableBean {
    private final CloseableHttpClient httpClient = this.create();

    public CloseableHttpClient get() {
        return this.httpClient;
    }

    private CloseableHttpClient create() {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultMaxPerRoute(cm.getMaxTotal());
        return HttpClients.custom().useSystemProperties().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec("standard").build()).setConnectionManager((HttpClientConnectionManager)cm).build();
    }

    public void destroy() throws Exception {
        this.httpClient.close();
    }
}

