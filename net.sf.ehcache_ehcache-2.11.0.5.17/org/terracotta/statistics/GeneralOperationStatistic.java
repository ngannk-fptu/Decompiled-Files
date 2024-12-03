/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.terracotta.statistics.AbstractOperationStatistic;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.jsr166e.LongAdder;
import org.terracotta.statistics.observer.ChainedOperationObserver;

class GeneralOperationStatistic<T extends Enum<T>>
extends AbstractOperationStatistic<T>
implements OperationStatistic<T> {
    private final EnumMap<T, LongAdder> counts;

    GeneralOperationStatistic(String name, Set<String> tags, Map<String, ? extends Object> properties, Class<T> type) {
        super(name, tags, properties, type);
        this.counts = new EnumMap(type);
        for (Enum t : (Enum[])type.getEnumConstants()) {
            this.counts.put(t, new LongAdder());
        }
    }

    @Override
    public long count(T type) {
        return this.counts.get(type).sum();
    }

    @Override
    public long sum(Set<T> types) {
        long sum = 0L;
        for (Enum t : types) {
            sum += this.counts.get(t).sum();
        }
        return sum;
    }

    @Override
    public void end(T result) {
        this.counts.get(result).increment();
        if (!this.derivedStatistics.isEmpty()) {
            long time = Time.time();
            for (ChainedOperationObserver observer : this.derivedStatistics) {
                observer.end(time, result);
            }
        }
    }

    @Override
    public void end(T result, long ... parameters) {
        this.counts.get(result).increment();
        if (!this.derivedStatistics.isEmpty()) {
            long time = Time.time();
            for (ChainedOperationObserver observer : this.derivedStatistics) {
                observer.end(time, result, parameters);
            }
        }
    }

    public String toString() {
        return this.counts.toString();
    }
}

