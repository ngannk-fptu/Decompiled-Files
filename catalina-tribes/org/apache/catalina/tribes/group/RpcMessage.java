/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import org.apache.catalina.tribes.util.Arrays;

public class RpcMessage
implements Externalizable {
    protected Serializable message;
    protected byte[] uuid;
    protected byte[] rpcId;
    protected boolean reply = false;

    public RpcMessage() {
    }

    public RpcMessage(byte[] rpcId, byte[] uuid, Serializable message) {
        this.rpcId = rpcId;
        this.uuid = uuid;
        this.message = message;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.reply = in.readBoolean();
        int length = in.readInt();
        this.uuid = new byte[length];
        in.readFully(this.uuid);
        length = in.readInt();
        this.rpcId = new byte[length];
        in.readFully(this.rpcId);
        this.message = (Serializable)in.readObject();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(this.reply);
        out.writeInt(this.uuid.length);
        out.write(this.uuid, 0, this.uuid.length);
        out.writeInt(this.rpcId.length);
        out.write(this.rpcId, 0, this.rpcId.length);
        out.writeObject(this.message);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("RpcMessage[");
        buf.append(super.toString());
        buf.append("] rpcId=");
        buf.append(Arrays.toString(this.rpcId));
        buf.append("; uuid=");
        buf.append(Arrays.toString(this.uuid));
        buf.append("; msg=");
        buf.append(this.message);
        return buf.toString();
    }

    public static class NoRpcChannelReply
    extends RpcMessage {
        public NoRpcChannelReply() {
        }

        public NoRpcChannelReply(byte[] rpcid, byte[] uuid) {
            super(rpcid, uuid, null);
            this.reply = true;
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.reply = true;
            int length = in.readInt();
            this.uuid = new byte[length];
            in.readFully(this.uuid);
            length = in.readInt();
            this.rpcId = new byte[length];
            in.readFully(this.rpcId);
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(this.uuid.length);
            out.write(this.uuid, 0, this.uuid.length);
            out.writeInt(this.rpcId.length);
            out.write(this.rpcId, 0, this.rpcId.length);
        }
    }
}

