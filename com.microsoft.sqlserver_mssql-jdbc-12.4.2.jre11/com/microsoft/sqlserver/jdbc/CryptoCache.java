/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.CryptoMetadata;
import mssql.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;

class CryptoCache {
    static final int MAX_WEIGHTED_CAPACITY = 2300;
    private ConcurrentLinkedHashMap<String, ConcurrentLinkedHashMap<String, CryptoMetadata>> paramMap = new ConcurrentLinkedHashMap.Builder().maximumWeightedCapacity(2300L).build();

    CryptoCache() {
    }

    ConcurrentLinkedHashMap<String, ConcurrentLinkedHashMap<String, CryptoMetadata>> getParamMap() {
        return this.paramMap;
    }

    ConcurrentLinkedHashMap<String, CryptoMetadata> getCacheEntry(String cacheLookupKey) {
        return this.paramMap.get(cacheLookupKey);
    }

    void addParamEntry(String key, ConcurrentLinkedHashMap<String, CryptoMetadata> value) {
        this.paramMap.put(key, value);
    }

    void removeParamEntry(String cacheLookupKey) {
        this.paramMap.remove(cacheLookupKey);
    }
}

