/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.Member;
import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.MigrationInfo;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.exception.TargetNotMemberException;
import java.io.IOException;

public class MigrationCommitOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation,
Versioned {
    private PartitionRuntimeState partitionState;
    private MigrationInfo migration;
    private String expectedMemberUuid;
    private transient boolean success;

    public MigrationCommitOperation() {
    }

    public MigrationCommitOperation(PartitionRuntimeState partitionState, String expectedMemberUuid) {
        this.partitionState = partitionState;
        this.expectedMemberUuid = expectedMemberUuid;
    }

    public MigrationCommitOperation(MigrationInfo migration, String expectedMemberUuid) {
        this.migration = migration;
        this.expectedMemberUuid = expectedMemberUuid;
    }

    @Override
    public void run() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Member localMember = nodeEngine.getLocalMember();
        if (!localMember.getUuid().equals(this.expectedMemberUuid)) {
            throw new IllegalStateException("This " + localMember + " is migration commit destination but most probably it's restarted and not the expected target.");
        }
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        if (nodeEngine.getClusterService().getClusterVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.success = service.commitMigrationOnDestination(this.migration, this.getCallerAddress());
        } else {
            this.partitionState.setMaster(this.getCallerAddress());
            this.success = service.processPartitionRuntimeState(this.partitionState);
        }
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
        this.expectedMemberUuid = in.readUTF();
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            this.migration = (MigrationInfo)in.readObject();
        } else {
            this.partitionState = new PartitionRuntimeState();
            this.partitionState.readData(in);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.expectedMemberUuid);
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.migration);
        } else {
            this.partitionState.writeData(out);
        }
    }

    @Override
    public int getId() {
        return 6;
    }
}

