/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.impl;

import com.hazelcast.spi.discovery.impl.DefaultDiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;

public class DefaultDiscoveryServiceProvider
implements DiscoveryServiceProvider {
    @Override
    public DiscoveryService newDiscoveryService(DiscoveryServiceSettings settings) {
        return new DefaultDiscoveryService(settings);
    }
}

