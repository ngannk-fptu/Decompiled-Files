/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.statistics.extended.ExpiringStatistic;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.OperationImpl;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.ValueStatistic;

class CompoundOperationImpl<T extends Enum<T>>
implements ExtendedStatistics.Operation<T> {
    private final OperationStatistic<T> source;
    private final Class<T> type;
    private final Map<T, OperationImpl<T>> operations;
    private final ConcurrentMap<Set<T>, OperationImpl<T>> compounds = new ConcurrentHashMap<Set<T>, OperationImpl<T>>();
    private final ConcurrentMap<List<Set<T>>, ExpiringStatistic<Double>> ratios = new ConcurrentHashMap<List<Set<T>>, ExpiringStatistic<Double>>();
    private final ScheduledExecutorService executor;
    private volatile long averageNanos;
    private volatile int historySize;
    private volatile long historyNanos;
    private volatile boolean alwaysOn = false;

    public CompoundOperationImpl(OperationStatistic<T> source, Class<T> type, long averagePeriod, TimeUnit averageUnit, ScheduledExecutorService executor, int historySize, long historyPeriod, TimeUnit historyUnit) {
        this.type = type;
        this.source = source;
        this.averageNanos = averageUnit.toNanos(averagePeriod);
        this.executor = executor;
        this.historySize = historySize;
        this.historyNanos = historyUnit.toNanos(historyPeriod);
        this.operations = new EnumMap<T, OperationImpl<T>>(type);
        for (Enum result : (Enum[])type.getEnumConstants()) {
            this.operations.put(result, new OperationImpl<Enum>(source, EnumSet.of(result), this.averageNanos, executor, historySize, this.historyNanos));
        }
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public ExtendedStatistics.Result component(T result) {
        return this.operations.get(result);
    }

    @Override
    public ExtendedStatistics.Result compound(Set<T> results) {
        if (results.size() == 1) {
            return this.component((Enum)results.iterator().next());
        }
        EnumSet<T> key = EnumSet.copyOf(results);
        OperationImpl existing = (OperationImpl)this.compounds.get(key);
        if (existing == null) {
            OperationImpl<T> created = new OperationImpl<T>(this.source, key, this.averageNanos, this.executor, this.historySize, this.historyNanos);
            OperationImpl<T> racer = this.compounds.putIfAbsent(key, created);
            if (racer == null) {
                return created;
            }
            return racer;
        }
        return existing;
    }

    @Override
    public ExtendedStatistics.Statistic<Double> ratioOf(Set<T> numerator, Set<T> denominator) {
        List<Set> key = Arrays.asList(EnumSet.copyOf(numerator), EnumSet.copyOf(denominator));
        ExpiringStatistic existing = (ExpiringStatistic)this.ratios.get(key);
        if (existing == null) {
            ExtendedStatistics.Statistic<Double> denominatorRate;
            final ExtendedStatistics.Statistic<Double> numeratorRate = this.compound(numerator).rate();
            ExpiringStatistic<Double> created = new ExpiringStatistic<Double>(new ValueStatistic<Double>(denominatorRate = this.compound(denominator).rate()){
                final /* synthetic */ ExtendedStatistics.Statistic val$denominatorRate;
                {
                    this.val$denominatorRate = statistic2;
                }

                @Override
                public Double value() {
                    return (Double)numeratorRate.value() / (Double)this.val$denominatorRate.value();
                }
            }, this.executor, this.historySize, this.historyNanos);
            ExpiringStatistic<Double> racer = this.ratios.putIfAbsent(key, created);
            if (racer == null) {
                return created;
            }
            return racer;
        }
        return existing;
    }

    @Override
    public void setAlwaysOn(boolean enable) {
        this.alwaysOn = enable;
        if (enable) {
            for (OperationImpl<T> operationImpl : this.operations.values()) {
                operationImpl.start();
            }
            for (OperationImpl<Object> operationImpl : this.compounds.values()) {
                operationImpl.start();
            }
            for (ExpiringStatistic expiringStatistic : this.ratios.values()) {
                expiringStatistic.start();
            }
        }
    }

    @Override
    public boolean isAlwaysOn() {
        return this.alwaysOn;
    }

    @Override
    public void setWindow(long time, TimeUnit unit) {
        this.averageNanos = unit.toNanos(time);
        for (OperationImpl<T> operationImpl : this.operations.values()) {
            operationImpl.setWindow(this.averageNanos);
        }
        for (OperationImpl<Object> operationImpl : this.compounds.values()) {
            operationImpl.setWindow(this.averageNanos);
        }
    }

    @Override
    public void setHistory(int samples, long time, TimeUnit unit) {
        this.historySize = samples;
        this.historyNanos = unit.toNanos(time);
        for (OperationImpl<T> operationImpl : this.operations.values()) {
            operationImpl.setHistory(this.historySize, this.historyNanos);
        }
        for (OperationImpl<Object> operationImpl : this.compounds.values()) {
            operationImpl.setHistory(this.historySize, this.historyNanos);
        }
        for (ExpiringStatistic expiringStatistic : this.ratios.values()) {
            expiringStatistic.setHistory(this.historySize, this.historyNanos);
        }
    }

    @Override
    public long getWindowSize(TimeUnit unit) {
        return unit.convert(this.averageNanos, TimeUnit.NANOSECONDS);
    }

    @Override
    public int getHistorySampleSize() {
        return this.historySize;
    }

    @Override
    public long getHistorySampleTime(TimeUnit unit) {
        return unit.convert(this.historySize, TimeUnit.NANOSECONDS);
    }

    boolean expire(long expiryTime) {
        if (this.alwaysOn) {
            return false;
        }
        boolean expired = true;
        for (OperationImpl<T> o : this.operations.values()) {
            expired &= o.expire(expiryTime);
        }
        Iterator it = this.compounds.values().iterator();
        while (it.hasNext()) {
            if (!((OperationImpl)it.next()).expire(expiryTime)) continue;
            it.remove();
        }
        it = this.ratios.values().iterator();
        while (it.hasNext()) {
            if (!((ExpiringStatistic)it.next()).expire(expiryTime)) continue;
            it.remove();
        }
        return expired & this.compounds.isEmpty() & this.ratios.isEmpty();
    }
}

