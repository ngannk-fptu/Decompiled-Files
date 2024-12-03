/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.core.Client;
import com.hazelcast.core.ClientType;
import java.net.InetSocketAddress;
import java.util.Set;

public class ClientImpl
implements Client {
    private final String uuid;
    private final InetSocketAddress socketAddress;
    private final String name;
    private final Set<String> labels;

    public ClientImpl(String uuid, InetSocketAddress socketAddress, String name, Set<String> labels) {
        this.uuid = uuid;
        this.socketAddress = socketAddress;
        this.name = name;
        this.labels = labels;
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }

    @Override
    public ClientType getClientType() {
        return ClientType.JAVA;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Set<String> getLabels() {
        return this.labels;
    }
}

