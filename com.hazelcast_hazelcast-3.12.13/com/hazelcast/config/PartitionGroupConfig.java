/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PartitionGroupConfig {
    private boolean enabled;
    private MemberGroupType groupType = MemberGroupType.PER_MEMBER;
    private final List<MemberGroupConfig> memberGroupConfigs = new LinkedList<MemberGroupConfig>();

    public boolean isEnabled() {
        return this.enabled;
    }

    public PartitionGroupConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public MemberGroupType getGroupType() {
        return this.groupType;
    }

    public PartitionGroupConfig setGroupType(MemberGroupType memberGroupType) {
        this.groupType = Preconditions.isNotNull(memberGroupType, "memberGroupType");
        return this;
    }

    public PartitionGroupConfig addMemberGroupConfig(MemberGroupConfig memberGroupConfig) {
        this.memberGroupConfigs.add(Preconditions.isNotNull(memberGroupConfig, "memberGroupConfig"));
        return this;
    }

    public Collection<MemberGroupConfig> getMemberGroupConfigs() {
        return Collections.unmodifiableCollection(this.memberGroupConfigs);
    }

    public PartitionGroupConfig clear() {
        this.memberGroupConfigs.clear();
        return this;
    }

    public PartitionGroupConfig setMemberGroupConfigs(Collection<MemberGroupConfig> memberGroupConfigs) {
        Preconditions.isNotNull(memberGroupConfigs, "memberGroupConfigs");
        this.memberGroupConfigs.clear();
        this.memberGroupConfigs.addAll(memberGroupConfigs);
        return this;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof PartitionGroupConfig)) {
            return false;
        }
        PartitionGroupConfig that = (PartitionGroupConfig)o;
        if (this.enabled != that.enabled) {
            return false;
        }
        if (this.groupType != that.groupType) {
            return false;
        }
        return this.memberGroupConfigs.equals(that.memberGroupConfigs);
    }

    public final int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.groupType != null ? this.groupType.hashCode() : 0);
        result = 31 * result + this.memberGroupConfigs.hashCode();
        return result;
    }

    public String toString() {
        return "PartitionGroupConfig{enabled=" + this.enabled + ", groupType=" + (Object)((Object)this.groupType) + ", memberGroupConfigs=" + this.memberGroupConfigs + '}';
    }

    public static enum MemberGroupType {
        HOST_AWARE,
        CUSTOM,
        PER_MEMBER,
        ZONE_AWARE,
        NODE_AWARE,
        SPI;

    }
}

