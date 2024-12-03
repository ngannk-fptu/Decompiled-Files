/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.filter;

import com.hazelcast.internal.util.filter.Filter;

public final class AlwaysApplyFilter<T>
implements Filter<T> {
    private AlwaysApplyFilter() {
    }

    public static <T> AlwaysApplyFilter<T> newInstance() {
        return new AlwaysApplyFilter<T>();
    }

    @Override
    public boolean accept(T object) {
        return true;
    }
}

