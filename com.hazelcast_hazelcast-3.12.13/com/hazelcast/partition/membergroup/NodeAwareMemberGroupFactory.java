/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.BackupSafeMemberGroupFactory;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeAwareMemberGroupFactory
extends BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    @Override
    protected Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> allMembers) {
        Map<String, MemberGroup> groups = MapUtil.createHashMap(allMembers.size());
        for (Member member : allMembers) {
            String nodeInfo = member.getStringAttribute("hazelcast.partition.group.node");
            if (nodeInfo == null) {
                throw new IllegalArgumentException("Not enough metadata information is provided. Node name information must be provided with NODE_AWARE partition group.");
            }
            MemberGroup group = (MemberGroup)groups.get(nodeInfo);
            if (group == null) {
                group = new DefaultMemberGroup();
                groups.put(nodeInfo, group);
            }
            group.addMember(member);
        }
        return new HashSet<MemberGroup>(groups.values());
    }
}

