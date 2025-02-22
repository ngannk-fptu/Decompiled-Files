/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.task.dynamicconfig;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.internal.eviction.EvictionPolicyComparator;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class EvictionConfigHolder {
    private final int size;
    private final String maxSizePolicy;
    private final String evictionPolicy;
    private final String comparatorClassName;
    private final Data comparator;

    public EvictionConfigHolder(int size, String maxSizePolicy, String evictionPolicy, String comparatorClassName, Data comparator) {
        this.size = size;
        this.maxSizePolicy = maxSizePolicy;
        this.evictionPolicy = evictionPolicy;
        this.comparatorClassName = comparatorClassName;
        this.comparator = comparator;
    }

    public int getSize() {
        return this.size;
    }

    public String getMaxSizePolicy() {
        return this.maxSizePolicy;
    }

    public String getEvictionPolicy() {
        return this.evictionPolicy;
    }

    public String getComparatorClassName() {
        return this.comparatorClassName;
    }

    public Data getComparator() {
        return this.comparator;
    }

    public EvictionConfig asEvictionConfg(SerializationService serializationService) {
        EvictionConfig config = new EvictionConfig(this.size, EvictionConfig.MaxSizePolicy.valueOf(this.maxSizePolicy), EvictionPolicy.valueOf(this.evictionPolicy));
        if (this.comparatorClassName != null) {
            config.setComparatorClassName(this.comparatorClassName);
        }
        if (this.comparator != null) {
            EvictionPolicyComparator evictionPolicyComparator = (EvictionPolicyComparator)serializationService.toObject(this.comparator);
            config.setComparator(evictionPolicyComparator);
        }
        return config;
    }

    public static EvictionConfigHolder of(EvictionConfig config, SerializationService serializationService) {
        return new EvictionConfigHolder(config.getSize(), config.getMaximumSizePolicy().name(), config.getEvictionPolicy().name(), config.getComparatorClassName(), (Data)serializationService.toData(config.getComparator()));
    }
}

