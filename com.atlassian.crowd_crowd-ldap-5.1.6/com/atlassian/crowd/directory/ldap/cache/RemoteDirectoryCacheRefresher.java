/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.cache.AbstractCacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplateWithAttributes
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.ldap.cache;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.AbstractCacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplateWithAttributes;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteDirectoryCacheRefresher
extends AbstractCacheRefresher
implements CacheRefresher {
    private static final Logger log = LoggerFactory.getLogger(RemoteDirectoryCacheRefresher.class);

    public RemoteDirectoryCacheRefresher(RemoteDirectory remoteDirectory) {
        super(remoteDirectory);
    }

    public CacheSynchronisationResult synchroniseChanges(DirectoryCache directoryCache, String syncToken) throws OperationFailedException {
        return CacheSynchronisationResult.FAILURE;
    }

    protected List<UserWithAttributes> findAllRemoteUsers(boolean withAttributes) throws OperationFailedException {
        long start = System.currentTimeMillis();
        log.debug("loading remote users");
        List<UserWithAttributes> users = withAttributes ? this.remoteDirectory.searchUsers(this.getUserQuery(UserWithAttributes.class)) : this.remoteDirectory.searchUsers(this.getUserQuery(User.class)).stream().map(UserTemplateWithAttributes::toUserWithNoAttributes).collect(Collectors.toList());
        log.info("found [ {} ] remote users in [ {} ms ]", (Object)users.size(), (Object)(System.currentTimeMillis() - start));
        return users;
    }

    private <T extends User> EntityQuery<T> getUserQuery(Class<T> clazz) {
        return QueryBuilder.queryFor(clazz, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(-1);
    }

    private <T extends Group> EntityQuery<T> getGroupQuery(Class<T> clazz) {
        return QueryBuilder.queryFor(clazz, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).returningAtMost(-1);
    }

    protected List<GroupWithAttributes> findAllRemoteGroups(boolean withAttributes) throws OperationFailedException {
        long start = System.currentTimeMillis();
        log.debug("loading remote groups");
        List<GroupWithAttributes> groups = withAttributes ? this.remoteDirectory.searchGroups(this.getGroupQuery(GroupWithAttributes.class)) : this.remoteDirectory.searchGroups(this.getGroupQuery(Group.class)).stream().map(GroupTemplateWithAttributes::ofGroupWithNoAttributes).collect(Collectors.toList());
        log.info("found [ {} ] remote groups in [ {} ms ]", (Object)groups.size(), (Object)(System.currentTimeMillis() - start));
        return groups;
    }

    protected PartialSynchronisationResult<? extends UserWithAttributes> synchroniseAllUsers(DirectoryCache directoryCache) throws OperationFailedException {
        Date syncStartDate = new Date();
        List<UserWithAttributes> ldapUsers = this.findAllRemoteUsers(this.isUserAttributeSynchronisationEnabled());
        directoryCache.deleteCachedUsersNotIn(ldapUsers, syncStartDate);
        directoryCache.addOrUpdateCachedUsers(ldapUsers, syncStartDate);
        return new PartialSynchronisationResult(ldapUsers);
    }

    protected PartialSynchronisationResult<? extends GroupWithAttributes> synchroniseAllGroups(DirectoryCache directoryCache) throws OperationFailedException {
        Date syncStartDate = new Date();
        List groups = DirectoryEntities.filterOutDuplicates(this.findAllRemoteGroups(this.isGroupAttributeSynchronisationEnabled()));
        directoryCache.deleteCachedGroupsNotIn(GroupType.GROUP, groups, syncStartDate);
        directoryCache.addOrUpdateCachedGroups((Collection)groups, syncStartDate);
        return new PartialSynchronisationResult((Collection)groups);
    }
}

