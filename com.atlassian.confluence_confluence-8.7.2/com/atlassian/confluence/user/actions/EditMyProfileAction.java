/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.apache.struts2.interceptor.ParameterAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.CaptchaAware;
import com.atlassian.confluence.security.CaptchaManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.AuthenticatorOverwrite;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.AuthenticationHelper;
import com.atlassian.confluence.user.actions.UserDetailsMap;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditMyProfileAction
extends AbstractUserProfileAction
implements CaptchaAware,
FormAware,
ParameterAware {
    private static final Logger log = LoggerFactory.getLogger(EditMyProfileAction.class);
    private CaptchaManager captchaManager;
    private UserDetailsMap userDetailsMap;
    private String fullName;
    private String email;
    private String password;
    private UserFormValidator validator;
    private LoginManager loginManager;

    @Override
    public void validate() {
        UserForm form = new UserForm(this.getUsername(), this.fullName, this.email);
        this.validator.validateEditUser(form, this.messageHolder);
        String website = this.getUserProperty("website");
        if (!StringUtils.isBlank((CharSequence)website) && !UrlUtils.verifyUrl(website)) {
            if (website.contains("http://") || !UrlUtils.verifyUrl("http://" + website)) {
                this.addFieldError("userparam-website", this.getText("user.website.invalid"));
            } else {
                this.getUserDetailsMap().setProperty("website", "http://" + website);
            }
        }
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doInput() throws Exception {
        if (this.getUser() == null) {
            return "error";
        }
        this.fullName = this.getUser().getFullName();
        this.email = this.getUser().getEmail();
        return "input";
    }

    public String doEdit() throws Exception {
        if (!this.getFieldErrors().isEmpty()) {
            return "input";
        }
        if (this.loginManager.requiresElevatedSecurityCheck(this.getUser().getName())) {
            AuthenticationHelper.systemLogout((Principal)((Object)this.getUser()), ServletActionContext.getRequest(), ServletActionContext.getResponse(), this.eventManager, this);
            return "login";
        }
        if (this.isConfirmPassOnEmailChange() && !StringUtils.equalsIgnoreCase((CharSequence)this.getUser().getEmail(), (CharSequence)this.email) && !this.userAccessor.authenticate(this.getUsername(), this.password)) {
            this.loginManager.onFailedLoginAttempt(this.getUser().getName(), ServletActionContext.getRequest());
            this.addActionError(this.getText("reenter.password.wrong"));
            return "input";
        }
        boolean shouldUpdatePersonalInfo = this.shouldUpdatePersonalInfo(this.getUser(), this.fullName, this.getPersonalInformation());
        try {
            if (!this.settingsManager.getGlobalSettings().isExternalUserManagement() && !this.userAccessor.isReadOnly(this.getUser())) {
                this.updateUser();
            }
        }
        catch (CrowdRuntimeException e) {
            this.addActionError(this.getText("edit.my.profile.failed"));
            log.warn("Failed to update user profile.", (Throwable)e);
            return "error";
        }
        if (shouldUpdatePersonalInfo) {
            this.personalInformationManager.savePersonalInformation(this.getUser(), this.getPersonalInformation(), this.fullName);
        }
        this.getUserDetailsMap().copyPropertiesToManager();
        return this.getFieldErrors().isEmpty() ? "success" : "input";
    }

    private boolean shouldUpdatePersonalInfo(User user, String fullName, String newInfo) {
        PersonalInformation oldInfo = this.getPersonalInformationEntity();
        return oldInfo == null || !newInfo.equals(oldInfo.getBodyContent().getBody()) || this.hasFullNameChanged(user, fullName);
    }

    private boolean hasFullNameChanged(User user, String fullName) {
        return fullName != null && !fullName.trim().equals(user.getFullName());
    }

    @Override
    public Object getFullName() {
        return this.fullName == null ? null : new HtmlFragment((Object)this.fullName);
    }

    @Override
    public Object getEmail() {
        return this.email == null ? null : new HtmlFragment((Object)this.email);
    }

    public String getStoredEmail() {
        return this.getUser().getEmail();
    }

    public void setFullName(Object fullName) {
        this.fullName = this.val(fullName);
    }

    public void setEmail(Object email) {
        this.email = this.val(email);
    }

    public void setPasswordconfirmation(String password) {
        this.password = password;
    }

    private String val(Object stringArray) {
        return stringArray == null ? null : ((String[])stringArray)[0];
    }

    private void updateUser() {
        DefaultUser userTemplate = new DefaultUser((User)this.getUser());
        userTemplate.setFullName(this.fullName);
        userTemplate.setEmail(this.email);
        this.userAccessor.saveUser((User)userTemplate);
    }

    @Override
    public boolean isEditMode() {
        return true;
    }

    @Override
    public String getUserProperty(String key) {
        return this.getUserDetailsMap().getProperty(key);
    }

    public List<String> getUserDetailsKeys(String groupKey) {
        return this.userDetailsManager.getProfileKeys(groupKey);
    }

    public List<String> getUserDetailsGroups() {
        return this.userDetailsManager.getProfileGroups();
    }

    public CaptchaManager getCaptchaManager() {
        return this.captchaManager;
    }

    public void setCaptchaManager(CaptchaManager captchaManager) {
        this.captchaManager = captchaManager;
    }

    public void setParameters(Map map) {
        this.getUserDetailsMap().setParameters(map);
    }

    public boolean isConfirmPassOnEmailChange() {
        return !AuthenticatorOverwrite.isPasswordConfirmationDisabled();
    }

    private UserDetailsMap getUserDetailsMap() {
        if (this.userDetailsMap == null) {
            this.userDetailsMap = new UserDetailsMap(this.getUser(), this.userDetailsManager);
        }
        return this.userDetailsMap;
    }

    public void setUserFormValidator(UserFormValidator validator) {
        this.validator = validator;
    }

    @Override
    public void setLoginManager(LoginManager loginManager) {
        super.setLoginManager(loginManager);
        this.loginManager = loginManager;
    }

    public String getLoginUrl() {
        return AuthenticationHelper.getLoginUrl();
    }
}

