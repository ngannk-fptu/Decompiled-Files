/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Map;
import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.store.StoreQuery;

public class ResultImpl
extends BaseResult {
    private final Object key;
    private final Object value;
    private final Map<String, Object> attributes;
    private final Object[] sortAttributes;

    public ResultImpl(Object key, Object value, StoreQuery query, Map<String, Object> attributes, Object[] sortAttributes) {
        super(query);
        this.key = key;
        this.value = value;
        this.attributes = attributes;
        this.sortAttributes = sortAttributes;
    }

    @Override
    Object getSortAttribute(int pos) {
        return this.sortAttributes[pos];
    }

    @Override
    protected Object basicGetKey() {
        return this.key;
    }

    @Override
    protected Object basicGetValue() {
        return this.value;
    }

    @Override
    protected Object basicGetAttribute(String name) {
        return this.attributes.get(name);
    }
}

