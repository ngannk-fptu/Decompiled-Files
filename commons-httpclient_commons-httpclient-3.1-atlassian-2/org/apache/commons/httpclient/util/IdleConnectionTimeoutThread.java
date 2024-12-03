/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.httpclient.HttpConnectionManager;

public class IdleConnectionTimeoutThread
extends Thread {
    private List connectionManagers = new ArrayList();
    private boolean shutdown = false;
    private long timeoutInterval = 1000L;
    private long connectionTimeout = 3000L;

    public IdleConnectionTimeoutThread() {
        this.setDaemon(true);
    }

    public synchronized void addConnectionManager(HttpConnectionManager connectionManager) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionManagers.add(connectionManager);
    }

    public synchronized void removeConnectionManager(HttpConnectionManager connectionManager) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionManagers.remove(connectionManager);
    }

    protected void handleCloseIdleConnections(HttpConnectionManager connectionManager) {
        connectionManager.closeIdleConnections(this.connectionTimeout);
    }

    @Override
    public synchronized void run() {
        while (!this.shutdown) {
            for (HttpConnectionManager connectionManager : this.connectionManagers) {
                this.handleCloseIdleConnections(connectionManager);
            }
            try {
                this.wait(this.timeoutInterval);
            }
            catch (InterruptedException interruptedException) {}
        }
        this.connectionManagers.clear();
    }

    public synchronized void shutdown() {
        this.shutdown = true;
        this.notifyAll();
    }

    public synchronized void setConnectionTimeout(long connectionTimeout) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.connectionTimeout = connectionTimeout;
    }

    public synchronized void setTimeoutInterval(long timeoutInterval) {
        if (this.shutdown) {
            throw new IllegalStateException("IdleConnectionTimeoutThread has been shutdown");
        }
        this.timeoutInterval = timeoutInterval;
    }
}

