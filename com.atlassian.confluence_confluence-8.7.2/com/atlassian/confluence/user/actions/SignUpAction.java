/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.crowd.exception.InvalidCredentialException
 *  com.atlassian.crowd.exception.InvalidUserException
 *  com.atlassian.crowd.exception.OperationNotPermittedException
 *  com.atlassian.crowd.exception.runtime.OperationFailedException
 *  com.atlassian.seraph.config.SecurityConfigFactory
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.search.SearchResult
 *  com.atlassian.user.search.page.Pager
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.user.GroupInviteUserSignupEvent;
import com.atlassian.confluence.event.events.user.PublicUserSignupEvent;
import com.atlassian.confluence.event.events.user.UserSignupEvent;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.user.SignupValidator;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.user.actions.AbstractLoginSignupAction;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.crowd.exception.InvalidCredentialException;
import com.atlassian.crowd.exception.InvalidUserException;
import com.atlassian.crowd.exception.OperationNotPermittedException;
import com.atlassian.crowd.exception.runtime.OperationFailedException;
import com.atlassian.seraph.config.SecurityConfigFactory;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.search.SearchResult;
import com.atlassian.user.search.page.Pager;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUpAction
extends AbstractLoginSignupAction
implements Evented<UserSignupEvent>,
ExternalUserManagementAware,
CaptchaAware {
    private static final Logger log = LoggerFactory.getLogger(SignUpAction.class);
    private String password;
    private String confirm;
    private String email;
    private String fullName;
    private UserFormValidator userFormValidator;
    private SignupValidator signupValidator;
    private User previousSignUpAttempt;

    @Override
    public void validate() {
        this.checkSignupAllowed();
        if (!this.messageHolder.hasErrors()) {
            this.validateAction();
        }
    }

    public void checkSignupAllowed() {
        this.signupValidator.validateSignup(this.token, this.messageHolder);
    }

    private Pager<User> getUsersWithDuplicateEmails() {
        SearchResult usersByEmail = this.userAccessor.getUsersByEmail(this.email);
        Pager pager = usersByEmail.pager();
        return pager;
    }

    private void validateEmailIfNecessary() {
        if (!this.hasValidToken() && this.domainRestrictedSignupEnabled() && !this.signupManager.isEmailOnRestrictedDomain(this.email)) {
            this.addActionError("signup.domain.not.allowed", new Object[]{this.getContactAdminUrl()});
        }
    }

    private boolean domainRestrictedSignupEnabled() {
        return this.signupManager.isDomainRestrictedSignupEnabled();
    }

    private boolean isPublicSignupWithNoRestrictionsPermitted() {
        Settings settings = this.getGlobalSettings();
        if (settings.isExternalUserManagement()) {
            return false;
        }
        if (settings.isDenyPublicSignup()) {
            return false;
        }
        return !this.domainRestrictedSignupEnabled();
    }

    private String[] getContactAdminUrl() {
        return new String[]{this.getBootstrapStatusProvider().getWebAppContextPath() + "/administrators.action"};
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        this.checkSignupAllowed();
        return super.doDefault();
    }

    private boolean hasValidToken() {
        return !StringUtils.isBlank((CharSequence)this.token) && this.signupManager.canSignUpWith(this.token);
    }

    @XsrfProtectionExcluded
    public String execute() throws Exception {
        if (this.hasErrors() || this.messageHolder.hasErrors()) {
            return "input";
        }
        if (this.previousSignUpAttempt != null) {
            this.permissionManager.withExemption(() -> this.userAccessor.removeUser(this.previousSignUpAttempt));
        }
        if (this.isPublicSignupWithNoRestrictionsPermitted() || this.hasValidToken()) {
            try {
                this.permissionManager.withExemption(() -> this.userAccessor.addUser(this.username, this.password, this.email, this.fullName, new String[]{this.userAccessor.getNewUserDefaultGroupName()}));
            }
            catch (LicenseUserLimitExceededException e) {
                this.addActionError("not.licensed", new Object[]{this.getContactAdminUrl()});
                return "input";
            }
            catch (InfrastructureException e) {
                this.addActionError("create.user.failed", this.username);
                log.error("Failed to create user: " + this.username, (Throwable)e);
                return "error";
            }
            SecurityConfigFactory.getInstance().getAuthenticator().login(ServletActionContext.getRequest(), ServletActionContext.getResponse(), this.username, this.password, true);
            return "success";
        }
        if (this.domainRestrictedSignupEnabled()) {
            return this.initiateDomainRestrictedSignup();
        }
        this.messageHolder.addActionError("public.signup.disabled", new Object[]{this.getContactAdminUrl()});
        return "input";
    }

    private void validateAction() {
        UserForm form = new UserForm(this.username, this.fullName, this.email, this.password, this.confirm);
        this.userFormValidator.validateNewUserBySignup(form, this.messageHolder);
        if (this.messageHolder.hasErrors()) {
            return;
        }
        this.validateEmailIfNecessary();
        Pager<User> duplicateUsers = this.getUsersWithDuplicateEmails();
        for (User user : duplicateUsers) {
            if (!this.userAccessor.isDeactivated(user) || !this.signupManager.isPendingConfirmation(user)) continue;
            this.previousSignUpAttempt = user;
        }
        if (!duplicateUsers.isEmpty() && this.previousSignUpAttempt == null) {
            this.messageHolder.addFieldError("email", "signup.email.not.unique");
        }
        if (!(this.previousSignUpAttempt != null && this.previousSignUpAttempt.getName().equalsIgnoreCase(this.username) || this.getUser() == null)) {
            this.addFieldError("username", this.getText("user.exists"));
        }
    }

    private String initiateDomainRestrictedSignup() throws OperationFailedException, InvalidUserException, InvalidCredentialException, OperationNotPermittedException {
        DefaultUser newUser = new DefaultUser(this.username, this.fullName, this.email);
        String emailToken = this.signupManager.createUserPendingConfirmation((User)newUser, this.password);
        this.signupManager.sendConfirmationEmail(emailToken, (User)newUser);
        return "email-sent";
    }

    @Override
    public UserSignupEvent getEventToPublish(String result) {
        if ("success".equals(result)) {
            return this.isPublicSignupWithNoRestrictionsPermitted() ? new PublicUserSignupEvent(this, this.getUser()) : new GroupInviteUserSignupEvent(this, this.getUser());
        }
        return null;
    }

    @Override
    public boolean isPermitted() {
        return true;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLinkLoginURL(HttpServletRequest request) {
        return SeraphUtils.getLinkLoginURL(request, this.username);
    }

    public void setUserFormValidator(UserFormValidator userFormValidator) {
        this.userFormValidator = userFormValidator;
    }

    public void setSignupValidator(SignupValidator signupValidator) {
        this.signupValidator = signupValidator;
    }
}

