/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import org.terracotta.statistics.archive.Timestamped;

final class NullStatistic<T extends Number>
implements ExtendedStatistics.Statistic<T> {
    private static final Map<Object, ExtendedStatistics.Statistic<?>> COMMON = new HashMap();
    private final T value;

    private NullStatistic(T value) {
        this.value = value;
    }

    @Override
    public boolean active() {
        return false;
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public List<Timestamped<T>> history() throws UnsupportedOperationException {
        return Collections.emptyList();
    }

    static <T extends Number> ExtendedStatistics.Statistic<T> instance(T value) {
        ExtendedStatistics.Statistic<?> cached = COMMON.get(value);
        if (cached == null) {
            return new NullStatistic<T>(value);
        }
        return cached;
    }

    static {
        COMMON.put(Double.NaN, new NullStatistic<Double>(Double.NaN));
        COMMON.put(Float.valueOf(Float.NaN), new NullStatistic<Float>(Float.valueOf(Float.NaN)));
        COMMON.put(0L, new NullStatistic<Long>(0L));
        COMMON.put(null, new NullStatistic<Object>(null));
    }
}

