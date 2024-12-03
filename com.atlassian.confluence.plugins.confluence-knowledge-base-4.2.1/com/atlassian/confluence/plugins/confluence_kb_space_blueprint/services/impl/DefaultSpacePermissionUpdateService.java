/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.impl;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SpacePermissionUpdateResult;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SpacePermissionUpdateService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class DefaultSpacePermissionUpdateService
implements SpacePermissionUpdateService {
    private static final Logger log = LoggerFactory.getLogger(DefaultSpacePermissionUpdateService.class);
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final TransactionTemplate transactionTemplate;

    public DefaultSpacePermissionUpdateService(PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, I18NBeanFactory i18NBeanFactory, TransactionTemplate transactionTemplate) {
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public SpacePermissionUpdateResult setEnableAnonymousViewSpace(ConfluenceUser user, Space space, boolean setEnabled, boolean skipPermissionChecks) {
        SpacePermission anonymousViewPermission = SpacePermission.createAnonymousSpacePermission((String)"VIEWSPACE", (Space)space);
        if (!this.canModifySpacePermissions(user, space) && !skipPermissionChecks) {
            return SpacePermissionUpdateResult.error(this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.plugins.confluence-knowledge-base.not.permitted.space.permissions"));
        }
        if (skipPermissionChecks) {
            log.debug("Skipping permission check while updating anonymous view permission for space with key: " + space.getKey());
            this.permissionManager.withExemption(() -> this.setEnablePermission(anonymousViewPermission, setEnabled));
        } else {
            this.setEnablePermission(anonymousViewPermission, setEnabled);
        }
        log.debug((setEnabled ? "Enabled" : "Disabled") + " anonymous view permission to space with key: " + space.getKey());
        return SpacePermissionUpdateResult.success();
    }

    @Override
    public SpacePermissionUpdateResult setEnableUnlicensedViewSpace(ConfluenceUser user, Space space, boolean setEnabled, boolean skipPermissionChecks) {
        SpacePermission unlicensedAuthenticatedViewPermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)"VIEWSPACE", (Space)space);
        if (!this.canModifySpacePermissions(user, space) && !skipPermissionChecks) {
            return SpacePermissionUpdateResult.error(this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.plugins.confluence-knowledge-base.not.permitted.space.permissions"));
        }
        if (skipPermissionChecks) {
            log.debug("Skipping permission check while updating unlicensed view permission for space with key: " + space.getKey());
            this.permissionManager.withExemption(() -> this.setEnablePermission(unlicensedAuthenticatedViewPermission, setEnabled));
        } else {
            this.setEnablePermission(unlicensedAuthenticatedViewPermission, setEnabled);
        }
        log.debug((setEnabled ? "Enabled" : "Disabled") + " unlicensed view permission to space with key: " + space.getKey());
        return SpacePermissionUpdateResult.success();
    }

    @Override
    public SpacePermissionUpdateResult setEnableGlobalUnlicensedAccess(ConfluenceUser user, boolean setEnabled, boolean skipPermissionChecks) {
        SpacePermission unlicensedGlobalAccessPermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)"LIMITEDUSECONFLUENCE", null);
        if (!this.canModifyGlobalPermissions(user) && !skipPermissionChecks) {
            return SpacePermissionUpdateResult.error(this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.plugins.confluence-knowledge-base.not.permitted.global.permissions"));
        }
        if (skipPermissionChecks) {
            log.debug("Skipping permission check while updating global unlicensed access to Confluence.");
            this.permissionManager.withExemption(() -> this.setEnablePermission(unlicensedGlobalAccessPermission, setEnabled));
        } else {
            this.setEnablePermission(unlicensedGlobalAccessPermission, setEnabled);
        }
        log.debug((setEnabled ? "Enabled" : "Disabled") + " global unlicensed access to Confluence.");
        return SpacePermissionUpdateResult.success();
    }

    private boolean canModifySpacePermissions(ConfluenceUser user, Space space) {
        return this.permissionManager.hasPermission((User)user, Permission.SET_PERMISSIONS, (Object)space) || this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    private boolean canModifyGlobalPermissions(ConfluenceUser user) {
        return this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @VisibleForTesting
    protected void setEnablePermission(SpacePermission constructedPermission, boolean setEnabled) {
        boolean currentlyEnabled = this.spacePermissionManager.permissionExists(constructedPermission);
        if (setEnabled && !currentlyEnabled) {
            this.transactionTemplate.execute(() -> {
                this.spacePermissionManager.savePermission(constructedPermission);
                return null;
            });
        } else if (!setEnabled && currentlyEnabled) {
            this.transactionTemplate.execute(() -> {
                this.spacePermissionManager.removePermission(this.getLiveHibernatePermission(constructedPermission));
                return null;
            });
        }
    }

    private SpacePermission getLiveHibernatePermission(SpacePermission constructedPermission) {
        Space space = constructedPermission.getSpace();
        List existingPerms = space != null ? space.getPermissions() : this.spacePermissionManager.getGlobalPermissions();
        return existingPerms.stream().filter(arg_0 -> ((SpacePermission)constructedPermission).equals(arg_0)).findFirst().orElseThrow(() -> new RuntimeException("Permission exists but could not retrieve permission object: " + constructedPermission));
    }
}

