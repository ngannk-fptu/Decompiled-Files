/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.partition.membergroup.ConfigMemberGroupFactory;
import com.hazelcast.partition.membergroup.HostAwareMemberGroupFactory;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.partition.membergroup.NodeAwareMemberGroupFactory;
import com.hazelcast.partition.membergroup.SPIAwareMemberGroupFactory;
import com.hazelcast.partition.membergroup.SingleMemberGroupFactory;
import com.hazelcast.partition.membergroup.ZoneAwareMemberGroupFactory;
import com.hazelcast.spi.discovery.integration.DiscoveryService;

public final class MemberGroupFactoryFactory {
    private MemberGroupFactoryFactory() {
    }

    public static MemberGroupFactory newMemberGroupFactory(PartitionGroupConfig partitionGroupConfig, DiscoveryService discoveryService) {
        PartitionGroupConfig.MemberGroupType memberGroupType = partitionGroupConfig == null || !partitionGroupConfig.isEnabled() ? PartitionGroupConfig.MemberGroupType.PER_MEMBER : partitionGroupConfig.getGroupType();
        switch (memberGroupType) {
            case HOST_AWARE: {
                return new HostAwareMemberGroupFactory();
            }
            case CUSTOM: {
                return new ConfigMemberGroupFactory(partitionGroupConfig.getMemberGroupConfigs());
            }
            case PER_MEMBER: {
                return new SingleMemberGroupFactory();
            }
            case ZONE_AWARE: {
                return new ZoneAwareMemberGroupFactory();
            }
            case NODE_AWARE: {
                return new NodeAwareMemberGroupFactory();
            }
            case SPI: {
                return new SPIAwareMemberGroupFactory(discoveryService);
            }
        }
        throw new RuntimeException("Unknown MemberGroupType:" + (Object)((Object)memberGroupType));
    }
}

