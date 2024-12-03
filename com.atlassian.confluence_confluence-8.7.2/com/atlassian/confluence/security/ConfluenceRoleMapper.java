/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.seraph.auth.SimpleAbstractRoleMapper
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.ConfluenceGroupCache;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.DisabledUserManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.seraph.auth.SimpleAbstractRoleMapper;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.security.Principal;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceRoleMapper
extends SimpleAbstractRoleMapper {
    public static final String ADMIN_JSP_ROLE = "admin_jsp_role";
    public static final String CONFLUENCE_ADMIN_ROLE = "confluenceadmin_seraph_role";
    private PermissionManager permissionManager;
    private UserAccessor userAccessor;
    private DisabledUserManager disabledUserManager;

    public ConfluenceRoleMapper() {
    }

    @VisibleForTesting
    ConfluenceRoleMapper(PermissionManager permissionManager, UserAccessor userAccessor, DisabledUserManager disabledUserManager) {
        this.permissionManager = permissionManager;
        this.userAccessor = userAccessor;
        this.disabledUserManager = disabledUserManager;
    }

    public boolean hasRole(Principal user, HttpServletRequest request, String role) {
        if (user instanceof User && GeneralUtil.isSetupComplete()) {
            if (ADMIN_JSP_ROLE.equals(role)) {
                String url = request.getServletPath() + (request.getPathInfo() == null ? "" : request.getPathInfo());
                if (url.endsWith("default.jsp")) {
                    return this.getPermissionManager().hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
                }
                return this.getPermissionManager().hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
            }
            if (CONFLUENCE_ADMIN_ROLE.equals(role)) {
                return this.getPermissionManager().hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
            }
        }
        return this.seraphHasRole(user, request, role);
    }

    public boolean canLogin(Principal user, HttpServletRequest request) {
        if (user instanceof User) {
            User atlassianUser = (User)user;
            return !this.getDisabledUserManager().isDisabled(atlassianUser);
        }
        return super.canLogin(user, request);
    }

    private boolean seraphHasRole(Principal user, HttpServletRequest request, String role) {
        Collection groups = ConfluenceGroupCache.getGroups(request, this.getUserAccessor());
        if (groups == null && role == null) {
            return true;
        }
        if (groups == null) {
            return false;
        }
        return groups.contains(role);
    }

    private PermissionManager getPermissionManager() {
        if (this.permissionManager == null && ContainerManager.isContainerSetup()) {
            this.permissionManager = (PermissionManager)ContainerManager.getComponent((String)"permissionManager");
        }
        return this.permissionManager;
    }

    private UserAccessor getUserAccessor() {
        if (this.userAccessor == null && ContainerManager.isContainerSetup()) {
            this.userAccessor = (UserAccessor)ContainerManager.getComponent((String)"userAccessor");
        }
        return this.userAccessor;
    }

    private DisabledUserManager getDisabledUserManager() {
        if (this.disabledUserManager == null && ContainerManager.isContainerSetup()) {
            this.disabledUserManager = (DisabledUserManager)ContainerManager.getComponent((String)"disabledUserManager");
        }
        return this.disabledUserManager;
    }
}

