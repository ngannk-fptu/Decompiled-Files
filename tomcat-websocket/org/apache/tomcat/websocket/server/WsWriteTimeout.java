/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.websocket.server;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.tomcat.websocket.BackgroundProcess;
import org.apache.tomcat.websocket.BackgroundProcessManager;
import org.apache.tomcat.websocket.server.WsRemoteEndpointImplServer;

public class WsWriteTimeout
implements BackgroundProcess {
    private final Set<WsRemoteEndpointImplServer> endpoints = new ConcurrentSkipListSet<WsRemoteEndpointImplServer>(Comparator.comparingLong(WsRemoteEndpointImplServer::getTimeoutExpiry));
    private final AtomicInteger count = new AtomicInteger(0);
    private int backgroundProcessCount = 0;
    private volatile int processPeriod = 1;

    @Override
    public void backgroundProcess() {
        ++this.backgroundProcessCount;
        if (this.backgroundProcessCount >= this.processPeriod) {
            this.backgroundProcessCount = 0;
            long now = System.currentTimeMillis();
            for (WsRemoteEndpointImplServer endpoint : this.endpoints) {
                if (endpoint.getTimeoutExpiry() >= now) break;
                endpoint.onTimeout(false);
            }
        }
    }

    @Override
    public void setProcessPeriod(int period) {
        this.processPeriod = period;
    }

    @Override
    public int getProcessPeriod() {
        return this.processPeriod;
    }

    public void register(WsRemoteEndpointImplServer endpoint) {
        int newCount;
        boolean result = this.endpoints.add(endpoint);
        if (result && (newCount = this.count.incrementAndGet()) == 1) {
            BackgroundProcessManager.getInstance().register(this);
        }
    }

    public void unregister(WsRemoteEndpointImplServer endpoint) {
        int newCount;
        boolean result = this.endpoints.remove(endpoint);
        if (result && (newCount = this.count.decrementAndGet()) == 0) {
            BackgroundProcessManager.getInstance().unregister(this);
        }
    }
}

