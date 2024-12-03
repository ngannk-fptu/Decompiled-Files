/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.spi.discovery.DiscoveryNode;

public interface NodeFilter {
    public boolean test(DiscoveryNode var1);
}

