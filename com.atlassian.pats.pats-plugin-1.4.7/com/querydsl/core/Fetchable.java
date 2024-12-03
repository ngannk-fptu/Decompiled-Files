/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.QueryResults;
import java.util.List;

public interface Fetchable<T> {
    public List<T> fetch();

    public T fetchFirst();

    public T fetchOne();

    public CloseableIterator<T> iterate();

    public QueryResults<T> fetchResults();

    public long fetchCount();
}

