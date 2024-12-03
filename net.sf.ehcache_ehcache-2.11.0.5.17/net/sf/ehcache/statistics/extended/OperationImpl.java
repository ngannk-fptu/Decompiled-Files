/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.LatencyImpl;
import net.sf.ehcache.statistics.extended.RateImpl;
import net.sf.ehcache.statistics.extended.SemiExpiringStatistic;
import org.terracotta.statistics.OperationStatistic;

class OperationImpl<T extends Enum<T>>
implements ExtendedStatistics.Result {
    private final OperationStatistic<T> source;
    private final SemiExpiringStatistic<Long> count;
    private final RateImpl rate;
    private final LatencyImpl latency;

    public OperationImpl(OperationStatistic<T> source, Set<T> targets, long averageNanos, ScheduledExecutorService executor, int historySize, long historyNanos) {
        this.source = source;
        this.count = new SemiExpiringStatistic<Long>(source.statistic(targets), executor, historySize, historyNanos);
        this.latency = new LatencyImpl<T>(source, targets, averageNanos, executor, historySize, historyNanos);
        this.rate = new RateImpl<T>(source, targets, averageNanos, executor, historySize, historyNanos);
    }

    @Override
    public ExtendedStatistics.Statistic<Double> rate() {
        return this.rate;
    }

    @Override
    public ExtendedStatistics.Latency latency() throws UnsupportedOperationException {
        return this.latency;
    }

    @Override
    public ExtendedStatistics.Statistic<Long> count() {
        return this.count;
    }

    void start() {
        this.count.start();
        this.rate.start();
        this.latency.start();
    }

    boolean expire(long expiryTime) {
        return this.count.expire(expiryTime) & this.rate.expire(expiryTime) & this.latency.expire(expiryTime);
    }

    void setWindow(long averageNanos) {
        this.rate.setWindow(averageNanos);
        this.latency.setWindow(averageNanos);
    }

    void setHistory(int historySize, long historyNanos) {
        this.count.setHistory(historySize, historyNanos);
        this.rate.setHistory(historySize, historyNanos);
        this.latency.setHistory(historySize, historyNanos);
    }
}

