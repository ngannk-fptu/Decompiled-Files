/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.command;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.command.RaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

public class UpdateRaftGroupMembersCmd
extends RaftGroupCmd
implements IdentifiedDataSerializable {
    private Collection<Endpoint> members;
    private Endpoint member;
    private MembershipChangeMode mode;

    public UpdateRaftGroupMembersCmd() {
    }

    public UpdateRaftGroupMembersCmd(Collection<Endpoint> members, Endpoint member, MembershipChangeMode mode) {
        this.members = members;
        this.member = member;
        this.mode = mode;
    }

    public Collection<Endpoint> getMembers() {
        return this.members;
    }

    public Endpoint getMember() {
        return this.member;
    }

    public MembershipChangeMode getMode() {
        return this.mode;
    }

    @Override
    public int getFactoryId() {
        return RaftDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.members.size());
        for (Endpoint member : this.members) {
            out.writeObject(member);
        }
        out.writeObject(this.member);
        out.writeUTF(this.mode.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int count = in.readInt();
        LinkedHashSet<Endpoint> members = new LinkedHashSet<Endpoint>();
        for (int i = 0; i < count; ++i) {
            Endpoint member = (Endpoint)in.readObject();
            members.add(member);
        }
        this.members = members;
        this.member = (Endpoint)in.readObject();
        this.mode = MembershipChangeMode.valueOf(in.readUTF());
    }

    public String toString() {
        return "ChangeRaftGroupMembersCmd{members=" + this.members + ", member=" + this.member + ", mode=" + (Object)((Object)this.mode) + '}';
    }
}

