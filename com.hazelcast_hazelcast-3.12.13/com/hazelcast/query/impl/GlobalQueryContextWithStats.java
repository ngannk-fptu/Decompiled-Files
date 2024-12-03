/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GlobalQueryContextWithStats
extends QueryContext {
    private final HashMap<String, QueryTrackingIndex> knownIndexes = new HashMap();
    private final HashSet<QueryTrackingIndex> trackedIndexes = new HashSet(8);

    @Override
    void attachTo(Indexes indexes, int ownedPartitionCount) {
        super.attachTo(indexes, ownedPartitionCount);
        for (QueryTrackingIndex trackedIndex : this.trackedIndexes) {
            trackedIndex.resetPerQueryStats();
        }
        this.trackedIndexes.clear();
    }

    @Override
    void applyPerQueryStats() {
        for (QueryTrackingIndex trackedIndex : this.trackedIndexes) {
            trackedIndex.incrementQueryCount();
        }
    }

    @Override
    public Index matchIndex(String pattern, QueryContext.IndexMatchHint matchHint) {
        InternalIndex delegate = this.indexes.matchIndex(pattern, matchHint, this.ownedPartitionCount);
        if (delegate == null) {
            return null;
        }
        QueryTrackingIndex trackingIndex = this.knownIndexes.get(pattern);
        if (trackingIndex == null) {
            trackingIndex = new QueryTrackingIndex();
            this.knownIndexes.put(pattern, trackingIndex);
        }
        trackingIndex.attachTo(delegate);
        this.trackedIndexes.add(trackingIndex);
        return trackingIndex;
    }

    private static class QueryTrackingIndex
    implements InternalIndex {
        private InternalIndex delegate;
        private boolean hasQueries;

        private QueryTrackingIndex() {
        }

        public void attachTo(InternalIndex delegate) {
            this.delegate = delegate;
        }

        public void resetPerQueryStats() {
            this.hasQueries = false;
        }

        public void incrementQueryCount() {
            if (this.hasQueries) {
                this.delegate.getPerIndexStats().incrementQueryCount();
            }
        }

        @Override
        public String getName() {
            return this.delegate.getName();
        }

        @Override
        public String[] getComponents() {
            return this.delegate.getComponents();
        }

        @Override
        public boolean isOrdered() {
            return this.delegate.isOrdered();
        }

        @Override
        public String getUniqueKey() {
            return this.delegate.getUniqueKey();
        }

        @Override
        public TypeConverter getConverter() {
            return this.delegate.getConverter();
        }

        @Override
        public void putEntry(QueryableEntry entry, Object oldValue, Index.OperationSource operationSource) {
            this.delegate.putEntry(entry, oldValue, operationSource);
        }

        @Override
        public void removeEntry(Data key, Object value, Index.OperationSource operationSource) {
            this.delegate.removeEntry(key, value, operationSource);
        }

        @Override
        public boolean isEvaluateOnly() {
            return this.delegate.isEvaluateOnly();
        }

        @Override
        public boolean canEvaluate(Class<? extends Predicate> predicateClass) {
            return this.delegate.canEvaluate(predicateClass);
        }

        @Override
        public Set<QueryableEntry> evaluate(Predicate predicate) {
            Set<QueryableEntry> result = this.delegate.evaluate(predicate);
            this.hasQueries = true;
            return result;
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable value) {
            Set<QueryableEntry> result = this.delegate.getRecords(value);
            this.hasQueries = true;
            return result;
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable[] values) {
            Set<QueryableEntry> result = this.delegate.getRecords(values);
            this.hasQueries = true;
            return result;
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
            Set<QueryableEntry> result = this.delegate.getRecords(from, fromInclusive, to, toInclusive);
            this.hasQueries = true;
            return result;
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparison comparison, Comparable value) {
            Set<QueryableEntry> result = this.delegate.getRecords(comparison, value);
            this.hasQueries = true;
            return result;
        }

        @Override
        public void clear() {
            this.delegate.clear();
        }

        @Override
        public void destroy() {
            this.delegate.destroy();
        }

        @Override
        public Comparable canonicalizeQueryArgumentScalar(Comparable value) {
            return this.delegate.canonicalizeQueryArgumentScalar(value);
        }

        @Override
        public boolean hasPartitionIndexed(int partitionId) {
            return this.delegate.hasPartitionIndexed(partitionId);
        }

        @Override
        public boolean allPartitionsIndexed(int ownedPartitionCount) {
            return this.delegate.allPartitionsIndexed(ownedPartitionCount);
        }

        @Override
        public void markPartitionAsIndexed(int partitionId) {
            this.delegate.markPartitionAsIndexed(partitionId);
        }

        @Override
        public void markPartitionAsUnindexed(int partitionId) {
            this.delegate.markPartitionAsUnindexed(partitionId);
        }

        @Override
        public PerIndexStats getPerIndexStats() {
            return this.delegate.getPerIndexStats();
        }
    }
}

