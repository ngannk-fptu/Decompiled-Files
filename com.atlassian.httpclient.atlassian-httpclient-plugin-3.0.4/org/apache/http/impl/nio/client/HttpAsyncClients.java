/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.CloseableHttpPipeliningClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.MinimalHttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.util.Args;

public class HttpAsyncClients {
    private HttpAsyncClients() {
    }

    public static HttpAsyncClientBuilder custom() {
        return HttpAsyncClientBuilder.create();
    }

    public static CloseableHttpAsyncClient createDefault() {
        return HttpAsyncClientBuilder.create().build();
    }

    public static CloseableHttpAsyncClient createSystem() {
        return HttpAsyncClientBuilder.create().useSystemProperties().build();
    }

    public static CloseableHttpAsyncClient createMinimal() {
        return MinimalHttpAsyncClientBuilder.create().disableCookieManagement().build();
    }

    public static CloseableHttpAsyncClient createMinimal(ConnectingIOReactor ioReactor) {
        Args.notNull(ioReactor, "I/O reactor");
        return HttpAsyncClients.createMinimal(new PoolingNHttpClientConnectionManager(ioReactor), false);
    }

    public static CloseableHttpAsyncClient createMinimal(NHttpClientConnectionManager connManager) {
        return HttpAsyncClients.createMinimal(connManager, false);
    }

    public static CloseableHttpAsyncClient createMinimal(NHttpClientConnectionManager connManager, boolean shared) {
        Args.notNull(connManager, "Connection manager");
        return MinimalHttpAsyncClientBuilder.create().setConnectionManager(connManager).setConnectionManagerShared(shared).disableCookieManagement().build();
    }

    public static CloseableHttpPipeliningClient createPipelining() {
        return MinimalHttpAsyncClientBuilder.create().build();
    }

    public static CloseableHttpPipeliningClient createPipelining(ConnectingIOReactor ioReactor) {
        return HttpAsyncClients.createPipelining(new PoolingNHttpClientConnectionManager(ioReactor), false);
    }

    public static CloseableHttpPipeliningClient createPipelining(NHttpClientConnectionManager connManager) {
        return HttpAsyncClients.createPipelining(connManager, false);
    }

    public static CloseableHttpPipeliningClient createPipelining(NHttpClientConnectionManager connManager, boolean shared) {
        Args.notNull(connManager, "Connection manager");
        return MinimalHttpAsyncClientBuilder.create().setConnectionManager(connManager).setConnectionManagerShared(shared).build();
    }
}

