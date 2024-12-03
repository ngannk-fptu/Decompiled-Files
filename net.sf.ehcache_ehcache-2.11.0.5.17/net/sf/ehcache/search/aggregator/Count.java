/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;

public class Count
implements AggregatorInstance<Integer> {
    private int count;

    public Count createClone() {
        return new Count();
    }

    @Override
    public void accept(Object input) throws AggregatorException {
        ++this.count;
    }

    @Override
    public Integer aggregateResult() {
        return this.count;
    }

    @Override
    public Attribute<?> getAttribute() {
        return null;
    }
}

