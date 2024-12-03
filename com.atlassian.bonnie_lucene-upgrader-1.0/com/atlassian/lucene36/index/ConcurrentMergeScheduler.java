/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.IndexWriter;
import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.MergeScheduler;
import com.atlassian.lucene36.store.Directory;
import com.atlassian.lucene36.util.CollectionUtil;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConcurrentMergeScheduler
extends MergeScheduler {
    private int mergeThreadPriority = -1;
    protected List<MergeThread> mergeThreads = new ArrayList<MergeThread>();
    private int maxThreadCount = Math.max(1, Math.min(3, Runtime.getRuntime().availableProcessors() / 2));
    private int maxMergeCount = this.maxThreadCount + 2;
    protected Directory dir;
    private volatile boolean closed;
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
    static boolean anyExceptions = false;
    private boolean suppressExceptions;
    private static List<ConcurrentMergeScheduler> allInstances;

    public ConcurrentMergeScheduler() {
        if (allInstances != null) {
            this.addMyself();
        }
    }

    public void setMaxThreadCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count should be at least 1");
        }
        if (count > this.maxMergeCount) {
            throw new IllegalArgumentException("count should be <= maxMergeCount (= " + this.maxMergeCount + ")");
        }
        this.maxThreadCount = count;
    }

    public int getMaxThreadCount() {
        return this.maxThreadCount;
    }

    public void setMaxMergeCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("count should be at least 1");
        }
        if (count < this.maxThreadCount) {
            throw new IllegalArgumentException("count should be >= maxThreadCount (= " + this.maxThreadCount + ")");
        }
        this.maxMergeCount = count;
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
        CollectionUtil.mergeSort(activeMerges, compareByMergeDocCount);
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
        return this.writer != null && this.writer.verbose();
    }

    protected void message(String message) {
        this.writer.message("CMS: " + message);
    }

    private synchronized void initMergeThreadPriority() {
        if (this.mergeThreadPriority == -1) {
            this.mergeThreadPriority = 1 + Thread.currentThread().getPriority();
            if (this.mergeThreadPriority > 10) {
                this.mergeThreadPriority = 10;
            }
        }
    }

    public void close() {
        this.closed = true;
        this.sync();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sync() {
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
            if (toSync == null) break;
            try {
                toSync.join();
            }
            catch (InterruptedException ie) {
                throw new ThreadInterruptedException(ie);
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
    public void merge(IndexWriter writer) throws IOException {
        assert (!Thread.holdsLock(writer));
        this.writer = writer;
        this.initMergeThreadPriority();
        this.dir = writer.getDirectory();
        if (this.verbose()) {
            this.message("now merge");
            this.message("  index: " + writer.segString());
        }
        while (true) {
            Object var11_8;
            ConcurrentMergeScheduler concurrentMergeScheduler = this;
            synchronized (concurrentMergeScheduler) {
                long startStallTime = 0L;
                while (this.mergeThreadCount() >= 1 + this.maxMergeCount) {
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
            }
            MergePolicy.OneMerge merge = writer.getNextMerge();
            if (merge == null) {
                if (this.verbose()) {
                    this.message("  no more merges pending; now return");
                }
                return;
            }
            writer.mergeInit(merge);
            boolean success = false;
            try {
                ConcurrentMergeScheduler concurrentMergeScheduler2 = this;
                synchronized (concurrentMergeScheduler2) {
                    this.message("  consider merge " + merge.segString(this.dir));
                    MergeThread merger = this.getMergeThread(writer, merge);
                    this.mergeThreads.add(merger);
                    if (this.verbose()) {
                        this.message("    launch new thread [" + merger.getName() + "]");
                    }
                    merger.start();
                    this.updateMergeThreads();
                    success = true;
                }
                var11_8 = null;
                if (success) continue;
            }
            catch (Throwable throwable) {
                var11_8 = null;
                if (!success) {
                    writer.mergeFinish(merge);
                }
                throw throwable;
            }
            writer.mergeFinish(merge);
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean anyUnhandledExceptions() {
        if (allInstances == null) {
            throw new RuntimeException("setTestMode() was not called; often this is because your test case's setUp method fails to call super.setUp in LuceneTestCase");
        }
        List<ConcurrentMergeScheduler> list = allInstances;
        synchronized (list) {
            int count = allInstances.size();
            for (int i = 0; i < count; ++i) {
                allInstances.get(i).sync();
            }
            boolean v = anyExceptions;
            anyExceptions = false;
            return v;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clearUnhandledExceptions() {
        List<ConcurrentMergeScheduler> list = allInstances;
        synchronized (list) {
            anyExceptions = false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addMyself() {
        List<ConcurrentMergeScheduler> list = allInstances;
        synchronized (list) {
            int size = allInstances.size();
            int upto = 0;
            for (int i = 0; i < size; ++i) {
                ConcurrentMergeScheduler other = allInstances.get(i);
                if (other.closed && 0 == other.mergeThreadCount()) continue;
                allInstances.set(upto++, other);
            }
            allInstances.subList(upto, allInstances.size()).clear();
            allInstances.add(this);
        }
    }

    void setSuppressExceptions() {
        this.suppressExceptions = true;
    }

    void clearSuppressExceptions() {
        this.suppressExceptions = false;
    }

    @Deprecated
    public static void setTestMode() {
        allInstances = new ArrayList<ConcurrentMergeScheduler>();
    }

    protected class MergeThread
    extends Thread {
        IndexWriter tWriter;
        MergePolicy.OneMerge startMerge;
        MergePolicy.OneMerge runningMerge;
        private volatile boolean done;

        public MergeThread(IndexWriter writer, MergePolicy.OneMerge startMerge) throws IOException {
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
            catch (NullPointerException npe) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public void run() {
            MergePolicy.OneMerge merge = this.startMerge;
            try {
                block11: {
                    try {
                        if (ConcurrentMergeScheduler.this.verbose()) {
                            ConcurrentMergeScheduler.this.message("  merge thread: start");
                        }
                        while (true) {
                            this.setRunningMerge(merge);
                            ConcurrentMergeScheduler.this.doMerge(merge);
                            merge = this.tWriter.getNextMerge();
                            if (merge == null) break;
                            this.tWriter.mergeInit(merge);
                            ConcurrentMergeScheduler.this.updateMergeThreads();
                            if (!ConcurrentMergeScheduler.this.verbose()) continue;
                            ConcurrentMergeScheduler.this.message("  merge thread: do another merge " + merge.segString(ConcurrentMergeScheduler.this.dir));
                        }
                        if (!ConcurrentMergeScheduler.this.verbose()) break block11;
                        ConcurrentMergeScheduler.this.message("  merge thread: done");
                    }
                    catch (Throwable exc) {
                        if (!(exc instanceof MergePolicy.MergeAbortedException) && !ConcurrentMergeScheduler.this.suppressExceptions) {
                            anyExceptions = true;
                            ConcurrentMergeScheduler.this.handleMergeException(exc);
                        }
                        Object var4_3 = null;
                        this.done = true;
                        ConcurrentMergeScheduler concurrentMergeScheduler3 = ConcurrentMergeScheduler.this;
                        synchronized (concurrentMergeScheduler3) {
                            ConcurrentMergeScheduler.this.updateMergeThreads();
                            ConcurrentMergeScheduler.this.notifyAll();
                            return;
                        }
                    }
                }
                Object var4_2 = null;
                this.done = true;
                ConcurrentMergeScheduler concurrentMergeScheduler = ConcurrentMergeScheduler.this;
                synchronized (concurrentMergeScheduler) {
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    ConcurrentMergeScheduler.this.notifyAll();
                    return;
                }
            }
            catch (Throwable throwable) {
                Object var4_4 = null;
                this.done = true;
                ConcurrentMergeScheduler concurrentMergeScheduler2 = ConcurrentMergeScheduler.this;
                synchronized (concurrentMergeScheduler2) {
                    ConcurrentMergeScheduler.this.updateMergeThreads();
                    ConcurrentMergeScheduler.this.notifyAll();
                    throw throwable;
                }
            }
        }
    }
}

