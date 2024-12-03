/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

abstract class BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    BackupSafeMemberGroupFactory() {
    }

    @Override
    public final Collection<MemberGroup> createMemberGroups(Collection<? extends Member> members) {
        Set<MemberGroup> groups = this.createInternalMemberGroups(members);
        if (groups.size() == 1 && members.size() > 1) {
            MemberGroup group1 = (MemberGroup)groups.iterator().next();
            DefaultMemberGroup group2 = new DefaultMemberGroup();
            int sizePerGroup = group1.size() / 2;
            Iterator<Member> iter = group1.iterator();
            while (group2.size() < sizePerGroup && iter.hasNext()) {
                group2.addMember(iter.next());
                iter.remove();
            }
            groups.add(group2);
        }
        return groups;
    }

    protected abstract Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> var1);
}

