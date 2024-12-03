/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.nio.AdvancedNetworkStats;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import java.util.Collection;
import java.util.Map;

public class UnifiedAggregateEndpointManager
implements AggregateEndpointManager {
    private final TcpIpEndpointManager unified;
    private final Map<EndpointQualifier, EndpointManager<TcpIpConnection>> views;

    public UnifiedAggregateEndpointManager(TcpIpEndpointManager unified, Map<EndpointQualifier, EndpointManager<TcpIpConnection>> views) {
        this.unified = unified;
        this.views = views;
    }

    @Override
    public Collection<TcpIpConnection> getActiveConnections() {
        return this.unified.getActiveConnections();
    }

    @Override
    public Collection<TcpIpConnection> getConnections() {
        return this.unified.getConnections();
    }

    public EndpointManager<TcpIpConnection> getEndpointManager(EndpointQualifier qualifier) {
        return this.views.get(qualifier);
    }

    public void reset(boolean cleanListeners) {
        this.unified.reset(cleanListeners);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        this.unified.addConnectionListener(listener);
    }

    @Override
    public AdvancedNetworkStats getInboundNetworkStats() {
        return null;
    }

    @Override
    public AdvancedNetworkStats getOutboundNetworkStats() {
        return null;
    }
}

