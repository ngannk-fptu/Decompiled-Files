/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.atlassian.crowd.common.util.ProxyUtil
 *  com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager
 *  com.atlassian.crowd.embedded.api.ApplicationFactory
 *  com.atlassian.crowd.embedded.api.Directories
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.OperationType
 *  com.atlassian.crowd.embedded.api.PasswordCredential
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.embedded.api.UserCapabilities
 *  com.atlassian.crowd.embedded.impl.DirectoryUserCapabilities
 *  com.atlassian.crowd.embedded.impl.IdentifierUtils
 *  com.atlassian.crowd.event.EventStore
 *  com.atlassian.crowd.event.EventTokenExpiredException
 *  com.atlassian.crowd.event.Events
 *  com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException
 *  com.atlassian.crowd.event.user.UserAuthenticatedEvent
 *  com.atlassian.crowd.event.user.UserAuthenticationFailedInvalidAuthenticationEvent
 *  com.atlassian.crowd.exception.ApplicationPermissionException
 *  com.atlassian.crowd.exception.BulkAddFailedException
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
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.exception.ReadOnlyGroupException
 *  com.atlassian.crowd.exception.UserAlreadyExistsException
 *  com.atlassian.crowd.exception.UserNotFoundException
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.manager.application.ApplicationService
 *  com.atlassian.crowd.manager.application.ApplicationService$MembershipsIterable
 *  com.atlassian.crowd.manager.application.PagedSearcher
 *  com.atlassian.crowd.manager.application.PagingNotSupportedException
 *  com.atlassian.crowd.manager.avatar.AvatarProvider
 *  com.atlassian.crowd.manager.avatar.AvatarReference
 *  com.atlassian.crowd.manager.avatar.AvatarReference$BlobAvatar
 *  com.atlassian.crowd.manager.avatar.AvatarReference$UriAvatarReference
 *  com.atlassian.crowd.manager.directory.BulkAddResult
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.crowd.manager.directory.DirectoryPermissionException
 *  com.atlassian.crowd.manager.permission.PermissionManager
 *  com.atlassian.crowd.manager.webhook.InvalidWebhookEndpointException
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.model.DirectoryEntity
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.ApplicationDirectoryMapping
 *  com.atlassian.crowd.model.application.ApplicationType
 *  com.atlassian.crowd.model.application.Applications
 *  com.atlassian.crowd.model.event.OperationEvent
 *  com.atlassian.crowd.model.group.Group
 *  com.atlassian.crowd.model.group.GroupTemplate
 *  com.atlassian.crowd.model.group.GroupWithAttributes
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.crowd.model.user.UserTemplateWithAttributes
 *  com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes
 *  com.atlassian.crowd.model.user.UserWithAttributes
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.atlassian.crowd.model.webhook.WebhookTemplate
 *  com.atlassian.crowd.search.Entity
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.membership.MembershipQuery
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Pair
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.common.util.ProxyUtil;
import com.atlassian.crowd.darkfeature.CrowdDarkFeatureManager;
import com.atlassian.crowd.embedded.api.ApplicationFactory;
import com.atlassian.crowd.embedded.api.Directories;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.PasswordCredential;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.embedded.api.UserCapabilities;
import com.atlassian.crowd.embedded.impl.DirectoryUserCapabilities;
import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.atlassian.crowd.event.EventStore;
import com.atlassian.crowd.event.EventTokenExpiredException;
import com.atlassian.crowd.event.Events;
import com.atlassian.crowd.event.IncrementalSynchronisationNotAvailableException;
import com.atlassian.crowd.event.user.UserAuthenticatedEvent;
import com.atlassian.crowd.event.user.UserAuthenticationFailedInvalidAuthenticationEvent;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.BulkAddFailedException;
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
import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.ReadOnlyGroupException;
import com.atlassian.crowd.exception.UserAlreadyExistsException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.application.ApplicationService;
import com.atlassian.crowd.manager.application.AuthenticationOrderOptimizer;
import com.atlassian.crowd.manager.application.EventTransformer;
import com.atlassian.crowd.manager.application.FixedSizeLinkedHashMap;
import com.atlassian.crowd.manager.application.MembershipsIterableImpl;
import com.atlassian.crowd.manager.application.PagedSearcher;
import com.atlassian.crowd.manager.application.PagingNotSupportedException;
import com.atlassian.crowd.manager.application.canonicality.CanonicalEntityByNameFinder;
import com.atlassian.crowd.manager.application.canonicality.SimpleCanonicalityChecker;
import com.atlassian.crowd.manager.application.filtering.AccessFilter;
import com.atlassian.crowd.manager.application.filtering.AccessFilterFactory;
import com.atlassian.crowd.manager.application.search.GroupSearchStrategy;
import com.atlassian.crowd.manager.application.search.MembershipSearchStrategy;
import com.atlassian.crowd.manager.application.search.SearchStrategyFactory;
import com.atlassian.crowd.manager.application.search.UserSearchStrategy;
import com.atlassian.crowd.manager.avatar.AvatarProvider;
import com.atlassian.crowd.manager.avatar.AvatarReference;
import com.atlassian.crowd.manager.directory.BulkAddResult;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.DirectoryPermissionException;
import com.atlassian.crowd.manager.permission.PermissionManager;
import com.atlassian.crowd.manager.webhook.InvalidWebhookEndpointException;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.model.DirectoryEntity;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.atlassian.crowd.model.application.ApplicationType;
import com.atlassian.crowd.model.application.Applications;
import com.atlassian.crowd.model.event.OperationEvent;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.group.GroupTemplate;
import com.atlassian.crowd.model.group.GroupWithAttributes;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.crowd.model.user.UserTemplateWithAttributes;
import com.atlassian.crowd.model.user.UserTemplateWithCredentialAndAttributes;
import com.atlassian.crowd.model.user.UserWithAttributes;
import com.atlassian.crowd.model.webhook.Webhook;
import com.atlassian.crowd.model.webhook.WebhookTemplate;
import com.atlassian.crowd.search.Entity;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.membership.MembershipQuery;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Pair;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ApplicationServiceGeneric
implements ApplicationService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceGeneric.class);
    private static final Pattern USER_KEY_PATTERN = Pattern.compile("(\\d+):(.+)");
    private static final Set<OperationType> UPDATE_GROUP_PERMISSION = ImmutableSet.of((Object)OperationType.UPDATE_GROUP);
    private static final Set<OperationType> CREATE_AND_UPDATE_GROUP_PERMISSIONS = ImmutableSet.of((Object)OperationType.CREATE_GROUP, (Object)OperationType.UPDATE_GROUP);
    private final SearchStrategyFactory searchStrategyFactory;
    private final CrowdDarkFeatureManager crowdDarkFeatureManager;
    private final DirectoryManager directoryManager;
    private final PermissionManager permissionManager;
    private final EventPublisher eventPublisher;
    private final EventStore eventStore;
    private final WebhookRegistry webhookRegistry;
    private final AvatarProvider avatarProvider;
    private final AuthenticationOrderOptimizer authenticationOrderOptimizer;
    private final ApplicationFactory applicationFactory;
    private final AccessFilterFactory accessFilterFactory;
    private final Predicate<Directory> supportsNestedGroups = new Predicate<Directory>(){

        public boolean apply(Directory directory) {
            try {
                return ApplicationServiceGeneric.this.directoryManager.supportsNestedGroups(directory.getId().longValue());
            }
            catch (DirectoryInstantiationException e) {
                throw new com.atlassian.crowd.exception.runtime.OperationFailedException((Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
            }
        }
    };

    public ApplicationServiceGeneric(DirectoryManager directoryManager, SearchStrategyFactory searchStrategyFactory, PermissionManager permissionManager, EventPublisher eventPublisher, EventStore eventStore, WebhookRegistry webhookRegistry, AvatarProvider avatarProvider, AuthenticationOrderOptimizer authenticationOrderOptimizer, ApplicationFactory applicationFactory, AccessFilterFactory accessFilterFactory, CrowdDarkFeatureManager crowdDarkFeatureManager) {
        this.directoryManager = (DirectoryManager)Preconditions.checkNotNull((Object)directoryManager);
        this.searchStrategyFactory = (SearchStrategyFactory)Preconditions.checkNotNull((Object)searchStrategyFactory);
        this.permissionManager = (PermissionManager)Preconditions.checkNotNull((Object)permissionManager);
        this.eventPublisher = eventPublisher;
        this.eventStore = eventStore;
        this.webhookRegistry = webhookRegistry;
        this.authenticationOrderOptimizer = authenticationOrderOptimizer;
        this.avatarProvider = avatarProvider;
        this.applicationFactory = applicationFactory;
        this.accessFilterFactory = accessFilterFactory;
        this.crowdDarkFeatureManager = crowdDarkFeatureManager;
    }

    public com.atlassian.crowd.model.user.User authenticateUser(Application application, String username, PasswordCredential passwordCredential) throws OperationFailedException, InactiveAccountException, InvalidAuthenticationException, ExpiredCredentialException, UserNotFoundException {
        if (application.getApplicationDirectoryMappings().isEmpty()) {
            throw new InvalidAuthenticationException("Unable to authenticate user as there are no directories mapped to the application " + application.getName());
        }
        OperationFailedException failedException = null;
        List<Directory> sortedDirectories = this.authenticationOrderOptimizer.optimizeDirectoryOrderForAuthentication(application, this.getActiveDirectories(application), username);
        for (Directory directory : sortedDirectories) {
            String directoryDescription = directory.getName() + " (" + directory.getId() + ")";
            try {
                logger.debug("Trying to authenticate user '{}' in directory '{}' for application '{}'", new Object[]{username, directoryDescription, application.getName()});
                com.atlassian.crowd.model.user.User user = this.directoryManager.authenticateUser(directory.getId().longValue(), username, passwordCredential);
                logger.debug("Authenticated user '{}' in directory '{}' for application '{}'", new Object[]{username, directoryDescription, application.getName()});
                this.eventPublisher.publish((Object)new UserAuthenticatedEvent((Object)this, directory, application, user));
                return user;
            }
            catch (OperationFailedException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to authenticate against '{}': ", (Object)directoryDescription, (Object)e);
                }
                logger.error("Directory '{}' is not functional during authentication of '{}'. Skipped.", (Object)directoryDescription, (Object)username);
                if (failedException != null) continue;
                failedException = e;
            }
            catch (UserNotFoundException e) {
                logger.debug("User '{}' does not exist in directory '{}', continuing", (Object)username, (Object)directoryDescription);
            }
            catch (InvalidAuthenticationException e) {
                logger.info("Invalid credentials for user '{}' in directory '{}', aborting", (Object)username, (Object)directoryDescription);
                this.eventPublisher.publish((Object)new UserAuthenticationFailedInvalidAuthenticationEvent((Object)this, directory, username));
                throw new InvalidAuthenticationException(username, directory, (Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
        }
        if (failedException != null) {
            logger.debug("Failed to find user '{}' in any directory for application '{}', rethrowing: ", new Object[]{username, application.getName(), failedException});
            throw failedException;
        }
        logger.debug("Failed to find user '{}' in any directory for application '{}'", (Object)username, (Object)application.getName());
        throw new UserNotFoundException(username);
    }

    public boolean isUserAuthorised(Application application, String username) {
        try {
            com.atlassian.crowd.model.user.User user = this.findUserByName(application, username);
            return this.isUserAuthorised(application, user);
        }
        catch (UserNotFoundException e) {
            return false;
        }
    }

    public boolean isUserAuthorised(Application application, com.atlassian.crowd.model.user.User user) {
        return this.isUserAuthorised(user.getName(), user.getDirectoryId(), application);
    }

    private boolean isUserAuthorised(String username, long directoryId, Application application) {
        try {
            return this.isAllowedToAuthenticate(username, directoryId, application);
        }
        catch (OperationFailedException e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public void addAllUsers(Application application, Collection<UserTemplateWithCredentialAndAttributes> userTemplates) throws ApplicationPermissionException, OperationFailedException, BulkAddFailedException {
        logger.debug("Adding users for application {}", (Object)application);
        HashSet<String> failedUsers = new HashSet<String>();
        HashSet<String> existingUsers = new HashSet<String>();
        Directory directory = this.findFirstDirectoryWithCreateUserPermission(application);
        if (directory == null) {
            throw new ApplicationPermissionException("Application '" + application.getName() + "' has no directories that allow adding of users.");
        }
        try {
            for (UserTemplateWithCredentialAndAttributes userTemplate : userTemplates) {
                userTemplate.setDirectoryId(directory.getId().longValue());
            }
            BulkAddResult result = this.directoryManager.addAllUsers(directory.getId().longValue(), userTemplates, false);
            for (com.atlassian.crowd.model.user.User user : result.getExistingEntities()) {
                existingUsers.add(user.getName());
            }
            for (com.atlassian.crowd.model.user.User user : result.getFailedEntities()) {
                failedUsers.add(user.getName());
            }
        }
        catch (DirectoryPermissionException ex) {
            throw new ApplicationPermissionException("Permission Exception when trying to add users to directory '" + directory.getName() + "'. " + ex.getMessage(), (Throwable)ex);
        }
        catch (DirectoryNotFoundException ex) {
            throw new OperationFailedException("Directory Not Found when trying to add users to directory '" + directory.getName() + "'.", (Throwable)ex);
        }
        if (failedUsers.size() > 0 || existingUsers.size() > 0) {
            throw new BulkAddFailedException(failedUsers, existingUsers);
        }
    }

    private Directory findFirstDirectoryWithCreateUserPermission(Application application) {
        for (Directory directory : this.getActiveDirectories(application)) {
            if (!this.permissionManager.hasPermission(application, directory, OperationType.CREATE_USER)) continue;
            return directory;
        }
        return null;
    }

    public com.atlassian.crowd.model.user.User findUserByName(Application application, String name) throws UserNotFoundException {
        return this.finder(application).findUserByName(name);
    }

    public com.atlassian.crowd.model.user.User findRemoteUserByName(Application application, String username) throws UserNotFoundException {
        return this.finder(application).findRemoteUserByName(username);
    }

    @VisibleForTesting
    static Pair<Long, String> directoryIdAndExternalIdFromKey(String key) {
        Matcher matcher = USER_KEY_PATTERN.matcher(key);
        Preconditions.checkArgument((boolean)matcher.matches(), (Object)"Invalid user key");
        return Pair.pair((Object)Long.parseLong(matcher.group(1)), (Object)matcher.group(2));
    }

    public com.atlassian.crowd.model.user.User findUserByKey(Application application, String key) throws UserNotFoundException {
        Pair<Long, String> directoryIdAndExternalId = ApplicationServiceGeneric.directoryIdAndExternalIdFromKey(key);
        long directoryId = (Long)directoryIdAndExternalId.left();
        String externalId = (String)directoryIdAndExternalId.right();
        boolean isDirectoryActive = Iterables.any(this.getActiveDirectories(application), (Predicate)Directories.directoryWithIdPredicate((long)directoryId));
        if (!isDirectoryActive) {
            logger.debug("Cannot look up in directory {} because it is not mapped to the application", (Object)directoryId);
            throw new UserNotFoundException(externalId);
        }
        try {
            com.atlassian.crowd.model.user.User user = this.directoryManager.findUserByExternalId(directoryId, externalId);
            return this.checkCanonicalUser(user, application);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (OperationFailedException e) {
            throw new UserNotFoundException(externalId, (Throwable)e);
        }
    }

    public UserWithAttributes findUserWithAttributesByKey(Application application, String key) throws UserNotFoundException {
        Pair<Long, String> directoryIdAndExternalId = ApplicationServiceGeneric.directoryIdAndExternalIdFromKey(key);
        long directoryId = (Long)directoryIdAndExternalId.left();
        String externalId = (String)directoryIdAndExternalId.right();
        boolean isDirectoryActive = Iterables.any(this.getActiveDirectories(application), (Predicate)Directories.directoryWithIdPredicate((long)directoryId));
        if (!isDirectoryActive) {
            logger.debug("Cannot look up in directory {} because it is not mapped to the application", (Object)directoryId);
            throw new UserNotFoundException(externalId);
        }
        try {
            UserWithAttributes user = this.directoryManager.findUserWithAttributesByExternalId(directoryId, externalId);
            return this.checkCanonicalUser(user, application);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (OperationFailedException e) {
            throw new UserNotFoundException(externalId, (Throwable)e);
        }
    }

    private <T extends com.atlassian.crowd.model.user.User> T checkCanonicalUser(T user, Application application) throws UserNotFoundException {
        if (!this.isCanonical(application, user)) {
            logger.debug("Skipping user '{}' from directory {} because it is shadowed by another user", (Object)user.getName(), (Object)user.getDirectoryId());
            throw new UserNotFoundException(user.getExternalId());
        }
        if (!this.simpleAccessFilter(application).hasAccess(user.getDirectoryId(), Entity.USER, user.getName())) {
            throw new UserNotFoundException(user.getExternalId());
        }
        return user;
    }

    private com.atlassian.crowd.model.user.User fastFailingFindUser(Application application, String name) throws UserNotFoundException, OperationFailedException {
        return this.finder(application).fastFailingFindUserByName(name);
    }

    private Group fastFailingFindGroup(Application application, String name) throws GroupNotFoundException, OperationFailedException {
        return this.finder(application).fastFailingFindGroupByName(name);
    }

    public UserWithAttributes findUserWithAttributesByName(Application application, String name) throws UserNotFoundException {
        return this.finder(application).findUserWithAttributesByName(name);
    }

    public com.atlassian.crowd.model.user.User addUser(Application application, UserTemplate user, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        return this.addUser(application, UserTemplateWithAttributes.toUserWithNoAttributes((com.atlassian.crowd.model.user.User)user), credential);
    }

    public UserWithAttributes addUser(Application application, UserTemplateWithAttributes user, PasswordCredential credential) throws InvalidUserException, OperationFailedException, InvalidCredentialException, ApplicationPermissionException {
        if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)user.getName())) {
            throw new InvalidUserException((User)user, "User name may not contain leading or trailing whitespace");
        }
        if (application.isFilteringUsersWithAccessEnabled()) {
            throw new OperationFailedException("Adding users is not supported while filtering user with access is enabled");
        }
        logger.debug("Adding user '{}' for application '{}'", (Object)user.getName(), (Object)application.getName());
        try {
            this.fastFailingFindUser(application, user.getName());
            throw new InvalidUserException((User)user, "User already exists");
        }
        catch (UserNotFoundException userNotFoundException) {
            Directory directory = this.findFirstDirectoryWithCreateUserPermission(application);
            if (directory == null) {
                throw new ApplicationPermissionException("Application '" + application.getName() + "' has no directories that allow adding of users.");
            }
            try {
                user.setDirectoryId(directory.getId().longValue());
                UserWithAttributes newUser = this.directoryManager.addUser(directory.getId().longValue(), user, credential);
                logger.debug("User '{}' was added to directory '{}'.", (Object)user.getName(), (Object)directory.getName());
                return newUser;
            }
            catch (DirectoryPermissionException dpe) {
                throw new ApplicationPermissionException("Permission Exception when trying to add user '" + user.getName() + "' to directory '" + directory.getName() + "'. " + dpe.getMessage(), (Throwable)dpe);
            }
            catch (DirectoryNotFoundException de) {
                throw new OperationFailedException("Directory not found when trying to add user '" + user.getName() + "' to directory '" + directory.getName() + "'.", (Throwable)de);
            }
            catch (UserAlreadyExistsException e) {
                throw new InvalidUserException((User)user, "User " + user.getName() + " already exists.", (Throwable)e);
            }
        }
    }

    public com.atlassian.crowd.model.user.User updateUser(Application application, UserTemplate user) throws InvalidUserException, OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        logger.debug("Updating user '{}' for application '{}'", (Object)user.getName(), (Object)application.getName());
        com.atlassian.crowd.model.user.User existingUser = this.fastFailingFindUser(application, user.getName());
        if (StringUtils.isBlank((CharSequence)user.getExternalId())) {
            user.setExternalId(existingUser.getExternalId());
        }
        if (user.getDirectoryId() <= 0L) {
            user.setDirectoryId(existingUser.getDirectoryId());
        } else if (user.getDirectoryId() != existingUser.getDirectoryId()) {
            throw new InvalidUserException((User)user, "Attempted to update user '" + user.getName() + "' with invalid directory ID " + user.getDirectoryId() + ", we expected ID " + existingUser.getDirectoryId() + ".");
        }
        if (this.isDifferentEmail(user, existingUser) && !this.isAllowedToChangeEmail(application)) {
            throw new ApplicationPermissionException("External applications are not allowed to change user emails");
        }
        Directory directory = this.findDirectoryById(existingUser.getDirectoryId());
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_USER)) {
            throw new ApplicationPermissionException("Cannot update user '" + user.getName() + "' because directory '" + directory.getName() + "' does not allow updates.");
        }
        try {
            return this.directoryManager.updateUser(directory.getId().longValue(), user);
        }
        catch (DirectoryPermissionException dpe) {
            throw new ApplicationPermissionException("Permission Exception when trying to update user '" + user.getName() + "' in directory '" + directory.getName() + "'.", (Throwable)dpe);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    private boolean isAllowedToChangeEmail(Application application) {
        return this.isRunningInEmbeddedCrowd() || this.isCrowdConsoleApp(application) || (Boolean)SystemProperties.EMAIL_CHANGE_BY_EXTERNAL_APPS_ENABLED.getValue() != false;
    }

    private boolean isCrowdConsoleApp(Application application) {
        return application.getType().equals((Object)ApplicationType.CROWD);
    }

    private boolean isRunningInEmbeddedCrowd() {
        return this.applicationFactory.isEmbeddedCrowd();
    }

    private boolean isDifferentEmail(UserTemplate updatedUser, com.atlassian.crowd.model.user.User existingUser) {
        return !IdentifierUtils.equalsInLowerCase((String)updatedUser.getEmailAddress(), (String)existingUser.getEmailAddress());
    }

    public com.atlassian.crowd.model.user.User renameUser(Application application, String oldUserName, String newUsername) throws UserNotFoundException, OperationFailedException, ApplicationPermissionException, InvalidUserException {
        logger.debug("Renaming user '{}' to '{}' for application '{}'", new Object[]{oldUserName, newUsername, application.getName()});
        com.atlassian.crowd.model.user.User existingUser = this.fastFailingFindUser(application, oldUserName);
        Directory directory = this.findDirectoryById(existingUser.getDirectoryId());
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_USER)) {
            throw new ApplicationPermissionException("Cannot rename user '" + oldUserName + "' because directory '" + directory.getName() + "' does not allow updates.");
        }
        try {
            return this.directoryManager.renameUser(directory.getId().longValue(), oldUserName, newUsername);
        }
        catch (DirectoryPermissionException dpe) {
            throw new ApplicationPermissionException("Permission Exception when trying to rename user '" + oldUserName + "' in directory '" + directory.getName() + "'.", (Throwable)dpe);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (UserAlreadyExistsException e) {
            throw new InvalidUserException((User)existingUser, "User " + newUsername + " already exists.");
        }
    }

    public void updateUserCredential(Application application, String username, PasswordCredential credential) throws OperationFailedException, InvalidCredentialException, ApplicationPermissionException, UserNotFoundException {
        com.atlassian.crowd.model.user.User user = this.fastFailingFindUser(application, username);
        Directory directory = this.findDirectoryById(user.getDirectoryId());
        if (this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_USER)) {
            try {
                this.directoryManager.updateUserCredential(user.getDirectoryId(), username, credential);
            }
            catch (DirectoryPermissionException e) {
                throw new ApplicationPermissionException((Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
            }
        } else {
            throw new ApplicationPermissionException("Not allowed to update user '" + user.getName() + "' in directory '" + directory.getName() + "'.");
        }
    }

    public void storeUserAttributes(Application application, String username, Map<String, Set<String>> attributes) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        logger.debug("Storing user attributes for user '{}' and application '{}'", (Object)username, (Object)application.getName());
        com.atlassian.crowd.model.user.User user = this.fastFailingFindUser(application, username);
        Directory directory = this.findDirectoryById(user.getDirectoryId());
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_USER_ATTRIBUTE)) {
            throw new ApplicationPermissionException("Not allowed to update user attributes '" + user.getName() + "' in directory '" + directory.getName() + "'.");
        }
        try {
            this.directoryManager.storeUserAttributes(directory.getId().longValue(), username, attributes);
        }
        catch (DirectoryPermissionException ex) {
            throw new ApplicationPermissionException((Throwable)ex);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public void removeUserAttributes(Application application, String username, String attributeName) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        logger.debug("Removing user attributes for user '{}' and application '{}'", (Object)username, (Object)application.getName());
        com.atlassian.crowd.model.user.User user = this.fastFailingFindUser(application, username);
        Directory directory = this.findDirectoryById(user.getDirectoryId());
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_USER_ATTRIBUTE)) {
            throw new ApplicationPermissionException("Not allowed to update user attributes '" + user.getName() + "' in directory '" + directory.getName() + "'.");
        }
        try {
            this.directoryManager.removeUserAttributes(directory.getId().longValue(), username, attributeName);
        }
        catch (DirectoryPermissionException ex) {
            throw new ApplicationPermissionException((Throwable)ex);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public void removeUser(Application application, String username) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException {
        com.atlassian.crowd.model.user.User user = this.fastFailingFindUser(application, username);
        Directory directory = this.findDirectoryById(user.getDirectoryId());
        if (!this.permissionManager.hasPermission(application, directory, OperationType.DELETE_USER)) {
            throw new ApplicationPermissionException("Not allowed to delete user '" + user.getName() + "' from directory '" + directory.getName() + "'.");
        }
        try {
            this.directoryManager.removeUser(directory.getId().longValue(), username);
        }
        catch (DirectoryPermissionException ex) {
            throw new ApplicationPermissionException((Throwable)ex);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public <T> List<T> searchUsers(Application application, EntityQuery<T> query) {
        return this.getUserSearchStrategyOrFail(application).searchUsers(query);
    }

    public Group findGroupByName(Application application, String name) throws GroupNotFoundException {
        return this.finder(application).findGroupByName(name);
    }

    public GroupWithAttributes findGroupWithAttributesByName(Application application, String name) throws GroupNotFoundException {
        return this.finder(application).findGroupWithAttributesByName(name);
    }

    private CanonicalEntityByNameFinder finder(Application application) {
        return new CanonicalEntityByNameFinder(this.directoryManager, this.getActiveDirectories(application), this.simpleAccessFilter(application));
    }

    public Group addGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException {
        if (IdentifierUtils.hasLeadingOrTrailingWhitespace((String)group.getName())) {
            throw new InvalidGroupException((Group)group, "Group name may not contain leading or trailing whitespace");
        }
        if (application.isFilteringGroupsWithAccessEnabled()) {
            throw new OperationFailedException("Adding groups is not supported while filtering groups with access is enabled");
        }
        logger.debug("Adding group '{}' for application '{}'", (Object)group.getName(), (Object)application.getName());
        try {
            this.fastFailingFindGroup(application, group.getName());
            throw new InvalidGroupException((Group)group, "Group already exists");
        }
        catch (GroupNotFoundException e) {
            OperationType operationType = this.getCreateOperationType((Group)group);
            for (Directory directory : this.getActiveDirectories(application)) {
                if (!this.permissionManager.hasPermission(application, directory, operationType)) continue;
                try {
                    group.setDirectoryId(directory.getId().longValue());
                    this.directoryManager.addGroup(directory.getId().longValue(), group);
                }
                catch (DirectoryPermissionException dpe) {
                    logger.info("Could not add group '{}' to directory '{}'", (Object)group.getName(), (Object)directory.getName());
                    logger.info(dpe.getMessage());
                }
                catch (DirectoryNotFoundException onfe) {
                    logger.error(onfe.getMessage(), (Throwable)onfe);
                }
            }
            try {
                return this.fastFailingFindGroup(application, group.getName());
            }
            catch (GroupNotFoundException e2) {
                throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow adding of groups");
            }
        }
    }

    public Group updateGroup(Application application, GroupTemplate group) throws InvalidGroupException, OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        logger.debug("Updating group '{}' for application '{}'", (Object)group.getName(), (Object)application.getName());
        Group groupToUpdate = this.fastFailingFindGroup(application, group.getName());
        OperationType operationType = this.getUpdateOperationType(groupToUpdate);
        boolean atleastOneDirectoryHasPermission = false;
        for (Directory directory : this.getActiveDirectories(application)) {
            if (!this.permissionManager.hasPermission(application, directory, operationType)) continue;
            try {
                group.setDirectoryId(directory.getId().longValue());
                this.directoryManager.updateGroup(directory.getId().longValue(), group);
                atleastOneDirectoryHasPermission = true;
            }
            catch (DirectoryPermissionException dpe) {
                logger.info("Could not update group '{}' to directory '{}'", (Object)group.getName(), (Object)directory.getName());
                logger.info(dpe.getMessage());
            }
            catch (GroupNotFoundException dpe) {
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
            catch (ReadOnlyGroupException e) {
                logger.info("Could not update group '{}' to directory '{}' because the group is read-only.", new Object[]{group.getName(), directory.getName(), e});
            }
        }
        if (!atleastOneDirectoryHasPermission) {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group modifications");
        }
        return this.fastFailingFindGroup(application, group.getName());
    }

    public void storeGroupAttributes(Application application, String groupname, Map<String, Set<String>> attributes) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        logger.debug("Storing group attributes for group '{}' and application '{}'", (Object)groupname, (Object)application.getName());
        Group groupToUpdate = this.fastFailingFindGroup(application, groupname);
        OperationType operationType = this.getUpdateAttributeOperationType(groupToUpdate);
        boolean atleastOneDirectoryHasPermission = false;
        for (Directory directory : this.getActiveDirectories(application)) {
            if (!this.permissionManager.hasPermission(application, directory, operationType)) continue;
            try {
                this.directoryManager.storeGroupAttributes(directory.getId().longValue(), groupname, attributes);
                atleastOneDirectoryHasPermission = true;
            }
            catch (DirectoryPermissionException dpe) {
                logger.info("Could not update group '{}' to directory '{}'", (Object)groupname, (Object)directory.getName());
                logger.info(dpe.getMessage());
            }
            catch (GroupNotFoundException dpe) {
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
        }
        if (!atleastOneDirectoryHasPermission) {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group attribute modifications");
        }
    }

    public void removeGroupAttributes(Application application, String groupname, String attributeName) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        logger.debug("Removing group attributes for group '{}' and application '{}'", (Object)groupname, (Object)application.getName());
        Group groupToUpdate = this.fastFailingFindGroup(application, groupname);
        boolean atleastOneDirectoryHasPermission = false;
        OperationType operationType = this.getUpdateAttributeOperationType(groupToUpdate);
        for (Directory directory : this.getActiveDirectories(application)) {
            if (!this.permissionManager.hasPermission(application, directory, operationType)) continue;
            try {
                this.directoryManager.removeGroupAttributes(directory.getId().longValue(), groupname, attributeName);
                atleastOneDirectoryHasPermission = true;
            }
            catch (DirectoryPermissionException dpe) {
                logger.info("Could not update group '{}' to directory '{}'", (Object)groupname, (Object)directory.getName());
                logger.info(dpe.getMessage());
            }
            catch (GroupNotFoundException dpe) {
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
        }
        if (!atleastOneDirectoryHasPermission) {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group attribute modifications");
        }
    }

    public void removeGroup(Application application, String groupname) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException {
        Group groupToRemove = this.fastFailingFindGroup(application, groupname);
        boolean permissibleByAnyDirectory = false;
        OperationType operationType = this.getDeleteOperationType(groupToRemove);
        for (Directory directory : this.getActiveDirectories(application)) {
            if (!this.permissionManager.hasPermission(application, directory, operationType)) continue;
            try {
                this.directoryManager.removeGroup(directory.getId().longValue(), groupname);
                permissibleByAnyDirectory = true;
            }
            catch (DirectoryPermissionException e) {
                logger.info("Could not remove group '{}' from directory '{}'", (Object)groupname, (Object)directory.getName());
            }
            catch (GroupNotFoundException e) {
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
            catch (ReadOnlyGroupException e) {
                logger.info("Could not update group '{}' to directory '{}' because the group is read-only.", new Object[]{groupname, directory.getName(), e});
            }
        }
        if (!permissibleByAnyDirectory) {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group removal");
        }
    }

    public <T> List<T> searchGroups(Application application, EntityQuery<T> query) {
        return this.getGroupSearchStrategyOrFail(application).searchGroups(query);
    }

    public void addUserToGroup(Application application, String username, String groupName) throws OperationFailedException, ApplicationPermissionException, UserNotFoundException, GroupNotFoundException, MembershipAlreadyExistsException {
        Directory directory = application.isMembershipAggregationEnabled() ? this.findDirectoryToAddUserToGroupAggregating(application, username, groupName) : this.findDirectoryToAddUserToGroupNonAggregating(application, username, groupName);
        this.addUserToGroup(username, groupName, directory);
    }

    private void addUserToGroup(String username, String groupName, Directory directory) throws UserNotFoundException, GroupNotFoundException, OperationFailedException, MembershipAlreadyExistsException, ApplicationPermissionException {
        try {
            this.directoryManager.addUserToGroup(directory.getId().longValue(), username, groupName);
        }
        catch (DirectoryPermissionException e) {
            throw new ApplicationPermissionException("Permission Exception when trying to update group '" + groupName + "' in directory '" + directory.getName() + "'.", (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (ReadOnlyGroupException e) {
            throw new ApplicationPermissionException(String.format("Could not add user %s to group %s in directory %s because the directory or group is read-only.", username, groupName, directory.getName()));
        }
    }

    private Directory findDirectoryToAddUserToGroupNonAggregating(Application application, String username, String groupName) throws UserNotFoundException, OperationFailedException, GroupNotFoundException, ApplicationPermissionException {
        long userDirectoryId = this.fastFailingFindUser(application, username).getDirectoryId();
        try {
            try {
                this.directoryManager.findGroupByName(userDirectoryId, groupName);
            }
            catch (GroupNotFoundException e) {
                Group group = this.fastFailingFindGroup(application, groupName);
                try {
                    Directory directory = this.findDirectoryById(userDirectoryId);
                    this.checkUpdateAndCreatePermissionsForGroup(application, directory, groupName);
                    this.directoryManager.addGroup(userDirectoryId, new GroupTemplate(group).withDirectoryId(userDirectoryId));
                }
                catch (InvalidGroupException e1) {
                    throw new GroupNotFoundException(String.format("Unable to create group %s in directory %d in order to add membership of user %s (group %s found in directory %d)", group.getName(), userDirectoryId, username, group.getName(), userDirectoryId), (Throwable)e1);
                }
                catch (DirectoryPermissionException e1) {
                    throw new ApplicationPermissionException((Throwable)e1);
                }
            }
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        Directory directory = this.findDirectoryById(userDirectoryId);
        this.checkUpdatePermissionsForGroup(application, directory, groupName);
        return directory;
    }

    private void checkUpdatePermissionsForGroup(Application application, Directory directory, String groupName) throws ApplicationPermissionException {
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_GROUP)) {
            logger.info("The application {} could not update the group {} in the directory {} due to lack of permissions.", new Object[]{application.getName(), groupName, directory.getName()});
            throw new ApplicationPermissionException(String.format("Could not add user to group '%s' to the directory '%s' in application '%s' because application does not allow updates.", groupName, directory.getName(), application.getName()));
        }
    }

    private void checkUpdateAndCreatePermissionsForGroup(Application application, Directory directory, String groupName) throws ApplicationPermissionException {
        if (!this.permissionManager.hasPermission(application, directory, OperationType.CREATE_GROUP)) {
            logger.info("The application {} could not add the group {} to the directory {} due to lack of permissions.", new Object[]{application.getName(), groupName, directory.getName()});
            throw new ApplicationPermissionException(String.format("Could not add group '%s' to the directory '%s' in application '%s' because the application does not allow group creation.", groupName, directory.getName(), application.getName()));
        }
        if (!this.permissionManager.hasPermission(application, directory, OperationType.UPDATE_GROUP)) {
            logger.info("The application {} could not update the group {} in the directory {} due to lack of permissions.", new Object[]{application.getName(), groupName, directory.getName()});
            throw new ApplicationPermissionException(String.format("Could not add user to group '%s' to the directory '%s' in application '%s' because application does not allow updates.", groupName, directory.getName(), application.getName()));
        }
    }

    private Directory findDirectoryToAddUserToGroupAggregating(Application application, String username, String groupName) throws UserNotFoundException, MembershipAlreadyExistsException, GroupNotFoundException, OperationFailedException, ApplicationPermissionException {
        Directory directory;
        ImmutableList directoriesWithUser = ImmutableList.copyOf((Iterable)Iterables.filter(this.getActiveDirectories(application), this.containsUser(username)));
        if (directoriesWithUser.isEmpty()) {
            throw new UserNotFoundException(username);
        }
        ImmutableList directoriesWithUserAndGroup = ImmutableList.copyOf((Iterable)Iterables.filter((Iterable)directoriesWithUser, this.containsGroup(groupName)));
        if (Iterables.any((Iterable)directoriesWithUserAndGroup, this.containsUserDirectMembershipInGroup(username, groupName))) {
            throw new MembershipAlreadyExistsException(username, groupName);
        }
        Directory writeableDirectoryWithUserAndGroup = this.directoryWithPermissions(application, (Collection<Directory>)directoriesWithUserAndGroup, UPDATE_GROUP_PERMISSION);
        if (writeableDirectoryWithUserAndGroup != null) {
            directory = writeableDirectoryWithUserAndGroup;
        } else {
            directory = this.directoryWithPermissions(application, (Collection<Directory>)directoriesWithUser, CREATE_AND_UPDATE_GROUP_PERMISSIONS);
            if (directory != null) {
                Group group = this.fastFailingFindGroup(application, groupName);
                try {
                    this.directoryManager.addGroup(directory.getId().longValue(), new GroupTemplate(group).withDirectoryId(directory.getId().longValue()));
                }
                catch (InvalidGroupException e) {
                    throw new OperationFailedException((Throwable)e);
                }
                catch (DirectoryNotFoundException e) {
                    throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
                }
                catch (DirectoryPermissionException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
            }
        }
        if (directory == null) {
            throw new ApplicationPermissionException("Did not have update groups permission in any of the directories " + directoriesWithUserAndGroup);
        }
        return directory;
    }

    @Nullable
    private Directory directoryWithPermissionsAnd(Application application, Collection<Directory> directories, Set<OperationType> permissions, Predicate<Directory> andPredicate) {
        return (Directory)Iterables.find(directories, (Predicate)Predicates.and(this.hasPermissions(application, permissions), andPredicate), null);
    }

    @Nullable
    private Directory directoryWithPermissions(Application application, Collection<Directory> directories, Set<OperationType> permissions) {
        return this.directoryWithPermissionsAnd(application, directories, permissions, (Predicate<Directory>)Predicates.alwaysTrue());
    }

    private Predicate<Directory> hasPermissions(Application application, Set<OperationType> operationTypes) {
        return directory -> Iterables.all((Iterable)operationTypes, operationType -> this.hasPermissions(application, (Directory)directory, (OperationType)operationType));
    }

    private boolean hasPermissions(Application application, Directory directory, OperationType operationType) {
        return this.permissionManager.hasPermission(application, directory, operationType);
    }

    private Predicate<Directory> containsUserDirectMembershipInGroup(final String username, final String groupName) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException {
                return ApplicationServiceGeneric.this.directoryManager.isUserDirectGroupMember(directory.getId().longValue(), username, groupName);
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if user %s is a member of group %s in directory %d", username, groupName, directory.getId());
            }
        };
    }

    private Predicate<Directory> containsUserNestedMembershipInGroup(final String username, final String groupName) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException {
                return ApplicationServiceGeneric.this.directoryManager.isUserNestedGroupMember(directory.getId().longValue(), username, groupName);
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if user %s is a nested member of group %s in directory %d", username, groupName, directory.getId());
            }
        };
    }

    private Predicate<Directory> containsGroupDirectMembershipInGroup(final String childName, final String parentName) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException {
                return ApplicationServiceGeneric.this.directoryManager.isGroupDirectGroupMember(directory.getId().longValue(), childName, parentName);
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if child group %s is a member of parent group %s in directory %d", childName, parentName, directory.getId());
            }
        };
    }

    private Predicate<Directory> containsGroupNestedMembershipInGroup(final String childName, final String parentName) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException {
                return ApplicationServiceGeneric.this.directoryManager.isGroupNestedGroupMember(directory.getId().longValue(), childName, parentName);
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if child group %s is a nested member of parent group %s in directory %d", childName, parentName, directory.getId());
            }
        };
    }

    private Predicate<Directory> containsGroup(final String groupName) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException, GroupNotFoundException {
                ApplicationServiceGeneric.this.directoryManager.findGroupByName(directory.getId().longValue(), groupName);
                return true;
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if group %s exists in directory %d", groupName, directory.getId());
            }
        };
    }

    private Predicate<Directory> containsUser(final String username) {
        return new DirectoryPredicate(){

            @Override
            protected boolean fallibleCheckForEntity(Directory directory) throws DirectoryNotFoundException, OperationFailedException, UserNotFoundException {
                ApplicationServiceGeneric.this.directoryManager.findUserByName(directory.getId().longValue(), username);
                return true;
            }

            @Override
            protected String errorMessage(Directory directory) {
                return String.format("Failed to determine if user %s exists in directory %d", username, directory.getId());
            }
        };
    }

    private Directory findDirectoryById(long directoryId) throws ConcurrentModificationException {
        try {
            return this.directoryManager.findDirectoryById(directoryId);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public void addGroupToGroup(Application application, String childGroupName, String parentGroupName) throws OperationFailedException, ApplicationPermissionException, GroupNotFoundException, InvalidMembershipException, MembershipAlreadyExistsException {
        if (IdentifierUtils.equalsInLowerCase((String)childGroupName, (String)parentGroupName)) {
            throw new InvalidMembershipException("Cannot add a group to itself.");
        }
        if (this.isGroupNestedGroupMember(application, parentGroupName, childGroupName)) {
            throw new InvalidMembershipException("Cannot add child group '" + childGroupName + "' to parent group '" + parentGroupName + "' - this would cause a circular dependency.");
        }
        DirectoryAndGroup pair = application.isMembershipAggregationEnabled() ? this.findDirectoryAndGroupForAddGroupToGroupAggregating(application, childGroupName, parentGroupName) : this.findDirectoryAndGroupForAddGroupToGroupNonAggregating(application, childGroupName, parentGroupName);
        Directory directory = pair.directory;
        Group createdParentGroup = pair.group;
        try {
            Group parentGroup;
            Group childGroup = this.directoryManager.findGroupByName(directory.getId().longValue(), childGroupName);
            Group group = parentGroup = createdParentGroup != null ? createdParentGroup : this.directoryManager.findGroupByName(directory.getId().longValue(), parentGroupName);
            if (childGroup.getType() != parentGroup.getType()) {
                throw new InvalidMembershipException("Cannot add group of type " + childGroup.getType().name() + " to group of type " + parentGroup.getType().name());
            }
            this.directoryManager.addGroupToGroup(directory.getId().longValue(), childGroupName, parentGroupName);
        }
        catch (DirectoryPermissionException e) {
            throw new ApplicationPermissionException("Permission Exception when trying to update group '" + parentGroupName + "' in directory '" + directory.getName() + "'.", (Throwable)e);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (ReadOnlyGroupException e) {
            throw new ApplicationPermissionException(String.format("Could not add child group %s to parent group %s in directory %s because the directory or group is read-only.", childGroupName, parentGroupName, directory.getName()));
        }
        catch (NestedGroupsNotSupportedException e) {
            throw new InvalidMembershipException((Throwable)e);
        }
    }

    private DirectoryAndGroup findDirectoryAndGroupForAddGroupToGroupNonAggregating(Application application, String childGroupName, String parentGroupName) throws GroupNotFoundException, OperationFailedException, InvalidMembershipException, ApplicationPermissionException {
        Group createdParentGroup = null;
        long childDirectoryId = this.fastFailingFindGroup(application, childGroupName).getDirectoryId();
        Directory directory = this.findDirectoryById(childDirectoryId);
        try {
            if (!this.directoryManager.supportsNestedGroups(childDirectoryId)) {
                throw new InvalidMembershipException("Nested directories are not supported by directory " + directory.getName());
            }
            try {
                this.directoryManager.findGroupByName(childDirectoryId, parentGroupName);
            }
            catch (GroupNotFoundException e) {
                Group parentGroup = this.fastFailingFindGroup(application, parentGroupName);
                createdParentGroup = this.directoryManager.addGroup(directory.getId().longValue(), new GroupTemplate(parentGroup).withDirectoryId(directory.getId().longValue()));
            }
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        catch (InvalidGroupException e) {
            throw new OperationFailedException((Throwable)e);
        }
        catch (DirectoryPermissionException e) {
            throw new ApplicationPermissionException(String.format("Parent group %s could not be added to directory %d where the canonical instance of %s was found.", parentGroupName, childDirectoryId, childGroupName));
        }
        if (!this.hasPermissions(application, UPDATE_GROUP_PERMISSION).apply((Object)directory)) {
            throw new ApplicationPermissionException("Cannot update group '" + parentGroupName + "' because directory '" + directory.getName() + "' does not allow updates.");
        }
        return new DirectoryAndGroup(directory, createdParentGroup);
    }

    private DirectoryAndGroup findDirectoryAndGroupForAddGroupToGroupAggregating(Application application, String childGroupName, String parentGroupName) throws GroupNotFoundException, MembershipAlreadyExistsException, OperationFailedException, ApplicationPermissionException {
        Group createdParentGroup;
        Directory directory;
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        ImmutableList directoriesWithChild = ImmutableList.copyOf((Iterable)Iterables.filter(activeDirectories, this.containsGroup(childGroupName)));
        if (directoriesWithChild.isEmpty()) {
            throw new GroupNotFoundException(childGroupName);
        }
        ImmutableList directoriesWithChildAndParent = ImmutableList.copyOf((Iterable)Iterables.filter((Iterable)directoriesWithChild, this.containsGroup(parentGroupName)));
        if (Iterables.any((Iterable)directoriesWithChildAndParent, this.containsGroupDirectMembershipInGroup(childGroupName, parentGroupName))) {
            throw new MembershipAlreadyExistsException(childGroupName, parentGroupName);
        }
        Directory writeableDirectoryWithChildAndParent = this.directoryWithPermissionsAnd(application, (Collection<Directory>)directoriesWithChildAndParent, UPDATE_GROUP_PERMISSION, this.supportsNestedGroups);
        if (writeableDirectoryWithChildAndParent != null) {
            directory = writeableDirectoryWithChildAndParent;
            createdParentGroup = null;
        } else {
            directory = this.directoryWithPermissionsAnd(application, (Collection<Directory>)directoriesWithChild, CREATE_AND_UPDATE_GROUP_PERMISSIONS, this.supportsNestedGroups);
            if (directory != null) {
                Group parentGroup = this.fastFailingFindGroup(application, parentGroupName);
                try {
                    createdParentGroup = this.directoryManager.addGroup(directory.getId().longValue(), new GroupTemplate(parentGroup).withDirectoryId(directory.getId().longValue()));
                }
                catch (InvalidGroupException e) {
                    throw new OperationFailedException((Throwable)e);
                }
                catch (DirectoryNotFoundException e) {
                    throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
                }
                catch (DirectoryPermissionException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
            } else {
                createdParentGroup = null;
            }
        }
        if (directory == null) {
            throw new ApplicationPermissionException(String.format("Could not find a directory in which it is possible to add %s to %s", childGroupName, parentGroupName));
        }
        return new DirectoryAndGroup(directory, createdParentGroup);
    }

    public void removeUserFromGroup(Application application, String username, String groupName) throws OperationFailedException, ApplicationPermissionException, MembershipNotFoundException, UserNotFoundException, GroupNotFoundException {
        if (application.isMembershipAggregationEnabled()) {
            this.removeUserFromGroupAggregating(application, username, groupName);
        } else {
            this.removeUserFromGroupNonAggregating(application, username, groupName);
        }
    }

    private void removeUserFromGroupNonAggregating(Application application, String username, String groupName) throws UserNotFoundException, OperationFailedException, GroupNotFoundException, MembershipNotFoundException, ApplicationPermissionException {
        com.atlassian.crowd.model.user.User user = this.fastFailingFindUser(application, username);
        try {
            this.directoryManager.findGroupByName(user.getDirectoryId(), groupName);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        if (!this.isUserDirectGroupMember(application, username, groupName)) {
            throw new MembershipNotFoundException(username, groupName);
        }
        Directory directory = this.findDirectoryById(user.getDirectoryId());
        if (this.hasPermissions(application, UPDATE_GROUP_PERMISSION).apply((Object)directory)) {
            try {
                this.directoryManager.removeUserFromGroup(directory.getId().longValue(), username, groupName);
            }
            catch (DirectoryPermissionException e) {
                throw new ApplicationPermissionException((Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
            }
            catch (ReadOnlyGroupException e) {
                throw new ApplicationPermissionException(String.format("Could not remove user %s from group %s in directory %s because the directory or group is read-only.", username, groupName, directory.getName()));
            }
        } else {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group modifications");
        }
    }

    private void removeUserFromGroupAggregating(Application application, String username, String groupName) throws UserNotFoundException, GroupNotFoundException, MembershipNotFoundException, OperationFailedException, ApplicationPermissionException {
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        ImmutableList directoriesWithMembership = ImmutableList.copyOf((Iterable)Iterables.filter(activeDirectories, this.containsUserDirectMembershipInGroup(username, groupName)));
        if (directoriesWithMembership.isEmpty()) {
            if (!Iterables.any(activeDirectories, this.containsUser(username))) {
                throw new UserNotFoundException(username);
            }
            if (!Iterables.any(activeDirectories, this.containsGroup(groupName))) {
                throw new GroupNotFoundException(groupName);
            }
            throw new MembershipNotFoundException(username, groupName);
        }
        if (Iterables.all((Iterable)directoriesWithMembership, this.hasPermissions(application, UPDATE_GROUP_PERMISSION))) {
            for (Directory directory : directoriesWithMembership) {
                try {
                    this.directoryManager.removeUserFromGroup(directory.getId().longValue(), username, groupName);
                }
                catch (DirectoryPermissionException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
                catch (DirectoryNotFoundException e) {
                }
                catch (ReadOnlyGroupException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
            }
        } else {
            throw new ApplicationPermissionException(String.format("At least one directory containing %s as a member of %s does not have write permission", username, groupName));
        }
    }

    public void removeGroupFromGroup(Application application, String childGroupName, String parentGroupName) throws OperationFailedException, ApplicationPermissionException, MembershipNotFoundException, GroupNotFoundException {
        if (application.isMembershipAggregationEnabled()) {
            this.removeGroupFromGroupAggregating(application, childGroupName, parentGroupName);
        } else {
            this.removeGroupFromGroupNonAggregating(application, childGroupName, parentGroupName);
        }
    }

    private void removeGroupFromGroupNonAggregating(Application application, String childGroupName, String parentGroupName) throws GroupNotFoundException, OperationFailedException, MembershipNotFoundException, ApplicationPermissionException {
        Group childGroup = this.fastFailingFindGroup(application, childGroupName);
        try {
            this.directoryManager.findGroupByName(childGroup.getDirectoryId(), parentGroupName);
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
        if (!this.isGroupDirectGroupMember(application, childGroupName, parentGroupName)) {
            throw new MembershipNotFoundException(childGroupName, parentGroupName);
        }
        Directory directory = this.findDirectoryById(childGroup.getDirectoryId());
        if (this.hasPermissions(application, UPDATE_GROUP_PERMISSION).apply((Object)directory)) {
            try {
                this.directoryManager.removeGroupFromGroup(directory.getId().longValue(), childGroupName, parentGroupName);
            }
            catch (DirectoryPermissionException e) {
                throw new ApplicationPermissionException((Throwable)e);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
            }
            catch (ReadOnlyGroupException e) {
                throw new ApplicationPermissionException(String.format("Could not remove child group %s from parent group %s in directory %s because the directory or group is read-only.", childGroupName, parentGroupName, directory.getName()), (Throwable)e);
            }
            catch (InvalidMembershipException e) {
                throw new OperationFailedException(String.format("Cannot remove group %s from %s because they have different types", childGroupName, parentGroupName), (Throwable)e);
            }
        } else {
            throw new ApplicationPermissionException("Application \"" + application.getName() + "\" does not allow group modifications");
        }
    }

    private void removeGroupFromGroupAggregating(Application application, String childGroupName, String parentGroupName) throws GroupNotFoundException, MembershipNotFoundException, OperationFailedException, ApplicationPermissionException {
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        ImmutableList directoriesWithMembership = ImmutableList.copyOf((Iterable)Iterables.filter(activeDirectories, this.containsGroupDirectMembershipInGroup(childGroupName, parentGroupName)));
        if (directoriesWithMembership.isEmpty()) {
            if (!Iterables.any(activeDirectories, this.containsGroup(childGroupName))) {
                throw new GroupNotFoundException(childGroupName);
            }
            if (!Iterables.any(activeDirectories, this.containsGroup(parentGroupName))) {
                throw new GroupNotFoundException(parentGroupName);
            }
            throw new MembershipNotFoundException(childGroupName, parentGroupName);
        }
        if (Iterables.all((Iterable)directoriesWithMembership, this.hasPermissions(application, UPDATE_GROUP_PERMISSION))) {
            for (Directory directory : directoriesWithMembership) {
                try {
                    this.directoryManager.removeGroupFromGroup(directory.getId().longValue(), childGroupName, parentGroupName);
                }
                catch (DirectoryPermissionException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
                catch (DirectoryNotFoundException e) {
                }
                catch (ReadOnlyGroupException e) {
                    throw new ApplicationPermissionException((Throwable)e);
                }
                catch (InvalidMembershipException e) {
                    throw new OperationFailedException(String.format("Cannot remove group %s from %s because they have different types", childGroupName, parentGroupName), (Throwable)e);
                }
            }
        } else {
            throw new ApplicationPermissionException(String.format("At least one directory containing %s as a member of %s does not have write permission", childGroupName, parentGroupName));
        }
    }

    public boolean isUserDirectGroupMember(Application application, String username, String groupName) {
        if (application.isMembershipAggregationEnabled()) {
            return Iterables.any(this.getActiveDirectories(application), this.containsUserDirectMembershipInGroup(username, groupName));
        }
        try {
            com.atlassian.crowd.model.user.User user = this.findUserByName(application, username);
            return this.directoryManager.isUserDirectGroupMember(user.getDirectoryId(), username, groupName);
        }
        catch (UserNotFoundException e) {
            return false;
        }
        catch (OperationFailedException e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
        }
    }

    public boolean isGroupDirectGroupMember(Application application, String childGroup, String parentGroup) {
        if (application.isMembershipAggregationEnabled()) {
            return Iterables.any(this.getActiveDirectories(application), this.containsGroupDirectMembershipInGroup(childGroup, parentGroup));
        }
        try {
            Group group = this.findGroupByName(application, childGroup);
            return this.directoryManager.isGroupDirectGroupMember(group.getDirectoryId(), childGroup, parentGroup);
        }
        catch (GroupNotFoundException e) {
            return false;
        }
        catch (OperationFailedException e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while determining if the group is a direct group member: " + e.getMessage());
        }
    }

    public boolean isUserNestedGroupMember(Application application, String username, String groupName) {
        if (application.isMembershipAggregationEnabled()) {
            return Iterables.any(this.getActiveDirectories(application), this.containsUserNestedMembershipInGroup(username, groupName));
        }
        try {
            com.atlassian.crowd.model.user.User user = this.findUserByName(application, username);
            return this.directoryManager.isUserNestedGroupMember(user.getDirectoryId(), username, groupName);
        }
        catch (UserNotFoundException e) {
            return false;
        }
        catch (OperationFailedException e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
        catch (DirectoryNotFoundException e) {
            throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
        }
    }

    public boolean isGroupNestedGroupMember(Application application, String childGroup, String parentGroup) {
        if (application.isMembershipAggregationEnabled()) {
            return Iterables.any(this.getActiveDirectories(application), this.containsGroupNestedMembershipInGroup(childGroup, parentGroup));
        }
        try {
            Group group = this.findGroupByName(application, childGroup);
            return this.directoryManager.isGroupNestedGroupMember(group.getDirectoryId(), childGroup, parentGroup);
        }
        catch (GroupNotFoundException e) {
            return false;
        }
        catch (OperationFailedException e) {
            logger.error(e.getMessage(), (Throwable)e);
            return false;
        }
        catch (DirectoryNotFoundException e) {
            throw new ConcurrentModificationException("Directory mapping was removed while determining if the group is a nested group member: " + e.getMessage());
        }
    }

    public <T> List<T> searchDirectGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.getMembershipSearchStrategyOrFail(application).searchDirectGroupRelationships(query);
    }

    public <T> List<T> searchNestedGroupRelationships(Application application, MembershipQuery<T> query) {
        return this.getMembershipSearchStrategyOrFail(application).searchNestedGroupRelationships(query);
    }

    <T extends DirectoryEntity> boolean isCanonical(Application application, @Nullable T entity) {
        if (entity == null) {
            return true;
        }
        Directory firstActive = (Directory)Iterables.getFirst(this.getActiveDirectories(application), null);
        if (firstActive == null) {
            return false;
        }
        if (firstActive.getId().equals(entity.getDirectoryId())) {
            return true;
        }
        try {
            com.atlassian.crowd.model.user.User canonicalEntity;
            if (entity instanceof com.atlassian.crowd.model.user.User) {
                canonicalEntity = this.findUserByName(application, entity.getName());
            } else if (entity instanceof Group) {
                canonicalEntity = this.findGroupByName(application, entity.getName());
            } else {
                throw new IllegalArgumentException("Entity must be an instance of User or Group (was " + entity.getClass().getName() + ")");
            }
            return entity.getDirectoryId() == canonicalEntity.getDirectoryId();
        }
        catch (GroupNotFoundException | UserNotFoundException e) {
            return false;
        }
    }

    public String getCurrentEventToken(Application application) throws IncrementalSynchronisationNotAvailableException {
        ImmutableList activeDirectories = ImmutableList.copyOf(this.getActiveDirectories(application));
        this.assertIncrementalSynchronisationIsAvailable((List<Directory>)activeDirectories);
        return this.eventStore.getCurrentEventToken(activeDirectories.stream().map(Directory::getId).collect(Collectors.toList()));
    }

    public Events getNewEvents(Application application, String eventToken) throws EventTokenExpiredException, OperationFailedException {
        Events events = this.eventStore.getNewEvents(eventToken, application);
        ImmutableList eventsList = ImmutableList.copyOf((Iterable)events.getEvents());
        if ((application.isFilteringGroupsWithAccessEnabled() || application.isFilteringUsersWithAccessEnabled()) && !eventsList.isEmpty()) {
            throw new EventTokenExpiredException("Incremental sync is not available when access based filtering is on.");
        }
        List<OperationEvent> applicationEvents = new EventTransformer(this.getCachedDirectoryManagerIfEnabled(), application).transformEvents((Iterable<OperationEvent>)eventsList);
        return new Events(applicationEvents, events.getNewEventToken());
    }

    @VisibleForTesting
    DirectoryManager getCachedDirectoryManagerIfEnabled() {
        return this.crowdDarkFeatureManager.isEventTransformerDirectoryManagerCacheEnabled() ? (DirectoryManager)ProxyUtil.cached(new FixedSizeLinkedHashMap((Integer)SystemProperties.EVENT_TRANSFORMER_DIRECTORY_MANAGER_CACHE_SIZE.getValue()), (Object)this.directoryManager) : this.directoryManager;
    }

    public Webhook findWebhookById(Application application, long webhookId) throws WebhookNotFoundException, ApplicationPermissionException {
        Webhook webhook = this.webhookRegistry.findById(webhookId);
        if (application.getId().equals(webhook.getApplication().getId())) {
            return webhook;
        }
        throw new ApplicationPermissionException("Application does not own Webhook");
    }

    public Webhook registerWebhook(Application application, String endpointUrl, @Nullable String token) throws InvalidWebhookEndpointException {
        ApplicationServiceGeneric.ensureWebhookEndpointUrlIsValid(endpointUrl);
        WebhookTemplate webhookTemplate = new WebhookTemplate(application, endpointUrl, token);
        return this.webhookRegistry.add((Webhook)webhookTemplate);
    }

    public void unregisterWebhook(Application application, long webhookId) throws ApplicationPermissionException, WebhookNotFoundException {
        Webhook webhook = this.webhookRegistry.findById(webhookId);
        if (!application.getId().equals(webhook.getApplication().getId())) {
            throw new ApplicationPermissionException("Application does not own Webhook");
        }
        this.webhookRegistry.remove(webhook);
    }

    public UserCapabilities getCapabilitiesForNewUsers(Application application) {
        Directory directory = this.findFirstDirectoryWithCreateUserPermission(application);
        if (directory == null) {
            return DirectoryUserCapabilities.none();
        }
        return DirectoryUserCapabilities.fromDirectory((Directory)directory);
    }

    protected List<Directory> getActiveDirectories(Application application) {
        return Applications.getActiveDirectories((Application)application);
    }

    private MembershipSearchStrategy getMembershipSearchStrategyOrFail(Application application) {
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        return this.searchStrategyFactory.createMembershipSearchStrategy(application.isMembershipAggregationEnabled(), activeDirectories, new SimpleCanonicalityChecker(this.directoryManager, activeDirectories), this.simpleAccessFilter(application));
    }

    private GroupSearchStrategy getGroupSearchStrategyOrFail(Application application) {
        return this.searchStrategyFactory.createGroupSearchStrategy(true, this.getActiveDirectories(application), this.simpleAccessFilter(application));
    }

    private UserSearchStrategy getUserSearchStrategyOrFail(Application application) {
        return this.searchStrategyFactory.createUserSearchStrategy(true, this.getActiveDirectories(application), this.simpleAccessFilter(application));
    }

    private AccessFilter simpleAccessFilter(Application application) {
        return this.accessFilterFactory.create(application, false);
    }

    private static void ensureWebhookEndpointUrlIsValid(String endpointUrl) throws InvalidWebhookEndpointException {
        URI endpointUri;
        try {
            endpointUri = new URI(endpointUrl);
        }
        catch (URISyntaxException e) {
            throw new InvalidWebhookEndpointException(endpointUrl, (Throwable)e);
        }
        if (!endpointUri.isAbsolute()) {
            throw new InvalidWebhookEndpointException(endpointUrl, "because the url is not absolute");
        }
        if (!"http".equalsIgnoreCase(endpointUri.getScheme()) && !"https".equalsIgnoreCase(endpointUri.getScheme())) {
            throw new InvalidWebhookEndpointException(endpointUrl, "because the url scheme is not http or https");
        }
    }

    private void assertIncrementalSynchronisationIsAvailable(List<Directory> activeDirectories) throws IncrementalSynchronisationNotAvailableException {
        for (Directory directory : activeDirectories) {
            if (!BooleanUtils.isFalse((Boolean)BooleanUtils.toBooleanObject((String)directory.getValue("com.atlassian.crowd.directory.sync.cache.enabled")))) continue;
            throw new IncrementalSynchronisationNotAvailableException("Directory '" + directory.getName() + "' is not cached and so cannot be incrementally synchronised");
        }
    }

    private OperationType getCreateOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.CREATE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private OperationType getUpdateOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.UPDATE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private OperationType getUpdateAttributeOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.UPDATE_GROUP_ATTRIBUTE;
            }
        }
        throw new UnsupportedOperationException();
    }

    private OperationType getDeleteOperationType(Group group) {
        switch (group.getType()) {
            case GROUP: {
                return OperationType.DELETE_GROUP;
            }
        }
        throw new UnsupportedOperationException();
    }

    private boolean isAllowedToAuthenticate(String username, long directoryId, Application application) throws OperationFailedException, DirectoryNotFoundException {
        if (!application.isActive()) {
            logger.debug("User does not have access to application '{}' as the application is inactive", (Object)application.getName());
            return false;
        }
        ApplicationDirectoryMapping directoryMapping = application.getApplicationDirectoryMapping(directoryId);
        if (directoryMapping != null && (directoryMapping.isAllowAllToAuthenticate() || this.directoryManager.isUserNestedGroupMember(directoryId, username, directoryMapping.getAuthorisedGroupNames()))) {
            return true;
        }
        logger.debug("User does not have access to application '{}' as the directory is not allow all to authenticate and the user is not a member of any of the authorised groups", (Object)application.getName());
        return false;
    }

    private static ConcurrentModificationException concurrentModificationExceptionForDirectoryIteration(DirectoryNotFoundException e) {
        ConcurrentModificationException concurrentModificationException = new ConcurrentModificationException("Directory mapping was removed while iterating through directories");
        concurrentModificationException.initCause(e);
        return concurrentModificationException;
    }

    private static ConcurrentModificationException concurrentModificationExceptionForDirectoryAccess(DirectoryNotFoundException e) {
        ConcurrentModificationException concurrentModificationException = new ConcurrentModificationException("Directory mapping was removed while accessing the directory");
        concurrentModificationException.initCause(e);
        return concurrentModificationException;
    }

    private Directory getDefiningDirectory(Application application, String username) throws OperationFailedException, UserNotFoundException {
        long dirId = this.fastFailingFindUser(application, username).getDirectoryId();
        return application.getApplicationDirectoryMapping(dirId).getDirectory();
    }

    @Nullable
    public URI getUserAvatarLink(Application application, String username, int sizeHint) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException {
        Directory d = this.getDefiningDirectory(application, username);
        AvatarReference av = this.directoryManager.getUserAvatarByName(d.getId().longValue(), username, sizeHint);
        if (av instanceof AvatarReference.UriAvatarReference) {
            return ((AvatarReference.UriAvatarReference)av).getUri();
        }
        if (av instanceof AvatarReference.BlobAvatar) {
            return this.avatarProvider.getHostedUserAvatarUrl(application.getId().longValue(), username, sizeHint);
        }
        com.atlassian.crowd.model.user.User user = this.directoryManager.findUserByName(d.getId().longValue(), username);
        return this.avatarProvider.getUserAvatar(user, sizeHint);
    }

    @Nullable
    public AvatarReference getUserAvatar(Application application, String username, int sizeHint) throws UserNotFoundException, DirectoryNotFoundException, OperationFailedException {
        Directory d = this.getDefiningDirectory(application, username);
        AvatarReference av = this.directoryManager.getUserAvatarByName(d.getId().longValue(), username, sizeHint);
        if (av != null) {
            return av;
        }
        com.atlassian.crowd.model.user.User user = this.directoryManager.findUserByName(d.getId().longValue(), username);
        URI uri = this.avatarProvider.getUserAvatar(user, sizeHint);
        if (uri != null) {
            return new AvatarReference.UriAvatarReference(uri);
        }
        return null;
    }

    public void expireAllPasswords(Application application) throws OperationFailedException {
        logger.info("Expiring all passwords for application '{}'", (Object)application.getName());
        for (Directory directory : this.getActiveDirectories(application)) {
            try {
                if (!this.directoryManager.supportsExpireAllPasswords(directory.getId().longValue())) continue;
                this.directoryManager.expireAllPasswords(directory.getId().longValue());
            }
            catch (DirectoryNotFoundException e) {
                throw new OperationFailedException((Throwable)e);
            }
        }
    }

    public com.atlassian.crowd.model.user.User userAuthenticated(Application application, String username) throws UserNotFoundException, OperationFailedException, InactiveAccountException {
        OperationFailedException initialException = null;
        List<Directory> sortedDirectories = this.authenticationOrderOptimizer.optimizeDirectoryOrderForAuthentication(application, this.getActiveDirectories(application), username);
        for (Directory directory : sortedDirectories) {
            try {
                com.atlassian.crowd.model.user.User user = this.directoryManager.userAuthenticated(directory.getId().longValue(), username);
                this.eventPublisher.publish((Object)new UserAuthenticatedEvent((Object)this, directory, application, user));
                return user;
            }
            catch (OperationFailedException e) {
                logger.debug("userAuthenticated() failed for user '{}' directory {}, continuing", new Object[]{username, directory.getId(), e});
                if (initialException != null) continue;
                initialException = e;
            }
            catch (UserNotFoundException e) {
                logger.debug("User not found during userAuthenticated() for user '{}' directory {}, continuing", new Object[]{username, directory.getId(), e});
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryIteration(e);
            }
        }
        if (initialException != null) {
            throw initialException;
        }
        throw new UserNotFoundException(username);
    }

    public ApplicationService.MembershipsIterable getMemberships(Application application) {
        return MembershipsIterableImpl.runWithClassLoader(Thread.currentThread().getContextClassLoader(), new MembershipsIterableImpl(this.directoryManager, this.searchStrategyFactory, application, this.accessFilterFactory));
    }

    public <T> PagedSearcher<T> createPagedUserSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        AccessFilter accessFilter = this.accessFilterFactory.create(application, true);
        return this.searchStrategyFactory.createUserSearchStrategy(true, activeDirectories, accessFilter).createPagedUserSearcher(query);
    }

    public <T> PagedSearcher<T> createPagedGroupSearcher(Application application, EntityQuery<T> query) throws PagingNotSupportedException {
        List<Directory> activeDirectories = this.getActiveDirectories(application);
        AccessFilter accessFilter = this.accessFilterFactory.create(application, true);
        return this.searchStrategyFactory.createGroupSearchStrategy(true, activeDirectories, accessFilter).createPagedGroupSearcher(query);
    }

    private abstract class DirectoryPredicate
    implements Predicate<Directory> {
        private DirectoryPredicate() {
        }

        public final boolean apply(Directory directory) {
            try {
                return this.fallibleCheckForEntity(directory);
            }
            catch (DirectoryNotFoundException e) {
                throw ApplicationServiceGeneric.concurrentModificationExceptionForDirectoryAccess(e);
            }
            catch (ObjectNotFoundException e) {
                return false;
            }
            catch (OperationFailedException e) {
                throw new com.atlassian.crowd.exception.runtime.OperationFailedException(this.errorMessage(directory), (Throwable)e);
            }
        }

        protected abstract boolean fallibleCheckForEntity(Directory var1) throws ObjectNotFoundException, OperationFailedException, DirectoryNotFoundException;

        protected abstract String errorMessage(Directory var1);
    }

    private static class DirectoryAndGroup {
        final Directory directory;
        final Group group;

        DirectoryAndGroup(Directory directory, Group group) {
            this.directory = directory;
            this.group = group;
        }
    }
}

