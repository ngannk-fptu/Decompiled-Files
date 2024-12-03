/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EntryListenerConfigReadOnly;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MapIndexConfigReadOnly;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.QueryCacheConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class QueryCacheConfigReadOnly
extends QueryCacheConfig {
    public QueryCacheConfigReadOnly(QueryCacheConfig other) {
        super(other);
    }

    @Override
    public List<MapIndexConfig> getIndexConfigs() {
        List<MapIndexConfig> mapIndexConfigs = super.getIndexConfigs();
        ArrayList<MapIndexConfigReadOnly> readOnlyMapIndexConfigs = new ArrayList<MapIndexConfigReadOnly>(mapIndexConfigs.size());
        for (MapIndexConfig mapIndexConfig : mapIndexConfigs) {
            readOnlyMapIndexConfigs.add(mapIndexConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyMapIndexConfigs);
    }

    @Override
    public List<EntryListenerConfig> getEntryListenerConfigs() {
        List<EntryListenerConfig> listenerConfigs = super.getEntryListenerConfigs();
        ArrayList<EntryListenerConfigReadOnly> readOnlyListenerConfigs = new ArrayList<EntryListenerConfigReadOnly>(listenerConfigs.size());
        for (EntryListenerConfig listenerConfig : listenerConfigs) {
            readOnlyListenerConfigs.add(listenerConfig.getAsReadOnly());
        }
        return Collections.unmodifiableList(readOnlyListenerConfigs);
    }

    @Override
    public EvictionConfig getEvictionConfig() {
        return super.getEvictionConfig().getAsReadOnly();
    }

    @Override
    public PredicateConfig getPredicateConfig() {
        return super.getPredicateConfig().getAsReadOnly();
    }

    @Override
    public QueryCacheConfig setBatchSize(int batchSize) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setBufferSize(int bufferSize) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setDelaySeconds(int delaySeconds) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setEvictionConfig(EvictionConfig evictionConfig) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setIncludeValue(boolean includeValue) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setIndexConfigs(List<MapIndexConfig> indexConfigs) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setPredicateConfig(PredicateConfig predicateConfig) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setPopulate(boolean populate) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }

    @Override
    public QueryCacheConfig setCoalesce(boolean coalesce) {
        throw new UnsupportedOperationException("This config is read-only query cache: " + this.getName());
    }
}

