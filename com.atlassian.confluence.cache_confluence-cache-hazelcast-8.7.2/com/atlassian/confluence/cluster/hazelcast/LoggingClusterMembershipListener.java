/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.Member
 *  com.hazelcast.core.MembershipAdapter
 *  com.hazelcast.core.MembershipEvent
 *  com.hazelcast.nio.Address
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cluster.hazelcast;

import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.nio.Address;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LoggingClusterMembershipListener
extends MembershipAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoggingClusterMembershipListener.class);

    LoggingClusterMembershipListener() {
    }

    public void memberAdded(MembershipEvent event) {
        log.info("{} joined the cluster", (Object)event.getMember().getAddress());
        this.logClusterMembers(event.getMembers());
    }

    public void memberRemoved(MembershipEvent event) {
        log.info("{} left the cluster", (Object)event.getMember().getAddress());
        this.logClusterMembers(event.getMembers());
    }

    private void logClusterMembers(Set<Member> members) {
        log.info("Cluster now has {} members: {}", (Object)members.size(), this.sortedAddresses(members));
    }

    private SortedSet<Address> sortedAddresses(Set<Member> members) {
        return members.stream().map(Member::getAddress).collect(Collectors.toCollection(() -> new TreeSet<Address>(Comparator.comparing(Address::toString))));
    }
}

