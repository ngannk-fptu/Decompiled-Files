/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MergePolicy;
import org.apache.lucene.index.MergeScheduler;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.CollectionUtil;
import org.apache.lucene.util.ThreadInterruptedException;

public class ConcurrentMergeScheduler
extends MergeScheduler {
    private int mergeThreadPriority = -1;
    protected List<MergeThread> mergeThreads = new ArrayList<MergeThread>();
    public static final int DEFAULT_MAX_THREAD_COUNT = 1;
    public static final int DEFAULT_MAX_MERGE_COUNT = 2;
    private int maxThreadCount = 1;
    private int maxMergeCount = 2;
    protected Directory dir;
    protected IndexWriter writer;
    protected int mergeThreadCount;
    protected static final Comparator<MergeThread> compareByMergeDocCount = new Comparator<MergeThread>(){

        @Override
        public int compare(MergeThread t1, MergeThread t2) {
            MergePolicy.OneMerge m1 = t1.getCurrentMerge();
            MergePolicy.OneMerge m2 = t2.getCurrentMerge();
            int c1 = m1 == null ? Integer.MAX_VALUE : m1.totalDocCount;
            int c2 = m2 == null ? Integer.MAX_VALUE : m2.totalDocCount;
            return c2 - c1;
        }
    };
    private boolean suppressExceptions;

    public void setMaxMergesAndThreads(int maxMergeCount, int maxThreadCount) {
        if (maxThreadCount < 1) {
            throw new IllegalArgumentException("maxThreadCount should be at least 1");
        }
        if (maxMergeCount < 1) {
            throw new IllegalArgumentException("maxMergeCount should be at least 1");
        }
        if (maxThreadCount > maxMergeCount) {
            throw new IllegalArgumentException("maxThreadCount should be <= maxMergeCount (= " + maxMergeCount + ")");
        }
        this.maxThreadCount = maxThreadCount;
        this.maxMergeCount = maxMergeCount;
    }

    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }

    public int getMaxMergeCount() {
        return this.maxMergeCount;
    }

    public synchronized int getMergeThreadPriority() {
        this.initMergeThreadPriority();
        return this.mergeThreadPriority;
    }

    public synchronized void setMergeThreadPriority(int pri) {
        if (pri > 10 || pri < 1) {
            throw new IllegalArgumentException("priority must be in range 1 .. 10 inclusive");
        }
        this.mergeThreadPriority = pri;
        this.updateMergeThreads();
    }

    protected synchronized void updateMergeThreads() {
        ArrayList<MergeThread> activeMerges = new ArrayList<MergeThread>();
        int threadIdx = 0;
        while (threadIdx < this.mergeThreads.size()) {
            MergeThread mergeThread = this.mergeThreads.get(threadIdx);
            if (!mergeThread.isAlive()) {
                this.mergeThreads.remove(threadIdx);
                continue;
            }
            if (mergeThread.getCurrentMerge() != null) {
                activeMerges.add(mergeThread);
            }
            ++threadIdx;
        }
        CollectionUtil.timSort(activeMerges, compareByMergeDocCount);
        int pri = this.mergeThreadPriority;
        int activeMergeCount = activeMerges.size();
        for (threadIdx = 0; threadIdx < activeMergeCount; ++threadIdx) {
            boolean doPause;
            MergeThread mergeThread = (MergeThread)activeMerges.get(threadIdx);
            MergePolicy.OneMerge merge = mergeThread.getCurrentMerge();
            if (merge == null) continue;
            boolean bl = doPause = threadIdx < activeMergeCount - this.maxThreadCount;
            if (this.verbose() && doPause != merge.getPause()) {
                if (doPause) {
                    this.message("pause thread " + mergeThread.getName());
                } else {
                    this.message("unpause thread " + mergeThread.getName());
                }
            }
            if (doPause != merge.getPause()) {
                merge.setPause(doPause);
            }
            if (doPause) continue;
            if (this.verbose()) {
                this.message("set priority of merge thread " + mergeThread.getName() + " to " + pri);
            }
            mergeThread.setThreadPriority(pri);
            pri = Math.min(10, 1 + pri);
        }
    }

    protected boolean verbose() {
        return this.writer != null && this.writer.infoStream.isEnabled("CMS");
    }

    protected void message(String message) {
        this.writer.infoStream.message("CMS", message);
    }

    private synchronized void initMergeThreadPriority() {
        if (this.mergeThreadPriority == -1) {
            this.mergeThreadPriority = 1 + Thread.currentThread().getPriority();
            if (this.mergeThreadPriority > 10) {
                this.mergeThreadPriority = 10;
            }
        }
    }

    @Override
    public void close() {
        this.sync();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sync() {
        block12: {
            boolean interrupted = false;
            block8: while (true) {
                while (true) {
                    Thread toSync = null;
                    ConcurrentMergeScheduler concurrentMergeScheduler = this;
                    synchronized (concurrentMergeScheduler) {
                        for (MergeThread t : this.mergeThreads) {
                            if (!t.isAlive()) continue;
                            toSync = t;
                            break;
                        }
                    }
                    if (toSync == null) break block12;
                    try {
                        toSync.join();
                        continue block8;
                    }
                    catch (InterruptedException ie) {
                        interrupted = true;
                        continue;
                    }
                    break;
                }
            }
            finally {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    protected synchronized int mergeThreadCount() {
        int count = 0;
        for (MergeThread mt : this.mergeThreads) {
            if (!mt.isAlive() || mt.getCurrentMerge() == null) continue;
            ++count;
        }
        return count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void merge(IndexWriter writer) throws IOException {
        assert (!Thread.holdsLock(writer));
        this.writer = writer;
        this.initMergeThreadPriority();
        this.dir = writer.getDirectory();
        if (this.verbose()) {
            this.message("now merge");
            this.message("  index: " + writer.segString());
        }
        while (true) {
            MergePolicy.OneMerge merge;
            long startStallTime = 0L;
            while (writer.hasPendingMerges() && this.mergeThreadCount() >= this.maxMergeCount) {
                startStallTime = System.currentTimeMillis();
                if (this.verbose()) {
                    this.message("    too many merges; stalling...");
                }
                try {
                    this.wait();
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
            if (this.verbose() && startStallTime != 0L) {
                this.message("  stalled for " + (System.currentTimeMillis() - startStallTime) + " msec");
            }
            if ((merge = writer.getNextMerge()) == null) {
                if (this.verbose()) {
                    this.message("  no more merges pending; now return");
                }
                return;
            }
            boolean success = false;
            try {
                if (this.verbose()) {
                    this.message("  consider merge " + writer.segString(merge.segments));
                }
                MergeThread merger = this.getMergeThread(writer, merge);
                this.mergeThreads.add(merger);
                if (this.verbose()) {
                    this.message("    launch new thread [" + merger.getName() + "]");
                }
                merger.start();
                this.updateMergeThreads();
                success = true;
                continue;
            }
            finally {
                if (success) continue;
                writer.mergeFinish(merge);
                continue;
            }
            break;
        }
    }

    protected void doMerge(MergePolicy.OneMerge merge) throws IOException {
        this.writer.merge(merge);
    }

    protected synchronized MergeThread getMergeThread(IndexWriter writer, MergePolicy.OneMerge merge) throws IOException {
        MergeThread thread = new MergeThread(writer, merge);
        thread.setThreadPriority(this.mergeThreadPriority);
        thread.setDaemon(true);
        thread.setName("Lucene Merge Thread #" + this.mergeThreadCount++);
        return thread;
    }

    protected void handleMergeException(Throwable exc) {
        try {
            Thread.sleep(250L);
        }
        catch (InterruptedException ie) {
            throw new ThreadInterruptedException(ie);
        }
        throw new MergePolicy.MergeException(exc, this.dir);
    }

    void setSuppressExceptions() {
        this.suppressExceptions = true;
    }

    void clearSuppressExceptions() {
        this.suppressExceptions = false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName() + ": ");
        sb.append("maxThreadCount=").append(this.maxThreadCount).append(", ");
        sb.append("maxMergeCount=").append(this.maxMergeCount).append(", ");
        sb.append("mergeThreadPriority=").append(this.mergeThreadPriority);
        return sb.toString();
    }

    @Override
    public MergeScheduler clone() {
        ConcurrentMergeScheduler clone = (ConcurrentMergeScheduler)super.clone();
        clone.writer = null;
        clone.dir = null;
        clone.mergeThreads = new ArrayList<MergeThread>();
        return clone;
    }

    protected class MergeThread
    extends Thread {
        IndexWriter tWriter;
        MergePolicy.OneMerge startMerge;
        MergePolicy.OneMerge runningMerge;
        private volatile boolean done;

        public MergeThread(IndexWriter writer, MergePolicy.OneMerge startMerge) {
            this.tWriter = writer;
            this.startMerge = startMerge;
        }

        public synchronized void setRunningMerge(MergePolicy.OneMerge merge) {
            this.runningMerge = merge;
        }

        public synchronized MergePolicy.OneMerge getRunningMerge() {
            return this.runningMerge;
        }

        public synchronized MergePolicy.OneMerge getCurrentMerge() {
            if (this.done) {
                return null;
            }
            if (this.runningMerge != null) {
                return this.runningMerge;
            }
            return this.startMerge;
        }

        public void setThreadPriority(int pri) {
            try {
                this.setPriority(pri);
            }
            catch (NullPointerException nullPointerException) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            ConcurrentMergeScheduler concurrentMergeScheduler;
            MergePolicy.OneMerge merge = this.startMerge;
            try {
                if (ConcurrentMergeScheduler.this.verbose()) {
                    ConcurrentMergeScheduler.this.message("  merge thread: start");
                }
                while (true) {
                    this.setRunningMerge(merge);
                    ConcurrentMergeScheduler.this.doMerge(merge);
                    merge = this.tWriter.getNextMerge();
                    concurrentMergeScheduler = ConcurrentMergeScheduler.this;
                    synchronized (concurrentMergeScheduler) {
                        ConcurrentMergeScheduler.this.notifyAll();
                    }
                    if (merge == null) break;
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    if (!ConcurrentMergeScheduler.this.verbose()) continue;
                    ConcurrentMergeScheduler.this.message("  merge thread: do another merge " + this.tWriter.segString(merge.segments));
                }
                if (ConcurrentMergeScheduler.this.verbose()) {
                    ConcurrentMergeScheduler.this.message("  merge thread: done");
                }
            }
            catch (Throwable exc) {
                if (!(exc instanceof MergePolicy.MergeAbortedException) && !ConcurrentMergeScheduler.this.suppressExceptions) {
                    ConcurrentMergeScheduler.this.handleMergeException(exc);
                }
            }
            finally {
                this.done = true;
                concurrentMergeScheduler = ConcurrentMergeScheduler.this;
                synchronized (concurrentMergeScheduler) {
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    ConcurrentMergeScheduler.this.notifyAll();
                }
            }
        }
    }
}

