/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelInitializerProvider;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Networking {
    public Channel register(EndpointQualifier var1, ChannelInitializerProvider var2, SocketChannel var3, boolean var4) throws IOException;

    public void start();

    public void shutdown();
}

