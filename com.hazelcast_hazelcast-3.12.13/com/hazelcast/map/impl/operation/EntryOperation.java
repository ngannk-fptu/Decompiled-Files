/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.concurrent.lock.LockWaitNotifyKey;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.Offloadable;
import com.hazelcast.core.ReadOnly;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.operation.EntryBackupOperation;
import com.hazelcast.map.impl.operation.EntryOffloadableLockMismatchException;
import com.hazelcast.map.impl.operation.EntryOffloadableSetUnlockOperation;
import com.hazelcast.map.impl.operation.EntryOperator;
import com.hazelcast.map.impl.operation.KeyBasedMapOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.BackupAwareOperation;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.CallStatus;
import com.hazelcast.spi.Offload;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.exception.WrongTargetException;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.UuidUtil;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class EntryOperation
extends KeyBasedMapOperation
implements BackupAwareOperation,
BlockingOperation,
MutatingOperation {
    private static final int SET_UNLOCK_FAST_RETRY_LIMIT = 10;
    private EntryProcessor entryProcessor;
    private transient boolean offload;
    private transient Object response;
    private transient boolean readOnly;
    private transient int setUnlockRetryCount;
    private transient long begin;

    public EntryOperation() {
    }

    public EntryOperation(String name, Data dataKey, EntryProcessor entryProcessor) {
        super(name, dataKey);
        this.entryProcessor = entryProcessor;
    }

    @Override
    public void innerBeforeRun() throws Exception {
        super.innerBeforeRun();
        this.begin = Clock.currentTimeMillis();
        this.readOnly = this.entryProcessor instanceof ReadOnly;
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        ManagedContext managedContext = serializationService.getManagedContext();
        managedContext.initialize(this.entryProcessor);
    }

    @Override
    public CallStatus call() {
        if (this.shouldWait()) {
            return CallStatus.WAIT;
        }
        if (this.offload) {
            return new EntryOperationOffload(this.getCallerAddress());
        }
        this.response = EntryOperator.operator(this, this.entryProcessor).operateOnKey(this.dataKey).doPostOperateOps().getResult();
        return CallStatus.DONE_RESPONSE;
    }

    @Override
    public WaitNotifyKey getWaitKey() {
        return new LockWaitNotifyKey(this.getServiceNamespace(), this.dataKey);
    }

    @Override
    public boolean shouldWait() {
        if (this.entryProcessor instanceof ReadOnly) {
            this.offload = this.isOffloadingRequested(this.entryProcessor);
            return false;
        }
        if (!this.recordStore.isLocked(this.dataKey) && this.isOffloadingRequested(this.entryProcessor)) {
            this.offload = true;
            return false;
        }
        this.offload = false;
        return !this.recordStore.canAcquireLock(this.dataKey, this.getCallerUuid(), this.getThreadId());
    }

    private boolean isOffloadingRequested(EntryProcessor entryProcessor) {
        String executorName;
        return entryProcessor instanceof Offloadable && !(executorName = ((Offloadable)((Object)entryProcessor)).getExecutorName()).equals("no-offloading");
    }

    @Override
    public void onWaitExpire() {
        this.sendResponse(null);
    }

    @Override
    public Object getResponse() {
        return this.response;
    }

    @Override
    public Operation getBackupOperation() {
        EntryBackupProcessor backupProcessor = this.entryProcessor.getBackupProcessor();
        return backupProcessor != null ? new EntryBackupOperation(this.name, this.dataKey, backupProcessor) : null;
    }

    @Override
    public boolean shouldBackup() {
        return this.mapContainer.getTotalBackupCount() > 0 && this.entryProcessor.getBackupProcessor() != null;
    }

    @Override
    public int getAsyncBackupCount() {
        return this.mapContainer.getAsyncBackupCount();
    }

    @Override
    public int getSyncBackupCount() {
        return this.mapContainer.getBackupCount();
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.entryProcessor = (EntryProcessor)in.readObject();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.entryProcessor);
    }

    private final class EntryOperationOffload
    extends Offload {
        private Address callerAddress;

        private EntryOperationOffload(Address callerAddress) {
            super(EntryOperation.this);
            this.callerAddress = callerAddress;
        }

        @Override
        public void start() {
            this.verifyEntryProcessor();
            boolean shouldCloneForOffloading = InMemoryFormat.OBJECT.equals((Object)EntryOperation.this.mapContainer.getMapConfig().getInMemoryFormat());
            Object oldValue = EntryOperation.this.recordStore.get(EntryOperation.this.dataKey, false, this.callerAddress);
            Object clonedOldValue = shouldCloneForOffloading ? this.serializationService.toData(oldValue) : oldValue;
            String executorName = ((Offloadable)((Object)EntryOperation.this.entryProcessor)).getExecutorName();
            String string = executorName = executorName.equals("hz:offloadable") ? "hz:offloadable" : executorName;
            if (EntryOperation.this.readOnly) {
                this.executeReadOnlyEntryProcessor(clonedOldValue, executorName);
            } else {
                this.executeMutatingEntryProcessor(clonedOldValue, executorName);
            }
        }

        private void verifyEntryProcessor() {
            if (!(EntryOperation.this.entryProcessor instanceof Offloadable)) {
                throw new HazelcastException("EntryProcessor is expected to implement Offloadable for this operation");
            }
            if (EntryOperation.this.readOnly && EntryOperation.this.entryProcessor.getBackupProcessor() != null) {
                throw new HazelcastException("EntryProcessor.getBackupProcessor() should return null if ReadOnly implemented");
            }
        }

        private void executeReadOnlyEntryProcessor(final Object oldValue, String executorName) {
            this.executionService.execute(executorName, new Runnable(){

                @Override
                public void run() {
                    try {
                        Data result = EntryOperator.operator(EntryOperation.this, EntryOperation.this.entryProcessor).operateOnKeyValue(EntryOperation.this.dataKey, oldValue).getResult();
                        EntryOperation.this.sendResponse(result);
                    }
                    catch (Throwable t) {
                        EntryOperation.this.sendResponse(t);
                    }
                }
            });
        }

        private void executeMutatingEntryProcessor(final Object oldValue, String executorName) {
            final String finalCaller = UuidUtil.newUnsecureUuidString();
            Data finalDataKey = EntryOperation.this.dataKey;
            final long finalThreadId = EntryOperation.this.threadId;
            long finalCallId = EntryOperation.this.getCallId();
            final long finalBegin = EntryOperation.this.begin;
            this.lock(finalDataKey, finalCaller, finalThreadId, finalCallId);
            try {
                this.executionService.execute(executorName, new Runnable(){

                    @Override
                    public void run() {
                        try {
                            EntryOperator entryOperator = EntryOperator.operator(EntryOperation.this, EntryOperation.this.entryProcessor).operateOnKeyValue(EntryOperation.this.dataKey, oldValue);
                            Data result = entryOperator.getResult();
                            EntryEventType modificationType = entryOperator.getEventType();
                            if (modificationType != null) {
                                Object newValue = EntryOperationOffload.this.serializationService.toData(entryOperator.getNewValue());
                                EntryOperationOffload.this.updateAndUnlock(EntryOperationOffload.this.serializationService.toData(oldValue), newValue, modificationType, finalCaller, finalThreadId, result, finalBegin);
                            } else {
                                EntryOperationOffload.this.unlockOnly(result, finalCaller, finalThreadId, finalBegin);
                            }
                        }
                        catch (Throwable t) {
                            EntryOperation.this.getLogger().severe("Unexpected error on Offloadable execution", t);
                            EntryOperationOffload.this.unlockOnly(t, finalCaller, finalThreadId, finalBegin);
                        }
                    }
                });
            }
            catch (Throwable t) {
                this.unlock(finalDataKey, finalCaller, finalThreadId, finalCallId, t);
                ExceptionUtil.sneakyThrow(t);
            }
        }

        private void lock(Data finalDataKey, String finalCaller, long finalThreadId, long finalCallId) {
            boolean locked = EntryOperation.this.recordStore.localLock(finalDataKey, finalCaller, finalThreadId, finalCallId, -1L);
            if (!locked) {
                throw new IllegalStateException(String.format("Could not obtain a lock by the caller=%s and threadId=%d", finalCaller, EntryOperation.this.threadId));
            }
        }

        private void unlock(Data finalDataKey, String finalCaller, long finalThreadId, long finalCallId, Throwable cause) {
            boolean unlocked = EntryOperation.this.recordStore.unlock(finalDataKey, finalCaller, finalThreadId, finalCallId);
            if (!unlocked) {
                throw new IllegalStateException(String.format("Could not unlock by the caller=%s and threadId=%d", finalCaller, EntryOperation.this.threadId), cause);
            }
        }

        private void unlockOnly(Object result, String caller, long threadId, long now) {
            this.updateAndUnlock(null, null, null, caller, threadId, result, now);
        }

        private void updateAndUnlock(Data previousValue, Data newValue, EntryEventType modificationType, String caller, long threadId, final Object result, long now) {
            EntryOffloadableSetUnlockOperation updateOperation = new EntryOffloadableSetUnlockOperation(EntryOperation.this.name, modificationType, EntryOperation.this.dataKey, previousValue, newValue, caller, threadId, now, EntryOperation.this.entryProcessor.getBackupProcessor());
            updateOperation.setPartitionId(EntryOperation.this.getPartitionId());
            updateOperation.setReplicaIndex(0);
            updateOperation.setNodeEngine(this.nodeEngine);
            updateOperation.setCallerUuid(EntryOperation.this.getCallerUuid());
            OperationAccessor.setCallerAddress(updateOperation, EntryOperation.this.getCallerAddress());
            OperationResponseHandler setUnlockResponseHandler = new OperationResponseHandler(){

                public void sendResponse(Operation op, Object response) {
                    if (EntryOperationOffload.this.isRetryable(response) || EntryOperationOffload.this.isTimeout(response)) {
                        this.retry(op);
                    } else {
                        EntryOperation.this.sendResponse(this.toResponse(response));
                    }
                }

                private void retry(final Operation op) {
                    EntryOperation.this.setUnlockRetryCount++;
                    if (EntryOperationOffload.this.isFastRetryLimitReached()) {
                        EntryOperationOffload.this.executionService.schedule(new Runnable(){

                            @Override
                            public void run() {
                                EntryOperationOffload.this.operationService.execute(op);
                            }
                        }, 500L, TimeUnit.MILLISECONDS);
                    } else {
                        EntryOperationOffload.this.operationService.execute(op);
                    }
                }

                private Object toResponse(Object response) {
                    if (response instanceof Throwable) {
                        Throwable t = (Throwable)response;
                        if (t instanceof EntryOffloadableLockMismatchException) {
                            t = new RetryableHazelcastException(t.getMessage(), t);
                        }
                        return t;
                    }
                    return result;
                }
            };
            updateOperation.setOperationResponseHandler(setUnlockResponseHandler);
            this.operationService.execute(updateOperation);
        }

        private boolean isRetryable(Object response) {
            return response instanceof RetryableHazelcastException && !(response instanceof WrongTargetException);
        }

        private boolean isTimeout(Object response) {
            return response instanceof CallTimeoutResponse;
        }

        private boolean isFastRetryLimitReached() {
            return EntryOperation.this.setUnlockRetryCount > 10;
        }
    }
}

