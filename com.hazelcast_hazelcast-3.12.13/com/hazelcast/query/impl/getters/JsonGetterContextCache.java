/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.JsonGetterContext;
import com.hazelcast.util.SampleableConcurrentHashMap;

public class JsonGetterContextCache {
    private final int cleanupRemoveAtLeastItems;
    private final SampleableConcurrentHashMap<String, JsonGetterContext> internalCache;
    private final int maxContexts;

    public JsonGetterContextCache(int maxContexts, int cleanupRemoveAtLeastItems) {
        this.maxContexts = maxContexts;
        this.cleanupRemoveAtLeastItems = cleanupRemoveAtLeastItems;
        this.internalCache = new SampleableConcurrentHashMap(maxContexts);
    }

    public JsonGetterContext getContext(String queryPath) {
        JsonGetterContext context = (JsonGetterContext)this.internalCache.get(queryPath);
        if (context != null) {
            return context;
        }
        context = new JsonGetterContext(queryPath);
        JsonGetterContext previousContextValue = this.internalCache.putIfAbsent(queryPath, context);
        if (previousContextValue == null) {
            this.cleanupIfNeccessary(context);
            return context;
        }
        return previousContextValue;
    }

    private void cleanupIfNeccessary(JsonGetterContext excluded) {
        int cacheCount;
        while ((cacheCount = this.internalCache.size()) > this.maxContexts) {
            int sampleCount = Math.max(cacheCount - this.maxContexts, this.cleanupRemoveAtLeastItems) + 1;
            for (SampleableConcurrentHashMap.SamplingEntry sample : this.internalCache.getRandomSamples(sampleCount)) {
                if (excluded == sample.getEntryValue()) continue;
                this.internalCache.remove(sample.getEntryKey());
            }
        }
    }

    int getCacheSize() {
        return this.internalCache.size();
    }
}

