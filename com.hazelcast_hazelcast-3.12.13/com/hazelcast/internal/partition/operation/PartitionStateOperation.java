/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.version.Version;
import java.io.IOException;

public final class PartitionStateOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation,
Versioned {
    private PartitionRuntimeState partitionState;
    private boolean sync;
    private boolean success;

    public PartitionStateOperation() {
    }

    public PartitionStateOperation(PartitionRuntimeState partitionState, boolean sync) {
        this.partitionState = partitionState;
        this.sync = sync;
    }

    @Override
    public void run() {
        Address callerAddress = this.getCallerAddress();
        this.partitionState.setMaster(callerAddress);
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        this.success = partitionService.processPartitionRuntimeState(this.partitionState);
        ILogger logger = this.getLogger();
        if (logger.isFineEnabled()) {
            String message = (this.success ? "Applied" : "Rejected") + " new partition state. Version: " + this.partitionState.getVersion() + ", caller: " + callerAddress;
            logger.fine(message);
        }
    }

    @Override
    public boolean returnsResponse() {
        return this.sync;
    }

    @Override
    public Object getResponse() {
        return this.success;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        Version version = in.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            this.partitionState = (PartitionRuntimeState)in.readObject();
        } else {
            this.partitionState = new PartitionRuntimeState();
            this.partitionState.readData(in);
        }
        this.sync = in.readBoolean();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        Version version = out.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.partitionState);
        } else {
            this.partitionState.writeData(out);
        }
        out.writeBoolean(this.sync);
    }

    @Override
    public int getId() {
        return 9;
    }
}

