/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.confluence.user.crowd.CrowdUserDirectoryHelper;
import com.atlassian.confluence.util.PlainTextToHtmlConverter;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.user.EntityException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewUserAction
extends AbstractUsersAction {
    private static final Logger log = LoggerFactory.getLogger(ViewUserAction.class);
    private LoginManager loginManager;
    private CrowdUserDirectoryHelper crowdUserDirectoryHelper;
    private CrowdService crowdService;
    private LoginInfo loginInfo;
    private User crowdUser;
    private List<Directory> directories;
    private boolean disableFailure;
    private boolean enableFailure;

    @Override
    public void setLoginManager(LoginManager loginManager) {
        this.loginManager = loginManager;
    }

    public void setCrowdService(CrowdService crowdService) {
        this.crowdService = crowdService;
    }

    public void setCrowdUserDirectoryHelper(CrowdUserDirectoryHelper crowdUserDirectoryHelper) {
        this.crowdUserDirectoryHelper = crowdUserDirectoryHelper;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        if (this.disableFailure) {
            this.addActionError("user.not.disabled", PlainTextToHtmlConverter.encodeHtmlEntities(this.getUsername()));
        }
        if (this.enableFailure) {
            this.addActionError("user.not.enabled", PlainTextToHtmlConverter.encodeHtmlEntities(this.getUsername()));
        }
        ConfluenceUser user = this.getUser();
        this.loginInfo = this.loginManager.getLoginInfo(user);
        this.crowdUser = this.crowdService.getUser(user != null ? user.getName() : "");
        this.directories = this.crowdUserDirectoryHelper.getDirectoriesForUser(user);
        return "success";
    }

    public boolean isUserDeactivated() {
        return this.userAccessor.isDeactivated(this.getUser());
    }

    public User getCrowdUser() {
        return this.crowdUser;
    }

    public List<Directory> getDirectories() {
        if (this.directories == null) {
            this.directories = this.crowdUserDirectoryHelper.getDirectoriesForUser(this.getUser());
        }
        return this.directories;
    }

    @Deprecated
    public Directory getDirectory() {
        if (this.getDirectories().size() == 0) {
            return null;
        }
        return this.getDirectories().get(0);
    }

    public boolean canRemove() {
        ConfluenceUser user = this.getUser();
        try {
            return user != null && !user.getName().equals(this.getRemoteUsername()) && !this.settingsManager.getGlobalSettings().isExternalUserManagement() && this.userAccessor.isUserRemovable(user) && this.permissionManager.hasPermission((com.atlassian.user.User)this.getAuthenticatedUser(), Permission.REMOVE, user);
        }
        catch (EntityException e) {
            log.error("Error checking whether or not user is removable", (Throwable)e);
            this.addActionError("user.not.removable.check.failed", this.getUsername());
            return false;
        }
    }

    public boolean canEdit() {
        return this.permissionManager.hasPermission((com.atlassian.user.User)this.getAuthenticatedUser(), Permission.EDIT, this.getUser());
    }

    public LoginInfo getLoginInfo() {
        return this.loginInfo;
    }

    public void setDisableFailure(boolean disableFailure) {
        this.disableFailure = disableFailure;
    }

    public void setEnableFailure(boolean enableFailure) {
        this.enableFailure = enableFailure;
    }

    public boolean isEditMode() {
        return false;
    }
}

