/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.ClientService;
import com.hazelcast.instance.Node;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.Preconditions;
import java.util.Collection;

public final class ClientServiceProxy
implements ClientService {
    private final ClientEngine clientEngine;
    private final NodeEngine nodeEngine;

    public ClientServiceProxy(Node node) {
        this.clientEngine = node.clientEngine;
        this.nodeEngine = node.nodeEngine;
    }

    @Override
    public Collection<Client> getConnectedClients() {
        return this.clientEngine.getClients();
    }

    @Override
    public String addClientListener(ClientListener clientListener) {
        Preconditions.checkNotNull(clientListener, "clientListener should not be null");
        EventService eventService = this.nodeEngine.getEventService();
        EventRegistration registration = eventService.registerLocalListener("hz:core:clientEngine", "hz:core:clientEngine", clientListener);
        return registration.getId();
    }

    @Override
    public boolean removeClientListener(String registrationId) {
        Preconditions.checkNotNull(registrationId, "registrationId should not be null");
        EventService eventService = this.nodeEngine.getEventService();
        return eventService.deregisterListener("hz:core:clientEngine", "hz:core:clientEngine", registrationId);
    }
}

