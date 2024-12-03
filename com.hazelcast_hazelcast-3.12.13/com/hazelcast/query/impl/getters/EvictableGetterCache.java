/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConcurrentReferenceHashMap;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.SampleableConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.Nullable;

class EvictableGetterCache {
    private final SampleableConcurrentHashMap<Class, SampleableConcurrentHashMap<String, Getter>> getterCache;
    private final ConstructorFunction<Class, SampleableConcurrentHashMap<String, Getter>> getterCacheConstructor;
    private final int maxClassCount;
    private final int afterEvictionClassCount;
    private final int maxGetterPerClassCount;
    private final int afterEvictionGetterPerClassCount;

    EvictableGetterCache(int maxClassCount, final int maxGetterPerClassCount, float evictPercentage, boolean strongReferences) {
        ConcurrentReferenceHashMap.ReferenceType referenceType = strongReferences ? ConcurrentReferenceHashMap.ReferenceType.STRONG : ConcurrentReferenceHashMap.ReferenceType.SOFT;
        this.getterCache = new SampleableConcurrentHashMap(maxClassCount, referenceType, referenceType);
        this.getterCacheConstructor = new ConstructorFunction<Class, SampleableConcurrentHashMap<String, Getter>>(){

            @Override
            public SampleableConcurrentHashMap<String, Getter> createNew(Class arg) {
                return new SampleableConcurrentHashMap<String, Getter>(maxGetterPerClassCount);
            }
        };
        this.maxClassCount = maxClassCount;
        this.afterEvictionClassCount = (int)((float)maxClassCount * (1.0f - evictPercentage));
        this.maxGetterPerClassCount = maxGetterPerClassCount;
        this.afterEvictionGetterPerClassCount = (int)((float)maxGetterPerClassCount * (1.0f - evictPercentage));
    }

    @Nullable
    Getter getGetter(Class clazz, String attributeName) {
        ConcurrentMap cache = (ConcurrentMap)this.getterCache.get(clazz);
        if (cache == null) {
            return null;
        }
        return (Getter)cache.get(attributeName);
    }

    Getter putGetter(Class clazz, String attributeName, Getter getter) {
        SampleableConcurrentHashMap<String, Getter> cache = ConcurrencyUtil.getOrPutIfAbsent(this.getterCache, clazz, this.getterCacheConstructor);
        Getter foundGetter = cache.putIfAbsent(attributeName, getter);
        this.evictOnPut(cache);
        return foundGetter == null ? getter : foundGetter;
    }

    private void evictOnPut(SampleableConcurrentHashMap<String, Getter> getterPerClassCache) {
        this.evictMap(getterPerClassCache, this.maxGetterPerClassCount, this.afterEvictionGetterPerClassCount);
        this.evictMap(this.getterCache, this.maxClassCount, this.afterEvictionClassCount);
    }

    private void evictMap(SampleableConcurrentHashMap<?, ?> map, int triggeringEvictionSize, int afterEvictionSize) {
        map.purgeStaleEntries();
        int mapSize = map.size();
        if (mapSize - triggeringEvictionSize >= 0) {
            for (SampleableConcurrentHashMap.SamplingEntry entry : map.getRandomSamples(mapSize - afterEvictionSize)) {
                map.remove(entry.getEntryKey());
            }
        }
    }

    int getClassCacheSize() {
        return this.getterCache.size();
    }

    int getGetterPerClassCacheSize(Class clazz) {
        SampleableConcurrentHashMap cacheForClass = (SampleableConcurrentHashMap)this.getterCache.get(clazz);
        return cacheForClass != null ? cacheForClass.size() : -1;
    }
}

