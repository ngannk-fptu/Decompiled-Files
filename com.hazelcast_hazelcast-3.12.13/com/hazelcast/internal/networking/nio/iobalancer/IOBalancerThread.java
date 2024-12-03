/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.iobalancer.IOBalancer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.ThreadUtil;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

class IOBalancerThread
extends Thread {
    private static final String THREAD_NAME_PREFIX = "IO.BalancerThread";
    private final IOBalancer ioBalancer;
    private final ILogger log;
    private final long balancerIntervalMs;
    private final BlockingQueue<Runnable> workQueue;
    private volatile boolean shutdown;

    IOBalancerThread(IOBalancer ioBalancer, int balancerIntervalSeconds, String hzName, ILogger log, BlockingQueue<Runnable> workQueue) {
        super(ThreadUtil.createThreadName(hzName, THREAD_NAME_PREFIX));
        this.ioBalancer = ioBalancer;
        this.log = log;
        this.balancerIntervalMs = TimeUnit.SECONDS.toMillis(balancerIntervalSeconds);
        this.workQueue = workQueue;
    }

    void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            this.log.finest("Starting IOBalancer thread");
            long nextRebalanceMs = System.currentTimeMillis() + this.balancerIntervalMs;
            while (!this.shutdown) {
                while (true) {
                    long maxPollDurationMs;
                    Runnable task;
                    Runnable runnable = task = (maxPollDurationMs = nextRebalanceMs - System.currentTimeMillis()) <= 0L ? (Runnable)this.workQueue.poll() : this.workQueue.poll(maxPollDurationMs, TimeUnit.MILLISECONDS);
                    if (task == null) break;
                    task.run();
                }
                this.ioBalancer.rebalance();
                nextRebalanceMs = System.currentTimeMillis() + this.balancerIntervalMs;
            }
        }
        catch (InterruptedException e) {
            this.log.finest("IOBalancer thread stopped");
            EmptyStatement.ignore(e);
        }
        catch (Throwable e) {
            this.log.severe("IOBalancer failed", e);
        }
    }
}

