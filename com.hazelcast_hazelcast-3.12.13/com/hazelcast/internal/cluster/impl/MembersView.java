/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.internal.cluster.impl.MemberMap;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MembersView
implements IdentifiedDataSerializable,
Versioned {
    private int version;
    private List<MemberInfo> members;

    public MembersView() {
    }

    public MembersView(int version, List<MemberInfo> members) {
        this.version = version;
        this.members = members;
    }

    static MembersView cloneAdding(MembersView source, Collection<MemberInfo> newMembers) {
        ArrayList<MemberInfo> list = new ArrayList<MemberInfo>(source.size() + newMembers.size());
        list.addAll(source.getMembers());
        int newVersion = Math.max(source.version, source.size());
        for (MemberInfo newMember : newMembers) {
            MemberInfo m = new MemberInfo(newMember.getAddress(), newMember.getUuid(), newMember.getAttributes(), newMember.isLiteMember(), newMember.getVersion(), ++newVersion, newMember.getAddressMap());
            list.add(m);
        }
        return new MembersView(newVersion, Collections.unmodifiableList(list));
    }

    public static MembersView createNew(int version, Collection<MemberImpl> members) {
        ArrayList<MemberInfo> list = new ArrayList<MemberInfo>(members.size());
        for (MemberImpl member : members) {
            list.add(new MemberInfo(member));
        }
        return new MembersView(version, Collections.unmodifiableList(list));
    }

    public List<MemberInfo> getMembers() {
        return this.members;
    }

    public int size() {
        return this.members.size();
    }

    public int getVersion() {
        return this.version;
    }

    MemberMap toMemberMap() {
        MemberImpl[] m = new MemberImpl[this.size()];
        int ix = 0;
        for (MemberInfo memberInfo : this.members) {
            m[ix++] = memberInfo.toMember();
        }
        return MemberMap.createNew(this.version, m);
    }

    public boolean containsAddress(Address address) {
        for (MemberInfo member : this.members) {
            if (!member.getAddress().equals(address)) continue;
            return true;
        }
        return false;
    }

    public boolean containsMember(Address address, String uuid) {
        for (MemberInfo member : this.members) {
            if (!member.getAddress().equals(address)) continue;
            return member.getUuid().equals(uuid);
        }
        return false;
    }

    public Set<Address> getAddresses() {
        HashSet<Address> addresses = new HashSet<Address>(this.members.size());
        for (MemberInfo member : this.members) {
            addresses.add(member.getAddress());
        }
        return addresses;
    }

    public MemberInfo getMember(Address address) {
        for (MemberInfo member : this.members) {
            if (!member.getAddress().equals(address)) continue;
            return member;
        }
        return null;
    }

    public boolean isLaterThan(MembersView other) {
        return this.version > other.version;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 38;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.version);
        out.writeInt(this.members.size());
        for (MemberInfo member : this.members) {
            member.writeData(out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.version = in.readInt();
        int size = in.readInt();
        ArrayList<MemberInfo> members = new ArrayList<MemberInfo>(size);
        for (int i = 0; i < size; ++i) {
            MemberInfo member = new MemberInfo();
            member.readData(in);
            members.add(member);
        }
        this.members = Collections.unmodifiableList(members);
    }

    public String toString() {
        return "MembersView{version=" + this.version + ", members=" + this.members + '}';
    }
}

