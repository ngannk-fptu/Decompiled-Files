/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.terracotta.statistics.SourceStatistic;
import org.terracotta.statistics.observer.ChainedObserver;

public class AbstractSourceStatistic<T extends ChainedObserver>
implements SourceStatistic<T> {
    protected final Collection<T> derivedStatistics = new CopyOnWriteArrayList<T>();

    @Override
    public void addDerivedStatistic(T derived) {
        this.derivedStatistics.add(derived);
    }

    @Override
    public void removeDerivedStatistic(T derived) {
        this.derivedStatistics.remove(derived);
    }
}

