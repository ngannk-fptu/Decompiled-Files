/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.MemberGroup;
import java.util.Collection;

public interface MemberGroupFactory {
    public Collection<MemberGroup> createMemberGroups(Collection<? extends Member> var1);
}

