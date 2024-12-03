/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.statistics.extended.AbstractStatistic;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import org.terracotta.statistics.SourceStatistic;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.Timestamped;
import org.terracotta.statistics.derived.EventParameterSimpleMovingAverage;
import org.terracotta.statistics.derived.LatencySampling;
import org.terracotta.statistics.observer.ChainedOperationObserver;

class LatencyImpl<T extends Enum<T>>
implements ExtendedStatistics.Latency {
    private final SourceStatistic<ChainedOperationObserver<T>> source;
    private final LatencySampling<T> latencySampler;
    private final EventParameterSimpleMovingAverage average;
    private final StatisticImpl<Long> minimumStatistic;
    private final StatisticImpl<Long> maximumStatistic;
    private final StatisticImpl<Double> averageStatistic;
    private boolean active = false;
    private long touchTimestamp = -1L;

    public LatencyImpl(SourceStatistic<ChainedOperationObserver<T>> statistic, Set<T> targets, long averageNanos, ScheduledExecutorService executor, int historySize, long historyNanos) {
        this.average = new EventParameterSimpleMovingAverage(averageNanos, TimeUnit.NANOSECONDS);
        this.minimumStatistic = new StatisticImpl<Long>(this.average.minimumStatistic(), executor, historySize, historyNanos);
        this.maximumStatistic = new StatisticImpl<Long>(this.average.maximumStatistic(), executor, historySize, historyNanos);
        this.averageStatistic = new StatisticImpl<Double>(this.average.averageStatistic(), executor, historySize, historyNanos);
        this.latencySampler = new LatencySampling<T>(targets, 1.0);
        this.latencySampler.addDerivedStatistic(this.average);
        this.source = statistic;
    }

    synchronized void start() {
        if (!this.active) {
            this.source.addDerivedStatistic(this.latencySampler);
            this.minimumStatistic.startSampling();
            this.maximumStatistic.startSampling();
            this.averageStatistic.startSampling();
            this.active = true;
        }
    }

    @Override
    public ExtendedStatistics.Statistic<Long> minimum() {
        return this.minimumStatistic;
    }

    @Override
    public ExtendedStatistics.Statistic<Long> maximum() {
        return this.maximumStatistic;
    }

    @Override
    public ExtendedStatistics.Statistic<Double> average() {
        return this.averageStatistic;
    }

    private synchronized void touch() {
        this.touchTimestamp = Time.absoluteTime();
        this.start();
    }

    public synchronized boolean expire(long expiry) {
        if (this.touchTimestamp < expiry) {
            if (this.active) {
                this.source.removeDerivedStatistic(this.latencySampler);
                this.minimumStatistic.stopSampling();
                this.maximumStatistic.stopSampling();
                this.averageStatistic.stopSampling();
                this.active = false;
            }
            return true;
        }
        return false;
    }

    void setWindow(long averageNanos) {
        this.average.setWindow(averageNanos, TimeUnit.NANOSECONDS);
    }

    void setHistory(int historySize, long historyNanos) {
        this.minimumStatistic.setHistory(historySize, historyNanos);
        this.maximumStatistic.setHistory(historySize, historyNanos);
        this.averageStatistic.setHistory(historySize, historyNanos);
    }

    class StatisticImpl<T extends Number>
    extends AbstractStatistic<T> {
        public StatisticImpl(ValueStatistic<T> value, ScheduledExecutorService executor, int historySize, long historyNanos) {
            super(value, executor, historySize, historyNanos);
        }

        @Override
        public boolean active() {
            return LatencyImpl.this.active;
        }

        @Override
        public T value() {
            LatencyImpl.this.touch();
            return super.value();
        }

        @Override
        public List<Timestamped<T>> history() throws UnsupportedOperationException {
            LatencyImpl.this.touch();
            return super.history();
        }
    }
}

