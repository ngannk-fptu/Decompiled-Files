/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.core.MemberLeftException;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.PartitionRuntimeState;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.UrgentSystemOperation;
import com.hazelcast.spi.exception.CallerNotMemberException;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.operationexecutor.OperationExecutor;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import java.util.concurrent.atomic.AtomicInteger;

public final class FetchPartitionStateOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    @Override
    public void beforeRun() {
        Address caller = this.getCallerAddress();
        Address masterAddress = this.getNodeEngine().getMasterAddress();
        ILogger logger = this.getLogger();
        if (!caller.equals(masterAddress)) {
            String msg = caller + " requested our partition table but it's not our known master. Master: " + masterAddress;
            logger.warning(msg);
            throw new IllegalStateException(msg);
        }
        InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)this.getService();
        if (!service.isMemberMaster(caller)) {
            String msg = caller + " requested our partition table but it's not the master known by migration system.";
            logger.warning(msg);
            throw new RetryableHazelcastException(msg);
        }
    }

    @Override
    public CallStatus call() {
        return new OffloadImpl();
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        if (throwable instanceof MemberLeftException || throwable instanceof TargetNotMemberException || throwable instanceof CallerNotMemberException) {
            return ExceptionAction.THROW_EXCEPTION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public int getId() {
        return 4;
    }

    private final class SendPartitionStateTask
    implements Runnable,
    UrgentSystemOperation {
        private final AtomicInteger remaining = new AtomicInteger();

        private SendPartitionStateTask(int partitionThreadCount) {
            this.remaining.set(partitionThreadCount);
        }

        @Override
        public void run() {
            if (this.remaining.decrementAndGet() == 0) {
                InternalPartitionServiceImpl service = (InternalPartitionServiceImpl)FetchPartitionStateOperation.this.getService();
                PartitionRuntimeState partitionState = service.createPartitionStateInternal();
                FetchPartitionStateOperation.this.sendResponse(partitionState);
            }
        }
    }

    private final class OffloadImpl
    extends Offload {
        private OffloadImpl() {
            super(FetchPartitionStateOperation.this);
        }

        @Override
        public void start() {
            NodeEngine nodeEngine = FetchPartitionStateOperation.this.getNodeEngine();
            OperationServiceImpl operationService = (OperationServiceImpl)nodeEngine.getOperationService();
            OperationExecutor executor = operationService.getOperationExecutor();
            int partitionThreadCount = executor.getPartitionThreadCount();
            SendPartitionStateTask barrierTask = new SendPartitionStateTask(partitionThreadCount);
            executor.executeOnPartitionThreads(barrierTask);
        }
    }
}

