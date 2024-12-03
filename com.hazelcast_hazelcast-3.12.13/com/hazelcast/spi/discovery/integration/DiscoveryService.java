/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery.integration;

import com.hazelcast.spi.discovery.DiscoveryNode;
import java.util.Map;

public interface DiscoveryService {
    public void start();

    public Iterable<DiscoveryNode> discoverNodes();

    public void destroy();

    public Map<String, Object> discoverLocalMetadata();
}

