/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl.operations;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import com.hazelcast.spi.NamedOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.PartitionAwareOperation;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractSchedulerOperation
extends Operation
implements NamedOperation,
PartitionAwareOperation,
IdentifiedDataSerializable {
    protected String schedulerName;

    AbstractSchedulerOperation() {
    }

    AbstractSchedulerOperation(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public String getSchedulerName() {
        return this.schedulerName;
    }

    @Override
    public String getName() {
        return this.schedulerName;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:scheduledExecutorService";
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    public ScheduledExecutorContainer getContainer() {
        this.checkNotShutdown();
        DistributedScheduledExecutorService service = (DistributedScheduledExecutorService)this.getService();
        return service.getPartitionOrMemberBin(this.getPartitionId()).getOrCreateContainer(this.schedulerName);
    }

    private void checkNotShutdown() {
        DistributedScheduledExecutorService service = (DistributedScheduledExecutorService)this.getService();
        if (service.isShutdown(this.getSchedulerName())) {
            throw new RejectedExecutionException("Executor is shut down.");
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeUTF(this.schedulerName);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.schedulerName = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", name=").append(this.schedulerName);
        sb.append(", partiotionId=").append(this.getPartitionId());
    }
}

