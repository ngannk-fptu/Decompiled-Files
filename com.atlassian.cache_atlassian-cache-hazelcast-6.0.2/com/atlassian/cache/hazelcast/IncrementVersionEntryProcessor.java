/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.map.AbstractEntryProcessor
 */
package com.atlassian.cache.hazelcast;

import com.hazelcast.map.AbstractEntryProcessor;
import java.util.Map;

class IncrementVersionEntryProcessor<K>
extends AbstractEntryProcessor<K, Long> {
    private static final IncrementVersionEntryProcessor<Object> INSTANCE = new IncrementVersionEntryProcessor();

    protected static <T> IncrementVersionEntryProcessor<T> getInstance() {
        return INSTANCE;
    }

    private IncrementVersionEntryProcessor() {
    }

    public Object process(Map.Entry<K, Long> entry) {
        Long value = entry.getValue();
        value = value == null ? 1L : value + 1L;
        entry.setValue(value);
        return value;
    }
}

