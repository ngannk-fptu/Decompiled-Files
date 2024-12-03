/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.cluster.ClusterState;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.hotrestart.HotRestartService;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.version.Version;
import java.util.Set;

public interface Cluster {
    public String addMembershipListener(MembershipListener var1);

    public boolean removeMembershipListener(String var1);

    public Set<Member> getMembers();

    public Member getLocalMember();

    public void promoteLocalLiteMember();

    public long getClusterTime();

    public ClusterState getClusterState();

    public void changeClusterState(ClusterState var1);

    public void changeClusterState(ClusterState var1, TransactionOptions var2);

    public Version getClusterVersion();

    public HotRestartService getHotRestartService();

    public void shutdown();

    public void shutdown(TransactionOptions var1);

    public void changeClusterVersion(Version var1);

    public void changeClusterVersion(Version var1, TransactionOptions var2);
}

