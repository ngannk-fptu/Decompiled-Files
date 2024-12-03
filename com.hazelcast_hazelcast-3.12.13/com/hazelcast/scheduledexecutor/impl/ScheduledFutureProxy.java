/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.partition.PartitionLostEvent;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.scheduledexecutor.StaleTaskException;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorServiceProxy;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskResult;
import com.hazelcast.scheduledexecutor.impl.operations.CancelTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.DisposeTaskOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetDelayOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetResultOperation;
import com.hazelcast.scheduledexecutor.impl.operations.GetStatisticsOperation;
import com.hazelcast.scheduledexecutor.impl.operations.IsCanceledOperation;
import com.hazelcast.scheduledexecutor.impl.operations.IsDoneOperation;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressFBWarnings(value={"EQ_COMPARETO_USE_OBJECT_EQUALS"})
public final class ScheduledFutureProxy<V>
implements IScheduledFuture<V>,
HazelcastInstanceAware {
    private transient HazelcastInstance instance;
    private final transient AtomicBoolean partitionLost = new AtomicBoolean(false);
    private final transient AtomicBoolean memberLost = new AtomicBoolean(false);
    private volatile ScheduledTaskHandler handler;

    ScheduledFutureProxy(ScheduledTaskHandler handler, ScheduledExecutorServiceProxy executor) {
        Preconditions.checkNotNull(handler);
        this.handler = handler;
        ((DistributedScheduledExecutorService)executor.getService()).addLossListener(this);
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.instance = hazelcastInstance;
    }

    @Override
    public ScheduledTaskHandler getHandler() {
        return this.handler;
    }

    @Override
    public ScheduledTaskStatistics getStats() {
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        GetStatisticsOperation op = new GetStatisticsOperation(this.handler);
        return (ScheduledTaskStatistics)this.invoke(op).join();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        Preconditions.checkNotNull(unit, "Unit is null");
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        GetDelayOperation op = new GetDelayOperation(this.handler, unit);
        return (Long)this.invoke(op).join();
    }

    @Override
    public int compareTo(Delayed o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning) {
            throw new UnsupportedOperationException("mayInterruptIfRunning flag is not supported.");
        }
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        CancelTaskOperation op = new CancelTaskOperation(this.handler, mayInterruptIfRunning);
        return (Boolean)this.invoke(op).join();
    }

    @Override
    public boolean isCancelled() {
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        IsCanceledOperation op = new IsCanceledOperation(this.handler);
        return (Boolean)this.invoke(op).join();
    }

    @Override
    public boolean isDone() {
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        IsDoneOperation op = new IsDoneOperation(this.handler);
        return (Boolean)this.invoke(op).join();
    }

    private InternalCompletableFuture<V> get0() {
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        GetResultOperation op = new GetResultOperation(this.handler);
        return this.invoke(op);
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        try {
            return this.get0().get();
        }
        catch (ScheduledTaskResult.ExecutionExceptionDecorator ex) {
            return (V)ExceptionUtil.sneakyThrow(ex.getCause());
        }
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Preconditions.checkNotNull(unit, "Unit is null");
        try {
            return this.get0().get(timeout, unit);
        }
        catch (ScheduledTaskResult.ExecutionExceptionDecorator ex) {
            return (V)ExceptionUtil.sneakyThrow(ex.getCause());
        }
    }

    @Override
    public void dispose() {
        this.checkAccessibleHandler();
        this.checkAccessibleOwner();
        DisposeTaskOperation op = new DisposeTaskOperation(this.handler);
        InternalCompletableFuture future = this.invoke(op);
        this.handler = null;
        future.join();
    }

    void notifyMemberLost(MembershipEvent event) {
        ScheduledTaskHandler handler = this.handler;
        if (handler == null) {
            return;
        }
        if (handler.isAssignedToMember() && handler.getAddress().equals(event.getMember().getAddress())) {
            this.memberLost.set(true);
        }
    }

    void notifyPartitionLost(PartitionLostEvent event) {
        ScheduledTaskHandler handler = this.handler;
        if (handler == null) {
            return;
        }
        int durability = this.instance.getConfig().getScheduledExecutorConfig(handler.getSchedulerName()).getDurability();
        if (handler.isAssignedToPartition() && handler.getPartitionId() == event.getPartitionId() && event.getLostBackupCount() >= durability) {
            this.partitionLost.set(true);
        }
    }

    private void checkAccessibleOwner() {
        if (this.handler.isAssignedToPartition()) {
            if (this.partitionLost.get()) {
                throw new IllegalStateException("Partition " + this.handler.getPartitionId() + ", holding this scheduled task was lost along with all backups.");
            }
        } else if (this.memberLost.get()) {
            throw new IllegalStateException("Member with address: " + this.handler.getAddress() + ",  holding this scheduled task is not part of this cluster.");
        }
    }

    private void checkAccessibleHandler() {
        if (this.handler == null) {
            throw new StaleTaskException("Scheduled task was previously disposed.");
        }
    }

    private <T> InternalCompletableFuture<T> invoke(Operation op) {
        if (this.handler.isAssignedToPartition()) {
            op.setPartitionId(this.handler.getPartitionId());
            return this.invokeOnPartition(op);
        }
        return this.invokeOnAddress(op, this.handler.getAddress());
    }

    private <T> InternalCompletableFuture<T> invokeOnPartition(Operation op) {
        InternalOperationService opService = ((HazelcastInstanceImpl)this.instance).node.getNodeEngine().getOperationService();
        return opService.invokeOnPartition(op);
    }

    private <T> InternalCompletableFuture<T> invokeOnAddress(Operation op, Address address) {
        InternalOperationService opService = ((HazelcastInstanceImpl)this.instance).node.getNodeEngine().getOperationService();
        return opService.invokeOnTarget(op.getServiceName(), op, address);
    }
}

