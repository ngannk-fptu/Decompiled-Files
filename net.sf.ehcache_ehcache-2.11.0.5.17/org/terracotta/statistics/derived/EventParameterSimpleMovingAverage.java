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
import org.terracotta.statistics.jsr166e.LongMaxUpdater;
import org.terracotta.statistics.observer.ChainedEventObserver;

public class EventParameterSimpleMovingAverage
implements ChainedEventObserver {
    private static final int PARTITION_COUNT = 10;
    private final Queue<AveragePartition> archive = new ConcurrentLinkedQueue<AveragePartition>();
    private final AtomicReference<AveragePartition> activePartition;
    private volatile long windowSize;
    private volatile long partitionSize;

    public EventParameterSimpleMovingAverage(long time, TimeUnit unit) {
        this.windowSize = unit.toNanos(time);
        this.partitionSize = this.windowSize / 10L;
        this.activePartition = new AtomicReference<AveragePartition>(new AveragePartition(Long.MIN_VALUE, this.partitionSize));
    }

    public void setWindow(long time, TimeUnit unit) {
        this.windowSize = unit.toNanos(time);
        this.partitionSize = this.windowSize / 10L;
    }

    public Double value() {
        return this.average();
    }

    public ValueStatistic<Double> averageStatistic() {
        return new ValueStatistic<Double>(){

            @Override
            public Double value() {
                return EventParameterSimpleMovingAverage.this.average();
            }
        };
    }

    public ValueStatistic<Long> minimumStatistic() {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return EventParameterSimpleMovingAverage.this.minimum();
            }
        };
    }

    public ValueStatistic<Long> maximumStatistic() {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return EventParameterSimpleMovingAverage.this.maximum();
            }
        };
    }

    public final double average() {
        AveragePartition partition;
        long startTime = Time.time() - this.windowSize;
        AveragePartition current = this.activePartition.get();
        if (current.isBefore(startTime)) {
            return Double.NaN;
        }
        Average average = new Average();
        current.aggregate(average);
        Iterator it = this.archive.iterator();
        while (it.hasNext() && (partition = (AveragePartition)it.next()) != current) {
            if (partition.isBefore(startTime)) {
                it.remove();
                continue;
            }
            partition.aggregate(average);
        }
        return (double)average.total / (double)average.count;
    }

    public final Long maximum() {
        AveragePartition partition;
        long startTime = Time.time() - this.windowSize;
        AveragePartition current = this.activePartition.get();
        if (current.isBefore(startTime)) {
            return null;
        }
        long maximum = current.maximum();
        Iterator it = this.archive.iterator();
        while (it.hasNext() && (partition = (AveragePartition)it.next()) != current) {
            if (partition.isBefore(startTime)) {
                it.remove();
                continue;
            }
            maximum = Math.max(maximum, partition.maximum());
        }
        return maximum;
    }

    public final Long minimum() {
        AveragePartition partition;
        long startTime = Time.time() - this.windowSize;
        AveragePartition current = this.activePartition.get();
        if (current.isBefore(startTime)) {
            return null;
        }
        long minimum = current.minimum();
        Iterator it = this.archive.iterator();
        while (it.hasNext() && (partition = (AveragePartition)it.next()) != current) {
            if (partition.isBefore(startTime)) {
                it.remove();
                continue;
            }
            minimum = Math.min(minimum, partition.minimum());
        }
        return minimum;
    }

    @Override
    public void event(long time, long ... parameters) {
        AveragePartition newPartition;
        AveragePartition partition;
        do {
            if (!(partition = this.activePartition.get()).targetFor(time)) continue;
            partition.event(parameters[0]);
            return;
        } while (!this.activePartition.compareAndSet(partition, newPartition = new AveragePartition(time, this.partitionSize)));
        this.archive(partition);
        newPartition.event(parameters[0]);
    }

    private void archive(AveragePartition partition) {
        this.archive.add(partition);
        long startTime = partition.end() - this.windowSize;
        AveragePartition earliest = this.archive.peek();
        while (earliest != null && earliest.isBefore(startTime) && !this.archive.remove(earliest)) {
            earliest = this.archive.peek();
        }
    }

    static class Average {
        long total;
        long count;

        Average() {
        }
    }

    static class AveragePartition {
        private final LongAdder total = new LongAdder();
        private final LongAdder count = new LongAdder();
        private final LongMaxUpdater maximum = new LongMaxUpdater();
        private final LongMaxUpdater minimum = new LongMaxUpdater();
        private final long start;
        private final long end;

        public AveragePartition(long start, long length) {
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

        public void event(long parameter) {
            this.total.add(parameter);
            this.count.increment();
            this.maximum.update(parameter);
            this.minimum.update(-parameter);
        }

        public void aggregate(Average average) {
            average.total += this.total.sum();
            average.count += this.count.sum();
        }

        public long maximum() {
            return this.maximum.max();
        }

        public long minimum() {
            return -this.minimum.max();
        }
    }
}

