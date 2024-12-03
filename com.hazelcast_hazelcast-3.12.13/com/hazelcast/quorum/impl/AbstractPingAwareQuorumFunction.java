/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.ConfigAccessor;
import com.hazelcast.config.IcmpFailureDetectorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.internal.cluster.fd.PingFailureDetector;
import com.hazelcast.quorum.PingAware;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;

public abstract class AbstractPingAwareQuorumFunction
implements PingAware,
HazelcastInstanceAware,
MembershipListener {
    private boolean pingFDEnabled;
    private PingFailureDetector<Member> pingFailureDetector;

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        boolean icmpParallelMode;
        boolean icmpEnabled;
        Config config = hazelcastInstance.getConfig();
        IcmpFailureDetectorConfig icmpFailureDetectorConfig = ConfigAccessor.getActiveMemberNetworkConfig(config).getIcmpFailureDetectorConfig();
        HazelcastProperties hazelcastProperties = new HazelcastProperties(config);
        boolean bl = icmpEnabled = icmpFailureDetectorConfig == null ? hazelcastProperties.getBoolean(GroupProperty.ICMP_ENABLED) : icmpFailureDetectorConfig.isEnabled();
        boolean bl2 = icmpEnabled && (icmpFailureDetectorConfig == null ? hazelcastProperties.getBoolean(GroupProperty.ICMP_PARALLEL_MODE) : icmpFailureDetectorConfig.isParallelMode()) ? true : (icmpParallelMode = false);
        if (!icmpEnabled || !icmpParallelMode) {
            return;
        }
        int icmpMaxAttempts = icmpFailureDetectorConfig == null ? hazelcastProperties.getInteger(GroupProperty.ICMP_MAX_ATTEMPTS) : icmpFailureDetectorConfig.getMaxAttempts();
        this.pingFailureDetector = new PingFailureDetector(icmpMaxAttempts);
        this.pingFDEnabled = true;
    }

    @Override
    public void onPingLost(Member member) {
        if (!this.pingFDEnabled) {
            return;
        }
        this.pingFailureDetector.logAttempt(member);
    }

    @Override
    public void onPingRestored(Member member) {
        if (!this.pingFDEnabled) {
            return;
        }
        this.pingFailureDetector.heartbeat(member);
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        if (this.pingFDEnabled) {
            this.pingFailureDetector.heartbeat(membershipEvent.getMember());
        }
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        if (this.pingFDEnabled) {
            this.pingFailureDetector.remove(membershipEvent.getMember());
        }
    }

    protected boolean isAlivePerIcmp(Member member) {
        if (!this.pingFDEnabled || member.localMember()) {
            return true;
        }
        return this.pingFailureDetector.isAlive(member);
    }
}

