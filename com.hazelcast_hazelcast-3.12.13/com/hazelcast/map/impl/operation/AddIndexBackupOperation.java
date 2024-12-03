/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.impl.operationservice.TargetAware;
import com.hazelcast.version.MemberVersion;
import java.io.IOException;

public class AddIndexBackupOperation
extends MapOperation
implements BackupOperation,
TargetAware {
    private static final MemberVersion V3_12_1 = MemberVersion.of(3, 12, 1);
    private String attributeName;
    private boolean ordered;
    private transient boolean targetSupported;

    public AddIndexBackupOperation() {
    }

    public AddIndexBackupOperation(String name, String attributeName, boolean ordered) {
        super(name);
        this.attributeName = attributeName;
        this.ordered = ordered;
    }

    @Override
    public void setTarget(Address address) {
        MemberVersion memberVersion;
        MemberImpl target = this.getNodeEngine().getClusterService().getMember(address);
        this.targetSupported = target == null ? false : (memberVersion = target.getVersion()).compareTo(V3_12_1) >= 0;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public void run() throws Exception {
        int partitionId = this.getPartitionId();
        Indexes indexes = this.mapContainer.getIndexes(partitionId);
        indexes.recordIndexDefinition(this.attributeName, this.ordered);
    }

    @Override
    public Object getResponse() {
        return Boolean.TRUE;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        if (this.targetSupported) {
            super.writeInternal(out);
            out.writeUTF(this.attributeName);
            out.writeBoolean(this.ordered);
        } else {
            super.writeInternal(out);
            out.writeUTF(this.name);
            out.writeInt(0);
            out.writeInt(Integer.MAX_VALUE);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.attributeName = in.readUTF();
        this.ordered = in.readBoolean();
    }

    @Override
    public int getId() {
        return this.targetSupported ? 151 : 140;
    }
}

