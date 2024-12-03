/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ConnectionType;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.spi.annotation.PrivateApi;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@PrivateApi
public interface Connection {
    public boolean isAlive();

    public long lastReadTimeMillis();

    public long lastWriteTimeMillis();

    public ConnectionType getType();

    public EndpointManager getEndpointManager();

    public void setType(ConnectionType var1);

    public boolean isClient();

    public InetAddress getInetAddress();

    public InetSocketAddress getRemoteSocketAddress();

    public Address getEndPoint();

    public int getPort();

    public boolean write(OutboundFrame var1);

    public void close(String var1, Throwable var2);

    public String getCloseReason();

    public Throwable getCloseCause();
}

