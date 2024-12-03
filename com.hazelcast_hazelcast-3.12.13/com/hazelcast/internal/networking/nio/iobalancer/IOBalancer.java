/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.iobalancer.IOBalancerThread;
import com.hazelcast.internal.networking.nio.iobalancer.LoadImbalance;
import com.hazelcast.internal.networking.nio.iobalancer.LoadMigrationStrategy;
import com.hazelcast.internal.networking.nio.iobalancer.LoadTracker;
import com.hazelcast.internal.networking.nio.iobalancer.MigrationStrategy;
import com.hazelcast.internal.networking.nio.iobalancer.MonkeyMigrationStrategy;
import com.hazelcast.internal.util.counters.MwCounter;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggingService;
import com.hazelcast.spi.properties.GroupProperty;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IOBalancer {
    private static final String PROP_MONKEY_BALANCER = "hazelcast.io.balancer.monkey";
    private final ILogger logger;
    private final int balancerIntervalSeconds;
    private final MigrationStrategy strategy;
    private final LoadTracker inLoadTracker;
    private final LoadTracker outLoadTracker;
    private final String hzName;
    private final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
    private volatile boolean enabled;
    private IOBalancerThread ioBalancerThread;
    @Probe
    private final SwCounter imbalanceDetectedCount = SwCounter.newSwCounter();
    @Probe
    private final MwCounter migrationCompletedCount = MwCounter.newMwCounter();

    public IOBalancer(NioThread[] inputThreads, NioThread[] outputThreads, String hzName, int balancerIntervalSeconds, LoggingService loggingService) {
        this.logger = loggingService.getLogger(IOBalancer.class);
        this.balancerIntervalSeconds = balancerIntervalSeconds;
        this.strategy = this.createMigrationStrategy();
        this.hzName = hzName;
        this.inLoadTracker = new LoadTracker(inputThreads, this.logger);
        this.outLoadTracker = new LoadTracker(outputThreads, this.logger);
        this.enabled = this.isEnabled(inputThreads, outputThreads);
    }

    LoadTracker getInLoadTracker() {
        return this.inLoadTracker;
    }

    LoadTracker getOutLoadTracker() {
        return this.outLoadTracker;
    }

    BlockingQueue<Runnable> getWorkQueue() {
        return this.workQueue;
    }

    public void channelAdded(MigratablePipeline inboundPipeline, MigratablePipeline outboundPipeline) {
        if (this.enabled) {
            this.workQueue.add(new AddPipelineTask(inboundPipeline, outboundPipeline));
        }
    }

    public void channelRemoved(MigratablePipeline inboundPipeline, MigratablePipeline outboundPipeline) {
        if (this.enabled) {
            this.workQueue.add(new RemovePipelineTask(inboundPipeline, outboundPipeline));
        }
    }

    public void start() {
        if (this.enabled) {
            this.ioBalancerThread = new IOBalancerThread(this, this.balancerIntervalSeconds, this.hzName, this.logger, this.workQueue);
            this.ioBalancerThread.start();
        }
    }

    public void stop() {
        if (this.ioBalancerThread != null) {
            this.ioBalancerThread.shutdown();
        }
    }

    void rebalance() {
        this.scheduleMigrationIfNeeded(this.inLoadTracker);
        this.scheduleMigrationIfNeeded(this.outLoadTracker);
    }

    private void scheduleMigrationIfNeeded(LoadTracker loadTracker) {
        LoadImbalance loadImbalance = loadTracker.updateImbalance();
        if (this.strategy.imbalanceDetected(loadImbalance)) {
            this.imbalanceDetectedCount.inc();
            this.tryMigrate(loadImbalance);
        } else if (this.logger.isFinestEnabled()) {
            long min = loadImbalance.minimumLoad;
            long max = loadImbalance.maximumLoad;
            if (max == Long.MIN_VALUE) {
                this.logger.finest("There is at most 1 pipeline associated with each thread. There is nothing to balance");
            } else {
                this.logger.finest("No imbalance has been detected. Max. load: " + max + " Min load: " + min + ".");
            }
        }
    }

    private MigrationStrategy createMigrationStrategy() {
        if (Boolean.getBoolean(PROP_MONKEY_BALANCER)) {
            this.logger.warning("Using Monkey IO Balancer Strategy. This is for stress tests only. Do not user in production! Disable by not setting the property 'hazelcast.io.balancer.monkey' to true.");
            return new MonkeyMigrationStrategy();
        }
        this.logger.finest("Using normal IO Balancer Strategy.");
        return new LoadMigrationStrategy();
    }

    private boolean isEnabled(NioThread[] inputThreads, NioThread[] outputThreads) {
        if (this.balancerIntervalSeconds <= 0) {
            this.logger.warning("I/O Balancer is disabled as the '" + GroupProperty.IO_BALANCER_INTERVAL_SECONDS + "' property is set to " + this.balancerIntervalSeconds + ". Set the property to a value larger than 0 to enable the I/O Balancer.");
            return false;
        }
        if (inputThreads.length == 1 && outputThreads.length == 1) {
            this.logger.finest("I/O Balancer is disabled as there is only a single a pair of I/O threads. Use the '" + GroupProperty.IO_THREAD_COUNT + "' property to increase number of I/O Threads.");
            return false;
        }
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("I/O Balancer is enabled. Scanning every " + this.balancerIntervalSeconds + " seconds for imbalances.");
        }
        return true;
    }

    private void tryMigrate(LoadImbalance loadImbalance) {
        MigratablePipeline pipeline = this.strategy.findPipelineToMigrate(loadImbalance);
        if (pipeline == null) {
            this.logger.finest("I/O imbalance is detected, but no suitable migration candidate is found.");
            return;
        }
        NioThread dstOwner = loadImbalance.dstOwner;
        if (this.logger.isFinestEnabled()) {
            NioThread srcOwner = loadImbalance.srcOwner;
            this.logger.finest("Scheduling migration of pipeline " + pipeline + " from " + srcOwner + " to " + dstOwner);
        }
        pipeline.requestMigration(dstOwner);
    }

    public void signalMigrationComplete() {
        this.migrationCompletedCount.inc();
    }

    private final class AddPipelineTask
    implements Runnable {
        private final MigratablePipeline inboundPipeline;
        private final MigratablePipeline outboundPipeline;

        private AddPipelineTask(MigratablePipeline inboundPipeline, MigratablePipeline outboundPipeline) {
            this.inboundPipeline = inboundPipeline;
            this.outboundPipeline = outboundPipeline;
        }

        @Override
        public void run() {
            if (IOBalancer.this.logger.isFinestEnabled()) {
                IOBalancer.this.logger.finest("Adding pipelines: " + this.inboundPipeline + ", " + this.outboundPipeline);
            }
            IOBalancer.this.inLoadTracker.addPipeline(this.inboundPipeline);
            IOBalancer.this.outLoadTracker.addPipeline(this.outboundPipeline);
        }
    }

    private final class RemovePipelineTask
    implements Runnable {
        private final MigratablePipeline inboundPipeline;
        private final MigratablePipeline outboundPipeline;

        private RemovePipelineTask(MigratablePipeline inboundPipeline, MigratablePipeline outboundPipeline) {
            this.inboundPipeline = inboundPipeline;
            this.outboundPipeline = outboundPipeline;
        }

        @Override
        public void run() {
            if (IOBalancer.this.logger.isFinestEnabled()) {
                IOBalancer.this.logger.finest("Removing pipelines: " + this.inboundPipeline + ", " + this.outboundPipeline);
            }
            IOBalancer.this.inLoadTracker.removePipeline(this.inboundPipeline);
            IOBalancer.this.outLoadTracker.removePipeline(this.outboundPipeline);
        }
    }
}

