/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.lucene.index.TrackingIndexWriter;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.util.ThreadInterruptedException;

public class ControlledRealTimeReopenThread<T>
extends Thread
implements Closeable {
    private final ReferenceManager<T> manager;
    private final long targetMaxStaleNS;
    private final long targetMinStaleNS;
    private final TrackingIndexWriter writer;
    private volatile boolean finish;
    private volatile long waitingGen;
    private volatile long searchingGen;
    private long refreshStartGen;
    private final ReentrantLock reopenLock = new ReentrantLock();
    private final Condition reopenCond = this.reopenLock.newCondition();

    public ControlledRealTimeReopenThread(TrackingIndexWriter writer, ReferenceManager<T> manager, double targetMaxStaleSec, double targetMinStaleSec) {
        if (targetMaxStaleSec < targetMinStaleSec) {
            throw new IllegalArgumentException("targetMaxScaleSec (= " + targetMaxStaleSec + ") < targetMinStaleSec (=" + targetMinStaleSec + ")");
        }
        this.writer = writer;
        this.manager = manager;
        this.targetMaxStaleNS = (long)(1.0E9 * targetMaxStaleSec);
        this.targetMinStaleNS = (long)(1.0E9 * targetMinStaleSec);
        manager.addListener(new HandleRefresh());
    }

    private synchronized void refreshDone(boolean didRefresh) {
        this.searchingGen = this.refreshStartGen;
        this.notifyAll();
    }

    @Override
    public synchronized void close() {
        this.finish = true;
        this.reopenLock.lock();
        try {
            this.reopenCond.signal();
        }
        finally {
            this.reopenLock.unlock();
        }
        try {
            this.join();
        }
        catch (InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
        this.searchingGen = Long.MAX_VALUE;
        this.notifyAll();
    }

    public void waitForGeneration(long targetGen) throws InterruptedException {
        this.waitForGeneration(targetGen, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized boolean waitForGeneration(long targetGen, int maxMS) throws InterruptedException {
        long curGen = this.writer.getGeneration();
        if (targetGen > curGen) {
            throw new IllegalArgumentException("targetGen=" + targetGen + " was never returned by the ReferenceManager instance (current gen=" + curGen + ")");
        }
        if (targetGen > this.searchingGen) {
            this.waitingGen = Math.max(this.waitingGen, targetGen);
            this.reopenLock.lock();
            try {
                this.reopenCond.signal();
            }
            finally {
                this.reopenLock.unlock();
            }
            long startMS = System.nanoTime() / 1000000L;
            while (targetGen > this.searchingGen) {
                if (maxMS < 0) {
                    this.wait();
                    continue;
                }
                long msLeft = startMS + (long)maxMS - System.nanoTime() / 1000000L;
                if (msLeft <= 0L) {
                    return false;
                }
                this.wait(msLeft);
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        long lastReopenStartNS = System.nanoTime();
        while (!this.finish) {
            boolean hasWaiting;
            long nextReopenStartNS;
            long sleepNS;
            while (!this.finish && (sleepNS = (nextReopenStartNS = lastReopenStartNS + ((hasWaiting = this.waitingGen > this.searchingGen) ? this.targetMinStaleNS : this.targetMaxStaleNS)) - System.nanoTime()) > 0L) {
                this.reopenLock.lock();
                try {
                    this.reopenCond.awaitNanos(sleepNS);
                }
                catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                finally {
                    this.reopenLock.unlock();
                }
            }
            if (this.finish) break;
            lastReopenStartNS = System.nanoTime();
            this.refreshStartGen = this.writer.getAndIncrementGeneration();
            try {
                this.manager.maybeRefreshBlocking();
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private class HandleRefresh
    implements ReferenceManager.RefreshListener {
        private HandleRefresh() {
        }

        @Override
        public void beforeRefresh() {
        }

        @Override
        public void afterRefresh(boolean didRefresh) {
            ControlledRealTimeReopenThread.this.refreshDone(didRefresh);
        }
    }
}

