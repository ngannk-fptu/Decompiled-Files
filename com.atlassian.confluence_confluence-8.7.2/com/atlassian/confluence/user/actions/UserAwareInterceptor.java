/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.opensymphony.xwork2.Action
 *  com.opensymphony.xwork2.ActionInvocation
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.struts.AbstractAwareInterceptor;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.UserAware;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;

public class UserAwareInterceptor
extends AbstractAwareInterceptor {
    private SpacePermissionManager spacePermissionManager;
    private ConfluenceAccessManager confluenceAccessManager;

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        try (Ticker ignored = Timers.start((String)"UserAwareInterceptor.intercept()");){
            Action action = (Action)actionInvocation.getAction();
            if (action instanceof UserAware) {
                ConfluenceUser loggedInUser;
                UserAware userAware = (UserAware)action;
                User targetUser = userAware.getUser();
                if (userAware.isViewPermissionRequired() && !this.canBrowseUsers(loggedInUser = AuthenticatedUserThreadLocal.get())) {
                    String string = "notpermitted";
                    return string;
                }
                if (userAware.isUserRequired() && targetUser == null) {
                    String string = "notfound";
                    return string;
                }
            }
        }
        return actionInvocation.invoke();
    }

    private boolean canBrowseUsers(ConfluenceUser user) {
        if (this.shouldCheckBrowseUsersPermission(user)) {
            return this.getSpacePermissionManager().hasPermission("VIEWUSERPROFILES", null, user);
        }
        return true;
    }

    private boolean shouldCheckBrowseUsersPermission(ConfluenceUser user) {
        return user == null || !this.hasLicensedAccess(user);
    }

    private boolean hasLicensedAccess(ConfluenceUser user) {
        return this.getConfluenceAccessManager().getUserAccessStatus(user).hasLicensedAccess();
    }

    private SpacePermissionManager getSpacePermissionManager() {
        if (this.spacePermissionManager == null) {
            this.spacePermissionManager = (SpacePermissionManager)ContainerManager.getComponent((String)"spacePermissionManager");
        }
        return this.spacePermissionManager;
    }

    private ConfluenceAccessManager getConfluenceAccessManager() {
        if (this.confluenceAccessManager == null) {
            this.confluenceAccessManager = (ConfluenceAccessManager)ContainerManager.getComponent((String)"confluenceAccessManager");
        }
        return this.confluenceAccessManager;
    }

    void setSpacePermissionManager(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    void setConfluenceAccessManager(ConfluenceAccessManager confluenceAccessManager) {
        this.confluenceAccessManager = confluenceAccessManager;
    }
}

