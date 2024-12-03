/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.commons.lang.IteratorAdapter;
import com.querydsl.core.Fetchable;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.support.QueryBase;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.SubQueryExpression;
import java.util.List;
import javax.annotation.Nullable;

public abstract class FetchableQueryBase<T, Q extends FetchableQueryBase<T, Q>>
extends QueryBase<Q>
implements Fetchable<T> {
    public FetchableQueryBase(QueryMixin<Q> queryMixin) {
        super(queryMixin);
    }

    @Override
    public List<T> fetch() {
        return IteratorAdapter.asList(this.iterate());
    }

    @Override
    public final T fetchFirst() {
        return ((FetchableQueryBase)this.limit(1L)).fetchOne();
    }

    public <T> T transform(ResultTransformer<T> transformer) {
        return transformer.transform((FetchableQuery)((Object)this));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected <T> T uniqueResult(CloseableIterator<T> it) {
        try {
            if (it.hasNext()) {
                Object rv = it.next();
                if (it.hasNext()) {
                    throw new NonUniqueResultException();
                }
                Object e = rv;
                return (T)e;
            }
            T t = null;
            return t;
        }
        finally {
            it.close();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SubQueryExpression) {
            SubQueryExpression s = (SubQueryExpression)o;
            return s.getMetadata().equals(this.queryMixin.getMetadata());
        }
        return false;
    }
}

