/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.attribute.AttributePredicates
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.audit.AuditLogEntityType
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.audit.ImmutableAuditLogChangeset
 *  com.atlassian.crowd.audit.ImmutableAuditLogChangeset$Builder
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity
 *  com.atlassian.crowd.audit.ImmutableAuditLogEntity$Builder
 *  com.atlassian.crowd.directory.AbstractForwardingDirectory
 *  com.atlassian.crowd.directory.FastEntityCountProvider
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.MultiValuesQueriesSupport
 *  com.atlassian.crowd.directory.RemoteCrowdDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectoryProperties$SyncGroupMembershipsAfterAuth
 *  com.atlassian.crowd.directory.hybrid.LocalGroupHandler
 *  com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory
 *  com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory
 *  com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult
 *  com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher
 *  com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache
 *  com.atlassian.crowd.embedded.api.Attributes
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.impl.IdentifierSet
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.exception.DirectoryNotFoundException
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.GroupNotFoundException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.InvalidAuthenticationException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidGroupException
 *  com.atlassian.crowd.exception.InvalidMembershipException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.MembershipAlreadyExistsException
 *  com.atlassian.crowd.exception.MembershipNotFoundException
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.manager.audit.AuditService
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.model.directory.SynchronisationStatusKey
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.group.InternalDirectoryGroup
 *  com.atlassian.crowd.model.user.InternalUser
 *  com.atlassian.crowd.model.user.TimestampedUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.TimedOperation
 *  com.atlassian.crowd.util.persistence.hibernate.batch.BatchConfigParser
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory;

