/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 */
package net.java.ao.schema.info;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.java.ao.RawEntity;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.EntityInfoResolver;
import net.java.ao.schema.info.EntityInfoResolverWrapper;

public class CachingEntityInfoResolver
extends EntityInfoResolverWrapper
implements EntityInfoResolver {
    private final LoadingCache<Class<? extends RawEntity<?>>, EntityInfo> cache = CacheBuilder.newBuilder().build(new CacheLoader<Class<? extends RawEntity<?>>, EntityInfo>(){

        public EntityInfo load(Class type) throws Exception {
            return CachingEntityInfoResolver.super.resolve(type);
        }
    });

    public CachingEntityInfoResolver(EntityInfoResolver delegate) {
        super(delegate);
    }

    @Override
    public <T extends RawEntity<K>, K> EntityInfo<T, K> resolve(Class<T> type) {
        return (EntityInfo)this.cache.getUnchecked(type);
    }
}

