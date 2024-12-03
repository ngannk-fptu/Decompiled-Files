/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.exception.TargetNotMemberException;

public class AssignPartitions
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    private PartitionRuntimeState partitionState;

    @Override
    public void run() {
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        this.partitionState = service.firstArrangement();
    }

    @Override
    public Object getResponse() {
        return this.partitionState;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }
}

