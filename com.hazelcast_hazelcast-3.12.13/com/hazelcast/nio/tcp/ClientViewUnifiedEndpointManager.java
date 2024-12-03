/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ConnectionListener;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.nio.tcp.TcpIpUnifiedEndpointManager;
import java.util.Collection;
import java.util.Set;

class ClientViewUnifiedEndpointManager
implements EndpointManager<TcpIpConnection> {
    private final TcpIpUnifiedEndpointManager unifiedEndpointManager;

    ClientViewUnifiedEndpointManager(TcpIpUnifiedEndpointManager unifiedEndpointManager) {
        this.unifiedEndpointManager = unifiedEndpointManager;
    }

    @Override
    public Collection<TcpIpConnection> getConnections() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<TcpIpConnection> getActiveConnections() {
        return this.unifiedEndpointManager.getCurrentClientConnections();
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        this.unifiedEndpointManager.addConnectionListener(listener);
    }

    @Override
    public void accept(Packet packet) {
        this.unifiedEndpointManager.accept(packet);
    }

    @Override
    public TcpIpConnection getConnection(Address address) {
        return this.unifiedEndpointManager.getConnection(address);
    }

    @Override
    public TcpIpConnection getOrConnect(Address address) {
        return this.unifiedEndpointManager.getOrConnect(address);
    }

    @Override
    public TcpIpConnection getOrConnect(Address address, boolean silent) {
        return this.unifiedEndpointManager.getOrConnect(address, silent);
    }

    @Override
    public boolean registerConnection(Address address, TcpIpConnection connection) {
        return this.unifiedEndpointManager.registerConnection(address, connection);
    }

    @Override
    public boolean transmit(Packet packet, TcpIpConnection connection) {
        return this.unifiedEndpointManager.transmit(packet, connection);
    }

    @Override
    public boolean transmit(Packet packet, Address target) {
        return this.unifiedEndpointManager.transmit(packet, target);
    }

    public String toString() {
        return this.unifiedEndpointManager.toString();
    }
}

