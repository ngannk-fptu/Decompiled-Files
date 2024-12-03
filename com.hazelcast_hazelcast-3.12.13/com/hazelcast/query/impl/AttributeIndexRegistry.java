/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.CompositeConverter;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AttributeIndexRegistry {
    private final ConcurrentMap<String, Record> registry = new ConcurrentHashMap<String, Record>();

    public void register(InternalIndex index) {
        String[] components = index.getComponents();
        String firstComponent = components[0];
        Record record = (Record)this.registry.get(firstComponent);
        if (record == null) {
            record = new Record();
            this.registry.put(firstComponent, record);
        }
        if (index.isOrdered()) {
            if (record.orderedWorseThan(index)) {
                record.ordered = components.length == 1 ? index : new FirstComponentDecorator(index);
            }
        } else if (record.unorderedWorseThan(index)) {
            record.unordered = index;
        }
    }

    public InternalIndex match(String attribute, QueryContext.IndexMatchHint matchHint) {
        Record record = (Record)this.registry.get(attribute);
        if (record == null) {
            return null;
        }
        switch (matchHint) {
            case NONE: 
            case PREFER_ORDERED: {
                InternalIndex ordered = record.ordered;
                return ordered == null ? record.unordered : ordered;
            }
            case PREFER_UNORDERED: {
                InternalIndex unordered = record.unordered;
                return unordered == null ? record.ordered : unordered;
            }
        }
        throw new IllegalStateException("unexpected match hint: " + (Object)((Object)matchHint));
    }

    public void clear() {
        this.registry.clear();
    }

    static final class FirstComponentDecorator
    implements InternalIndex {
        final InternalIndex delegate;
        private final int width;
        private final String[] components;

        public FirstComponentDecorator(InternalIndex delegate) {
            assert (delegate.getComponents().length > 1);
            assert (delegate.isOrdered());
            this.delegate = delegate;
            this.width = delegate.getComponents().length;
            this.components = new String[]{delegate.getComponents()[0]};
        }

        @Override
        public String getName() {
            throw this.newUnsupportedException();
        }

        @Override
        public String[] getComponents() {
            return this.components;
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
            CompositeConverter converter = (CompositeConverter)this.delegate.getConverter();
            return converter == null ? null : converter.getComponentConverter(0);
        }

        @Override
        public void putEntry(QueryableEntry entry, Object oldValue, Index.OperationSource operationSource) {
            throw this.newUnsupportedException();
        }

        @Override
        public void removeEntry(Data key, Object value, Index.OperationSource operationSource) {
            throw this.newUnsupportedException();
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
            return this.delegate.evaluate(predicate);
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable value) {
            CompositeValue from = new CompositeValue(this.width, value, CompositeValue.NEGATIVE_INFINITY);
            CompositeValue to = new CompositeValue(this.width, value, CompositeValue.POSITIVE_INFINITY);
            return this.delegate.getRecords(from, false, to, false);
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable[] values) {
            if (values.length == 0) {
                return Collections.emptySet();
            }
            TypeConverter converter = this.getConverter();
            if (converter == null) {
                return Collections.emptySet();
            }
            if (values.length == 1) {
                return this.getRecords(values[0]);
            }
            HashSet<Comparable> convertedValues = new HashSet<Comparable>();
            for (Comparable value : values) {
                Comparable converted = converter.convert(value);
                convertedValues.add(this.canonicalizeQueryArgumentScalar(converted));
            }
            if (convertedValues.size() == 1) {
                return this.getRecords((Comparable)convertedValues.iterator().next());
            }
            HashSet<QueryableEntry> result = new HashSet<QueryableEntry>();
            for (Comparable value : convertedValues) {
                result.addAll(this.getRecords(value));
            }
            return result;
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
            CompositeValue compositeFrom = new CompositeValue(this.width, from, fromInclusive ? CompositeValue.NEGATIVE_INFINITY : CompositeValue.POSITIVE_INFINITY);
            CompositeValue compositeTo = new CompositeValue(this.width, to, toInclusive ? CompositeValue.POSITIVE_INFINITY : CompositeValue.NEGATIVE_INFINITY);
            return this.delegate.getRecords(compositeFrom, false, compositeTo, false);
        }

        @Override
        public Set<QueryableEntry> getRecords(Comparison comparison, Comparable value) {
            switch (comparison) {
                case NOT_EQUAL: {
                    HashSet<QueryableEntry> result = new HashSet<QueryableEntry>();
                    result.addAll(this.delegate.getRecords(Comparison.LESS, new CompositeValue(this.width, value, CompositeValue.NEGATIVE_INFINITY)));
                    result.addAll(this.delegate.getRecords(Comparison.GREATER, new CompositeValue(this.width, value, CompositeValue.POSITIVE_INFINITY)));
                    return result;
                }
                case LESS: {
                    CompositeValue lessFrom = new CompositeValue(this.width, AbstractIndex.NULL, CompositeValue.POSITIVE_INFINITY);
                    CompositeValue lessTo = new CompositeValue(this.width, value, CompositeValue.NEGATIVE_INFINITY);
                    return this.delegate.getRecords(lessFrom, false, lessTo, false);
                }
                case GREATER: {
                    return this.delegate.getRecords(Comparison.GREATER, new CompositeValue(this.width, value, CompositeValue.POSITIVE_INFINITY));
                }
                case LESS_OR_EQUAL: {
                    CompositeValue greaterOrEqualFrom = new CompositeValue(this.width, AbstractIndex.NULL, CompositeValue.POSITIVE_INFINITY);
                    CompositeValue greaterOrEqualTo = new CompositeValue(this.width, value, CompositeValue.POSITIVE_INFINITY);
                    return this.delegate.getRecords(greaterOrEqualFrom, false, greaterOrEqualTo, false);
                }
                case GREATER_OR_EQUAL: {
                    return this.delegate.getRecords(Comparison.GREATER_OR_EQUAL, new CompositeValue(this.width, value, CompositeValue.NEGATIVE_INFINITY));
                }
            }
            throw new IllegalStateException("unexpected comparison: " + (Object)((Object)comparison));
        }

        @Override
        public void clear() {
            throw this.newUnsupportedException();
        }

        @Override
        public void destroy() {
            throw this.newUnsupportedException();
        }

        @Override
        public Comparable canonicalizeQueryArgumentScalar(Comparable value) {
            return this.delegate.canonicalizeQueryArgumentScalar(value);
        }

        @Override
        public boolean hasPartitionIndexed(int partitionId) {
            throw this.newUnsupportedException();
        }

        @Override
        public boolean allPartitionsIndexed(int ownedPartitionCount) {
            return this.delegate.allPartitionsIndexed(ownedPartitionCount);
        }

        @Override
        public void markPartitionAsIndexed(int partitionId) {
            throw this.newUnsupportedException();
        }

        @Override
        public void markPartitionAsUnindexed(int partitionId) {
            throw this.newUnsupportedException();
        }

        @Override
        public PerIndexStats getPerIndexStats() {
            return this.delegate.getPerIndexStats();
        }

        private RuntimeException newUnsupportedException() {
            return new UnsupportedOperationException("decorated composite indexes support only querying");
        }
    }

    private static class Record {
        volatile InternalIndex unordered;
        volatile InternalIndex ordered;

        private Record() {
        }

        public boolean unorderedWorseThan(InternalIndex candidate) {
            assert (!candidate.isOrdered());
            return this.unordered == null && candidate.getComponents().length == 1;
        }

        public boolean orderedWorseThan(InternalIndex candidate) {
            assert (candidate.isOrdered());
            InternalIndex current = this.ordered;
            if (current == null) {
                return true;
            }
            if (current instanceof FirstComponentDecorator) {
                String[] candidateComponents = candidate.getComponents();
                if (candidateComponents.length > 1) {
                    FirstComponentDecorator currentDecorator = (FirstComponentDecorator)current;
                    return currentDecorator.width > candidateComponents.length;
                }
                return true;
            }
            return false;
        }
    }
}

