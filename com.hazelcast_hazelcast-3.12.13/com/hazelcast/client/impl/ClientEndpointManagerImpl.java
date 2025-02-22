/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.client.impl.ClientEndpointImpl;
import com.hazelcast.client.impl.ClientEndpointManager;
import com.hazelcast.client.impl.ClientEvent;
import com.hazelcast.client.impl.ClientEventType;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Connection;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.security.auth.login.LoginException;

public class ClientEndpointManagerImpl
implements ClientEndpointManager {
    private final ILogger logger;
    private final EventService eventService;
    @Probe(name="count", level=ProbeLevel.MANDATORY)
    private final ConcurrentMap<Connection, ClientEndpoint> endpoints = new ConcurrentHashMap<Connection, ClientEndpoint>();
    @Probe(name="totalRegistrations", level=ProbeLevel.MANDATORY)
    private final MwCounter totalRegistrations = MwCounter.newMwCounter();

    public ClientEndpointManagerImpl(NodeEngine nodeEngine) {
        this.logger = nodeEngine.getLogger(ClientEndpointManager.class);
        this.eventService = nodeEngine.getEventService();
        MetricsRegistry metricsRegistry = ((NodeEngineImpl)nodeEngine).getMetricsRegistry();
        metricsRegistry.scanAndRegister(this, "client.endpoint");
    }

    @Override
    public Set<ClientEndpoint> getEndpoints(String clientUuid) {
        Preconditions.checkNotNull(clientUuid, "clientUuid can't be null");
        Set<ClientEndpoint> endpointSet = SetUtil.createHashSet(this.endpoints.size());
        for (ClientEndpoint endpoint : this.endpoints.values()) {
            if (!clientUuid.equals(endpoint.getUuid())) continue;
            endpointSet.add(endpoint);
        }
        return endpointSet;
    }

    @Override
    public ClientEndpoint getEndpoint(Connection connection) {
        Preconditions.checkNotNull(connection, "connection can't be null");
        return (ClientEndpoint)this.endpoints.get(connection);
    }

    @Override
    public boolean registerEndpoint(ClientEndpoint endpoint) {
        Preconditions.checkNotNull(endpoint, "endpoint can't be null");
        Connection conn = endpoint.getConnection();
        if (this.endpoints.putIfAbsent(conn, endpoint) != null) {
            return false;
        }
        this.totalRegistrations.inc();
        ClientEvent event = new ClientEvent(endpoint.getUuid(), ClientEventType.CONNECTED, endpoint.getSocketAddress(), endpoint.getClientType(), endpoint.getName(), endpoint.getLabels());
        this.sendClientEvent(event);
        return true;
    }

    @Override
    public void removeEndpoint(ClientEndpoint clientEndpoint) {
        Preconditions.checkNotNull(clientEndpoint, "endpoint can't be null");
        ClientEndpointImpl endpoint = (ClientEndpointImpl)clientEndpoint;
        if (this.endpoints.remove(endpoint.getConnection()) == null) {
            return;
        }
        this.logger.info("Destroying " + endpoint);
        try {
            endpoint.destroy();
        }
        catch (LoginException e) {
            this.logger.warning(e);
        }
        ClientEvent event = new ClientEvent(endpoint.getUuid(), ClientEventType.DISCONNECTED, endpoint.getSocketAddress(), endpoint.getClientType(), endpoint.getName(), endpoint.getLabels());
        this.sendClientEvent(event);
    }

    private void sendClientEvent(ClientEvent event) {
        Collection<EventRegistration> regs = this.eventService.getRegistrations("hz:core:clientEngine", "hz:core:clientEngine");
        String uuid = event.getUuid();
        this.eventService.publishEvent("hz:core:clientEngine", regs, (Object)event, uuid.hashCode());
    }

    @Override
    public void clear() {
        this.endpoints.clear();
    }

    @Override
    public Collection<ClientEndpoint> getEndpoints() {
        return this.endpoints.values();
    }

    @Override
    public int size() {
        return this.endpoints.size();
    }
}

