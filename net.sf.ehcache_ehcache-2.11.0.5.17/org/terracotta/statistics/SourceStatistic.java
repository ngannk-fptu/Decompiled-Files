/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import org.terracotta.statistics.observer.ChainedObserver;

public interface SourceStatistic<T extends ChainedObserver> {
    public void addDerivedStatistic(T var1);

    public void removeDerivedStatistic(T var1);
}

