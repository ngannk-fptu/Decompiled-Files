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
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.model.DirectoryEntities
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.Membership
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  com.google.common.primitives.Longs
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.synchronisation.cache;

import com.atlassian.crowd.directory.DeduplicatingDnMapperDecorator;
import com.atlassian.crowd.directory.MicrosoftActiveDirectory;
import com.atlassian.crowd.directory.RFC4519Directory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.ldap.cache.UsnChangedCacheRefresherIncSyncException;
import com.atlassian.crowd.directory.rfc4519.RFC4519DirectoryMembershipsIterableBuilder;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.PartialSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.AbstractCacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.ActiveDirectoryTokenHolder;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.directory.synchronisation.cache.LDAPEntityNameMap;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.model.DirectoryEntities;
import com.atlassian.crowd.model.Tombstone;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.LDAPGroupWithAttributes;
import com.atlassian.crowd.model.group.Membership;
import com.atlassian.crowd.model.user.LDAPUserWithAttributes;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.primitives.Longs;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsnChangedCacheRefresher
extends AbstractCacheRefresher<LDAPGroupWithAttributes>
implements CacheRefresher {
    private static final Logger log = LoggerFactory.getLogger(UsnChangedCacheRefresher.class);
    public static final String PROPERTY_USE_LEGACY_AD_INCREMENTAL_SYNC = "crowd.use.legacy.ad.incremental.sync";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final MicrosoftActiveDirectory activeDirectory;
    final LDAPEntityNameMap<LDAPUserWithAttributes> userMap = new LDAPEntityNameMap();
    private final LDAPEntityNameMap<LDAPGroupWithAttributes> groupMap = new LDAPEntityNameMap();
    private Future<List<LDAPUserWithAttributes>> userListFuture;
    private Future<List<LDAPGroupWithAttributes>> groupListFuture;
    private Set<String> groupsDnsToUpdate = new HashSet<String>();
    private Set<String> primaryGroupSids = new HashSet<String>();
    private final boolean useLegacyADIncrementalSync = Boolean.valueOf(System.getProperty("crowd.use.legacy.ad.incremental.sync", "false"));

    public UsnChangedCacheRefresher(MicrosoftActiveDirectory activeDirectory) {
        super((RemoteDirectory)activeDirectory);
        this.activeDirectory = activeDirectory;
    }

    public CacheSynchronisationResult synchroniseChanges(DirectoryCache directoryCache, @Nullable String highestCommittedUsn) throws OperationFailedException {
        if (!this.isIncrementalSyncEnabled()) {
            return CacheSynchronisationResult.FAILURE;
        }
        ActiveDirectoryTokenHolder maybeTokenHolder = this.deserializeDirectorySyncToken(highestCommittedUsn);
        if (maybeTokenHolder == null) {
            log.info("Synchronisation token not present, full sync of directory [{}] is necessary before incremental sync is possible.", (Object)this.activeDirectory.getDirectoryId());
            return CacheSynchronisationResult.FAILURE;
        }
        String currentInvocationId = this.activeDirectory.fetchInvocationId();
        if (!Objects.equals(currentInvocationId, maybeTokenHolder.getInvocationId())) {
            log.info("Last incremental synchronization took place for AD instance with invocation id '{}', current instance has invocation id of '{}', falling back to full", (Object)maybeTokenHolder.getInvocationId(), (Object)currentInvocationId);
            return CacheSynchronisationResult.FAILURE;
        }
        long lastUsnParsed = maybeTokenHolder.getLastUsnChanged();
        long currentHighestCommittedUSN = this.activeDirectory.fetchHighestCommittedUSN();
        this.synchroniseUserChanges(directoryCache, lastUsnParsed);
        this.synchroniseGroupChanges(directoryCache, lastUsnParsed);
        return new CacheSynchronisationResult(true, this.serializeDirectorySyncToken(currentInvocationId, currentHighestCommittedUSN));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CacheSynchronisationResult synchroniseAll(DirectoryCache directoryCache) throws OperationFailedException {
        ExecutorService queryExecutor = Executors.newFixedThreadPool(3, ThreadFactories.namedThreadFactory((String)"CrowdUsnChangedCacheRefresher"));
        try {
            this.userListFuture = queryExecutor.submit(() -> {
                long start = System.currentTimeMillis();
                log.debug("loading remote users");
                List ldapUsers = this.isUserAttributeSynchronisationEnabled() ? this.activeDirectory.searchUsers(QueryBuilder.queryFor(UserWithAttributes.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(-1)) : this.activeDirectory.searchUsers(QueryBuilder.queryFor(User.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(-1));
                log.info("found [ {} ] remote users in [ {}ms ]", (Object)ldapUsers.size(), (Object)(System.currentTimeMillis() - start));
                return ldapUsers;
            });
            this.groupListFuture = queryExecutor.submit(() -> {
                long start = System.currentTimeMillis();
                log.debug("loading remote groups");
                List ldapGroups = this.isGroupAttributeSynchronisationEnabled() ? this.activeDirectory.searchGroups(QueryBuilder.queryFor(GroupWithAttributes.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).returningAtMost(-1)) : this.activeDirectory.searchGroups(QueryBuilder.queryFor(Group.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).returningAtMost(-1));
                log.info("found [ " + ldapGroups.size() + " ] remote groups in [ " + (System.currentTimeMillis() - start) + "ms ]");
                return ldapGroups;
            });
            super.synchroniseAll(directoryCache);
            if (!this.isIncrementalSyncEnabled()) {
                CacheSynchronisationResult cacheSynchronisationResult = new CacheSynchronisationResult(true, null);
                return cacheSynchronisationResult;
            }
            CacheSynchronisationResult cacheSynchronisationResult = new CacheSynchronisationResult(true, this.serializeDirectorySyncToken(this.activeDirectory.fetchInvocationId(), this.activeDirectory.fetchHighestCommittedUSN()));
            return cacheSynchronisationResult;
        }
        finally {
            queryExecutor.shutdown();
            this.userListFuture = null;
            this.groupListFuture = null;
        }
    }

    protected PartialSynchronisationResult<? extends UserWithAttributes> synchroniseAllUsers(DirectoryCache directoryCache) throws OperationFailedException {
        Date syncStartDate = new Date();
        try {
            List<LDAPUserWithAttributes> ldapUsers = this.userListFuture.get();
            this.userMap.putAll(ldapUsers);
            directoryCache.deleteCachedUsersNotIn(ldapUsers, syncStartDate);
            directoryCache.addOrUpdateCachedUsers(ldapUsers, syncStartDate);
            return new PartialSynchronisationResult(ldapUsers);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationFailedException("background query interrupted", (Throwable)e);
        }
        catch (ExecutionException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    protected PartialSynchronisationResult<LDAPGroupWithAttributes> synchroniseAllGroups(DirectoryCache directoryCache) throws OperationFailedException {
        Date syncStartDate = new Date();
        try {
            List<LDAPGroupWithAttributes> ldapGroups = this.groupListFuture.get();
            ldapGroups = Collections.unmodifiableList(DirectoryEntities.filterOutDuplicates(ldapGroups));
            this.groupMap.putAll(ldapGroups);
            directoryCache.deleteCachedGroupsNotIn(GroupType.GROUP, ldapGroups, syncStartDate);
            directoryCache.addOrUpdateCachedGroups(ldapGroups, syncStartDate);
            return new PartialSynchronisationResult(ldapGroups);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OperationFailedException("background query interrupted", (Throwable)e);
        }
        catch (ExecutionException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private void synchroniseUserChangesUsn(DirectoryCache directoryCache, long highestCommittedUsn) throws OperationFailedException {
        long start = System.currentTimeMillis();
        log.debug("loading changed remote users");
        List<LDAPUserWithAttributes> updatedUsers = this.activeDirectory.findAddedOrUpdatedUsersSince(highestCommittedUsn);
        List<Tombstone> tombstones = this.activeDirectory.findUserTombstonesSince(highestCommittedUsn);
        this.userMap.putAll(updatedUsers);
        log.info("found [ {} ] changed, [ {} ] deleted remote users in [ {}ms ]", new Object[]{updatedUsers.size(), tombstones.size(), System.currentTimeMillis() - start});
        ImmutableSet tombstonesGuids = ImmutableSet.copyOf((Iterable)Iterables.transform(tombstones, Tombstone::getObjectGUID));
        directoryCache.deleteCachedUsersByGuid((Set)tombstonesGuids);
        this.userMap.removeAllByGuid((Collection<String>)tombstonesGuids);
        directoryCache.addOrUpdateCachedUsers(updatedUsers, null);
    }

    private Pair<? extends Set<String>, ? extends Set<String>> validateAndReturnUserGuidsToAddAndDelete(DirectoryCache directoryCache) throws OperationFailedException {
        if (!this.activeDirectory.isUsersExternalIdConfigured()) {
            throw new UsnChangedCacheRefresherIncSyncException("externalId attribute is not configured in directory.");
        }
        log.debug("loading changed users");
        ImmutableSet userGuidsInCache = ImmutableSet.copyOf((Collection)directoryCache.getAllUserGuids());
        if ((long)userGuidsInCache.size() != directoryCache.getUserCount()) {
            throw new UsnChangedCacheRefresherIncSyncException("Cache returned different number of guids and users (possible reason is overlapping guids in cache, most likely null/empty values).");
        }
        if (userGuidsInCache.contains("")) {
            throw new UsnChangedCacheRefresherIncSyncException("Empty user guids returned from cache. Falling back to a full sync in order to populate the guids");
        }
        Set<String> userGuidsInAd = this.activeDirectory.findAllUserGuids();
        log.info("Found [ {} ] user GUIDs in cache, [ {} ] user GUIDs in remote directory", (Object)userGuidsInCache.size(), (Object)userGuidsInAd.size());
        if (userGuidsInAd.contains("")) {
            throw new UsnChangedCacheRefresherIncSyncException("Empty user guids returned from AD. Possible reasons are externalId attribute value in directory configuration or AD server configuration.");
        }
        return Pair.of((Object)Sets.difference(userGuidsInAd, (Set)userGuidsInCache), (Object)Sets.difference((Set)userGuidsInCache, userGuidsInAd));
    }

    private void synchroniseUserChangesGuid(DirectoryCache directoryCache, Long highestCommittedUsn) throws OperationFailedException {
        long start = System.currentTimeMillis();
        Pair<? extends Set<String>, ? extends Set<String>> guidsToAddAndRemove = this.validateAndReturnUserGuidsToAddAndDelete(directoryCache);
        Set guidsToAdd = (Set)guidsToAddAndRemove.getLeft();
        Set guidsToRemove = (Set)guidsToAddAndRemove.getRight();
        ImmutableMap.Builder usersToAddByGuidBuilder = ImmutableMap.builder();
        ImmutableList.Builder usersToUpdateBuilder = ImmutableList.builder();
        for (LDAPUserWithAttributes user : this.activeDirectory.findAddedOrUpdatedUsersSince(highestCommittedUsn)) {
            String externalId2 = user.getExternalId();
            if (StringUtils.isEmpty((CharSequence)externalId2)) {
                throw new UsnChangedCacheRefresherIncSyncException("A null or empty guid retrieved from AD.");
            }
            if (guidsToAdd.contains(externalId2)) {
                usersToAddByGuidBuilder.put((Object)externalId2, (Object)user);
                continue;
            }
            usersToUpdateBuilder.add((Object)user);
        }
        ImmutableMap usersToAddByGuid = usersToAddByGuidBuilder.build();
        ImmutableList usersToUpdate = usersToUpdateBuilder.build();
        log.info("scanned and compared [ {} ] users to delete, [ {} ] users to add, [ {} ] users to update in DB cache in [ {}ms ]", new Object[]{guidsToRemove.size(), guidsToAdd.size(), usersToUpdate.size(), System.currentTimeMillis() - start});
        this.logUserChanges(guidsToRemove, guidsToAdd, (ImmutableMap<String, LDAPUserWithAttributes>)usersToAddByGuid, (ImmutableList<LDAPUserWithAttributes>)usersToUpdate);
        directoryCache.deleteCachedUsersByGuid(guidsToRemove);
        this.userMap.removeAllByGuid(guidsToRemove);
        directoryCache.addOrUpdateCachedUsers((Collection)usersToUpdate, null);
        this.userMap.putAll((Collection<LDAPUserWithAttributes>)usersToUpdate);
        Function<String, LDAPUserWithAttributes> fetchUserByGuid = externalId -> {
            try {
                LDAPUserWithAttributes userFromCache = (LDAPUserWithAttributes)usersToAddByGuid.get(externalId);
                if (userFromCache != null) {
                    return userFromCache;
                }
                log.trace("Detected additional user with external id '{}' (probably added during the sync). Fetching it from AD...", externalId);
                return this.activeDirectory.findUserByExternalId((String)externalId);
            }
            catch (OperationFailedException | UserNotFoundException e) {
                log.warn("Failed to fetch user by objectGUID '{}' from ActiveDirectory", externalId, (Object)e);
                throw new UsnChangedCacheRefresherIncSyncException("Problems while looking up users by objectGUID in ActiveDirectory detected, falling back to a full sync.");
            }
        };
        List newUsers = guidsToAdd.stream().map(fetchUserByGuid).collect(Collectors.toList());
        directoryCache.addOrUpdateCachedUsers(newUsers, null);
        this.userMap.putAll(newUsers);
        for (LDAPUserWithAttributes newUser : newUsers) {
            Set<String> groups = newUser.getValues("memberOf");
            if (groups != null && groups.size() > 0) {
                this.groupsDnsToUpdate.addAll(groups);
            }
            if (!this.activeDirectory.getLdapPropertiesMapper().isPrimaryGroupSupported()) continue;
            Optional<String> primaryGroupSidOrEmpty = this.activeDirectory.getPrimaryGroupSIDOfUser(newUser);
            primaryGroupSidOrEmpty.ifPresent(s -> this.primaryGroupSids.add((String)s));
        }
    }

    void synchroniseUserChanges(DirectoryCache directoryCache, Long highestCommittedUsn) throws OperationFailedException {
        if (this.useLegacyADIncrementalSync) {
            this.synchroniseUserChangesUsn(directoryCache, highestCommittedUsn);
        } else {
            this.synchroniseUserChangesGuid(directoryCache, highestCommittedUsn);
        }
    }

    private void synchroniseGroupChanges(DirectoryCache directoryCache, long highestCommittedUsn) throws OperationFailedException {
        long start = System.currentTimeMillis();
        log.debug("loading changed groups");
        Set<String> groupGuidsInCache = this.getAndValidateGroupGuidsFromCache(directoryCache);
        Set<String> groupGuidsInAd = this.getSynchronizableGroupGuidsFromAd(directoryCache);
        Sets.SetView groupGuidsToRemove = Sets.difference(groupGuidsInCache, groupGuidsInAd);
        log.info("Found [ {} ] group GUIDs in cache, [ {} ] group GUIDs in remote directory", (Object)groupGuidsInCache.size(), (Object)groupGuidsInAd.size());
        Pair<List<LDAPGroupWithAttributes>, List<LDAPGroupWithAttributes>> addedAndUpdated = this.findAndValidateAddedAndUpdatedGroupsSince(highestCommittedUsn, groupGuidsInCache, groupGuidsInAd);
        log.info("scanned and compared [ {} ] groups to delete, [ {} ] groups to add, [ {} ] groups to update in DB cache in [ {}ms ]", new Object[]{groupGuidsToRemove.size(), ((List)addedAndUpdated.getLeft()).size(), ((List)addedAndUpdated.getRight()).size(), System.currentTimeMillis() - start});
        this.syncGroups(directoryCache, (Set<String>)groupGuidsToRemove, (Collection)addedAndUpdated.getLeft(), (Collection)addedAndUpdated.getRight());
    }

    private Pair<List<LDAPGroupWithAttributes>, List<LDAPGroupWithAttributes>> findAndValidateAddedAndUpdatedGroupsSince(long highestCommittedUsn, Set<String> groupGuidsInCache, Set<String> groupGuidsInAd) throws OperationFailedException {
        HashMap<String, LDAPGroupWithAttributes> newGroups = new HashMap<String, LDAPGroupWithAttributes>();
        ImmutableList.Builder groupsToUpdateBuilder = ImmutableList.builder();
        for (LDAPGroupWithAttributes group : this.activeDirectory.findAddedOrUpdatedGroupsSince(highestCommittedUsn)) {
            String externalId = group.getExternalId();
            if (StringUtils.isEmpty((CharSequence)externalId)) {
                throw new UsnChangedCacheRefresherIncSyncException("A null or empty guid retrieved from AD.");
            }
            if (groupGuidsInCache.contains(externalId)) {
                groupsToUpdateBuilder.add((Object)group);
                continue;
            }
            if (!groupGuidsInAd.contains(externalId)) continue;
            newGroups.put(externalId, group);
        }
        Sets.SetView missingGuids = Sets.difference((Set)Sets.difference(groupGuidsInAd, groupGuidsInCache), newGroups.keySet());
        if (!missingGuids.isEmpty()) {
            log.warn("Failed to fetch groups by objectGUIDs '{}' from ActiveDirectory", (Object)missingGuids);
            throw new UsnChangedCacheRefresherIncSyncException("Problems while looking up groups by objectGUID in ActiveDirectory detected, falling back to a full sync.");
        }
        return Pair.of((Object)ImmutableList.copyOf(newGroups.values()), (Object)groupsToUpdateBuilder.build());
    }

    private Set<String> getAndValidateGroupGuidsFromCache(DirectoryCache directoryCache) throws OperationFailedException {
        ImmutableSet groupGuidsInCache = ImmutableSet.copyOf((Collection)directoryCache.getAllGroupGuids());
        if ((long)groupGuidsInCache.size() != directoryCache.getExternalCachedGroupCount()) {
            throw new UsnChangedCacheRefresherIncSyncException("Cache returned different number of guids and non-local groups (possible reason is overlapping guids in cache, most likely null/empty values).");
        }
        if (groupGuidsInCache.contains("")) {
            throw new UsnChangedCacheRefresherIncSyncException("Empty group guids returned from cache. Falling back to a full sync in order to populate the guids");
        }
        return groupGuidsInCache;
    }

    private Set<String> getSynchronizableGroupGuidsFromAd(DirectoryCache directoryCache) throws OperationFailedException {
        if (!this.activeDirectory.isGroupExternalIdConfigured()) {
            throw new UsnChangedCacheRefresherIncSyncException("externalId attribute is not configured in directory.");
        }
        List namesAndGuids = DirectoryEntities.filterOutDuplicates(this.activeDirectory.findAllGroupNamesAndGuids(), Pair::getLeft);
        Predicate isLocalGroup = IdentifierUtils.containsIdentifierPredicate((Collection)directoryCache.getAllLocalGroupNames());
        Set<String> groupGuidsInAd = namesAndGuids.stream().filter(group -> !isLocalGroup.test(group.getLeft())).map(Pair::getRight).collect(Collectors.toSet());
        if (groupGuidsInAd.contains("")) {
            throw new UsnChangedCacheRefresherIncSyncException("Empty groups guids returned from AD. Possible reasons are externalId attribute value in directory configuration or AD server configuration.");
        }
        return groupGuidsInAd;
    }

    private void syncGroups(DirectoryCache directoryCache, Set<String> groupGuidsToRemove, Collection<LDAPGroupWithAttributes> newGroups, Collection<LDAPGroupWithAttributes> groupsToUpdate) throws OperationFailedException {
        directoryCache.deleteCachedGroupsByGuids(groupGuidsToRemove);
        this.groupMap.removeAllByGuid(groupGuidsToRemove);
        directoryCache.addOrUpdateCachedGroups(groupsToUpdate, null);
        this.groupMap.putAll(groupsToUpdate);
        directoryCache.addOrUpdateCachedGroups(newGroups, null);
        this.groupMap.putAll(newGroups);
        this.synchroniseMemberships((Collection)Sets.union((Set)ImmutableSet.copyOf(newGroups), (Set)ImmutableSet.copyOf(groupsToUpdate)), directoryCache, false);
        this.synchroniseMemberships((Collection)Sets.union((Set)ImmutableSet.copyOf(this.activeDirectory.searchGroupsByDns(this.groupsDnsToUpdate)), (Set)ImmutableSet.copyOf(this.activeDirectory.searchGroupsBySids(this.primaryGroupSids))), directoryCache, false);
    }

    protected Iterable<Membership> getMemberships(Collection<LDAPGroupWithAttributes> groupsToInclude, boolean isFullSync) throws OperationFailedException {
        try {
            Map<LdapName, String> users = this.userMap.toLdapNameKeyedMap();
            Map<LdapName, String> groups = this.groupMap.toLdapNameKeyedMap();
            RFC4519DirectoryMembershipsIterableBuilder iterableBuilder = new RFC4519DirectoryMembershipsIterableBuilder().forConnector(this.activeDirectory).forGroups(groupsToInclude).withDnMapper(new DeduplicatingDnMapperDecorator(RFC4519Directory.DN_MAPPER));
            if (isFullSync) {
                iterableBuilder.withFullCache(users, groups);
            } else {
                iterableBuilder.withPartialCache(users, groups);
            }
            return iterableBuilder.build();
        }
        catch (InvalidNameException e) {
            throw new OperationFailedException("Failed to get directory memberships due to invalid DN", (Throwable)e);
        }
    }

    private ActiveDirectoryTokenHolder deserializeDirectorySyncToken(String token) {
        if (!Strings.isNullOrEmpty((String)token)) {
            try {
                return (ActiveDirectoryTokenHolder)OBJECT_MAPPER.readValue(token, ActiveDirectoryTokenHolder.class);
            }
            catch (IOException e) {
                if (Longs.tryParse((String)token) != null) {
                    log.warn("Cannot proceed with incremental synchronization as sync token '{}' does not contain the invocation id, falling back to FULL", (Object)token);
                }
                log.warn("Cannot parse sync token '{}', falling back to FULL", (Object)token);
            }
        }
        return null;
    }

    private String serializeDirectorySyncToken(String currentInvocationId, long currentHighestCommittedUsn) {
        try {
            return OBJECT_MAPPER.writeValueAsString((Object)new ActiveDirectoryTokenHolder(currentInvocationId, currentHighestCommittedUsn));
        }
        catch (IOException e) {
            log.warn("Cannot serialize synchronisation token obtained from Azure AD. Last invocation id: '{}', highestCommittedUsn: '{}'", new Object[]{currentInvocationId, currentHighestCommittedUsn, e});
            return null;
        }
    }

    private void logUserChanges(Set<String> guidsToRemove, Set<String> guidsToAdd, ImmutableMap<String, LDAPUserWithAttributes> usersToAddByGuid, ImmutableList<LDAPUserWithAttributes> usersToUpdate) {
        if (log.isTraceEnabled()) {
            String DELIMITER = ",\n";
            log.trace("Users to remove: {}", (Object)String.join((CharSequence)",\n", guidsToRemove));
            log.trace("Users to add: {}", (Object)guidsToAdd.stream().map(guid -> Optional.ofNullable(usersToAddByGuid.get(guid)).map(user -> String.format("%s(%s)", guid, user.getName())).orElse(String.format("%s", guid))).collect(Collectors.joining(",\n")));
            log.trace("Users to update: {}", (Object)usersToUpdate.stream().map(user -> String.format("%s(%s)", user.getExternalId(), user.getName())).collect(Collectors.joining(",\n")));
        }
    }
}

