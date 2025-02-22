/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.UUID;

public abstract class Invalidation
implements IMapEvent,
IdentifiedDataSerializable {
    private String dataStructureName;
    private String sourceUuid;
    private UUID partitionUuid;
    private long sequence = -1L;

    public Invalidation() {
    }

    public Invalidation(String dataStructureName) {
        this.dataStructureName = Preconditions.checkNotNull(dataStructureName, "dataStructureName cannot be null");
    }

    public Invalidation(String dataStructureName, String sourceUuid, UUID partitionUuid, long sequence) {
        this.dataStructureName = Preconditions.checkNotNull(dataStructureName, "dataStructureName cannot be null");
        this.sourceUuid = sourceUuid;
        this.partitionUuid = Preconditions.checkNotNull(partitionUuid, "partitionUuid cannot be null");
        this.sequence = Preconditions.checkPositive(sequence, "sequence should be positive");
    }

    public final UUID getPartitionUuid() {
        return this.partitionUuid;
    }

    public final String getSourceUuid() {
        return this.sourceUuid;
    }

    public final long getSequence() {
        return this.sequence;
    }

    public Data getKey() {
        throw new UnsupportedOperationException("getKey is not supported");
    }

    @Override
    public final String getName() {
        return this.dataStructureName;
    }

    @Override
    public Member getMember() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntryEventType getEventType() {
        return EntryEventType.INVALIDATION;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.dataStructureName);
        out.writeUTF(this.sourceUuid);
        out.writeLong(this.sequence);
        boolean nullUuid = this.partitionUuid == null;
        out.writeBoolean(nullUuid);
        if (!nullUuid) {
            out.writeLong(this.partitionUuid.getMostSignificantBits());
            out.writeLong(this.partitionUuid.getLeastSignificantBits());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.dataStructureName = in.readUTF();
        this.sourceUuid = in.readUTF();
        this.sequence = in.readLong();
        boolean nullUuid = in.readBoolean();
        if (!nullUuid) {
            this.partitionUuid = new UUID(in.readLong(), in.readLong());
        }
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    public String toString() {
        return "dataStructureName='" + this.dataStructureName + "', sourceUuid='" + this.sourceUuid + "', partitionUuid='" + this.partitionUuid + ", sequence=" + this.sequence;
    }
}

