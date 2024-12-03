/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdMembershipCacheKey;
import com.atlassian.confluence.impl.user.crowd.MembershipCache;
import com.atlassian.confluence.user.crowd.NameUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

public final class DefaultMembershipCache
implements MembershipCache {
    private final TransactionAwareCacheFactory cacheFactory;

    public DefaultMembershipCache(TransactionAwareCacheFactory cacheFactory) {
        this.cacheFactory = cacheFactory;
    }

    public Boolean isUserDirectMember(long directoryId, String userName, String groupName) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forUser(directoryId, userName));
        return cachedMemberships == null ? null : Boolean.valueOf(cachedMemberships.containsKey(NameUtils.getCanonicalName(groupName)));
    }

    @Override
    public boolean isUserDirectMember(long directoryId, String userName, String groupName, java.util.function.Supplier<Iterable<String>> groupMembershipSupplier) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forUser(directoryId, userName), (Supplier<Map<String, String>>)((Supplier)() -> NameUtils.canonicalMappingForNames((Iterable)groupMembershipSupplier.get())));
        return cachedMemberships.containsKey(NameUtils.getCanonicalName(groupName));
    }

    public Boolean isGroupDirectMember(long directoryId, String groupName, String parentGroupName) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forGroup(directoryId, groupName));
        return cachedMemberships == null ? null : Boolean.valueOf(cachedMemberships.containsKey(NameUtils.getCanonicalName(parentGroupName)));
    }

    @Override
    public boolean isGroupDirectMember(long directoryId, String childGroupName, String parentGroupName, java.util.function.Supplier<Iterable<String>> groupMembershipSupplier) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forGroup(directoryId, childGroupName), (Supplier<Map<String, String>>)((Supplier)() -> NameUtils.canonicalMappingForNames((Iterable)groupMembershipSupplier.get())));
        return cachedMemberships.containsKey(NameUtils.getCanonicalName(parentGroupName));
    }

    public List<String> getGroupsForUser(long directoryId, String userName) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forUser(directoryId, userName));
        return cachedMemberships == null ? null : ImmutableList.copyOf(cachedMemberships.values());
    }

    @Override
    public List<String> getGroupsForUser(long directoryId, String userName, java.util.function.Supplier<List<String>> valueSupplier) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forUser(directoryId, userName), (Supplier<Map<String, String>>)((Supplier)() -> NameUtils.canonicalMappingForNames((Iterable)valueSupplier.get())));
        return ImmutableList.copyOf(cachedMemberships.values());
    }

    @Override
    public List<String> getGroupsForGroup(long directoryId, String groupName) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forGroup(directoryId, groupName));
        return cachedMemberships == null ? null : ImmutableList.copyOf(cachedMemberships.values());
    }

    @Override
    public List<String> getGroupsForGroup(long directoryId, String groupName, java.util.function.Supplier<List<String>> valueSupplier) {
        Map<String, String> cachedMemberships = this.getCache().get(CachedCrowdMembershipCacheKey.forGroup(directoryId, groupName), (Supplier<Map<String, String>>)((Supplier)() -> NameUtils.canonicalMappingForNames((Iterable)valueSupplier.get())));
        return ImmutableList.copyOf(cachedMemberships.values());
    }

    @Override
    public void removeUserGroupMemberships(long directoryId, String userName) {
        CachedCrowdMembershipCacheKey cacheKey = CachedCrowdMembershipCacheKey.forUser(directoryId, userName);
        this.getCache().remove(cacheKey);
    }

    @Override
    public void removeGroupGroupMemberships(long directoryId, String groupName) {
        CachedCrowdMembershipCacheKey cacheKey = CachedCrowdMembershipCacheKey.forGroup(directoryId, groupName);
        this.getCache().remove(cacheKey);
    }

    @Override
    public void removeAllUserMemberships(long directoryId, String userName) {
        for (CachedCrowdMembershipCacheKey key : this.getCache().getKeys()) {
            if (key.getDirectoryId() != directoryId || key.getType() != CachedCrowdMembershipCacheKey.MemberType.GROUPS_FOR_USER || !key.getName().equals(NameUtils.getCanonicalName(userName))) continue;
            this.getCache().remove(key);
        }
    }

    @Override
    public void removeAllGroupMemberships(long directoryId, String groupName) {
        for (CachedCrowdMembershipCacheKey key : this.getCache().getKeys()) {
            if (key.getDirectoryId() != directoryId) continue;
            Map<String, String> cachedMemberships = this.getCache().get(key);
            if (cachedMemberships != null && cachedMemberships.containsKey(NameUtils.getCanonicalName(groupName))) {
                this.getCache().remove(key);
            }
            if (key.getType() != CachedCrowdMembershipCacheKey.MemberType.GROUPS_FOR_GROUP || !key.getName().equals(NameUtils.getCanonicalName(groupName))) continue;
            this.getCache().remove(key);
        }
    }

    @Override
    public void removeAllDirectoryMemberships(long directoryId) {
        for (CachedCrowdMembershipCacheKey key : this.getCache().getKeys()) {
            if (key.getDirectoryId() != directoryId) continue;
            this.getCache().remove(key);
        }
    }

    private TransactionAwareCache<CachedCrowdMembershipCacheKey, Map<String, String>> getCache() {
        return CoreCache.GROUP_MEMBERSHIPS_BY_USER.resolve(this.cacheFactory::getTxCache);
    }
}

