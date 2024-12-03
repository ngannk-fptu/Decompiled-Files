/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.IOReactorUtils;
import org.apache.http.impl.nio.client.MinimalHttpAsyncClient;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.HttpAsyncRequestExecutor;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.VersionInfo;

class MinimalHttpAsyncClientBuilder {
    private NHttpClientConnectionManager connManager;
    private boolean connManagerShared;
    private ConnectionReuseStrategy reuseStrategy;
    private ConnectionKeepAliveStrategy keepAliveStrategy;
    private String userAgent;
    private ThreadFactory threadFactory;
    private boolean cookieManagementDisabled;

    public static MinimalHttpAsyncClientBuilder create() {
        return new MinimalHttpAsyncClientBuilder();
    }

    protected MinimalHttpAsyncClientBuilder() {
    }

    public final MinimalHttpAsyncClientBuilder setConnectionManager(NHttpClientConnectionManager connManager) {
        this.connManager = connManager;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder setConnectionManagerShared(boolean shared) {
        this.connManagerShared = shared;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder setConnectionReuseStrategy(ConnectionReuseStrategy reuseStrategy) {
        this.reuseStrategy = reuseStrategy;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder setKeepAliveStrategy(ConnectionKeepAliveStrategy keepAliveStrategy) {
        this.keepAliveStrategy = keepAliveStrategy;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder setThreadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public final MinimalHttpAsyncClientBuilder disableCookieManagement() {
        this.cookieManagementDisabled = true;
        return this;
    }

    public MinimalHttpAsyncClient build() {
        String userAgent;
        ConnectionKeepAliveStrategy keepAliveStrategy;
        ConnectionReuseStrategy reuseStrategy;
        NHttpClientConnectionManager connManager = this.connManager;
        if (connManager == null) {
            connManager = new PoolingNHttpClientConnectionManager(IOReactorUtils.create(IOReactorConfig.DEFAULT, this.threadFactory));
        }
        if ((reuseStrategy = this.reuseStrategy) == null) {
            reuseStrategy = DefaultConnectionReuseStrategy.INSTANCE;
        }
        if ((keepAliveStrategy = this.keepAliveStrategy) == null) {
            keepAliveStrategy = DefaultConnectionKeepAliveStrategy.INSTANCE;
        }
        if ((userAgent = this.userAgent) == null) {
            userAgent = VersionInfo.getUserAgent("Apache-HttpAsyncClient", "org.apache.http.nio.client", this.getClass());
        }
        HttpProcessorBuilder b = HttpProcessorBuilder.create();
        b.addAll(new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(userAgent));
        if (!this.cookieManagementDisabled) {
            b.add(new RequestAddCookies());
            b.add(new ResponseProcessCookies());
        }
        HttpProcessor httpprocessor = b.build();
        ThreadFactory threadFactory = null;
        HttpAsyncRequestExecutor eventHandler = null;
        if (!this.connManagerShared) {
            threadFactory = this.threadFactory;
            if (threadFactory == null) {
                threadFactory = Executors.defaultThreadFactory();
            }
            eventHandler = new HttpAsyncRequestExecutor();
        }
        return new MinimalHttpAsyncClient(connManager, threadFactory, eventHandler, httpprocessor, reuseStrategy, keepAliveStrategy);
    }
}

