/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.search.Collector;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.util.Counter;
import com.atlassian.lucene36.util.ThreadInterruptedException;
import java.io.IOException;

public class TimeLimitingCollector
extends Collector {
    private long t0 = Long.MIN_VALUE;
    private long timeout = Long.MIN_VALUE;
    private Collector collector;
    private final Counter clock;
    private final long ticksAllowed;
    private boolean greedy = false;
    private int docBase;

    public TimeLimitingCollector(Collector collector, Counter clock, long ticksAllowed) {
        this.collector = collector;
        this.clock = clock;
        this.ticksAllowed = ticksAllowed;
    }

    public void setBaseline(long clockTime) {
        this.t0 = clockTime;
        this.timeout = this.t0 + this.ticksAllowed;
    }

    public void setBaseline() {
        this.setBaseline(this.clock.get());
    }

    public boolean isGreedy() {
        return this.greedy;
    }

    public void setGreedy(boolean greedy) {
        this.greedy = greedy;
    }

    public void collect(int doc) throws IOException {
        long time = this.clock.get();
        if (this.timeout < time) {
            if (this.greedy) {
                this.collector.collect(doc);
            }
            throw new TimeExceededException(this.timeout - this.t0, time - this.t0, this.docBase + doc);
        }
        this.collector.collect(doc);
    }

    public void setNextReader(IndexReader reader, int base) throws IOException {
        this.collector.setNextReader(reader, base);
        this.docBase = base;
        if (Long.MIN_VALUE == this.t0) {
            this.setBaseline();
        }
    }

    public void setScorer(Scorer scorer) throws IOException {
        this.collector.setScorer(scorer);
    }

    public boolean acceptsDocsOutOfOrder() {
        return this.collector.acceptsDocsOutOfOrder();
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    public static Counter getGlobalCounter() {
        return TimerThreadHolder.THREAD.counter;
    }

    public static TimerThread getGlobalTimerThread() {
        return TimerThreadHolder.THREAD;
    }

    public static final class TimerThread
    extends Thread {
        public static final String THREAD_NAME = "TimeLimitedCollector timer thread";
        public static final int DEFAULT_RESOLUTION = 20;
        private volatile long time = 0L;
        private volatile boolean stop = false;
        private volatile long resolution;
        final Counter counter;

        public TimerThread(long resolution, Counter counter) {
            super(THREAD_NAME);
            this.resolution = resolution;
            this.counter = counter;
            this.setDaemon(true);
        }

        public TimerThread(Counter counter) {
            this(20L, counter);
        }

        public void run() {
            while (!this.stop) {
                this.counter.addAndGet(this.resolution);
                try {
                    Thread.sleep(this.resolution);
                }
                catch (InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
            }
        }

        public long getMilliseconds() {
            return this.time;
        }

        public void stopTimer() {
            this.stop = true;
        }

        public long getResolution() {
            return this.resolution;
        }

        public void setResolution(long resolution) {
            this.resolution = Math.max(resolution, 5L);
        }
    }

    private static final class TimerThreadHolder {
        static final TimerThread THREAD = new TimerThread(Counter.newCounter(true));

        private TimerThreadHolder() {
        }

        static {
            THREAD.start();
        }
    }

    public static class TimeExceededException
    extends RuntimeException {
        private long timeAllowed;
        private long timeElapsed;
        private int lastDocCollected;

        private TimeExceededException(long timeAllowed, long timeElapsed, int lastDocCollected) {
            super("Elapsed time: " + timeElapsed + "Exceeded allowed search time: " + timeAllowed + " ms.");
            this.timeAllowed = timeAllowed;
            this.timeElapsed = timeElapsed;
            this.lastDocCollected = lastDocCollected;
        }

        public long getTimeAllowed() {
            return this.timeAllowed;
        }

        public long getTimeElapsed() {
            return this.timeElapsed;
        }

        public int getLastDocCollected() {
            return this.lastDocCollected;
        }
    }
}

