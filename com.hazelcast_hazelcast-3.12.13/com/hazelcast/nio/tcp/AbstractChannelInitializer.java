/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio.tcp;

import com.hazelcast.config.EndpointConfig;
import com.hazelcast.internal.networking.ChannelInitializer;
import com.hazelcast.nio.IOService;

public abstract class AbstractChannelInitializer
implements ChannelInitializer {
    protected final IOService ioService;
    private final EndpointConfig config;

    protected AbstractChannelInitializer(IOService ioService, EndpointConfig config) {
        this.config = config;
        this.ioService = ioService;
    }
}

