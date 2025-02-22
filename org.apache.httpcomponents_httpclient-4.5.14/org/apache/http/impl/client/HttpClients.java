/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.MinimalHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClients {
    private HttpClients() {
    }

    public static HttpClientBuilder custom() {
        return HttpClientBuilder.create();
    }

    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create().build();
    }

    public static CloseableHttpClient createSystem() {
        return HttpClientBuilder.create().useSystemProperties().build();
    }

    public static CloseableHttpClient createMinimal() {
        return new MinimalHttpClient(new PoolingHttpClientConnectionManager());
    }

    public static CloseableHttpClient createMinimal(HttpClientConnectionManager connManager) {
        return new MinimalHttpClient(connManager);
    }
}

