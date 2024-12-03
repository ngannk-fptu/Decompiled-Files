/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl;

import com.hazelcast.client.impl.ClientEndpoint;
import com.hazelcast.nio.Connection;
import java.util.Collection;
import java.util.Set;

public interface ClientEndpointManager {
    public Collection<ClientEndpoint> getEndpoints();

    public ClientEndpoint getEndpoint(Connection var1);

    public Set<ClientEndpoint> getEndpoints(String var1);

    public int size();

    public void clear();

    public boolean registerEndpoint(ClientEndpoint var1);

    public void removeEndpoint(ClientEndpoint var1);
}

