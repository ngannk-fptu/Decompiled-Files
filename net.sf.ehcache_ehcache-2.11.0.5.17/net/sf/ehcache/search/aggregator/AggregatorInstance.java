/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;

public interface AggregatorInstance<T> {
    public void accept(Object var1) throws AggregatorException;

    public Object aggregateResult();

    public Attribute<?> getAttribute();

    public AggregatorInstance<T> createClone();
}

