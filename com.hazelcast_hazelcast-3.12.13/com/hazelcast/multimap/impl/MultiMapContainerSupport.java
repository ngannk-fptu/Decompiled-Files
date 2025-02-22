/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapValue;
import com.hazelcast.multimap.impl.ValueCollectionFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

abstract class MultiMapContainerSupport {
    protected final ConcurrentMap<Data, MultiMapValue> multiMapValues = MapUtil.createConcurrentHashMap(1000);
    protected final String name;
    protected final NodeEngine nodeEngine;
    protected final MultiMapConfig config;

    MultiMapContainerSupport(String name, NodeEngine nodeEngine) {
        this.name = name;
        this.nodeEngine = nodeEngine;
        this.config = nodeEngine.getConfig().findMultiMapConfig(name);
    }

    public MultiMapValue getOrCreateMultiMapValue(Data dataKey) {
        MultiMapValue multiMapValue = (MultiMapValue)this.multiMapValues.get(dataKey);
        if (multiMapValue != null) {
            return multiMapValue;
        }
        MultiMapConfig.ValueCollectionType valueCollectionType = this.config.getValueCollectionType();
        Collection<MultiMapRecord> collection = ValueCollectionFactory.createCollection(valueCollectionType);
        multiMapValue = new MultiMapValue(collection);
        this.multiMapValues.put(dataKey, multiMapValue);
        return multiMapValue;
    }

    public MultiMapValue getMultiMapValueOrNull(Data dataKey) {
        return (MultiMapValue)this.multiMapValues.get(dataKey);
    }

    public ConcurrentMap<Data, MultiMapValue> getMultiMapValues() {
        return this.multiMapValues;
    }

    public MultiMapConfig getConfig() {
        return this.config;
    }
}

