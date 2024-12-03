/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Member;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.partition.PartitionEvent;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
public class MigrationEvent
implements DataSerializable,
PartitionEvent {
    private int partitionId;
    private Member oldOwner;
    private Member newOwner;
    private MigrationStatus status;

    public MigrationEvent() {
    }

    public MigrationEvent(int partitionId, Member oldOwner, Member newOwner, MigrationStatus status) {
        this.partitionId = partitionId;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
        this.status = status;
    }

    @Override
    public int getPartitionId() {
        return this.partitionId;
    }

    public Member getOldOwner() {
        return this.oldOwner;
    }

    public Member getNewOwner() {
        return this.newOwner;
    }

    public MigrationStatus getStatus() {
        return this.status;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.partitionId);
        out.writeObject(this.oldOwner);
        out.writeObject(this.newOwner);
        MigrationStatus.writeTo(this.status, out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.partitionId = in.readInt();
        this.oldOwner = (Member)in.readObject();
        this.newOwner = (Member)in.readObject();
        this.status = MigrationStatus.readFrom(in);
    }

    public String toString() {
        return "MigrationEvent{partitionId=" + this.partitionId + ", status=" + (Object)((Object)this.status) + ", oldOwner=" + this.oldOwner + ", newOwner=" + this.newOwner + '}';
    }

    public static enum MigrationStatus {
        STARTED(0),
        COMPLETED(1),
        FAILED(-1);

        private final byte code;

        private MigrationStatus(int code) {
            this.code = (byte)code;
        }

        public static void writeTo(MigrationStatus status, DataOutput out) throws IOException {
            out.writeByte(status.code);
        }

        public static MigrationStatus readFrom(DataInput in) throws IOException {
            byte code = in.readByte();
            switch (code) {
                case 0: {
                    return STARTED;
                }
                case 1: {
                    return COMPLETED;
                }
                case -1: {
                    return FAILED;
                }
            }
            return null;
        }
    }
}

