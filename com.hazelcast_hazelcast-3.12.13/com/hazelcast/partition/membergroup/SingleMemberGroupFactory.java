/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.partition.membergroup.SingleMemberGroup;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.Set;

public class SingleMemberGroupFactory
implements MemberGroupFactory {
    public Set<MemberGroup> createMemberGroups(Collection<? extends Member> members) {
        Set<MemberGroup> groups = SetUtil.createHashSet(members.size());
        for (Member member : members) {
            groups.add(new SingleMemberGroup(member));
        }
        return groups;
    }
}

