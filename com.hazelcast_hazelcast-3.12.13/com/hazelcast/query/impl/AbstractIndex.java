/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.ComparableIdentifiedDataSerializable;
import com.hazelcast.query.impl.Comparison;
import com.hazelcast.query.impl.CompositeConverter;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.IndexStore;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.TypeConverters;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.query.impl.predicates.PredicateDataSerializerHook;
import com.hazelcast.util.SetUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractIndex
implements InternalIndex {
    public static final ComparableIdentifiedDataSerializable NULL = new NullObject();
    protected final InternalSerializationService ss;
    protected final Extractors extractors;
    protected final IndexStore indexStore;
    protected final IndexCopyBehavior copyBehavior;
    private final String name;
    private final String[] components;
    private final boolean ordered;
    private final String uniqueKey;
    private final PerIndexStats stats;
    private volatile TypeConverter converter;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public AbstractIndex(IndexDefinition definition, InternalSerializationService ss, Extractors extractors, IndexCopyBehavior copyBehavior, PerIndexStats stats) {
        this.name = definition.getName();
        this.components = definition.getComponents();
        this.ordered = definition.isOrdered();
        this.uniqueKey = definition.getUniqueKey();
        this.ss = ss;
        this.extractors = extractors;
        this.copyBehavior = copyBehavior;
        this.indexStore = this.createIndexStore(definition, stats);
        this.stats = stats;
    }

    protected abstract IndexStore createIndexStore(IndexDefinition var1, PerIndexStats var2);

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public String[] getComponents() {
        return this.components;
    }

    @Override
    public boolean isOrdered() {
        return this.ordered;
    }

    @Override
    public String getUniqueKey() {
        return this.uniqueKey;
    }

    @Override
    public TypeConverter getConverter() {
        return this.converter;
    }

    @Override
    public void putEntry(QueryableEntry entry, Object oldValue, Index.OperationSource operationSource) {
        long timestamp = this.stats.makeTimestamp();
        IndexOperationStats operationStats = this.stats.createOperationStats();
        if (AbstractIndex.converterIsUnassignedOrTransient(this.converter)) {
            this.converter = this.obtainConverter(entry);
        }
        Object newAttributeValue = this.extractAttributeValue(entry.getKeyData(), entry.getTargetObject(false));
        if (oldValue == null) {
            this.indexStore.insert(newAttributeValue, entry, operationStats);
            this.stats.onInsert(timestamp, operationStats, operationSource);
        } else {
            Object oldAttributeValue = this.extractAttributeValue(entry.getKeyData(), oldValue);
            this.indexStore.update(oldAttributeValue, newAttributeValue, entry, operationStats);
            this.stats.onUpdate(timestamp, operationStats, operationSource);
        }
    }

    @Override
    public void removeEntry(Data key, Object value, Index.OperationSource operationSource) {
        long timestamp = this.stats.makeTimestamp();
        IndexOperationStats operationStats = this.stats.createOperationStats();
        Object attributeValue = this.extractAttributeValue(key, value);
        this.indexStore.remove(attributeValue, key, value, operationStats);
        this.stats.onRemove(timestamp, operationStats, operationSource);
    }

    @Override
    public boolean isEvaluateOnly() {
        return this.indexStore.isEvaluateOnly();
    }

    @Override
    public boolean canEvaluate(Class<? extends Predicate> predicateClass) {
        return this.indexStore.canEvaluate(predicateClass);
    }

    @Override
    public Set<QueryableEntry> evaluate(Predicate predicate) {
        assert (this.converter != null);
        return this.indexStore.evaluate(predicate, this.converter);
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable value) {
        long timestamp = this.stats.makeTimestamp();
        if (this.converter == null) {
            this.stats.onIndexHit(timestamp, 0L);
            return Collections.emptySet();
        }
        Set<QueryableEntry> result = this.indexStore.getRecords(this.convert(value));
        this.stats.onIndexHit(timestamp, result.size());
        return result;
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable[] values) {
        if (values.length == 1) {
            return this.getRecords(values[0]);
        }
        long timestamp = this.stats.makeTimestamp();
        if (this.converter == null || values.length == 0) {
            this.stats.onIndexHit(timestamp, 0L);
            return Collections.emptySet();
        }
        Set<Comparable> convertedValues = SetUtil.createHashSet(values.length);
        for (Comparable value : values) {
            Comparable converted = this.convert(value);
            convertedValues.add(this.canonicalizeQueryArgumentScalar(converted));
        }
        Set<QueryableEntry> result = this.indexStore.getRecords(convertedValues);
        this.stats.onIndexHit(timestamp, result.size());
        return result;
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparable from, boolean fromInclusive, Comparable to, boolean toInclusive) {
        long timestamp = this.stats.makeTimestamp();
        if (this.converter == null) {
            this.stats.onIndexHit(timestamp, 0L);
            return Collections.emptySet();
        }
        Set<QueryableEntry> result = this.indexStore.getRecords(this.convert(from), fromInclusive, this.convert(to), toInclusive);
        this.stats.onIndexHit(timestamp, result.size());
        return result;
    }

    @Override
    public Set<QueryableEntry> getRecords(Comparison comparison, Comparable value) {
        long timestamp = this.stats.makeTimestamp();
        if (this.converter == null) {
            this.stats.onIndexHit(timestamp, 0L);
            return Collections.emptySet();
        }
        Set<QueryableEntry> result = this.indexStore.getRecords(comparison, this.convert(value));
        this.stats.onIndexHit(timestamp, result.size());
        return result;
    }

    @Override
    public void clear() {
        this.indexStore.clear();
        this.converter = null;
        this.stats.onClear();
    }

    @Override
    public void destroy() {
        this.stats.onClear();
    }

    @Override
    public final Comparable canonicalizeQueryArgumentScalar(Comparable value) {
        return this.indexStore.canonicalizeQueryArgumentScalar(value);
    }

    @Override
    public PerIndexStats getPerIndexStats() {
        return this.stats;
    }

    private Object extractAttributeValue(Data key, Object value) {
        if (this.components.length == 1) {
            return QueryableEntry.extractAttributeValue(this.extractors, this.ss, this.components[0], key, value, null);
        }
        Comparable[] valueComponents = new Comparable[this.components.length];
        for (int i = 0; i < this.components.length; ++i) {
            Object extractedValue = QueryableEntry.extractAttributeValue(this.extractors, this.ss, this.components[i], key, value, null);
            if (extractedValue instanceof MultiResult) {
                throw new IllegalStateException("Collection/array attributes are not supported by composite indexes: " + this.components[i]);
            }
            if (extractedValue != null && !(extractedValue instanceof Comparable)) {
                throw new IllegalStateException("Unsupported non-comparable value type: " + extractedValue.getClass());
            }
            valueComponents[i] = (Comparable)extractedValue;
        }
        return new CompositeValue(valueComponents);
    }

    private Comparable convert(Comparable value) {
        return this.converter.convert(value);
    }

    private TypeConverter obtainConverter(QueryableEntry entry) {
        if (this.components.length == 1) {
            return entry.getConverter(this.components[0]);
        }
        CompositeConverter existingConverter = (CompositeConverter)this.converter;
        TypeConverter[] converters = new TypeConverter[this.components.length];
        for (int i = 0; i < this.components.length; ++i) {
            TypeConverter existingComponentConverter = AbstractIndex.getNonTransientComponentConverter(existingConverter, i);
            if (existingComponentConverter == null) {
                converters[i] = entry.getConverter(this.components[i]);
                assert (converters[i] != null);
                continue;
            }
            converters[i] = existingComponentConverter;
        }
        return new CompositeConverter(converters);
    }

    private static boolean converterIsUnassignedOrTransient(TypeConverter converter) {
        if (converter == null) {
            return true;
        }
        if (converter == TypeConverters.NULL_CONVERTER) {
            return true;
        }
        if (!(converter instanceof CompositeConverter)) {
            return false;
        }
        CompositeConverter compositeConverter = (CompositeConverter)converter;
        return compositeConverter.isTransient();
    }

    private static TypeConverter getNonTransientComponentConverter(CompositeConverter converter, int index) {
        if (converter == null) {
            return null;
        }
        TypeConverter componentConverter = converter.getComponentConverter(index);
        return componentConverter == TypeConverters.NULL_CONVERTER ? null : componentConverter;
    }

    private static final class NullObject
    implements ComparableIdentifiedDataSerializable {
        private NullObject() {
        }

        public int compareTo(Object o) {
            if (this == o) {
                return 0;
            }
            return o == CompositeValue.NEGATIVE_INFINITY ? 1 : -1;
        }

        public int hashCode() {
            return 0;
        }

        public boolean equals(Object obj) {
            return this == obj;
        }

        public String toString() {
            return "NULL";
        }

        @Override
        public void writeData(ObjectDataOutput out) {
        }

        @Override
        public void readData(ObjectDataInput in) {
        }

        @Override
        public int getFactoryId() {
            return PredicateDataSerializerHook.F_ID;
        }

        @Override
        public int getId() {
            return 17;
        }
    }
}

