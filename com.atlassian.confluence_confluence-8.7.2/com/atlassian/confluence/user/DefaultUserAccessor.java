/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.user.DefaultUserAccessor
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.User
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.configuration.RepositoryAccessor
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.impl.DuplicateEntityException
 *  com.atlassian.user.impl.EntityValidationException
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.DefaultPager
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.search.page.PagerUtils
 *  com.atlassian.user.search.query.Query
 *  com.atlassian.user.security.password.Credential
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.InsufficientPrivilegeException;
import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.confluence.event.events.group.GroupCreateEvent;
import com.atlassian.confluence.event.events.group.GroupRemoveEvent;
import com.atlassian.confluence.event.events.user.UserCreateEvent;
import com.atlassian.confluence.event.events.user.UserDeactivateEvent;
import com.atlassian.confluence.event.events.user.UserReactivateEvent;
import com.atlassian.confluence.event.events.user.UserRemoveCompletedEvent;
import com.atlassian.confluence.event.events.user.UserRemoveEvent;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionManagerInternal;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceUpdateTrigger;
import com.atlassian.confluence.user.AtlassianUserQueryHelper;
import com.atlassian.confluence.user.AtlassianUserResolver;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceLocalAvatarFactory;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserManager;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.DisabledUserManager;
import com.atlassian.confluence.user.GroupManagerGroupResolver;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.UserManagementOperationFailedException;
import com.atlassian.confluence.user.UserMentionsContentReindexer;
import com.atlassian.confluence.user.UserProfilePictureAccessorImpl;
import com.atlassian.confluence.user.UserPropertySetAccessor;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.confluence.user.avatar.AvatarProviderAccessor;
import com.atlassian.confluence.user.crowd.CrowdUserConversionHelper;
import com.atlassian.confluence.user.crowd.CrowdUserDirectoryHelper;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.UserManager;
import com.atlassian.user.configuration.RepositoryAccessor;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.impl.DuplicateEntityException;
import com.atlassian.user.impl.EntityValidationException;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.DefaultPager;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.search.page.PagerUtils;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.security.password.Credential;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.opensymphony.module.propertyset.PropertySet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultUserAccessor
extends bucket.user.DefaultUserAccessor
implements UserAccessorInternal {
    @VisibleForTesting
    public static int BULK_FETCH_USERS_BATCH_SIZE = Integer.getInteger("confluence.user.fetch.batch.size", 400);
    @VisibleForTesting
    public static final int BULK_FETCH_GROUP_BATCH_SIZE = Integer.getInteger("confluence.group.fetch.batch.size", 400);
    private static final Logger log = LoggerFactory.getLogger(DefaultUserAccessor.class);
    private final SpacePermissionManagerInternal spacePermissionManager;
    private final PersonalInformationManager personalInformationManager;
    private final UserChecker userChecker;
    private final EventPublisher eventPublisher;
    private final ContentPermissionManager contentPermissionManager;
    private final PermissionManager permissionManager;
    private final CrowdService crowdService;
    private final DisabledUserManager disabledUserManager;
    private final ConfluenceUserDao confluenceUserDao;
    private final SettingsManager settingsManager;
    private final CrowdUserDirectoryHelper crowdUserDirectoryHelper;
    private final UserPropertySetAccessor propertySetAccessor;
    private final AtlassianUserQueryHelper atlassianUserQueryHelper;
    private final AtlassianUserResolver userResolver;
    private final UserProfilePictureAccessorImpl userProfilePictureAccessor;
    private final GroupManagerGroupResolver groupResolver;
    private final UserRemover userRemover;
    private final ConfluenceUserManager confluenceUserManager;
    private final UserVerificationTokenManager userVerificationTokenManager;
    private final BootstrapManager bootstrapManager;

    public DefaultUserAccessor(RepositoryAccessor repositoryAccessor, SpacePermissionManagerInternal spacePermissionManager, NotificationManager notificationManager, PersonalInformationManager personalInformationManager, UserChecker userChecker, AttachmentManager attachmentManager, EventPublisher eventPublisher, ContentPermissionManager contentPermissionManager, PermissionManager permissionManager, FollowManager followManager, CrowdService crowdService, DisabledUserManager disabledUserManager, ConfluenceUserDao confluenceUserDao, UserManager backingUserManager, SettingsManager settingsManager, AvatarProviderAccessor avatarProviderAccessor, ResourceAccessor resourceAccessor, PlatformTransactionManager transactionManager, UserMentionsContentReindexer userMentionsContentReindexer, CrowdUserDirectoryHelper crowdUserDirectoryHelper, SynchronizationManager synchronizationManager, UserVerificationTokenManager userVerificationTokenManager, BootstrapManager bootstrapManager) {
        super(repositoryAccessor);
        this.spacePermissionManager = spacePermissionManager;
        this.personalInformationManager = personalInformationManager;
        this.userChecker = userChecker;
        this.eventPublisher = eventPublisher;
        this.contentPermissionManager = contentPermissionManager;
        this.permissionManager = permissionManager;
        this.crowdService = crowdService;
        this.disabledUserManager = disabledUserManager;
        this.confluenceUserDao = confluenceUserDao;
        this.settingsManager = settingsManager;
        this.crowdUserDirectoryHelper = crowdUserDirectoryHelper;
        this.propertySetAccessor = new UserPropertySetAccessor(repositoryAccessor.getPropertySetFactory());
        this.atlassianUserQueryHelper = new AtlassianUserQueryHelper(repositoryAccessor.getEntityQueryParser());
        this.userResolver = new AtlassianUserResolver(confluenceUserDao, repositoryAccessor.getUserManager(), this.atlassianUserQueryHelper, crowdService);
        this.userProfilePictureAccessor = new UserProfilePictureAccessorImpl(avatarProviderAccessor, transactionManager, repositoryAccessor.getPropertySetFactory(), eventPublisher, new ConfluenceLocalAvatarFactory(repositoryAccessor.getPropertySetFactory(), personalInformationManager, attachmentManager, resourceAccessor));
        this.groupResolver = new GroupManagerGroupResolver(repositoryAccessor.getGroupManager());
        this.userRemover = new UserRemover(this, permissionManager, this.propertySetAccessor, personalInformationManager, spacePermissionManager, notificationManager, followManager, eventPublisher, confluenceUserDao, userMentionsContentReindexer, userChecker, synchronizationManager, disabledUserManager);
        this.confluenceUserManager = new ConfluenceUserManager(backingUserManager, confluenceUserDao, eventPublisher);
        this.userVerificationTokenManager = userVerificationTokenManager;
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public boolean isLicensedToAddMoreUsers() {
        return this.userChecker.isLicensedToAddMoreUsers();
    }

    public boolean isUserRemovable(com.atlassian.user.User user) throws EntityException {
        return !this.getUserManager().isReadOnly(user) && this.crowdUserDirectoryHelper.getDirectoriesForUser(user).size() <= 1;
    }

    public void addMembership(Group group, com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, group)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.getGroupManager().addMembership(group, user);
        }
        catch (EntityException e) {
            log.error(String.format("Failed to add '%s' as a member of '%s'", user.getName(), group.getName()), (Throwable)e);
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
        finally {
            this.userChecker.resetResult();
        }
    }

    public boolean removeMembership(Group group, com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, group)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.getGroupManager().removeMembership(group, user);
            boolean bl = true;
            return bl;
        }
        catch (EntityException e) {
            log.error(String.format("Failed to remove '%s' as a member of '%s'", user.getName(), group.getName()), (Throwable)e);
            boolean bl = false;
            return bl;
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
        finally {
            this.userChecker.resetResult();
        }
    }

    @Override
    public @Nullable ConfluenceUser getUserByName(String name) {
        return this.userResolver.getUserByName(name);
    }

    @Override
    public @Nullable ConfluenceUser getUserByKey(UserKey key) {
        return this.userResolver.getUserByKey(key);
    }

    @Override
    public boolean isDeletedUser(ConfluenceUser user) {
        return this.confluenceUserDao.isDeletedUser(user);
    }

    @Override
    public boolean isUnsyncedUser(ConfluenceUser user) {
        return this.confluenceUserDao.isUnsyncedUser(user);
    }

    @Override
    public boolean isCrowdManaged(ConfluenceUser user) {
        return !this.isUnsyncedUser(user) && !this.isDeletedUser(user);
    }

    @Override
    public @Nullable ConfluenceUser getExistingUserByKey(UserKey key) {
        return this.userResolver.getExistingUserByKey(key);
    }

    @Override
    public boolean exists(String name) {
        return this.confluenceUserManager.exists(name);
    }

    public com.atlassian.user.User addUser(String username, String password, String email, String fullname, String[] groups) {
        com.atlassian.user.User user = this.addUser(username, password, email, fullname);
        if (groups != null) {
            for (String groupName : groups) {
                Group group = this.getGroupCreateIfNecessary(groupName);
                this.addMembership(group, user);
            }
        }
        return user;
    }

    public com.atlassian.user.User addUser(String username, String password, String email, String fullname) {
        return this.createUser((com.atlassian.user.User)new DefaultUser(username.toLowerCase(), fullname, email), Credential.unencrypted((String)password));
    }

    public void saveUser(com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.getUserManager().saveUser(user);
        }
        catch (EntityException e) {
            throw new InfrastructureException((Throwable)e);
        }
    }

    public Group getGroupCreateIfNecessary(String name) {
        Group group = this.getGroup(name);
        if (group == null) {
            group = this.createGroup(name);
        }
        return group;
    }

    public void addMembership(String groupName, String username) {
        Group group = this.getGroup(groupName);
        ConfluenceUser user = this.getUserByName(username);
        if (group == null || user == null) {
            log.error(String.format("Failed to add '%s' as a member of '%s'", username, groupName));
            return;
        }
        this.addMembership(group, user);
    }

    public boolean removeMembership(String groupName, String username) {
        Group group = this.getGroup(groupName);
        ConfluenceUser user = this.getUserByName(username);
        if (group == null || user == null) {
            log.error(String.format("Failed to remove '%s' as a member of '%s'", username, groupName));
            return false;
        }
        return this.removeMembership(group, user);
    }

    @Override
    public ConfluenceUser createUser(com.atlassian.user.User userTemplate, Credential credential) {
        if (!this.permissionManager.hasCreatePermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, com.atlassian.user.User.class)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.userWillBeCreated();
            com.atlassian.user.User createdUser = this.getUserManager().createUser(userTemplate, credential);
            this.userWasCreated(createdUser);
            return (ConfluenceUser)createdUser;
        }
        catch (EntityException e) {
            throw new InfrastructureException((Throwable)e);
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
    }

    private void userWillBeCreated() {
        if (!this.isLicensedToAddMoreUsers()) {
            throw new LicenseUserLimitExceededException("You are not licensed to add any more users to this installation of Confluence. Please contact sales@atlassian.com");
        }
    }

    private void userWasCreated(com.atlassian.user.User user) {
        this.personalInformationManager.createPersonalInformation(user);
        this.userChecker.incrementRegisteredUserCount();
        if (this.bootstrapManager.isSetupComplete()) {
            this.eventPublisher.publish((Object)new UserCreateEvent(this, user));
        }
    }

    public Group addGroup(String groupname) {
        return this.createGroup(groupname.toLowerCase());
    }

    public Group createGroup(String groupname) {
        if (!this.permissionManager.hasCreatePermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, Group.class)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            Group group = this.getGroupManager().createGroup(groupname);
            if (this.bootstrapManager.isSetupComplete()) {
                this.eventPublisher.publish((Object)new GroupCreateEvent(this, group));
            }
            return group;
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
            return null;
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
    }

    public void removeGroup(Group group) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, group)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.spacePermissionManager.removeAllPermissionsForGroup(group.getName(), SpacePermissionContext.builder().updateTrigger(SpaceUpdateTrigger.GROUP_REMOVED).sendEvents(false).build());
            this.contentPermissionManager.removeAllGroupPermissions(group.getName());
            this.getGroupManager().removeGroup(group);
            this.eventPublisher.publish((Object)new GroupRemoveEvent(this, group));
        }
        catch (EntityException e) {
            throw new InfrastructureException((Throwable)e);
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
        finally {
            this.userChecker.resetResult();
        }
    }

    public void removeUser(com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.userRemover.removeUser(user, userArg -> {
            ConfluenceUser userToRemove = this.getUserByName(userArg.getName());
            if (userToRemove != null) {
                try {
                    this.getUserManager().removeUser((com.atlassian.user.User)userToRemove);
                }
                catch (EntityException e) {
                    throw new InfrastructureException((Throwable)e);
                }
            }
        });
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<String> getUserNamesWithConfluenceAccess() {
        try (Ticker ignored = Timers.start((String)(this.getClass().getName() + "_getUserNamesWithConfluenceAccess"));){
            HashMap<String, String> activeUserNames = new HashMap<String, String>();
            for (String name : this.getActiveUserNames()) {
                activeUserNames.put(DefaultUserAccessor.normalise(name), name);
            }
            List<SpacePermission> usePermissions = this.spacePermissionManager.getGlobalPermissions("USECONFLUENCE");
            TreeSet<String> usernames = new TreeSet<String>();
            for (SpacePermission permission : usePermissions) {
                ConfluenceUser user = permission.getUserSubject();
                if (user != null) {
                    String normalisedName = DefaultUserAccessor.normalise(user.getName());
                    String originalUsernameFromMap = (String)activeUserNames.get(normalisedName);
                    if (originalUsernameFromMap == null) continue;
                    usernames.add(originalUsernameFromMap);
                    continue;
                }
                if (permission.isGroupPermission()) {
                    Group group = this.getGroup(permission.getGroup());
                    if (group == null) {
                        log.info("Could not find group configured with USE permission: {}", (Object)permission.getGroup());
                        continue;
                    }
                    for (String member : this.getMemberNames(group)) {
                        String normalisedName = DefaultUserAccessor.normalise(member);
                        String originalUsernameFromMap = (String)activeUserNames.get(normalisedName);
                        if (originalUsernameFromMap == null) continue;
                        usernames.add(originalUsernameFromMap);
                    }
                    continue;
                }
                log.info("Found USE permission with no associated username or group: {}", (Object)permission);
            }
            ImmutableList usernameList = ImmutableList.copyOf(usernames);
            log.debug("Found {} licensed users: {}", (Object)usernameList.size(), (Object)usernameList);
            ImmutableList immutableList = usernameList;
            return immutableList;
        }
    }

    @Override
    public Pager<ConfluenceUser> searchUnsyncedUsers(String searchParam) {
        return new DefaultPager(this.confluenceUserDao.searchUnsyncedUsers(searchParam));
    }

    private static String normalise(String name) {
        return name.toLowerCase(Locale.ENGLISH);
    }

    private Iterable<String> getActiveUserNames() {
        PropertyRestriction restriction = Restriction.on((Property)UserTermKeys.ACTIVE).containing((Object)true);
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)restriction).returningAtMost(-1);
        return this.crowdService.search((com.atlassian.crowd.embedded.api.Query)query);
    }

    @Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public int countLicenseConsumingUsers() {
        return this.getUserNamesWithConfluenceAccess().size();
    }

    private User toCrowdUser(com.atlassian.user.User legacyUser) {
        return new CrowdUserConversionHelper(this.crowdService).toCrowdUser(legacyUser);
    }

    @Override
    public boolean isDeactivated(String username) {
        return username != null && this.disabledUserManager.isDisabled(username);
    }

    @Override
    public boolean isDeactivated(com.atlassian.user.User user) {
        return user != null && this.disabledUserManager.isDisabled(user);
    }

    public void savePersonalInformation(PersonalInformation newInfo, PersonalInformation oldInfo) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, oldInfo.getUser())) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        String oldUsername = oldInfo.getUsername();
        String newUsername = newInfo.getUsername();
        if (!newUsername.equals(oldUsername)) {
            throw new IllegalArgumentException(String.format("Usernames on update do not match. New: %s Old: %s", newUsername, oldUsername));
        }
        this.personalInformationManager.savePersonalInformation(newInfo, oldInfo);
    }

    public void deactivateUser(com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.disabledUserManager.disableUser(this.toCrowdUser(user));
            this.userChecker.decrementRegisteredUserCount();
            this.eventPublisher.publish((Object)new UserDeactivateEvent(this, user, true));
        }
        catch (UserNotFoundException e) {
            log.error("Attempt to deactivate user " + user.getName() + " unsuccessful. User not found.");
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
    }

    public void reactivateUser(com.atlassian.user.User user) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        try {
            this.disabledUserManager.enableUser(this.toCrowdUser(user));
            this.userChecker.incrementRegisteredUserCount();
            this.eventPublisher.publish((Object)new UserReactivateEvent(this, user, true));
        }
        catch (UserNotFoundException e) {
            log.error("Attempt to reactivate user " + user.getName() + " unsuccessful. User not found.");
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
    }

    @Override
    public boolean isReadOnly(com.atlassian.user.User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        try {
            return this.getUserManager().isReadOnly(user);
        }
        catch (EntityException e) {
            log.error("Error determining if User [" + user + "] is readonly", (Throwable)e);
            return false;
        }
    }

    @Override
    public boolean isReadOnly(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("Group cannot be null.");
        }
        try {
            return this.getGroupManager().isReadOnly(group);
        }
        catch (EntityException e) {
            log.error("Error determining if Group [ " + group + "] is readonly", (Throwable)e);
            return false;
        }
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public ProfilePictureInfo getUserProfilePicture(@Nullable com.atlassian.user.User user) {
        return this.userProfilePictureAccessor.getUserProfilePicture(user);
    }

    @Override
    public void setUserProfilePicture(com.atlassian.user.User user, Attachment attachment) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.userProfilePictureAccessor.setUserProfilePicture(user, attachment);
    }

    @Override
    public void setUserProfilePicture(com.atlassian.user.User user, String imagePath) {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.userProfilePictureAccessor.setUserProfilePicture(user, imagePath);
    }

    @Override
    @Deprecated
    public List<String> getAllDefaultGroupNames() {
        return ImmutableList.of((Object)this.settingsManager.getGlobalSettings().getDefaultUsersGroup(), (Object)"confluence-administrators");
    }

    @Override
    @Deprecated
    public String getNewUserDefaultGroupName() {
        return this.settingsManager.getGlobalSettings().getDefaultUsersGroup();
    }

    @Override
    public boolean isSuperUser(com.atlassian.user.User user) {
        Group confluenceAdmins = this.getGroup("confluence-administrators");
        return this.hasMembership(confluenceAdmins, user) && !this.isDeactivated(user);
    }

    @Override
    @Transactional(readOnly=true)
    public ConfluenceUserPreferences getConfluenceUserPreferences(@Nullable com.atlassian.user.User user) {
        return this.propertySetAccessor.getConfluenceUserPreferences(user);
    }

    public UserPreferences getUserPreferences(com.atlassian.user.User user) {
        return this.propertySetAccessor.getUserPreferences(user);
    }

    @Override
    public PropertySet getPropertySet(com.atlassian.user.User user) {
        return this.propertySetAccessor.getPropertySet(user);
    }

    @Override
    public List<String> getGroupNames(com.atlassian.user.User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return this.getGroupNamesForUserName(user.getName());
    }

    @Override
    public List<String> getGroupNamesForUserName(String userName) {
        if (userName == null) {
            return Collections.emptyList();
        }
        return ImmutableList.copyOf((Iterable)this.crowdService.search((com.atlassian.crowd.embedded.api.Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(userName).returningAtMost(-1)));
    }

    @Override
    public List<Group> getGroupsAsList() {
        return PagerUtils.toList((Pager)this.getGroups());
    }

    @Override
    public Group getGroup(String name) {
        return this.groupResolver.getGroup(name);
    }

    @Override
    public Pager<String> getMemberNames(Group group) {
        return this.groupResolver.getMemberNames(group);
    }

    @Override
    public List<String> getMemberNamesAsList(Group group) {
        return this.groupResolver.getMemberNamesAsList(group);
    }

    @Override
    public Iterable<ConfluenceUser> getMembers(Group group) {
        if (group == null) {
            return Collections.emptySet();
        }
        try {
            return StreamSupport.stream(this.getGroupManager().getMemberNames(group).spliterator(), false).map(this::getUserByName).filter(Objects::nonNull).collect(Collectors.toList());
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
            return Collections.emptySet();
        }
    }

    @Override
    public List<Group> getWriteableGroups() {
        return this.getGroupManager().getWritableGroups();
    }

    @Override
    @Transactional(readOnly=true)
    public PropertySet getPropertySet(ConfluenceUser user) {
        return this.propertySetAccessor.getPropertySet(user);
    }

    @Override
    public List<com.atlassian.user.User> findUsersAsList(Query<com.atlassian.user.User> search) throws EntityException {
        return this.atlassianUserQueryHelper.findUsersAsList(search);
    }

    public SearchResult<com.atlassian.user.User> findUsers(Query<com.atlassian.user.User> query) throws EntityException {
        return this.atlassianUserQueryHelper.findUsers(query);
    }

    @Override
    public ConfluenceUser renameUser(ConfluenceUser user, String newUsername) throws EntityException {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        if (this.crowdService.getUser(newUsername) != null) {
            throw new DuplicateEntityException("User with username '" + newUsername + "' already exists.");
        }
        try {
            this.crowdService.renameUser(this.toCrowdUser(user), newUsername);
            return user.getName().equals(newUsername) ? user : this.getUserByKey(user.getKey());
        }
        catch (InvalidUserException e) {
            throw new EntityValidationException((Throwable)e);
        }
        catch (OperationNotPermittedException e) {
            throw new EntityException((Throwable)e);
        }
        catch (CrowdRuntimeException e) {
            throw new UserManagementOperationFailedException(e);
        }
    }

    @Override
    public List<Group> getGroupsByGroupNames(List<String> groupNames) {
        if (groupNames == null || groupNames.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            ArrayList<Group> groups = new ArrayList<Group>();
            List partitions = Lists.partition(groupNames, (int)BULK_FETCH_GROUP_BATCH_SIZE);
            for (List partition : partitions) {
                groups.addAll(this.atlassianUserQueryHelper.findGroupsByName(partition));
            }
            return groups;
        }
        catch (EntityException e) {
            log.error(e.getMessage(), (Throwable)e);
            return Collections.emptyList();
        }
    }

    @Override
    public PageResponse<ConfluenceUser> getUsers(LimitedRequest limitedRequest) {
        return this.userResolver.getUsers(limitedRequest);
    }

    @Override
    public List<ConfluenceUser> getUsersByUserKeys(List<UserKey> userKeys) {
        return this.userResolver.getUsersByUserKeys(userKeys);
    }

    @Override
    public @Nullable ConfluenceUser getExistingUserByPerson(@NonNull Person person) {
        return this.userResolver.getExistingUserByPerson(person);
    }

    @Override
    public Optional<ConfluenceUser> getExistingByApiUser(com.atlassian.confluence.api.model.people.User user) {
        return this.userResolver.getExistingByApiUser(user);
    }

    @Override
    public int countUnsyncedUsers() {
        return this.confluenceUserDao.countUnsyncedUsers();
    }

    public SearchResult getUsersByEmail(String email) {
        return this.atlassianUserQueryHelper.getUsersByEmail(email);
    }

    @Override
    public void alterPassword(com.atlassian.user.User user, String plainTextPassword, String token) throws EntityException {
        if (!this.isValidAlterPasswordToken(user, token)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername(), "Unable to change user password without valid token.");
        }
        this.getUserManager().alterPassword(user, plainTextPassword);
    }

    public void alterPassword(com.atlassian.user.User user, String plainTextPassword) throws EntityException {
        if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, user)) {
            throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
        }
        this.getUserManager().alterPassword(user, plainTextPassword);
    }

    private boolean isValidAlterPasswordToken(com.atlassian.user.User user, String token) {
        String username = user.getName();
        return this.userVerificationTokenManager.hasValidUserToken(username, UserVerificationTokenType.PASSWORD_RESET, token) || this.userVerificationTokenManager.hasValidUserToken(username, UserVerificationTokenType.USER_SIGNUP, token);
    }

    private static class UserRemover {
        private final UserAccessorInternal userAccessor;
        private final PermissionManager permissionManager;
        private final UserPropertySetAccessor propertySetAccessor;
        private final PersonalInformationManager personalInformationManager;
        private final SpacePermissionManagerInternal spacePermissionManager;
        private final NotificationManager notificationManager;
        private final FollowManager followManager;
        private final EventPublisher eventPublisher;
        private final ConfluenceUserDao confluenceUserDao;
        private final UserMentionsContentReindexer userMentionsContentReindexer;
        private final UserChecker userChecker;
        private final SynchronizationManager synchronizationManager;
        private final DisabledUserManager disabledUserManager;

        public UserRemover(UserAccessorInternal userAccessor, PermissionManager permissionManager, UserPropertySetAccessor propertySetAccessor, PersonalInformationManager personalInformationManager, SpacePermissionManagerInternal spacePermissionManager, NotificationManager notificationManager, FollowManager followManager, EventPublisher eventPublisher, ConfluenceUserDao confluenceUserDao, UserMentionsContentReindexer userMentionsContentReindexer, UserChecker userChecker, SynchronizationManager synchronizationManager, DisabledUserManager disabledUserManager) {
            this.userAccessor = userAccessor;
            this.permissionManager = permissionManager;
            this.propertySetAccessor = propertySetAccessor;
            this.personalInformationManager = personalInformationManager;
            this.spacePermissionManager = spacePermissionManager;
            this.notificationManager = notificationManager;
            this.followManager = followManager;
            this.eventPublisher = eventPublisher;
            this.confluenceUserDao = confluenceUserDao;
            this.userMentionsContentReindexer = userMentionsContentReindexer;
            this.userChecker = userChecker;
            this.synchronizationManager = synchronizationManager;
            this.disabledUserManager = disabledUserManager;
        }

        void removeUser(com.atlassian.user.User user, Consumer<ConfluenceUser> crowdRemover) {
            long startRemove = System.currentTimeMillis();
            if (user == null) {
                throw new IllegalArgumentException("Do not call delete with a null user");
            }
            if (!this.permissionManager.hasPermission((com.atlassian.user.User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, user)) {
                throw new InsufficientPrivilegeException(AuthenticatedUserThreadLocal.getUsername());
            }
            ConfluenceUser confluenceUser = FindUserHelper.getUser(user);
            if (confluenceUser == null) {
                return;
            }
            boolean isCrowdManaged = this.userAccessor.isCrowdManaged(confluenceUser);
            if (isCrowdManaged) {
                this.userAccessor.getGroups(confluenceUser).forEach(group -> {
                    log.info("Removing user with key [{}] from group {}", (Object)confluenceUser.getKey().getStringValue(), (Object)group.getName());
                    this.userAccessor.removeMembership((Group)group, confluenceUser);
                });
            }
            this.propertySetAccessor.removeUserProperties(confluenceUser);
            this.personalInformationManager.removePersonalInformation(confluenceUser);
            this.spacePermissionManager.removeAllUserPermissions(confluenceUser, SpacePermissionContext.createDefault());
            this.notificationManager.removeAllNotificationsForUser(confluenceUser);
            this.followManager.removeAllConnectionsFor(confluenceUser);
            this.eventPublisher.publish((Object)new UserRemoveEvent(this, confluenceUser));
            try {
                if (isCrowdManaged) {
                    crowdRemover.accept(confluenceUser);
                }
                String username = confluenceUser.getName();
                this.confluenceUserDao.rename(confluenceUser, confluenceUser.getKey().getStringValue(), true);
                this.userMentionsContentReindexer.reindex(confluenceUser, username);
                if (isCrowdManaged && !this.disabledUserManager.isDisabled(confluenceUser)) {
                    this.userChecker.decrementRegisteredUserCount();
                }
            }
            catch (CrowdRuntimeException e) {
                throw new UserManagementOperationFailedException(e);
            }
            this.synchronizationManager.runOnSuccessfulCommit(() -> this.eventPublisher.publish((Object)new UserRemoveCompletedEvent(this, confluenceUser)));
            log.info("Finished removing user with key [{}] in {}ms", (Object)confluenceUser.getKey().getStringValue(), (Object)(System.currentTimeMillis() - startRemove));
        }
    }
}

