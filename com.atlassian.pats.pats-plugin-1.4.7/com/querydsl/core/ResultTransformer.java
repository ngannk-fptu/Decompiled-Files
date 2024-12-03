/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.FetchableQuery;

public interface ResultTransformer<T> {
    public T transform(FetchableQuery<?, ?> var1);
}

