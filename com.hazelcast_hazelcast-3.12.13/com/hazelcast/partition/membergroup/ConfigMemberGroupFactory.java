/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.partition.membergroup.BackupSafeMemberGroupFactory;
import com.hazelcast.partition.membergroup.DefaultMemberGroup;
import com.hazelcast.partition.membergroup.MemberGroup;
import com.hazelcast.partition.membergroup.MemberGroupFactory;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigMemberGroupFactory
extends BackupSafeMemberGroupFactory
implements MemberGroupFactory {
    private final Map<Integer, MemberGroupConfig> memberGroupConfigMap;

    public ConfigMemberGroupFactory(Collection<MemberGroupConfig> memberGroupConfigs) {
        this.memberGroupConfigMap = MapUtil.createLinkedHashMap(memberGroupConfigs.size());
        int key = 0;
        for (MemberGroupConfig groupConfig : memberGroupConfigs) {
            this.memberGroupConfigMap.put(key++, groupConfig);
        }
    }

    @Override
    protected Set<MemberGroup> createInternalMemberGroups(Collection<? extends Member> members) {
        HashMap<Integer, MemberGroup> memberGroups = new HashMap<Integer, MemberGroup>();
        block0: for (Member member : members) {
            String host = ((MemberImpl)member).getAddress().getHost();
            for (Map.Entry<Integer, MemberGroupConfig> entry : this.memberGroupConfigMap.entrySet()) {
                Collection<String> interfaces = entry.getValue().getInterfaces();
                boolean match = AddressUtil.isIpAddress(host) ? AddressUtil.matchAnyInterface(host, interfaces) : AddressUtil.matchAnyDomain(host, interfaces);
                if (!match) continue;
                MemberGroup group = (MemberGroup)memberGroups.get(entry.getKey());
                if (group == null) {
                    group = new DefaultMemberGroup();
                    memberGroups.put(entry.getKey(), group);
                }
                group.addMember(member);
                continue block0;
            }
        }
        return new HashSet<MemberGroup>(memberGroups.values());
    }
}

