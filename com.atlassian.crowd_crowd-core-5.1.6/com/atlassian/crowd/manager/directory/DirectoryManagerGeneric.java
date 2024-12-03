/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.dao.application.ApplicationDAO
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper
 *  com.atlassian.crowd.directory.ldap.LDAPPropertiesMapperImpl
 *  com.atlassian.crowd.directory.ldap.LdapPoolType
 *  com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper
 *  com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.embedded.spi.DirectoryDao
 *  com.atlassian.crowd.event.directory.DirectoryCreatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.group.GroupAttributeDeletedEvent
 *  com.atlassian.crowd.event.group.GroupAttributeStoredEvent
 *  com.atlassian.crowd.event.group.GroupCreatedEvent
 *  com.atlassian.crowd.event.group.GroupDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent
 *  com.atlassian.crowd.event.group.GroupUpdatedEvent
 *  com.atlassian.crowd.event.login.AllPasswordsExpiredEvent
 *  com.atlassian.crowd.event.user.UserAttributeDeletedEvent
 *  com.atlassian.crowd.event.user.UserAttributeStoredEvent
 *  com.atlassian.crowd.event.user.UserCreatedEvent
 *  com.atlassian.crowd.event.user.UserCredentialUpdatedEvent
 *  com.atlassian.crowd.event.user.UserCredentialValidationFailed
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.event.user.UserEditedEvent
 *  com.atlassian.crowd.event.user.UserEmailChangedEvent
 *  com.atlassian.crowd.event.user.UserRenamedEvent
 *  com.atlassian.crowd.event.user.UsersDeletedEvent
 *  com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
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
 *  com.atlassian.crowd.exception.NestedGroupsNotSupportedException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.runtime.DirectoryCurrentlySynchronisingException
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.manager.directory.BeforeGroupRemoval
 *  com.atlassian.crowd.manager.directory.BulkAddResult
 *  com.atlassian.crowd.manager.directory.BulkAddResult$Builder
 *  com.atlassian.crowd.manager.directory.BulkRemoveResult
 *  com.atlassian.crowd.manager.directory.BulkRemoveResult$Builder
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.manager.directory.SynchronisationMode
 *  com.atlassian.crowd.manager.directory.SynchronisationStatusManager
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.NameComparator
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.user.ImmutableUser
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.search.query.DirectoryQueries
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.crowd.util.BatchResult
 *  com.atlassian.crowd.util.BoundedCount
 *  com.atlassian.crowd.util.DirectoryEntityUtils
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.ListMultimap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.core.event.MultiEventPublisher;
import com.atlassian.crowd.dao.application.ApplicationDAO;
import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapper;
import com.atlassian.crowd.directory.ldap.LDAPPropertiesMapperImpl;
import com.atlassian.crowd.directory.ldap.LdapPoolType;
import com.atlassian.crowd.directory.ldap.util.LDAPPropertiesHelper;
import com.atlassian.crowd.directory.ldap.util.LdapConnectionPropertiesDiffResultMapper;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectorySynchronisationInformation;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.embedded.spi.DirectoryDao;
import com.atlassian.crowd.event.directory.DirectoryCreatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.group.GroupAttributeDeletedEvent;
import com.atlassian.crowd.event.group.GroupAttributeStoredEvent;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.event.group.GroupDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipsDeletedEvent;
import com.atlassian.crowd.event.group.GroupUpdatedEvent;
import com.atlassian.crowd.event.login.AllPasswordsExpiredEvent;
import com.atlassian.crowd.event.user.UserAttributeDeletedEvent;
import com.atlassian.crowd.event.user.UserAttributeStoredEvent;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.crowd.event.user.UserCredentialUpdatedEvent;
import com.atlassian.crowd.event.user.UserCredentialValidationFailed;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.event.user.UserEditedEvent;
import com.atlassian.crowd.event.user.UserEmailChangedEvent;
import com.atlassian.crowd.event.user.UserRenamedEvent;
import com.atlassian.crowd.event.user.UsersDeletedEvent;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
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
import com.atlassian.crowd.exception.NestedGroupsNotSupportedException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.manager.directory.BeforeGroupRemoval;
import com.atlassian.crowd.manager.directory.BulkAddResult;
import com.atlassian.crowd.manager.directory.BulkRemoveResult;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.manager.directory.DirectorySynchronisationUtils;
import com.atlassian.crowd.manager.directory.DirectorySynchroniser;
import com.atlassian.crowd.manager.directory.RemoteDirectorySearcher;
import com.atlassian.crowd.manager.directory.SynchronisationMode;
import com.atlassian.crowd.manager.directory.SynchronisationStatusManager;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
import com.atlassian.crowd.manager.directory.nestedgroups.NestedGroupsCacheProvider;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.NameComparator;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.crowd.model.user.ImmutableUser;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.search.query.DirectoryQueries;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.crowd.util.BatchResult;
import com.atlassian.crowd.util.BoundedCount;
import com.atlassian.crowd.util.DirectoryEntityUtils;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DirectoryManagerGeneric
implements DirectoryManager {
    private static final Logger logger = LoggerFactory.getLogger(DirectoryManagerGeneric.class);
    private final DirectoryDao directoryDao;
    private final ApplicationDAO applicationDAO;
    private final MultiEventPublisher eventPublisher;
    private final PermissionManager permissionManager;
    private final DirectoryInstanceLoader directoryInstanceLoader;
    private final DirectorySynchroniser directorySynchroniser;
    private final DirectoryPollerManager directoryPollerManager;
    private final ClusterLockService lockService;
    private final SynchronisationStatusManager synchronisationStatusManager;
    private final BeforeGroupRemoval beforeGroupRemoval;
    private final LDAPPropertiesHelper ldapPropertiesHelper;
    private final Optional<NestedGroupsCacheProvider> nestedGroupsCacheProvider;
    private final LdapConnectionPropertiesDiffResultMapper ldapConnectionPropertiesDiffResultMapper;

    public DirectoryManagerGeneric(DirectoryDao directoryDao, ApplicationDAO applicationDAO, MultiEventPublisher eventPublisher, PermissionManager permissionManager, DirectoryInstanceLoader directoryInstanceLoader, DirectorySynchroniser directorySynchroniser, DirectoryPollerManager directoryPollerManager, ClusterLockService lockService, SynchronisationStatusManager synchronisationStatusManager, BeforeGroupRemoval beforeGroupRemoval, Optional<NestedGroupsCacheProvider> nestedGroupsCacheProvider, LDAPPropertiesHelper ldapPropertiesHelper, LdapConnectionPropertiesDiffResultMapper ldapConnectionPropertiesDiffResultMapper) {
        this.directoryDao = (DirectoryDao)Preconditions.checkNotNull((Object)directoryDao);
        this.applicationDAO = (ApplicationDAO)Preconditions.checkNotNull((Object)applicationDAO);
        this.eventPublisher = (MultiEventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.directoryInstanceLoader = (DirectoryInstanceLoader)Preconditions.checkNotNull((Object)directoryInstanceLoader);
        this.directorySynchroniser = (DirectorySynchroniser)Preconditions.checkNotNull((Object)directorySynchroniser);
        this.directoryPollerManager = (DirectoryPollerManager)Preconditions.checkNotNull((Object)directoryPollerManager);
        this.lockService = (ClusterLockService)Preconditions.checkNotNull((Object)lockService);
        this.synchronisationStatusManager = (SynchronisationStatusManager)Preconditions.checkNotNull((Object)synchronisationStatusManager);
        this.beforeGroupRemoval = (BeforeGroupRemoval)Preconditions.checkNotNull((Object)beforeGroupRemoval);
        this.ldapPropertiesHelper = (LDAPPropertiesHelper)Preconditions.checkNotNull((Object)ldapPropertiesHelper);
        this.nestedGroupsCacheProvider = nestedGroupsCacheProvider;
        this.ldapConnectionPropertiesDiffResultMapper = (LdapConnectionPropertiesDiffResultMapper)Preconditions.checkNotNull((Object)ldapConnectionPropertiesDiffResultMapper);
    }

    public Directory addDirectory(Directory directory) throws DirectoryInstantiationException {
        if (!this.directoryInstanceLoader.canLoad(directory.getImplementationClass())) {
            throw new IllegalArgumentException("Failed to instantiate directory with class: " + directory.getImplementationClass());
        }
        Directory addedDirectory = this.directoryDao.add(directory);
        this.eventPublisher.publish(new DirectoryCreatedEvent((Object)this, addedDirectory));
        return addedDirectory;
    }

    public Directory findDirectoryById(long directoryId) throws DirectoryNotFoundException {
        return this.directoryDao.findById(directoryId);
    }

    public List<Directory> findAllDirectories() {
        return this.directoryDao.search(DirectoryQueries.allDirectories());
    }

    public List<Directory> searchDirectories(EntityQuery<Directory> query) {
        return this.directoryDao.search(query);
    }

    public Directory findDirectoryByName(String name) throws DirectoryNotFoundException {
        return this.directoryDao.findByName(name);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Directory updateDirectory(Directory directory) throws DirectoryNotFoundException {
        Directory updatedDirectory;
        if (directory.getId() == null) {
            throw new DirectoryNotFoundException(directory.getId());
        }
        ImmutableDirectory oldDirectory = ImmutableDirectory.from((Directory)this.findDirectoryById(directory.getId()));
        if (this.hasSpringLdapPoolClearingChanges((Directory)oldDirectory, directory)) {
            ClusterLock lock = this.lockService.getLockForName(DirectorySynchronisationUtils.getLockName(directory.getId()));
            if (!lock.tryLock()) throw new DirectoryCurrentlySynchronisingException(directory.getId().longValue());
            try {
                updatedDirectory = this.directoryDao.update(directory);
            }
            finally {
                lock.unlock();
            }
        } else {
            updatedDirectory = this.directoryDao.update(directory);
        }
        this.eventPublisher.publish(new DirectoryUpdatedEvent((Object)this, (Directory)oldDirectory, updatedDirectory));
        return updatedDirectory;
    }

    private boolean hasSpringLdapPoolClearingChanges(Directory oldDirectory, Directory newDirectory) {
        if (oldDirectory.getType() == DirectoryType.CONNECTOR && Boolean.parseBoolean((String)oldDirectory.getAttributes().get("com.atlassian.crowd.directory.sync.cache.enabled"))) {
            LDAPPropertiesMapperImpl oldDirectoryPropertiesMapper = new LDAPPropertiesMapperImpl(this.ldapPropertiesHelper);
            oldDirectoryPropertiesMapper.setAttributes(oldDirectory.getAttributes());
            LDAPPropertiesMapperImpl newDirectoryPropertiesMapper = new LDAPPropertiesMapperImpl(this.ldapPropertiesHelper);
            newDirectoryPropertiesMapper.setAttributes(newDirectory.getAttributes());
            if (oldDirectoryPropertiesMapper.getLdapPoolType() == LdapPoolType.COMMONS_POOL2) {
                return newDirectoryPropertiesMapper.getLdapPoolType() == LdapPoolType.JNDI || this.ldapConnectionPropertiesDiffResultMapper.getConnectionPropertiesDifference((LDAPPropertiesMapper)oldDirectoryPropertiesMapper, (LDAPPropertiesMapper)newDirectoryPropertiesMapper).getNumberOfDiffs() > 0;
            }
        }
        return false;
    }

    public void removeDirectory(Directory directory) throws DirectoryNotFoundException, com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException {
        ClusterLock lock = this.lockService.getLockForName(DirectorySynchronisationUtils.getLockName(directory.getId()));
        if (lock.tryLock()) {
            try {
                this.applicationDAO.removeDirectoryMappings(directory.getId().longValue());
                this.synchronisationStatusManager.removeStatusesForDirectory(directory.getId().longValue());
                this.synchronisationStatusManager.clearSynchronisationTokenForDirectory(directory.getId().longValue());
                this.directoryDao.remove(directory);
                this.eventPublisher.publish(new DirectoryDeletedEvent((Object)this, directory));
            }
            finally {
                lock.unlock();
            }
        } else {
            throw new com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException(directory.getId().longValue());
        }
    }

    public boolean supportsNestedGroups(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        return remoteDirectory.supportsNestedGroups();
    }

    public boolean isSynchronisable(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        return DirectoryManagerGeneric.isSynchronisable(remoteDirectory);
    }

    private static boolean isSynchronisable(RemoteDirectory remoteDirectory) {
        return remoteDirectory instanceof SynchronisableDirectory;
    }

    public SynchronisationMode getSynchronisationMode(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        return DirectoryManagerGeneric.isSynchronisable(remoteDirectory) ? DirectoryManagerGeneric.getSynchronisationMode((SynchronisableDirectory)remoteDirectory) : null;
    }

    private static SynchronisationMode getSynchronisationMode(SynchronisableDirectory directory) {
        return directory.isIncrementalSyncEnabled() ? SynchronisationMode.INCREMENTAL : SynchronisationMode.FULL;
    }

    public void synchroniseCache(long directoryId, SynchronisationMode mode) throws OperationFailedException, DirectoryNotFoundException {
        this.synchroniseCache(directoryId, mode, true);
    }

    public void synchroniseCache(long directoryId, SynchronisationMode mode, boolean runInBackground) throws OperationFailedException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        if (DirectoryManagerGeneric.isSynchronisable(remoteDirectory)) {
            if (runInBackground) {
                this.directoryPollerManager.triggerPoll(directoryId, mode);
            } else {
                if (this.isSynchronising(directoryId)) {
                    throw new OperationFailedException("Directory " + directoryId + " is currently synchronising");
                }
                this.directorySynchroniser.synchronise((SynchronisableDirectory)remoteDirectory, mode);
            }
        }
    }

    public boolean isSynchronising(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        return this.directorySynchroniser.isSynchronising(directoryId);
    }

    public DirectorySynchronisationInformation getDirectorySynchronisationInformation(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        if (DirectoryManagerGeneric.isSynchronisable(remoteDirectory)) {
            return this.synchronisationStatusManager.getDirectorySynchronisationInformation(directoryId);
        }
        return null;
    }

    private RemoteDirectory getDirectoryImplementation(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        return this.directoryInstanceLoader.getDirectory(this.findDirectoryById(directoryId));
    }

    private RemoteDirectorySearcher getSearcher(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        return new RemoteDirectorySearcher(this.getDirectoryImplementation(directoryId), this.nestedGroupsCacheProvider);
    }

    public com.atlassian.crowd.model.user.User authenticateUser(long directoryId, String username, PasswordCredential passwordCredential) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, DirectoryNotFoundException, UserNotFoundException {
        return this.getDirectoryImplementation(directoryId).authenticate(username, passwordCredential);
    }

    public com.atlassian.crowd.model.user.User userAuthenticated(long directoryId, String username) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException, InactiveAccountException {
        return this.getDirectoryImplementation(directoryId).userAuthenticated(username);
    }

    public com.atlassian.crowd.model.user.User findUserByName(long directoryId, String username) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException {
        return this.getDirectoryImplementation(directoryId).findUserByName(username);
    }

    public UserWithAttributes findUserWithAttributesByName(long directoryId, String username) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException {
        return this.getDirectoryImplementation(directoryId).findUserWithAttributesByName(username);
    }

    public <T> List<T> searchUsers(long directoryId, EntityQuery<T> query) throws OperationFailedException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).searchUsers(query);
    }

    public com.atlassian.crowd.model.user.User addUser(long directoryId, UserTemplate user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, UserAlreadyExistsException {
        return this.addUser(directoryId, UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    public UserWithAttributes addUser(long directoryId, UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidCredentialException, InvalidUserException, OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, UserAlreadyExistsException {
        if (this.userExists(directoryId, user.getName())) {
            throw new UserAlreadyExistsException(directoryId, user.getName());
        }
        if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)user.getName())) {
            throw new InvalidUserException((User)user, "User name may not contain leading or trailing whitespace");
        }
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, OperationType.CREATE_USER)) {
            UserWithAttributes createdUser = this.getDirectoryImplementation(directoryId).addUser(user, credential);
            this.eventPublisher.publish(new UserCreatedEvent((Object)this, directory, (com.atlassian.crowd.model.user.User)createdUser));
            return createdUser;
        }
        throw new DirectoryPermissionException("Directory does not allow adding of users");
    }

    private boolean userExists(long directoryId, String username) throws DirectoryNotFoundException, OperationFailedException {
        try {
            this.findUserByName(directoryId, username);
            return true;
        }
        catch (UserNotFoundException e) {
            return false;
        }
    }

    public com.atlassian.crowd.model.user.User updateUser(long directoryId, UserTemplate user) throws OperationFailedException, DirectoryPermissionException, InvalidUserException, DirectoryNotFoundException, UserNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, OperationType.UPDATE_USER)) {
            RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
            ImmutableUser currentUser = ImmutableUser.from((com.atlassian.crowd.model.user.User)remoteDirectory.findUserByName(user.getName()));
            com.atlassian.crowd.model.user.User updatedUser = remoteDirectory.updateUser(user);
            this.eventPublisher.publish(new UserEditedEvent((Object)this, directory, updatedUser, (com.atlassian.crowd.model.user.User)currentUser));
            String originalEmail = currentUser.getEmailAddress();
            if (!Objects.equals(originalEmail, updatedUser.getEmailAddress())) {
                this.eventPublisher.publish(new UserEmailChangedEvent((Object)this, directory, updatedUser, originalEmail));
            }
            return updatedUser;
        }
        throw new DirectoryPermissionException("Directory does not allow user modifications");
    }

    public com.atlassian.crowd.model.user.User renameUser(long directoryId, String oldUsername, String newUsername) throws OperationFailedException, DirectoryPermissionException, InvalidUserException, DirectoryNotFoundException, UserNotFoundException, UserAlreadyExistsException {
        if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)newUsername)) {
            UserTemplate user = new UserTemplate(newUsername, directoryId);
            throw new InvalidUserException((User)user, "User name may not contain leading or trailing whitespace");
        }
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, OperationType.UPDATE_USER)) {
            RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
            if (newUsername.equals(oldUsername)) {
                return remoteDirectory.findUserByName(oldUsername);
            }
            com.atlassian.crowd.model.user.User updatedUser = remoteDirectory.renameUser(oldUsername, newUsername);
            this.eventPublisher.publish(new UserRenamedEvent((Object)this, directory, updatedUser, oldUsername));
            return updatedUser;
        }
        throw new DirectoryPermissionException("Directory does not allow user modifications");
    }

    public void storeUserAttributes(long directoryId, String username, Map<String, Set<String>> attributes) throws OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.UPDATE_USER_ATTRIBUTE)) {
            throw new DirectoryPermissionException("Directory does not allow user attribute modifications");
        }
        this.getDirectoryImplementation(directoryId).storeUserAttributes(username, attributes);
        com.atlassian.crowd.model.user.User updatedUser = this.getDirectoryImplementation(directoryId).findUserByName(username);
        this.eventPublisher.publish(new UserAttributeStoredEvent((Object)this, directory, updatedUser, attributes));
    }

    public void removeUserAttributes(long directoryId, String username, String attributeName) throws OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, UserNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.UPDATE_USER_ATTRIBUTE)) {
            throw new DirectoryPermissionException("Directory does not allow user attribute modifications");
        }
        this.getDirectoryImplementation(directoryId).removeUserAttributes(username, attributeName);
        com.atlassian.crowd.model.user.User updatedUser = this.getDirectoryImplementation(directoryId).findUserByName(username);
        this.eventPublisher.publish(new UserAttributeDeletedEvent((Object)this, directory, updatedUser, attributeName));
    }

    public void updateUserCredential(long directoryId, String username, PasswordCredential credential) throws OperationFailedException, DirectoryPermissionException, InvalidCredentialException, DirectoryNotFoundException, UserNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, OperationType.UPDATE_USER)) {
            logger.info("The password for {} in {} is being changed.", (Object)username, (Object)directory.getName());
            try {
                this.getDirectoryImplementation(directoryId).updateUserCredential(username, credential);
                this.eventPublisher.publish(new UserCredentialUpdatedEvent((Object)this, directory, username, credential));
            }
            catch (InvalidCredentialException invalidCredentialException) {
                this.eventPublisher.publish(new UserCredentialValidationFailed((Object)this, directory, invalidCredentialException.getViolatedConstraints()));
                throw invalidCredentialException;
            }
        } else {
            throw new DirectoryPermissionException("Directory does not allow user modifications");
        }
    }

    public void removeUser(long directoryId, String username) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException, UserNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.DELETE_USER)) {
            throw new DirectoryPermissionException("Directory does not allow user removal");
        }
        this.getDirectoryImplementation(directoryId).removeUser(username);
        this.eventPublisher.publish(new UsersDeletedEvent((Object)this, directory, Collections.singleton(username)));
        this.eventPublisher.publish(new UserDeletedEvent((Object)this, directory, username));
    }

    public Group findGroupByName(long directoryId, String groupName) throws OperationFailedException, GroupNotFoundException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).findGroupByName(groupName);
    }

    public GroupWithAttributes findGroupWithAttributesByName(long directoryId, String groupName) throws OperationFailedException, GroupNotFoundException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).findGroupWithAttributesByName(groupName);
    }

    public <T> List<T> searchGroups(long directoryId, EntityQuery<T> query) throws OperationFailedException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).searchGroups(query);
    }

    public Group addGroup(long directoryId, GroupTemplate group) throws InvalidGroupException, OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        try {
            this.findGroupByName(directoryId, group.getName());
            throw new InvalidGroupException((Group)group, "Group with name <" + group.getName() + "> already exists in directory <" + directory.getName() + ">");
        }
        catch (GroupNotFoundException e) {
            if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)group.getName())) {
                throw new InvalidGroupException((Group)group, "Group name may not contain leading or trailing whitespace");
            }
            OperationType operationType = DirectoryManagerGeneric.getCreateOperationType((Group)group);
            if (!this.permissionManager.hasPermission(directory, operationType)) {
                if (operationType.equals((Object)OperationType.CREATE_GROUP)) {
                    throw new DirectoryPermissionException("Directory does not allow adding of groups");
                }
                throw new DirectoryPermissionException("Directory does not allow adding of roles");
            }
            Group createdGroup = this.getDirectoryImplementation(directoryId).addGroup(group);
            this.eventPublisher.publish(new GroupCreatedEvent((Object)this, directory, createdGroup));
            return createdGroup;
        }
    }

    public Group updateGroup(long directoryId, GroupTemplate group) throws OperationFailedException, DirectoryPermissionException, InvalidGroupException, DirectoryNotFoundException, GroupNotFoundException, ReadOnlyGroupException {
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType((Group)group))) {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        Group updatedGroup = this.getDirectoryImplementation(directoryId).updateGroup(group);
        this.eventPublisher.publish(new GroupUpdatedEvent((Object)this, directory, updatedGroup));
        return updatedGroup;
    }

    public Group renameGroup(long directoryId, String oldGroupname, String newGroupname) throws OperationFailedException, DirectoryPermissionException, InvalidGroupException, DirectoryNotFoundException, GroupNotFoundException {
        Group groupToUpdate;
        OperationType operationType;
        if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)newGroupname)) {
            GroupTemplate group = new GroupTemplate(newGroupname, directoryId);
            throw new InvalidGroupException((Group)group, "Group name may not contain leading or trailing whitespace");
        }
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(groupToUpdate = this.findGroupByName(directoryId, oldGroupname)))) {
            Group updatedGroup = this.getDirectoryImplementation(directoryId).renameGroup(oldGroupname, newGroupname);
            this.eventPublisher.publish(new GroupUpdatedEvent((Object)this, directory, updatedGroup));
            return updatedGroup;
        }
        if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
            throw new DirectoryPermissionException("Directory does not allow group modifications");
        }
        throw new DirectoryPermissionException("Directory does not allow role modifications");
    }

    public void storeGroupAttributes(long directoryId, String groupName, Map<String, Set<String>> attributes) throws OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, GroupNotFoundException {
        Group groupToUpdate;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateAttributeOperationType(groupToUpdate = this.findGroupByName(directoryId, groupName)))) {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        this.getDirectoryImplementation(directoryId).storeGroupAttributes(groupName, attributes);
        Group updateGroup = this.findGroupByName(directoryId, groupName);
        this.eventPublisher.publish(new GroupAttributeStoredEvent((Object)this, directory, updateGroup, attributes));
    }

    public void removeGroupAttributes(long directoryId, String groupName, String attributeName) throws OperationFailedException, DirectoryPermissionException, DirectoryNotFoundException, GroupNotFoundException {
        Group groupToUpdate;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateAttributeOperationType(groupToUpdate = this.findGroupByName(directoryId, groupName)))) {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP_ATTRIBUTE)) {
                throw new DirectoryPermissionException("Directory does not allow group attribute modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role attribute modifications");
        }
        this.getDirectoryImplementation(directoryId).removeGroupAttributes(groupName, attributeName);
        Group updateGroup = this.findGroupByName(directoryId, groupName);
        this.eventPublisher.publish(new GroupAttributeDeletedEvent((Object)this, directory, updateGroup, attributeName));
    }

    public void removeGroup(long directoryId, String groupName) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException, GroupNotFoundException, ReadOnlyGroupException {
        Group groupToDelete;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getDeleteOperationType(groupToDelete = this.findGroupByName(directoryId, groupName)))) {
            if (operationType.equals((Object)OperationType.DELETE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group removal");
            }
            throw new DirectoryPermissionException("Directory does not allow role removal");
        }
        this.beforeGroupRemoval.beforeRemoveGroup(directoryId, groupName);
        this.applicationDAO.removeGroupMappings(directoryId, groupName);
        this.getDirectoryImplementation(directoryId).removeGroup(groupName);
        this.eventPublisher.publish(new GroupDeletedEvent((Object)this, directory, groupName));
    }

    public boolean isUserDirectGroupMember(long directoryId, String username, String groupName) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).isUserDirectGroupMember(username, groupName);
    }

    public boolean isGroupDirectGroupMember(long directoryId, String childGroup, String parentGroup) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).isGroupDirectGroupMember(childGroup, parentGroup);
    }

    public void addUserToGroup(long directoryId, String username, String groupName) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException, GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException, MembershipAlreadyExistsException {
        Group groupToUpdate;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(groupToUpdate = this.findGroupByName(directoryId, groupName)))) {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        this.getDirectoryImplementation(directoryId).addUserToGroup(username, groupName);
        this.eventPublisher.publish(new GroupMembershipCreatedEvent((Object)this, directory, username, groupName, MembershipType.GROUP_USER));
        this.eventPublisher.publish(new GroupMembershipsCreatedEvent((Object)this, directory, (Iterable)ImmutableList.of((Object)username), groupName, MembershipType.GROUP_USER));
    }

    public void addGroupToGroup(long directoryId, String childGroup, String parentGroup) throws DirectoryPermissionException, OperationFailedException, InvalidMembershipException, NestedGroupsNotSupportedException, DirectoryNotFoundException, GroupNotFoundException, ReadOnlyGroupException, MembershipAlreadyExistsException {
        Group parentGroupToUpdate;
        OperationType operationType;
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        if (!remoteDirectory.supportsNestedGroups()) {
            throw new NestedGroupsNotSupportedException(directoryId);
        }
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(parentGroupToUpdate = this.findGroupByName(directoryId, parentGroup)))) {
            if (childGroup.equals(parentGroup)) {
                throw new InvalidMembershipException("Cannot add direct circular group membership reference");
            }
        } else {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        remoteDirectory.addGroupToGroup(childGroup, parentGroup);
        this.eventPublisher.publish(new GroupMembershipCreatedEvent((Object)this, directory, childGroup, parentGroup, MembershipType.GROUP_GROUP));
        this.eventPublisher.publish(new GroupMembershipsCreatedEvent((Object)this, directory, (Iterable)ImmutableList.of((Object)childGroup), parentGroup, MembershipType.GROUP_GROUP));
    }

    public void removeUserFromGroup(long directoryId, String username, String groupName) throws DirectoryPermissionException, OperationFailedException, MembershipNotFoundException, DirectoryNotFoundException, GroupNotFoundException, UserNotFoundException, ReadOnlyGroupException {
        Group groupToUpdate;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(groupToUpdate = this.findGroupByName(directoryId, groupName)))) {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        this.getDirectoryImplementation(directoryId).removeUserFromGroup(username, groupName);
        this.eventPublisher.publish(new GroupMembershipDeletedEvent((Object)this, directory, username, groupName, MembershipType.GROUP_USER));
        this.eventPublisher.publish(new GroupMembershipsDeletedEvent((Object)this, directory, (Iterable)ImmutableList.of((Object)username), groupName, MembershipType.GROUP_USER));
    }

    public void removeGroupFromGroup(long directoryId, String childGroup, String parentGroup) throws DirectoryPermissionException, OperationFailedException, InvalidMembershipException, MembershipNotFoundException, DirectoryNotFoundException, GroupNotFoundException, ReadOnlyGroupException {
        Group groupToUpdate;
        OperationType operationType;
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        if (!remoteDirectory.supportsNestedGroups()) {
            throw new UnsupportedOperationException("Directory with id [" + directoryId + "] does not support nested groups");
        }
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(groupToUpdate = this.findGroupByName(directoryId, parentGroup)))) {
            if (childGroup.equals(parentGroup)) {
                throw new InvalidMembershipException("Cannot remove direct circular group membership reference");
            }
        } else {
            if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
                throw new DirectoryPermissionException("Directory does not allow group modifications");
            }
            throw new DirectoryPermissionException("Directory does not allow role modifications");
        }
        remoteDirectory.removeGroupFromGroup(childGroup, parentGroup);
        this.eventPublisher.publish(new GroupMembershipDeletedEvent((Object)this, directory, childGroup, parentGroup, MembershipType.GROUP_GROUP));
        this.eventPublisher.publish(new GroupMembershipsDeletedEvent((Object)this, directory, (Iterable)ImmutableList.of((Object)childGroup), parentGroup, MembershipType.GROUP_GROUP));
    }

    public <T> List<T> searchDirectGroupRelationships(long directoryId, MembershipQuery<T> query) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).searchDirectGroupRelationships(query);
    }

    public <T> ListMultimap<String, T> searchDirectGroupRelationshipsGroupedByName(long directoryId, MembershipQuery<T> query) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).searchDirectGroupRelationshipsGroupedByName(query);
    }

    public BoundedCount countDirectMembersOfGroup(long directoryId, String groupName, int querySizeHint) throws OperationFailedException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).countDirectMembersOfGroup(groupName, querySizeHint);
    }

    @Transactional(readOnly=true)
    public boolean isUserNestedGroupMember(long directoryId, String username, String groupName) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).isUserNestedGroupMember(username, groupName);
    }

    @Transactional(readOnly=true)
    public boolean isUserNestedGroupMember(long directoryId, String username, Set<String> groupNames) throws OperationFailedException, DirectoryNotFoundException {
        return !this.getSearcher(directoryId).filterNestedUserMembersOfGroups((Set<String>)ImmutableSet.of((Object)username), groupNames).isEmpty();
    }

    @Transactional(readOnly=true)
    public Set<String> filterNestedUserMembersOfGroups(long directoryId, Set<String> userNames, Set<String> groupNames) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).filterNestedUserMembersOfGroups(userNames, groupNames);
    }

    @Transactional(readOnly=true)
    public boolean isGroupNestedGroupMember(long directoryId, String childGroupName, String parentGroupName) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).isGroupNestedGroupMember(childGroupName, parentGroupName);
    }

    @Transactional(readOnly=true)
    public <T> List<T> searchNestedGroupRelationships(long directoryId, MembershipQuery<T> query) throws OperationFailedException, DirectoryNotFoundException {
        return this.getSearcher(directoryId).searchNestedGroupRelationships(query);
    }

    public BulkAddResult<com.atlassian.crowd.model.user.User> addAllUsers(long directoryId, Collection<UserTemplateWithCredentialAndAttributes> users, boolean overwrite) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException {
        List<UserTemplateWithCredentialAndAttributes> failedEntities;
        List<Object> successfulEntities;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.CREATE_USER)) {
            throw new DirectoryPermissionException("Directory does not allow adding of users");
        }
        BulkAddResult.Builder bulkAddResultBuilder = BulkAddResult.builder((long)users.size()).setOverwrite(overwrite);
        BulkRemoveResult<String> bulkRemoveExistingResult = this.removeAllUsers(directoryId, users, overwrite);
        ArrayList usersToAdd = overwrite ? Lists.newArrayList(DirectoryManagerGeneric.getDeletedAndNotFoundEntities(users, bulkRemoveExistingResult, NameComparator.normaliserOf(UserTemplateWithCredentialAndAttributes.class))) : Lists.newArrayList(DirectoryManagerGeneric.getNotFoundEntities(users, bulkRemoveExistingResult, NameComparator.normaliserOf(UserTemplateWithCredentialAndAttributes.class)));
        for (String stillExistingUserName : bulkRemoveExistingResult.getFailedEntities()) {
            try {
                bulkAddResultBuilder.addExistingEntity((Object)this.findUserByName(directoryId, stillExistingUserName));
                if (overwrite) {
                    logger.error("User could not be removed for bulk add with overwrite: {}", (Object)stillExistingUserName);
                    continue;
                }
                logger.info("User already exists in directory; overwrite is off so import will skip it: {}", (Object)stillExistingUserName);
            }
            catch (UserNotFoundException e) {
                logger.debug("User previously existed and couldn't be removed but is now missing so importing it: {}", (Object)stillExistingUserName);
                usersToAdd.add(Iterables.find(users, (Predicate)DirectoryEntityUtils.whereNameEquals((String)stillExistingUserName)));
            }
        }
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        Set uniqueUsersToAdd = this.retainUniqueEntities(usersToAdd);
        if (remoteDirectory instanceof InternalRemoteDirectory) {
            BatchResult batchResult = ((InternalRemoteDirectory)remoteDirectory).addAllUsers(uniqueUsersToAdd);
            successfulEntities = batchResult.getSuccessfulEntities();
            failedEntities = batchResult.getFailedEntities();
        } else {
            successfulEntities = new ArrayList(uniqueUsersToAdd);
            failedEntities = new ArrayList();
            for (UserTemplateWithCredentialAndAttributes userTemplateWithCredentialAndAttributes : uniqueUsersToAdd) {
                try {
                    if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)userTemplateWithCredentialAndAttributes.getName())) {
                        throw new InvalidUserException((User)userTemplateWithCredentialAndAttributes, "User name may not contain leading or trailing whitespace");
                    }
                    successfulEntities.add(remoteDirectory.addUser((UserTemplateWithAttributes)userTemplateWithCredentialAndAttributes, userTemplateWithCredentialAndAttributes.getCredential()));
                }
                catch (Exception e) {
                    logger.error("Failed to add user: {}", (Object)userTemplateWithCredentialAndAttributes, (Object)e);
                    failedEntities.add(userTemplateWithCredentialAndAttributes);
                }
            }
        }
        bulkAddResultBuilder.addFailedEntities(failedEntities);
        DirectoryManagerGeneric.logFailedEntities(remoteDirectory, failedEntities);
        for (com.atlassian.crowd.model.user.User user : successfulEntities) {
            this.eventPublisher.publish(new UserCreatedEvent((Object)this, directory, user));
        }
        return bulkAddResultBuilder.build();
    }

    private static <T extends DirectoryEntity> Iterable<T> getDeletedAndNotFoundEntities(Collection<T> users, final BulkRemoveResult<String> bulkRemoveExistingResult, Function<T, String> normaliser) {
        return Iterables.filter(users, (Predicate)Predicates.compose((Predicate)new Predicate<String>(){

            public boolean apply(String normalisedUserName) {
                return !bulkRemoveExistingResult.getFailedEntities().contains(normalisedUserName) || bulkRemoveExistingResult.getMissingEntities().contains(normalisedUserName);
            }
        }, normaliser));
    }

    private static <T extends DirectoryEntity> Iterable<T> getNotFoundEntities(Collection<T> users, final BulkRemoveResult<String> bulkRemoveExistingResult, final Function<T, String> normaliser) {
        return Iterables.filter(users, (Predicate)new Predicate<T>(){

            public boolean apply(T user) {
                return bulkRemoveExistingResult.getMissingEntities().contains(normaliser.apply(user));
            }
        });
    }

    private <T> Set<T> retainUniqueEntities(Iterable<T> entities) {
        HashSet<T> uniqueEntities = new HashSet<T>();
        for (T entity : entities) {
            boolean added;
            if (logger.isDebugEnabled()) {
                logger.debug("Going to add: " + entity);
            }
            if (added = uniqueEntities.add(entity)) continue;
            logger.warn("Duplicate entity. Entity is already in the set of entities to bulk add: " + entity);
        }
        return uniqueEntities;
    }

    /*
     * WARNING - void declaration
     */
    private BulkRemoveResult<String> removeAllUsers(long directoryId, Collection<? extends UserTemplate> users, boolean doRemove) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.DELETE_USER) && doRemove) {
            throw new DirectoryPermissionException("Directory does not allow removing users");
        }
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        ArrayList<UserTemplate> usersToRemove = new ArrayList<UserTemplate>();
        BulkRemoveResult.Builder resultBuilder = BulkRemoveResult.builder((long)users.size());
        Function usernameNormaliser = NameComparator.normaliserOf(com.atlassian.crowd.model.user.User.class);
        for (UserTemplate userTemplate : users) {
            try {
                this.findUserByName(directoryId, (String)usernameNormaliser.apply((Object)userTemplate));
                usersToRemove.add(userTemplate);
            }
            catch (UserNotFoundException e) {
                resultBuilder.addMissingEntity(usernameNormaliser.apply((Object)userTemplate));
            }
        }
        Set namesOfUniqueUsersToRemove = this.retainUniqueEntities(Iterables.transform(usersToRemove, (Function)usernameNormaliser));
        if (!doRemove) {
            resultBuilder.addFailedEntities(namesOfUniqueUsersToRemove);
        } else {
            void var11_13;
            ArrayList<String> failedEntities;
            if (remoteDirectory instanceof InternalRemoteDirectory) {
                BatchResult batchResult = ((InternalRemoteDirectory)remoteDirectory).removeAllUsers(namesOfUniqueUsersToRemove);
                List list = batchResult.getSuccessfulEntities();
                failedEntities = batchResult.getFailedEntities();
            } else {
                ArrayList<String> arrayList = new ArrayList<String>(namesOfUniqueUsersToRemove.size());
                failedEntities = new ArrayList();
                for (String username2 : namesOfUniqueUsersToRemove) {
                    try {
                        remoteDirectory.removeUser(username2);
                        arrayList.add(username2);
                    }
                    catch (Exception e) {
                        logger.error("Failed to remove user: {}", (Object)username2, (Object)e);
                        failedEntities.add(username2);
                    }
                }
            }
            resultBuilder.addFailedEntities(failedEntities);
            DirectoryManagerGeneric.logFailedEntitiesWithNames(remoteDirectory, failedEntities);
            this.eventPublisher.publish(new UsersDeletedEvent((Object)this, directory, Collections.unmodifiableCollection(var11_13)));
            this.eventPublisher.publishAll(var11_13.stream().map(username -> new UserDeletedEvent((Object)this, directory, username)).collect(Collectors.toList()));
        }
        return resultBuilder.build();
    }

    public BulkAddResult<Group> addAllGroups(long directoryId, Collection<GroupTemplate> groups, boolean overwrite) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException {
        List<GroupTemplate> failedEntities;
        List<Group> successfulEntities;
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.CREATE_GROUP)) {
            throw new DirectoryPermissionException("Directory does not allow adding of groups");
        }
        BulkAddResult.Builder bulkAddResultBuilder = BulkAddResult.builder((long)groups.size()).setOverwrite(overwrite);
        BulkRemoveResult<String> bulkRemoveExistingResult = this.removeAllGroups(directoryId, groups, overwrite);
        ArrayList groupsToAdd = overwrite ? Lists.newArrayList(DirectoryManagerGeneric.getDeletedAndNotFoundEntities(groups, bulkRemoveExistingResult, NameComparator.normaliserOf(GroupTemplate.class))) : Lists.newArrayList(DirectoryManagerGeneric.getNotFoundEntities(groups, bulkRemoveExistingResult, NameComparator.normaliserOf(GroupTemplate.class)));
        for (String stillExistingGroupName : bulkRemoveExistingResult.getFailedEntities()) {
            try {
                bulkAddResultBuilder.addExistingEntity((Object)this.findGroupByName(directoryId, stillExistingGroupName));
                if (overwrite) {
                    logger.error("Group could not be removed for bulk add with overwrite: {}", (Object)stillExistingGroupName);
                    continue;
                }
                logger.info("Group already exists in directory; overwrite is off so import will skip it: {}", (Object)stillExistingGroupName);
            }
            catch (GroupNotFoundException e) {
                logger.debug("Group previously existed and couldn't be removed but is now missing so importing it: {}", (Object)stillExistingGroupName);
                groupsToAdd.add(Iterables.find(groups, (Predicate)DirectoryEntityUtils.whereNameEquals((String)stillExistingGroupName)));
            }
        }
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        Set uniqueGroupsToAdd = this.retainUniqueEntities(groupsToAdd);
        if (remoteDirectory instanceof InternalRemoteDirectory) {
            BatchResult batchResult = ((InternalRemoteDirectory)remoteDirectory).addAllGroups(uniqueGroupsToAdd);
            successfulEntities = batchResult.getSuccessfulEntities();
            failedEntities = batchResult.getFailedEntities();
        } else {
            successfulEntities = new ArrayList(uniqueGroupsToAdd.size());
            failedEntities = new ArrayList();
            for (GroupTemplate group : uniqueGroupsToAdd) {
                try {
                    if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)group.getName())) {
                        throw new InvalidGroupException((Group)group, "Group name may not contain leading or trailing whitespace");
                    }
                    successfulEntities.add(remoteDirectory.addGroup(group));
                }
                catch (Exception e) {
                    logger.error("Failed to add group: {}", (Object)group, (Object)e);
                    failedEntities.add(group);
                }
            }
        }
        bulkAddResultBuilder.addFailedEntities(failedEntities);
        DirectoryManagerGeneric.logFailedEntities(remoteDirectory, failedEntities);
        for (Group addedGroup : successfulEntities) {
            this.eventPublisher.publish(new GroupCreatedEvent((Object)this, directory, addedGroup));
        }
        return bulkAddResultBuilder.build();
    }

    private BulkRemoveResult<String> removeAllGroups(long directoryId, Collection<GroupTemplate> groups, boolean doRemove) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException {
        Directory directory = this.findDirectoryById(directoryId);
        if (!this.permissionManager.hasPermission(directory, OperationType.DELETE_GROUP) && doRemove) {
            throw new DirectoryPermissionException("Directory does not allow removing groups");
        }
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        ArrayList<GroupTemplate> groupsToRemove = new ArrayList<GroupTemplate>();
        BulkRemoveResult.Builder resultBuilder = BulkRemoveResult.builder((long)groups.size());
        Function groupNameNormaliser = NameComparator.normaliserOf(Group.class);
        for (GroupTemplate group : groups) {
            try {
                this.findGroupByName(directoryId, (String)groupNameNormaliser.apply((Object)group));
                groupsToRemove.add(group);
            }
            catch (GroupNotFoundException e) {
                resultBuilder.addMissingEntity(groupNameNormaliser.apply((Object)group));
            }
        }
        Set namesOfUniqueGroupsToRemove = this.retainUniqueEntities(Iterables.transform(groupsToRemove, (Function)groupNameNormaliser));
        if (!doRemove) {
            resultBuilder.addFailedEntities(namesOfUniqueGroupsToRemove);
        } else {
            ArrayList<String> failedEntities;
            List<String> successfulEntities;
            if (remoteDirectory instanceof InternalRemoteDirectory) {
                BatchResult batchResult = ((InternalRemoteDirectory)remoteDirectory).removeAllGroups(namesOfUniqueGroupsToRemove);
                successfulEntities = batchResult.getSuccessfulEntities();
                failedEntities = batchResult.getFailedEntities();
            } else {
                successfulEntities = new ArrayList(namesOfUniqueGroupsToRemove.size());
                failedEntities = new ArrayList();
                for (String groupName : namesOfUniqueGroupsToRemove) {
                    try {
                        remoteDirectory.removeGroup(groupName);
                        successfulEntities.add(groupName);
                    }
                    catch (Exception e) {
                        logger.error("Failed to remove group: {}", (Object)groupName, (Object)e);
                        failedEntities.add(groupName);
                    }
                }
            }
            resultBuilder.addFailedEntities(failedEntities);
            DirectoryManagerGeneric.logFailedEntitiesWithNames(remoteDirectory, failedEntities);
            for (String deletedGroup : successfulEntities) {
                this.eventPublisher.publish(new GroupDeletedEvent((Object)this, directory, deletedGroup));
            }
        }
        return resultBuilder.build();
    }

    public BulkAddResult<String> addAllUsersToGroup(long directoryId, Collection<String> userNames, String groupName) throws DirectoryPermissionException, OperationFailedException, DirectoryNotFoundException, GroupNotFoundException {
        Group groupToUpdate;
        OperationType operationType;
        Directory directory = this.findDirectoryById(directoryId);
        if (this.permissionManager.hasPermission(directory, operationType = DirectoryManagerGeneric.getUpdateOperationType(groupToUpdate = this.findGroupByName(directoryId, groupName)))) {
            RemoteDirectory remoteDirectory = this.directoryInstanceLoader.getDirectory(directory);
            Set<String> usersToAdd = this.retainUniqueEntities(userNames);
            BulkAddResult.Builder resultBuilder = BulkAddResult.builder((long)userNames.size()).setOverwrite(true);
            if (remoteDirectory instanceof InternalRemoteDirectory) {
                BatchResult batchResult = ((InternalRemoteDirectory)remoteDirectory).addAllUsersToGroup(usersToAdd, groupName);
                List successfulUsers = batchResult.getSuccessfulEntities();
                for (String username : batchResult.getFailedEntities()) {
                    if (this.isUserDirectGroupMember(directoryId, username, groupName)) {
                        resultBuilder.addExistingEntity((Object)username);
                        continue;
                    }
                    resultBuilder.addFailedEntity((Object)username);
                }
                for (String username : successfulUsers) {
                    this.eventPublisher.publish(new GroupMembershipCreatedEvent((Object)this, directory, username, groupName, MembershipType.GROUP_USER));
                }
                this.eventPublisher.publish(new GroupMembershipsCreatedEvent((Object)this, directory, (Iterable)successfulUsers, groupName, MembershipType.GROUP_USER));
            } else {
                for (String username : usersToAdd) {
                    try {
                        this.addUserToGroup(directoryId, username, groupName);
                    }
                    catch (MembershipAlreadyExistsException e) {
                        resultBuilder.addExistingEntity((Object)username);
                    }
                    catch (Exception e) {
                        resultBuilder.addFailedEntity((Object)username);
                        logger.error(e.getMessage());
                    }
                }
            }
            BulkAddResult bulkAddResult = resultBuilder.build();
            for (String failedUser : bulkAddResult.getFailedEntities()) {
                logger.warn("Could not add the following user to the group '{}': {}", (Object)groupName, (Object)failedUser);
            }
            return bulkAddResult;
        }
        if (operationType.equals((Object)OperationType.UPDATE_GROUP)) {
            throw new DirectoryPermissionException("Directory does not allow group modifications");
        }
        throw new DirectoryPermissionException("Directory does not allow role modifications");
    }

    private static OperationType getCreateOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.CREATE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static OperationType getUpdateOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.UPDATE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static OperationType getUpdateAttributeOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.UPDATE_GROUP_ATTRIBUTE;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static OperationType getDeleteOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.DELETE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private static void logFailedEntities(RemoteDirectory remoteDirectory, Collection<? extends DirectoryEntity> failedEntities) {
        DirectoryManagerGeneric.logFailedEntitiesWithNames(remoteDirectory, Iterables.transform(failedEntities, (Function)new Function<DirectoryEntity, String>(){

            public String apply(DirectoryEntity input) {
                return input.getName();
            }
        }));
    }

    private static void logFailedEntitiesWithNames(RemoteDirectory remoteDirectory, Iterable<String> failedEntities) {
        String directoryName = remoteDirectory.getDescriptiveName();
        for (String failedEntity : failedEntities) {
            logger.warn("Could not add the following entityName to the directory '{}': {}", (Object)directoryName, (Object)failedEntity);
        }
    }

    public com.atlassian.crowd.model.user.User findUserByExternalId(long directoryId, String externalId) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException {
        return this.getDirectoryImplementation(directoryId).findUserByExternalId(externalId);
    }

    public UserWithAttributes findUserWithAttributesByExternalId(long directoryId, String externalId) throws DirectoryNotFoundException, UserNotFoundException, OperationFailedException {
        RemoteDirectory directory = this.getDirectoryImplementation(directoryId);
        com.atlassian.crowd.model.user.User user = directory.findUserByExternalId(externalId);
        return directory.findUserWithAttributesByName(user.getName());
    }

    public void expireAllPasswords(long directoryId) throws OperationFailedException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        if (!remoteDirectory.supportsPasswordExpiration()) {
            throw new OperationFailedException("Expiring passwords not supported by directory " + directoryId);
        }
        remoteDirectory.expireAllPasswords();
        this.eventPublisher.publish(new AllPasswordsExpiredEvent((Object)this, this.findDirectoryById(directoryId)));
    }

    public boolean supportsExpireAllPasswords(long directoryId) throws DirectoryInstantiationException, DirectoryNotFoundException {
        RemoteDirectory remoteDirectory = this.getDirectoryImplementation(directoryId);
        return remoteDirectory.supportsPasswordExpiration();
    }

    public AvatarReference getUserAvatarByName(long directoryId, String username, int sizeHint) throws UserNotFoundException, OperationFailedException, DirectoryNotFoundException {
        return this.getDirectoryImplementation(directoryId).getUserAvatarByName(username, sizeHint);
    }

    @Nonnull
    public com.atlassian.crowd.model.user.User findRemoteUserByName(Long directoryId, String username) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException {
        return this.getDirectoryImplementation(directoryId).getAuthoritativeDirectory().findUserByName(username);
    }

    public com.atlassian.crowd.model.user.User updateUserFromRemoteDirectory(com.atlassian.crowd.model.user.User remoteUser) throws OperationFailedException, DirectoryNotFoundException, UserNotFoundException {
        return this.getDirectoryImplementation(remoteUser.getDirectoryId()).updateUserFromRemoteDirectory(remoteUser);
    }

    public List<Application> findAuthorisedApplications(long directoryId, List<String> groupNames) {
        return this.applicationDAO.findAuthorisedApplications(directoryId, groupNames);
    }
}

