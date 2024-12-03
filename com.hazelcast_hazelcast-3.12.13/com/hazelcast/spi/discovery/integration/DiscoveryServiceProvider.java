/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.integration;

import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;

public interface DiscoveryServiceProvider {
    public DiscoveryService newDiscoveryService(DiscoveryServiceSettings var1);
}

