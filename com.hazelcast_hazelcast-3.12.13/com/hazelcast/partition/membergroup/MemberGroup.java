/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import java.util.Collection;
import java.util.Iterator;

public interface MemberGroup {
    public void addMember(Member var1);

    public void addMembers(Collection<Member> var1);

    public void removeMember(Member var1);

    public boolean hasMember(Member var1);

    public Iterator<Member> iterator();

    public int size();
}

