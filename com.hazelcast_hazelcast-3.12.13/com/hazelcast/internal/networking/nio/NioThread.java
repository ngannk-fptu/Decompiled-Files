/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio;

import com.hazelcast.internal.metrics.Probe;
import com.hazelcast.internal.metrics.ProbeLevel;
import com.hazelcast.internal.networking.ChannelErrorHandler;
import com.hazelcast.internal.networking.nio.NioPipeline;
import com.hazelcast.internal.networking.nio.SelectorMode;
import com.hazelcast.internal.networking.nio.SelectorOptimizer;
import com.hazelcast.internal.util.counters.SwCounter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.operationexecutor.OperationHostileThread;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.concurrent.IdleStrategy;
import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NioThread
extends Thread
implements OperationHostileThread {
    private static final int SELECT_WAIT_TIME_MILLIS = Integer.getInteger("hazelcast.io.select.wait.time.millis", 5000);
    private static final int SELECT_FAILURE_PAUSE_MILLIS = 1000;
    private static final int SELECT_IDLE_COUNT_THRESHOLD = 10;
    private static final Random RANDOM = new Random();
    private static final int TEST_SELECTOR_BUG_PROBABILITY = Integer.parseInt(System.getProperty("hazelcast.io.selector.bug.probability", "16"));
    @Probe(name="ioThreadId", level=ProbeLevel.INFO)
    public int id;
    @Probe(level=ProbeLevel.INFO)
    volatile long bytesTransceived;
    @Probe(level=ProbeLevel.INFO)
    volatile long framesTransceived;
    @Probe(level=ProbeLevel.INFO)
    volatile long priorityFramesTransceived;
    @Probe(level=ProbeLevel.INFO)
    volatile long processCount;
    @Probe(name="taskQueueSize")
    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
    @Probe
    private final SwCounter eventCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter selectorIOExceptionCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter completedTaskCount = SwCounter.newSwCounter();
    @Probe
    private final SwCounter selectorRebuildCount = SwCounter.newSwCounter();
    private final ILogger logger;
    private Selector selector;
    private final ChannelErrorHandler errorHandler;
    private final SelectorMode selectMode;
    private final IdleStrategy idleStrategy;
    private volatile long lastSelectTimeMs;
    private volatile boolean stop;
    private boolean selectorWorkaroundTest;

    public NioThread(String threadName, ILogger logger, ChannelErrorHandler errorHandler) {
        this(threadName, logger, errorHandler, SelectorMode.SELECT, null);
    }

    public NioThread(String threadName, ILogger logger, ChannelErrorHandler errorHandler, SelectorMode selectMode, IdleStrategy idleStrategy) {
        this(threadName, logger, errorHandler, selectMode, SelectorOptimizer.newSelector(logger), idleStrategy);
    }

    public NioThread(String threadName, ILogger logger, ChannelErrorHandler errorHandler, SelectorMode selectMode, Selector selector, IdleStrategy idleStrategy) {
        super(threadName);
        this.logger = logger;
        this.selectMode = selectMode;
        this.errorHandler = errorHandler;
        this.selector = selector;
        this.selectorWorkaroundTest = false;
        this.idleStrategy = idleStrategy;
    }

    void setSelectorWorkaroundTest(boolean selectorWorkaroundTest) {
        this.selectorWorkaroundTest = selectorWorkaroundTest;
    }

    public long bytesTransceived() {
        return this.bytesTransceived;
    }

    public long framesTransceived() {
        return this.framesTransceived;
    }

    public long priorityFramesTransceived() {
        return this.priorityFramesTransceived;
    }

    public long handleCount() {
        return this.processCount;
    }

    public long eventCount() {
        return this.eventCount.get();
    }

    public long completedTaskCount() {
        return this.completedTaskCount.get();
    }

    public Selector getSelector() {
        return this.selector;
    }

    public long getEventCount() {
        return this.eventCount.get();
    }

    @Probe
    private long idleTimeMs() {
        return Math.max(System.currentTimeMillis() - this.lastSelectTimeMs, 0L);
    }

    public void addTask(Runnable task) {
        this.taskQueue.add(task);
    }

    public void addTaskAndWakeup(Runnable task) {
        this.taskQueue.add(task);
        if (this.selectMode != SelectorMode.SELECT_NOW) {
            this.selector.wakeup();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    switch (this.selectMode) {
                        case SELECT_WITH_FIX: {
                            this.selectLoopWithFix();
                            break;
                        }
                        case SELECT_NOW: {
                            this.selectNowLoop();
                            break;
                        }
                        case SELECT: {
                            this.selectLoop();
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Selector.select mode not set, use -Dhazelcast.io.selectorMode={select|selectnow|selectwithfix} to explicitly specify select mode or leave empty for default select mode.");
                        }
                    }
                }
                catch (IOException nonFatalException) {
                    this.selectorIOExceptionCount.inc();
                    this.logger.warning(this.getName() + " " + nonFatalException.toString(), nonFatalException);
                    this.coolDown();
                    continue;
                }
                break;
            }
        }
        catch (Throwable e) {
            this.errorHandler.onError(null, e);
        }
        finally {
            this.closeSelector();
        }
        this.logger.finest(this.getName() + " finished");
    }

    private void coolDown() {
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException i) {
            this.interrupt();
        }
    }

    private void selectLoop() throws IOException {
        while (!this.stop) {
            this.processTaskQueue();
            int selectedKeys = this.selector.select(SELECT_WAIT_TIME_MILLIS);
            if (selectedKeys <= 0) continue;
            this.processSelectionKeys();
        }
    }

    private void selectLoopWithFix() throws IOException {
        int idleCount = 0;
        while (!this.stop) {
            this.processTaskQueue();
            long before = System.currentTimeMillis();
            int selectedKeys = this.selector.select(SELECT_WAIT_TIME_MILLIS);
            if (selectedKeys > 0) {
                idleCount = 0;
                this.processSelectionKeys();
                continue;
            }
            if (!this.taskQueue.isEmpty()) {
                idleCount = 0;
                continue;
            }
            long selectTimeTaken = System.currentTimeMillis() - before;
            idleCount = selectTimeTaken < (long)SELECT_WAIT_TIME_MILLIS ? idleCount + 1 : 0;
            if (!this.selectorBugDetected(idleCount)) continue;
            this.rebuildSelector();
            idleCount = 0;
        }
    }

    private boolean selectorBugDetected(int idleCount) {
        return idleCount > 10 || this.selectorWorkaroundTest && RANDOM.nextInt(TEST_SELECTOR_BUG_PROBABILITY) == 1;
    }

    private void selectNowLoop() throws IOException {
        long idleRound = 0L;
        while (!this.stop) {
            boolean tasksProcessed = this.processTaskQueue();
            int selectedKeys = this.selector.selectNow();
            if (selectedKeys > 0) {
                this.processSelectionKeys();
                idleRound = 0L;
                continue;
            }
            if (tasksProcessed) {
                idleRound = 0L;
                continue;
            }
            if (this.idleStrategy == null) continue;
            this.idleStrategy.idle(++idleRound);
        }
    }

    private boolean processTaskQueue() {
        Runnable task;
        boolean tasksProcessed = false;
        while (!this.stop && (task = this.taskQueue.poll()) != null) {
            task.run();
            this.completedTaskCount.inc();
            tasksProcessed = true;
        }
        return tasksProcessed;
    }

    private void processSelectionKeys() {
        this.lastSelectTimeMs = System.currentTimeMillis();
        Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey sk = it.next();
            it.remove();
            this.processSelectionKey(sk);
        }
    }

    private void processSelectionKey(SelectionKey sk) {
        NioPipeline pipeline = (NioPipeline)sk.attachment();
        try {
            if (!sk.isValid()) {
                throw new CancelledKeyException();
            }
            this.eventCount.inc();
            pipeline.process();
        }
        catch (Throwable t) {
            pipeline.onError(t);
        }
    }

    private void closeSelector() {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("Closing selector for:" + this.getName());
        }
        try {
            this.selector.close();
        }
        catch (Exception e) {
            this.logger.finest("Failed to close selector", e);
        }
    }

    public void shutdown() {
        this.stop = true;
        this.taskQueue.clear();
        this.interrupt();
    }

    private void rebuildSelector() {
        this.selectorRebuildCount.inc();
        Selector newSelector = SelectorOptimizer.newSelector(this.logger);
        Selector oldSelector = this.selector;
        for (SelectionKey key : oldSelector.keys()) {
            NioPipeline pipeline = (NioPipeline)key.attachment();
            SelectableChannel channel = key.channel();
            try {
                int ops = key.interestOps();
                SelectionKey newSelectionKey = channel.register(newSelector, ops, pipeline);
                pipeline.setSelectionKey(newSelectionKey);
            }
            catch (ClosedChannelException e) {
                this.logger.info("Channel was closed while trying to register with new selector.");
            }
            catch (CancelledKeyException e) {
                EmptyStatement.ignore(e);
            }
            key.cancel();
        }
        this.closeSelector();
        this.selector = newSelector;
        this.logger.warning("Recreated Selector because of possible java/network stack bug.");
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

