/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public final class PartitionStateVersionCheckOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    private int version;
    private transient boolean stale;

    public PartitionStateVersionCheckOperation() {
    }

    public PartitionStateVersionCheckOperation(int version) {
        this.version = version;
    }

    @Override
    public void run() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        int currentVersion = partitionService.getPartitionStateVersion();
        if (currentVersion < this.version) {
            this.stale = true;
            ILogger logger = this.getLogger();
            if (logger.isFineEnabled()) {
                logger.fine("Partition table is stale! Current version: " + currentVersion + ", master version: " + this.version);
            }
        }
    }

    @Override
    public Object getResponse() {
        return !this.stale;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.version = in.readInt();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.version);
    }

    @Override
    public int getId() {
        return 23;
    }
}

