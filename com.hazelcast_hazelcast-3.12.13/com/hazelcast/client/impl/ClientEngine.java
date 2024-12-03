/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointManager;
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
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.TransactionManagerService;
import com.hazelcast.util.function.Consumer;
import java.util.Collection;
import java.util.Map;

public interface ClientEngine
extends Consumer<ClientMessage> {
    public boolean bind(ClientEndpoint var1);

    public Collection<Client> getClients();

    public int getClientEndpointCount();

    public IPartitionService getPartitionService();

    public ClusterService getClusterService();

    public EventService getEventService();

    public ProxyService getProxyService();

    public ILogger getLogger(Class var1);

    public Address getThisAddress();

    public String getThisUuid();

    public ClientEndpointManager getEndpointManager();

    public ClientExceptions getClientExceptions();

    public SecurityContext getSecurityContext();

    public TransactionManagerService getTransactionManagerService();

    public ClientPartitionListenerService getPartitionListenerService();

    public Map<ClientType, Integer> getConnectedClientStats();

    public Map<String, String> getClientStatistics();

    public String getOwnerUuid(String var1);

    public boolean isClientAllowed(Client var1);

    public void applySelector(ClientSelector var1);

    public Address memberAddressOf(Address var1);

    public Address clientAddressOf(Address var1);
}

