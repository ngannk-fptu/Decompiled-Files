/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.internal;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.util.TimeUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultLongTaskTimer
extends AbstractMeter
implements LongTaskTimer {
    private final Deque<SampleImpl> activeTasks = new ConcurrentLinkedDeque<SampleImpl>();
    private final Clock clock;
    private final TimeUnit baseTimeUnit;
    private final DistributionStatisticConfig distributionStatisticConfig;
    private final boolean supportsAggregablePercentiles;

    @Deprecated
    public DefaultLongTaskTimer(Meter.Id id, Clock clock) {
        this(id, clock, TimeUnit.MILLISECONDS, DistributionStatisticConfig.DEFAULT, false);
    }

    public DefaultLongTaskTimer(Meter.Id id, Clock clock, TimeUnit baseTimeUnit, DistributionStatisticConfig distributionStatisticConfig, boolean supportsAggregablePercentiles) {
        super(id);
        this.clock = clock;
        this.baseTimeUnit = baseTimeUnit;
        this.distributionStatisticConfig = distributionStatisticConfig;
        this.supportsAggregablePercentiles = supportsAggregablePercentiles;
    }

    @Override
    public LongTaskTimer.Sample start() {
        SampleImpl sample = new SampleImpl();
        this.activeTasks.add(sample);
        return sample;
    }

    @Override
    public double duration(TimeUnit unit) {
        long now = this.clock.monotonicTime();
        long sum = 0L;
        for (SampleImpl task : this.activeTasks) {
            sum += now - task.startTime();
        }
        return TimeUtils.nanosToUnit(sum, unit);
    }

    @Override
    public double max(TimeUnit unit) {
        LongTaskTimer.Sample oldest = this.activeTasks.peek();
        return oldest == null ? 0.0 : oldest.duration(unit);
    }

    @Override
    public int activeTasks() {
        return this.activeTasks.size();
    }

    protected void forEachActive(Consumer<LongTaskTimer.Sample> sample) {
        this.activeTasks.forEach(sample);
    }

    @Override
    public TimeUnit baseTimeUnit() {
        return this.baseTimeUnit;
    }

    @Override
    public HistogramSnapshot takeSnapshot() {
        ArrayBlockingQueue percentilesRequested = new ArrayBlockingQueue(this.distributionStatisticConfig.getPercentiles() == null ? 1 : this.distributionStatisticConfig.getPercentiles().length);
        double[] percentilesRequestedArr = this.distributionStatisticConfig.getPercentiles();
        if (percentilesRequestedArr != null && percentilesRequestedArr.length > 0) {
            Arrays.stream(percentilesRequestedArr).sorted().boxed().forEach(percentilesRequested::add);
        }
        NavigableSet<Double> buckets = this.distributionStatisticConfig.getHistogramBuckets(this.supportsAggregablePercentiles);
        CountAtBucket[] countAtBucketsArr = new CountAtBucket[]{};
        List percentilesAboveInterpolatableLine = percentilesRequested.stream().filter(p -> p * (double)(this.activeTasks.size() + 1) > (double)this.activeTasks.size()).collect(Collectors.toList());
        percentilesRequested.removeAll(percentilesAboveInterpolatableLine);
        ArrayList<ValueAtPercentile> valueAtPercentiles = new ArrayList<ValueAtPercentile>(percentilesRequested.size());
        if (!percentilesRequested.isEmpty() || !buckets.isEmpty()) {
            Double percentile = (Double)percentilesRequested.poll();
            Double bucket = buckets.pollFirst();
            ArrayList<CountAtBucket> countAtBuckets = new ArrayList<CountAtBucket>(buckets.size());
            Double priorActiveTaskDuration = null;
            int count = 0;
            List youngestToOldestDurations = ((Stream)StreamSupport.stream(((Iterable)this.activeTasks::descendingIterator).spliterator(), false).sequential()).map(task -> task.duration(TimeUnit.NANOSECONDS)).collect(Collectors.toList());
            for (Double activeTaskDuration : youngestToOldestDurations) {
                double rank;
                while (bucket != null && activeTaskDuration > bucket) {
                    countAtBuckets.add(new CountAtBucket(bucket, (double)count));
                    bucket = buckets.pollFirst();
                }
                ++count;
                if (percentile != null && (double)count >= (rank = percentile * (double)(this.activeTasks.size() + 1))) {
                    double percentileValue = activeTaskDuration;
                    if ((double)count != rank && priorActiveTaskDuration != null) {
                        double priorPercentileValue = priorActiveTaskDuration;
                        percentileValue = priorPercentileValue + (percentileValue - priorPercentileValue) * (rank - (double)((int)rank));
                    }
                    valueAtPercentiles.add(new ValueAtPercentile(percentile, percentileValue));
                    percentile = (Double)percentilesRequested.poll();
                }
                priorActiveTaskDuration = activeTaskDuration;
            }
            while (bucket != null) {
                countAtBuckets.add(new CountAtBucket(bucket, (double)count));
                bucket = buckets.pollFirst();
            }
            countAtBucketsArr = countAtBuckets.toArray(countAtBucketsArr);
        }
        double duration = this.duration(TimeUnit.NANOSECONDS);
        double max = this.max(TimeUnit.NANOSECONDS);
        for (Double percentile : percentilesAboveInterpolatableLine) {
            valueAtPercentiles.add(new ValueAtPercentile(percentile, max));
        }
        ValueAtPercentile[] valueAtPercentilesArr = valueAtPercentiles.toArray(new ValueAtPercentile[0]);
        return new HistogramSnapshot(this.activeTasks.size(), duration, max, valueAtPercentilesArr, countAtBucketsArr, (ps, scaling) -> ps.print("Summary output for LongTaskTimer histograms is not supported."));
    }

    class SampleImpl
    extends LongTaskTimer.Sample {
        private final long startTime;
        private volatile boolean stopped;

        private SampleImpl() {
            this.startTime = DefaultLongTaskTimer.this.clock.monotonicTime();
        }

        @Override
        public long stop() {
            DefaultLongTaskTimer.this.activeTasks.remove(this);
            long duration = (long)this.duration(TimeUnit.NANOSECONDS);
            this.stopped = true;
            return duration;
        }

        @Override
        public double duration(TimeUnit unit) {
            return this.stopped ? -1.0 : TimeUtils.nanosToUnit(DefaultLongTaskTimer.this.clock.monotonicTime() - this.startTime, unit);
        }

        private long startTime() {
            return this.startTime;
        }

        public String toString() {
            double durationInNanoseconds = this.duration(TimeUnit.NANOSECONDS);
            return "SampleImpl{duration(seconds)=" + TimeUtils.nanosToUnit(durationInNanoseconds, TimeUnit.SECONDS) + ", duration(nanos)=" + durationInNanoseconds + ", startTimeNanos=" + this.startTime + '}';
        }
    }
}

