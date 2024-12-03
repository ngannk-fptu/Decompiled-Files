/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributePredicates
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$AddRemoveSets
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupShadowingType
 *  com.atlassian.crowd.directory.DirectoryCacheChangeOperations$GroupsToAddUpdateReplace
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.synchronisation.cache.GroupActionStrategy
 *  com.atlassian.crowd.directory.synchronisation.utils.AddUpdateSets
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierMap
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.embedded.spi.GroupDao
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.event.DirectoryEvent
 *  com.atlassian.crowd.event.azure.AzureGroupsRemovedEvent
 *  com.atlassian.crowd.event.group.GroupCreatedEvent
 *  com.atlassian.crowd.event.group.GroupDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent
 *  com.atlassian.crowd.event.group.GroupUpdatedEvent
 *  com.atlassian.crowd.event.user.UserCreatedFromDirectorySynchronisationEvent
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.event.user.UserEditedEvent
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.event.user.UsersDeletedEvent
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupType
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction$BooleanLogic
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.MatchMode
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.TermRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.EqualityUtil
 *  com.atlassian.crowd.util.TimedOperation
 *  com.atlassian.crowd.util.TimedProgressOperation
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  javax.annotation.Nullable
 *  org.apache.commons.collections.MapUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.tuple.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.attribute.AttributePredicates;
