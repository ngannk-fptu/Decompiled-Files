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
import com.hazelcast.internal.partition.impl.InternalMigrationListener;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.internal.partition.operation.BeforePromotionOperation;
import com.hazelcast.internal.partition.operation.FinalizePromotionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.TargetNotMemberException;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.Preconditions;
import com.hazelcast.version.Version;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class PromotionCommitOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation,
Versioned {
    private PartitionRuntimeState partitionState;
    private Collection<MigrationInfo> promotions;
    private String expectedMemberUuid;
    private transient boolean success;
    private transient boolean beforeStateCompleted;

    public PromotionCommitOperation() {
    }

    public PromotionCommitOperation(PartitionRuntimeState partitionState, Collection<MigrationInfo> promotions, String expectedMemberUuid) {
        Preconditions.checkNotNull(promotions);
        this.partitionState = partitionState;
        this.promotions = promotions;
        this.expectedMemberUuid = expectedMemberUuid;
    }

    @Override
    public void beforeRun() throws Exception {
        if (this.beforeStateCompleted) {
            return;
        }
        NodeEngine nodeEngine = this.getNodeEngine();
        Member localMember = nodeEngine.getLocalMember();
        if (!localMember.getUuid().equals(this.expectedMemberUuid)) {
            throw new IllegalStateException("This " + localMember + " is promotion commit destination but most probably it's restarted and not the expected target.");
        }
        Address masterAddress = nodeEngine.getMasterAddress();
        Address caller = this.getCallerAddress();
        if (!caller.equals(masterAddress)) {
            throw new IllegalStateException("Caller is not master node! Caller: " + caller + ", Master: " + masterAddress);
        }
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        if (!partitionService.isMemberMaster(caller)) {
            throw new RetryableHazelcastException("Caller is not master node known by migration system! Caller: " + caller);
        }
    }

    @Override
    public CallStatus call() throws Exception {
        if (!this.beforeStateCompleted) {
            return this.beforePromotion();
        }
        this.finalizePromotion();
        return CallStatus.DONE_RESPONSE;
    }

    private CallStatus beforePromotion() {
        NodeEngineImpl nodeEngine = (NodeEngineImpl)this.getNodeEngine();
        InternalOperationService operationService = nodeEngine.getOperationService();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        if (!partitionService.getMigrationManager().acquirePromotionPermit()) {
            throw new RetryableHazelcastException("Another promotion is being run currently. This is only expected when promotion is retried to an unresponsive destination.");
        }
        ILogger logger = this.getLogger();
        int partitionStateVersion = partitionService.getPartitionStateVersion();
        if (this.partitionState.getVersion() <= partitionStateVersion) {
            logger.warning("Already applied promotions to the partition state. Promotion state version: " + this.partitionState.getVersion() + ", current version: " + partitionStateVersion);
            partitionService.getMigrationManager().releasePromotionPermit();
            this.success = true;
            return CallStatus.DONE_RESPONSE;
        }
        partitionService.getInternalMigrationListener().onPromotionStart(InternalMigrationListener.MigrationParticipant.DESTINATION, this.promotions);
        if (logger.isFineEnabled()) {
            logger.fine("Submitting BeforePromotionOperations for " + this.promotions.size() + " promotions. Promotion partition state version: " + this.partitionState.getVersion() + ", current partition state version: " + partitionStateVersion);
        }
        BeforePromotionOperationCallback beforePromotionsCallback = new BeforePromotionOperationCallback(this, new AtomicInteger(this.promotions.size()));
        for (MigrationInfo promotion : this.promotions) {
            if (logger.isFinestEnabled()) {
                logger.finest("Submitting BeforePromotionOperation for promotion: " + promotion);
            }
            BeforePromotionOperation op = new BeforePromotionOperation(promotion, beforePromotionsCallback);
            op.setPartitionId(promotion.getPartitionId()).setNodeEngine(nodeEngine).setService(partitionService);
            operationService.execute(op);
        }
        return CallStatus.DONE_VOID;
    }

    private void finalizePromotion() {
        NodeEngine nodeEngine = this.getNodeEngine();
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        OperationService operationService = nodeEngine.getOperationService();
        this.partitionState.setMaster(this.getCallerAddress());
        this.success = partitionService.processPartitionRuntimeState(this.partitionState);
        ILogger logger = this.getLogger();
        if (!this.success) {
            logger.severe("Promotion of " + this.promotions.size() + " partitions failed. . Promotion partition state version: " + this.partitionState.getVersion() + ", current partition state version: " + partitionService.getPartitionStateVersion());
        }
        if (logger.isFineEnabled()) {
            logger.fine("Submitting FinalizePromotionOperations for " + this.promotions.size() + " promotions. Result: " + this.success + ". Promotion partition state version: " + this.partitionState.getVersion() + ", current partition state version: " + partitionService.getPartitionStateVersion());
        }
        for (MigrationInfo promotion : this.promotions) {
            if (logger.isFinestEnabled()) {
                logger.finest("Submitting FinalizePromotionOperation for promotion: " + promotion + ". Result: " + this.success);
            }
            FinalizePromotionOperation op = new FinalizePromotionOperation(promotion, this.success);
            op.setPartitionId(promotion.getPartitionId()).setNodeEngine(nodeEngine).setService(partitionService);
            operationService.execute(op);
        }
        partitionService.getInternalMigrationListener().onPromotionComplete(InternalMigrationListener.MigrationParticipant.DESTINATION, this.promotions, this.success);
        partitionService.getMigrationManager().releasePromotionPermit();
    }

    @Override
    public int getId() {
        return 10;
    }

    private void onBeforePromotionsComplete() {
        this.beforeStateCompleted = true;
        this.getNodeEngine().getOperationService().execute(this);
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
        Version version = in.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            this.partitionState = (PartitionRuntimeState)in.readObject();
        } else {
            this.partitionState = new PartitionRuntimeState();
            this.partitionState.readData(in);
        }
        int len = in.readInt();
        if (len > 0) {
            this.promotions = new ArrayList<MigrationInfo>(len);
            for (int i = 0; i < len; ++i) {
                MigrationInfo migrationInfo;
                if (version.isGreaterOrEqual(Versions.V3_12)) {
                    migrationInfo = (MigrationInfo)in.readObject();
                } else {
                    migrationInfo = new MigrationInfo();
                    migrationInfo.readData(in);
                }
                this.promotions.add(migrationInfo);
            }
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.expectedMemberUuid);
        Version version = out.getVersion();
        if (version.isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(this.partitionState);
        } else {
            this.partitionState.writeData(out);
        }
        int len = this.promotions.size();
        out.writeInt(len);
        for (MigrationInfo migrationInfo : this.promotions) {
            if (version.isGreaterOrEqual(Versions.V3_12)) {
                out.writeObject(migrationInfo);
                continue;
            }
            migrationInfo.writeData(out);
        }
    }

    private static class BeforePromotionOperationCallback
    implements Runnable {
        private final PromotionCommitOperation promotionCommitOperation;
        private final AtomicInteger tasks;

        BeforePromotionOperationCallback(PromotionCommitOperation promotionCommitOperation, AtomicInteger tasks) {
            this.promotionCommitOperation = promotionCommitOperation;
            this.tasks = tasks;
        }

        @Override
        public void run() {
            int remainingTasks = this.tasks.decrementAndGet();
            ILogger logger = this.promotionCommitOperation.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("Remaining before promotion tasks: " + remainingTasks);
            }
            if (remainingTasks == 0) {
                logger.fine("All before promotion tasks are completed.");
                this.promotionCommitOperation.onBeforePromotionsComplete();
            }
        }
    }
}

