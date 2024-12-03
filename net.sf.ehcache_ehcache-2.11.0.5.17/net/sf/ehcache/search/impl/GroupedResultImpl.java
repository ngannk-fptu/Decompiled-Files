/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.search.impl.BaseResult;
import net.sf.ehcache.store.StoreQuery;

public class GroupedResultImpl
extends BaseResult {
    private final Map<String, Object> attributes;
    private final Object[] sortAttributes;
    private final Map<String, Object> groupByValues;

    public GroupedResultImpl(StoreQuery query, Map<String, Object> attributes, Object[] sortAttributes, List<Object> aggregatorResults, Map<String, Object> groupBy) {
        super(query);
        this.attributes = attributes;
        this.sortAttributes = sortAttributes;
        this.groupByValues = groupBy;
        this.setAggregateResults(aggregatorResults);
    }

    @Override
    protected Object basicGetKey() {
        throw new AssertionError((Object)"Not supported");
    }

    @Override
    protected Object basicGetValue() {
        throw new AssertionError((Object)"Not supported");
    }

    @Override
    protected Object basicGetAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    Object getSortAttribute(int pos) {
        return this.sortAttributes[pos];
    }

    public Map<String, Object> getGroupByValues() {
        return Collections.unmodifiableMap(this.groupByValues);
    }
}

