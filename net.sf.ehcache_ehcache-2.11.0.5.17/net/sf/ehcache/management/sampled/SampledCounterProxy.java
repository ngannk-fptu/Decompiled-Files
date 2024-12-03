/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.TimeStampedCounterValue;
import org.terracotta.statistics.archive.Timestamped;

public class SampledCounterProxy<E extends Number>
implements SampledCounter {
    protected final ExtendedStatistics.Statistic<E> rate;

    public SampledCounterProxy(ExtendedStatistics.Statistic<E> rate) {
        this.rate = rate;
    }

    @Override
    public TimeStampedCounterValue getMostRecentSample() {
        return new TimeStampedCounterValue(System.currentTimeMillis(), ((Number)this.rate.value()).longValue());
    }

    @Override
    public TimeStampedCounterValue[] getAllSampleValues() {
        ArrayList<TimeStampedCounterValue> arr = new ArrayList<TimeStampedCounterValue>();
        for (Timestamped<E> ts : this.rate.history()) {
            arr.add(new TimeStampedCounterValue(ts.getTimestamp(), ((Number)ts.getSample()).longValue()));
        }
        return this.sortAndPresent(arr);
    }

    protected TimeStampedCounterValue[] sortAndPresent(List<TimeStampedCounterValue> arr) {
        Collections.sort(arr, new Comparator<TimeStampedCounterValue>(){

            @Override
            public int compare(TimeStampedCounterValue o1, TimeStampedCounterValue o2) {
                return (int)(o1.getTimestamp() - o2.getTimestamp());
            }
        });
        return arr.toArray(new TimeStampedCounterValue[arr.size()]);
    }

    @Override
    public void setValue(long newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long increment() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long decrement() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getAndSet(long newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long increment(long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long decrement(long amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getAndReset() {
        throw new UnsupportedOperationException();
    }
}

