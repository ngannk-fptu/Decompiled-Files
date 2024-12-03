/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.partition.membergroup;

import com.hazelcast.core.Member;
import com.hazelcast.partition.membergroup.MemberGroup;
import java.util.Collection;
import java.util.Iterator;

public class SingleMemberGroup
implements MemberGroup {
    private Member member;

    public SingleMemberGroup() {
    }

    public SingleMemberGroup(Member member) {
        this.member = member;
    }

    @Override
    public void addMember(Member member) {
        if (this.member != null) {
            throw new UnsupportedOperationException();
        }
        this.member = member;
    }

    @Override
    public void addMembers(Collection<Member> members) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeMember(Member member) {
        if (this.member != null && this.member.equals(member)) {
            this.member = null;
        }
    }

    @Override
    public boolean hasMember(Member member) {
        return this.member != null && this.member.equals(member);
    }

    @Override
    public Iterator<Member> iterator() {
        return new MemberIterator();
    }

    @Override
    public int size() {
        return this.member != null ? 1 : 0;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (this.member == null ? 0 : this.member.hashCode());
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
        SingleMemberGroup other = (SingleMemberGroup)obj;
        return !(this.member == null ? other.member != null : !this.member.equals(other.member));
    }

    public String toString() {
        return "SingleMemberGroup{member=" + this.member + '}';
    }

    private class MemberIterator
    implements Iterator<Member> {
        boolean end;

        private MemberIterator() {
        }

        @Override
        public boolean hasNext() {
            return !this.end;
        }

        @Override
        public Member next() {
            if (this.hasNext()) {
                this.end = true;
                return SingleMemberGroup.this.member;
            }
            return null;
        }

        @Override
        public void remove() {
            if (this.end) {
                SingleMemberGroup.this.member = null;
            }
        }
    }
}

