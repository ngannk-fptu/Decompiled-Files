/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.concurrent.ScheduledExecutorService;
import net.sf.ehcache.statistics.extended.SemiExpiringStatistic;
import org.terracotta.statistics.ValueStatistic;

class ExpiringStatistic<T extends Number>
extends SemiExpiringStatistic<T> {
    public ExpiringStatistic(ValueStatistic<T> source, ScheduledExecutorService executor, int historySize, long historyNanos) {
        super(source, executor, historySize, historyNanos);
    }

    @Override
    public T value() {
        this.touch();
        return (T)super.value();
    }
}