import com.atlassian.crowd.attribute.AttributePredicates;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.AuditLogEntityType;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.audit.ImmutableAuditLogChangeset;
import com.atlassian.crowd.audit.ImmutableAuditLogEntity;
import com.atlassian.crowd.directory.AbstractForwardingDirectory;
import com.atlassian.crowd.directory.FastEntityCountProvider;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.MultiValuesQueriesSupport;
import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectoryProperties;
import com.atlassian.crowd.directory.hybrid.LocalGroupHandler;
import com.atlassian.crowd.directory.ldap.cache.CacheRefresherFactory;
import com.atlassian.crowd.directory.ldap.cache.DirectoryCacheFactory;
import com.atlassian.crowd.directory.synchronisation.CacheSynchronisationResult;
import com.atlassian.crowd.directory.synchronisation.cache.CacheRefresher;
import com.atlassian.crowd.directory.synchronisation.cache.DirectoryCache;
import com.atlassian.crowd.embedded.api.Attributes;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.impl.IdentifierSet;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.exception.DirectoryNotFoundException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.GroupNotFoundException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidGroupException;
import com.atlassian.crowd.exception.InvalidMembershipException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.MembershipAlreadyExistsException;
import com.atlassian.crowd.exception.MembershipNotFoundException;
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.manager.audit.AuditService;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.model.directory.SynchronisationStatusKey;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.group.InternalDirectoryGroup;
import com.atlassian.crowd.model.user.InternalUser;
import com.atlassian.crowd.model.user.TimestampedUser;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.TimedOperation;
import com.atlassian.crowd.util.persistence.hibernate.batch.BatchConfigParser;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbCachingRemoteDirectory
extends AbstractForwardingDirectory
implements RemoteDirectory,
SynchronisableDirectory,
FastEntityCountProvider,
MultiValuesQueriesSupport {
    private static final Logger log = LoggerFactory.getLogger(DbCachingRemoteDirectory.class);
    private final RemoteDirectory remoteDirectory;
    private final LocalGroupHandler localGroupHandler;
    private final InternalRemoteDirectory internalDirectory;
    private final DirectoryCacheFactory directoryCacheFactory;
    private final CacheRefresherFactory cacheRefresherFactory;
    private final AuditService auditService;
    private final String directoryName;
    private final AuditLogUserMapper auditLogUserMapper;
    private final EventPublisher eventPublisher;
    private final DirectoryDao directoryDao;
    private final BatchConfigParser batchConfigParser;

    public DbCachingRemoteDirectory(RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory, DirectoryCacheFactory directoryCacheFactory, CacheRefresherFactory cacheRefresherFactory, AuditService auditService, AuditLogUserMapper auditLogUserMapper, String directoryName, EventPublisher eventPublisher, DirectoryDao directoryDao, BatchConfigParser batchConfigParser) {
        this(remoteDirectory, internalDirectory, directoryCacheFactory, new LocalGroupHandler(internalDirectory), cacheRefresherFactory, auditService, auditLogUserMapper, directoryName, eventPublisher, directoryDao, batchConfigParser);
    }

    private DbCachingRemoteDirectory(RemoteDirectory remoteDirectory, InternalRemoteDirectory internalDirectory, DirectoryCacheFactory directoryCacheFactory, LocalGroupHandler localGroupHandler, CacheRefresherFactory cacheRefresherFactory, AuditService auditService, AuditLogUserMapper auditLogUserMapper, String directoryName, EventPublisher eventPublisher, DirectoryDao directoryDao, BatchConfigParser batchConfigParser) {
        this.remoteDirectory = remoteDirectory;
        this.internalDirectory = internalDirectory;
        this.directoryCacheFactory = directoryCacheFactory;
        this.localGroupHandler = localGroupHandler;
        this.cacheRefresherFactory = cacheRefresherFactory;
        this.auditService = auditService;
        this.directoryName = directoryName;
        this.auditLogUserMapper = auditLogUserMapper;
        this.eventPublisher = eventPublisher;
        this.directoryDao = directoryDao;
        this.batchConfigParser = batchConfigParser;
    }

    public long getDirectoryId() {
        return this.remoteDirectory.getDirectoryId();
    }

    public void setDirectoryId(long directoryId) {
        throw new UnsupportedOperationException("You cannot mutate the directoryID of " + ((Object)((Object)this)).getClass().getName());
    }

    public String getDescriptiveName() {
        return this.remoteDirectory.getDescriptiveName();
    }

    public void setAttributes(Map<String, String> attributes) {
        throw new UnsupportedOperationException("You cannot mutate the attributes of " + ((Object)((Object)this)).getClass().getName());
    }

    public User authenticate(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        if (this.remoteDirectory instanceof RemoteCrowdDirectory) {
            return this.authenticateAndUpdateInternalUser(name, credential);
        }
        return this.performAuthenticationAndUpdateAttributes(name, credential);
    }

    private User performAuthenticationAndUpdateAttributes(String name, PasswordCredential credential) throws UserNotFoundException, ExpiredCredentialException, InactiveAccountException, OperationFailedException, InvalidAuthenticationException {
        HashMap<String, Set<String>> attributesToUpdate = new HashMap<String, Set<String>>();
        try {
            TimestampedUser internalUser;
            User authenticatedUser = this.authenticateAndUpdateInternalUser(name, credential);
            if (!this.remoteDirectory.supportsInactiveAccounts() && !(internalUser = this.internalDirectory.findUserByName(name)).isActive()) {
                throw new InactiveAccountException(name);
            }
            attributesToUpdate.put("invalidPasswordAttempts", Collections.singleton(Long.toString(0L)));
            attributesToUpdate.put("lastAuthenticated", Collections.singleton(Long.toString(System.currentTimeMillis())));
            this.storeUserAttributes(name, attributesToUpdate);
            return authenticatedUser;
        }
        catch (InvalidAuthenticationException e) {
            UserWithAttributes user = this.findUserWithAttributesByName(name);
            long currentInvalidAttempts = NumberUtils.toLong((String)user.getValue("invalidPasswordAttempts"), (long)0L);
            attributesToUpdate.put("invalidPasswordAttempts", Collections.singleton(Long.toString(++currentInvalidAttempts)));
            this.storeUserAttributes(name, attributesToUpdate);
            throw e;
        }
    }

    @VisibleForTesting
    protected User authenticateAndUpdateInternalUser(String name, PasswordCredential credential) throws UserNotFoundException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, OperationFailedException {
        User remoteUser = this.remoteDirectory.authenticate(name, credential);
        this.updateUserFromRemoteDirectory(remoteUser);
        return remoteUser;
    }

    public User updateUserFromRemoteDirectory(@Nonnull User remoteUser) throws OperationFailedException, UserNotFoundException {
        TimestampedUser internalUser;
        Set<String> remoteUserMemberships = null;
        if (this.remoteDirectory.getLocallyFilteredGroupNames().isPresent()) {
            remoteUserMemberships = this.fetchRemoteUserMemberships(remoteUser.getName());
            this.checkIfUserCanBeUpdatedWithCurrentGroupFiltering(remoteUser.getName(), remoteUserMemberships);
        }
        boolean internalUserNotSynchronizedYet = (internalUser = this.findLocalUserByExternalIdOrName(remoteUser)) == null;
        try {
            if (internalUserNotSynchronizedYet) {
                internalUser = this.addInternalUser((UserWithAttributes)UserTemplateWithAttributes.toUserWithNoAttributes((User)remoteUser));
            } else {
                String oldUsername = internalUser.getName();
                if (!oldUsername.equals(remoteUser.getName())) {
                    internalUser = this.internalDirectory.forceRenameUser((User)internalUser, remoteUser.getName());
                    Directory directory = this.directoryDao.findById(remoteUser.getDirectoryId());
                    this.eventPublisher.publish((Object)new UserRenamedEvent((Object)this, directory, (User)internalUser, oldUsername));
                }
                internalUser = this.updateUserAndSetActiveFlag(remoteUser, (User)internalUser);
            }
        }
        catch (InvalidUserException ex) {
            throw new OperationFailedException((Throwable)ex);
        }
        catch (DirectoryNotFoundException e) {
            throw new OperationFailedException("Invalid directory: directory " + remoteUser.getDirectoryId() + " not found", (Throwable)e);
        }
        if (this.shouldSyncGroupMembershipAfterUserAuthentication(internalUserNotSynchronizedYet)) {
            if (remoteUserMemberships == null) {
                remoteUserMemberships = this.fetchRemoteUserMemberships(remoteUser.getName());
            }
            this.updateGroupsMembershipOnLogin(remoteUser, remoteUserMemberships);
        }
        return internalUser;
    }

    public User userAuthenticated(String username) throws OperationFailedException, UserNotFoundException, InactiveAccountException {
        User authenticated = super.userAuthenticated(username);
        if (!(this.getAuthoritativeDirectory() instanceof RemoteCrowdDirectory)) {
            this.storeUserAttributes(authenticated.getName(), Collections.singletonMap("lastAuthenticated", Collections.singleton(Long.toString(System.currentTimeMillis()))));
        }
        return authenticated;
    }

    @VisibleForTesting
    protected void checkIfUserCanBeUpdatedWithCurrentGroupFiltering(String username, Set<String> userRemoteGroupNames) throws OperationFailedException, UserNotFoundException {
        block2: {
            try {
                log.debug("Local group filtering is enabled for directory {} so checking if user '{}' exists in cache", (Object)this.remoteDirectory.getDirectoryId(), (Object)username);
                this.internalDirectory.findUserByName(username);
            }
            catch (UserNotFoundException e) {
                log.debug("User '{}' not found in cache. Now checking if the user has membership to any non-local group in cache", (Object)username);
                if (this.isAnyGroupExistingInCache(userRemoteGroupNames)) break block2;
                log.debug("User '{}' does not have membership to any group in cache. Aborting.", (Object)username);
                throw e;
            }
        }
    }

    private boolean isAnyGroupExistingInCache(Set<String> userRemoteGroupNames) throws OperationFailedException {
        if (userRemoteGroupNames.isEmpty()) {
            return false;
        }
        Iterable batches = Iterables.partition(userRemoteGroupNames, (int)this.batchConfigParser.getCrowdQueryBatchSize());
        for (List batch : batches) {
            List batchResult = this.internalDirectory.searchGroups(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Combine.allOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)GroupTermKeys.NAME).exactlyMatchingAny((Collection)batch), Restriction.on((Property)GroupTermKeys.LOCAL).exactlyMatching((Object)false)})).returningAtMost(1));
            if (batchResult.isEmpty()) continue;
            return true;
        }
        return false;
    }

    private Set<String> fetchRemoteUserMemberships(String username) throws OperationFailedException {
        MembershipQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(username).returningAtMost(-1);
        return ImmutableSet.copyOf((Collection)this.remoteDirectory.searchGroupRelationships(query));
    }

    private TimestampedUser findLocalUserByExternalIdOrName(User remoteUser) {
        TimestampedUser user = this.findLocalUserByExternalId(remoteUser.getExternalId());
        if (user != null) {
            return user;
        }
        try {
            return this.internalDirectory.findUserByName(remoteUser.getName());
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    private TimestampedUser findLocalUserByExternalId(String externalId) {
        try {
            if (externalId != null && !externalId.isEmpty()) {
                return this.internalDirectory.findUserByExternalId(externalId);
            }
            return null;
        }
        catch (UserNotFoundException e) {
            return null;
        }
    }

    @VisibleForTesting
    protected User updateUserAndSetActiveFlag(User remoteUser, User internalUser) throws UserNotFoundException, InvalidUserException, OperationFailedException {
        this.preventExternalIdDuplication(remoteUser, internalUser);
        UserTemplate userTemplate = new UserTemplate(remoteUser);
        if (!this.remoteDirectory.supportsInactiveAccounts() || this.internalDirectory.isLocalUserStatusEnabled()) {
            userTemplate.setActive(internalUser.isActive());
        }
        return this.internalDirectory.updateUser(userTemplate);
    }

    @VisibleForTesting
    protected void updateGroupsMembershipOnLogin(User user, Set<String> userRemoteGroupNames) throws OperationFailedException, UserNotFoundException {
        log.debug("Updating groups on login for user '{}'", (Object)user.getName());
        MembershipQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(user.getName()).returningAtMost(-1);
        ImmutableSet userLocalGroupNames = ImmutableSet.copyOf((Collection)this.internalDirectory.searchGroupRelationships(query));
        Predicate<String> isLocalGroup = arg_0 -> ((IdentifierSet)this.findAllLocalGroups()).contains(arg_0);
        Set<String> groupsToAddUser = userRemoteGroupNames.stream().filter(IdentifierUtils.containsIdentifierPredicate((Collection)userLocalGroupNames).negate()).filter(isLocalGroup.negate()).collect(Collectors.toSet());
        Set<String> groupsToRemoveUser = userLocalGroupNames.stream().filter(IdentifierUtils.containsIdentifierPredicate(userRemoteGroupNames).negate()).filter(isLocalGroup.negate()).collect(Collectors.toSet());
        if (!groupsToRemoveUser.isEmpty() && !userRemoteGroupNames.isEmpty()) {
            this.removeUserFromGroups(user, groupsToRemoveUser);
        }
        this.addUserToGroups(user, groupsToAddUser);
    }

    private void removeUserFromGroups(User user, Set<String> groupsToRemoveUser) throws UserNotFoundException, OperationFailedException {
        for (String groupName : groupsToRemoveUser) {
            try {
                this.internalDirectory.removeUserFromGroup(user.getName(), groupName);
            }
            catch (GroupNotFoundException e) {
                log.debug("group '{}' not found when trying to remove user '{}' from group during auth", (Object)groupName, (Object)user.getName());
            }
            catch (ReadOnlyGroupException e) {
                throw new RuntimeException("Failed to remove user from internal directory as group " + groupName + " is read only ", e);
            }
            catch (MembershipNotFoundException e) {
                log.debug("User '{}' is no longer member of the group '{}'", (Object)user.getName(), (Object)groupName);
            }
        }
    }

    private void addUserToGroups(User user, Set<String> groupsToAddUser) throws UserNotFoundException, OperationFailedException {
        Set<String> filteredGroupsToAddUser = this.remoteDirectory.getLocallyFilteredGroupNames().map(IdentifierUtils::containsIdentifierPredicate).map(filter -> groupsToAddUser.stream().filter(filter).collect(Collectors.toSet())).orElse(groupsToAddUser);
        for (String groupName : filteredGroupsToAddUser) {
            try {
                try {
                    this.addUserToGroupInternal(user.getName(), groupName);
                }
                catch (GroupNotFoundException e) {
                    log.debug("group '{}' doesn't exist during authentication of user '{}', trying to create", (Object)groupName, (Object)user.getName());
                    if (!this.syncRemoteGroupToInternalDirectory(groupName)) continue;
                    this.addUserToGroupInternal(user.getName(), groupName);
                }
            }
            catch (GroupNotFoundException e) {
                throw new RuntimeException("Failed adding the user " + user.getName() + " as group " + groupName + " doesn't exist", e);
            }
            catch (ReadOnlyGroupException e) {
                throw new RuntimeException("Failed to add user from internal directory as group " + groupName + " is read only ", e);
            }
        }
    }

    private boolean syncRemoteGroupToInternalDirectory(String groupName) throws OperationFailedException {
        block6: {
            GroupWithAttributes remoteGroup;
            try {
                remoteGroup = this.remoteDirectory.findGroupWithAttributesByName(groupName);
            }
            catch (GroupNotFoundException e) {
                log.debug("Tried to add group " + groupName + " to internal directory, but failed retrieving the group from remote. Ignoring.", (Throwable)e);
                return false;
            }
            try {
                GroupTemplate internalGroup = new GroupTemplate(remoteGroup.getName(), this.internalDirectory.getDirectoryId());
                internalGroup.setDescription(remoteGroup.getDescription());
                this.internalDirectory.addGroup(internalGroup);
                Map groupAttributes = remoteGroup.getKeys().stream().filter(AttributePredicates.SYNCING_ATTRIBUTE).collect(Collectors.toMap(Function.identity(), arg_0 -> ((GroupWithAttributes)remoteGroup).getValues(arg_0)));
                if (groupAttributes.isEmpty()) break block6;
                try {
                    this.internalDirectory.storeGroupAttributes(internalGroup.getName(), groupAttributes);
                }
                catch (GroupNotFoundException e) {
                    throw new OperationFailedException((Throwable)e);
                }
            }
            catch (InvalidGroupException ige) {
                log.debug("Failed to add group " + groupName, (Throwable)ige);
            }
        }
        return true;
    }

    private void preventExternalIdDuplication(User remoteUser, User internalUser) throws OperationFailedException, InvalidUserException {
        if (StringUtils.isBlank((CharSequence)remoteUser.getExternalId()) || remoteUser.getExternalId().equals(internalUser.getExternalId())) {
            return;
        }
        try {
            TimestampedUser internalUserByExternalId = this.internalDirectory.findUserByExternalId(remoteUser.getExternalId());
            if (internalUserByExternalId != null) {
                this.removeExternalId((User)internalUserByExternalId);
                log.warn("Possible user unique id duplication, removing unique id: " + internalUser.getExternalId() + " for user " + internalUser.getName());
            }
        }
        catch (UserNotFoundException userNotFoundException) {
            // empty catch block
        }
    }

    private void removeExternalId(User user) throws UserNotFoundException, InvalidUserException, OperationFailedException {
        UserTemplate userTemplate = new UserTemplate(user);
        userTemplate.setExternalId(null);
        this.internalDirectory.updateUser(userTemplate);
    }

    private IdentifierSet findAllLocalGroups() throws OperationFailedException {
        if (!this.localGroupHandler.isLocalGroupsEnabled()) {
            return new IdentifierSet();
        }
        return new IdentifierSet((Collection)this.internalDirectory.searchGroups(QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.LOCAL).exactlyMatching((Object)true)).returningAtMost(-1)));
    }

    public UserWithAttributes addUser(UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, InvalidCredentialException, UserAlreadyExistsException, OperationFailedException {
        UserTemplateWithAttributes userToBeAddedToTheRemoteServer = UserTemplateWithAttributes.toUserWithNoAttributes((User)user);
        if (this.isUserAttributeSynchronisationEnabled()) {
            user.getAttributes().entrySet().stream().filter(AttributePredicates.SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE).forEach(syncAttrEntry -> userToBeAddedToTheRemoteServer.setAttribute((String)syncAttrEntry.getKey(), (Set)syncAttrEntry.getValue()));
        }
        UserWithAttributes remoteUser = this.remoteDirectory.addUser(userToBeAddedToTheRemoteServer, credential);
        UserTemplateWithAttributes userTemplateWithAttributes = new UserTemplateWithAttributes(remoteUser);
        for (String key : user.getKeys()) {
            if (remoteUser.getValue(key) != null) continue;
            userTemplateWithAttributes.setAttribute(key, user.getValues(key));
        }
        return this.addInternalUser((UserWithAttributes)userTemplateWithAttributes);
    }

    @VisibleForTesting
    boolean isUserAttributeSynchronisationEnabled() {
        return Boolean.parseBoolean(this.remoteDirectory.getValue("userAttributesSyncEnabled"));
    }

    private UserWithAttributes addInternalUser(UserWithAttributes user) throws InvalidUserException, OperationFailedException {
        try {
            return this.internalDirectory.addUser(new UserTemplateWithAttributes(user), PasswordCredential.NONE);
        }
        catch (InvalidCredentialException ex) {
            throw new RuntimeException("Unexpected Credential Exception", ex);
        }
        catch (UserAlreadyExistsException ex) {
            try {
                this.internalDirectory.updateUser((UserTemplate)new UserTemplateWithAttributes(user));
                return this.internalDirectory.findUserWithAttributesByName(user.getName());
            }
            catch (UserNotFoundException e) {
                throw new ConcurrentModificationException(e);
            }
        }
    }

    public User updateUser(UserTemplate user) throws InvalidUserException, UserNotFoundException, OperationFailedException {
        UserTemplate remoteUserTemplate = new UserTemplate((User)user);
        if (this.remoteDirectory.supportsInactiveAccounts() && this.isLocalUserStatusEnabled()) {
            User existingRemoteUser = this.remoteDirectory.findUserByName(user.getName());
            remoteUserTemplate.setActive(existingRemoteUser.isActive());
        }
        User updatedUser = this.remoteDirectory.updateUser(remoteUserTemplate);
        UserTemplate updatedUserTemplate = new UserTemplate(updatedUser);
        if (!this.remoteDirectory.supportsInactiveAccounts() || this.isLocalUserStatusEnabled()) {
            updatedUserTemplate.setActive(user.isActive());
        }
        return this.internalDirectory.updateUser(updatedUserTemplate);
    }

    private boolean isLocalUserStatusEnabled() {
        return this.internalDirectory.isLocalUserStatusEnabled();
    }

    public void updateUserCredential(String username, PasswordCredential credential) throws UserNotFoundException, InvalidCredentialException, OperationFailedException {
        this.remoteDirectory.updateUserCredential(username, credential);
        this.auditLogUpdateCredential(username);
    }

    private void auditLogUpdateCredential(String username) {
        if (this.auditService.shouldAuditEvent()) {
            ImmutableAuditLogEntity.Builder primaryUserEntityBuilder = new ImmutableAuditLogEntity.Builder().setPrimary().setEntityName(username).setEntityType(AuditLogEntityType.USER);
            try {
                InternalUser internalUser = (InternalUser)this.internalDirectory.findUserByName(username);
                primaryUserEntityBuilder.setEntityId(internalUser.getId());
            }
            catch (UserNotFoundException e) {
                log.debug("User '{}' doesn\u2019t exist in the cache for directory {} while credential update. Audit log entry will be incomplete", (Object)username, (Object)this.getDirectoryId());
            }
            ImmutableAuditLogEntity directoryEntity = new ImmutableAuditLogEntity.Builder().setEntityId(Long.valueOf(this.getDirectoryId())).setEntityName(this.directoryName).setEntityType(AuditLogEntityType.DIRECTORY).build();
            ImmutableAuditLogChangeset auditLogChangeset = new ImmutableAuditLogChangeset.Builder().setEventType(AuditLogEventType.PASSWORD_CHANGED).addEntity(primaryUserEntityBuilder.build()).addEntity(directoryEntity).addEntry(this.auditLogUserMapper.calculatePasswordDiff()).build();
            this.auditService.saveAudit((AuditLogChangeset)auditLogChangeset);
        }
    }

    public User renameUser(String oldName, String newName) throws UserNotFoundException, InvalidUserException, OperationFailedException, UserAlreadyExistsException {
        this.remoteDirectory.renameUser(oldName, newName);
        return this.internalDirectory.renameUser(oldName, newName);
    }

    public void storeUserAttributes(String username, Map<String, Set<String>> attributes) throws UserNotFoundException, OperationFailedException {
        Map<String, Set> attributesToSync;
        if (this.isUserAttributeSynchronisationEnabled() && !(attributesToSync = attributes.entrySet().stream().filter(AttributePredicates.SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).isEmpty()) {
            this.remoteDirectory.storeUserAttributes(username, attributesToSync);
        }
        this.internalDirectory.storeUserAttributes(username, attributes);
    }

    public void removeUserAttributes(String username, String attributeName) throws UserNotFoundException, OperationFailedException {
        if (this.isUserAttributeSynchronisationEnabled() && AttributePredicates.SYNCING_ATTRIBUTE.test(attributeName)) {
            this.remoteDirectory.removeUserAttributes(username, attributeName);
        }
        this.internalDirectory.removeUserAttributes(username, attributeName);
    }

    public void removeUser(String name) throws UserNotFoundException, OperationFailedException {
        try {
            this.remoteDirectory.removeUser(name);
        }
        catch (UserNotFoundException ex) {
            this.internalDirectory.removeUser(name);
            throw ex;
        }
        this.internalDirectory.removeUser(name);
    }

    public Group addGroup(GroupTemplate group) throws InvalidGroupException, OperationFailedException {
        Group addedGroup;
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            try {
                return this.localGroupHandler.createLocalGroup(DbCachingRemoteDirectory.makeGroupTemplate((Group)group));
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
        try {
            addedGroup = this.remoteDirectory.addGroup(group);
        }
        catch (InvalidGroupException ige) {
            Group existingRemoteGroup;
            try {
                existingRemoteGroup = this.remoteDirectory.findGroupByName(group.getName());
            }
            catch (GroupNotFoundException gnfe) {
                throw ige;
            }
            this.internalDirectory.addGroup(new GroupTemplate(existingRemoteGroup));
            throw ige;
        }
        GroupTemplate templateForInternalGroup = new GroupTemplate(addedGroup);
        try {
            return this.internalDirectory.addGroup(templateForInternalGroup);
        }
        catch (InvalidGroupException ige) {
            try {
                return this.internalDirectory.updateGroup(templateForInternalGroup);
            }
            catch (GroupNotFoundException | ReadOnlyGroupException exceptionFromUpdateGroup) {
                throw new OperationFailedException((Throwable)ige);
            }
        }
    }

    public Group updateGroup(GroupTemplate group) throws InvalidGroupException, GroupNotFoundException, OperationFailedException, ReadOnlyGroupException {
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            return this.localGroupHandler.updateLocalGroup(DbCachingRemoteDirectory.makeGroupTemplate((Group)group));
        }
        Group updatedGroup = this.remoteDirectory.updateGroup(group);
        return this.internalDirectory.updateGroup(new GroupTemplate(updatedGroup));
    }

    public Group renameGroup(String oldName, String newName) throws GroupNotFoundException, InvalidGroupException {
        throw new UnsupportedOperationException("Renaming groups is not supported");
    }

    public void storeGroupAttributes(String groupName, Map<String, Set<String>> attributes) throws GroupNotFoundException, OperationFailedException {
        Map<String, Set> attributesToSync = attributes.entrySet().stream().filter(AttributePredicates.SYNCHRONISABLE_ATTRIBUTE_ENTRY_PREDICATE).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!attributesToSync.isEmpty()) {
            this.remoteDirectory.storeGroupAttributes(groupName, attributesToSync);
        }
        this.internalDirectory.storeGroupAttributes(groupName, attributes);
    }

    public void removeGroupAttributes(String groupName, String attributeName) throws GroupNotFoundException, OperationFailedException {
        if (AttributePredicates.SYNCING_ATTRIBUTE.test(attributeName)) {
            this.remoteDirectory.removeGroupAttributes(groupName, attributeName);
        }
        this.internalDirectory.removeGroupAttributes(groupName, attributeName);
    }

    public void removeGroup(String name) throws GroupNotFoundException, OperationFailedException, ReadOnlyGroupException {
        Validate.notEmpty((CharSequence)name, (String)"group name must not be empty", (Object[])new Object[0]);
        InternalDirectoryGroup internalGroup = this.internalDirectory.findGroupByName(name);
        if (internalGroup.isLocal()) {
            this.internalDirectory.removeGroup(name);
        } else {
            if (this.localGroupHandler.isLocalGroupsEnabled()) {
                throw new ReadOnlyGroupException(name);
            }
            this.removeRemoteGroup(name);
        }
    }

    private void removeRemoteGroup(String name) throws ReadOnlyGroupException, OperationFailedException, GroupNotFoundException {
        try {
            this.remoteDirectory.removeGroup(name);
        }
        catch (GroupNotFoundException e) {
            this.internalDirectory.removeGroup(name);
            throw e;
        }
        this.internalDirectory.removeGroup(name);
    }

    public void addUserToGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, OperationFailedException, ReadOnlyGroupException, MembershipAlreadyExistsException {
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            this.localGroupHandler.addUserToLocalGroup(username, groupName);
        } else {
            try {
                this.remoteDirectory.addUserToGroup(username, groupName);
            }
            catch (MembershipAlreadyExistsException e) {
                this.addUserToGroupInternal(username, groupName);
                throw e;
            }
            this.addUserToGroupInternal(username, groupName);
        }
    }

    private void addUserToGroupInternal(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, OperationFailedException {
        try {
            this.internalDirectory.addUserToGroup(username, groupName);
        }
        catch (MembershipAlreadyExistsException e) {
            log.debug("User (" + username + ") is already a member of group (" + groupName + ").");
        }
    }

    public void addGroupToGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, OperationFailedException, ReadOnlyGroupException, MembershipAlreadyExistsException {
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            this.localGroupHandler.addGroupToGroup(parentGroup, childGroup);
        } else {
            try {
                this.remoteDirectory.addGroupToGroup(childGroup, parentGroup);
            }
            catch (MembershipAlreadyExistsException e) {
                this.addGroupToGroupInternal(childGroup, parentGroup);
                throw e;
            }
            this.addGroupToGroupInternal(childGroup, parentGroup);
        }
    }

    private void addGroupToGroupInternal(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, ReadOnlyGroupException, OperationFailedException {
        try {
            this.internalDirectory.addGroupToGroup(childGroup, parentGroup);
        }
        catch (MembershipAlreadyExistsException e) {
            log.debug("Group (" + childGroup + ") is already a member of group (" + parentGroup + ").");
        }
    }

    public void removeUserFromGroup(String username, String groupName) throws GroupNotFoundException, UserNotFoundException, MembershipNotFoundException, OperationFailedException, ReadOnlyGroupException {
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            this.localGroupHandler.removeUserFromLocalGroup(username, groupName);
        } else {
            try {
                this.remoteDirectory.removeUserFromGroup(username, groupName);
            }
            catch (GroupNotFoundException | MembershipNotFoundException | UserNotFoundException exceptionFromRemoteDirectory) {
                this.silentlyRemoveUserFromGroupInTheCache(username, groupName);
                throw exceptionFromRemoteDirectory;
            }
            this.internalDirectory.removeUserFromGroup(username, groupName);
        }
    }

    private void silentlyRemoveUserFromGroupInTheCache(String username, String groupName) {
        try {
            this.internalDirectory.removeUserFromGroup(username, groupName);
        }
        catch (ObjectNotFoundException | OperationFailedException | ReadOnlyGroupException e) {
            log.debug("Ignoring exception when removing user from group in cache", e);
        }
    }

    public void removeGroupFromGroup(String childGroup, String parentGroup) throws GroupNotFoundException, InvalidMembershipException, MembershipNotFoundException, OperationFailedException, ReadOnlyGroupException {
        if (this.localGroupHandler.isLocalGroupsEnabled()) {
            this.localGroupHandler.removeGroupFromGroup(childGroup, parentGroup);
        } else {
            try {
                this.remoteDirectory.removeGroupFromGroup(childGroup, parentGroup);
            }
            catch (GroupNotFoundException | MembershipNotFoundException exceptionFromRemoteDirectory) {
                this.silentlyRemoveGroupFromGroupInTheCache(childGroup, parentGroup);
                throw exceptionFromRemoteDirectory;
            }
            this.internalDirectory.removeGroupFromGroup(childGroup, parentGroup);
        }
    }

    private void silentlyRemoveGroupFromGroupInTheCache(String childGroup, String parentGroup) {
        try {
            this.internalDirectory.removeGroupFromGroup(childGroup, parentGroup);
        }
        catch (InvalidMembershipException | ObjectNotFoundException | OperationFailedException | ReadOnlyGroupException e) {
            log.debug("Ignoring exception when removing group from group in cache", e);
        }
    }

    public void testConnection() throws OperationFailedException {
        this.remoteDirectory.testConnection();
    }

    public boolean supportsInactiveAccounts() {
        return this.remoteDirectory.supportsInactiveAccounts() || this.internalDirectory.isLocalUserStatusEnabled();
    }

    public boolean supportsNestedGroups() {
        return this.remoteDirectory.supportsNestedGroups();
    }

    public boolean supportsPasswordExpiration() {
        return this.remoteDirectory.supportsPasswordExpiration();
    }

    public boolean supportsSettingEncryptedCredential() {
        return this.remoteDirectory.supportsSettingEncryptedCredential();
    }

    public boolean isRolesDisabled() {
        return true;
    }

    @VisibleForTesting
    protected boolean shouldSyncGroupMembershipAfterUserAuthentication(boolean isNewUser) {
        if (this.remoteDirectory.supportsNestedGroups() && this.remoteDirectory.getLocallyFilteredGroupNames().isPresent()) {
            return false;
        }
        SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth groupSyncMode = SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.forDirectory((Attributes)this.remoteDirectory);
        return groupSyncMode == SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.ALWAYS || groupSyncMode == SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER && isNewUser;
    }

    public Set<String> getValues(String name) {
        return this.remoteDirectory.getValues(name);
    }

    public String getValue(String name) {
        return this.remoteDirectory.getValue(name);
    }

    public boolean isEmpty() {
        return this.remoteDirectory.isEmpty();
    }

    public Set<String> getKeys() {
        return this.remoteDirectory.getKeys();
    }

    public boolean isIncrementalSyncEnabled() {
        return Boolean.parseBoolean(this.remoteDirectory.getValue("crowd.sync.incremental.enabled"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void synchroniseCache(SynchronisationMode mode, SynchronisationStatusManager synchronisationStatusManager) throws OperationFailedException {
        block23: {
            String description;
            TimedOperation operation;
            long directoryId;
            block22: {
                String token;
                directoryId = this.getDirectoryId();
                SynchronisationMode synchronisedMode = null;
                CacheSynchronisationResult result = null;
                CacheRefresher cacheRefresher = this.cacheRefresherFactory.createRefresher(this.remoteDirectory);
                try {
                    token = Optional.ofNullable(synchronisationStatusManager.getDirectorySynchronisationInformation(directoryId).getLastRound()).map(info -> {
                        if (info.getStartTime() > Long.parseLong(Optional.ofNullable(this.getValue("configuration.change.timestamp")).orElse("0"))) {
                            return synchronisationStatusManager.getLastSynchronisationTokenForDirectory(directoryId);
                        }
                        return null;
                    }).orElse(null);
                    synchronisationStatusManager.clearSynchronisationTokenForDirectory(directoryId);
                }
                catch (DirectoryNotFoundException e) {
                    token = null;
                }
                operation = new TimedOperation();
                try {
                    DirectoryCache directoryCache = this.directoryCacheFactory.createDirectoryCache(this.remoteDirectory, this.internalDirectory);
                    if (mode == SynchronisationMode.INCREMENTAL && this.isIncrementalSyncEnabled() && token != null) {
                        log.info("{} synchronisation for directory [ {} ] starting", (Object)mode, (Object)directoryId);
                        synchronisationStatusManager.syncStatus(directoryId, SynchronisationStatusKey.INCREMENTAL, Collections.emptyList());
                        try {
                            log.info("Attempting {} synchronisation for directory [ {} ]", (Object)mode, (Object)directoryId);
                            result = cacheRefresher.synchroniseChanges(directoryCache, token);
                            if (result.isSuccess()) {
                                synchronisedMode = SynchronisationMode.INCREMENTAL;
                            } else {
                                log.info("Incremental synchronisation for directory [ {} ] was not completed, falling back to a full synchronisation", (Object)directoryId);
                            }
                        }
                        catch (OperationFailedException | RuntimeException e) {
                            log.error("Incremental synchronisation for directory [ {} ] was unexpectedly interrupted, falling back to a full synchronisation", (Object)directoryId, (Object)e);
                            synchronisationStatusManager.syncFailure(directoryId, SynchronisationMode.INCREMENTAL, e);
                        }
                    }
                    if (synchronisedMode == null) {
                        log.info("{} synchronisation for directory [ {} ] starting", (Object)SynchronisationMode.FULL, (Object)directoryId);
                        synchronisationStatusManager.syncStatus(directoryId, SynchronisationStatusKey.FULL, Collections.emptyList());
                        try {
                            result = cacheRefresher.synchroniseAll(directoryCache);
                        }
                        catch (OperationFailedException | RuntimeException e) {
                            log.error("Exception occured when performing full synchronization", e);
                            synchronisationStatusManager.syncFailure(directoryId, SynchronisationMode.FULL, e);
                            Throwables.propagateIfPossible((Throwable)e, RuntimeException.class, OperationFailedException.class);
                        }
                        synchronisedMode = SynchronisationMode.FULL;
                    }
                    description = " synchronisation complete for directory [ " + directoryId + " ]";
                    if (synchronisedMode == null) break block22;
                }
                catch (Throwable throwable) {
                    String description2 = " synchronisation complete for directory [ " + directoryId + " ]";
                    if (synchronisedMode != null) {
                        log.info(operation.complete(synchronisedMode + description2));
                        if (synchronisedMode.equals((Object)SynchronisationMode.FULL)) {
                            synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.SUCCESS_FULL, Collections.emptyList());
                        } else if (synchronisedMode.equals((Object)SynchronisationMode.INCREMENTAL)) {
                            synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.SUCCESS_INCREMENTAL, Collections.emptyList());
                        }
                        if (result != null) {
                            synchronisationStatusManager.storeSynchronisationTokenForDirectory(directoryId, result.getSyncStatusToken());
                        }
                    } else {
                        log.info(operation.complete("failed" + description2));
                        synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.FAILURE, Collections.emptyList());
                    }
                    throw throwable;
                }
                log.info(operation.complete(synchronisedMode + description));
                if (synchronisedMode.equals((Object)SynchronisationMode.FULL)) {
                    synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.SUCCESS_FULL, Collections.emptyList());
                } else if (synchronisedMode.equals((Object)SynchronisationMode.INCREMENTAL)) {
                    synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.SUCCESS_INCREMENTAL, Collections.emptyList());
                }
                if (result != null) {
                    synchronisationStatusManager.storeSynchronisationTokenForDirectory(directoryId, result.getSyncStatusToken());
                }
                break block23;
            }
            log.info(operation.complete("failed" + description));
            synchronisationStatusManager.syncFinished(directoryId, SynchronisationStatusKey.FAILURE, Collections.emptyList());
        }
    }

    public RemoteDirectory getAuthoritativeDirectory() {
        return this.remoteDirectory;
    }

    public void expireAllPasswords() throws OperationFailedException {
        this.remoteDirectory.expireAllPasswords();
    }

    public long getUserCount() throws OperationFailedException {
        return this.internalDirectory.getUserCount();
    }

    public long getGroupCount() throws OperationFailedException {
        return this.internalDirectory.getGroupCount();
    }

    protected InternalRemoteDirectory getDelegate() {
        return this.internalDirectory;
    }

    private static GroupTemplate makeGroupTemplate(Group group) {
        GroupTemplate template = new GroupTemplate(group);
        template.setDescription(group.getDescription());
        return template;
    }

    public <T> ListMultimap<String, T> searchGroupRelationshipsGroupedByName(MembershipQuery<T> query) {
        return this.internalDirectory.searchGroupRelationshipsGroupedByName(query);
    }
}

