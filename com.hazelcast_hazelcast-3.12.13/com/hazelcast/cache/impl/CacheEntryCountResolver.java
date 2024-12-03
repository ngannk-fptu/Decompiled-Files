/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheContext;

public abstract class CacheEntryCountResolver {
    public abstract long getEntryCount();

    public static CacheEntryCountResolver createEntryCountResolver(CacheContext cacheContext) {
        return new CacheContextBackedEntryCountResolver(cacheContext);
    }

    private static class CacheContextBackedEntryCountResolver
    extends CacheEntryCountResolver {
        private final CacheContext cacheContext;

        public CacheContextBackedEntryCountResolver(CacheContext cacheContext) {
            this.cacheContext = cacheContext;
        }

        @Override
        public long getEntryCount() {
            return this.cacheContext.getEntryCount();
        }
    }
}

