/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.core.task.MultiQueueTaskManager
 *  com.atlassian.core.task.Task
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.crowd.model.user.UserTemplate
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.security.random.SecureTokenGenerator
 *  com.atlassian.user.User
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableSet
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.user;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.event.events.user.DomainRestrictedUserSignupEvent;
import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.mail.notification.NotificationsSender;
import com.atlassian.confluence.mail.notification.listeners.NotificationData;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.SignupManager;
import com.atlassian.confluence.user.UserVerificationTokenManager;
import com.atlassian.confluence.user.UserVerificationTokenType;
import com.atlassian.confluence.user.notifications.EmailService;
import com.atlassian.confluence.user.notifications.InviteEmailBuilder;
import com.atlassian.confluence.user.notifications.NotificationSendResult;
import com.atlassian.confluence.user.notifications.SignupEmailBuilder;
import com.atlassian.confluence.user.notifications.WelcomeEmailBuilder;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.core.task.Task;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.crowd.model.user.UserTemplate;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.security.random.SecureTokenGenerator;
import com.atlassian.user.User;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class DefaultSignupManager
implements SignupManager {
    private BandanaManager bandanaManager;
    private SecureTokenGenerator secureTokenGenerator;
    public static final String SIGNUP_TOKEN = "easy-user.sign-up.token";
    public static final String PREVIOUS_SIGNUP_TOKEN = "easy-user.sign-up.token.previous";
    public static final String NOTIFY_ON_SIGNUP_TOKEN = "easy-user.sign-up.notify-on-invite";
    static final String RESTRICTED_DOMAINS = "easy-user.sign-up.restricted-domains";
    private final UserVerificationTokenManager userVerificationTokenManager;
    private final CrowdService crowdService;
    private final SettingsManager settingsManager;
    private InviteEmailBuilder inviteEmailBuilder;
    private SignupEmailBuilder signupEmailBuilder;
    private final EmailService notificationsService;
    private final NotificationsSender notificationsSender;
    private final EventPublisher eventPublisher;
    private final PersonalInformationManager personalInformationManager;
    private final UserChecker userChecker;
    private final MultiQueueTaskManager taskManager;
    private final WelcomeEmailBuilder welcomeEmailBuilder;

    public DefaultSignupManager(BandanaManager bandanaManager, SecureTokenGenerator secureTokenGenerator, UserVerificationTokenManager userVerificationTokenManager, CrowdService crowdService, SettingsManager settingsManager, WikiStyleRenderer wikiStyleRenderer, EmailService notificationsService, NotificationsSender notificationsSender, EventPublisher eventPublisher, UserChecker userChecker, PersonalInformationManager personalInformationManager, MultiQueueTaskManager taskManager, I18NBeanFactory i18NBeanFactory, DataSourceFactory dataSourceFactory) {
        this.bandanaManager = bandanaManager;
        this.secureTokenGenerator = secureTokenGenerator;
        this.crowdService = crowdService;
        this.settingsManager = settingsManager;
        this.userVerificationTokenManager = userVerificationTokenManager;
        this.notificationsSender = notificationsSender;
        this.inviteEmailBuilder = new InviteEmailBuilder(settingsManager, wikiStyleRenderer);
        this.signupEmailBuilder = new SignupEmailBuilder(i18NBeanFactory);
        this.welcomeEmailBuilder = new WelcomeEmailBuilder(settingsManager, dataSourceFactory, userVerificationTokenManager);
        this.notificationsService = notificationsService;
        this.eventPublisher = eventPublisher;
        this.personalInformationManager = personalInformationManager;
        this.userChecker = userChecker;
        this.taskManager = taskManager;
    }

    @Override
    public String refreshAndGetToken() {
        this.setPreviousSignupToken(this.getSignUpTokenInternal());
        String token = this.secureTokenGenerator.generateToken().substring(0, 16);
        this.setSignupToken(token);
        return token;
    }

    private void setSignupToken(String token) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SIGNUP_TOKEN, (Object)token);
    }

    @Override
    public boolean canSignUpWith(String token) {
        String signUpToken = this.getSignUpToken();
        return StringUtils.isNotBlank((CharSequence)signUpToken) && signUpToken.equals(token);
    }

    @Override
    public boolean isEmailSentOnInviteSignUp() {
        Boolean val = this.getSignupNotifyToken();
        return val == null ? false : val;
    }

    private Boolean getSignupNotifyToken() {
        return (Boolean)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, NOTIFY_ON_SIGNUP_TOKEN);
    }

    @Override
    public boolean isPublicSignupPermitted() {
        Settings settings = this.getSettings();
        return !settings.isExternalUserManagement() && !settings.isDenyPublicSignup() && StringUtils.isBlank((CharSequence)this.getRestrictedDomains());
    }

    @Override
    public String getRelativeSignupURL() {
        return "/signup.action?token=" + this.getSignUpToken();
    }

    @Override
    public String getSignupURL() {
        String baseUrl = this.getBaseUrl();
        String relativeSignupURL = this.getRelativeSignupURL();
        if (StringUtils.isBlank((CharSequence)baseUrl)) {
            return null;
        }
        return baseUrl + relativeSignupURL;
    }

    private String getBaseUrl() {
        String baseUrl = this.getSettings().getBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    @Override
    public void setEmailSentOnInviteSignUp(boolean notify) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, NOTIFY_ON_SIGNUP_TOKEN, (Object)notify);
    }

    @Override
    public String restorePreviousToken() {
        String previousToken = this.getPreviousSignupToken();
        String currentToken = this.getSignUpTokenInternal();
        this.setSignupToken(previousToken);
        this.setPreviousSignupToken(currentToken);
        return previousToken;
    }

    @Override
    public String getSignUpToken() {
        String currentSignupToken = this.getSignUpTokenInternal();
        if (currentSignupToken == null) {
            return this.refreshAndGetToken();
        }
        return currentSignupToken;
    }

    private String getSignUpTokenInternal() {
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, SIGNUP_TOKEN);
    }

    @Override
    public NotificationSendResult sendInvites(SendUserInviteEvent event) {
        NotificationData notificationData = this.inviteEmailBuilder.buildFrom(event, this.getRelativeSignupURL());
        NotificationSendResult result = this.notificationsService.sendToEmails(notificationData, event.getRecipients());
        this.eventPublisher.publish((Object)event);
        return result;
    }

    @Override
    public void sendConfirmationEmail(String token, User user) {
        PreRenderedMailNotificationQueueItem queueItem = this.signupEmailBuilder.buildFrom(token, user);
        this.eventPublisher.publish((Object)new DomainRestrictedUserSignupEvent(this, user));
        this.taskManager.addTask("mail", (Task)queueItem);
    }

    @Override
    public void sendWelcomeEmail(ConfluenceUser user) {
        NotificationData notificationData = this.welcomeEmailBuilder.buildFrom(user);
        NotificationContext context = this.welcomeEmailBuilder.buildContextFrom(user, notificationData);
        this.notificationsSender.sendNotification(user.getName(), context, notificationData, null);
    }

    private Settings getSettings() {
        return this.settingsManager.getGlobalSettings();
    }

    private void setPreviousSignupToken(String currentToken) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PREVIOUS_SIGNUP_TOKEN, (Object)currentToken);
    }

    private String getPreviousSignupToken() {
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, PREVIOUS_SIGNUP_TOKEN);
    }

    @Override
    public void setPublicSignupMode() {
        this.setDenyPublicSignup(false);
        this.setRestrictedDomains("");
    }

    @Override
    public void setPrivateSignupMode() {
        this.setDenyPublicSignup(true);
        this.setRestrictedDomains("");
    }

    @Override
    public void setDomainRestrictedSignupMode(String allowedDomains) {
        this.setDenyPublicSignup(false);
        this.setRestrictedDomains(allowedDomains);
    }

    @Override
    public String getRestrictedDomains() {
        return (String)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, RESTRICTED_DOMAINS);
    }

    private Set<String> getRestrictedDomainSet() {
        return ImmutableSet.copyOf((Iterable)Splitter.on((CharMatcher)CharMatcher.anyOf((CharSequence)",; ")).omitEmptyStrings().trimResults().split((CharSequence)this.getRestrictedDomains()));
    }

    @Override
    public boolean isEmailOnRestrictedDomain(String email) {
        if (email == null) {
            return false;
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }
        String emailDomain = parts[1].trim();
        return this.getRestrictedDomainSet().contains(emailDomain);
    }

    private String setPendingConfirmation(User user) {
        return this.userVerificationTokenManager.generateAndSaveToken(user.getName(), UserVerificationTokenType.USER_SIGNUP);
    }

    @Override
    public boolean isPendingConfirmation(User user) {
        return this.userVerificationTokenManager.hasToken(user.getName(), UserVerificationTokenType.USER_SIGNUP);
    }

    private void removePendingConfirmation(User user) {
        this.userVerificationTokenManager.clearToken(user.getName(), UserVerificationTokenType.USER_SIGNUP);
    }

    @Override
    public boolean isTokenForUserValid(User user, String token) {
        return this.userVerificationTokenManager.hasValidUserToken(user.getName(), UserVerificationTokenType.USER_SIGNUP, token);
    }

    @Override
    public boolean doesUserHaveOutdatedSignupToken(User user) {
        return this.userVerificationTokenManager.hasOutdatedUserToken(user.getName(), UserVerificationTokenType.USER_SIGNUP);
    }

    @Override
    public void enableConfirmedUser(User user) throws UserNotFoundException, OperationFailedException, InvalidUserException, OperationNotPermittedException {
        if (!this.userChecker.isLicensedToAddMoreUsers()) {
            throw new LicenseUserLimitExceededException("You are not licensed to add any more users to this installation of Confluence. Please contact sales@atlassian.com");
        }
        com.atlassian.crowd.embedded.api.User crowdUser = this.crowdService.getUser(user.getName());
        UserTemplate template = new UserTemplate(crowdUser);
        template.setActive(true);
        this.crowdService.updateUser((com.atlassian.crowd.embedded.api.User)template);
        this.crowdService.addUserToGroup(crowdUser, this.crowdService.getGroup(this.settingsManager.getGlobalSettings().getDefaultUsersGroup()));
        this.removePendingConfirmation(user);
        this.personalInformationManager.createPersonalInformation(user);
        this.userChecker.incrementRegisteredUserCount();
    }

    @Override
    public String createUserPendingConfirmation(User user, String password) throws OperationFailedException, InvalidUserException, InvalidCredentialException, OperationNotPermittedException {
        UserTemplate template = new UserTemplate(user.getName());
        template.setDisplayName(user.getFullName());
        template.setEmailAddress(user.getEmail());
        template.setActive(false);
        this.crowdService.addUser((com.atlassian.crowd.embedded.api.User)template, password);
        return this.setPendingConfirmation(user);
    }

    @Override
    public boolean isDomainRestrictedSignupEnabled() {
        return !this.settingsManager.getGlobalSettings().isDenyPublicSignup() && StringUtils.isNotBlank((CharSequence)this.getRestrictedDomains());
    }

    private void setDenyPublicSignup(boolean deny) {
        Settings globalSettings = this.getSettings();
        globalSettings.setDenyPublicSignup(deny);
        this.settingsManager.updateGlobalSettings(globalSettings);
    }

    private void setRestrictedDomains(String domains) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, RESTRICTED_DOMAINS, (Object)domains);
    }

    void setInviteEmailBuilder(InviteEmailBuilder inviteEmailBuilder) {
        this.inviteEmailBuilder = inviteEmailBuilder;
    }

    void setSignupEmailBuilder(SignupEmailBuilder signupEmailBuilder) {
        this.signupEmailBuilder = signupEmailBuilder;
    }
}

