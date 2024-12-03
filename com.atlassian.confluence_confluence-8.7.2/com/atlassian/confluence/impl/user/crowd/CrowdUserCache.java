/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Cache;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdEntityCacheKey;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CrowdUserCache {
    private static final Logger log = LoggerFactory.getLogger(CrowdUserCache.class);
    private final Cache<CachedCrowdEntityCacheKey, Option<TimestampedUser>> userCache;

    public CrowdUserCache(Cache<CachedCrowdEntityCacheKey, Option<TimestampedUser>> userCache) {
        this.userCache = userCache;
    }

    public Collection<? extends TimestampedUser> findByNames(long directoryId, Collection<String> userNames, BulkLoader loader) {
        return (Collection)this.userCache.getBulk(this.asCacheKeys(directoryId, userNames), keys -> this.invokeLoader(loader, (Set<CachedCrowdEntityCacheKey>)keys, directoryId)).values().stream().flatMap(Option::toStream).collect(ImmutableList.toImmutableList());
    }

    private Map<CachedCrowdEntityCacheKey, Option<TimestampedUser>> invokeLoader(BulkLoader loader, Set<CachedCrowdEntityCacheKey> keys, long directoryId) {
        Set<String> userNames = CrowdUserCache.asUserNames(keys);
        log.debug("Invoking bulk loader for {} uncached users {}", (Object)userNames.size(), userNames);
        Collection loadedUsers = (Collection)loader.apply(directoryId, userNames);
        log.debug("Bulk loader returned {} users {}", (Object)loadedUsers.size(), (Object)Collections2.transform((Collection)loadedUsers, DirectoryEntity::getName));
        return CrowdUserCache.withMissing(CrowdUserCache.asMap(loadedUsers), keys);
    }

    private static <K, V> Map<K, Option<V>> withMissing(Map<K, Option<V>> valueMap, Set<K> requestedKeys) {
        return ImmutableMap.builder().putAll(valueMap).putAll(CrowdUserCache.asMapOfEmptyOptions(Sets.difference(requestedKeys, valueMap.keySet()))).build();
    }

    private static <K, V> Map<K, Option<V>> asMapOfEmptyOptions(Set<K> keys) {
        return Maps.asMap(keys, key -> Option.none());
    }

    private Set<CachedCrowdEntityCacheKey> asCacheKeys(long directoryId, Collection<String> userNames) {
        return (Set)userNames.stream().map(username -> new CachedCrowdEntityCacheKey(directoryId, (String)username)).collect(ImmutableSet.toImmutableSet());
    }

    private static Set<String> asUserNames(Set<CachedCrowdEntityCacheKey> keys) {
        return (Set)keys.stream().map(CachedCrowdEntityCacheKey::getName).collect(ImmutableSet.toImmutableSet());
    }

    private static Map<CachedCrowdEntityCacheKey, Option<TimestampedUser>> asMap(Collection<? extends TimestampedUser> users) {
        return (Map)users.stream().collect(ImmutableMap.toImmutableMap(CachedCrowdEntityCacheKey::new, Option::some));
    }

    @FunctionalInterface
    public static interface BulkLoader
    extends BiFunction<Long, Collection<String>, Collection<? extends TimestampedUser>> {
    }
}

