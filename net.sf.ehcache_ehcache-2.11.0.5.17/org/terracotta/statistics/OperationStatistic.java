/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.Set;
import org.terracotta.statistics.SourceStatistic;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.observer.ChainedOperationObserver;
import org.terracotta.statistics.observer.OperationObserver;

public interface OperationStatistic<T extends Enum<T>>
extends OperationObserver<T>,
SourceStatistic<ChainedOperationObserver<? super T>> {
    public Class<T> type();

    public ValueStatistic<Long> statistic(T var1);

    public ValueStatistic<Long> statistic(Set<T> var1);

    public long count(T var1);

    public long sum(Set<T> var1);

    public long sum();
}

