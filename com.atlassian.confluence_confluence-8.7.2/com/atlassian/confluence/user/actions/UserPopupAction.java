/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.follow.FollowManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

@RequiresAnyConfluenceAccess
public class UserPopupAction
extends ConfluenceActionSupport
implements UserAware {
    private FollowManager followManager;
    private String username;
    private User user;
    private boolean canFollowUser;
    private String profileGroups;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        User user = this.getUser();
        this.canFollowUser = this.canFollow(user);
        return user == null ? "error" : "success";
    }

    private boolean canFollow(User followee) {
        if (!this.currentUserCanFollowOthers()) {
            return false;
        }
        return followee != null && this.getConfluenceAccessManager().getUserAccessStatus(followee).hasLicensedAccess();
    }

    private boolean currentUserCanFollowOthers() {
        ConfluenceUser currentUser = this.getAuthenticatedUser();
        return currentUser != null && this.getConfluenceAccessManager().getUserAccessStatus(currentUser).hasLicensedAccess();
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProfileGroups(String profileGroups) {
        if (profileGroups != null) {
            this.profileGroups = profileGroups.trim();
        }
    }

    @Override
    public User getUser() {
        if (this.user == null) {
            this.user = this.userAccessor.getUserByName(this.username);
        }
        if (this.user == null && this.username != null) {
            this.user = UnknownUser.unknownUser(this.username, this.i18NBeanFactory.getI18NBean());
        }
        return this.user;
    }

    public boolean isFollowing() {
        return this.followManager.isUserFollowing(this.getAuthenticatedUser(), this.getUser());
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getUser());
    }

    public void setFollowManager(FollowManager followManager) {
        this.followManager = followManager;
    }

    @Override
    public boolean isUserRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public boolean canFollowUser() {
        return this.canFollowUser;
    }

    public String getProfileGroups() {
        return this.profileGroups;
    }
}

