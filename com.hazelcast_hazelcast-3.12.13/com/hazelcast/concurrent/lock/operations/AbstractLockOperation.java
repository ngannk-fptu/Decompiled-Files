/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock.operations;

import com.hazelcast.concurrent.lock.LockDataSerializerHook;
import com.hazelcast.concurrent.lock.LockServiceImpl;
import com.hazelcast.concurrent.lock.LockStoreImpl;
import com.hazelcast.concurrent.lock.ObjectNamespaceSerializationHelper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.LockInterceptorService;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespace;
import com.hazelcast.spi.ServiceNamespaceAware;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public abstract class AbstractLockOperation
extends Operation
implements PartitionAwareOperation,
IdentifiedDataSerializable,
NamedOperation,
ServiceNamespaceAware,
Versioned {
    public static final int ANY_THREAD = 0;
    private static final AtomicLongFieldUpdater<AbstractLockOperation> REFERENCE_CALL_ID = AtomicLongFieldUpdater.newUpdater(AbstractLockOperation.class, "referenceCallId");
    protected ObjectNamespace namespace;
    protected Data key;
    protected long threadId;
    protected long leaseTime = -1L;
    protected transient Object response;
    private volatile long referenceCallId;
    private transient boolean asyncBackup;

    public AbstractLockOperation() {
    }

    protected AbstractLockOperation(ObjectNamespace namespace, Data key, long threadId) {
        this.namespace = namespace;
        this.key = key;
        this.threadId = threadId;
    }

    protected AbstractLockOperation(ObjectNamespace namespace, Data key, long threadId, long timeout) {
        this.namespace = namespace;
        this.key = key;
        this.threadId = threadId;
        this.setWaitTimeout(timeout);
    }

    public AbstractLockOperation(ObjectNamespace namespace, Data key, long threadId, long leaseTime, long timeout) {
        this.namespace = namespace;
        this.key = key;
        this.threadId = threadId;
        this.leaseTime = leaseTime;
        this.setWaitTimeout(timeout);
    }

    @Override
    public final Object getResponse() {
        return this.response;
    }

    protected final LockStoreImpl getLockStore() {
        LockServiceImpl service = (LockServiceImpl)this.getService();
        return service.getLockStore(this.getPartitionId(), this.namespace);
    }

    public final int getSyncBackupCount() {
        if (this.asyncBackup) {
            return 0;
        }
        return this.getLockStore().getBackupCount();
    }

    public final int getAsyncBackupCount() {
        LockStoreImpl lockStore = this.getLockStore();
        if (this.asyncBackup) {
            return lockStore.getBackupCount() + lockStore.getAsyncBackupCount();
        }
        return lockStore.getAsyncBackupCount();
    }

    public final void setAsyncBackup(boolean asyncBackup) {
        this.asyncBackup = asyncBackup;
    }

    @Override
    protected void onSetCallId(long callId) {
        REFERENCE_CALL_ID.compareAndSet(this, 0L, callId);
    }

    protected final long getReferenceCallId() {
        return this.referenceCallId != 0L ? this.referenceCallId : this.getCallId();
    }

    protected final void setReferenceCallId(long refCallId) {
        this.referenceCallId = refCallId;
    }

    protected final void interceptLockOperation() {
        Object targetService = this.getNodeEngine().getService(this.namespace.getServiceName());
        if (targetService instanceof LockInterceptorService) {
            ((LockInterceptorService)targetService).onBeforeLock(this.namespace.getObjectName(), this.key);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    public final Data getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.namespace.getObjectName();
    }

    @Override
    public ServiceNamespace getServiceNamespace() {
        return this.namespace;
    }

    @Override
    public final int getFactoryId() {
        return LockDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        ObjectNamespaceSerializationHelper.writeNamespaceCompatibly(this.namespace, out);
        out.writeData(this.key);
        out.writeLong(this.threadId);
        out.writeLong(this.leaseTime);
        out.writeLong(this.referenceCallId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.namespace = ObjectNamespaceSerializationHelper.readNamespaceCompatibly(in);
        this.key = in.readData();
        this.threadId = in.readLong();
        this.leaseTime = in.readLong();
        this.referenceCallId = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", namespace=").append(this.namespace);
        sb.append(", threadId=").append(this.threadId);
    }
}

