/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util.thread.strategy;

import java.io.Closeable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.VirtualThreads;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.TryExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject(value="Adaptive execution strategy")
public class AdaptiveExecutionStrategy
extends ContainerLifeCycle
implements ExecutionStrategy,
Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AdaptiveExecutionStrategy.class);
    private final AutoLock _lock = new AutoLock();
    private final LongAdder _pcMode = new LongAdder();
    private final LongAdder _picMode = new LongAdder();
    private final LongAdder _pecMode = new LongAdder();
    private final LongAdder _epcMode = new LongAdder();
    private final ExecutionStrategy.Producer _producer;
    private final Executor _executor;
    private final TryExecutor _tryExecutor;
    private final Executor _virtualExecutor;
    private State _state = State.IDLE;
    private boolean _pending;

    public AdaptiveExecutionStrategy(ExecutionStrategy.Producer producer, Executor executor) {
        this._producer = producer;
        this._executor = executor;
        this._tryExecutor = TryExecutor.asTryExecutor(executor);
        this._virtualExecutor = VirtualThreads.getVirtualThreadsExecutor(this._executor);
        this.addBean(this._producer);
        this.addBean(this._tryExecutor);
        this.addBean(this._virtualExecutor);
        if (LOG.isDebugEnabled()) {
            LOG.debug("{} created", (Object)this);
        }
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void dispatch() {
        execute = false;
        l = this._lock.lock();
        try {
            switch (1.$SwitchMap$org$eclipse$jetty$util$thread$strategy$AdaptiveExecutionStrategy$State[this._state.ordinal()]) {
                case 1: {
                    if (this._pending) ** break;
                    this._pending = true;
                    execute = true;
                    ** break;
lbl10:
                    // 1 sources

                    break;
                }
                case 2: {
                    this._state = State.REPRODUCING;
                    ** break;
lbl14:
                    // 1 sources

                    break;
                }
                ** default:
lbl16:
                // 1 sources

                break;
            }
        }
        finally {
            if (l != null) {
                l.close();
            }
        }
        if (AdaptiveExecutionStrategy.LOG.isDebugEnabled()) {
            AdaptiveExecutionStrategy.LOG.debug("{} dispatch {}", (Object)this, (Object)execute);
        }
        if (execute) {
            this._executor.execute(this);
        }
    }

    @Override
    public void produce() {
        this.tryProduce(false);
    }

    @Override
    public void run() {
        this.tryProduce(true);
    }

    /*
     * Unable to fully structure code
     */
    private void tryProduce(boolean wasPending) {
        if (AdaptiveExecutionStrategy.LOG.isDebugEnabled()) {
            AdaptiveExecutionStrategy.LOG.debug("{} tryProduce {}", (Object)this, (Object)wasPending);
        }
        l = this._lock.lock();
        try {
            if (wasPending) {
                this._pending = false;
            }
            switch (1.$SwitchMap$org$eclipse$jetty$util$thread$strategy$AdaptiveExecutionStrategy$State[this._state.ordinal()]) {
                case 1: {
                    this._state = State.PRODUCING;
                    ** break;
lbl11:
                    // 1 sources

                    break;
                }
                case 2: {
                    this._state = State.REPRODUCING;
                    return;
                }
                case 3: {
                    return;
                }
                default: {
                    throw new IllegalStateException(this.toStringLocked());
                }
            }
        }
        finally {
            if (l != null) {
                l.close();
            }
        }
        nonBlocking = Invocable.isNonBlockingInvocation();
        block24: while (this.isRunning()) {
            try {
                task = this.produceTask();
                if (task == null) {
                    l = this._lock.lock();
                    try {
                        switch (1.$SwitchMap$org$eclipse$jetty$util$thread$strategy$AdaptiveExecutionStrategy$State[this._state.ordinal()]) {
                            case 2: {
                                this._state = State.IDLE;
                                return;
                            }
                            case 3: {
                                this._state = State.PRODUCING;
                                continue block24;
                            }
                        }
                        throw new IllegalStateException(this.toStringLocked());
                    }
                    finally {
                        if (l != null) {
                            l.close();
                        }
                        continue;
                    }
                }
                if (this.consumeTask(task, this.selectSubStrategy(task, nonBlocking))) continue;
                return;
            }
            catch (Throwable th) {
                AdaptiveExecutionStrategy.LOG.warn("Unable to produce", th);
            }
        }
    }

    private SubStrategy selectSubStrategy(Runnable task, boolean nonBlocking) {
        Invocable.InvocationType taskType = Invocable.getInvocationType(task);
        switch (taskType) {
            case NON_BLOCKING: {
                return SubStrategy.PRODUCE_CONSUME;
            }
            case EITHER: {
                if (nonBlocking) {
                    return SubStrategy.PRODUCE_CONSUME;
                }
                try (AutoLock l = this._lock.lock();){
                    if (this._pending || this._tryExecutor.tryExecute(this)) {
                        this._pending = true;
                        this._state = State.IDLE;
                        SubStrategy subStrategy = SubStrategy.EXECUTE_PRODUCE_CONSUME;
                        return subStrategy;
                    }
                }
                return SubStrategy.PRODUCE_INVOKE_CONSUME;
            }
            case BLOCKING: {
                if (!nonBlocking) {
                    try (AutoLock l = this._lock.lock();){
                        if (this._pending || this._tryExecutor.tryExecute(this)) {
                            this._pending = true;
                            this._state = State.IDLE;
                            SubStrategy subStrategy = SubStrategy.EXECUTE_PRODUCE_CONSUME;
                            return subStrategy;
                        }
                    }
                }
                return SubStrategy.PRODUCE_EXECUTE_CONSUME;
            }
        }
        throw new IllegalStateException(String.format("taskType=%s %s", new Object[]{taskType, this}));
    }

    private boolean consumeTask(Runnable task, SubStrategy subStrategy) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("ss={} t={}/{} {}", new Object[]{subStrategy, task, Invocable.getInvocationType(task), this});
        }
        switch (subStrategy) {
            case PRODUCE_CONSUME: {
                this._pcMode.increment();
                this.runTask(task);
                return true;
            }
            case PRODUCE_INVOKE_CONSUME: {
                this._picMode.increment();
                this.invokeAsNonBlocking(task);
                return true;
            }
            case PRODUCE_EXECUTE_CONSUME: {
                this._pecMode.increment();
                this.execute(task);
                return true;
            }
            case EXECUTE_PRODUCE_CONSUME: {
                this._epcMode.increment();
                this.runTask(task);
                try (AutoLock l = this._lock.lock();){
                    if (this._state == State.IDLE) {
                        this._state = State.PRODUCING;
                        boolean bl = true;
                        return bl;
                    }
                }
                return false;
            }
        }
        throw new IllegalStateException(String.format("ss=%s %s", new Object[]{subStrategy, this}));
    }

    private void runTask(Runnable task) {
        try {
            task.run();
        }
        catch (Throwable x) {
            LOG.warn("Task run failed", x);
        }
    }

    private void invokeAsNonBlocking(Runnable task) {
        try {
            Invocable.invokeNonBlocking(task);
        }
        catch (Throwable x) {
            LOG.warn("Task invoke failed", x);
        }
    }

    private Runnable produceTask() {
        try {
            return this._producer.produce();
        }
        catch (Throwable e) {
            LOG.warn("Task produce failed", e);
            return null;
        }
    }

    private void execute(Runnable task) {
        block5: {
            try {
                Executor executor = this._virtualExecutor;
                if (executor == null) {
                    executor = this._executor;
                }
                executor.execute(task);
            }
            catch (RejectedExecutionException e) {
                if (this.isRunning()) {
                    LOG.warn("Execute failed", (Throwable)e);
                } else {
                    LOG.trace("IGNORED", (Throwable)e);
                }
                if (!(task instanceof Closeable)) break block5;
                IO.close((Closeable)((Object)task));
            }
        }
    }

    @ManagedAttribute(value="whether this execution strategy uses virtual threads", readonly=true)
    public boolean isUseVirtualThreads() {
        return this._virtualExecutor != null;
    }

    @ManagedAttribute(value="number of tasks consumed with PC mode", readonly=true)
    public long getPCTasksConsumed() {
        return this._pcMode.longValue();
    }

    @ManagedAttribute(value="number of tasks executed with PIC mode", readonly=true)
    public long getPICTasksExecuted() {
        return this._picMode.longValue();
    }

    @ManagedAttribute(value="number of tasks executed with PEC mode", readonly=true)
    public long getPECTasksExecuted() {
        return this._pecMode.longValue();
    }

    @ManagedAttribute(value="number of tasks consumed with EPC mode", readonly=true)
    public long getEPCTasksConsumed() {
        return this._epcMode.longValue();
    }

    @ManagedAttribute(value="whether this execution strategy is idle", readonly=true)
    public boolean isIdle() {
        try (AutoLock l = this._lock.lock();){
            boolean bl = this._state == State.IDLE;
            return bl;
        }
    }

    @ManagedOperation(value="resets the task counts", impact="ACTION")
    public void reset() {
        this._pcMode.reset();
        this._epcMode.reset();
        this._pecMode.reset();
        this._picMode.reset();
    }

    @Override
    public String toString() {
        try (AutoLock l = this._lock.lock();){
            String string = this.toStringLocked();
            return string;
        }
    }

    public String toStringLocked() {
        StringBuilder builder = new StringBuilder();
        this.getString(builder);
        this.getState(builder);
        return builder.toString();
    }

    private void getString(StringBuilder builder) {
        builder.append(this.getClass().getSimpleName());
        builder.append('@');
        builder.append(Integer.toHexString(this.hashCode()));
        builder.append('/');
        builder.append(this._producer);
        builder.append('/');
    }

    private void getState(StringBuilder builder) {
        builder.append((Object)this._state);
        builder.append("/p=");
        builder.append(this._pending);
        builder.append('/');
        builder.append(this._tryExecutor);
        builder.append("[pc=");
        builder.append(this.getPCTasksConsumed());
        builder.append(",pic=");
        builder.append(this.getPICTasksExecuted());
        builder.append(",pec=");
        builder.append(this.getPECTasksExecuted());
        builder.append(",epc=");
        builder.append(this.getEPCTasksConsumed());
        builder.append("]");
        builder.append("@");
        builder.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
    }

    private static enum State {
        IDLE,
        PRODUCING,
        REPRODUCING;

    }

    private static enum SubStrategy {
        PRODUCE_CONSUME,
        PRODUCE_INVOKE_CONSUME,
        PRODUCE_EXECUTE_CONSUME,
        EXECUTE_PRODUCE_CONSUME;

    }
}

