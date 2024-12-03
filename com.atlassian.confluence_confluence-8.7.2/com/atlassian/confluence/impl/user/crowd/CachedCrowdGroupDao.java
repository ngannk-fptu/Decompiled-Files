/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Supplier
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.impl.ImmutableAttributes
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.model.group.DelegatingGroupWithAttributes
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.group.InternalGroup
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.util.BatchResult
 *  io.atlassian.fugue.Option
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.impl.user.crowd;

import com.atlassian.cache.Supplier;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCache;
import com.atlassian.confluence.impl.cache.tx.TransactionAwareCacheFactory;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdEntityCacheKey;
import com.atlassian.confluence.impl.user.crowd.CachedCrowdInternalDirectoryGroup;
import com.atlassian.confluence.impl.user.crowd.hibernate.InternalGroupDao;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.impl.ImmutableAttributes;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.model.group.DelegatingGroupWithAttributes;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.group.InternalGroup;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.util.BatchResult;
import io.atlassian.fugue.Option;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CachedCrowdGroupDao
implements InternalGroupDao<InternalGroup>,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CachedCrowdGroupDao.class);
    private final InternalGroupDao<InternalGroup> delegate;
    private final TransactionAwareCacheFactory cacheFactory;

    public CachedCrowdGroupDao(InternalGroupDao<InternalGroup> delegate, TransactionAwareCacheFactory cacheFactory) {
        this.delegate = delegate;
        this.cacheFactory = cacheFactory;
    }

    public void afterPropertiesSet() throws Exception {
        this.getGroupCache();
        this.getGroupAttributeCache();
    }

    private TransactionAwareCache<CachedCrowdEntityCacheKey, Option<InternalDirectoryGroup>> getGroupCache() {
        return CoreCache.CROWD_GROUPS_BY_NAME.resolve(this.cacheFactory::getTxCache);
    }

    private Option<InternalDirectoryGroup> findGroupInternal(CachedCrowdEntityCacheKey key) {
        try {
            return Option.some((Object)new CachedCrowdInternalDirectoryGroup(this.delegate.findByName(key.getDirectoryId(), key.getName())));
        }
        catch (GroupNotFoundException e) {
            return Option.none();
        }
    }

    private TransactionAwareCache<CachedCrowdEntityCacheKey, Option<ImmutableAttributes>> getGroupAttributeCache() {
        return CoreCache.CROWD_GROUP_ATTRIBUTES_BY_NAME.resolve(this.cacheFactory::getTxCache);
    }

    private Option<ImmutableAttributes> findAttributesInternal(CachedCrowdEntityCacheKey key) {
        try {
            return Option.some((Object)new ImmutableAttributes((Attributes)this.delegate.findByNameWithAttributes(key.getDirectoryId(), key.getName())));
        }
        catch (GroupNotFoundException e) {
            return Option.none();
        }
    }

    private InternalDirectoryGroup findGroup(CachedCrowdEntityCacheKey key) throws GroupNotFoundException {
        return (InternalDirectoryGroup)Objects.requireNonNull(this.getGroupCache().get(key, (Supplier<Option<InternalDirectoryGroup>>)((Supplier)() -> this.findGroupInternal(key)))).getOrThrow(() -> new GroupNotFoundException(key.getName()));
    }

    private Attributes findAttributes(CachedCrowdEntityCacheKey key) throws GroupNotFoundException {
        return (Attributes)Objects.requireNonNull(this.getGroupAttributeCache().get(key, (Supplier<Option<ImmutableAttributes>>)((Supplier)() -> this.findAttributesInternal(key)))).getOrThrow(() -> new GroupNotFoundException(key.getName()));
    }

    public InternalDirectoryGroup findByName(long directoryId, String name) throws GroupNotFoundException {
        return this.findGroup(new CachedCrowdEntityCacheKey(directoryId, name));
    }

    public GroupWithAttributes findByNameWithAttributes(long directoryId, String name) throws GroupNotFoundException {
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(directoryId, name);
        InternalDirectoryGroup group = this.findGroup(key);
        Attributes attributes = this.findAttributes(key);
        return new DelegatingGroupWithAttributes((Group)group, attributes);
    }

    public BatchResult<Group> addAll(Set<? extends Group> groups) throws DirectoryNotFoundException {
        for (Group group : groups) {
            this.removeFromCaches(new CachedCrowdEntityCacheKey(group));
        }
        return this.delegate.addAll(groups);
    }

    @Override
    public InternalGroup add(Group group) throws DirectoryNotFoundException, InvalidGroupException {
        return this.add(group, false);
    }

    @Override
    public InternalGroup addLocal(Group group) throws DirectoryNotFoundException, InvalidGroupException {
        return this.add(group, true);
    }

    private InternalGroup add(Group group, boolean local) throws DirectoryNotFoundException, InvalidGroupException {
        log.debug("adding single group [ {} ]", (Object)group);
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(group);
        this.removeFromCaches(key);
        InternalGroup createdGroup = local ? this.delegate.addLocal(group) : this.delegate.add(group);
        this.getGroupCache().remove(key);
        return createdGroup;
    }

    public Group update(Group group) throws GroupNotFoundException {
        CachedCrowdEntityCacheKey key = new CachedCrowdEntityCacheKey(group);
        this.getGroupCache().remove(key);
        Group updatedGroup = this.delegate.update(group);
        this.getGroupCache().remove(key);
        return updatedGroup;
    }

    public Group rename(Group group, String newName) throws GroupNotFoundException, InvalidGroupException {
        this.removeFromCaches(new CachedCrowdEntityCacheKey(group));
        this.removeFromCaches(new CachedCrowdEntityCacheKey(group.getDirectoryId(), newName));
        return this.delegate.rename(group, newName);
    }

    public void storeAttributes(Group group, Map<String, Set<String>> attributes) throws GroupNotFoundException {
        this.getGroupAttributeCache().remove(new CachedCrowdEntityCacheKey(group));
        this.delegate.storeAttributes(group, attributes);
    }

    public void removeAttribute(Group group, String attributeName) throws GroupNotFoundException {
        this.getGroupAttributeCache().remove(new CachedCrowdEntityCacheKey(group));
        this.delegate.removeAttribute(group, attributeName);
    }

    public void remove(Group group) throws GroupNotFoundException {
        this.removeFromCaches(new CachedCrowdEntityCacheKey(group));
        this.delegate.remove(group);
    }

    public <T> List<T> search(long directoryId, EntityQuery<T> query) {
        return this.delegate.search(directoryId, query);
    }

    @Override
    public InternalGroup internalFindByName(long directoryId, String groupName) throws GroupNotFoundException {
        return this.delegate.internalFindByName(directoryId, groupName);
    }

    @Override
    public InternalGroup internalFindByGroup(Group group) throws GroupNotFoundException {
        return this.delegate.internalFindByGroup(group);
    }

    @Override
    public void removeAllGroups(long directoryId) {
        this.getGroupCache().removeAll();
        this.getGroupAttributeCache().removeAll();
        this.delegate.removeAllGroups(directoryId);
    }

    public BatchResult<String> removeAllGroups(long directoryId, Set<String> groupNames) {
        for (String groupName : groupNames) {
            this.removeFromCaches(new CachedCrowdEntityCacheKey(directoryId, groupName));
        }
        return this.delegate.removeAllGroups(directoryId, groupNames);
    }

    public Set<String> getAllExternalIds(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getAllExternalIds(directoryId);
    }

    public long getGroupCount(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getGroupCount(directoryId);
    }

    public Set<String> getLocalGroupNames(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getLocalGroupNames(directoryId);
    }

    public Map<String, String> findByExternalIds(long directoryId, Set<String> externalIds) {
        return this.delegate.findByExternalIds(directoryId, externalIds);
    }

    public Map<String, String> findExternalIdsByNames(long directoryId, Set<String> groupNames) {
        return this.delegate.findExternalIdsByNames(directoryId, groupNames);
    }

    public long getExternalGroupCount(long directoryId) throws DirectoryNotFoundException {
        return this.delegate.getExternalGroupCount(directoryId);
    }

    private void removeFromCaches(CachedCrowdEntityCacheKey cacheKey) {
        this.getGroupCache().remove(cacheKey);
        this.getGroupAttributeCache().remove(cacheKey);
    }
}

