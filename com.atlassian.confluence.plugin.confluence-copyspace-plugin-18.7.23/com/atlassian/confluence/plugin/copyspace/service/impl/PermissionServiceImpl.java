/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceException;
import com.atlassian.confluence.plugin.copyspace.service.PermissionService;
import com.atlassian.confluence.plugin.copyspace.util.MetadataCopier;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="permissionCheckServiceImpl")
public class PermissionServiceImpl
implements PermissionService {
    public static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);
    private final PermissionManager permissionManager;
    private final AccessModeService accessModeService;
    private final SpacePermissionManager spacePermissionManager;
    private final PageManager pageManager;

    @Autowired
    public PermissionServiceImpl(@ComponentImport PermissionManager permissionManager, @ComponentImport AccessModeService accessModeService, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.accessModeService = accessModeService;
        this.spacePermissionManager = spacePermissionManager;
        this.pageManager = pageManager;
    }

    @Override
    public boolean canInitiateSpaceCopy(User user, Space space) {
        boolean canCreateSpaces = (Boolean)this.accessModeService.withReadOnlyAccessExemption(() -> this.permissionManager.hasCreatePermission(user, PermissionManager.TARGET_APPLICATION, Space.class));
        boolean canAdministerThisSpace = this.permissionManager.hasPermission(user, Permission.ADMINISTER, (Object)space);
        return canCreateSpaces && canAdministerThisSpace;
    }

    @Override
    public boolean canViewSpace(User user, Space space) {
        boolean hasViewPermission = this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space);
        if (!hasViewPermission) {
            log.debug("User {} doesn't have view permission for space {}", (Object)user, (Object)space);
        }
        return hasViewPermission;
    }

    @Override
    public void copySpacePermissions(Space originalSpace, Space newSpace, Boolean keepMetaData) {
        for (SpacePermission originalPermission : originalSpace.getPermissions()) {
            try {
                this.copyPermission(newSpace, keepMetaData, originalPermission);
            }
            catch (Exception e) {
                log.error("Cannot copy space permission", (Throwable)e);
            }
        }
    }

    @Override
    public boolean checkIfWatcherHasViewPermission(ConfluenceUser user, ContentId contentId) {
        ContentEntityObject originContent = this.pageManager.getById(contentId.asLong());
        boolean hasViewPermission = this.permissionManager.hasPermission((User)user, Permission.VIEW, (Object)originContent);
        if (!hasViewPermission) {
            log.debug("User {} doesn't have view permission for origin content {}", (Object)user, (Object)contentId);
        }
        return hasViewPermission;
    }

    private void copyPermission(Space newSpace, Boolean keepMetaData, SpacePermission originalPermission) {
        SpacePermission spacePermission;
        if (originalPermission.isUserPermission()) {
            spacePermission = SpacePermission.createUserSpacePermission((String)originalPermission.getType(), (Space)newSpace, (ConfluenceUser)originalPermission.getUserSubject());
        } else if (originalPermission.isGroupPermission()) {
            spacePermission = SpacePermission.createGroupSpacePermission((String)originalPermission.getType(), (Space)newSpace, (String)originalPermission.getGroup());
        } else if (originalPermission.isAnonymousPermission()) {
            spacePermission = SpacePermission.createAnonymousSpacePermission((String)originalPermission.getType(), (Space)newSpace);
        } else if (originalPermission.isAuthenticatedUsersPermission()) {
            spacePermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)originalPermission.getType(), (Space)newSpace);
        } else {
            throw new CopySpaceException("Permission type didn't identify. Permission id:" + originalPermission.getId());
        }
        newSpace.addPermission(spacePermission);
        this.spacePermissionManager.savePermission(spacePermission);
        if (keepMetaData.booleanValue()) {
            MetadataCopier.copyEntityMetadata((ConfluenceEntityObject)originalPermission, (ConfluenceEntityObject)spacePermission);
        } else {
            spacePermission.setCreator(newSpace.getCreator());
            spacePermission.setCreationDate(new Date());
            spacePermission.setLastModifier(newSpace.getCreator());
            spacePermission.setLastModificationDate(new Date());
        }
    }
}

