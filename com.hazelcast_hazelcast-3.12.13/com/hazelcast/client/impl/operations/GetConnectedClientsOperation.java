/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.operations;

import com.hazelcast.client.impl.ClientEndpointImpl;
import com.hazelcast.client.impl.ClientEngine;
import com.hazelcast.client.impl.operations.AbstractClientOperation;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientType;
import com.hazelcast.spi.ReadonlyOperation;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Map;

public class GetConnectedClientsOperation
extends AbstractClientOperation
implements ReadonlyOperation {
    private Map<String, ClientType> clients;

    @Override
    public void run() throws Exception {
        ClientEngine service = (ClientEngine)this.getService();
        Collection<Client> serviceClients = service.getClients();
        this.clients = MapUtil.createHashMap(serviceClients.size());
        for (Client clientEndpoint : serviceClients) {
            ClientEndpointImpl clientEndpointImpl = (ClientEndpointImpl)clientEndpoint;
            this.clients.put(clientEndpointImpl.getUuid(), clientEndpointImpl.getClientType());
        }
    }

    @Override
    public Object getResponse() {
        return this.clients;
    }

    @Override
    public int getId() {
        return 2;
    }
}

