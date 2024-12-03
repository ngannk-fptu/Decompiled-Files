/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.ConnectionListenable;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.util.function.Consumer;
import java.util.Collection;

@PrivateApi
public interface EndpointManager<T extends Connection>
extends ConnectionListenable,
Consumer<Packet> {
    public Collection<T> getConnections();

    public Collection<T> getActiveConnections();

    public boolean registerConnection(Address var1, T var2);

    public T getConnection(Address var1);

    public T getOrConnect(Address var1);

    public T getOrConnect(Address var1, boolean var2);

    public boolean transmit(Packet var1, T var2);

    public boolean transmit(Packet var1, Address var2);
}

