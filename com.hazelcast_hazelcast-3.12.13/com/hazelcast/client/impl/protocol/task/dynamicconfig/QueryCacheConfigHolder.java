/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.client.impl.protocol.task.dynamicconfig.EvictionConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.ListenerConfigHolder;
import com.hazelcast.client.impl.protocol.task.dynamicconfig.PredicateConfigHolder;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.ArrayList;
import java.util.List;

public class QueryCacheConfigHolder {
    private int batchSize;
    private int bufferSize;
    private int delaySeconds;
    private boolean includeValue;
    private boolean populate;
    private boolean coalesce;
    private String inMemoryFormat;
    private String name;
    private PredicateConfigHolder predicateConfigHolder;
    private EvictionConfigHolder evictionConfigHolder;
    private List<ListenerConfigHolder> listenerConfigs;
    private List<MapIndexConfig> indexConfigs;

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public int getDelaySeconds() {
        return this.delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public void setIncludeValue(boolean includeValue) {
        this.includeValue = includeValue;
    }

    public boolean isPopulate() {
        return this.populate;
    }

    public void setPopulate(boolean populate) {
        this.populate = populate;
    }

    public boolean isCoalesce() {
        return this.coalesce;
    }

    public void setCoalesce(boolean coalesce) {
        this.coalesce = coalesce;
    }

    public String getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public void setInMemoryFormat(String inMemoryFormat) {
        this.inMemoryFormat = inMemoryFormat;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PredicateConfigHolder getPredicateConfigHolder() {
        return this.predicateConfigHolder;
    }

    public void setPredicateConfigHolder(PredicateConfigHolder predicateConfigHolder) {
        this.predicateConfigHolder = predicateConfigHolder;
    }

    public EvictionConfigHolder getEvictionConfigHolder() {
        return this.evictionConfigHolder;
    }

    public void setEvictionConfigHolder(EvictionConfigHolder evictionConfigHolder) {
        this.evictionConfigHolder = evictionConfigHolder;
    }

    public List<ListenerConfigHolder> getListenerConfigs() {
        return this.listenerConfigs;
    }

    public void setListenerConfigs(List<ListenerConfigHolder> listenerConfigs) {
        this.listenerConfigs = listenerConfigs;
    }

    public List<MapIndexConfig> getIndexConfigs() {
        return this.indexConfigs;
    }

    public void setIndexConfigs(List<MapIndexConfig> indexConfigs) {
        this.indexConfigs = indexConfigs;
    }

    public QueryCacheConfig asQueryCacheConfig(SerializationService serializationService) {
        QueryCacheConfig config = new QueryCacheConfig();
        config.setBatchSize(this.batchSize);
        config.setBufferSize(this.bufferSize);
        config.setCoalesce(this.coalesce);
        config.setDelaySeconds(this.delaySeconds);
        config.setEvictionConfig(this.evictionConfigHolder.asEvictionConfg(serializationService));
        if (this.listenerConfigs != null && !this.listenerConfigs.isEmpty()) {
            ArrayList<EntryListenerConfig> entryListenerConfigs = new ArrayList<EntryListenerConfig>(this.listenerConfigs.size());
            for (ListenerConfigHolder holder : this.listenerConfigs) {
                entryListenerConfigs.add((EntryListenerConfig)holder.asListenerConfig(serializationService));
            }
            config.setEntryListenerConfigs(entryListenerConfigs);
        } else {
            config.setEntryListenerConfigs(new ArrayList<EntryListenerConfig>());
        }
        config.setIncludeValue(this.includeValue);
        config.setInMemoryFormat(InMemoryFormat.valueOf(this.inMemoryFormat));
        config.setIndexConfigs(this.indexConfigs == null ? new ArrayList() : this.indexConfigs);
        config.setName(this.name);
        config.setPredicateConfig(this.predicateConfigHolder.asPredicateConfig(serializationService));
        config.setPopulate(this.populate);
        return config;
    }

    public static QueryCacheConfigHolder of(QueryCacheConfig config, SerializationService serializationService) {
        QueryCacheConfigHolder holder = new QueryCacheConfigHolder();
        holder.setBatchSize(config.getBatchSize());
        holder.setBufferSize(config.getBufferSize());
        holder.setCoalesce(config.isCoalesce());
        holder.setDelaySeconds(config.getDelaySeconds());
        holder.setEvictionConfigHolder(EvictionConfigHolder.of(config.getEvictionConfig(), serializationService));
        holder.setIncludeValue(config.isIncludeValue());
        holder.setInMemoryFormat(config.getInMemoryFormat().toString());
        holder.setName(config.getName());
        if (config.getIndexConfigs() != null && !config.getIndexConfigs().isEmpty()) {
            ArrayList<MapIndexConfig> indexConfigs = new ArrayList<MapIndexConfig>(config.getIndexConfigs().size());
            for (MapIndexConfig indexConfig : config.getIndexConfigs()) {
                indexConfigs.add(new MapIndexConfig(indexConfig));
            }
            holder.setIndexConfigs(indexConfigs);
        }
        if (config.getEntryListenerConfigs() != null && !config.getEntryListenerConfigs().isEmpty()) {
            ArrayList<ListenerConfigHolder> listenerConfigHolders = new ArrayList<ListenerConfigHolder>(config.getEntryListenerConfigs().size());
            for (EntryListenerConfig listenerConfig : config.getEntryListenerConfigs()) {
                listenerConfigHolders.add(ListenerConfigHolder.of(listenerConfig, serializationService));
            }
            holder.setListenerConfigs(listenerConfigHolders);
        }
        holder.setPredicateConfigHolder(PredicateConfigHolder.of(config.getPredicateConfig(), serializationService));
        holder.setPopulate(config.isPopulate());
        return holder;
    }
}

