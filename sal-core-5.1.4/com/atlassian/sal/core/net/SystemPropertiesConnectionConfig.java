/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.core.net.ConnectionConfig;

public class SystemPropertiesConnectionConfig
implements ConnectionConfig {
    public static final String HTTP_SOCKET_TIMEOUT_PROPERTY_NAME = "http.socketTimeout";
    public static final String HTTP_CONNECTION_TIMEOUT_PROPERTY_NAME = "http.connectionTimeout";
    public static final String HTTP_MAX_REDIRECTS_PROPERTY_NAME = "http.max-redirects";
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    public static final int DEFAULT_MAX_REDIRECTS = 20;
    private final int socketTimeout = Integer.getInteger("http.socketTimeout", 10000);
    private final int connectionTimeout = Integer.getInteger("http.connectionTimeout", 10000);
    private final int maxRedirects = Integer.getInteger("http.max-redirects", 20);

    @Override
    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    @Override
    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    @Override
    public int getMaxRedirects() {
        return this.maxRedirects;
    }
}

