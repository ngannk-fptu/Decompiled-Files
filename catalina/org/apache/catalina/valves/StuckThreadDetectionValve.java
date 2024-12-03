/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletException;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class StuckThreadDetectionValve
extends ValveBase {
    private static final Log log = LogFactory.getLog(StuckThreadDetectionValve.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.valves");
    private final AtomicInteger stuckCount = new AtomicInteger(0);
    private AtomicLong interruptedThreadsCount = new AtomicLong();
    private int threshold = 600;
    private int interruptThreadThreshold;
    private final Map<Long, MonitoredThread> activeThreads = new ConcurrentHashMap<Long, MonitoredThread>();
    private final Queue<CompletedStuckThread> completedStuckThreadsQueue = new ConcurrentLinkedQueue<CompletedStuckThread>();

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getInterruptThreadThreshold() {
        return this.interruptThreadThreshold;
    }

    public void setInterruptThreadThreshold(int interruptThreadThreshold) {
        this.interruptThreadThreshold = interruptThreadThreshold;
    }

    public StuckThreadDetectionValve() {
        super(true);
    }

    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Monitoring stuck threads with threshold = " + this.threshold + " sec"));
        }
    }

    private void notifyStuckThreadDetected(MonitoredThread monitoredThread, long activeTime, int numStuckThreads) {
        if (log.isWarnEnabled()) {
            String msg = sm.getString("stuckThreadDetectionValve.notifyStuckThreadDetected", new Object[]{monitoredThread.getThread().getName(), activeTime, monitoredThread.getStartTime(), numStuckThreads, monitoredThread.getRequestUri(), this.threshold, String.valueOf(monitoredThread.getThread().getId())});
            Throwable th = new Throwable();
            th.setStackTrace(monitoredThread.getThread().getStackTrace());
            log.warn((Object)msg, th);
        }
    }

    private void notifyStuckThreadCompleted(CompletedStuckThread thread, int numStuckThreads) {
        if (log.isWarnEnabled()) {
            String msg = sm.getString("stuckThreadDetectionValve.notifyStuckThreadCompleted", new Object[]{thread.getName(), thread.getTotalActiveTime(), numStuckThreads, String.valueOf(thread.getId())});
            log.warn((Object)msg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        if (this.threshold <= 0) {
            this.getNext().invoke(request, response);
            return;
        }
        Thread currentThread = Thread.currentThread();
        Long key = currentThread.getId();
        StringBuffer requestUrl = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestUrl.append('?');
            requestUrl.append(request.getQueryString());
        }
        MonitoredThread monitoredThread = new MonitoredThread(currentThread, requestUrl.toString(), this.interruptThreadThreshold > 0);
        this.activeThreads.put(key, monitoredThread);
        try {
            this.getNext().invoke(request, response);
        }
        finally {
            this.activeThreads.remove(key);
            if (monitoredThread.markAsDone() == MonitoredThreadState.STUCK) {
                if (monitoredThread.wasInterrupted()) {
                    this.interruptedThreadsCount.incrementAndGet();
                }
                this.completedStuckThreadsQueue.add(new CompletedStuckThread(monitoredThread.getThread(), monitoredThread.getActiveTimeInMillis()));
            }
        }
    }

    @Override
    public void backgroundProcess() {
        super.backgroundProcess();
        long thresholdInMillis = (long)this.threshold * 1000L;
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            long activeTime = monitoredThread.getActiveTimeInMillis();
            if (activeTime >= thresholdInMillis && monitoredThread.markAsStuckIfStillRunning()) {
                int numStuckThreads = this.stuckCount.incrementAndGet();
                this.notifyStuckThreadDetected(monitoredThread, activeTime, numStuckThreads);
            }
            if (this.interruptThreadThreshold <= 0 || activeTime < (long)this.interruptThreadThreshold * 1000L) continue;
            monitoredThread.interruptIfStuck(this.interruptThreadThreshold);
        }
        CompletedStuckThread completedStuckThread = this.completedStuckThreadsQueue.poll();
        while (completedStuckThread != null) {
            int numStuckThreads = this.stuckCount.decrementAndGet();
            this.notifyStuckThreadCompleted(completedStuckThread, numStuckThreads);
            completedStuckThread = this.completedStuckThreadsQueue.poll();
        }
    }

    public int getStuckThreadCount() {
        return this.stuckCount.get();
    }

    public long[] getStuckThreadIds() {
        ArrayList<Long> idList = new ArrayList<Long>();
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            if (!monitoredThread.isMarkedAsStuck()) continue;
            idList.add(monitoredThread.getThread().getId());
        }
        long[] result = new long[idList.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (Long)idList.get(i);
        }
        return result;
    }

    public String[] getStuckThreadNames() {
        ArrayList<String> nameList = new ArrayList<String>();
        for (MonitoredThread monitoredThread : this.activeThreads.values()) {
            if (!monitoredThread.isMarkedAsStuck()) continue;
            nameList.add(monitoredThread.getThread().getName());
        }
        return nameList.toArray(new String[0]);
    }

    public long getInterruptedThreadsCount() {
        return this.interruptedThreadsCount.get();
    }

    private static class MonitoredThread {
        private final Thread thread;
        private final String requestUri;
        private final long start;
        private final AtomicInteger state = new AtomicInteger(MonitoredThreadState.RUNNING.ordinal());
        private final Semaphore interruptionSemaphore;
        private boolean interrupted;

        MonitoredThread(Thread thread, String requestUri, boolean interruptible) {
            this.thread = thread;
            this.requestUri = requestUri;
            this.start = System.currentTimeMillis();
            this.interruptionSemaphore = interruptible ? new Semaphore(1) : null;
        }

        public Thread getThread() {
            return this.thread;
        }

        public String getRequestUri() {
            return this.requestUri;
        }

        public long getActiveTimeInMillis() {
            return System.currentTimeMillis() - this.start;
        }

        public Date getStartTime() {
            return new Date(this.start);
        }

        public boolean markAsStuckIfStillRunning() {
            return this.state.compareAndSet(MonitoredThreadState.RUNNING.ordinal(), MonitoredThreadState.STUCK.ordinal());
        }

        public MonitoredThreadState markAsDone() {
            int val = this.state.getAndSet(MonitoredThreadState.DONE.ordinal());
            MonitoredThreadState threadState = MonitoredThreadState.values()[val];
            if (threadState == MonitoredThreadState.STUCK && this.interruptionSemaphore != null) {
                try {
                    this.interruptionSemaphore.acquire();
                }
                catch (InterruptedException e) {
                    log.debug((Object)"thread interrupted after the request is finished, ignoring", (Throwable)e);
                }
            }
            return threadState;
        }

        boolean isMarkedAsStuck() {
            return this.state.get() == MonitoredThreadState.STUCK.ordinal();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public boolean interruptIfStuck(long interruptThreadThreshold) {
            if (!this.isMarkedAsStuck() || this.interruptionSemaphore == null || !this.interruptionSemaphore.tryAcquire()) {
                return false;
            }
            try {
                if (log.isWarnEnabled()) {
                    String msg = sm.getString("stuckThreadDetectionValve.notifyStuckThreadInterrupted", new Object[]{this.getThread().getName(), this.getActiveTimeInMillis(), this.getStartTime(), this.getRequestUri(), interruptThreadThreshold, String.valueOf(this.getThread().getId())});
                    Throwable th = new Throwable();
                    th.setStackTrace(this.getThread().getStackTrace());
                    log.warn((Object)msg, th);
                }
                this.thread.interrupt();
            }
            finally {
                this.interrupted = true;
                this.interruptionSemaphore.release();
            }
            return true;
        }

        public boolean wasInterrupted() {
            return this.interrupted;
        }
    }

    private static class CompletedStuckThread {
        private final String threadName;
        private final long threadId;
        private final long totalActiveTime;

        CompletedStuckThread(Thread thread, long totalActiveTime) {
            this.threadName = thread.getName();
            this.threadId = thread.getId();
            this.totalActiveTime = totalActiveTime;
        }

        public String getName() {
            return this.threadName;
        }

        public long getId() {
            return this.threadId;
        }

        public long getTotalActiveTime() {
            return this.totalActiveTime;
        }
    }

    private static enum MonitoredThreadState {
        RUNNING,
        STUCK,
        DONE;

    }
}

