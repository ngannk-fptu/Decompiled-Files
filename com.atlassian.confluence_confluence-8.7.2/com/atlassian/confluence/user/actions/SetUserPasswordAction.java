/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.crowd.exception.runtime.CrowdRuntimeException
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class SetUserPasswordAction
extends AbstractUsersAction
implements ExternalUserManagementAware {
    private static final Logger log = LoggerFactory.getLogger(SetUserPasswordAction.class);
    private String newPassword;
    private String newPasswordConfirmation;

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

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        return "success";
    }

    public void validateDoSet() {
        if (this.hasFieldErrors()) {
            return;
        }
        if (this.newPassword == null || !this.newPassword.equals(this.newPasswordConfirmation)) {
            this.addActionError(this.getText("user.pass.confirmpass.dont.match"));
        }
    }

    public String doSet() throws Exception {
        try {
            this.userAccessor.alterPassword(this.getUser(), this.newPassword);
        }
        catch (CrowdRuntimeException e) {
            String rootCause = ExceptionUtils.getRootCauseMessage((Throwable)e);
            this.addActionError(this.getText("set.user.pass.failure"));
            log.warn("Failed to update user password. Cause: {}", (Object)rootCause);
            return "error";
        }
        return "success";
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getUser());
    }
}

