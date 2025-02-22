/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.internal.CPGroupInfo;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.MembershipChangeSchedule;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class MetadataRaftGroupSnapshot
implements IdentifiedDataSerializable {
    private final Collection<CPMemberInfo> members = new ArrayList<CPMemberInfo>();
    private long membersCommitIndex;
    private final Collection<CPGroupInfo> groups = new ArrayList<CPGroupInfo>();
    private MembershipChangeSchedule membershipChangeSchedule;
    private List<CPMemberInfo> initialCPMembers;
    private Set<CPMemberInfo> initializedCPMembers = new HashSet<CPMemberInfo>();
    private MetadataRaftGroupManager.MetadataRaftGroupInitStatus initializationStatus;
    private Set<Long> initializationCommitIndices = new HashSet<Long>();

    public void setGroups(Collection<CPGroupInfo> groups) {
        for (CPGroupInfo group : groups) {
            this.groups.add(new CPGroupInfo(group));
        }
    }

    public void setMembers(Collection<CPMemberInfo> members) {
        this.members.addAll(members);
    }

    public Collection<CPMemberInfo> getMembers() {
        return this.members;
    }

    public long getMembersCommitIndex() {
        return this.membersCommitIndex;
    }

    public void setMembersCommitIndex(long membersCommitIndex) {
        this.membersCommitIndex = membersCommitIndex;
    }

    public Collection<CPGroupInfo> getGroups() {
        return this.groups;
    }

    public MembershipChangeSchedule getMembershipChangeSchedule() {
        return this.membershipChangeSchedule;
    }

    public void setMembershipChangeSchedule(MembershipChangeSchedule membershipChangeSchedule) {
        this.membershipChangeSchedule = membershipChangeSchedule;
    }

    public Set<CPMemberInfo> getInitializedCPMembers() {
        return this.initializedCPMembers;
    }

    public void setInitializedCPMembers(Collection<CPMemberInfo> initializedCPMembers) {
        this.initializedCPMembers.addAll(initializedCPMembers);
    }

    public List<CPMemberInfo> getInitialCPMembers() {
        return this.initialCPMembers;
    }

    public void setInitialCPMembers(List<CPMemberInfo> initialCPMembers) {
        this.initialCPMembers = initialCPMembers;
    }

    public MetadataRaftGroupManager.MetadataRaftGroupInitStatus getInitializationStatus() {
        return this.initializationStatus;
    }

    public void setInitializationStatus(MetadataRaftGroupManager.MetadataRaftGroupInitStatus initializationStatus) {
        this.initializationStatus = initializationStatus;
    }

    public Set<Long> getInitializationCommitIndices() {
        return this.initializationCommitIndices;
    }

    public void setInitializationCommitIndices(Set<Long> initializationCommitIndices) {
        this.initializationCommitIndices.addAll(initializationCommitIndices);
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 10;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.members.size());
        for (CPMemberInfo member : this.members) {
            out.writeObject(member);
        }
        out.writeLong(this.membersCommitIndex);
        out.writeInt(this.groups.size());
        for (CPGroupInfo group : this.groups) {
            out.writeObject(group);
        }
        out.writeObject(this.membershipChangeSchedule);
        boolean discoveredInitialCPMembers = this.initialCPMembers != null;
        out.writeBoolean(discoveredInitialCPMembers);
        if (discoveredInitialCPMembers) {
            out.writeInt(this.initialCPMembers.size());
            for (CPMemberInfo member : this.initialCPMembers) {
                out.writeObject(member);
            }
        }
        out.writeInt(this.initializedCPMembers.size());
        for (CPMemberInfo member : this.initializedCPMembers) {
            out.writeObject(member);
        }
        out.writeUTF(this.initializationStatus.name());
        out.writeInt(this.initializationCommitIndices.size());
        Iterator<Serializable> iterator = this.initializationCommitIndices.iterator();
        while (iterator.hasNext()) {
            long commitIndex = (Long)iterator.next();
            out.writeLong(commitIndex);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int i;
        CPMemberInfo member;
        int i2;
        int len = in.readInt();
        for (i2 = 0; i2 < len; ++i2) {
            CPMemberInfo member2 = (CPMemberInfo)in.readObject();
            this.members.add(member2);
        }
        this.membersCommitIndex = in.readLong();
        len = in.readInt();
        for (i2 = 0; i2 < len; ++i2) {
            CPGroupInfo group = (CPGroupInfo)in.readObject();
            this.groups.add(group);
        }
        this.membershipChangeSchedule = (MembershipChangeSchedule)in.readObject();
        boolean discoveredInitialCPMembers = in.readBoolean();
        if (discoveredInitialCPMembers) {
            len = in.readInt();
            this.initialCPMembers = new ArrayList<CPMemberInfo>(len);
            for (int i3 = 0; i3 < len; ++i3) {
                member = (CPMemberInfo)in.readObject();
                this.initialCPMembers.add(member);
            }
        }
        len = in.readInt();
        for (i = 0; i < len; ++i) {
            member = (CPMemberInfo)in.readObject();
            this.initializedCPMembers.add(member);
        }
        this.initializationStatus = MetadataRaftGroupManager.MetadataRaftGroupInitStatus.valueOf(in.readUTF());
        len = in.readInt();
        for (i = 0; i < len; ++i) {
            long commitIndex = in.readLong();
            this.initializationCommitIndices.add(commitIndex);
        }
    }
}

