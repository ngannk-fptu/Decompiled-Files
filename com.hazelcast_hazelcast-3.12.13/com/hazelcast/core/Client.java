/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.ClientType;
import com.hazelcast.core.Endpoint;
import java.net.InetSocketAddress;
import java.util.Set;

public interface Client
extends Endpoint {
    @Override
    public String getUuid();

    @Override
    public InetSocketAddress getSocketAddress();

    public ClientType getClientType();

    public String getName();

    public Set<String> getLabels();
}

