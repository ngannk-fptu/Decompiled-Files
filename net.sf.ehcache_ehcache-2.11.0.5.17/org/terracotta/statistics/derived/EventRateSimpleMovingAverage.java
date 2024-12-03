/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.derived;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.jsr166e.LongAdder;
import org.terracotta.statistics.observer.ChainedEventObserver;

public class EventRateSimpleMovingAverage
implements ChainedEventObserver,
ValueStatistic<Double> {
    private static final int PARTITION_COUNT = 10;
    private final Queue<CounterPartition> archive = new ConcurrentLinkedQueue<CounterPartition>();
    private final AtomicReference<CounterPartition> activePartition;
    private volatile long windowSize;
    private volatile long partitionSize;

    public EventRateSimpleMovingAverage(long time, TimeUnit unit) {
        this.windowSize = unit.toNanos(time);
        this.partitionSize = this.windowSize / 10L;
        this.activePartition = new AtomicReference<CounterPartition>(new CounterPartition(Time.time(), this.partitionSize));
    }

    public void setWindow(long time, TimeUnit unit) {
        this.windowSize = unit.toNanos(time);
        this.partitionSize = this.windowSize / 10L;
    }

    @Override
    public Double value() {
        return this.rateUsingSeconds();
    }

    public Double rateUsingSeconds() {
        CounterPartition partition;
        long count;
        long endTime = Time.time();
        long startTime = endTime - this.windowSize;
        CounterPartition current = this.activePartition.get();
        long actualStartTime = startTime;
        if (current.isBefore(startTime)) {
            count = 0L;
        } else {
            count = current.sum();
            actualStartTime = Math.min(actualStartTime, current.start());
        }
        Iterator it = this.archive.iterator();
        while (it.hasNext() && (partition = (CounterPartition)it.next()) != current) {
            if (partition.isBefore(startTime)) {
                it.remove();
                continue;
            }
            actualStartTime = Math.min(actualStartTime, partition.start());
            count += partition.sum();
        }
        if (count == 0L) {
            return 0.0;
        }
        return (double)(TimeUnit.SECONDS.toNanos(1L) * count) / (double)(endTime - actualStartTime);
    }

    public Double rate(TimeUnit base) {
        return this.rateUsingSeconds() * ((double)base.toNanos(1L) / (double)TimeUnit.SECONDS.toNanos(1L));
    }

    @Override
    public void event(long time, long ... parameters) {
        CounterPartition newPartition;
        CounterPartition partition;
        do {
            if (!(partition = this.activePartition.get()).targetFor(time)) continue;
            partition.increment();
            return;
        } while (!this.activePartition.compareAndSet(partition, newPartition = new CounterPartition(time, this.partitionSize)));
        this.archive(partition);
        newPartition.increment();
    }

    private void archive(CounterPartition partition) {
        this.archive.add(partition);
        long startTime = partition.end() - this.windowSize;
        CounterPartition earliest = this.archive.peek();
        while (earliest != null && earliest.isBefore(startTime) && !this.archive.remove(earliest)) {
            earliest = this.archive.peek();
        }
    }

    static class CounterPartition
    extends LongAdder {
        private final long start;
        private final long end;

        CounterPartition(long start, long length) {
            this.start = start;
            this.end = start + length;
        }

        public boolean targetFor(long time) {
            return this.end > time;
        }

        public boolean isBefore(long time) {
            return this.end < time;
        }

        public long start() {
            return this.start;
        }

        public long end() {
            return this.end;
        }
    }
}

