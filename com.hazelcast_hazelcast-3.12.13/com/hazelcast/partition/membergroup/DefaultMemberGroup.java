/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.MemberGroup;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DefaultMemberGroup
implements MemberGroup {
    private final Set<Member> members = new HashSet<Member>();

    public DefaultMemberGroup() {
    }

    public DefaultMemberGroup(Collection<Member> members) {
        this.addMembers(members);
    }

    @Override
    public void addMember(Member member) {
        this.members.add(member);
    }

    @Override
    public void addMembers(Collection<Member> members) {
        this.members.addAll(members);
    }

    @Override
    public void removeMember(Member member) {
        this.members.remove(member);
    }

    @Override
    public boolean hasMember(Member member) {
        return this.members.contains(member);
    }

    public Set<Member> getMembers() {
        return this.members;
    }

    @Override
    public Iterator<Member> iterator() {
        return this.members.iterator();
    }

    @Override
    public int size() {
        return this.members.size();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + this.members.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        DefaultMemberGroup other = (DefaultMemberGroup)obj;
        return this.members.equals(other.members);
    }

    public String toString() {
        return "DefaultMemberGroup{members=" + this.members + '}';
    }
}

