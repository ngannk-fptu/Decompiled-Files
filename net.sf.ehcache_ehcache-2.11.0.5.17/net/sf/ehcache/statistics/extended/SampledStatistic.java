/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.StatisticArchive;
import org.terracotta.statistics.archive.StatisticSampler;
import org.terracotta.statistics.archive.Timestamped;

class SampledStatistic<T extends Number> {
    private final StatisticSampler<T> sampler;
    private final StatisticArchive<T> history;

    public SampledStatistic(ValueStatistic<T> statistic, ScheduledExecutorService executor, int historySize, long periodNanos) {
        this.history = new StatisticArchive(historySize);
        this.sampler = new StatisticSampler<T>(executor, periodNanos, TimeUnit.NANOSECONDS, statistic, this.history);
    }

    public void startSampling() {
        this.sampler.start();
    }

    public void stopSampling() {
        this.sampler.stop();
        this.history.clear();
    }

    public List<Timestamped<T>> history() {
        return this.history.getArchive();
    }

    void adjust(int historySize, long historyNanos) {
        this.history.setCapacity(historySize);
        this.sampler.setPeriod(historyNanos, TimeUnit.NANOSECONDS);
    }
}

