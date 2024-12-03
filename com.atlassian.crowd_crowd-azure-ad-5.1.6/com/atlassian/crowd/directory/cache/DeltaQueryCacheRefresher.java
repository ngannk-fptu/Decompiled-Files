/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.util.EqualityUtil
 *  com.google.common.base.Strings
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.cache;

import com.atlassian.crowd.directory.AzureAdDirectory;
import com.atlassian.crowd.directory.cache.BackgroundQueriesProcessor;
import com.atlassian.crowd.directory.cache.DeltaQuerySyncTokenHolder;
import com.atlassian.crowd.directory.rest.mapper.DeltaQueryResult;
import com.atlassian.crowd.directory.rest.util.ThrowingMapMergeOperatorUtil;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupWithMembershipChanges;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.util.EqualityUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeltaQueryCacheRefresher
implements CacheRefresher {
    private static final Logger log = LoggerFactory.getLogger(DeltaQueryCacheRefresher.class);
    protected final AzureAdDirectory azureAdDirectory;

    public DeltaQueryCacheRefresher(AzureAdDirectory remoteDirectory) {
        this.azureAdDirectory = remoteDirectory;
    }

    public CacheSynchronisationResult synchroniseAll(DirectoryCache directoryCache) throws OperationFailedException {
        try (BackgroundQueriesProcessor processor = new BackgroundQueriesProcessor("DeltaQueryCacheRefresher-" + this.azureAdDirectory.getDirectoryId(), this::getUsersFromDeltaQuery, this.azureAdDirectory::performGroupsDeltaQuery);){
            Date syncStartDate = new Date();
            DeltaQueryResult<UserWithAttributes> allUsers = this.synchroniseAllUsers(directoryCache, processor.getUsers(), syncStartDate);
            PartialSynchronisationResult<GroupWithMembershipChanges> allGroups = this.synchroniseAllGroups(directoryCache, processor.getGroups(), syncStartDate);
            this.synchroniseAllMemberships(directoryCache, allUsers, allGroups.getResults());
            CacheSynchronisationResult cacheSynchronisationResult = new CacheSynchronisationResult(true, new DeltaQuerySyncTokenHolder(allUsers.getSyncToken().orElse(null), allGroups.getSyncToken().orElse(null)).serialize());
            return cacheSynchronisationResult;
        }
    }

    protected DeltaQueryResult<UserWithAttributes> getUsersFromDeltaQuery() throws OperationFailedException {
        return this.azureAdDirectory.performUsersDeltaQuery();
    }

    protected DeltaQueryResult<UserWithAttributes> getUserChangesFromDeltaQuery(String userSyncToken) throws OperationFailedException {
        return this.azureAdDirectory.fetchUserChanges(userSyncToken);
    }

    public CacheSynchronisationResult synchroniseChanges(DirectoryCache directoryCache, @Nullable String syncToken) throws OperationFailedException {
        Optional<DeltaQuerySyncTokenHolder> validSyncToken = this.extractSyncToken(syncToken);
        if (!validSyncToken.isPresent()) {
            return CacheSynchronisationResult.FAILURE;
        }
        DeltaQuerySyncTokenHolder deltaQueryTokens = validSyncToken.get();
        try (BackgroundQueriesProcessor processor = new BackgroundQueriesProcessor("DeltaQueryCacheRefresher-" + this.azureAdDirectory.getDirectoryId(), () -> this.getUserChangesFromDeltaQuery(deltaQueryTokens.getUsersDeltaQuerySyncToken()), () -> this.azureAdDirectory.fetchGroupChanges(deltaQueryTokens.getGroupsDeltaQuerySyncToken()));){
            Date syncStartDate = new Date();
            DeltaQueryResult<UserWithAttributes> mappedUsers = this.synchroniseUserChanges(directoryCache, processor.getUsers(), syncStartDate);
            DeltaQueryResult<GroupWithMembershipChanges> mappedGroups = this.synchroniseGroupChanges(directoryCache, processor.getGroups(), syncStartDate);
            this.synchroniseMembershipChanges(directoryCache, mappedUsers, mappedGroups.getChangedEntities());
            CacheSynchronisationResult cacheSynchronisationResult = new CacheSynchronisationResult(true, new DeltaQuerySyncTokenHolder(mappedUsers.getSyncToken().orElse(null), mappedGroups.getSyncToken().orElse(null)).serialize());
            return cacheSynchronisationResult;
        }
    }

    protected Optional<DeltaQuerySyncTokenHolder> extractSyncToken(String syncToken) {
        if (Strings.isNullOrEmpty((String)syncToken)) {
            log.info("Synchronisation token not present, full sync of directory [{}] is necessary before incremental sync is possible.", (Object)this.azureAdDirectory.getDirectoryId());
            return Optional.empty();
        }
        return Optional.of(syncToken).map(DeltaQuerySyncTokenHolder::deserialize).filter(this::isValidToken);
    }

    protected boolean isValidToken(DeltaQuerySyncTokenHolder deltaQueryTokens) {
        if (Strings.isNullOrEmpty((String)deltaQueryTokens.getGroupsDeltaQuerySyncToken())) {
            log.info("Groups delta token not present, falling back to full sync of directory [{}].", (Object)this.azureAdDirectory.getDirectoryId());
            return false;
        }
        if (Strings.isNullOrEmpty((String)deltaQueryTokens.getUsersDeltaQuerySyncToken())) {
            log.info("Users delta token not present, falling back to full sync of directory [{}].", (Object)this.azureAdDirectory.getDirectoryId());
            return false;
        }
        return true;
    }

    private void synchroniseAllMemberships(DirectoryCache directoryCache, DeltaQueryResult<UserWithAttributes> mappedUsers, Collection<GroupWithMembershipChanges> mappedGroups) throws OperationFailedException {
        IdToNameResolver usersResolver = this.usersResolver(mappedUsers, directoryCache);
        IdToNameResolver groupResolver = this.groupResolver(mappedGroups, directoryCache);
        for (GroupWithMembershipChanges group : mappedGroups) {
            log.debug("Synchronising memberships for group '{}'", (Object)group.getName());
            if (this.azureAdDirectory.supportsNestedGroups()) {
                directoryCache.syncGroupMembersForGroup((Group)group, groupResolver.getNames(group.getGroupChildrenIdsToAdd(), true));
            }
            directoryCache.syncUserMembersForGroup((Group)group, usersResolver.getNames(group.getUserChildrenIdsToAdd(), true));
        }
    }

    protected void synchroniseMembershipChanges(DirectoryCache directoryCache, DeltaQueryResult<UserWithAttributes> mappedUsers, Collection<GroupWithMembershipChanges> mappedGroups) throws OperationFailedException {
        IdToNameResolver usersResolver = this.usersResolver(mappedUsers, directoryCache);
        IdToNameResolver groupResolver = this.groupResolver(mappedGroups, directoryCache);
        for (GroupWithMembershipChanges group : mappedGroups) {
            if (this.azureAdDirectory.supportsNestedGroups()) {
                directoryCache.addGroupMembersForGroup((Group)group, groupResolver.getNames(group.getGroupChildrenIdsToAdd(), true));
                directoryCache.deleteGroupMembersForGroup((Group)group, groupResolver.getNames(group.getGroupChildrenIdsToDelete(), false));
            }
            directoryCache.addUserMembersForGroup((Group)group, usersResolver.getNames(group.getUserChildrenIdsToAdd(), true));
            directoryCache.deleteUserMembersForGroup((Group)group, usersResolver.getNames(group.getUserChildrenIdsToDelete(), false));
        }
    }

    protected Set<String> getNames(Map<String, String> idToNameCache, Set<String> idsToResolve, IdToNameProvider findById, boolean failOnNotResolved, String entityType) throws OperationFailedException {
        Sets.SetView difference = Sets.difference(idsToResolve, idToNameCache.keySet());
        if (!difference.isEmpty()) {
            log.debug("Azure AD reported memberships on {}s that were not modified: {}, trying to resolve them from directory cache", (Object)entityType, (Object)difference);
            idToNameCache.putAll(findById.getIdToNames((Set<String>)difference));
            if (failOnNotResolved && !difference.isEmpty()) {
                throw new OperationFailedException("Azure AD reported new memberships on " + entityType + "s that were not returned during " + entityType + " sync. " + StringUtils.capitalize((String)entityType) + "s in question: " + difference);
            }
        }
        return idsToResolve.stream().map(idToNameCache::get).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    protected <T> Map<String, String> mapIdToUniqueNames(Collection<T> entities, Function<T, String> idMapper, Function<T, String> nameMapper, String entityName) {
        ThrowingMapMergeOperatorUtil.mapUniqueNamesToIds(entities, nameMapper, idMapper, entityName);
        return entities.stream().collect(Collectors.toMap(idMapper, nameMapper));
    }

    protected IdToNameResolver usersResolver(DeltaQueryResult<UserWithAttributes> mappedUsers, DirectoryCache directoryCache) {
        Map<String, String> cache = this.mapIdToUniqueNames(mappedUsers.getChangedEntities(), User::getExternalId, Principal::getName, "user");
        return (idsToResolve, failOnNotResolved) -> this.getNames(cache, (Set<String>)Sets.difference((Set)idsToResolve, mappedUsers.getNamelessEntities()).immutableCopy(), arg_0 -> ((DirectoryCache)directoryCache).findUsersByExternalIds(arg_0), failOnNotResolved, "user");
    }

    protected IdToNameResolver groupResolver(Collection<GroupWithMembershipChanges> mappedGroups, DirectoryCache directoryCache) {
        Map<String, String> cache = this.mapIdToUniqueNames(mappedGroups, Group::getExternalId, DirectoryEntity::getName, "group");
        return (idsToResolve, failOnNotResolved) -> this.getNames(cache, idsToResolve, arg_0 -> ((DirectoryCache)directoryCache).findGroupsByExternalIds(arg_0), failOnNotResolved, "group");
    }

    protected DeltaQueryResult<UserWithAttributes> synchroniseUserChanges(DirectoryCache directoryCache, DeltaQueryResult<UserWithAttributes> mappedUsers, Date syncStartDate) throws OperationFailedException {
        this.handleNamelessEntities(mappedUsers, "users");
        directoryCache.deleteCachedUsersByGuid(mappedUsers.getDeletedEntities());
        directoryCache.addOrUpdateCachedUsers(mappedUsers.getChangedEntities(), syncStartDate);
        return mappedUsers;
    }

    protected DeltaQueryResult<GroupWithMembershipChanges> synchroniseGroupChanges(DirectoryCache directoryCache, DeltaQueryResult<GroupWithMembershipChanges> mappedGroups, Date syncStartDate) throws OperationFailedException {
        this.handleNamelessEntities(mappedGroups, "groups");
        this.checkNoRenamedGroups(directoryCache, mappedGroups);
        this.checkNoReaddedGroups(directoryCache, mappedGroups);
        directoryCache.deleteCachedGroupsByGuids(mappedGroups.getDeletedEntities());
        directoryCache.addOrUpdateCachedGroups(mappedGroups.getChangedEntities(), syncStartDate);
        return mappedGroups;
    }

    protected void checkNoRenamedGroups(DirectoryCache directoryCache, DeltaQueryResult<GroupWithMembershipChanges> mappedGroups) throws OperationFailedException {
        Map<String, String> newEntitiesExternalIdsToNames = mappedGroups.getChangedEntities().stream().collect(Collectors.toMap(Group::getExternalId, DirectoryEntity::getName));
        Map externalIdsToNames = directoryCache.findGroupsByExternalIds(newEntitiesExternalIdsToNames.keySet());
        Set differences = externalIdsToNames.entrySet().stream().filter(entry -> {
            String matchingChangedEntity = (String)newEntitiesExternalIdsToNames.get(entry.getKey());
            return matchingChangedEntity != null && EqualityUtil.different((String)matchingChangedEntity, (String)((String)entry.getValue()));
        }).collect(Collectors.toSet());
        if (!differences.isEmpty()) {
            log.info("Cannot proceed with incremental synchronisation due to groups with known external ids but unknownnames, falling back to full synchronisation. Groups in question: [{}]", differences);
            throw new OperationFailedException("Cannot proceed with incremental synchronisation due to renamed groups, falling back to full synchronisation.");
        }
    }

    protected void checkNoReaddedGroups(DirectoryCache directoryCache, DeltaQueryResult<GroupWithMembershipChanges> mappedGroups) throws OperationFailedException {
        Map<String, String> newEntitiesNamesToExternalIds = ThrowingMapMergeOperatorUtil.mapUniqueNamesToIds(mappedGroups.getChangedEntities(), DirectoryEntity::getName, Group::getExternalId, "group");
        Map namesToExternalIds = directoryCache.findGroupsExternalIdsByNames(newEntitiesNamesToExternalIds.keySet());
        Set differences = namesToExternalIds.entrySet().stream().filter(entry -> {
            String matchingChangedEntity = (String)newEntitiesNamesToExternalIds.get(entry.getKey());
            return matchingChangedEntity != null && EqualityUtil.different((String)matchingChangedEntity, (String)((String)entry.getValue())) && !mappedGroups.getDeletedEntities().contains(entry.getValue());
        }).collect(Collectors.toSet());
        if (!differences.isEmpty()) {
            log.info("Cannot proceed with incremental synchronisation due to groups readded with known names/duplicates, falling back to full synchronisation. Groups in question: [{}]", differences);
            throw new OperationFailedException("Cannot proceed with incremental synchronisation due to readded/duplicate groups, falling back to full synchronisation.");
        }
    }

    private DeltaQueryResult<UserWithAttributes> synchroniseAllUsers(DirectoryCache directoryCache, DeltaQueryResult<UserWithAttributes> mappedUsers, Date syncStartDate) throws OperationFailedException {
        this.handleNamelessEntities(mappedUsers, "users");
        directoryCache.deleteCachedUsersNotIn(mappedUsers.getChangedEntities(), syncStartDate);
        directoryCache.addOrUpdateCachedUsers(mappedUsers.getChangedEntities(), syncStartDate);
        return mappedUsers;
    }

    private PartialSynchronisationResult<GroupWithMembershipChanges> synchroniseAllGroups(DirectoryCache directoryCache, DeltaQueryResult<GroupWithMembershipChanges> mappedGroups, Date syncStartDate) throws OperationFailedException {
        this.handleNamelessEntities(mappedGroups, "groups");
        directoryCache.deleteCachedGroupsNotInByExternalId(mappedGroups.getChangedEntities(), syncStartDate);
        directoryCache.addOrUpdateCachedGroups(mappedGroups.getChangedEntities(), syncStartDate);
        return new PartialSynchronisationResult(mappedGroups.getChangedEntities(), (String)mappedGroups.getSyncToken().orElse(null));
    }

    protected <T> void handleNamelessEntities(DeltaQueryResult<T> mappedEntities, String entityType) {
        Sets.SetView difference = Sets.difference(mappedEntities.getNamelessEntities(), mappedEntities.getDeletedEntities());
        if (!difference.isEmpty()) {
            log.warn("Azure AD returned the following {} without ids: {}", (Object)entityType, (Object)difference);
        }
    }

    protected static interface IdToNameProvider {
        public Map<String, String> getIdToNames(Set<String> var1) throws OperationFailedException;
    }

    protected static interface IdToNameResolver {
        public Set<String> getNames(Set<String> var1, boolean var2) throws OperationFailedException;
    }
}

