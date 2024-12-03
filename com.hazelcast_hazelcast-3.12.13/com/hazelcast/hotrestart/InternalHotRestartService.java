/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.hotrestart;

import com.hazelcast.internal.management.dto.ClusterHotRestartStatusDTO;
import com.hazelcast.nio.Address;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface InternalHotRestartService {
    public boolean isEnabled();

    public boolean triggerForceStart();

    public boolean triggerPartialStart();

    public boolean isMemberExcluded(Address var1, String var2);

    public Set<String> getExcludedMemberUuids();

    public void notifyExcludedMember(Address var1);

    public void handleExcludedMemberUuids(Address var1, Set<String> var2);

    public ClusterHotRestartStatusDTO getCurrentClusterHotRestartStatus();

    public void resetService(boolean var1);

    public void forceStartBeforeJoin();

    public void waitPartitionReplicaSyncOnCluster(long var1, TimeUnit var3);
}

