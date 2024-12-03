/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 */
package com.atlassian.crowd.search.util;

import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class OrderedResultsConstrainer<T> {
    private final List<T> results = new ArrayList<T>();
    private final Predicate<T> filter;
    private final int startIndex;
    private final int endIndex;
    private int currentIndex;

    public OrderedResultsConstrainer(Predicate<T> filter, int startIndex, int maxResults) {
        this.filter = filter;
        this.startIndex = startIndex;
        this.endIndex = EntityQuery.addToMaxResults((int)maxResults, (int)startIndex);
        this.currentIndex = 0;
    }

    public void addAll(Iterable<T> collection) {
        Iterator<T> it = collection.iterator();
        while (this.getRemainingCount() != 0 && it.hasNext()) {
            T next = it.next();
            if (this.filter != null && !this.filter.test(next) || this.currentIndex++ < this.startIndex) continue;
            this.results.add(next);
        }
    }

    public List<T> toList() {
        return this.results;
    }

    public int getRemainingCount() {
        return this.isAllResultsQuery() ? -1 : this.endIndex - this.currentIndex;
    }

    private boolean isAllResultsQuery() {
        return this.endIndex == -1;
    }
}

