/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdInternalDirectoryGroup;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdMembershipCacheKey;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdMembershipDao;
import com.atlassian.confluence.impl.user.crowd.GroupMembershipCache;
import com.atlassian.confluence.user.crowd.NameUtils;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DefaultGroupMembershipCache
implements GroupMembershipCache {
    private final TransactionAwareCache<CachedCrowdMembershipCacheKey, Map<String, InternalDirectoryGroup>> cache;

    DefaultGroupMembershipCache(TransactionAwareCache<CachedCrowdMembershipCacheKey, Map<String, InternalDirectoryGroup>> cache) {
        this.cache = cache;
    }

    public static GroupMembershipCache createParentGroupMembershipCache(TransactionAwareCacheFactory cacheFactory) {
        return new DefaultGroupMembershipCache(cacheFactory.getTxCache(CachedCrowdMembershipDao.class.getName() + ".GROUP_PARENT_CACHE"));
    }

    public static GroupMembershipCache createChildGroupMembershipCache(TransactionAwareCacheFactory cacheFactory) {
        return new DefaultGroupMembershipCache(cacheFactory.getTxCache(CachedCrowdMembershipDao.class.getName() + ".GROUP_CHILD_CACHE"));
    }

    @Override
    public List<InternalDirectoryGroup> getGroupsForGroup(long directoryId, String groupName) {
        Map<String, InternalDirectoryGroup> memberships = this.cache.get(DefaultGroupMembershipCache.cacheKey(directoryId, groupName));
        if (memberships != null) {
            return ImmutableList.copyOf(memberships.values());
        }
        return null;
    }

    @Override
    public List<InternalDirectoryGroup> getGroupsForGroup(long directoryId, String groupName, java.util.function.Supplier<List<InternalDirectoryGroup>> groupLoader) {
        Collection<InternalDirectoryGroup> groups = this.cache.get(DefaultGroupMembershipCache.cacheKey(directoryId, groupName), (Supplier<Map<String, InternalDirectoryGroup>>)((Supplier)() -> NameUtils.canonicalMappingForGroups(DefaultGroupMembershipCache.cacheable((Iterable)groupLoader.get())))).values();
        return ImmutableList.copyOf(groups);
    }

    private static CachedCrowdMembershipCacheKey cacheKey(long directoryId, String groupName) {
        return CachedCrowdMembershipCacheKey.forGroup(directoryId, groupName);
    }

    @Override
    public void removeGroupGroupMemberships(long directoryId, String groupName) {
        this.cache.remove(DefaultGroupMembershipCache.cacheKey(directoryId, groupName));
    }

    @Override
    public void removeAllGroupMemberships(Group group) {
        long directoryId = group.getDirectoryId();
        for (CachedCrowdMembershipCacheKey key : this.cache.getKeys()) {
            if (key.getDirectoryId() != directoryId) continue;
            Map<String, InternalDirectoryGroup> cachedMemberships = this.cache.get(key);
            if (cachedMemberships != null && cachedMemberships.containsKey(NameUtils.getCanonicalName(group))) {
                this.cache.remove(key);
            }
            if (!key.matches(group)) continue;
            this.cache.remove(key);
        }
    }

    @Override
    public void removeAllDirectoryMemberships(long directoryId) {
        for (CachedCrowdMembershipCacheKey key : this.cache.getKeys()) {
            if (key.getDirectoryId() != directoryId) continue;
            this.cache.remove(key);
        }
    }

    private static Iterable<InternalDirectoryGroup> cacheable(Iterable<InternalDirectoryGroup> storedGroups) {
        return Iterables.transform(storedGroups, CachedCrowdInternalDirectoryGroup::new);
    }
}

