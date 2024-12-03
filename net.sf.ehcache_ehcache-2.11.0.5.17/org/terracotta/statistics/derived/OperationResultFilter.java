/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics.derived;

import java.util.EnumSet;
import java.util.Set;
import org.terracotta.statistics.AbstractSourceStatistic;
import org.terracotta.statistics.observer.ChainedEventObserver;
import org.terracotta.statistics.observer.ChainedOperationObserver;

public class OperationResultFilter<T extends Enum<T>>
extends AbstractSourceStatistic<ChainedEventObserver>
implements ChainedOperationObserver<T> {
    private final Set<T> targets;

    public OperationResultFilter(Set<T> targets, ChainedEventObserver ... observers) {
        this.targets = EnumSet.copyOf(targets);
        for (ChainedEventObserver observer : observers) {
            this.addDerivedStatistic(observer);
        }
    }

    @Override
    public void begin(long time) {
    }

    @Override
    public void end(long time, T result) {
        if (!this.derivedStatistics.isEmpty() && this.targets.contains(result)) {
            for (ChainedEventObserver derived : this.derivedStatistics) {
                derived.event(time, new long[0]);
            }
        }
    }

    @Override
    public void end(long time, T result, long ... parameters) {
        if (!this.derivedStatistics.isEmpty() && this.targets.contains(result)) {
            for (ChainedEventObserver derived : this.derivedStatistics) {
                derived.event(time, parameters);
            }
        }
    }
}

