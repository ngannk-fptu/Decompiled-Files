/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultAccumulatorInfoSupplier
implements AccumulatorInfoSupplier {
    private static final ConstructorFunction<String, ConcurrentMap<String, AccumulatorInfo>> INFO_CTOR = new ConstructorFunction<String, ConcurrentMap<String, AccumulatorInfo>>(){

        @Override
        public ConcurrentMap<String, AccumulatorInfo> createNew(String arg) {
            return new ConcurrentHashMap<String, AccumulatorInfo>();
        }
    };
    private final ConcurrentMap<String, ConcurrentMap<String, AccumulatorInfo>> cacheInfoPerMap = new ConcurrentHashMap<String, ConcurrentMap<String, AccumulatorInfo>>();

    @Override
    public AccumulatorInfo getAccumulatorInfoOrNull(String mapName, String cacheId) {
        ConcurrentMap cacheToInfoMap = (ConcurrentMap)this.cacheInfoPerMap.get(mapName);
        if (cacheToInfoMap == null) {
            return null;
        }
        return (AccumulatorInfo)cacheToInfoMap.get(cacheId);
    }

    @Override
    public void putIfAbsent(String mapName, String cacheId, AccumulatorInfo info) {
        ConcurrentMap<String, AccumulatorInfo> cacheToInfoMap = ConcurrencyUtil.getOrPutIfAbsent(this.cacheInfoPerMap, mapName, INFO_CTOR);
        cacheToInfoMap.putIfAbsent(cacheId, info);
    }

    @Override
    public void remove(String mapName, String cacheId) {
        ConcurrentMap cacheToInfoMap = (ConcurrentMap)this.cacheInfoPerMap.get(mapName);
        if (cacheToInfoMap == null) {
            return;
        }
        cacheToInfoMap.remove(cacheId);
    }

    @Override
    public ConcurrentMap<String, ConcurrentMap<String, AccumulatorInfo>> getAll() {
        return this.cacheInfoPerMap;
    }

    public int accumulatorInfoCountOfMap(String mapName) {
        ConcurrentMap accumulatorInfo = (ConcurrentMap)this.cacheInfoPerMap.get(mapName);
        if (accumulatorInfo == null) {
            return 0;
        }
        return accumulatorInfo.size();
    }
}

