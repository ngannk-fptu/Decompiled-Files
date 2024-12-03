/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.impl.GlobalIndexesStats;
import com.hazelcast.monitor.impl.IndexesStats;
import com.hazelcast.monitor.impl.PartitionIndexesStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.AttributeIndexRegistry;
import com.hazelcast.query.impl.ConverterCache;
import com.hazelcast.query.impl.DefaultIndexProvider;
import com.hazelcast.query.impl.GlobalQueryContextProvider;
import com.hazelcast.query.impl.GlobalQueryContextProviderWithStats;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.IndexCopyBehavior;
import com.hazelcast.query.impl.IndexDefinition;
import com.hazelcast.query.impl.IndexInfo;
import com.hazelcast.query.impl.IndexProvider;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.PartitionQueryContextProvider;
import com.hazelcast.query.impl.PartitionQueryContextProviderWithStats;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryContextProvider;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Indexes {
    public static final int SKIP_PARTITIONS_COUNT_CHECK = -1;
    private static final InternalIndex[] EMPTY_INDEXES = new InternalIndex[0];
    private final boolean global;
    private final boolean usesCachedQueryableEntries;
    private final IndexesStats stats;
    private final Extractors extractors;
    private final IndexProvider indexProvider;
    private final IndexCopyBehavior indexCopyBehavior;
    private final QueryContextProvider queryContextProvider;
    private final InternalSerializationService serializationService;
    private final Map<String, InternalIndex> indexesByName = new ConcurrentHashMap<String, InternalIndex>(3);
    private final AttributeIndexRegistry attributeIndexRegistry = new AttributeIndexRegistry();
    private final AttributeIndexRegistry evaluateOnlyAttributeIndexRegistry = new AttributeIndexRegistry();
    private final ConverterCache converterCache = new ConverterCache(this);
    private final ConcurrentMap<String, Boolean> definitions = new ConcurrentHashMap<String, Boolean>();
    private volatile InternalIndex[] indexes = EMPTY_INDEXES;
    private volatile InternalIndex[] compositeIndexes = EMPTY_INDEXES;

    private Indexes(InternalSerializationService serializationService, IndexCopyBehavior indexCopyBehavior, Extractors extractors, IndexProvider indexProvider, boolean usesCachedQueryableEntries, boolean statisticsEnabled, boolean global) {
        this.global = global;
        this.indexCopyBehavior = indexCopyBehavior;
        this.serializationService = serializationService;
        this.usesCachedQueryableEntries = usesCachedQueryableEntries;
        this.stats = Indexes.createStats(global, statisticsEnabled);
        this.extractors = extractors == null ? Extractors.newBuilder(serializationService).build() : extractors;
        this.indexProvider = indexProvider == null ? new DefaultIndexProvider() : indexProvider;
        this.queryContextProvider = Indexes.createQueryContextProvider(this, global, statisticsEnabled);
    }

    public static void markPartitionAsIndexed(int partitionId, InternalIndex[] indexes) {
        for (InternalIndex index : indexes) {
            index.markPartitionAsIndexed(partitionId);
        }
    }

    public static void markPartitionAsUnindexed(int partitionId, InternalIndex[] indexes) {
        for (InternalIndex index : indexes) {
            index.markPartitionAsUnindexed(partitionId);
        }
    }

    public static Builder newBuilder(SerializationService ss, IndexCopyBehavior indexCopyBehavior) {
        return new Builder(ss, indexCopyBehavior);
    }

    public synchronized InternalIndex addOrGetIndex(String name, boolean ordered) {
        InternalIndex index = this.indexesByName.get(name);
        if (index != null) {
            return index;
        }
        IndexDefinition definition = IndexDefinition.parse(name, ordered);
        index = this.indexesByName.get(definition.getName());
        if (index != null) {
            return index;
        }
        index = this.indexProvider.createIndex(definition, this.extractors, this.serializationService, this.indexCopyBehavior, this.stats.createPerIndexStats(ordered, this.usesCachedQueryableEntries));
        this.indexesByName.put(definition.getName(), index);
        if (index.isEvaluateOnly()) {
            this.evaluateOnlyAttributeIndexRegistry.register(index);
        } else {
            this.attributeIndexRegistry.register(index);
        }
        this.converterCache.invalidate(index);
        this.indexes = this.indexesByName.values().toArray(EMPTY_INDEXES);
        if (definition.getComponents().length > 1) {
            InternalIndex[] oldCompositeIndexes = this.compositeIndexes;
            InternalIndex[] newCompositeIndexes = Arrays.copyOf(oldCompositeIndexes, oldCompositeIndexes.length + 1);
            newCompositeIndexes[oldCompositeIndexes.length] = index;
            this.compositeIndexes = newCompositeIndexes;
        }
        return index;
    }

    public void recordIndexDefinition(String name, boolean ordered) {
        if (this.definitions.containsKey(name) || this.indexesByName.containsKey(name)) {
            return;
        }
        IndexDefinition definition = IndexDefinition.parse(name, ordered);
        if (this.definitions.containsKey(definition.getName()) || this.indexesByName.containsKey(definition.getName())) {
            return;
        }
        this.definitions.put(name, ordered);
    }

    public void createIndexesFromRecordedDefinitions() {
        for (Map.Entry definition : this.definitions.entrySet()) {
            this.addOrGetIndex((String)definition.getKey(), (Boolean)definition.getValue());
            this.definitions.remove(definition.getKey(), definition.getValue());
        }
    }

    public Collection<IndexInfo> getIndexDefinitions() {
        ArrayList<IndexInfo> indexInfos = new ArrayList<IndexInfo>(this.definitions.size());
        for (Map.Entry definition : this.definitions.entrySet()) {
            indexInfos.add(new IndexInfo((String)definition.getKey(), (Boolean)definition.getValue()));
        }
        return indexInfos;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public InternalIndex[] getIndexes() {
        return this.indexes;
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public InternalIndex[] getCompositeIndexes() {
        return this.compositeIndexes;
    }

    public void destroyIndexes() {
        InternalIndex[] indexesSnapshot = this.getIndexes();
        this.indexes = EMPTY_INDEXES;
        this.compositeIndexes = EMPTY_INDEXES;
        this.indexesByName.clear();
        this.attributeIndexRegistry.clear();
        this.evaluateOnlyAttributeIndexRegistry.clear();
        this.converterCache.clear();
        for (InternalIndex index : indexesSnapshot) {
            index.destroy();
        }
    }

    public void clearAll() {
        InternalIndex[] indexesSnapshot;
        for (InternalIndex index : indexesSnapshot = this.getIndexes()) {
            index.clear();
        }
    }

    public boolean haveAtLeastOneIndex() {
        return this.indexes != EMPTY_INDEXES;
    }

    public boolean haveAtLeastOneIndexOrDefinition() {
        boolean haveAtLeastOneIndexOrDefinition;
        boolean bl = haveAtLeastOneIndexOrDefinition = this.haveAtLeastOneIndex() || !this.definitions.isEmpty();
        assert (this.isGlobal() || !haveAtLeastOneIndexOrDefinition || !this.haveAtLeastOneIndex() || this.definitions.isEmpty());
        return haveAtLeastOneIndexOrDefinition;
    }

    public void putEntry(QueryableEntry queryableEntry, Object oldValue, Index.OperationSource operationSource) {
        InternalIndex[] indexes;
        for (InternalIndex index : indexes = this.getIndexes()) {
            index.putEntry(queryableEntry, oldValue, operationSource);
        }
    }

    public void removeEntry(Data key, Object value, Index.OperationSource operationSource) {
        InternalIndex[] indexes;
        for (InternalIndex index : indexes = this.getIndexes()) {
            index.removeEntry(key, value, operationSource);
        }
    }

    public boolean isGlobal() {
        return this.global;
    }

    public InternalIndex getIndex(String name) {
        return this.indexesByName.get(name);
    }

    public Set<QueryableEntry> query(Predicate predicate, int ownedPartitionCount) {
        this.stats.incrementQueryCount();
        if (!this.haveAtLeastOneIndex() || !(predicate instanceof IndexAwarePredicate)) {
            return null;
        }
        IndexAwarePredicate indexAwarePredicate = (IndexAwarePredicate)predicate;
        QueryContext queryContext = this.queryContextProvider.obtainContextFor(this, ownedPartitionCount);
        if (!indexAwarePredicate.isIndexed(queryContext)) {
            return null;
        }
        Set<QueryableEntry> result = indexAwarePredicate.filter(queryContext);
        if (result != null) {
            this.stats.incrementIndexedQueryCount();
            queryContext.applyPerQueryStats();
        }
        return result;
    }

    public InternalIndex matchIndex(String pattern, QueryContext.IndexMatchHint matchHint, int ownedPartitionCount) {
        InternalIndex index = matchHint == QueryContext.IndexMatchHint.EXACT_NAME ? this.indexesByName.get(pattern) : this.attributeIndexRegistry.match(pattern, matchHint);
        if (index == null || !index.allPartitionsIndexed(ownedPartitionCount)) {
            return null;
        }
        return index;
    }

    public InternalIndex matchIndex(String pattern, Class<? extends Predicate> predicateClass, QueryContext.IndexMatchHint matchHint, int ownedPartitionCount) {
        InternalIndex index;
        if (matchHint == QueryContext.IndexMatchHint.EXACT_NAME) {
            index = this.indexesByName.get(pattern);
        } else {
            index = this.evaluateOnlyAttributeIndexRegistry.match(pattern, matchHint);
            if (index == null) {
                index = this.attributeIndexRegistry.match(pattern, matchHint);
            }
        }
        if (index == null) {
            return null;
        }
        if (!index.canEvaluate(predicateClass)) {
            return null;
        }
        if (!index.allPartitionsIndexed(ownedPartitionCount)) {
            return null;
        }
        return index;
    }

    public TypeConverter getConverter(String attribute) {
        return this.converterCache.get(attribute);
    }

    public IndexesStats getIndexesStats() {
        return this.stats;
    }

    private static QueryContextProvider createQueryContextProvider(Indexes indexes, boolean global, boolean statisticsEnabled) {
        if (statisticsEnabled) {
            return global ? new GlobalQueryContextProviderWithStats() : new PartitionQueryContextProviderWithStats(indexes);
        }
        return global ? new GlobalQueryContextProvider() : new PartitionQueryContextProvider(indexes);
    }

    private static IndexesStats createStats(boolean global, boolean statisticsEnabled) {
        if (statisticsEnabled) {
            return global ? new GlobalIndexesStats() : new PartitionIndexesStats();
        }
        return IndexesStats.EMPTY;
    }

    public static final class Builder {
        private final IndexCopyBehavior indexCopyBehavior;
        private final InternalSerializationService serializationService;
        private boolean global = true;
        private boolean statsEnabled;
        private boolean usesCachedQueryableEntries;
        private Extractors extractors;
        private IndexProvider indexProvider;

        Builder(SerializationService ss, IndexCopyBehavior indexCopyBehavior) {
            this.serializationService = Preconditions.checkNotNull((InternalSerializationService)ss, "serializationService cannot be null");
            this.indexCopyBehavior = Preconditions.checkNotNull(indexCopyBehavior, "indexCopyBehavior cannot be null");
        }

        public Builder global(boolean global) {
            this.global = global;
            return this;
        }

        public Builder indexProvider(IndexProvider indexProvider) {
            this.indexProvider = indexProvider;
            return this;
        }

        public Builder extractors(Extractors extractors) {
            this.extractors = extractors;
            return this;
        }

        public Builder usesCachedQueryableEntries(boolean usesCachedQueryableEntries) {
            this.usesCachedQueryableEntries = usesCachedQueryableEntries;
            return this;
        }

        public Builder statsEnabled(boolean statsEnabled) {
            this.statsEnabled = statsEnabled;
            return this;
        }

        public Indexes build() {
            return new Indexes(this.serializationService, this.indexCopyBehavior, this.extractors, this.indexProvider, this.usesCachedQueryableEntries, this.statsEnabled, this.global);
        }
    }
}

