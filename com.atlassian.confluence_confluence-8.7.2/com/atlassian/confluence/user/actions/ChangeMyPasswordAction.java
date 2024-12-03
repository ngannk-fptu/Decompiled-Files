/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.security.authentication.InvalidPasswordException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.FormAware;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.actions.AbstractUserProfileAction;
import com.atlassian.confluence.user.actions.AuthenticationHelper;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.user.EntityException;
import com.atlassian.user.security.authentication.InvalidPasswordException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.security.Principal;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeMyPasswordAction
extends AbstractUserProfileAction
implements FormAware {
    private static final Logger log = LoggerFactory.getLogger(ChangeMyPasswordAction.class);
    private LoginManager loginManager;
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirmation;

    public String getCurrentPassword() {
        return this.currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return this.newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doDefault() throws Exception {
        return super.doDefault();
    }

    @Override
    public void validate() {
        if (this.hasFieldErrors()) {
            return;
        }
        if (this.newPassword == null || !this.newPassword.equals(this.newPasswordConfirmation)) {
            this.addActionError(this.getText("your.pass.confirmpass.dont.match"));
        }
        if (!this.loginManager.requiresElevatedSecurityCheck(this.getUser().getName()) && !this.userAccessor.authenticate(this.getUser().getName(), this.currentPassword)) {
            this.loginManager.onFailedLoginAttempt(this.getUser().getName(), ServletActionContext.getRequest());
            this.addActionError(this.getText("cur.pass.not.correct"));
        }
    }

    public String execute() throws Exception {
        if (this.loginManager.requiresElevatedSecurityCheck(this.getUser().getName())) {
            AuthenticationHelper.systemLogout((Principal)((Object)this.getUser()), ServletActionContext.getRequest(), ServletActionContext.getResponse(), this.eventManager, this);
            return "login";
        }
        try {
            this.userAccessor.alterPassword(this.getUser(), this.newPassword);
        }
        catch (InvalidPasswordException e) {
            this.addFieldError("newPassword", this.getText("new.pass.invalid"));
            return "error";
        }
        catch (EntityException e) {
            log.error("error setting password", (Throwable)e);
            this.addFieldError("newPassword", this.getText("new.pass.error"));
            return "error";
        }
        catch (CrowdRuntimeException e) {
            String rootCause = ExceptionUtils.getRootCauseMessage((Throwable)e);
            this.addActionError(this.getText("change.my.pass.failure"));
            log.warn("Failed to update user password. Cause: {}", (Object)rootCause);
            return "error";
        }
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.getUsername() != null && super.isPermitted();
    }

    @Override
    public boolean isEditMode() {
        return true;
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

