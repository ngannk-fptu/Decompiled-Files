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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class DefaultAggregateEndpointManager
implements AggregateEndpointManager {
    private final ConcurrentMap<EndpointQualifier, EndpointManager<TcpIpConnection>> endpointManagers;
    private final AdvancedNetworkStats inboundNetworkStats = new AdvancedNetworkStats();
    private final AdvancedNetworkStats outboundNetworkStats = new AdvancedNetworkStats();

    public DefaultAggregateEndpointManager(ConcurrentMap<EndpointQualifier, EndpointManager<TcpIpConnection>> endpointManagers) {
        this.endpointManagers = endpointManagers;
    }

    public Set<TcpIpConnection> getActiveConnections() {
        HashSet connections = null;
        for (EndpointManager endpointManager : this.endpointManagers.values()) {
            Collection endpointConnections = endpointManager.getActiveConnections();
            if (endpointConnections == null || endpointConnections.isEmpty()) continue;
            if (connections == null) {
                connections = new HashSet();
            }
            connections.addAll(endpointConnections);
        }
        return connections == null ? Collections.emptySet() : connections;
    }

    public Set<TcpIpConnection> getConnections() {
        HashSet connections = null;
        for (EndpointManager endpointManager : this.endpointManagers.values()) {
            Collection endpointConnections = endpointManager.getConnections();
            if (endpointConnections == null || endpointConnections.isEmpty()) continue;
            if (connections == null) {
                connections = new HashSet();
            }
            connections.addAll(endpointConnections);
        }
        return connections == null ? Collections.emptySet() : connections;
    }

    public EndpointManager<TcpIpConnection> getEndpointManager(EndpointQualifier qualifier) {
        return (EndpointManager)this.endpointManagers.get(qualifier);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        for (EndpointManager manager : this.endpointManagers.values()) {
            manager.addConnectionListener(listener);
        }
    }

    @Override
    public AdvancedNetworkStats getInboundNetworkStats() {
        return this.inboundNetworkStats;
    }

    @Override
    public AdvancedNetworkStats getOutboundNetworkStats() {
        return this.outboundNetworkStats;
    }
}

