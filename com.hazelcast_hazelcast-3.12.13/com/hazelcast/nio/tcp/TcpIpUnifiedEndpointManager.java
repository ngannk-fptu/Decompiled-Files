/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.instance.ProtocolType;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.IOService;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpEndpointManager;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class TcpIpUnifiedEndpointManager
extends TcpIpEndpointManager {
    TcpIpUnifiedEndpointManager(NetworkingService root, EndpointConfig endpointConfig, ChannelInitializerProvider channelInitializerProvider, IOService ioService, LoggingService loggingService, MetricsRegistry metricsRegistry, HazelcastProperties properties) {
        super(root, endpointConfig, channelInitializerProvider, ioService, loggingService, metricsRegistry, properties, ProtocolType.valuesAsSet());
    }

    Set<TcpIpConnection> getRestConnections() {
        HashSet<TcpIpConnection> connections = this.activeConnections.isEmpty() ? Collections.emptySet() : new HashSet<TcpIpConnection>(this.activeConnections.size());
        for (TcpIpConnection conn : this.activeConnections) {
            if (!conn.isAlive() || conn.getType() != ConnectionType.REST_CLIENT) continue;
            connections.add(conn);
        }
        return connections;
    }

    Set<TcpIpConnection> getMemachedConnections() {
        HashSet<TcpIpConnection> connections = this.activeConnections.isEmpty() ? Collections.emptySet() : new HashSet<TcpIpConnection>(this.activeConnections.size());
        for (TcpIpConnection conn : this.activeConnections) {
            if (!conn.isAlive() || conn.getType() != ConnectionType.MEMCACHE_CLIENT) continue;
            connections.add(conn);
        }
        return connections;
    }

    Set<TcpIpConnection> getTextConnections() {
        HashSet<TcpIpConnection> connections = this.activeConnections.isEmpty() ? Collections.emptySet() : new HashSet<TcpIpConnection>(this.activeConnections.size());
        for (TcpIpConnection conn : this.activeConnections) {
            if ((!conn.isAlive() || conn.getType() != ConnectionType.REST_CLIENT) && conn.getType() != ConnectionType.MEMCACHE_CLIENT) continue;
            connections.add(conn);
        }
        return connections;
    }

    Set<TcpIpConnection> getCurrentClientConnections() {
        HashSet<TcpIpConnection> connections = this.activeConnections.isEmpty() ? Collections.emptySet() : new HashSet<TcpIpConnection>(this.activeConnections.size());
        for (TcpIpConnection conn : this.activeConnections) {
            if (!conn.isAlive() || !conn.isClient()) continue;
            connections.add(conn);
        }
        return connections;
    }

    @Probe(name="clientCount", level=ProbeLevel.MANDATORY)
    public int getCurrentClientConnectionsCount() {
        return this.getCurrentClientConnections().size();
    }

    @Probe(name="textCount", level=ProbeLevel.MANDATORY)
    public int getCurrentTextConnections() {
        return this.getTextConnections().size();
    }
}

