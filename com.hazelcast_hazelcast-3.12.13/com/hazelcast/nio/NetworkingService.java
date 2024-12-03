/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.Connection;
import com.hazelcast.nio.EndpointManager;
import com.hazelcast.nio.IOService;
import com.hazelcast.spi.annotation.PrivateApi;
import java.util.concurrent.TimeUnit;

@PrivateApi
public interface NetworkingService<T extends Connection> {
    public IOService getIoService();

    public Networking getNetworking();

    public AggregateEndpointManager getAggregateEndpointManager();

    public EndpointManager<T> getEndpointManager(EndpointQualifier var1);

    public void scheduleDeferred(Runnable var1, long var2, TimeUnit var4);

    public boolean isLive();

    public void start();

    public void stop();

    public void shutdown();
}

