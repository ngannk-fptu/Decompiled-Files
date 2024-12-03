/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.filter;

import com.atlassian.core.util.filter.Filter;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class FilterChain<T>
implements Filter<T> {
    private final List<Filter<T>> filters = new ArrayList<Filter<T>>();

    public void addFilter(Filter<T> filter) {
        this.filters.add(filter);
    }

    @Override
    public boolean isIncluded(T o) {
        for (Filter<T> filter : this.filters) {
            if (filter.isIncluded(o)) continue;
            return false;
        }
        return true;
    }
}

