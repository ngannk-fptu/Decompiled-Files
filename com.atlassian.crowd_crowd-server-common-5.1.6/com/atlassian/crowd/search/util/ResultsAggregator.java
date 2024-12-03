/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.util;

import java.util.List;
import java.util.function.Predicate;

public interface ResultsAggregator<T> {
    public void add(T var1);

    public void addAll(Iterable<? extends T> var1);

    public int size();

    public List<T> constrainResults();

    public List<T> constrainResults(Predicate<? super T> var1);

    public int getRequiredResultCount();
}

