/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking;

import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.internal.networking.ChannelInitializer;

public interface ChannelInitializerProvider {
    public ChannelInitializer provide(EndpointQualifier var1);
}

