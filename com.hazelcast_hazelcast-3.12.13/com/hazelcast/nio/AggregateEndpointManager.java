/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.networking.nio.AdvancedNetworkStats;
import com.hazelcast.nio.ConnectionListenable;
import com.hazelcast.nio.tcp.TcpIpConnection;
import java.util.Collection;

public interface AggregateEndpointManager
extends ConnectionListenable {
    public Collection<TcpIpConnection> getConnections();

    public Collection<TcpIpConnection> getActiveConnections();

    public AdvancedNetworkStats getInboundNetworkStats();

    public AdvancedNetworkStats getOutboundNetworkStats();
}

