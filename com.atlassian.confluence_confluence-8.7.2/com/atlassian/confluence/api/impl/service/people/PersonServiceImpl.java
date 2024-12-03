/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.longtasks.LongTaskId
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.people.Group
 *  com.atlassian.confluence.api.model.people.PasswordChangeDetails
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.model.people.UserDetailsForCreation
 *  com.atlassian.confluence.api.model.people.UserKey
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResults
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.people.PersonService
 *  com.atlassian.confluence.api.service.people.PersonService$PersonFinder
 *  com.atlassian.confluence.api.service.people.PersonService$PersonSearcher
 *  com.atlassian.confluence.api.service.people.PersonService$Validator
 *  com.atlassian.confluence.rest.api.model.pagination.PaginationLimits
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultGroup
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.user.security.password.Credential
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.validator.routines.EmailValidator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.people;

import com.atlassian.confluence.api.impl.pagination.PagerToPageResponseHelper;
import com.atlassian.confluence.api.impl.pagination.PaginationServiceInternal;
import com.atlassian.confluence.api.impl.service.content.factory.PersonFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.impl.service.content.finder.FinderProxyFactory;
import com.atlassian.confluence.api.impl.service.longtasks.LongTaskFactory;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.longtasks.LongTaskId;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.people.PasswordChangeDetails;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.model.people.UserDetailsForCreation;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.SimpleValidationResults;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.people.PersonService;
import com.atlassian.confluence.event.events.user.AdminAddedUserEvent;
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.rest.api.model.pagination.PaginationLimits;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.confluence.user.actions.DeleteUserLongRunningTask;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultGroup;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.search.page.Pager;
import com.atlassian.user.security.password.Credential;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonServiceImpl
implements PersonService {
    private static final Logger log = LoggerFactory.getLogger(PersonServiceImpl.class);
    private final PermissionManager permissionManager;
    private final PersonFactory personFactory;
    private final UserAccessorInternal userAccessor;
    private final GroupManager groupManager;
    private final FinderProxyFactory finderProxyFactory;
    private final LongRunningTaskManager longRunningTaskManager;
    private final LongTaskFactory longTaskFactory;
    private final I18NBeanFactory i18NBeanFactory;
    private final SettingsManager settingsManager;
    private final EventPublisher eventPublisher;
    private final SpacePermissionManager spacePermissionManager;
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PaginationServiceInternal paginationService;
    private final LoginManager loginManager;

    public PersonServiceImpl(PermissionManager permissionManager, PersonFactory personFactory, UserAccessorInternal userAccessor, GroupManager groupManager, FinderProxyFactory finderProxyFactory, LongRunningTaskManager longRunningTaskManager, LongTaskFactory longTaskFactory, I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager, EventPublisher eventPublisher, SpacePermissionManager spacePermissionManager, ConfluenceUserResolver confluenceUserResolver, LoginManager loginManager, PaginationServiceInternal paginationService) {
        this.permissionManager = permissionManager;
        this.personFactory = personFactory;
        this.userAccessor = userAccessor;
        this.groupManager = groupManager;
        this.finderProxyFactory = finderProxyFactory;
        this.longRunningTaskManager = longRunningTaskManager;
        this.longTaskFactory = longTaskFactory;
        this.i18NBeanFactory = i18NBeanFactory;
        this.settingsManager = settingsManager;
        this.eventPublisher = eventPublisher;
        this.spacePermissionManager = spacePermissionManager;
        this.confluenceUserResolver = confluenceUserResolver;
        this.loginManager = loginManager;
        this.paginationService = paginationService;
    }

    public com.atlassian.confluence.api.model.people.UserKey create(UserDetailsForCreation userDetailsForCreation) {
        this.validator().validateUserCreate(userDetailsForCreation).throwIfNotSuccessful("Unable to create user");
        String password = userDetailsForCreation.getPassword();
        if (Boolean.TRUE.equals(userDetailsForCreation.isNotifyViaEmail())) {
            password = UUID.randomUUID().toString();
        }
        DefaultUser user = new DefaultUser(userDetailsForCreation.getUserName(), userDetailsForCreation.getFullName(), userDetailsForCreation.getEmail());
        try {
            ConfluenceUser newlyCreatedUser = this.userAccessor.createUser((User)user, Credential.unencrypted((String)password));
            this.userAccessor.addMembership(this.userAccessor.getNewUserDefaultGroupName(), userDetailsForCreation.getUserName());
            this.sendNotificationViaEmail(userDetailsForCreation, newlyCreatedUser);
            com.atlassian.confluence.api.model.people.UserKey userKey = new com.atlassian.confluence.api.model.people.UserKey();
            userKey.setUserKey(newlyCreatedUser.getKey().getStringValue());
            return userKey;
        }
        catch (LicenseUserLimitExceededException e) {
            SimpleValidationResults.paymentRequiredResult((String)"License check failed", (Object[])new Object[0]).throwIfNotSuccessful();
        }
        catch (RuntimeException e) {
            log.error("Failed to create user: " + user, (Throwable)e);
            SimpleValidationResults.invalidResult((String)("Failed to create the user " + userDetailsForCreation.getUserName() + ". Check your server logs for more information"), (Object[])new Object[0]).throwIfNotSuccessful();
        }
        return null;
    }

    private void sendNotificationViaEmail(UserDetailsForCreation userDetailsForCreation, User newlyCreatedUser) {
        if (Boolean.TRUE.equals(userDetailsForCreation.isNotifyViaEmail())) {
            AdminAddedUserEvent addedUserEvent = new AdminAddedUserEvent(newlyCreatedUser);
            this.eventPublisher.publish((Object)addedUserEvent);
        }
    }

    public Person getCurrentUser(Expansion ... expansions) {
        return this.personFactory.forCurrentUser(new Expansions(expansions));
    }

    public PersonService.PersonFinder find(Expansion ... expansions) {
        this.validator().validateView().throwIfNotSuccessful("User not permitted to view user profiles");
        PersonFinderImpl finder = new PersonFinderImpl(expansions);
        return this.finderProxyFactory.createProxy(finder, PersonService.PersonFinder.class);
    }

    public void disable(String username) {
        this.validator().validateDisable(username).throwIfNotSuccessful();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        this.userAccessor.deactivateUser(user);
    }

    public void enable(String username) {
        this.validator().validateEnable(username).throwIfNotSuccessful();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        this.userAccessor.reactivateUser(user);
    }

    public LongTaskSubmission delete(Person personToDelete) {
        this.validator().validateDelete(personToDelete).throwIfNotSuccessful();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        ConfluenceUser userToDelete = Objects.requireNonNull(this.userAccessor.getUserByKey(personToDelete.optionalUserKey().orElse(null)));
        DeleteUserLongRunningTask deleteUserLongRunningTask = new DeleteUserLongRunningTask(this.i18NBeanFactory.getI18NBean(), this.settingsManager, this.userAccessor, userToDelete, this.eventPublisher);
        LongRunningTaskId longRunningTaskId = this.longRunningTaskManager.startLongRunningTask(currentUser, (LongRunningTask)deleteUserLongRunningTask);
        LongTaskId taskId = longRunningTaskId.asLongTaskId();
        return this.longTaskFactory.buildSubmission(taskId);
    }

    public PersonService.PersonSearcher search() {
        this.validator().validateView().throwIfNotSuccessful("User not permitted to view user profiles");
        PersonSearcherImpl searcher = new PersonSearcherImpl();
        return this.finderProxyFactory.createProxy(searcher, PersonService.PersonSearcher.class);
    }

    public void addMembership(String username, String groupName) {
        this.validator().validateAddMembership(username, groupName).throwIfNotSuccessful();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        Group group = this.userAccessor.getGroup(groupName);
        this.userAccessor.addMembership(group, user);
    }

    public void removeMembership(String username, String groupName) {
        this.validator().validateRemoveMembership(username, groupName).throwIfNotSuccessful();
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        Group group = this.userAccessor.getGroup(groupName);
        this.userAccessor.removeMembership(group, user);
    }

    public void changeUserPassword(String userName, String newPass) {
        this.validator().validateChangePassword(userName, newPass).throwIfNotSuccessful("Unable to change user password");
        ConfluenceUser givenUser = this.userAccessor.getUserByName(userName);
        try {
            this.userAccessor.alterPassword(givenUser, newPass);
        }
        catch (EntityException | RuntimeException e) {
            log.error("Error occurred when changing current user's password", e);
            SimpleValidationResults.invalidResult((String)("Error changing password for user " + userName + ". Check your server logs for more information"), (Object[])new Object[0]).throwIfNotSuccessful();
        }
    }

    public void changeMyPassword(PasswordChangeDetails passwordChangeDetails) {
        this.validator().validateChangeMyPassword(passwordChangeDetails).throwIfNotSuccessful("Unable to change current user's password");
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        try {
            this.userAccessor.alterPassword(currentUser, passwordChangeDetails.getNewPassword());
        }
        catch (EntityException | RuntimeException e) {
            log.error("Error occurred when changing current user's password", e);
            SimpleValidationResults.invalidResult((String)("Error changing password for user " + currentUser.getName() + ". Check your server logs for more information"), (Object[])new Object[0]).throwIfNotSuccessful();
        }
    }

    public PersonService.Validator validator() {
        return new ValidatorImpl();
    }

    private Optional<ValidationResult> performMandatoryFieldChecks(User user, Group group, String username, String groupName) {
        if (null == user && null == group) {
            return Optional.of(SimpleValidationResults.notFoundResult((String)String.format("User %s and group %s not found", username, groupName), (Object[])new Object[0]));
        }
        if (null == user) {
            return Optional.of(SimpleValidationResults.notFoundResult((String)String.format("User %s not found", username), (Object[])new Object[0]));
        }
        if (null == group) {
            return Optional.of(SimpleValidationResults.notFoundResult((String)String.format("Group %s not found", groupName), (Object[])new Object[0]));
        }
        return Optional.empty();
    }

    private class ValidatorImpl
    implements PersonService.Validator {
        private static final String ENGLISH_ANONYMOUS_USER = "anonymous";

        private ValidatorImpl() {
        }

        public ValidationResult validateView() {
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, User.class)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateDisable(String username) {
            ConfluenceUser user = PersonServiceImpl.this.userAccessor.getUserByName(username);
            if (null == user) {
                return SimpleValidationResults.notFoundResult((String)("User not found :" + username), (Object[])new Object[0]);
            }
            if (AuthenticatedUserThreadLocal.get() == user) {
                return SimpleValidationResult.FORBIDDEN;
            }
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateEnable(String username) {
            ConfluenceUser user = PersonServiceImpl.this.userAccessor.getUserByName(username);
            if (null == user) {
                return SimpleValidationResults.notFoundResult((String)("User not found :" + username), (Object[])new Object[0]);
            }
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.SET_PERMISSIONS, user)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateDelete(Person personToDelete) {
            if (personToDelete == null) {
                throw new NotFoundException();
            }
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            ConfluenceUser userToDelete = PersonServiceImpl.this.userAccessor.getUserByKey(personToDelete.optionalUserKey().orElse(null));
            try {
                if (currentUser != null && currentUser.equals(userToDelete) || !PersonServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.REMOVE, userToDelete)) {
                    return SimpleValidationResult.FORBIDDEN;
                }
                if (!PersonServiceImpl.this.userAccessor.isUnsyncedUser(userToDelete) && !PersonServiceImpl.this.userAccessor.isUserRemovable(userToDelete)) {
                    return SimpleValidationResult.builder().authorized(true).addError("User is not removable.", new Object[0]).build();
                }
                return SimpleValidationResult.VALID;
            }
            catch (EntityException e) {
                log.error("Error determining whether or not user is removable", (Throwable)e);
                return SimpleValidationResult.builder().addError(e.getMessage(), new Object[0]).build();
            }
        }

        public ValidationResult validateAddMembership(String username, String groupName) {
            Group group;
            ConfluenceUser user = PersonServiceImpl.this.userAccessor.getUserByName(username);
            Optional<ValidationResult> mandatoryFieldValidation = PersonServiceImpl.this.performMandatoryFieldChecks(user, group = PersonServiceImpl.this.userAccessor.getGroup(groupName), username, groupName);
            if (mandatoryFieldValidation.isPresent()) {
                return mandatoryFieldValidation.get();
            }
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, group)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateRemoveMembership(String username, String groupName) {
            Group group;
            ConfluenceUser user = PersonServiceImpl.this.userAccessor.getUserByName(username);
            Optional<ValidationResult> mandatoryFieldValidation = PersonServiceImpl.this.performMandatoryFieldChecks(user, group = PersonServiceImpl.this.userAccessor.getGroup(groupName), username, groupName);
            if (mandatoryFieldValidation.isPresent()) {
                return mandatoryFieldValidation.get();
            }
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.EDIT, group)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            HashSet<String> adminGroups = new HashSet<String>();
            for (SpacePermission spacePermission : PersonServiceImpl.this.spacePermissionManager.getGlobalPermissions()) {
                if (!"SYSTEMADMINISTRATOR".equals(spacePermission.getType()) || !spacePermission.isGroupPermission()) continue;
                adminGroups.add(spacePermission.getGroup());
            }
            if (adminGroups.contains(groupName) && adminGroups.size() == 1 && AuthenticatedUserThreadLocal.get() == user) {
                return SimpleValidationResult.builder().addError("To prevent Confluence being left without any administrators, you cannot remove yourself from the last remaining group(s) that allow you to ADMINISTER or USE confluence. If you wish to revoke your administrator access, have another administrator remove you.", new Object[0]).build();
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateUserCreate(UserDetailsForCreation userDetailsForCreation) {
            if (!PersonServiceImpl.this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, User.class)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            ValidationResult userNameValidationResult = this.validateUserName(userDetailsForCreation);
            if (!SimpleValidationResult.VALID.equals(userNameValidationResult)) {
                return userNameValidationResult;
            }
            ValidationResult fullNameValidationResult = this.validateFullName(userDetailsForCreation);
            if (!SimpleValidationResult.VALID.equals(fullNameValidationResult)) {
                return fullNameValidationResult;
            }
            ValidationResult emailValidationResult = this.validateEmail(userDetailsForCreation);
            if (!SimpleValidationResult.VALID.equals(emailValidationResult)) {
                return emailValidationResult;
            }
            ValidationResult passwordValidationResult = this.validatePassword(userDetailsForCreation);
            if (!SimpleValidationResult.VALID.equals(passwordValidationResult)) {
                return passwordValidationResult;
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateUserName(UserDetailsForCreation userDetailsForCreation) {
            String userName = userDetailsForCreation.getUserName();
            if (StringUtils.isBlank((CharSequence)userName)) {
                return SimpleValidationResults.invalidResult((String)"userName cannot be null or blank", (Object[])new Object[0]);
            }
            if (StringUtils.containsAny((CharSequence)userName, (CharSequence)"\\,+<>'\"")) {
                return SimpleValidationResults.invalidResult((String)"userName cannot contain the characters \\ or , or + or < or > or quotes", (Object[])new Object[0]);
            }
            if (!userName.matches("[^\\s]+")) {
                return SimpleValidationResults.invalidResult((String)"userName cannot contain any whitespace characters", (Object[])new Object[0]);
            }
            if (ENGLISH_ANONYMOUS_USER.equalsIgnoreCase(userName) || PersonServiceImpl.this.i18NBeanFactory.getI18NBean().getText("anonymous.name").equalsIgnoreCase(userName)) {
                return SimpleValidationResults.invalidResult((String)"This userName is reserved for use by Confluence", (Object[])new Object[0]);
            }
            if (!userName.equals(userName.toLowerCase())) {
                return SimpleValidationResults.invalidResult((String)"The userName must be in lower case", (Object[])new Object[0]);
            }
            if (PersonServiceImpl.this.confluenceUserResolver.getUserByName(userName) != null) {
                return SimpleValidationResults.conflictResult((String)"A user with this userName already exists.", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateFullName(UserDetailsForCreation userDetailsForCreation) {
            String fullName = userDetailsForCreation.getFullName();
            if (StringUtils.isBlank((CharSequence)fullName)) {
                return SimpleValidationResults.invalidResult((String)"fullName cannot be null or blank", (Object[])new Object[0]);
            }
            if (StringUtils.containsAny((CharSequence)fullName, (CharSequence)"<>")) {
                return SimpleValidationResults.invalidResult((String)"fullName cannot contain characters < or >", (Object[])new Object[0]);
            }
            if (PersonServiceImpl.this.i18NBeanFactory.getI18NBean().getText("anonymous.name").equalsIgnoreCase(fullName) || ENGLISH_ANONYMOUS_USER.equalsIgnoreCase(fullName)) {
                return SimpleValidationResults.invalidResult((String)"This fullName is reserved for use by Confluence", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validateEmail(UserDetailsForCreation userDetailsForCreation) {
            String email = userDetailsForCreation.getEmail();
            if (StringUtils.isBlank((CharSequence)email) || !EmailValidator.getInstance().isValid(email)) {
                return SimpleValidationResults.invalidResult((String)"Invalid email address", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        private ValidationResult validatePassword(UserDetailsForCreation userDetailsForCreation) {
            String password = userDetailsForCreation.getPassword();
            if (Boolean.TRUE.equals(userDetailsForCreation.isNotifyViaEmail()) && !StringUtils.isBlank((CharSequence)password)) {
                return SimpleValidationResults.invalidResult((String)"Cannot specify a password if notifyViaEmail is chosen", (Object[])new Object[0]);
            }
            if (Boolean.FALSE.equals(userDetailsForCreation.isNotifyViaEmail()) && StringUtils.isBlank((CharSequence)password)) {
                return SimpleValidationResults.invalidResult((String)"password cannot be null or empty", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateChangePassword(String userName, String password) {
            if (StringUtils.isBlank((CharSequence)userName)) {
                return SimpleValidationResults.invalidResult((String)"User name cannot be null or empty.", (Object[])new Object[0]);
            }
            if (StringUtils.isBlank((CharSequence)password)) {
                return SimpleValidationResults.invalidResult((String)"newPassword cannot be null or empty.", (Object[])new Object[0]);
            }
            ConfluenceUser givenUser = PersonServiceImpl.this.userAccessor.getUserByName(userName);
            if (givenUser == null) {
                return SimpleValidationResults.notFoundResult((String)("User " + userName + " not found"), (Object[])new Object[0]);
            }
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if (!PersonServiceImpl.this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, givenUser)) {
                return SimpleValidationResult.FORBIDDEN;
            }
            return SimpleValidationResult.VALID;
        }

        public ValidationResult validateChangeMyPassword(PasswordChangeDetails passwordChangeDetails) {
            if (StringUtils.isBlank((CharSequence)passwordChangeDetails.getNewPassword())) {
                return SimpleValidationResults.invalidResult((String)"newPassword cannot be null or empty.", (Object[])new Object[0]);
            }
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if (currentUser == null) {
                return SimpleValidationResults.notAuthenticatedResult((String)"User not authenticated", (Object[])new Object[0]);
            }
            if (PersonServiceImpl.this.loginManager.requiresElevatedSecurityCheck(currentUser.getName())) {
                return SimpleValidationResults.forbiddenResult((String)"User has exceeded the number of allowed failed logins. Please ask your administrator to reset the failed login count", (Object[])new Object[0]);
            }
            if (!PersonServiceImpl.this.userAccessor.authenticate(currentUser.getName(), passwordChangeDetails.getOldPassword())) {
                PersonServiceImpl.this.loginManager.onFailedLoginAttempt(currentUser.getName(), null);
                return SimpleValidationResults.forbiddenResult((String)"The oldPassword was incorrect. Please try again.", (Object[])new Object[0]);
            }
            return SimpleValidationResult.VALID;
        }
    }

    public class PersonFinderImpl
    extends AbstractFinder<Person>
    implements PersonService.PersonFinder {
        private UserKey userKey;
        private String username;
        private com.atlassian.confluence.api.model.people.Group memberOfGroup;
        private boolean hasUserKeyOrUsername;

        public PersonFinderImpl(Expansion ... expansions) {
            super(expansions);
            this.hasUserKeyOrUsername = false;
        }

        public PersonService.PersonFinder withUserKey(UserKey userKey) {
            this.userKey = userKey;
            this.hasUserKeyOrUsername = true;
            return this;
        }

        public PersonService.PersonFinder withUsername(String username) {
            if (this.userKey != null) {
                throw new BadRequestException("Only one of username or userkey can be provided");
            }
            this.username = username;
            this.hasUserKeyOrUsername = true;
            return this;
        }

        public PersonService.PersonFinder withMembershipOf(com.atlassian.confluence.api.model.people.Group group) {
            this.memberOfGroup = group;
            return this;
        }

        public PageResponse<Person> fetchMany(PageRequest request) {
            try {
                LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.users());
                if (this.memberOfGroup == null) {
                    return PersonServiceImpl.this.paginationService.performPaginationRequest(limitedRequest, PersonServiceImpl.this.userAccessor::getUsers, item -> PersonServiceImpl.this.personFactory.buildFrom((ConfluenceUser)item, this.getExpansions()));
                }
                Pager userNames = PersonServiceImpl.this.groupManager.getMemberNames((Group)new DefaultGroup(this.memberOfGroup.getName()));
                return PagerToPageResponseHelper.createFromPager(userNames, limitedRequest, username -> PersonServiceImpl.this.personFactory.buildFrom(PersonServiceImpl.this.userAccessor.getUserByName((String)username), this.getExpansions()));
            }
            catch (EntityException ex) {
                throw new InternalServerException("Error fetching users", (Throwable)ex);
            }
        }

        public Optional<Person> fetch() {
            if (this.hasUserKeyOrUsername) {
                ConfluenceUser user = null;
                if (this.userKey == null && this.username == null) {
                    return Optional.ofNullable(PersonServiceImpl.this.personFactory.anonymous());
                }
                if (this.userKey != null) {
                    user = PersonServiceImpl.this.userAccessor.getUserByKey(this.userKey);
                } else if (this.username != null) {
                    user = PersonServiceImpl.this.userAccessor.getUserByName(this.username);
                }
                if (user == null || this.memberOfGroup != null && !PersonServiceImpl.this.userAccessor.hasMembership(this.memberOfGroup.getName(), user.getName())) {
                    return Optional.empty();
                }
                return Optional.ofNullable(PersonServiceImpl.this.personFactory.forUser(user, this.getExpansions()));
            }
            return Optional.empty();
        }
    }

    public class PersonSearcherImpl
    implements PersonService.PersonSearcher {
        private String username;
        private boolean searchingUnsyncedUsers = false;

        public PersonService.PersonSearcher forUnsyncedUsers(String username) {
            this.username = username;
            this.searchingUnsyncedUsers = true;
            return this;
        }

        public PageResponse<Person> fetchMany(PageRequest request) {
            LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)request, (int)PaginationLimits.users());
            try {
                if (this.username != null && this.searchingUnsyncedUsers) {
                    return PagerToPageResponseHelper.createFromPager(PersonServiceImpl.this.userAccessor.searchUnsyncedUsers(this.username), limitedRequest, PersonServiceImpl.this.personFactory::forUser);
                }
            }
            catch (EntityException ex) {
                throw new InternalServerException("Error fetching users", (Throwable)ex);
            }
            return PageResponseImpl.empty((boolean)false);
        }
    }
}

