/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.IndeterminateOperationState;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutionException;

public class MemberLeftException
extends ExecutionException
implements RetryableException,
IndeterminateOperationState {
    private transient Member member;

    public MemberLeftException() {
    }

    public MemberLeftException(String message) {
        super(message);
    }

    public MemberLeftException(Member member) {
        super(member + " has left cluster!");
        this.member = member;
    }

    public MemberLeftException(Throwable cause) {
        super(cause);
    }

    public Member getMember() {
        return this.member;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Address address = this.member.getAddress();
        String host = address.getHost();
        int port = address.getPort();
        out.writeUTF(this.member.getUuid());
        out.writeUTF(host);
        out.writeInt(port);
        out.writeBoolean(this.member.isLiteMember());
        out.writeObject(this.member.getVersion());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String uuid = in.readUTF();
        String host = in.readUTF();
        int port = in.readInt();
        boolean liteMember = in.readBoolean();
        MemberVersion version = (MemberVersion)in.readObject();
        this.member = new MemberImpl.Builder(new Address(host, port)).version(version).uuid(uuid).liteMember(liteMember).build();
    }
}

