/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.Group
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.User
 *  com.atlassian.user.UserManager
 *  com.atlassian.user.impl.DefaultGroup
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.security.EntityRuntimeException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.user.UnknownUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.User;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.DefaultGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSetSpacePermissionChecker
implements SetSpacePermissionChecker {
    private static final Logger log = LoggerFactory.getLogger(DefaultSetSpacePermissionChecker.class);
    private PermissionManager permissionManager;
    private BootstrapManager bootstrapManager;
    private UserManager userManager;
    private GroupManager groupManager;
    private I18NBeanFactory i18NBeanFactory;

    @Override
    public boolean canSetPermission(User user, SpacePermission spacePermission) {
        if (!this.bootstrapManager.isSetupComplete()) {
            return true;
        }
        if ("SYSTEMADMINISTRATOR".equalsIgnoreCase(spacePermission.getType())) {
            return this.permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
        }
        if (spacePermission.isSpacePermission()) {
            return this.permissionManager.hasPermission(user, Permission.SET_PERMISSIONS, spacePermission.getSpace()) || this.permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
        }
        if (spacePermission.isGlobalPermission()) {
            if (spacePermission.isUserPermission()) {
                User spacePermissionUser;
                try {
                    spacePermissionUser = this.userManager.getUser(spacePermission.getUserName());
                }
                catch (EntityException e) {
                    throw new EntityRuntimeException("Error occurred trying to fetch the user in " + spacePermission, e);
                }
                if (spacePermissionUser == null) {
                    log.warn("User declared in {} does not exist.", (Object)spacePermission);
                    spacePermissionUser = UnknownUser.unknownUser(spacePermission.getUserName(), this.i18NBeanFactory.getI18NBean());
                }
                return this.permissionManager.hasPermission(user, Permission.SET_PERMISSIONS, spacePermissionUser);
            }
            if (spacePermission.isGroupPermission()) {
                Group spacePermissionGroup;
                try {
                    spacePermissionGroup = this.groupManager.getGroup(spacePermission.getGroup());
                }
                catch (EntityException e) {
                    throw new EntityRuntimeException("Error occurred trying to fetch the group in " + spacePermission, e);
                }
                if (spacePermissionGroup == null) {
                    log.warn("Group declared in {} does not exist.", (Object)spacePermission);
                    spacePermissionGroup = new DefaultGroup(spacePermission.getGroup());
                }
                return this.permissionManager.hasPermission(user, Permission.SET_PERMISSIONS, spacePermissionGroup);
            }
        }
        return this.permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void setI18NBeanFactory(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

