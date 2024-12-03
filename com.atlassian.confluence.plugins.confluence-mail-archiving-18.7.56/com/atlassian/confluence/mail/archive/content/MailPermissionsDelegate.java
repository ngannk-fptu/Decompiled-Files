/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.Content
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.Content;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.stereotype.Component;

@Component
public class MailPermissionsDelegate
extends AbstractPermissionsDelegate {
    private final ConfluenceAccessManager confluenceAccessManager;

    public MailPermissionsDelegate(@ComponentImport ConfluenceAccessManager confluenceAccessManager, @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public boolean canView(User user, Object target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target);
    }

    public boolean canView(User user) {
        return this.confluenceAccessManager.getUserAccessStatus((User)AuthenticatedUserThreadLocal.get()).canUseConfluence();
    }

    public boolean canEdit(User user, Object target) {
        throw new IllegalStateException("Editing privileges do not apply to mail");
    }

    public boolean canSetPermissions(User user, Object target) {
        throw new IllegalStateException("Permission-setting privileges do not apply to mail");
    }

    public boolean canRemove(User user, Object target) {
        return this.canView(user, target) && this.hasSpaceLevelPermission("REMOVEMAIL", user, target);
    }

    public boolean canExport(User user, Object target) {
        throw new IllegalStateException("Export privileges do not apply to mail");
    }

    public boolean canAdminister(User user, Object target) {
        throw new IllegalStateException("Administration privileges do not apply to mail");
    }

    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", (Space)container, user);
    }

    protected Space getSpaceFrom(Object target) {
        if (target instanceof Content) {
            target = ((Content)target).getEntity();
        }
        return ((SpaceContentEntityObject)target).getSpace();
    }
}

