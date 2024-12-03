/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.filter;

import com.hazelcast.internal.util.filter.Filter;

public final class AndFilter<T>
implements Filter<T> {
    private final Filter<T> filter1;
    private final Filter<T> filter2;

    public AndFilter(Filter<T> filter1, Filter<T> filter2) {
        this.filter1 = filter1;
        this.filter2 = filter2;
    }

    @Override
    public boolean accept(T object) {
        return this.filter1.accept(object) && this.filter2.accept(object);
    }
}

