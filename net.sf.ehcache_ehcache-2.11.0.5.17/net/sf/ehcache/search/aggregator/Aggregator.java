/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.aggregator;

import net.sf.ehcache.search.aggregator.AggregatorInstance;

public interface Aggregator {
    public <T> AggregatorInstance<T> createInstance();
}

