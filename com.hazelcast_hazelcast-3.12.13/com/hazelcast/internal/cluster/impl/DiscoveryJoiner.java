/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.cluster.impl.TcpIpJoiner;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.instance.Node;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.concurrent.BackoffIdleStrategy;
import com.hazelcast.util.concurrent.IdleStrategy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class DiscoveryJoiner
extends TcpIpJoiner {
    private final DiscoveryService discoveryService;
    private final boolean usePublicAddress;
    private final IdleStrategy idleStrategy = new BackoffIdleStrategy(0L, 0L, TimeUnit.MILLISECONDS.toNanos(10L), TimeUnit.MILLISECONDS.toNanos(500L));
    private final int maximumWaitingTimeBeforeJoinSeconds;

    public DiscoveryJoiner(Node node, DiscoveryService discoveryService, boolean usePublicAddress) {
        super(node);
        this.maximumWaitingTimeBeforeJoinSeconds = node.getProperties().getInteger(GroupProperty.WAIT_SECONDS_BEFORE_JOIN);
        this.discoveryService = discoveryService;
        this.usePublicAddress = usePublicAddress;
    }

    @Override
    protected Collection<Address> getPossibleAddressesForInitialJoin() {
        long deadLine = System.nanoTime() + TimeUnit.SECONDS.toNanos(this.maximumWaitingTimeBeforeJoinSeconds);
        int i = 0;
        while (System.nanoTime() < deadLine) {
            Collection<Address> possibleAddresses = this.getPossibleAddresses();
            if (!possibleAddresses.isEmpty()) {
                return possibleAddresses;
            }
            this.idleStrategy.idle(i);
            ++i;
        }
        return Collections.emptyList();
    }

    @Override
    protected Collection<Address> getPossibleAddresses() {
        Iterable<DiscoveryNode> discoveredNodes = Preconditions.checkNotNull(this.discoveryService.discoverNodes(), "Discovered nodes cannot be null!");
        MemberImpl localMember = this.node.nodeEngine.getLocalMember();
        Address localAddress = localMember.getAddress();
        ArrayList<Address> possibleMembers = new ArrayList<Address>();
        for (DiscoveryNode discoveryNode : discoveredNodes) {
            Address discoveredAddress;
            Address address = discoveredAddress = this.usePublicAddress ? discoveryNode.getPublicAddress() : discoveryNode.getPrivateAddress();
            if (localAddress.equals(discoveredAddress)) continue;
            possibleMembers.add(discoveredAddress);
        }
        return possibleMembers;
    }
}

