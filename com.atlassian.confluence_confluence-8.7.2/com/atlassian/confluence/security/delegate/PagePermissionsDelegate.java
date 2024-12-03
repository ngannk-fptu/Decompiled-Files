/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.security.delegate;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public class PagePermissionsDelegate
extends AbstractPermissionsDelegate<Page> {
    private ContentPermissionManager contentPermissionManager;

    @Override
    public boolean canView(User user, Page target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target) && this.hasContentLevelViewPermission(user, target);
    }

    @Override
    public boolean canEdit(User user, Page target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target) && this.hasSpaceLevelPermission("EDITSPACE", user, target) && this.hasContentLevelEditPermission(user, target);
    }

    @Override
    public boolean canSetPermissions(User user, Page target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.canEdit(user, target) && this.hasSpaceLevelPermission("SETPAGEPERMISSIONS", user, target);
    }

    @Override
    public boolean canRemove(User user, Page target) {
        if (!this.hasSpaceLevelPermission("REMOVEPAGE", user, target) && !this.canRemoveOwn(target, user)) {
            return false;
        }
        if (!this.canView(user, target)) {
            return false;
        }
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.hasContentLevelEditPermission(user, target);
    }

    @Override
    public boolean canMove(User user, Page source, Object target, String movePoint) {
        boolean canEdit = this.canEdit(user, source);
        Page targetPage = (Page)target;
        boolean canCreateInSpace = this.canCreate(user, targetPage.getSpace());
        if (canEdit && canCreateInSpace) {
            if (!(source.isDraft() || targetPage.getSpace().equals(source.getSpace()) || this.canRemoveHierarchy(user, source))) {
                return false;
            }
            if ("below".equals(movePoint) || "above".equals(movePoint)) {
                if (targetPage.getParent() != null) {
                    return this.hasContentLevelViewPermission(user, targetPage.getParent());
                }
                return true;
            }
            if ("append".equals(movePoint)) {
                return this.hasContentLevelViewPermission(user, targetPage);
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveHierarchy(User user, Page target) {
        if (!this.canView(user, target)) {
            return false;
        }
        if (!this.hasSpaceLevelPermission("REMOVEPAGE", user, target)) {
            if (!this.canRemoveOwn(target, user)) {
                return false;
            }
            for (Page page : target.getChildren()) {
                if (this.canRemoveOwn(page, user) && this.canRemoveHierarchy(user, page)) continue;
                return false;
            }
        }
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.hasContentLevelEditPermission(user, target);
    }

    private boolean canRemoveOwn(Object target, User user) {
        Page page = (Page)target;
        boolean isCreator = page != null && page.getCreator() != null && user != null && user.getName() != null && user.getName().equals(page.getCreator().getName());
        return isCreator && this.hasSpaceLevelPermission("REMOVEOWNCONTENT", user, target);
    }

    @Override
    public boolean canExport(User user, Page target) {
        return this.canView(user, target);
    }

    @Override
    public boolean canAdminister(User user, Page target) {
        throw new IllegalStateException("Administration privileges do not apply to pages");
    }

    private boolean hasContentLevelViewPermission(User user, Object target) {
        return this.contentPermissionManager.hasContentLevelPermission(user, "View", (ContentEntityObject)target);
    }

    private boolean hasContentLevelEditPermission(User user, Object target) {
        return this.contentPermissionManager.hasContentLevelPermission(user, "Edit", (ContentEntityObject)target);
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", (Space)container, user) && this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", (Space)container, user);
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        return ((AbstractPage)target).getSpace();
    }
}

