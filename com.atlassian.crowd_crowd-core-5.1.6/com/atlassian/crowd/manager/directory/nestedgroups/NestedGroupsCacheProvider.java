/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.google.common.base.Throwables
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 */
package com.atlassian.crowd.manager.directory.nestedgroups;

import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.google.common.base.Throwables;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NestedGroupsCacheProvider {
    private static final long GROUPS_CACHE_MARGIN_MS = 2000L;
    private static final String SUBGROUPS_CACHE_EXPIRY_MS_PROPERTY = "crowd.manager.directory.searcher.subgroups.cache.expiry.ms";
    private static final String SUBGROUPS_CACHE_MAX_SIZE_PROPERTY = "crowd.manager.directory.searcher.subgroups.cache.maxSize";
    private final long expiryMs;
    private final int maxSize;
    private final Cache<Object, Cache<String, String[]>> subgroupCaches;
    private final Cache<Object, Cache<String, Group>> groupCaches;

    public NestedGroupsCacheProvider(long expiryMs, int maxSize) {
        this.expiryMs = expiryMs;
        this.maxSize = maxSize;
        this.subgroupCaches = this.createCache(expiryMs, false);
        this.groupCaches = this.createCache(expiryMs + 2000L, false);
    }

    private Cache<String, String[]> createSubgroupsCache() {
        return this.createCache(this.expiryMs, true);
    }

    private Cache<String, Group> createGroupsCache() {
        return this.createCache(this.expiryMs + 2000L, true);
    }

    private <F, T> Cache<F, T> createCache(long expiryMs, boolean expireAfterWrite) {
        CacheBuilder builder = CacheBuilder.newBuilder().maximumSize((long)this.maxSize);
        return expireAfterWrite ? builder.expireAfterWrite(expiryMs, TimeUnit.MILLISECONDS).build() : builder.expireAfterAccess(expiryMs, TimeUnit.MILLISECONDS).build();
    }

    protected Cache<String, String[]> getSubgroupsCache(long directoryId, boolean isChildrenQuery, GroupType type) {
        try {
            return (Cache)this.subgroupCaches.get(Arrays.asList(directoryId, isChildrenQuery, type), this::createSubgroupsCache);
        }
        catch (ExecutionException e) {
            Throwables.propagateIfPossible((Throwable)e.getCause());
            throw new RuntimeException(e.getCause());
        }
    }

    protected Cache<String, Group> getGroupsCache(long directoryId, GroupType type) {
        try {
            return (Cache)this.groupCaches.get(Arrays.asList(directoryId, type), this::createGroupsCache);
        }
        catch (ExecutionException e) {
            Throwables.propagateIfPossible((Throwable)e.getCause());
            throw new RuntimeException(e.getCause());
        }
    }

    public static Optional<NestedGroupsCacheProvider> createFromSystemProperties() {
        long expiry = Long.getLong(SUBGROUPS_CACHE_EXPIRY_MS_PROPERTY, 0L);
        int size = Integer.getInteger(SUBGROUPS_CACHE_MAX_SIZE_PROPERTY, 0);
        return expiry > 0L && size > 0 ? Optional.of(new NestedGroupsCacheProvider(expiry, size)) : Optional.empty();
    }
}

