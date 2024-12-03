/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.nio.Address;
import java.util.Map;

public abstract class DiscoveryNode {
    public abstract Address getPrivateAddress();

    public abstract Address getPublicAddress();

    public abstract Map<String, Object> getProperties();
}

