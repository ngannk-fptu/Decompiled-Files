/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroup;
import com.hazelcast.cp.CPMember;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class CPGroupInfo
implements CPGroup,
IdentifiedDataSerializable {
    private RaftGroupId id;
    private Set<CPMemberInfo> initialMembers;
    private Set<CPMemberInfo> members;
    private long membersCommitIndex;
    private volatile CPGroup.CPGroupStatus status;
    private transient CPMemberInfo[] membersArray;

    public CPGroupInfo() {
    }

    public CPGroupInfo(RaftGroupId id, Collection<CPMemberInfo> members) {
        this.id = id;
        this.status = CPGroup.CPGroupStatus.ACTIVE;
        this.initialMembers = Collections.unmodifiableSet(new LinkedHashSet<CPMemberInfo>(members));
        this.members = Collections.unmodifiableSet(new LinkedHashSet<CPMemberInfo>(members));
        this.membersArray = members.toArray(new CPMemberInfo[0]);
    }

    CPGroupInfo(CPGroupInfo other) {
        this.id = other.id;
        this.status = other.status;
        this.membersCommitIndex = other.membersCommitIndex;
        this.initialMembers = Collections.unmodifiableSet(new LinkedHashSet<CPMemberInfo>(other.initialMembers));
        this.members = Collections.unmodifiableSet(new LinkedHashSet<CPMemberInfo>(other.members));
        this.membersArray = Arrays.copyOf(other.membersArray, other.membersArray.length);
    }

    @Override
    public RaftGroupId id() {
        return this.id;
    }

    public String name() {
        return this.id.name();
    }

    public int initialMemberCount() {
        return this.initialMembers.size();
    }

    @Override
    public Collection<CPMember> members() {
        return this.members;
    }

    public Collection<CPMemberInfo> memberImpls() {
        return this.members;
    }

    public Collection<CPMember> initialMembers() {
        return this.initialMembers;
    }

    public boolean containsMember(CPMemberInfo member) {
        return this.members.contains(member);
    }

    public int memberCount() {
        return this.members.size();
    }

    @Override
    public CPGroup.CPGroupStatus status() {
        return this.status;
    }

    public boolean setDestroying() {
        if (this.status == CPGroup.CPGroupStatus.DESTROYED) {
            return false;
        }
        this.status = CPGroup.CPGroupStatus.DESTROYING;
        return true;
    }

    public boolean setDestroyed() {
        Preconditions.checkState(this.status != CPGroup.CPGroupStatus.ACTIVE, "Cannot destroy " + this.id + " because status is: " + (Object)((Object)this.status));
        return this.forceSetDestroyed();
    }

    public boolean forceSetDestroyed() {
        if (this.status == CPGroup.CPGroupStatus.DESTROYED) {
            return false;
        }
        this.status = CPGroup.CPGroupStatus.DESTROYED;
        return true;
    }

    public long getMembersCommitIndex() {
        return this.membersCommitIndex;
    }

    public boolean applyMembershipChange(CPMemberInfo leaving, CPMemberInfo joining, long expectedMembersCommitIndex, long newMembersCommitIndex) {
        Preconditions.checkState(this.status == CPGroup.CPGroupStatus.ACTIVE, "Cannot apply membership change of Leave: " + leaving + " and Join: " + joining + " since status is: " + (Object)((Object)this.status));
        if (this.membersCommitIndex != expectedMembersCommitIndex) {
            return false;
        }
        LinkedHashSet<CPMemberInfo> m = new LinkedHashSet<CPMemberInfo>(this.members);
        if (leaving != null) {
            boolean removed = m.remove(leaving);
            assert (removed) : leaving + " is not member of " + this.toString();
        }
        if (joining != null) {
            boolean added = m.add(joining);
            assert (added) : joining + " is already member of " + this.toString();
        }
        this.members = Collections.unmodifiableSet(m);
        this.membersCommitIndex = newMembersCommitIndex;
        this.membersArray = this.members.toArray(new CPMemberInfo[0]);
        return true;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="Returning internal array intentionally to avoid performance penalty.")
    public CPMemberInfo[] membersArray() {
        return this.membersArray;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.id);
        out.writeInt(this.initialMembers.size());
        for (CPMemberInfo member : this.initialMembers) {
            out.writeObject(member);
        }
        out.writeLong(this.membersCommitIndex);
        out.writeInt(this.members.size());
        for (CPMemberInfo member : this.members) {
            out.writeObject(member);
        }
        out.writeUTF(this.status.toString());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = (RaftGroupId)in.readObject();
        int initialMemberCount = in.readInt();
        LinkedHashSet<CPMemberInfo> initialMembers = new LinkedHashSet<CPMemberInfo>();
        for (int i = 0; i < initialMemberCount; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            initialMembers.add(member);
        }
        this.initialMembers = Collections.unmodifiableSet(initialMembers);
        this.membersCommitIndex = in.readLong();
        int memberCount = in.readInt();
        this.members = new LinkedHashSet<CPMemberInfo>(memberCount);
        for (int i = 0; i < memberCount; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            this.members.add(member);
        }
        this.membersArray = this.members.toArray(new CPMemberInfo[0]);
        this.members = Collections.unmodifiableSet(this.members);
        this.status = CPGroup.CPGroupStatus.valueOf(in.readUTF());
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    public String toString() {
        return "CPGroupInfo{id=" + this.id + ", initialMembers=" + this.initialMembers + ", membersCommitIndex=" + this.membersCommitIndex + ", members=" + this.members() + ", status=" + (Object)((Object)this.status) + '}';
    }
}

