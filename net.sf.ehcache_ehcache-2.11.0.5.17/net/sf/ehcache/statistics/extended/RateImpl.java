/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.statistics.extended.ExpiringStatistic;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import org.terracotta.statistics.SourceStatistic;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.archive.Timestamped;
import org.terracotta.statistics.derived.EventRateSimpleMovingAverage;
import org.terracotta.statistics.derived.OperationResultFilter;
import org.terracotta.statistics.observer.ChainedOperationObserver;

public class RateImpl<T extends Enum<T>>
implements ExtendedStatistics.Statistic<Double> {
    private final ExpiringStatistic<Double> delegate;
    private final EventRateSimpleMovingAverage rate;

    public RateImpl(final SourceStatistic<ChainedOperationObserver<T>> source, final Set<T> targets, long averageNanos, ScheduledExecutorService executor, int historySize, long historyNanos) {
        this.rate = new EventRateSimpleMovingAverage(averageNanos, TimeUnit.NANOSECONDS);
        this.delegate = new ExpiringStatistic<Double>((ValueStatistic)this.rate, executor, historySize, historyNanos){
            private final ChainedOperationObserver<T> observer;
            {
                super(source2, executor, historySize, historyNanos);
                this.observer = new OperationResultFilter(targets, RateImpl.this.rate);
            }

            @Override
            protected void stopStatistic() {
                super.stopStatistic();
                source.removeDerivedStatistic(this.observer);
            }

            @Override
            protected void startStatistic() {
                super.startStatistic();
                source.addDerivedStatistic(this.observer);
            }
        };
    }

    @Override
    public boolean active() {
        return this.delegate.active();
    }

    @Override
    public Double value() {
        return this.delegate.value();
    }

    @Override
    public List<Timestamped<Double>> history() {
        return this.delegate.history();
    }

    protected void start() {
        this.delegate.start();
    }

    protected void setWindow(long averageNanos) {
        this.rate.setWindow(averageNanos, TimeUnit.NANOSECONDS);
    }

    protected void setHistory(int historySize, long historyNanos) {
        this.delegate.setHistory(historySize, historyNanos);
    }

    protected boolean expire(long expiry) {
        return this.delegate.expire(expiry);
    }
}

