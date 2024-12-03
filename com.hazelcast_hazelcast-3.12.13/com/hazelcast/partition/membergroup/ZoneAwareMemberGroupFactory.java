/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.BackupSafeMemberGroupFactory;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ZoneAwareMemberGroupFactory
extends BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    @Override
    protected Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> allMembers) {
        HashMap<String, MemberGroup> groups = new HashMap<String, MemberGroup>();
        for (Member member : allMembers) {
            MemberGroup group;
            String zoneInfo = member.getStringAttribute("hazelcast.partition.group.zone");
            String rackInfo = member.getStringAttribute("hazelcast.partition.group.rack");
            String hostInfo = member.getStringAttribute("hazelcast.partition.group.host");
            if (zoneInfo == null && rackInfo == null && hostInfo == null) {
                throw new IllegalArgumentException("Not enough metadata information is provided. At least one of availability zone, rack or host information must be provided with ZONE_AWARE partition group.");
            }
            if (zoneInfo != null) {
                group = (MemberGroup)groups.get(zoneInfo);
                if (group == null) {
                    group = new DefaultMemberGroup();
                    groups.put(zoneInfo, group);
                }
                group.addMember(member);
                continue;
            }
            if (rackInfo != null) {
                group = (MemberGroup)groups.get(rackInfo);
                if (group == null) {
                    group = new DefaultMemberGroup();
                    groups.put(rackInfo, group);
                }
                group.addMember(member);
                continue;
            }
            if (hostInfo == null) continue;
            group = (MemberGroup)groups.get(hostInfo);
            if (group == null) {
                group = new DefaultMemberGroup();
                groups.put(hostInfo, group);
            }
            group.addMember(member);
        }
        return new HashSet<MemberGroup>(groups.values());
    }
}

