/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.security.DefaultSpacePermissionManager;
import com.atlassian.confluence.security.persistence.dao.SpacePermissionDao;
import com.atlassian.confluence.spaces.Space;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Deprecated
@Internal
public class SpacePermissionGroupNamesCache {
    private static final Logger log = LoggerFactory.getLogger(SpacePermissionGroupNamesCache.class);
    private static final String CACHE_NAME = SpacePermissionGroupNamesCache.class.getName();
    private final SpacePermissionDao spacePermissionDao;
    private final Cache<CacheKey, Set<String>> cache;

    private SpacePermissionGroupNamesCache(SpacePermissionDao spacePermissionDao, Cache<CacheKey, Set<String>> cache) {
        this.spacePermissionDao = (SpacePermissionDao)Preconditions.checkNotNull((Object)spacePermissionDao);
        this.cache = (Cache)Preconditions.checkNotNull(cache);
    }

    public static SpacePermissionGroupNamesCache create(SpacePermissionDao spacePermissionDao, CacheFactory cacheFactory) {
        return new SpacePermissionGroupNamesCache(spacePermissionDao, (Cache<CacheKey, Set<String>>)cacheFactory.getCache(CACHE_NAME));
    }

    public @NonNull Set<String> getGroupNamesWithPermission(@Nullable Space space, String permissionType) {
        CacheKey cacheKey = new CacheKey(permissionType, space);
        Set cachedGroupNames = (Set)this.cache.get((Object)cacheKey);
        if (cachedGroupNames == null) {
            ImmutableSet groupNames = ImmutableSet.copyOf(DefaultSpacePermissionManager.queryDaoForGroupNamesWithPermission(space, permissionType, this.spacePermissionDao));
            log.debug("Cache miss on group names with permission {}, recaching from DAO results {}", (Object)cacheKey, (Object)groupNames);
            this.cache.putIfAbsent((Object)cacheKey, (Object)groupNames);
            return groupNames;
        }
        log.trace("Cache hit for group names for {}: {}", (Object)cacheKey, (Object)cachedGroupNames);
        return cachedGroupNames;
    }

    public void invalidate(String permissionType, @Nullable Space space) {
        CacheKey cacheKey = new CacheKey(permissionType, space);
        log.debug("Invalidating group names cache for {}", (Object)cacheKey);
        this.cache.remove((Object)cacheKey);
    }

    public void invalidateAll() {
        log.debug("Invalidating group names cache");
        this.cache.removeAll();
    }

    static final class CacheKey
    implements Serializable {
        final @NonNull String permissionType;
        final @Nullable String spaceKey;

        CacheKey(String permissionType, @Nullable Space space) {
            this.permissionType = permissionType;
            this.spaceKey = space == null ? null : space.getKey();
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheKey that = (CacheKey)o;
            return Objects.equals(this.permissionType, that.permissionType) && Objects.equals(this.spaceKey, that.spaceKey);
        }

        public int hashCode() {
            return Objects.hash(this.permissionType, this.spaceKey);
        }

        public String toString() {
            return "{permissionType='" + this.permissionType + "', spaceKey='" + this.spaceKey + "'}";
        }
    }
}

