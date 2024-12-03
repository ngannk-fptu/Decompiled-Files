/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import java.util.Collection;

public interface ClientService {
    public Collection<Client> getConnectedClients();

    public String addClientListener(ClientListener var1);

    public boolean removeClientListener(String var1);
}

