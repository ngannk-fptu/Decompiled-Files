/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.NullOperation;
import net.sf.ehcache.statistics.extended.NullStatistic;

final class NullCompoundOperation<T extends Enum<T>>
implements ExtendedStatistics.Operation<T> {
    private static final ExtendedStatistics.Operation INSTANCE = new NullCompoundOperation();

    private NullCompoundOperation() {
    }

    static <T extends Enum<T>> ExtendedStatistics.Operation<T> instance(Class<T> klazz) {
        return INSTANCE;
    }

    @Override
    public Class<T> type() {
        return null;
    }

    @Override
    public ExtendedStatistics.Result component(T result) {
        return NullOperation.instance();
    }

    @Override
    public ExtendedStatistics.Result compound(Set<T> results) {
        return NullOperation.instance();
    }

    @Override
    public ExtendedStatistics.Statistic<Double> ratioOf(Set<T> numerator, Set<T> denomiator) {
        return NullStatistic.instance(Double.NaN);
    }

    @Override
    public void setAlwaysOn(boolean enable) {
    }

    @Override
    public void setWindow(long time, TimeUnit unit) {
    }

    @Override
    public void setHistory(int samples, long time, TimeUnit unit) {
    }

    @Override
    public boolean isAlwaysOn() {
        return false;
    }

    @Override
    public long getWindowSize(TimeUnit unit) {
        return 0L;
    }

    @Override
    public int getHistorySampleSize() {
        return 0;
    }

    @Override
    public long getHistorySampleTime(TimeUnit unit) {
        return 0L;
    }
}

