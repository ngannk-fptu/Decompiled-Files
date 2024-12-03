/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.conn.SystemDefaultRoutePlanner
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.plugins.navlink.consumer.http.HttpClientFactory;
import java.net.ProxySelector;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

public class HttpClientFactoryImpl
implements HttpClientFactory {
    private static final int SOCKET_TIMEOUT = Integer.getInteger("navlink.httpclient.sotimeout", 3000);
    private static final int CONNECTION_TIMEOUT = Integer.getInteger("navlink.httpclient.conntimeout", 1500);
    private static final int CONNECTION_POOL_TIMEOUT_IN_MILLIS = Integer.getInteger("navlink.httpclient.pool.timeout", 3600000);

    @Override
    public CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create().setConnectionTimeToLive((long)CONNECTION_POOL_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS).setDefaultRequestConfig(this.createRequestConfig()).setRoutePlanner(this.createRoutePlaner()).useSystemProperties().build();
    }

    private RequestConfig createRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT).setCookieSpec("ignoreCookies").build();
    }

    private HttpRoutePlanner createRoutePlaner() {
        return new SystemDefaultRoutePlanner(ProxySelector.getDefault());
    }
}

