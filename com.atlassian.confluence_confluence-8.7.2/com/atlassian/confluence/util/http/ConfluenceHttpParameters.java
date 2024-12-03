/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import java.io.Serializable;

public class ConfluenceHttpParameters
implements Serializable {
    private static final long serialVersionUID = -5284398916009022036L;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
    private int connectionTimeout = 10000;
    private int socketTimeout = 10000;
    private boolean enabled = true;

    public ConfluenceHttpParameters() {
    }

    public ConfluenceHttpParameters(ConfluenceHttpParameters that) {
        this.connectionTimeout = that.connectionTimeout;
        this.socketTimeout = that.socketTimeout;
        this.enabled = that.enabled;
    }

    public int getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return this.socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

