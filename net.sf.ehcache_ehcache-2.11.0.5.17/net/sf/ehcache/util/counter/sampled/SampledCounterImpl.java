/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter.sampled;

import java.util.TimerTask;
import net.sf.ehcache.util.CircularLossyQueue;
import net.sf.ehcache.util.counter.CounterImpl;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.SampledCounterConfig;
import net.sf.ehcache.util.counter.sampled.TimeStampedCounterValue;

public class SampledCounterImpl
extends CounterImpl
implements SampledCounter {
    private static final int MILLIS_PER_SEC = 1000;
    protected final CircularLossyQueue<TimeStampedCounterValue> history;
    protected final boolean resetOnSample;
    private final TimerTask samplerTask;
    private final long intervalMillis;

    public SampledCounterImpl(SampledCounterConfig config) {
        this(config.getIntervalSecs(), config.getHistorySize(), config.isResetOnSample(), config.getInitialValue(), true);
    }

    public SampledCounterImpl(long intervalInSeconds, int historySize, boolean resetOnSample, long initValue, boolean sampleNow) {
        super(initValue);
        this.intervalMillis = intervalInSeconds * 1000L;
        this.history = new CircularLossyQueue(historySize);
        this.resetOnSample = resetOnSample;
        this.samplerTask = new TimerTask(){

            @Override
            public void run() {
                SampledCounterImpl.this.recordSample();
            }
        };
        if (sampleNow) {
            this.recordSample();
        }
    }

    @Override
    public TimeStampedCounterValue getMostRecentSample() {
        return this.history.peek();
    }

    @Override
    public TimeStampedCounterValue[] getAllSampleValues() {
        return this.history.toArray((TimeStampedCounterValue[])new TimeStampedCounterValue[this.history.depth()]);
    }

    @Override
    public void shutdown() {
        if (this.samplerTask != null) {
            this.samplerTask.cancel();
        }
    }

    public TimerTask getTimerTask() {
        return this.samplerTask;
    }

    public long getIntervalMillis() {
        return this.intervalMillis;
    }

    void recordSample() {
        long sample = this.resetOnSample ? this.getAndReset() : this.getValue();
        long now = System.currentTimeMillis();
        TimeStampedCounterValue timedSample = new TimeStampedCounterValue(now, sample);
        this.history.push(timedSample);
    }

    @Override
    public long getAndReset() {
        return this.getAndSet(0L);
    }
}

