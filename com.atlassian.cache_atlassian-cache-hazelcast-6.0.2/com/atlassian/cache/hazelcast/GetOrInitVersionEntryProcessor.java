/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.map.AbstractEntryProcessor
 */
package com.atlassian.cache.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import java.util.Map;

class GetOrInitVersionEntryProcessor<K>
extends AbstractEntryProcessor<K, Long> {
    private static GetOrInitVersionEntryProcessor<Object> INSTANCE = new GetOrInitVersionEntryProcessor();

    protected static <T> GetOrInitVersionEntryProcessor<T> getInstance() {
        return INSTANCE;
    }

    private GetOrInitVersionEntryProcessor() {
    }

    public Object process(Map.Entry<K, Long> entry) {
        Long value = entry.getValue();
        if (value == null) {
            value = 1L;
            entry.setValue(value);
        }
        return value;
    }
}

