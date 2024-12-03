/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointManager;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.ClientPartitionListenerService;
import com.hazelcast.client.impl.ClientSelector;
import com.hazelcast.client.impl.protocol.ClientExceptions;
import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientType;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.security.SecurityContext;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.ProxyService;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.TransactionManagerService;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class NoOpClientEngine
implements ClientEngine {
    @Override
    public boolean bind(ClientEndpoint endpoint) {
        return true;
    }

    @Override
    public Collection<Client> getClients() {
        return Collections.emptyList();
    }

    @Override
    public int getClientEndpointCount() {
        return 0;
    }

    @Override
    public IPartitionService getPartitionService() {
        return null;
    }

    @Override
    public ClusterService getClusterService() {
        return null;
    }

    @Override
    public EventService getEventService() {
        return null;
    }

    @Override
    public ProxyService getProxyService() {
        return null;
    }

    @Override
    public ILogger getLogger(Class clazz) {
        return null;
    }

    @Override
    public Address getThisAddress() {
        return null;
    }

    @Override
    public String getThisUuid() {
        return null;
    }

    @Override
    public ClientEndpointManager getEndpointManager() {
        return null;
    }

    @Override
    public ClientExceptions getClientExceptions() {
        return null;
    }

    @Override
    public SecurityContext getSecurityContext() {
        return null;
    }

    @Override
    public TransactionManagerService getTransactionManagerService() {
        return null;
    }

    @Override
    public ClientPartitionListenerService getPartitionListenerService() {
        return null;
    }

    @Override
    public Map<ClientType, Integer> getConnectedClientStats() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getClientStatistics() {
        return Collections.emptyMap();
    }

    @Override
    public String getOwnerUuid(String clientUuid) {
        return null;
    }

    @Override
    public boolean isClientAllowed(Client client) {
        return true;
    }

    @Override
    public void applySelector(ClientSelector selector) {
    }

    @Override
    public void accept(ClientMessage clientMessage) {
    }

    @Override
    public Address memberAddressOf(Address clientAddress) {
        throw new TargetNotMemberException("NoOpClientEngine does not supply translation from client to member address");
    }

    @Override
    public Address clientAddressOf(Address clientAddress) {
        throw new TargetNotMemberException("NoOpClientEngine does not supply translation from member to client address");
    }
}

