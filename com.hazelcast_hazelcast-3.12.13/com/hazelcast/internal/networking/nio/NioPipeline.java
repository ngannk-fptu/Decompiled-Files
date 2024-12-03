/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.Channel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.networking.ChannelHandler;
import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.NioChannel;
import com.hazelcast.internal.networking.nio.NioPipelineTask;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.iobalancer.IOBalancer;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

public abstract class NioPipeline
implements MigratablePipeline,
Runnable {
    protected static final int LOAD_BALANCING_HANDLE = 0;
    protected static final int LOAD_BALANCING_BYTE = 1;
    protected static final int LOAD_BALANCING_FRAME = 2;
    protected final int loadType = Integer.getInteger("hazelcast.io.load", 1);
    @Probe
    protected final SwCounter processCount = SwCounter.newSwCounter();
    protected final ILogger logger;
    protected final NioChannel channel;
    protected final SocketChannel socketChannel;
    protected volatile NioThread owner;
    protected SelectionKey selectionKey;
    private final ChannelErrorHandler errorHandler;
    private final int initialOps;
    private final IOBalancer ioBalancer;
    private final AtomicReference<TaskNode> delayedTaskStack = new AtomicReference();
    @Probe
    private volatile int ownerId;
    @Probe
    private final SwCounter startedMigrations = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedMigrations = SwCounter.newSwCounter();

    NioPipeline(NioChannel channel, NioThread owner, ChannelErrorHandler errorHandler, int initialOps, ILogger logger, IOBalancer ioBalancer) {
        this.channel = channel;
        this.socketChannel = channel.socketChannel();
        this.owner = owner;
        this.ownerId = owner.id;
        this.logger = logger;
        this.initialOps = initialOps;
        this.ioBalancer = ioBalancer;
        this.errorHandler = errorHandler;
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Probe(level=ProbeLevel.DEBUG)
    private long opsInterested() {
        SelectionKey selectionKey = this.selectionKey;
        return selectionKey == null ? -1L : (long)selectionKey.interestOps();
    }

    @Probe(level=ProbeLevel.DEBUG)
    private long opsReady() {
        SelectionKey selectionKey = this.selectionKey;
        return selectionKey == null ? -1L : (long)selectionKey.readyOps();
    }

    @Override
    public NioThread owner() {
        return this.owner;
    }

    void start() {
        this.addTaskAndWakeup(new NioPipelineTask(this){

            @Override
            protected void run0() {
                try {
                    NioPipeline.this.getSelectionKey();
                }
                catch (Throwable t) {
                    NioPipeline.this.onError(t);
                }
            }
        });
    }

    private SelectionKey getSelectionKey() throws IOException {
        if (this.selectionKey == null) {
            this.selectionKey = this.socketChannel.register(this.owner.getSelector(), this.initialOps, this);
        }
        return this.selectionKey;
    }

    final void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    final void registerOp(int operation) throws IOException {
        SelectionKey selectionKey = this.getSelectionKey();
        selectionKey.interestOps(selectionKey.interestOps() | operation);
    }

    final void unregisterOp(int operation) throws IOException {
        SelectionKey selectionKey = this.getSelectionKey();
        int interestOps = selectionKey.interestOps();
        if ((interestOps & operation) != 0) {
            selectionKey.interestOps(interestOps & ~operation);
        }
    }

    abstract void publishMetrics();

    abstract void process() throws Exception;

    final void addTaskAndWakeup(Runnable task) {
        NioThread localOwner;
        TaskNode update;
        TaskNode old;
        do {
            if ((localOwner = this.owner) == null) continue;
            localOwner.addTaskAndWakeup(task);
            return;
        } while (!this.delayedTaskStack.compareAndSet(old = this.delayedTaskStack.get(), update = new TaskNode(task, old)));
        localOwner = this.owner;
        if (localOwner != null) {
            this.restoreTasks(localOwner, this.delayedTaskStack.getAndSet(null), true);
        }
    }

    private void restoreTasks(NioThread owner, TaskNode node, boolean wakeup) {
        if (node == null) {
            return;
        }
        this.restoreTasks(owner, node.next, false);
        if (wakeup) {
            owner.addTaskAndWakeup(node.task);
        } else {
            owner.addTask(node.task);
        }
    }

    @Override
    public final void run() {
        if (this.owner == Thread.currentThread()) {
            try {
                this.process();
            }
            catch (Throwable t) {
                this.onError(t);
            }
        } else {
            this.addTaskAndWakeup(this);
        }
    }

    public void onError(Throwable error) {
        if (error instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        if (this.selectionKey != null) {
            this.selectionKey.cancel();
        }
        try {
            for (ChannelHandler channelHandler : this.handlers()) {
                channelHandler.interceptError(error);
            }
        }
        catch (Throwable newError) {
            error = newError;
        }
        this.errorHandler.onError(this.channel, error);
    }

    protected abstract Iterable<? extends ChannelHandler> handlers();

    @Override
    public final void requestMigration(NioThread newOwner) {
        this.owner.addTaskAndWakeup(new StartMigrationTask(newOwner));
    }

    private static class TaskNode {
        private final Runnable task;
        private final TaskNode next;

        TaskNode(Runnable task, TaskNode next) {
            this.task = task;
            this.next = next;
        }
    }

    private class CompleteMigrationTask
    implements Runnable {
        private final NioThread newOwner;

        CompleteMigrationTask(NioThread newOwner) {
            this.newOwner = newOwner;
        }

        @Override
        public void run() {
            try {
                assert (NioPipeline.this.owner == null);
                NioPipeline.this.owner = this.newOwner;
                NioPipeline.this.ownerId = this.newOwner.id;
                NioPipeline.this.restoreTasks(NioPipeline.this.owner, NioPipeline.this.delayedTaskStack.getAndSet(null), false);
                NioPipeline.this.completedMigrations.inc();
                NioPipeline.this.ioBalancer.signalMigrationComplete();
                if (!NioPipeline.this.socketChannel.isOpen()) {
                    return;
                }
                NioPipeline.this.selectionKey = NioPipeline.this.getSelectionKey();
                NioPipeline.this.registerOp(NioPipeline.this.initialOps);
            }
            catch (Throwable t) {
                NioPipeline.this.onError(t);
            }
        }
    }

    private class StartMigrationTask
    implements Runnable {
        private final NioThread newOwner;

        StartMigrationTask(NioThread newOwner) {
            this.newOwner = newOwner;
        }

        @Override
        public void run() {
            if (NioPipeline.this.owner == this.newOwner) {
                return;
            }
            NioPipeline.this.publishMetrics();
            try {
                this.startMigration(this.newOwner);
            }
            catch (Throwable t) {
                NioPipeline.this.onError(t);
            }
        }

        private void startMigration(NioThread newOwner) throws IOException {
            assert (NioPipeline.this.owner == Thread.currentThread()) : "startMigration can only run on the owning NioThread";
            assert (NioPipeline.this.owner != newOwner) : "newOwner can't be the same as the existing owner";
            if (!NioPipeline.this.socketChannel.isOpen()) {
                return;
            }
            NioPipeline.this.startedMigrations.inc();
            NioPipeline.this.unregisterOp(NioPipeline.this.initialOps);
            NioPipeline.this.selectionKey.cancel();
            NioPipeline.this.selectionKey = null;
            NioPipeline.this.owner = null;
            NioPipeline.this.ownerId = -1;
            newOwner.addTaskAndWakeup(new CompleteMigrationTask(newOwner));
        }
    }
}

