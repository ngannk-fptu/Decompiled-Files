/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.membergroup.BackupSafeMemberGroupFactory;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HostAwareMemberGroupFactory
extends BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    @Override
    protected Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> allMembers) {
        Map<String, MemberGroup> groups = MapUtil.createHashMap(allMembers.size());
        for (Member member : allMembers) {
            Address address = ((MemberImpl)member).getAddress();
            MemberGroup group = (MemberGroup)groups.get(address.getHost());
            if (group == null) {
                group = new DefaultMemberGroup();
                groups.put(address.getHost(), group);
            }
            group.addMember(member);
        }
        return new HashSet<MemberGroup>(groups.values());
    }
}

