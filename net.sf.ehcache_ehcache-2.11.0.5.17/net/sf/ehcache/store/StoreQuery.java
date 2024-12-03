/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.List;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.ExecutionHints;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.expression.Criteria;

public interface StoreQuery {
    public Criteria getCriteria();

    public boolean requestsKeys();

    public boolean requestsValues();

    public Cache getCache();

    @Deprecated
    public String[] getTargets();

    @Deprecated
    public void targets(String[] var1);

    public Set<Attribute<?>> requestedAttributes();

    public Set<Attribute<?>> groupByAttributes();

    public List<Ordering> getOrdering();

    public int maxResults();

    public List<Aggregator> getAggregators();

    public ExecutionHints getExecutionHints();

    public List<AggregatorInstance<?>> getAggregatorInstances();

    public static interface Ordering {
        public Attribute<?> getAttribute();

        public Direction getDirection();
    }
}

