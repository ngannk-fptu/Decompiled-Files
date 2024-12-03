/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

import com.hazelcast.hotrestart.InternalHotRestartService;
import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.nio.Address;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NoopInternalHotRestartService
implements InternalHotRestartService {
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean triggerForceStart() {
        return false;
    }

    @Override
    public boolean triggerPartialStart() {
        return false;
    }

    @Override
    public boolean isMemberExcluded(Address memberAddress, String memberUuid) {
        return false;
    }

    @Override
    public Set<String> getExcludedMemberUuids() {
        return Collections.emptySet();
    }

    @Override
    public void notifyExcludedMember(Address memberAddress) {
    }

    @Override
    public void handleExcludedMemberUuids(Address sender, Set<String> excludedMemberUuids) {
    }

    @Override
    public ClusterHotRestartStatusDTO getCurrentClusterHotRestartStatus() {
        return new ClusterHotRestartStatusDTO();
    }

    @Override
    public void resetService(boolean isAfterJoin) {
    }

    @Override
    public void forceStartBeforeJoin() {
    }

    @Override
    public void waitPartitionReplicaSyncOnCluster(long timeout, TimeUnit unit) {
    }
}

