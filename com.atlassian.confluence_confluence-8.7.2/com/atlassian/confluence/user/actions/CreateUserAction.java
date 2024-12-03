/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.core.exception.InfrastructureException
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  com.atlassian.user.security.password.Credential
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.actions.Tabbed;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.user.AdminAddedUserEvent;
import com.atlassian.confluence.license.exception.LicenseUserLimitExceededException;
import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.user.UserFormValidator;
import com.atlassian.confluence.user.actions.SearchUsersAction;
import com.atlassian.core.exception.InfrastructureException;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import com.atlassian.user.security.password.Credential;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class CreateUserAction
extends SearchUsersAction
implements Tabbed,
Evented<AdminAddedUserEvent> {
    private static final Logger log = LoggerFactory.getLogger(CreateUserAction.class);
    public static final String SEND_EMAIL = "sendEmail";
    protected String password;
    protected String confirm;
    protected String email;
    protected String fullName;
    protected boolean sendEmail;
    private UserFormValidator validator;
    private User newUser;

    @Override
    public void validate() {
        UserForm form;
        if (this.sendEmail) {
            this.confirm = null;
            this.password = null;
            String fakePassword = UUID.randomUUID().toString();
            form = new UserForm(this.username, this.fullName, this.email, fakePassword, fakePassword);
        } else {
            form = new UserForm(this.username, this.fullName, this.email, this.password, this.confirm);
        }
        this.validator.validateNewUser(form, this.messageHolder);
    }

    @Override
    public String execute() {
        DefaultUser user = new DefaultUser(this.username, this.fullName, this.email);
        try {
            this.setSendEmailDefault(this.sendEmail);
            if (this.sendEmail) {
                this.confirm = null;
                this.password = null;
                String fakePassword = UUID.randomUUID().toString();
                this.newUser = this.userAccessor.createUser((User)user, Credential.unencrypted((String)fakePassword));
            } else {
                this.newUser = this.userAccessor.createUser((User)user, Credential.unencrypted((String)this.password));
            }
            this.userAccessor.addMembership(this.userAccessor.getNewUserDefaultGroupName(), this.username);
        }
        catch (LicenseUserLimitExceededException e) {
            this.addActionError(this.getLicenseErrorHtml());
            return "error";
        }
        catch (InfrastructureException e) {
            this.addActionError("create.user.failed", user.getName());
            log.error("Failed to create user: " + user, (Throwable)e);
            return "error";
        }
        return "success";
    }

    @Override
    public String getSelectedTab() {
        return "create";
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getSendEmail() {
        return this.sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getConfirm() {
        return this.confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public void setUserFormValidator(UserFormValidator validator) {
        this.validator = validator;
    }

    @Override
    public AdminAddedUserEvent getEventToPublish(String result) {
        if ("success".equals(result)) {
            return new AdminAddedUserEvent(this.newUser);
        }
        return null;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser()) || this.permissionManager.isSystemAdministrator(this.getAuthenticatedUser());
    }
}

