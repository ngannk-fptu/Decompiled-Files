/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.discovery;

import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.Map;

public final class SimpleDiscoveryNode
extends DiscoveryNode {
    private final Address privateAddress;
    private final Address publicAddress;
    private final Map<String, Object> properties;

    public SimpleDiscoveryNode(Address privateAddress) {
        this(privateAddress, privateAddress, Collections.emptyMap());
    }

    public SimpleDiscoveryNode(Address privateAddress, Map<String, Object> properties) {
        this(privateAddress, privateAddress, properties);
    }

    public SimpleDiscoveryNode(Address privateAddress, Address publicAddress) {
        this(privateAddress, publicAddress, Collections.emptyMap());
    }

    public SimpleDiscoveryNode(Address privateAddress, Address publicAddress, Map<String, Object> properties) {
        Preconditions.checkNotNull(privateAddress, "The private address cannot be null");
        Preconditions.checkNotNull(properties, "The properties cannot be null");
        this.privateAddress = privateAddress;
        this.publicAddress = publicAddress;
        this.properties = Collections.unmodifiableMap(properties);
    }

    @Override
    public Address getPrivateAddress() {
        return this.privateAddress;
    }

    @Override
    public Address getPublicAddress() {
        return this.publicAddress;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
}

