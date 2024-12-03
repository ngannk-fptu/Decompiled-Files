/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.security.DefaultPermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class CommentPermissionsDelegate
extends AbstractPermissionsDelegate<Comment> {
    private PermissionManager permissionManager;

    @Override
    public boolean canView(User user, Comment target) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, target.getContainer());
    }

    @Override
    public boolean canEdit(User user, Comment target) {
        if (user == null) {
            return false;
        }
        Comment comment = target;
        return this.canCreate(user, comment.getContainer()) && (StringUtils.equals((CharSequence)user.getName(), (CharSequence)comment.getCreatorName()) || this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target));
    }

    @Override
    public boolean canSetPermissions(User user, Comment target) {
        throw new IllegalStateException("Permission-setting privileges do not apply to comments");
    }

    @Override
    public boolean canRemove(User user, Comment target) {
        if (!this.canView(user, target)) {
            return false;
        }
        if (this.hasSpaceLevelPermission("REMOVECOMMENT", user, target)) {
            return true;
        }
        if (user == null) {
            return false;
        }
        return StringUtils.equals((CharSequence)user.getName(), (CharSequence)target.getCreatorName()) && target.getChildren().isEmpty();
    }

    @Override
    public boolean canExport(User user, Comment target) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.EXPORT, target.getContainer());
    }

    @Override
    public boolean canAdminister(User user, Comment target) {
        throw new IllegalStateException("Administration privileges do not apply to comments");
    }

    @Deprecated
    public void setPermissionManagerTarget(DefaultPermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.permissionManager.hasPermissionNoExemptions(user, Permission.VIEW, container) && this.hasSpaceLevelPermission("COMMENT", user, container);
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        Space space;
        if (target instanceof SpaceContentEntityObject) {
            space = ((SpaceContentEntityObject)target).getSpace();
        } else if (target instanceof Comment) {
            space = ((Comment)target).getSpace();
        } else if (target instanceof Space) {
            space = (Space)target;
        } else if (target instanceof Contained) {
            space = this.getSpaceFrom(((Contained)target).getContainer());
        } else {
            throw new IllegalArgumentException("Unsupported container for create comment permission check: " + target.getClass());
        }
        return space;
    }
}