import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.directory.DirectoryCacheChangeOperations;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.synchronisation.cache.GroupActionStrategy;
import com.atlassian.crowd.directory.synchronisation.utils.AddUpdateSets;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationRoundInformation;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierMap;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.embedded.spi.GroupDao;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.event.DirectoryEvent;
import com.atlassian.crowd.event.azure.AzureGroupsRemovedEvent;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.event.group.GroupDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent;
import com.atlassian.crowd.event.group.GroupUpdatedEvent;
import com.atlassian.crowd.event.user.UserCreatedFromDirectorySynchronisationEvent;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.event.user.UserEditedEvent;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.event.user.UsersDeletedEvent;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupType;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.MatchMode;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.EqualityUtil;
import com.atlassian.crowd.util.TimedOperation;
import com.atlassian.crowd.util.TimedProgressOperation;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbCachingRemoteChangeOperations
implements DirectoryCacheChangeOperations {
    private static final Logger logger = LoggerFactory.getLogger(DbCachingRemoteChangeOperations.class);
    private final DirectoryDao directoryDao;
    private final RemoteDirectory remoteDirectory;
    private final InternalRemoteDirectory internalDirectory;
    private final SynchronisationStatusManager synchronisationStatusManager;
    private final MultiEventPublisher eventPublisher;
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final GroupActionStrategy groupActionStrategy;
    private final CrowdDarkFeatureManager crowdDarkFeatureManager;

    public DbCachingRemoteChangeOperations(DirectoryDao directoryDao, RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory, SynchronisationStatusManager synchronisationStatusManager, MultiEventPublisher eventPublisher, UserDao userDao, GroupDao groupDao, GroupActionStrategy groupActionStrategy, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        this.directoryDao = directoryDao;
        this.remoteDirectory = remoteDirectory;
        this.internalDirectory = internalDirectory;
        this.synchronisationStatusManager = synchronisationStatusManager;
        this.eventPublisher = eventPublisher;
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.groupActionStrategy = groupActionStrategy;
        this.crowdDarkFeatureManager = crowdDarkFeatureManager;
    }

    private List<TimestampedUser> findInternalUsersUpdatedBefore(@Nullable Date date) throws OperationFailedException {
        NullRestriction restriction = date == null ? NullRestrictionImpl.INSTANCE : Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)UserTermKeys.CREATED_DATE).lessThan((Object)date), Restriction.on((Property)UserTermKeys.UPDATED_DATE).lessThan((Object)date)});
        return this.internalDirectory.searchUsers(QueryBuilder.queryFor(TimestampedUser.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)restriction).returningAtMost(-1));
    }

    private Map<String, InternalDirectoryGroup> findAndMapByNameGroupsUpdatedBefore(Date date) throws OperationFailedException {
        return this.mapGroupsByName(this.findGroupsUpdatedBefore(date));
    }

    private Map<String, InternalDirectoryGroup> mapGroupsByName(List<InternalDirectoryGroup> groups) {
        return IdentifierMap.index(groups, DirectoryEntity::getName);
    }

    private Map<String, InternalDirectoryGroup> findAndMapByExternalIdGroupsUpdatedBefore(Date date) throws OperationFailedException {
        return this.mapGroupsByExternalId(this.findGroupsUpdatedBefore(date));
    }

    private Map<String, InternalDirectoryGroup> mapGroupsByExternalId(List<InternalDirectoryGroup> groups) {
        HashMap<String, InternalDirectoryGroup> result = new HashMap<String, InternalDirectoryGroup>(groups.size());
        for (InternalDirectoryGroup internalGroup : groups) {
            result.put(internalGroup.getExternalId(), internalGroup);
        }
        return result;
    }

    private List<InternalDirectoryGroup> findGroupsUpdatedBefore(Date date) throws OperationFailedException {
        NullRestriction restriction = date == null ? NullRestrictionImpl.INSTANCE : Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)GroupTermKeys.CREATED_DATE).lessThan((Object)date), Restriction.on((Property)GroupTermKeys.UPDATED_DATE).lessThan((Object)date)});
        List groups = this.internalDirectory.searchGroups(QueryBuilder.queryFor(InternalDirectoryGroup.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)restriction).returningAtMost(-1));
        List roles = this.internalDirectory.searchGroups(QueryBuilder.queryFor(InternalDirectoryGroup.class, (EntityDescriptor)EntityDescriptor.role()).with((SearchRestriction)restriction).returningAtMost(-1));
        return ImmutableList.builder().addAll((Iterable)groups).addAll((Iterable)roles).build();
    }

    public void addUsers(Set<UserTemplateWithCredentialAndAttributes> usersToAdd) throws OperationFailedException {
        if (!usersToAdd.isEmpty()) {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.ADDING_USERS, (List)ImmutableList.of((Object)usersToAdd.size()));
            logger.info("adding [ {} ] users", (Object)usersToAdd.size());
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                BatchResult result = this.internalDirectory.addAllUsers(usersToAdd);
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                this.publishEvents(result.getSuccessfulEntities().stream().map(addedUser -> new UserCreatedFromDirectorySynchronisationEvent((Object)this, immutableDirectory, addedUser)), initialSyncHasBeenStarted);
                DbCachingRemoteChangeOperations.logFailures(this.internalDirectory, (BatchResult<? extends DirectoryEntity>)result);
                logger.info(operation.complete("added [ " + result.getTotalSuccessful() + " ] users successfully"));
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException(operation.complete("failed while adding users"), (Throwable)e);
            }
        }
    }

    public void updateUsers(Collection<UserTemplate> usersToUpdate) throws OperationFailedException {
        if (!usersToUpdate.isEmpty()) {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.UPDATING_USERS, (List)ImmutableList.of((Object)usersToUpdate.size()));
            logger.info("updating [ {} ] users", (Object)usersToUpdate.size());
            TimedProgressOperation operation = new TimedProgressOperation("updating users", usersToUpdate.size(), logger);
            int successfulUpdates = 0;
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                for (UserTemplate user : usersToUpdate) {
                    operation.incrementProgress();
                    try {
                        ImmutableUser originalUser;
                        String externalId = user.getExternalId();
                        if (StringUtils.isNotEmpty((CharSequence)externalId)) {
                            com.atlassian.crowd.model.user.User userByExternalId = this.userByExternalIdOrNull(externalId);
                            if (userByExternalId != null) {
                                originalUser = ImmutableUser.from((com.atlassian.crowd.model.user.User)userByExternalId);
                                if (!StringUtils.equals((CharSequence)userByExternalId.getName(), (CharSequence)user.getName())) {
                                    String oldName = userByExternalId.getName();
                                    this.internalDirectory.forceRenameUser(userByExternalId, user.getName());
                                    this.publishEvent((DirectoryEvent)new UserRenamedEvent((Object)this, (Directory)immutableDirectory, (com.atlassian.crowd.model.user.User)user, oldName), initialSyncHasBeenStarted);
                                }
                            } else {
                                originalUser = ImmutableUser.from((com.atlassian.crowd.model.user.User)this.internalDirectory.findUserByName(user.getName()));
                            }
                        } else {
                            originalUser = ImmutableUser.from((com.atlassian.crowd.model.user.User)this.internalDirectory.findUserByName(user.getName()));
                        }
                        com.atlassian.crowd.model.user.User updatedUser = this.internalDirectory.updateUser(user);
                        this.publishEvent((DirectoryEvent)new UserEditedEvent((Object)this, (Directory)immutableDirectory, updatedUser, (com.atlassian.crowd.model.user.User)originalUser), initialSyncHasBeenStarted);
                        ++successfulUpdates;
                    }
                    catch (InvalidUserException e) {
                        logger.warn("Unable to synchronize user '{}' from remote directory: ", (Object)user.getName(), (Object)e);
                    }
                    catch (UserNotFoundException e) {
                        logger.warn("Could not find user to '{}' in internal directory: ", (Object)user.getName(), (Object)e);
                    }
                }
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException(operation.complete("failed while updating users"), (Throwable)e);
            }
            finally {
                logger.info(operation.complete("updated [ " + successfulUpdates + " ] users successfully"));
            }
        }
    }

    public void deleteCachedUsersByGuid(Set<String> guids) throws OperationFailedException {
        Set<String> userNamesToDelete = this.collectUserNamesOfUsersToDeleteByGuid(guids);
        this.deleteCachedUsersByName(userNamesToDelete);
    }

    @Nullable
    private com.atlassian.crowd.model.user.User userByExternalIdOrNull(String externalId) {
        try {
            return this.internalDirectory.findUserByExternalId(externalId);
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    private void deleteCachedUsersByName(Set<String> usernames) throws OperationFailedException {
        this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.DELETING_USERS, (List)ImmutableList.of((Object)usernames.size()));
        logger.info("deleting [ {} ] users", (Object)usernames.size());
        TimedOperation operation = new TimedOperation();
        try {
            this.internalDirectory.removeAllUsers(usernames);
            Directory directory = this.getDirectory();
            boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
            this.publishEvent((DirectoryEvent)new UsersDeletedEvent((Object)this, (Directory)immutableDirectory, Collections.unmodifiableCollection(usernames)), initialSyncHasBeenStarted);
            this.publishEvents(usernames.stream().map(deletedUser -> new UserDeletedEvent((Object)this, immutableDirectory, deletedUser)), initialSyncHasBeenStarted);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException(e.getCause());
        }
        finally {
            logger.info(operation.complete("deleted [ " + usernames.size() + " ] users"));
        }
    }

    private Set<String> collectUserNamesOfUsersToDeleteByGuid(Set<String> guids) {
        TimedProgressOperation timedProgressOperation = new TimedProgressOperation("Collecting usernames from guids", guids.size(), logger);
        ImmutableSet usernames = ImmutableSet.copyOf((Iterable)Iterables.filter((Iterable)Iterables.transform(guids, guid -> {
            try {
                TimestampedUser user = this.internalDirectory.findUserByExternalId(guid);
                if (user.isMarkedAsDeleted()) {
                    logger.debug("Skipping deletion of user '{}' from directory {}, because user is already marked as deleted", (Object)user.getName(), (Object)this.internalDirectory.getDirectoryId());
                    String string = null;
                    return string;
                }
                String string = user.getName();
                return string;
            }
            catch (UserNotFoundException e) {
                logger.debug("user with externalId [ {} ] was not found in [ {} ] and could not be deleted", guid, (Object)this.internalDirectory.getDirectoryId());
                String string = null;
                return string;
            }
            finally {
                timedProgressOperation.incrementedProgress();
            }
        }), (Predicate)Predicates.notNull()));
        timedProgressOperation.complete(String.format("Finished collecting usernames for [ %d ] guids", guids.size()));
        return usernames;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteCachedUsersNotIn(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers, Date synchStartDate) throws OperationFailedException {
        if (!((Boolean)SystemProperties.ALLOW_DUPLICATED_EXTERNAL_IDS_IN_SYNC.getValue()).booleanValue()) {
            this.deleteCachedUsersNotInOld(remoteUsers, synchStartDate);
            return;
        }
        TimedOperation scanningComparingAndDeletingOperation = new TimedOperation();
        try {
            Set<String> usersToDelete;
            TimedOperation scanningAndComparingOperation = new TimedOperation();
            try {
                List<TimestampedUser> internalUsers = this.findInternalUsersUpdatedBefore(synchStartDate);
                HashSet<TimestampedUser> toDelete = new HashSet<TimestampedUser>(internalUsers);
                toDelete.removeIf(User::isMarkedAsDeleted);
                DbCachingRemoteChangeOperations.matchUsers(remoteUsers, internalUsers).forEach(e -> toDelete.remove(e.getRight()));
                usersToDelete = toDelete.stream().map(Principal::getName).collect(Collectors.toSet());
            }
            finally {
                logger.info(scanningAndComparingOperation.complete("scanned and compared [ " + remoteUsers.size() + " ] users for delete in DB cache"));
            }
            if (!usersToDelete.isEmpty()) {
                this.deleteCachedUsersByName(usersToDelete);
            }
        }
        finally {
            logger.info(scanningComparingAndDeletingOperation.complete("scanned for deleted users"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteCachedUsersNotInOld(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers, Date synchStartDate) throws OperationFailedException {
        TimedOperation scanningComparingAndDeletingOperation = new TimedOperation();
        try {
            IdentifierSet remoteUsernames = new IdentifierSet(remoteUsers.size());
            HashSet<String> usersToDelete = new HashSet<String>();
            TimedOperation scanningAndComparingOperation = new TimedOperation();
            try {
                for (com.atlassian.crowd.model.user.User user : remoteUsers) {
                    remoteUsernames.add(user.getName());
                }
                Set<String> remoteExternalIds = DbCachingRemoteChangeOperations.externalIdsOf(remoteUsers);
                IdentifierMap identifierMap = IdentifierMap.index(this.findInternalUsersUpdatedBefore(synchStartDate), Principal::getName);
                for (TimestampedUser internalUser : identifierMap.values()) {
                    String userName = internalUser.getName();
                    boolean bl = StringUtils.isBlank((CharSequence)internalUser.getExternalId()) ? !remoteUsernames.contains(userName) : !remoteExternalIds.contains(internalUser.getExternalId());
                    boolean shouldDelete = bl;
                    if (!shouldDelete) continue;
                    if (internalUser.isMarkedAsDeleted()) {
                        logger.debug("user '{}' already marked as deleted", (Object)userName);
                        continue;
                    }
                    logger.debug("user '{}' not found, deleting", (Object)userName);
                    usersToDelete.add(userName);
                }
            }
            finally {
                logger.info(scanningAndComparingOperation.complete("scanned and compared [ " + remoteUsers.size() + " ] users for delete in DB cache"));
            }
            if (!usersToDelete.isEmpty()) {
                this.deleteCachedUsersByName(usersToDelete);
            }
        }
        finally {
            logger.info(scanningComparingAndDeletingOperation.complete("scanned for deleted users"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DirectoryCacheChangeOperations.GroupsToAddUpdateReplace findGroupsToUpdate(Collection<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        ImmutableSet.Builder groupsToAdd = ImmutableSet.builder();
        ImmutableSet.Builder groupsToUpdate = ImmutableSet.builder();
        ImmutableMap.Builder groupsToReplace = ImmutableMap.builder();
        TimedOperation operation = new TimedOperation();
        try {
            List<InternalDirectoryGroup> groupsUpdatedBefore = this.findGroupsUpdatedBefore(syncStartDate);
            Map<String, InternalDirectoryGroup> groupsByName = this.mapGroupsByName(groupsUpdatedBefore);
            Map<String, InternalDirectoryGroup> groupsByExternalId = this.mapGroupsByExternalId(groupsUpdatedBefore);
            for (Group group : remoteGroups) {
                InternalDirectoryGroup internalGroup = groupsByName.get(group.getName());
                DirectoryCacheChangeOperations.GroupsToAddUpdateReplace groupsToAddUpdateReplace = this.groupActionStrategy.decide(internalGroup, groupsByExternalId.get(group.getExternalId()), group, syncStartDate, this.getDirectoryId());
                groupsToAdd.addAll((Iterable)groupsToAddUpdateReplace.groupsToAdd);
                groupsToUpdate.addAll((Iterable)groupsToAddUpdateReplace.groupsToUpdate);
                groupsToReplace.putAll(groupsToAddUpdateReplace.groupsToReplace);
            }
            DirectoryCacheChangeOperations.GroupsToAddUpdateReplace groupsToAddUpdateReplace = new DirectoryCacheChangeOperations.GroupsToAddUpdateReplace((Set)groupsToAdd.build(), (Set)groupsToUpdate.build(), (Map)groupsToReplace.build());
            return groupsToAddUpdateReplace;
        }
        finally {
            logger.info(operation.complete("scanned and compared [ " + remoteGroups.size() + " ] groups for update in DB cache"));
        }
    }

    public void removeGroups(Collection<String> groupsToRemove) throws OperationFailedException {
        if (!groupsToRemove.isEmpty()) {
            TimedOperation operation = new TimedOperation();
            int successfulRemoves = 0;
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                for (String entry : groupsToRemove) {
                    try {
                        this.internalDirectory.removeGroup(entry);
                        this.publishEvent((DirectoryEvent)new GroupDeletedEvent((Object)this, (Directory)immutableDirectory, entry), initialSyncHasBeenStarted);
                        ++successfulRemoves;
                    }
                    catch (GroupNotFoundException e) {
                        logger.warn("Could not find group '{}': ", (Object)e.getGroupName(), (Object)e);
                    }
                    catch (ReadOnlyGroupException e) {
                        logger.warn("Group '{}' is read-only and not allowed to be modified: ", (Object)e.getGroupName(), (Object)e);
                    }
                }
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException(operation.complete("failed while removing groups"), (Throwable)e);
            }
            finally {
                logger.info(operation.complete("deleted [ " + successfulRemoves + " ] groups to be replaced"));
            }
        }
    }

    public void addGroups(Set<GroupTemplate> groupsToAdd) throws OperationFailedException {
        logger.debug("adding [ {} ] groups", (Object)groupsToAdd.size());
        if (!groupsToAdd.isEmpty()) {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.ADDING_GROUPS, (List)ImmutableList.of((Object)groupsToAdd.size()));
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                BatchResult result = this.internalDirectory.addAllGroups(groupsToAdd);
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                this.publishEvents(result.getSuccessfulEntities().stream().map(addedGroup -> new GroupCreatedEvent((Object)this, (Directory)immutableDirectory, addedGroup)), initialSyncHasBeenStarted);
                DbCachingRemoteChangeOperations.logFailures(this.internalDirectory, (BatchResult<? extends DirectoryEntity>)result);
                logger.info(operation.complete("added [ " + result.getTotalSuccessful() + " ] groups successfully"));
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException(operation.complete("failed while adding groups"), (Throwable)e);
            }
        }
    }

    public void updateGroups(Collection<GroupTemplate> groupsToUpdate) throws OperationFailedException {
        logger.debug("updating [ {} ] groups", (Object)groupsToUpdate.size());
        if (!groupsToUpdate.isEmpty()) {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.UPDATING_GROUPS, (List)ImmutableList.of((Object)groupsToUpdate.size()));
            TimedOperation operation = new TimedOperation();
            int successfulUpdates = 0;
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                for (GroupTemplate groupTemplate : groupsToUpdate) {
                    try {
                        Group updatedGroup = this.internalDirectory.updateGroup(groupTemplate);
                        this.publishEvent((DirectoryEvent)new GroupUpdatedEvent((Object)this, (Directory)immutableDirectory, updatedGroup), initialSyncHasBeenStarted);
                        ++successfulUpdates;
                    }
                    catch (InvalidGroupException e) {
                        logger.warn("Unable to synchronise group '{}' with remote directory: ", (Object)groupTemplate.getName(), (Object)e);
                    }
                    catch (ReadOnlyGroupException e) {
                        logger.warn("Unable to update read-only group '{}' with remote directory: ", (Object)groupTemplate.getName(), (Object)e);
                    }
                    catch (GroupNotFoundException e) {
                        logger.warn("Unable to find group '{}' on update with remote directory: ", (Object)groupTemplate.getName(), (Object)e);
                    }
                }
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException(operation.complete("failed while updating groups"), (Throwable)e);
            }
            finally {
                logger.info(operation.complete("updated [ " + successfulUpdates + " ] groups successfully"));
            }
        }
    }

    public void deleteCachedGroupsNotIn(GroupType groupType, List<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        Set<String> groupsToRemove = this.determineGroupsToRemoveByName(remoteGroups, syncStartDate);
        if (!groupsToRemove.isEmpty()) {
            this.deleteCachedGroups(groupsToRemove);
        }
    }

    public void deleteCachedGroupsNotInByExternalId(Collection<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        Set<String> groupsToRemove = this.determineGroupsToRemoveByExternalId(remoteGroups, syncStartDate);
        if (!groupsToRemove.isEmpty()) {
            this.deleteCachedGroupsByGuids(groupsToRemove);
            this.cleanUpAzureAdGroupFiltersIfAny(groupsToRemove);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> determineGroupsToRemoveByName(List<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        TimedOperation operation = new TimedOperation();
        try {
            IdentifierSet remoteGroupIdentifiers = new IdentifierSet(remoteGroups.size());
            remoteGroupIdentifiers.addAll(remoteGroups.stream().map(DirectoryEntity::getName).collect(Collectors.toSet()));
            Map<String, InternalDirectoryGroup> groups = this.findAndMapByNameGroupsUpdatedBefore(syncStartDate);
            Set<String> set = this.processGroups(syncStartDate, (Set<String>)remoteGroupIdentifiers, groups, DirectoryEntity::getName);
            return set;
        }
        finally {
            logger.info(operation.complete("scanned and compared [ " + remoteGroups.size() + " ] groups for delete in DB cache"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<String> determineGroupsToRemoveByExternalId(Collection<? extends Group> remoteGroups, Date syncStartDate) throws OperationFailedException {
        TimedOperation operation = new TimedOperation();
        try {
            ImmutableSet remoteGroupIdentifiers = ImmutableSet.copyOf((Collection)remoteGroups.stream().map(Group::getExternalId).collect(Collectors.toSet()));
            Map<String, InternalDirectoryGroup> groups = this.findAndMapByExternalIdGroupsUpdatedBefore(syncStartDate);
            Set<String> set = this.processGroups(syncStartDate, (Set<String>)remoteGroupIdentifiers, groups, Group::getExternalId);
            return set;
        }
        finally {
            logger.info(operation.complete("scanned and compared [ " + remoteGroups.size() + " ] groups for delete in DB cache"));
        }
    }

    private Set<String> processGroups(Date syncStartDate, Set<String> remoteGroupIdentifiers, Map<String, InternalDirectoryGroup> groups, Function<InternalDirectoryGroup, String> groupIdentifierExtractor) {
        HashSet<String> groupsToRemove = new HashSet<String>();
        for (InternalDirectoryGroup internalGroup : groups.values()) {
            if (internalGroup.isLocal()) continue;
            if (internalGroup.getCreatedDate() == null) {
                logger.warn("group [ {} ] in directory [ {} ] has no created date, skipping", (Object)groupIdentifierExtractor.apply(internalGroup), (Object)this.getDirectoryId());
            } else if (syncStartDate != null && internalGroup.getCreatedDate().getTime() > syncStartDate.getTime()) {
                logger.debug("group [ {} ] created after synchronisation start, skipping", (Object)groupIdentifierExtractor.apply(internalGroup));
                continue;
            }
            if (remoteGroupIdentifiers.contains(groupIdentifierExtractor.apply(internalGroup))) continue;
            logger.debug("group [ {} ] not found, deleting", (Object)groupIdentifierExtractor.apply(internalGroup));
            groupsToRemove.add(groupIdentifierExtractor.apply(internalGroup));
        }
        return groupsToRemove;
    }

    public void deleteCachedGroups(Set<String> groupnames) throws OperationFailedException {
        this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.DELETING_GROUPS, (List)ImmutableList.of((Object)groupnames.size()));
        logger.info("removing [ {} ] groups", (Object)groupnames.size());
        TimedOperation operation = new TimedOperation();
        try {
            BatchResult result = this.internalDirectory.removeAllGroups(groupnames);
            Directory directory = this.getDirectory();
            boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
            this.publishEvents(groupnames.stream().map(groupName -> new GroupDeletedEvent((Object)this, (Directory)immutableDirectory, groupName)), initialSyncHasBeenStarted);
            logger.info(operation.complete("removed [ " + result.getTotalSuccessful() + " ] groups successfully"));
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException(operation.complete("failed while deleting groups"), (Throwable)e);
        }
    }

    public void deleteCachedGroupsByGuids(Set<String> guids) throws OperationFailedException {
        if (!guids.isEmpty()) {
            List tombstonesAsRestrictions = guids.stream().map(guid -> new TermRestriction(GroupTermKeys.EXTERNAL_ID, MatchMode.EXACTLY_MATCHES, guid)).collect(Collectors.toList());
            EntityQuery queryForTombstonesNames = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, tombstonesAsRestrictions)).returningAtMost(-1);
            ImmutableSet groupsToDelete = ImmutableSet.copyOf((Collection)this.internalDirectory.searchGroups(queryForTombstonesNames));
            this.deleteCachedGroups((Set<String>)groupsToDelete);
            this.cleanUpAzureAdGroupFiltersIfAny(guids);
        }
    }

    private void cleanUpAzureAdGroupFiltersIfAny(Set<String> groupExternalIdsRemoved) throws OperationFailedException {
        try {
            if (this.getDirectory().getType() == DirectoryType.AZURE_AD) {
                this.eventPublisher.publish(new AzureGroupsRemovedEvent((Object)this, this.getDirectory(), groupExternalIdsRemoved));
            }
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException("failed while finding directory to remove any Azure Filtered Groups", (Throwable)e);
        }
    }

    protected boolean hasChanged(com.atlassian.crowd.model.user.User remoteUser, com.atlassian.crowd.model.user.User internalUser) {
        boolean externalIdsAreSet = StringUtils.isNotEmpty((CharSequence)remoteUser.getExternalId()) && StringUtils.isNotEmpty((CharSequence)internalUser.getExternalId());
        return EqualityUtil.different((String)remoteUser.getFirstName(), (String)internalUser.getFirstName()) || EqualityUtil.different((String)remoteUser.getLastName(), (String)internalUser.getLastName()) || EqualityUtil.different((String)remoteUser.getDisplayName(), (String)internalUser.getDisplayName()) || EqualityUtil.different((String)remoteUser.getEmailAddress(), (String)internalUser.getEmailAddress()) || EqualityUtil.different((String)remoteUser.getExternalId(), (String)internalUser.getExternalId()) || externalIdsAreSet && EqualityUtil.different((String)remoteUser.getName(), (String)internalUser.getName()) || this.remoteDirectory.supportsInactiveAccounts() && remoteUser.isActive() != internalUser.isActive();
    }

    private static UserTemplate makeUserTemplate(com.atlassian.crowd.model.user.User user) {
        UserTemplate template = new UserTemplate(user);
        template.setFirstName(user.getFirstName());
        template.setLastName(user.getLastName());
        template.setDisplayName(user.getDisplayName());
        template.setEmailAddress(user.getEmailAddress());
        return template;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DirectoryCacheChangeOperations.AddRemoveSets<String> findUserMembershipForGroupChanges(Group group, Collection<String> remoteUsers) throws OperationFailedException {
        Set remoteUsersSet = IdentifierUtils.toLowerCase(remoteUsers);
        TimedOperation operation = new TimedOperation();
        try {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.USER_MEMBERSHIPS, (List)ImmutableList.of((Object)remoteUsersSet.size(), (Object)group.getName()));
            logger.debug("synchronising [ {} ] user members for group '{}'", (Object)remoteUsersSet.size(), (Object)group.getName());
            Set internalMembers = IdentifierUtils.toLowerCase((Collection)this.internalDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).childrenOf(EntityDescriptor.group()).withName(group.getName()).returningAtMost(-1)));
            logger.debug("internal directory has [ {} ] members", (Object)internalMembers.size());
            Sets.SetView usersToAdd = Sets.difference((Set)remoteUsersSet, (Set)internalMembers);
            Sets.SetView usersToRemove = Sets.difference((Set)internalMembers, (Set)remoteUsersSet);
            DirectoryCacheChangeOperations.AddRemoveSets addRemoveSets = new DirectoryCacheChangeOperations.AddRemoveSets((Set)usersToAdd, (Set)usersToRemove);
            return addRemoveSets;
        }
        finally {
            logger.debug(operation.complete("scanned and compared [ " + remoteUsersSet.size() + " ] user members from '" + group.getName() + "'"));
        }
    }

    public void removeUserMembershipsForGroup(Group group, Set<String> usersToRemove) throws OperationFailedException {
        if (!usersToRemove.isEmpty()) {
            int removedUserMembershipsCount = 0;
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                removedUserMembershipsCount = this.crowdDarkFeatureManager.isDeleteUserMembershipsBatchingEnabled() ? this.removeUserMembershipsFromGroupInBulk(usersToRemove, group, immutableDirectory, initialSyncHasBeenStarted) : this.removeUserMembershipsFromGroupIteratively(usersToRemove, group, immutableDirectory, initialSyncHasBeenStarted);
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
            finally {
                logger.info(operation.complete("removed [ " + removedUserMembershipsCount + " ] user members from '" + group.getName() + "'"));
            }
        }
    }

    private int removeUserMembershipsFromGroupInBulk(Set<String> usersToRemove, Group group, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        try {
            BatchResult result = this.internalDirectory.removeUsersFromGroup(usersToRemove, group.getName());
            this.publishEvents(result.getSuccessfulEntities().stream().map(username -> new GroupMembershipDeletedEvent((Object)this, (Directory)immutableDirectory, username, group.getName(), MembershipType.GROUP_USER)), initialSyncHasBeenStarted);
            this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, (Directory)immutableDirectory, (Iterable)result.getSuccessfulEntities(), group.getName(), MembershipType.GROUP_USER), initialSyncHasBeenStarted);
            return result.getSuccessfulEntities().size();
        }
        catch (GroupNotFoundException e) {
            logger.info("Could not remove users from group. Group [{}] was not found in the cache.", (Object)e.getGroupName());
            return 0;
        }
    }

    private int removeUserMembershipsFromGroupIteratively(Set<String> usersToRemove, Group group, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        ArrayList<String> removedUsers = new ArrayList<String>();
        for (String username : usersToRemove) {
            try {
                this.internalDirectory.removeUserFromGroup(username, group.getName());
                this.publishEvent((DirectoryEvent)new GroupMembershipDeletedEvent((Object)this, (Directory)immutableDirectory, username, group.getName(), MembershipType.GROUP_USER), initialSyncHasBeenStarted);
                removedUsers.add(username);
            }
            catch (UserNotFoundException e) {
                logger.debug("Could not remove user '{}' from group '{}'. User was not found in the cache.", (Object)username, (Object)group.getName());
            }
            catch (GroupNotFoundException e) {
                logger.debug("Could not remove user '{}' from group '{}'. Group was not found in the cache.", (Object)username, (Object)group.getName());
            }
            catch (MembershipNotFoundException e) {
            }
            catch (ReadOnlyGroupException e) {
                logger.warn("Could not remove user '{}' from read-only group '{}'.", (Object)username, (Object)group.getName());
            }
        }
        this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, (Directory)immutableDirectory, removedUsers, group.getName(), MembershipType.GROUP_USER), initialSyncHasBeenStarted);
        return removedUsers.size();
    }

    /*
     * Loose catch block
     */
    public void addUserMembershipsForGroup(Group group, Set<String> usersToAdd) throws OperationFailedException {
        if (!usersToAdd.isEmpty()) {
            int usersAdded;
            Collection failedUsernames = null;
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                BatchResult result = this.internalDirectory.addAllUsersToGroup(usersToAdd, group.getName());
                failedUsernames = result.getFailedEntities();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                this.publishEvents(result.getSuccessfulEntities().stream().map(username -> new GroupMembershipCreatedEvent((Object)this, (Directory)immutableDirectory, username, group.getName(), MembershipType.GROUP_USER)), initialSyncHasBeenStarted);
                this.publishEvent((DirectoryEvent)new GroupMembershipsCreatedEvent((Object)this, (Directory)immutableDirectory, (Iterable)result.getSuccessfulEntities(), group.getName(), MembershipType.GROUP_USER), initialSyncHasBeenStarted);
                if (!failedUsernames.isEmpty()) {
                    logger.warn("Could not add the following missing users to group '{}': {}", (Object)group.getName(), (Object)failedUsernames);
                }
                usersAdded = failedUsernames != null ? usersToAdd.size() - failedUsernames.size() : 0;
            }
            catch (GroupNotFoundException e) {
                logger.info("Could not add users to group. Group '{}' was not found in the cache. Leaving membership changes for next sync.", (Object)e.getGroupName());
                int usersAdded2 = failedUsernames != null ? usersToAdd.size() - failedUsernames.size() : 0;
                logger.debug(operation.complete("added [ " + usersAdded2 + " ] user members to '" + group.getName() + "'"));
            }
            catch (DirectoryNotFoundException e2) {
                throw new OperationFailedException((Throwable)e2);
                {
                    catch (Throwable throwable) {
                        int usersAdded3 = failedUsernames != null ? usersToAdd.size() - failedUsernames.size() : 0;
                        logger.debug(operation.complete("added [ " + usersAdded3 + " ] user members to '" + group.getName() + "'"));
                        throw throwable;
                    }
                }
            }
            logger.debug(operation.complete("added [ " + usersAdded + " ] user members to '" + group.getName() + "'"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DirectoryCacheChangeOperations.AddRemoveSets<String> findGroupMembershipForGroupChanges(Group parentGroup, Collection<String> remoteGroups) throws OperationFailedException {
        logger.debug("synchronising [ {} ] group members for group '{}'", (Object)remoteGroups.size(), (Object)parentGroup.getName());
        Set remoteGroupsSet = IdentifierUtils.toLowerCase(remoteGroups);
        TimedOperation operation = new TimedOperation();
        try {
            this.synchronisationStatusManager.syncStatus(this.getDirectoryId(), SynchronisationStatusKey.GROUP_MEMBERSHIPS, (List)ImmutableList.of((Object)remoteGroupsSet.size(), (Object)parentGroup.getName()));
            Set internalGroups = IdentifierUtils.toLowerCase((Collection)this.internalDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).childrenOf(EntityDescriptor.group()).withName(parentGroup.getName()).returningAtMost(-1)));
            Sets.SetView groupsToAdd = Sets.difference((Set)remoteGroupsSet, (Set)internalGroups);
            Sets.SetView groupsToRemove = Sets.difference((Set)internalGroups, (Set)remoteGroupsSet);
            DirectoryCacheChangeOperations.AddRemoveSets addRemoveSets = new DirectoryCacheChangeOperations.AddRemoveSets((Set)groupsToAdd, (Set)groupsToRemove);
            return addRemoveSets;
        }
        finally {
            logger.debug(operation.complete("scanned and compared [ " + remoteGroups.size() + " ] group members from '" + parentGroup.getName() + "'"));
        }
    }

    public void addGroupMembershipsForGroup(Group parentGroup, Collection<String> groupsToAdd) throws OperationFailedException {
        if (!groupsToAdd.isEmpty()) {
            int addedGroupMembershipsCount = 0;
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                addedGroupMembershipsCount = this.crowdDarkFeatureManager.isNestedGroupsGroupMembershipChangesBatchedEnabled() ? this.addGroupMembershipsForGroupInBulk(groupsToAdd, parentGroup, immutableDirectory, initialSyncHasBeenStarted) : this.addGroupMembershipsForGroupIteratively(groupsToAdd, parentGroup, immutableDirectory, initialSyncHasBeenStarted);
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
            finally {
                logger.info(operation.complete("added [ " + addedGroupMembershipsCount + " ] group members to '" + parentGroup.getName() + "'"));
            }
        }
    }

    private int addGroupMembershipsForGroupInBulk(Collection<String> childGroupsToAdd, Group parentGroup, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        try {
            BatchResult result = this.internalDirectory.addAllGroupsToGroup(childGroupsToAdd, parentGroup.getName());
            this.publishEvents(result.getSuccessfulEntities().stream().map(groupName -> new GroupMembershipCreatedEvent((Object)this, (Directory)immutableDirectory, groupName, parentGroup.getName(), MembershipType.GROUP_GROUP)), initialSyncHasBeenStarted);
            this.publishEvent((DirectoryEvent)new GroupMembershipsCreatedEvent((Object)this, (Directory)immutableDirectory, (Iterable)result.getSuccessfulEntities(), parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
            return result.getSuccessfulEntities().size();
        }
        catch (GroupNotFoundException e) {
            logger.info("Could not add child groups to group. Group [{}] was not found in the cache.", (Object)e.getGroupName());
            return 0;
        }
    }

    private int addGroupMembershipsForGroupIteratively(Collection<String> childGroupsToAdd, Group parentGroup, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        ArrayList<String> addedGroups = new ArrayList<String>();
        for (String groupName : childGroupsToAdd) {
            try {
                this.internalDirectory.addGroupToGroup(groupName, parentGroup.getName());
                this.publishEvent((DirectoryEvent)new GroupMembershipCreatedEvent((Object)this, (Directory)immutableDirectory, groupName, parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
                addedGroups.add(groupName);
            }
            catch (GroupNotFoundException e) {
                logger.info("Could not add child group '{}' to parent group '{}'. Group '{}' was not found in the cache. Leaving membership changes for next sync.", new Object[]{groupName, parentGroup.getName(), e.getGroupName()});
            }
            catch (InvalidMembershipException e) {
                logger.warn("Could not add child group '{}' to parent group '{}'. Membership between child and parent group is invalid: ", new Object[]{groupName, parentGroup.getName(), e});
            }
            catch (ReadOnlyGroupException e) {
                logger.warn("Could not add child group '{}' to parent group '{}'. '{}' is a read-only group.", new Object[]{groupName, parentGroup.getName(), e.getGroupName(), e});
            }
            catch (MembershipAlreadyExistsException membershipAlreadyExistsException) {}
        }
        this.publishEvent((DirectoryEvent)new GroupMembershipsCreatedEvent((Object)this, (Directory)immutableDirectory, addedGroups, parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
        return addedGroups.size();
    }

    public void removeGroupMembershipsForGroup(Group parentGroup, Collection<String> groupsToRemove) throws OperationFailedException {
        if (!groupsToRemove.isEmpty()) {
            int removedGroupMembershipsCount = 0;
            TimedOperation operation = new TimedOperation();
            try {
                Directory directory = this.getDirectory();
                ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
                boolean initialSyncHasBeenStarted = this.initialSyncHasBeenStarted(directory);
                removedGroupMembershipsCount = this.crowdDarkFeatureManager.isNestedGroupsGroupMembershipChangesBatchedEnabled() ? this.removeGroupMembershipsFromGroupInBulk(groupsToRemove, parentGroup, immutableDirectory, initialSyncHasBeenStarted) : this.removeGroupMembershipsFromGroupIteratively(groupsToRemove, parentGroup, immutableDirectory, initialSyncHasBeenStarted);
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
            finally {
                logger.info(operation.complete("removed [ " + removedGroupMembershipsCount + " ] group members from '" + parentGroup.getName() + "'"));
            }
        }
    }

    private int removeGroupMembershipsFromGroupInBulk(Collection<String> groupsToRemove, Group parentGroup, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        try {
            BatchResult result = this.internalDirectory.removeGroupsFromGroup(groupsToRemove, parentGroup.getName());
            this.publishEvents(result.getSuccessfulEntities().stream().map(groupName -> new GroupMembershipDeletedEvent((Object)this, (Directory)immutableDirectory, groupName, parentGroup.getName(), MembershipType.GROUP_GROUP)), initialSyncHasBeenStarted);
            this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, (Directory)immutableDirectory, (Iterable)result.getSuccessfulEntities(), parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
            return result.getSuccessfulEntities().size();
        }
        catch (GroupNotFoundException e) {
            logger.info("Could not remove child groups from group. Group [{}] was not found in the cache.", (Object)e.getGroupName());
            return 0;
        }
    }

    private int removeGroupMembershipsFromGroupIteratively(Collection<String> groupsToRemove, Group parentGroup, ImmutableDirectory immutableDirectory, boolean initialSyncHasBeenStarted) throws OperationFailedException {
        ArrayList<String> removedGroups = new ArrayList<String>();
        for (String groupName : groupsToRemove) {
            try {
                this.internalDirectory.removeGroupFromGroup(groupName, parentGroup.getName());
                this.publishEvent((DirectoryEvent)new GroupMembershipDeletedEvent((Object)this, (Directory)immutableDirectory, groupName, parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
                removedGroups.add(groupName);
            }
            catch (GroupNotFoundException e) {
                logger.debug("Could not remove child group '{}' from parent group '{}'. Group '{}' was not found. The next sync will fix this problem.", new Object[]{groupName, parentGroup.getName(), e.getGroupName()});
            }
            catch (InvalidMembershipException e) {
                logger.warn("Could not remove child group '{}' from parent group '{}'. Membership between child and parent group is invalid: ", new Object[]{groupName, parentGroup.getName(), e});
            }
            catch (MembershipNotFoundException e) {
            }
            catch (ReadOnlyGroupException e) {
                logger.warn("Could not remove child group '{}' from parent group '{}'. '{}' is a read-only group.", new Object[]{groupName, parentGroup.getName(), e.getGroupName(), e});
            }
        }
        this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, (Directory)immutableDirectory, removedGroups, parentGroup.getName(), MembershipType.GROUP_GROUP), initialSyncHasBeenStarted);
        return removedGroups.size();
    }

    private boolean initialSyncHasBeenStarted(Directory directory) {
        return this.getLastSyncStartOrEnd(directory).filter(startOrEnd -> startOrEnd > this.getLastConfigChangeTimestamp(directory)).isPresent();
    }

    private Optional<Long> getLastSyncStartOrEnd(Directory directory) {
        return DbCachingRemoteChangeOperations.getLastSyncStartOrEnd(this.synchronisationStatusManager.getDirectorySynchronisationInformation(directory));
    }

    @VisibleForTesting
    protected static Optional<Long> getLastSyncStartOrEnd(DirectorySynchronisationInformation syncInfo) {
        Optional<Long> activeStart = Optional.ofNullable(syncInfo.getActiveRound()).map(DirectorySynchronisationRoundInformation::getStartTime);
        Optional<Long> lastEnd = Optional.ofNullable(syncInfo.getLastRound()).map(round -> round.getStartTime() + round.getDurationMs());
        return Collections.max(ImmutableList.of(activeStart, lastEnd), Comparator.comparing(o -> o.orElse(Long.MIN_VALUE)));
    }

    private long getLastConfigChangeTimestamp(Directory directory) {
        String value = directory.getValue("configuration.change.timestamp");
        return StringUtils.isEmpty((CharSequence)value) ? Long.MIN_VALUE : Long.parseLong(value);
    }

    @VisibleForTesting
    protected Directory getDirectory() throws DirectoryNotFoundException {
        return this.directoryDao.findById(this.getDirectoryId());
    }

    private long getDirectoryId() {
        return this.remoteDirectory.getDirectoryId();
    }

    private void publishEvent(DirectoryEvent event, boolean initialSyncHasBeenStarted) {
        this.publishEvents(Stream.of(event), initialSyncHasBeenStarted);
    }

    private void publishEvents(Stream<DirectoryEvent> event, boolean initialSyncHasBeenStarted) {
        if (initialSyncHasBeenStarted) {
            this.eventPublisher.publishAll(event.collect(Collectors.toList()));
        }
    }

    public DirectoryCacheChangeOperations.GroupShadowingType isGroupShadowed(Group remoteGroup) throws OperationFailedException {
        try {
            InternalDirectoryGroup internalGroup = this.internalDirectory.findGroupByName(remoteGroup.getName());
            if (remoteGroup.getType() == GroupType.LEGACY_ROLE && internalGroup.getType() == GroupType.GROUP) {
                return DirectoryCacheChangeOperations.GroupShadowingType.SHADOWED_BY_ROLE;
            }
            if (internalGroup.isLocal()) {
                return DirectoryCacheChangeOperations.GroupShadowingType.SHADOWED_BY_LOCAL_GROUP;
            }
            return DirectoryCacheChangeOperations.GroupShadowingType.NOT_SHADOWED;
        }
        catch (GroupNotFoundException ex) {
            return DirectoryCacheChangeOperations.GroupShadowingType.GROUP_REMOVED;
        }
    }

    public AddUpdateSets<UserTemplateWithCredentialAndAttributes, UserTemplate> getUsersToAddAndUpdate(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers, Date syncStartDate) throws OperationFailedException {
        this.logDuplicates(remoteUsers);
        if (!((Boolean)SystemProperties.ALLOW_DUPLICATED_EXTERNAL_IDS_IN_SYNC.getValue()).booleanValue()) {
            return this.getUsersToAddAndUpdateOld(remoteUsers, syncStartDate);
        }
        ImmutableSet.Builder usersToAdd = ImmutableSet.builder();
        ImmutableSet.Builder usersToUpdate = ImmutableSet.builder();
        List<Pair<com.atlassian.crowd.model.user.User, TimestampedUser>> matches = DbCachingRemoteChangeOperations.matchUsers(remoteUsers, this.findInternalUsersUpdatedBefore(null));
        logger.info("scanning [ {} ] users to add or update", (Object)remoteUsers.size());
        TimedProgressOperation operation = new TimedProgressOperation("scanning users to add or update", remoteUsers.size(), logger);
        for (Pair<com.atlassian.crowd.model.user.User, TimestampedUser> match : matches) {
            operation.incrementProgress();
            com.atlassian.crowd.model.user.User remoteUser = (com.atlassian.crowd.model.user.User)match.getLeft();
            TimestampedUser internalUser = (TimestampedUser)match.getRight();
            if (internalUser != null) {
                if (StringUtils.isEmpty((CharSequence)internalUser.getExternalId()) && !remoteUser.getName().equals(internalUser.getName())) {
                    logger.warn("remote username '{}' casing differs from local username '{}'. User details will be kept updated, but the username cannot be updated", (Object)remoteUser.getName(), (Object)internalUser.getName());
                }
                if (syncStartDate != null && internalUser.getUpdatedDate() != null && internalUser.getUpdatedDate().compareTo(syncStartDate) >= 0) {
                    logger.debug("user '{}' has been updated since the synchronisation started, skipping", (Object)remoteUser.getName());
                    continue;
                }
                if (!this.hasChanged(remoteUser, (com.atlassian.crowd.model.user.User)internalUser)) {
                    logger.trace("user '{}' unmodified, skipping", (Object)remoteUser.getName());
                    continue;
                }
                UserTemplate userToUpdate = DbCachingRemoteChangeOperations.makeUserTemplate(remoteUser);
                if (StringUtils.isEmpty((CharSequence)internalUser.getExternalId())) {
                    userToUpdate.setName(internalUser.getName());
                }
                if (!this.remoteDirectory.supportsInactiveAccounts() || this.internalDirectory.isLocalUserStatusEnabled()) {
                    userToUpdate.setActive(internalUser.isActive());
                }
                usersToUpdate.add((Object)userToUpdate);
                continue;
            }
            logger.debug("user '{}' not found, adding", (Object)remoteUser.getName());
            usersToAdd.add((Object)new UserTemplateWithCredentialAndAttributes((com.atlassian.crowd.model.user.User)DbCachingRemoteChangeOperations.makeUserTemplate(remoteUser), PasswordCredential.NONE));
        }
        return new AddUpdateSets((Set)usersToAdd.build(), (Set)usersToUpdate.build());
    }

    protected static List<Pair<com.atlassian.crowd.model.user.User, TimestampedUser>> matchUsers(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers, Collection<? extends TimestampedUser> internalUsers) {
        HashSet<String> duplicatedExternalIds = new HashSet<String>();
        HashMap<String, TimestampedUser> internalUsersByExternalId = new HashMap<String, TimestampedUser>();
        for (TimestampedUser timestampedUser : internalUsers) {
            if (!StringUtils.isNotEmpty((CharSequence)timestampedUser.getExternalId()) || internalUsersByExternalId.put(timestampedUser.getExternalId(), timestampedUser) == null) continue;
            duplicatedExternalIds.add(timestampedUser.getExternalId());
        }
        IdentifierMap internalUsersByName = IdentifierMap.index(internalUsers, Principal::getName);
        HashSet<String> hashSet = new HashSet<String>(DbCachingRemoteChangeOperations.externalIdsOf(remoteUsers));
        internalUsersByExternalId.keySet().removeAll(duplicatedExternalIds);
        hashSet.removeAll(duplicatedExternalIds);
        ArrayList<Pair> resultsMatchedByName = new ArrayList<Pair>();
        ArrayList<Pair> resultsMatchedById = new ArrayList<Pair>();
        for (com.atlassian.crowd.model.user.User user : remoteUsers) {
            TimestampedUser internalUserMatchedByName;
            TimestampedUser matchedUser = (TimestampedUser)internalUsersByExternalId.get(user.getExternalId());
            if (matchedUser == null && (internalUserMatchedByName = (TimestampedUser)internalUsersByName.get((Object)user.getName())) != null && !hashSet.contains(internalUserMatchedByName.getExternalId())) {
                matchedUser = internalUserMatchedByName;
            }
            if (matchedUser != null && StringUtils.isNotEmpty((CharSequence)matchedUser.getExternalId()) && Objects.equals(user.getExternalId(), matchedUser.getExternalId())) {
                resultsMatchedById.add(Pair.of((Object)user, (Object)matchedUser));
                continue;
            }
            resultsMatchedByName.add(Pair.of((Object)user, (Object)matchedUser));
        }
        ArrayList<Pair<com.atlassian.crowd.model.user.User, TimestampedUser>> results = new ArrayList<Pair<com.atlassian.crowd.model.user.User, TimestampedUser>>(resultsMatchedByName);
        results.addAll(resultsMatchedById);
        return results;
    }

    public AddUpdateSets<UserTemplateWithCredentialAndAttributes, UserTemplate> getUsersToAddAndUpdateOld(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers, Date syncStartDate) throws OperationFailedException {
        ImmutableSet.Builder usersToAdd = ImmutableSet.builder();
        ImmutableSet.Builder usersToUpdate = ImmutableSet.builder();
        IdentifierMap internalUsersByName = IdentifierMap.index(this.findInternalUsersUpdatedBefore(null), Principal::getName);
        Map<String, TimestampedUser> internalUsersByExternalId = DbCachingRemoteChangeOperations.mapUsersByExternalId(internalUsersByName.values());
        Set<String> remoteUserExternalIds = DbCachingRemoteChangeOperations.externalIdsOf(remoteUsers);
        logger.info("scanning [ {} ] users to add or update", (Object)remoteUsers.size());
        TimedProgressOperation operation = new TimedProgressOperation("scanning users to add or update", remoteUsers.size(), logger);
        for (com.atlassian.crowd.model.user.User user : remoteUsers) {
            TimestampedUser internalUserMatchedByName;
            operation.incrementProgress();
            TimestampedUser internalUser = null;
            if (StringUtils.isNotEmpty((CharSequence)user.getExternalId())) {
                internalUser = internalUsersByExternalId.get(user.getExternalId());
            }
            if (internalUser == null && (internalUserMatchedByName = (TimestampedUser)internalUsersByName.get(user.getName())) != null && (StringUtils.isEmpty((CharSequence)internalUserMatchedByName.getExternalId()) || !remoteUserExternalIds.contains(internalUserMatchedByName.getExternalId()))) {
                internalUser = internalUserMatchedByName;
            }
            if (internalUser != null) {
                if (StringUtils.isEmpty((CharSequence)internalUser.getExternalId()) && !user.getName().equals(internalUser.getName())) {
                    logger.warn("remote username '{}' casing differs from local username '{}'. User details will be kept updated, but the username cannot be updated", (Object)user.getName(), (Object)internalUser.getName());
                }
                if (syncStartDate != null && internalUser.getUpdatedDate() != null && internalUser.getUpdatedDate().compareTo(syncStartDate) >= 0) {
                    logger.debug("user '{}' has been updated since the synchronisation started, skipping", (Object)user.getName());
                    continue;
                }
                if (!this.hasChanged(user, (com.atlassian.crowd.model.user.User)internalUser)) {
                    logger.trace("user '{}' unmodified, skipping", (Object)user.getName());
                    continue;
                }
                UserTemplate userToUpdate = DbCachingRemoteChangeOperations.makeUserTemplate(user);
                if (StringUtils.isEmpty((CharSequence)internalUser.getExternalId())) {
                    userToUpdate.setName(internalUser.getName());
                }
                if (!this.remoteDirectory.supportsInactiveAccounts() || this.internalDirectory.isLocalUserStatusEnabled()) {
                    userToUpdate.setActive(internalUser.isActive());
                }
                usersToUpdate.add((Object)userToUpdate);
                continue;
            }
            logger.debug("user '{}' not found, adding", (Object)user.getName());
            usersToAdd.add((Object)new UserTemplateWithCredentialAndAttributes((com.atlassian.crowd.model.user.User)DbCachingRemoteChangeOperations.makeUserTemplate(user), PasswordCredential.NONE));
        }
        return new AddUpdateSets((Set)usersToAdd.build(), (Set)usersToUpdate.build());
    }

    protected static Map<String, TimestampedUser> mapUsersByExternalId(Collection<TimestampedUser> users) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (TimestampedUser user : users) {
            if (!StringUtils.isNotEmpty((CharSequence)user.getExternalId())) continue;
            builder.put((Object)user.getExternalId(), (Object)user);
        }
        return builder.build();
    }

    private static Set<String> externalIdsOf(Collection<? extends com.atlassian.crowd.model.user.User> users) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        for (com.atlassian.crowd.model.user.User user : users) {
            if (!StringUtils.isNotEmpty((CharSequence)user.getExternalId())) continue;
            builder.add((Object)user.getExternalId());
        }
        return builder.build();
    }

    public void addOrUpdateCachedUser(com.atlassian.crowd.model.user.User user) throws OperationFailedException {
        UserTemplate newUser = new UserTemplate(user);
        newUser.setDirectoryId(this.getDirectoryId());
        try {
            Directory directory = this.getDirectory();
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
            try {
                com.atlassian.crowd.model.user.User addedUser = this.internalDirectory.addUser(newUser, PasswordCredential.NONE);
                this.publishEvent((DirectoryEvent)new UserCreatedFromDirectorySynchronisationEvent((Object)this, immutableDirectory, addedUser), true);
            }
            catch (UserAlreadyExistsException e) {
                try {
                    ImmutableUser originalUser = ImmutableUser.from((com.atlassian.crowd.model.user.User)this.internalDirectory.findUserByName(newUser.getName()));
                    com.atlassian.crowd.model.user.User updatedUser = this.internalDirectory.updateUser(newUser);
                    this.publishEvent((DirectoryEvent)new UserEditedEvent((Object)this, (Directory)immutableDirectory, updatedUser, (com.atlassian.crowd.model.user.User)originalUser), true);
                }
                catch (UserNotFoundException unfe) {
                    logger.debug("User was deleted in the middle of the transaction", (Throwable)unfe);
                }
            }
            catch (InvalidCredentialException e) {
                throw new RuntimeException(e);
            }
        }
        catch (InvalidUserException e) {
            logger.error("Could not add or update user '{}': ", (Object)newUser.getName(), (Object)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void deleteCachedUser(String username) throws OperationFailedException {
        try {
            this.internalDirectory.removeUser(username);
            this.publishEvent((DirectoryEvent)new UsersDeletedEvent((Object)this, this.getDirectory(), Collections.singleton(username)), true);
            this.publishEvent((DirectoryEvent)new UserDeletedEvent((Object)this, this.getDirectory(), username), true);
        }
        catch (UserNotFoundException e) {
            logger.debug("Deleted user does not exist locally", (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void addOrUpdateCachedGroup(Group group) throws OperationFailedException {
        GroupTemplate newGroup = new GroupTemplate(group);
        newGroup.setDirectoryId(this.getDirectoryId());
        try {
            Directory directory = this.getDirectory();
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)directory);
            try {
                Group updatedGroup = this.internalDirectory.updateGroup(newGroup);
                this.publishEvent((DirectoryEvent)new GroupUpdatedEvent((Object)this, (Directory)immutableDirectory, updatedGroup), true);
            }
            catch (GroupNotFoundException e) {
                Group addedGroup = this.internalDirectory.addGroup(newGroup);
                this.publishEvent((DirectoryEvent)new GroupCreatedEvent((Object)this, (Directory)immutableDirectory, addedGroup), true);
            }
            catch (ReadOnlyGroupException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        catch (InvalidGroupException e) {
            logger.error("Could not add or update group '{}': ", (Object)newGroup.getName(), (Object)e);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public void deleteCachedGroup(String groupName) throws OperationFailedException {
        try {
            this.internalDirectory.removeGroup(groupName);
            this.publishEvent((DirectoryEvent)new GroupDeletedEvent((Object)this, this.getDirectory(), groupName), true);
        }
        catch (GroupNotFoundException e) {
            logger.debug("Deleted group does not exist locally", (Throwable)e);
        }
        catch (DirectoryNotFoundException | ReadOnlyGroupException e) {
            throw new OperationFailedException(e);
        }
    }

    public void addUserToGroup(String username, String groupName) throws OperationFailedException {
        try {
            this.internalDirectory.addUserToGroup(username, groupName);
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)this.getDirectory());
            this.publishEvent((DirectoryEvent)new GroupMembershipCreatedEvent((Object)this, (Directory)immutableDirectory, username, groupName, MembershipType.GROUP_USER), true);
            this.publishEvent((DirectoryEvent)new GroupMembershipsCreatedEvent((Object)this, (Directory)immutableDirectory, (Iterable)ImmutableList.of((Object)username), groupName, MembershipType.GROUP_USER), true);
        }
        catch (GroupNotFoundException e) {
            logger.debug("Cannot have membership without a group", (Throwable)e);
        }
        catch (UserNotFoundException e) {
            logger.debug("Cannot have membership without a user", (Throwable)e);
        }
        catch (DirectoryNotFoundException | ReadOnlyGroupException e) {
            throw new OperationFailedException(e);
        }
        catch (MembershipAlreadyExistsException e) {
            logger.debug("The membership specified already exists", (Throwable)e);
        }
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws OperationFailedException {
        try {
            this.internalDirectory.addGroupToGroup(childGroup, parentGroup);
            ImmutableDirectory immutableDirectory = ImmutableDirectory.from((Directory)this.getDirectory());
            this.publishEvent((DirectoryEvent)new GroupMembershipCreatedEvent((Object)this, (Directory)immutableDirectory, childGroup, parentGroup, MembershipType.GROUP_GROUP), true);
            this.publishEvent((DirectoryEvent)new GroupMembershipsCreatedEvent((Object)this, (Directory)immutableDirectory, (Iterable)ImmutableList.of((Object)childGroup), parentGroup, MembershipType.GROUP_GROUP), true);
        }
        catch (GroupNotFoundException e) {
            logger.debug("Cannot have membership without a group", (Throwable)e);
        }
        catch (InvalidMembershipException e) {
            logger.debug("Later events should fix this problem", (Throwable)e);
        }
        catch (DirectoryNotFoundException | ReadOnlyGroupException e) {
            throw new OperationFailedException(e);
        }
        catch (MembershipAlreadyExistsException e) {
            logger.debug("The membership specified already exists", (Throwable)e);
        }
    }

    public void removeUserFromGroup(String username, String groupName) throws OperationFailedException {
        try {
            this.internalDirectory.removeUserFromGroup(username, groupName);
            this.publishEvent((DirectoryEvent)new GroupMembershipDeletedEvent((Object)this, this.getDirectory(), username, groupName, MembershipType.GROUP_USER), true);
            this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, this.getDirectory(), (Iterable)ImmutableList.of((Object)username), groupName, MembershipType.GROUP_USER), true);
        }
        catch (MembershipNotFoundException e) {
            logger.debug("Membership has already been removed", (Throwable)e);
        }
        catch (GroupNotFoundException e) {
            logger.debug("Cannot have membership without a group", (Throwable)e);
        }
        catch (UserNotFoundException e) {
            logger.debug("Cannot have membership without a user", (Throwable)e);
        }
        catch (DirectoryNotFoundException | ReadOnlyGroupException e) {
            throw new OperationFailedException(e);
        }
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws OperationFailedException {
        try {
            this.internalDirectory.removeGroupFromGroup(childGroup, parentGroup);
            this.publishEvent((DirectoryEvent)new GroupMembershipDeletedEvent((Object)this, this.getDirectory(), childGroup, parentGroup, MembershipType.GROUP_GROUP), true);
            this.publishEvent((DirectoryEvent)new GroupMembershipsDeletedEvent((Object)this, this.getDirectory(), (Iterable)ImmutableList.of((Object)childGroup), parentGroup, MembershipType.GROUP_GROUP), true);
        }
        catch (MembershipNotFoundException e) {
            logger.debug("Membership has already been removed", (Throwable)e);
        }
        catch (GroupNotFoundException e) {
            logger.debug("Cannot have membership without a group", (Throwable)e);
        }
        catch (InvalidMembershipException e) {
            logger.debug("Later events should fix this problem", (Throwable)e);
        }
        catch (DirectoryNotFoundException | ReadOnlyGroupException e) {
            throw new OperationFailedException(e);
        }
    }

    public void syncGroupMembershipsForUser(String childUsername, Set<String> parentGroupNames) throws OperationFailedException {
        Set remoteParentGroupNames = IdentifierUtils.toLowerCase(parentGroupNames);
        Set localParentGroupNames = IdentifierUtils.toLowerCase((Collection)this.internalDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).parentsOf(EntityDescriptor.user()).withName(childUsername).returningAtMost(-1)));
        Sets.SetView addedParentGroupNames = Sets.difference((Set)remoteParentGroupNames, (Set)localParentGroupNames);
        for (String addedParentGroupName : addedParentGroupNames) {
            this.addUserToGroup(childUsername, addedParentGroupName);
        }
        Sets.SetView removedParentGroupNames = Sets.difference((Set)localParentGroupNames, (Set)remoteParentGroupNames);
        for (String removedParentGroupName : removedParentGroupNames) {
            this.removeUserFromGroup(childUsername, removedParentGroupName);
        }
    }

    public void syncGroupMembershipsAndMembersForGroup(String groupName, Set<String> parentGroupNames, Set<String> childGroupNames) throws OperationFailedException {
        Set remoteParentGroupNames = IdentifierUtils.toLowerCase(parentGroupNames);
        Set localParentGroupNames = IdentifierUtils.toLowerCase((Collection)this.internalDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).parentsOf(EntityDescriptor.group((GroupType)GroupType.GROUP)).withName(groupName).returningAtMost(-1)));
        Sets.SetView addedParentGroupNames = Sets.difference((Set)remoteParentGroupNames, (Set)localParentGroupNames);
        for (Object addedParentGroupName : addedParentGroupNames) {
            this.addGroupToGroup(groupName, (String)addedParentGroupName);
        }
        Sets.SetView removedParentGroupNames = Sets.difference((Set)localParentGroupNames, (Set)remoteParentGroupNames);
        for (String removedParentGroupName : removedParentGroupNames) {
            this.removeGroupFromGroup(groupName, removedParentGroupName);
        }
        Set remoteChildGroupNames = IdentifierUtils.toLowerCase(childGroupNames);
        Set localChildGroupNames = IdentifierUtils.toLowerCase((Collection)this.internalDirectory.searchGroupRelationships(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group((GroupType)GroupType.GROUP)).childrenOf(EntityDescriptor.group((GroupType)GroupType.GROUP)).withName(groupName).returningAtMost(-1)));
        Sets.SetView addedChildGroupNames = Sets.difference((Set)remoteChildGroupNames, (Set)localChildGroupNames);
        for (String addedChildGroupName : addedChildGroupNames) {
            this.addGroupToGroup(addedChildGroupName, groupName);
        }
        Sets.SetView removedChildGroupNames = Sets.difference((Set)localChildGroupNames, (Set)remoteChildGroupNames);
        for (String removedChildGroupName : removedChildGroupNames) {
            this.removeGroupFromGroup(removedChildGroupName, groupName);
        }
    }

    public UserWithAttributes findUserWithAttributesByName(String name) throws UserNotFoundException, OperationFailedException {
        return this.internalDirectory.findUserWithAttributesByName(name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(String name) throws GroupNotFoundException, OperationFailedException {
        return this.internalDirectory.findGroupWithAttributesByName(name);
    }

    public Map<String, String> findUsersByExternalIds(Set<String> externalIds) {
        return this.userDao.findByExternalIds(this.getDirectoryId(), externalIds);
    }

    public Map<String, String> findGroupsByExternalIds(Set<String> externalIds) throws OperationFailedException {
        return this.groupDao.findByExternalIds(this.getDirectoryId(), externalIds);
    }

    public Map<String, String> findGroupsExternalIdsByNames(Set<String> groupNames) throws OperationFailedException {
        return this.groupDao.findExternalIdsByNames(this.getDirectoryId(), groupNames);
    }

    public void applySyncingUserAttributes(String userName, Set<String> deletedAttributes, Map<String, Set<String>> storedAttributes) throws UserNotFoundException, OperationFailedException {
        Object filteredAttributes;
        if (deletedAttributes != null) {
            filteredAttributes = deletedAttributes.stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).collect(Collectors.toList());
            Iterator iterator = filteredAttributes.iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                this.internalDirectory.removeUserAttributes(userName, key);
            }
        }
        if (MapUtils.isNotEmpty(storedAttributes)) {
            filteredAttributes = storedAttributes.entrySet().stream().filter(AttributePredicates.SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            this.internalDirectory.storeUserAttributes(userName, (Map)filteredAttributes);
        }
    }

    public void applySyncingGroupAttributes(String groupName, Set<String> deletedAttributes, Map<String, Set<String>> storedAttributes) throws GroupNotFoundException, OperationFailedException {
        Object filteredAttributes;
        if (deletedAttributes != null) {
            filteredAttributes = deletedAttributes.stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).collect(Collectors.toList());
            Iterator iterator = filteredAttributes.iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                this.internalDirectory.removeGroupAttributes(groupName, key);
            }
        }
        if (MapUtils.isNotEmpty(storedAttributes)) {
            filteredAttributes = storedAttributes.entrySet().stream().filter(AttributePredicates.SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            this.internalDirectory.storeGroupAttributes(groupName, (Map)filteredAttributes);
        }
    }

    public Set<String> getAllUserGuids() throws OperationFailedException {
        return this.internalDirectory.getAllUserExternalIds();
    }

    public Set<String> getAllGroupGuids() throws OperationFailedException {
        try {
            return this.groupDao.getAllExternalIds(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public long getUserCount() throws OperationFailedException {
        return this.internalDirectory.getUserCount();
    }

    public long getGroupCount() throws OperationFailedException {
        try {
            return this.groupDao.getGroupCount(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public long getExternalCachedGroupCount() throws OperationFailedException {
        try {
            return this.groupDao.getExternalGroupCount(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    public Set<String> getAllLocalGroupNames() throws OperationFailedException {
        try {
            return this.groupDao.getLocalGroupNames(this.getDirectoryId());
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException((Throwable)e);
        }
    }

    private static void logFailures(InternalRemoteDirectory directory, BatchResult<? extends DirectoryEntity> result) {
        if (result.hasFailures()) {
            String directoryName = directory.getDescriptiveName();
            for (DirectoryEntity failedEntity : result.getFailedEntities()) {
                logger.warn("Could not add the following entity to the directory '{}': '{}'", (Object)directoryName, (Object)failedEntity.getName());
            }
        }
    }

    private void logDuplicates(Collection<? extends com.atlassian.crowd.model.user.User> remoteUsers) {
        if (logger.isTraceEnabled()) {
            logger.trace("Starting scanning for not unique users in remote directory: {}.", (Object)this.remoteDirectory.getDirectoryId());
            HashSet<com.atlassian.crowd.model.user.User> unique = new HashSet<com.atlassian.crowd.model.user.User>();
            for (com.atlassian.crowd.model.user.User user : remoteUsers) {
                if (unique.add(user)) continue;
                logger.trace("user [ {}, externalId: {} ] is not unique in remote directory {}.", new Object[]{user.getName(), user.getExternalId(), user.getDirectoryId()});
            }
            logger.trace("Completed scanning for not unique users in remote directory: {}.", (Object)this.remoteDirectory.getDirectoryId());
        }
    }
}

