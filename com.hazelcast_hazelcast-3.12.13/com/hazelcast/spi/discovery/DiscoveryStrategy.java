/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.partitiongroup.PartitionGroupStrategy;
import java.util.Map;

public interface DiscoveryStrategy {
    public void start();

    public Iterable<DiscoveryNode> discoverNodes();

    public void destroy();

    public PartitionGroupStrategy getPartitionGroupStrategy();

    public Map<String, Object> discoverLocalMetadata();
}

