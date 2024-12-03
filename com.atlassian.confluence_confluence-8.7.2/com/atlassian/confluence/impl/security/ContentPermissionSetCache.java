/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.security;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.security.CachingInheritedContentPermissionManager;
import com.atlassian.confluence.impl.security.NeverPermittedContentPermissionSet;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class ContentPermissionSetCache
implements CachingInheritedContentPermissionManager.PermissionsCache {
    private final Resolver<Long, ContentPermissionSet> resolver;
    private final TransactionAwareCache<Long, List<Cacheable<Long, ContentPermissionSet>>> cache;

    public ContentPermissionSetCache(TransactionAwareCache<Long, List<Cacheable<Long, ContentPermissionSet>>> cache, Resolver<Long, ContentPermissionSet> resolver) {
        this.resolver = resolver;
        this.cache = cache;
    }

    @Override
    public List<ContentPermissionSet> getOrLoad(ContentId contentId, java.util.function.Supplier<List<ContentPermissionSet>> loader) {
        List<Cacheable<Long, ContentPermissionSet>> cachedPermissionSets = this.cache.get(contentId.asLong(), (Supplier<List<Cacheable<Long, ContentPermissionSet>>>)((Supplier)() -> ContentPermissionSetCache.cacheableOf((List)loader.get())));
        return (List)cachedPermissionSets.stream().map(cachedValue -> cachedValue.resolve(this.resolver)).flatMap(opt -> opt.map(Stream::of).orElseGet(Stream::empty)).collect(ImmutableList.toImmutableList());
    }

    @Override
    public void remove(Iterable<ContentId> contentIds) {
        contentIds.forEach(id -> this.cache.remove(id.asLong()));
    }

    private static List<Cacheable<Long, ContentPermissionSet>> cacheableOf(List<ContentPermissionSet> permissionSets) {
        return (List)permissionSets.stream().map(cps -> ContentPermissionSetCache.cacheableOf(cps)).collect(ImmutableList.toImmutableList());
    }

    private static Cacheable<Long, ContentPermissionSet> cacheableOf(ContentPermissionSet permissionSet) {
        return permissionSet instanceof NeverPermittedContentPermissionSet ? new CacheByReference(permissionSet) : new CacheById(permissionSet.getId());
    }

    private static class CacheByReference<K, V extends Serializable>
    implements Cacheable<K, V> {
        private final V object;

        private CacheByReference(V object) {
            this.object = object;
        }

        @Override
        public Optional<V> resolve(Resolver<K, V> resolver) {
            return Optional.of(this.object);
        }
    }

    private static class CacheById<K extends Serializable, V>
    implements Cacheable<K, V> {
        private final K id;

        private CacheById(K id) {
            this.id = id;
        }

        @Override
        public Optional<V> resolve(Resolver<K, V> resolver) {
            return resolver.resolveById(this.id);
        }
    }

    @FunctionalInterface
    static interface Cacheable<K, V>
    extends Serializable {
        public Optional<V> resolve(Resolver<K, V> var1);
    }

    public static interface Resolver<K, V> {
        public Optional<V> resolveById(K var1);
    }
}

