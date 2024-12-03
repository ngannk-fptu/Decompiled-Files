/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEventType;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientType;
import java.net.InetSocketAddress;
import java.util.Set;

public class ClientEvent
implements Client {
    private final String uuid;
    private final ClientEventType eventType;
    private final InetSocketAddress address;
    private final ClientType clientType;
    private final String name;
    private final Set<String> labels;

    public ClientEvent(String uuid, ClientEventType eventType, InetSocketAddress address, ClientType clientType, String name, Set<String> labels) {
        this.uuid = uuid;
        this.eventType = eventType;
        this.address = address;
        this.clientType = clientType;
        this.name = name;
        this.labels = labels;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return this.address;
    }

    @Override
    public ClientType getClientType() {
        return this.clientType;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getLabels() {
        return this.labels;
    }

    public ClientEventType getEventType() {
        return this.eventType;
    }

    public String toString() {
        return "ClientEvent{uuid='" + this.uuid + '\'' + ", eventType=" + (Object)((Object)this.eventType) + ", address=" + this.address + ", clientType=" + (Object)((Object)this.clientType) + ", name='" + this.name + '\'' + ", attributes=" + this.labels + '}';
    }
}

