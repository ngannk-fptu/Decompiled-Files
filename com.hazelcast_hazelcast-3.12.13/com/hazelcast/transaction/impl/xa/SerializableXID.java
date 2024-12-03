/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.Arrays;
import javax.transaction.xa.Xid;

@BinaryInterface
public class SerializableXID
implements Xid,
DataSerializable {
    private int formatId;
    private byte[] globalTransactionId;
    private byte[] branchQualifier;

    public SerializableXID() {
    }

    public SerializableXID(int formatId, byte[] globalTransactionId, byte[] branchQualifier) {
        this.formatId = formatId;
        this.globalTransactionId = Arrays.copyOf(globalTransactionId, globalTransactionId.length);
        this.branchQualifier = Arrays.copyOf(branchQualifier, branchQualifier.length);
    }

    @Override
    public int getFormatId() {
        return this.formatId;
    }

    @Override
    public byte[] getGlobalTransactionId() {
        return Arrays.copyOf(this.globalTransactionId, this.globalTransactionId.length);
    }

    @Override
    public byte[] getBranchQualifier() {
        return Arrays.copyOf(this.branchQualifier, this.branchQualifier.length);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.formatId);
        out.writeInt(this.globalTransactionId.length);
        out.write(this.globalTransactionId);
        out.writeInt(this.branchQualifier.length);
        out.write(this.branchQualifier);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.formatId = in.readInt();
        int gtiLen = in.readInt();
        this.globalTransactionId = new byte[gtiLen];
        in.readFully(this.globalTransactionId);
        int bqLen = in.readInt();
        this.branchQualifier = new byte[bqLen];
        in.readFully(this.branchQualifier);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Xid)) {
            return false;
        }
        Xid that = (Xid)o;
        if (this.formatId != that.getFormatId()) {
            return false;
        }
        if (!Arrays.equals(this.branchQualifier, that.getBranchQualifier())) {
            return false;
        }
        return Arrays.equals(this.globalTransactionId, that.getGlobalTransactionId());
    }

    public int hashCode() {
        int result = this.formatId;
        result = 31 * result + Arrays.hashCode(this.globalTransactionId);
        result = 31 * result + Arrays.hashCode(this.branchQualifier);
        return result;
    }

    public String toString() {
        return "SerializableXid{formatId=" + this.formatId + ", globalTransactionId=" + Arrays.toString(this.globalTransactionId) + ", branchQualifier=" + Arrays.toString(this.branchQualifier) + '}';
    }
}

