/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.store.StoreQuery;

public class AggregateOnlyResult
extends BaseResult {
    public AggregateOnlyResult(StoreQuery query) {
        super(query);
    }

    @Override
    protected Object basicGetKey() {
        throw new AssertionError();
    }

    @Override
    protected Object basicGetValue() {
        throw new AssertionError();
    }

    @Override
    protected Object basicGetAttribute(String name) {
        throw new AssertionError();
    }

    @Override
    Object getSortAttribute(int pos) {
        throw new AssertionError();
    }
}

