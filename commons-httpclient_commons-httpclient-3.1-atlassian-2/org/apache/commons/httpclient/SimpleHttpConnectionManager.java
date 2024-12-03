/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleHttpConnectionManager
implements HttpConnectionManager {
    private static final Log LOG = LogFactory.getLog(SimpleHttpConnectionManager.class);
    private static final String MISUSE_MESSAGE = "SimpleHttpConnectionManager being used incorrectly.  Be sure that HttpMethod.releaseConnection() is always called and that only one thread and/or method is using this connection manager at a time.";
    protected HttpConnection httpConnection;
    private HttpConnectionManagerParams params = new HttpConnectionManagerParams();
    private long idleStartTime = Long.MAX_VALUE;
    private volatile boolean inUse = false;
    private boolean alwaysClose = false;

    static void finishLastResponse(HttpConnection conn) {
        InputStream lastResponse = conn.getLastResponseInputStream();
        if (lastResponse != null) {
            conn.setLastResponseInputStream(null);
            try {
                lastResponse.close();
            }
            catch (IOException ioe) {
                conn.close();
            }
        }
    }

    public SimpleHttpConnectionManager(boolean alwaysClose) {
        this.alwaysClose = alwaysClose;
    }

    public SimpleHttpConnectionManager() {
    }

    @Override
    public HttpConnection getConnection(HostConfiguration hostConfiguration) {
        return this.getConnection(hostConfiguration, 0L);
    }

    public boolean isConnectionStaleCheckingEnabled() {
        return this.params.isStaleCheckingEnabled();
    }

    public void setConnectionStaleCheckingEnabled(boolean connectionStaleCheckingEnabled) {
        this.params.setStaleCheckingEnabled(connectionStaleCheckingEnabled);
    }

    @Override
    public HttpConnection getConnectionWithTimeout(HostConfiguration hostConfiguration, long timeout) {
        if (this.httpConnection == null) {
            this.httpConnection = new HttpConnection(hostConfiguration);
            this.httpConnection.setHttpConnectionManager(this);
            this.httpConnection.getParams().setDefaults(this.params);
        } else if (!hostConfiguration.hostEquals(this.httpConnection) || !hostConfiguration.proxyEquals(this.httpConnection)) {
            if (this.httpConnection.isOpen()) {
                this.httpConnection.close();
            }
            this.httpConnection.setHost(hostConfiguration.getHost());
            this.httpConnection.setPort(hostConfiguration.getPort());
            this.httpConnection.setProtocol(hostConfiguration.getProtocol());
            this.httpConnection.setLocalAddress(hostConfiguration.getLocalAddress());
            this.httpConnection.setProxyHost(hostConfiguration.getProxyHost());
            this.httpConnection.setProxyPort(hostConfiguration.getProxyPort());
        } else {
            SimpleHttpConnectionManager.finishLastResponse(this.httpConnection);
        }
        this.idleStartTime = Long.MAX_VALUE;
        if (this.inUse) {
            LOG.warn((Object)MISUSE_MESSAGE);
        }
        this.inUse = true;
        return this.httpConnection;
    }

    @Override
    public HttpConnection getConnection(HostConfiguration hostConfiguration, long timeout) {
        return this.getConnectionWithTimeout(hostConfiguration, timeout);
    }

    @Override
    public void releaseConnection(HttpConnection conn) {
        if (conn != this.httpConnection) {
            throw new IllegalStateException("Unexpected release of an unknown connection.");
        }
        if (this.alwaysClose) {
            this.httpConnection.close();
        } else {
            SimpleHttpConnectionManager.finishLastResponse(this.httpConnection);
        }
        this.inUse = false;
        this.idleStartTime = System.currentTimeMillis();
    }

    @Override
    public HttpConnectionManagerParams getParams() {
        return this.params;
    }

    @Override
    public void setParams(HttpConnectionManagerParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }

    @Override
    public void closeIdleConnections(long idleTimeout) {
        if (this.httpConnection == null) {
            return;
        }
        long maxIdleTime = System.currentTimeMillis() - idleTimeout;
        if (this.idleStartTime <= maxIdleTime) {
            this.httpConnection.close();
        }
    }

    public void shutdown() {
        if (this.httpConnection != null) {
            this.httpConnection.close();
        }
    }
}

