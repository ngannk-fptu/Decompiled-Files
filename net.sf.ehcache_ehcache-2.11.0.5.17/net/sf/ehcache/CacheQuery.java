/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.ExecutionHints;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.SearchException;
import net.sf.ehcache.search.aggregator.Aggregator;
import net.sf.ehcache.search.aggregator.AggregatorException;
import net.sf.ehcache.search.aggregator.AggregatorInstance;
import net.sf.ehcache.search.expression.AlwaysMatch;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.store.StoreQuery;

class CacheQuery
implements Query,
StoreQuery {
    private volatile boolean frozen;
    private volatile boolean includeKeys;
    private volatile boolean includeValues;
    private volatile int maxResults = -1;
    private String[] targets;
    private final List<StoreQuery.Ordering> orderings = Collections.synchronizedList(new ArrayList());
    private final Set<Attribute<?>> includedAttributes = Collections.synchronizedSet(new HashSet());
    private final List<Criteria> criteria = Collections.synchronizedList(new ArrayList());
    private final List<Aggregator> aggregators = Collections.synchronizedList(new ArrayList());
    private final Set<Attribute<?>> groupByAttributes = Collections.synchronizedSet(new HashSet());
    private final Cache cache;
    private volatile ExecutionHints hints;

    public CacheQuery(Cache cache) {
        this.cache = cache;
    }

    @Override
    public Query includeKeys() {
        this.checkFrozen();
        this.includeKeys = true;
        return this;
    }

    @Override
    public Query includeValues() {
        this.checkFrozen();
        this.includeValues = true;
        return this;
    }

    @Override
    public Query includeAttribute(Attribute<?> ... attributes) {
        this.checkFrozen();
        if (attributes == null) {
            throw new NullPointerException();
        }
        for (Attribute<?> attribute : attributes) {
            if (attribute == null) {
                throw new NullPointerException("null attribute");
            }
            this.includedAttributes.add(attribute);
        }
        return this;
    }

    @Override
    public Query includeAggregator(Aggregator ... aggregators) throws SearchException, AggregatorException {
        this.checkFrozen();
        if (aggregators == null) {
            throw new NullPointerException();
        }
        for (Aggregator aggregator : aggregators) {
            if (aggregator == null) {
                throw new NullPointerException("null aggregator");
            }
            this.aggregators.add(aggregator);
        }
        return this;
    }

    @Override
    public Query addOrderBy(Attribute<?> attribute, Direction direction) {
        this.checkFrozen();
        this.orderings.add(new OrderingImpl(attribute, direction));
        return this;
    }

    @Override
    public Query addGroupBy(Attribute<?> ... attributes) {
        this.checkFrozen();
        if (attributes == null) {
            throw new NullPointerException();
        }
        for (Attribute<?> attribute : attributes) {
            if (attribute == null) {
                throw new NullPointerException("null attribute");
            }
            this.groupByAttributes.add(attribute);
        }
        return this;
    }

    @Override
    public Query maxResults(int maxResults) {
        this.checkFrozen();
        this.maxResults = maxResults;
        return this;
    }

    @Override
    public Query addCriteria(Criteria criteria) {
        this.checkFrozen();
        if (criteria == null) {
            throw new NullPointerException("null criteria");
        }
        this.criteria.add(criteria);
        return this;
    }

    @Override
    public Results execute() throws SearchException {
        return this.cache.executeQuery(this.snapshot());
    }

    @Override
    public Results execute(ExecutionHints params) throws SearchException {
        this.hints = params;
        return this.cache.executeQuery(this.snapshot());
    }

    @Override
    public Query end() {
        this.frozen = true;
        return this;
    }

    @Override
    public List<StoreQuery.Ordering> getOrdering() {
        this.assertFrozen();
        return Collections.unmodifiableList(this.orderings);
    }

    @Override
    public Criteria getCriteria() {
        this.assertFrozen();
        return this.getEffectiveCriteriaCopy();
    }

    @Override
    public boolean requestsKeys() {
        this.assertFrozen();
        return this.includeKeys;
    }

    @Override
    public boolean requestsValues() {
        this.assertFrozen();
        return this.includeValues;
    }

    @Override
    public Cache getCache() {
        this.assertFrozen();
        return this.cache;
    }

    @Override
    public Set<Attribute<?>> requestedAttributes() {
        this.assertFrozen();
        return Collections.unmodifiableSet(this.includedAttributes);
    }

    @Override
    public Set<Attribute<?>> groupByAttributes() {
        this.assertFrozen();
        return Collections.unmodifiableSet(this.groupByAttributes);
    }

    @Override
    public int maxResults() {
        this.assertFrozen();
        return this.maxResults;
    }

    @Override
    public ExecutionHints getExecutionHints() {
        this.assertFrozen();
        return this.hints;
    }

    @Override
    public List<Aggregator> getAggregators() {
        return Collections.unmodifiableList(this.aggregators);
    }

    @Override
    public List<AggregatorInstance<?>> getAggregatorInstances() {
        this.assertFrozen();
        return Collections.unmodifiableList(CacheQuery.createAggregatorInstances(this.aggregators));
    }

    private static List<AggregatorInstance<?>> createAggregatorInstances(List<Aggregator> aggregators) {
        ArrayList rv = new ArrayList(aggregators.size());
        for (Aggregator aggregator : aggregators) {
            rv.add(aggregator.createInstance());
        }
        return rv;
    }

    private Criteria getEffectiveCriteriaCopy() {
        Criteria result = new AlwaysMatch();
        for (Criteria c : this.criteria) {
            result = result.and(c);
        }
        return result;
    }

    private void assertFrozen() {
        if (!this.frozen) {
            throw new AssertionError((Object)"not frozen");
        }
    }

    private StoreQuery snapshot() {
        if (this.frozen) {
            return this;
        }
        return new StoreQueryImpl();
    }

    private void checkFrozen() {
        if (this.frozen) {
            throw new SearchException("Query is frozen and cannot be mutated");
        }
    }

    @Override
    public String[] getTargets() {
        return this.targets;
    }

    @Override
    public void targets(String[] targets) {
        this.targets = targets;
    }

    private static class OrderingImpl
    implements StoreQuery.Ordering {
        private final Attribute<?> attribute;
        private final Direction direction;

        public OrderingImpl(Attribute<?> attribute, Direction direction) {
            if (attribute == null || direction == null) {
                throw new NullPointerException();
            }
            this.attribute = attribute;
            this.direction = direction;
        }

        @Override
        public Attribute<?> getAttribute() {
            return this.attribute;
        }

        @Override
        public Direction getDirection() {
            return this.direction;
        }
    }

    private class StoreQueryImpl
    implements StoreQuery {
        private final Criteria copiedCriteria;
        private final boolean copiedIncludeKeys;
        private final boolean copiedIncludeValues;
        private final Set<Attribute<?>> copiedAttributes;
        private final int copiedMaxResults;
        private final List<StoreQuery.Ordering> copiedOrdering;
        private final List<Aggregator> copiedAggregators;
        private final List<AggregatorInstance<?>> copiedAggregatorInstances;
        private final Set<Attribute<?>> copiedGroupByAttributes;
        private final ExecutionHints execHints;

        private StoreQueryImpl() {
            this.copiedCriteria = CacheQuery.this.getEffectiveCriteriaCopy();
            this.copiedIncludeKeys = CacheQuery.this.includeKeys;
            this.copiedIncludeValues = CacheQuery.this.includeValues;
            this.copiedAttributes = Collections.unmodifiableSet(new HashSet(CacheQuery.this.includedAttributes));
            this.copiedMaxResults = CacheQuery.this.maxResults;
            this.copiedOrdering = Collections.unmodifiableList(new ArrayList<StoreQuery.Ordering>(CacheQuery.this.orderings));
            this.copiedAggregators = Collections.unmodifiableList(CacheQuery.this.aggregators);
            this.copiedAggregatorInstances = Collections.unmodifiableList(CacheQuery.createAggregatorInstances(CacheQuery.this.aggregators));
            this.copiedGroupByAttributes = Collections.unmodifiableSet(new HashSet(CacheQuery.this.groupByAttributes));
            this.execHints = CacheQuery.this.hints;
        }

        @Override
        public Criteria getCriteria() {
            return this.copiedCriteria;
        }

        @Override
        public boolean requestsKeys() {
            return this.copiedIncludeKeys;
        }

        @Override
        public boolean requestsValues() {
            return this.copiedIncludeValues;
        }

        @Override
        public Cache getCache() {
            return CacheQuery.this.cache;
        }

        @Override
        public Set<Attribute<?>> requestedAttributes() {
            return this.copiedAttributes;
        }

        @Override
        public Set<Attribute<?>> groupByAttributes() {
            return this.copiedGroupByAttributes;
        }

        @Override
        public int maxResults() {
            return this.copiedMaxResults;
        }

        @Override
        public List<StoreQuery.Ordering> getOrdering() {
            return this.copiedOrdering;
        }

        @Override
        public List<Aggregator> getAggregators() {
            return this.copiedAggregators;
        }

        @Override
        public List<AggregatorInstance<?>> getAggregatorInstances() {
            return this.copiedAggregatorInstances;
        }

        @Override
        public String[] getTargets() {
            return CacheQuery.this.targets;
        }

        @Override
        public void targets(String[] targets) {
            CacheQuery.this.targets = targets;
        }

        @Override
        public ExecutionHints getExecutionHints() {
            return this.execHints;
        }
    }
}

