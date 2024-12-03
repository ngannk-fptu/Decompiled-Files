/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.SampledStatistic;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.Timestamped;

abstract class AbstractStatistic<T extends Number>
implements ExtendedStatistics.Statistic<T> {
    private final ValueStatistic<T> source;
    private final SampledStatistic<T> history;

    AbstractStatistic(ValueStatistic<T> source, ScheduledExecutorService executor, int historySize, long historyNanos) {
        this.source = source;
        this.history = new SampledStatistic<T>(source, executor, historySize, historyNanos);
    }

    @Override
    public T value() {
        return this.source.value();
    }

    @Override
    public List<Timestamped<T>> history() {
        return this.history.history();
    }

    final void startSampling() {
        this.history.startSampling();
    }

    final void stopSampling() {
        this.history.stopSampling();
    }

    final void setHistory(int historySize, long historyNanos) {
        this.history.adjust(historySize, historyNanos);
    }
}

