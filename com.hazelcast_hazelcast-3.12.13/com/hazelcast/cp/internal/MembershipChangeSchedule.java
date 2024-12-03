/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MembershipChangeSchedule
implements IdentifiedDataSerializable {
    private List<Long> membershipChangeCommitIndices;
    private CPMemberInfo member;
    private MembershipChangeMode membershipChangeMode;
    private final List<CPGroupMembershipChange> changes = new ArrayList<CPGroupMembershipChange>();

    MembershipChangeSchedule() {
    }

    private MembershipChangeSchedule(List<Long> membershipChangeCommitIndices, CPMemberInfo member, MembershipChangeMode membershipChangeMode, List<CPGroupMembershipChange> changes) {
        this.membershipChangeCommitIndices = membershipChangeCommitIndices;
        this.member = member;
        this.membershipChangeMode = membershipChangeMode;
        this.changes.addAll(changes);
    }

    CPMemberInfo getAddedMember() {
        return this.membershipChangeMode == MembershipChangeMode.ADD ? this.member : null;
    }

    CPMemberInfo getLeavingMember() {
        return this.membershipChangeMode == MembershipChangeMode.REMOVE ? this.member : null;
    }

    List<CPGroupMembershipChange> getChanges() {
        return Collections.unmodifiableList(this.changes);
    }

    MembershipChangeSchedule excludeCompletedChanges(Collection<CPGroupId> completedGroupIds) {
        Preconditions.checkNotNull(completedGroupIds);
        ArrayList<CPGroupMembershipChange> remainingChanges = new ArrayList<CPGroupMembershipChange>(this.changes);
        Iterator it = remainingChanges.iterator();
        while (it.hasNext()) {
            CPGroupMembershipChange change = (CPGroupMembershipChange)it.next();
            if (!completedGroupIds.contains(change.groupId)) continue;
            it.remove();
        }
        return new MembershipChangeSchedule(this.membershipChangeCommitIndices, this.member, this.membershipChangeMode, remainingChanges);
    }

    List<Long> getMembershipChangeCommitIndices() {
        return this.membershipChangeCommitIndices;
    }

    MembershipChangeSchedule addRetriedCommitIndex(long commitIndex) {
        ArrayList<Long> membershipChangeCommitIndices = new ArrayList<Long>(this.membershipChangeCommitIndices);
        membershipChangeCommitIndices.add(commitIndex);
        return new MembershipChangeSchedule(membershipChangeCommitIndices, this.member, this.membershipChangeMode, this.changes);
    }

    static MembershipChangeSchedule forJoiningMember(List<Long> membershipChangeCommitIndices, CPMemberInfo member, List<CPGroupMembershipChange> changes) {
        return new MembershipChangeSchedule(membershipChangeCommitIndices, member, MembershipChangeMode.ADD, changes);
    }

    static MembershipChangeSchedule forLeavingMember(List<Long> membershipChangeCommitIndices, CPMemberInfo member, List<CPGroupMembershipChange> changes) {
        return new MembershipChangeSchedule(membershipChangeCommitIndices, member, MembershipChangeMode.REMOVE, changes);
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 19;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.membershipChangeCommitIndices.size());
        Iterator<Object> iterator = this.membershipChangeCommitIndices.iterator();
        while (iterator.hasNext()) {
            long commitIndex = iterator.next();
            out.writeLong(commitIndex);
        }
        out.writeObject(this.member);
        out.writeUTF(this.membershipChangeMode.name());
        out.writeInt(this.changes.size());
        for (CPGroupMembershipChange change : this.changes) {
            out.writeObject(change);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int membershipChangeCommitIndexCount = in.readInt();
        this.membershipChangeCommitIndices = new ArrayList<Long>(membershipChangeCommitIndexCount);
        for (int i = 0; i < membershipChangeCommitIndexCount; ++i) {
            long commitIndex = in.readLong();
            this.membershipChangeCommitIndices.add(commitIndex);
        }
        this.member = (CPMemberInfo)in.readObject();
        this.membershipChangeMode = MembershipChangeMode.valueOf(in.readUTF());
        int groupCount = in.readInt();
        for (int i = 0; i < groupCount; ++i) {
            CPGroupMembershipChange change = (CPGroupMembershipChange)in.readObject();
            this.changes.add(change);
        }
    }

    public String toString() {
        return "MembershipChangeSchedule{membershipChangeCommitIndices=" + this.membershipChangeCommitIndices + ", member=" + this.member + ", membershipChangeMode=" + (Object)((Object)this.membershipChangeMode) + ", changes=" + this.changes + '}';
    }

    public static class CPGroupMembershipChange
    implements IdentifiedDataSerializable {
        private CPGroupId groupId;
        private long membersCommitIndex;
        private Collection<CPMemberInfo> members;
        private CPMemberInfo memberToAdd;
        private CPMemberInfo memberToRemove;

        CPGroupMembershipChange() {
        }

        CPGroupMembershipChange(CPGroupId groupId, long membersCommitIndex, Collection<CPMemberInfo> members, CPMemberInfo memberToAdd, CPMemberInfo memberToRemove) {
            this.groupId = groupId;
            this.membersCommitIndex = membersCommitIndex;
            this.members = members;
            this.memberToAdd = memberToAdd;
            this.memberToRemove = memberToRemove;
        }

        CPGroupId getGroupId() {
            return this.groupId;
        }

        long getMembersCommitIndex() {
            return this.membersCommitIndex;
        }

        Collection<CPMemberInfo> getMembers() {
            return this.members;
        }

        CPMemberInfo getMemberToAdd() {
            return this.memberToAdd;
        }

        CPMemberInfo getMemberToRemove() {
            return this.memberToRemove;
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeLong(this.membersCommitIndex);
            out.writeInt(this.members.size());
            for (CPMemberInfo member : this.members) {
                out.writeObject(member);
            }
            out.writeObject(this.memberToAdd);
            out.writeObject(this.memberToRemove);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            this.membersCommitIndex = in.readLong();
            int len = in.readInt();
            this.members = new HashSet<CPMemberInfo>(len);
            for (int i = 0; i < len; ++i) {
                CPMemberInfo member = (CPMemberInfo)in.readObject();
                this.members.add(member);
            }
            this.memberToAdd = (CPMemberInfo)in.readObject();
            this.memberToRemove = (CPMemberInfo)in.readObject();
        }

        @Override
        public int getFactoryId() {
            return RaftServiceDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 41;
        }

        public String toString() {
            return "CPGroupMembershipChange{groupId=" + this.groupId + ", membersCommitIndex=" + this.membersCommitIndex + ", members=" + this.members + ", memberToAdd=" + this.memberToAdd + ", memberToRemove=" + this.memberToRemove + '}';
        }
    }
}

