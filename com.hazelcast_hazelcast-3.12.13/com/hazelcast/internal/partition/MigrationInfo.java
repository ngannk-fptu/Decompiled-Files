/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition;

import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.PartitionReplica;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.Version;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MigrationInfo
implements IdentifiedDataSerializable,
Versioned {
    private String uuid;
    private int partitionId;
    private PartitionReplica source;
    private PartitionReplica destination;
    private Address master;
    private int sourceCurrentReplicaIndex;
    private int sourceNewReplicaIndex;
    private int destinationCurrentReplicaIndex;
    private int destinationNewReplicaIndex;
    private int initialPartitionVersion = -1;
    private int partitionVersionIncrement;
    private final AtomicBoolean processing = new AtomicBoolean(false);
    private volatile MigrationStatus status;

    public MigrationInfo() {
    }

    public MigrationInfo(int partitionId, PartitionReplica source, PartitionReplica destination, int sourceCurrentReplicaIndex, int sourceNewReplicaIndex, int destinationCurrentReplicaIndex, int destinationNewReplicaIndex) {
        this.uuid = UuidUtil.newUnsecureUuidString();
        this.partitionId = partitionId;
        this.source = source;
        this.destination = destination;
        this.sourceCurrentReplicaIndex = sourceCurrentReplicaIndex;
        this.sourceNewReplicaIndex = sourceNewReplicaIndex;
        this.destinationCurrentReplicaIndex = destinationCurrentReplicaIndex;
        this.destinationNewReplicaIndex = destinationNewReplicaIndex;
        this.status = MigrationStatus.ACTIVE;
    }

    public PartitionReplica getSource() {
        return this.source;
    }

    public Address getSourceAddress() {
        return this.source != null ? this.source.address() : null;
    }

    public PartitionReplica getDestination() {
        return this.destination;
    }

    public Address getDestinationAddress() {
        return this.destination != null ? this.destination.address() : null;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getSourceCurrentReplicaIndex() {
        return this.sourceCurrentReplicaIndex;
    }

    public int getSourceNewReplicaIndex() {
        return this.sourceNewReplicaIndex;
    }

    public int getDestinationCurrentReplicaIndex() {
        return this.destinationCurrentReplicaIndex;
    }

    public int getDestinationNewReplicaIndex() {
        return this.destinationNewReplicaIndex;
    }

    public Address getMaster() {
        return this.master;
    }

    public MigrationInfo setMaster(Address master) {
        this.master = master;
        return this;
    }

    public boolean startProcessing() {
        return this.processing.compareAndSet(false, true);
    }

    public void doneProcessing() {
        this.processing.set(false);
    }

    public MigrationStatus getStatus() {
        return this.status;
    }

    public MigrationInfo setStatus(MigrationStatus status) {
        this.status = status;
        return this;
    }

    public boolean isValid() {
        return this.status != MigrationStatus.INVALID;
    }

    public int getInitialPartitionVersion() {
        return this.initialPartitionVersion;
    }

    public MigrationInfo setInitialPartitionVersion(int initialPartitionVersion) {
        assert (initialPartitionVersion > 0);
        this.initialPartitionVersion = initialPartitionVersion;
        return this;
    }

    public int getPartitionVersionIncrement() {
        if (this.partitionVersionIncrement > 0) {
            return this.partitionVersionIncrement;
        }
        int inc = 1;
        if (this.sourceNewReplicaIndex > -1) {
            ++inc;
        }
        if (this.destinationCurrentReplicaIndex > -1) {
            ++inc;
        }
        return inc;
    }

    public MigrationInfo setPartitionVersionIncrement(int partitionVersionIncrement) {
        assert (partitionVersionIncrement > 0);
        this.partitionVersionIncrement = partitionVersionIncrement;
        return this;
    }

    public int getFinalPartitionVersion() {
        if (this.initialPartitionVersion > 0) {
            return this.initialPartitionVersion + this.getPartitionVersionIncrement();
        }
        throw new IllegalStateException("Initial partition version is not set!");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.uuid);
        out.writeInt(this.partitionId);
        out.writeByte(this.sourceCurrentReplicaIndex);
        out.writeByte(this.sourceNewReplicaIndex);
        out.writeByte(this.destinationCurrentReplicaIndex);
        out.writeByte(this.destinationNewReplicaIndex);
        MigrationStatus.writeTo(this.status, out);
        Version version = out.getVersion();
        boolean hasSource = this.source != null;
        out.writeBoolean(hasSource);
        if (hasSource) {
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(this.source);
            } else {
                MigrationInfo.writePartitionReplicaLegacy(out, this.source);
            }
        }
        boolean hasDestination = this.destination != null;
        out.writeBoolean(hasDestination);
        if (hasDestination) {
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(this.destination);
            } else {
                MigrationInfo.writePartitionReplicaLegacy(out, this.destination);
            }
        }
        this.master.writeData(out);
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            out.writeInt(this.initialPartitionVersion);
            out.writeInt(this.partitionVersionIncrement);
        }
    }

    private static void writePartitionReplicaLegacy(ObjectDataOutput out, PartitionReplica destination) throws IOException {
        destination.address().writeData(out);
        out.writeUTF(destination.uuid());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        boolean hasDestination;
        this.uuid = in.readUTF();
        this.partitionId = in.readInt();
        this.sourceCurrentReplicaIndex = in.readByte();
        this.sourceNewReplicaIndex = in.readByte();
        this.destinationCurrentReplicaIndex = in.readByte();
        this.destinationNewReplicaIndex = in.readByte();
        this.status = MigrationStatus.readFrom(in);
        Version version = in.getVersion();
        boolean hasSource = in.readBoolean();
        if (hasSource) {
            this.source = version.isGreaterOrEqual(Versions.V3_12) ? (PartitionReplica)in.readObject() : MigrationInfo.readPartitionReplicaLegacy(in);
        }
        if (hasDestination = in.readBoolean()) {
            this.destination = version.isGreaterOrEqual(Versions.V3_12) ? (PartitionReplica)in.readObject() : MigrationInfo.readPartitionReplicaLegacy(in);
        }
        this.master = new Address();
        this.master.readData(in);
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            this.initialPartitionVersion = in.readInt();
            this.partitionVersionIncrement = in.readInt();
        }
    }

    private static PartitionReplica readPartitionReplicaLegacy(ObjectDataInput in) throws IOException {
        Address address = new Address();
        address.readData(in);
        String uuid = in.readUTF();
        return new PartitionReplica(address, uuid);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MigrationInfo that = (MigrationInfo)o;
        return this.uuid.equals(that.uuid);
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MigrationInfo{");
        sb.append("uuid=").append(this.uuid);
        sb.append(", partitionId=").append(this.partitionId);
        sb.append(", source=").append(this.source);
        sb.append(", sourceCurrentReplicaIndex=").append(this.sourceCurrentReplicaIndex);
        sb.append(", sourceNewReplicaIndex=").append(this.sourceNewReplicaIndex);
        sb.append(", destination=").append(this.destination);
        sb.append(", destinationCurrentReplicaIndex=").append(this.destinationCurrentReplicaIndex);
        sb.append(", destinationNewReplicaIndex=").append(this.destinationNewReplicaIndex);
        sb.append(", master=").append(this.master);
        sb.append(", initialPartitionVersion=").append(this.initialPartitionVersion);
        sb.append(", partitionVersionIncrement=").append(this.getPartitionVersionIncrement());
        sb.append(", processing=").append(this.processing);
        sb.append(", status=").append((Object)this.status);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 31;
    }

    public static enum MigrationStatus {
        ACTIVE(0),
        INVALID(1),
        SUCCESS(2),
        FAILED(3);

        private final int code;

        private MigrationStatus(int code) {
            this.code = code;
        }

        public static void writeTo(MigrationStatus type, DataOutput out) throws IOException {
            out.writeByte(type.code);
        }

        public static MigrationStatus readFrom(DataInput in) throws IOException {
            byte code = in.readByte();
            switch (code) {
                case 0: {
                    return ACTIVE;
                }
                case 1: {
                    return INVALID;
                }
                case 2: {
                    return SUCCESS;
                }
                case 3: {
                    return FAILED;
                }
            }
            throw new IllegalArgumentException("Code: " + code);
        }
    }
}

