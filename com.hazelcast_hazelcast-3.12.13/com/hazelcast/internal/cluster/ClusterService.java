/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster;

import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterClock;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.CoreService;
import java.util.Collection;

public interface ClusterService
extends CoreService,
Cluster {
    public MemberImpl getMember(Address var1);

    public MemberImpl getMember(String var1);

    public MemberImpl getMember(Address var1, String var2);

    public Collection<MemberImpl> getMemberImpls();

    public Collection<Member> getMembers(MemberSelector var1);

    public Address getMasterAddress();

    public boolean isMaster();

    public boolean isJoined();

    public Address getThisAddress();

    @Override
    public Member getLocalMember();

    public int getSize();

    public int getSize(MemberSelector var1);

    public ClusterClock getClusterClock();

    public String getClusterId();

    public int getMemberListVersion();

    public int getMemberListJoinVersion();
}

