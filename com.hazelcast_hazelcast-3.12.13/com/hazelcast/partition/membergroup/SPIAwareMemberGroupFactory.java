/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.config.InvalidConfigurationException;
import com.hazelcast.config.properties.ValidationException;
import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.BackupSafeMemberGroupFactory;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.impl.DefaultDiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SPIAwareMemberGroupFactory
extends BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    private final DiscoveryService discoveryService;

    public SPIAwareMemberGroupFactory(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    protected Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> allMembers) {
        Set<MemberGroup> memberGroups = SetUtil.createHashSet(allMembers.size());
        for (Member member : allMembers) {
            try {
                if (!member.localMember()) continue;
                DefaultDiscoveryService defaultDiscoveryService = (DefaultDiscoveryService)this.discoveryService;
                if (!defaultDiscoveryService.getDiscoveryStrategies().iterator().hasNext()) {
                    throw new RuntimeException("Could not load any Discovery Strategy, please check service definitions under META_INF.services folder. ");
                }
                Iterator<DiscoveryStrategy> iterator = defaultDiscoveryService.getDiscoveryStrategies().iterator();
                if (!iterator.hasNext()) continue;
                DiscoveryStrategy discoveryStrategy = iterator.next();
                Preconditions.checkNotNull(discoveryStrategy.getPartitionGroupStrategy());
                Iterable<MemberGroup> spiGroupsIterator = discoveryStrategy.getPartitionGroupStrategy().getMemberGroups();
                for (MemberGroup group : spiGroupsIterator) {
                    memberGroups.add(group);
                }
                return memberGroups;
            }
            catch (Exception e) {
                if (e instanceof ValidationException) {
                    throw new InvalidConfigurationException("Invalid configuration", e);
                }
                throw new RuntimeException("Failed to configure discovery strategies", e);
            }
        }
        return memberGroups;
    }
}

