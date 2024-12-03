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
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.security.delegate.AbstractPermissionsDelegate;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;

public class BlogPostPermissionsDelegate
extends AbstractPermissionsDelegate<BlogPost> {
    private ContentPermissionManager contentPermissionManager;

    @Override
    public boolean canView(User user, BlogPost target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target) && this.hasContentLevelViewPermission(user, target);
    }

    @Override
    public boolean canEdit(User user, BlogPost target) {
        return this.hasSpaceLevelPermission("VIEWSPACE", user, target) && this.hasSpaceLevelPermission("EDITBLOG", user, target) && this.hasContentLevelEditPermission(user, target);
    }

    @Override
    public boolean canSetPermissions(User user, BlogPost target) {
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.canEdit(user, target) && this.hasSpaceLevelPermission("SETPAGEPERMISSIONS", user, target);
    }

    @Override
    public boolean canRemove(User user, BlogPost target) {
        if (!this.hasSpaceLevelPermission("REMOVEBLOG", user, target) && !this.canRemoveOwn(target, user)) {
            return false;
        }
        if (!this.canView(user, target)) {
            return false;
        }
        return this.hasSpaceLevelPermission("SETSPACEPERMISSIONS", user, target) || this.hasContentLevelEditPermission(user, target);
    }

    @Override
    public boolean canMove(User user, BlogPost source, Object target, String movePoint) {
        return this.canRemove(user, source) && this.canCreate(user, target);
    }

    @Override
    public boolean canRemoveHierarchy(User user, BlogPost target) {
        return this.canRemove(user, target);
    }

    private boolean canRemoveOwn(Object target, User user) {
        BlogPost blog = (BlogPost)target;
        boolean isCreator = blog != null && blog.getCreator() != null && user != null && user.getName() != null && user.getName().equals(blog.getCreator().getName());
        return isCreator && this.hasSpaceLevelPermission("REMOVEOWNCONTENT", user, target);
    }

    @Override
    public boolean canExport(User user, BlogPost target) {
        throw new IllegalStateException("Export privileges do not apply to blog posts");
    }

    @Override
    public boolean canAdminister(User user, BlogPost target) {
        throw new IllegalStateException("Administration privileges do not apply to blog posts");
    }

    @Override
    protected Space getSpaceFrom(Object target) {
        return ((AbstractPage)target).getSpace();
    }

    @Override
    public boolean canCreate(User user, Object container) {
        return this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", (Space)container, user) && this.spacePermissionManager.hasPermissionNoExemptions("EDITBLOG", (Space)container, user);
    }

    private boolean hasContentLevelViewPermission(User user, Object target) {
        return this.contentPermissionManager.hasContentLevelPermission(user, "View", (ContentEntityObject)target);
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    private boolean hasContentLevelEditPermission(User user, Object target) {
        return this.contentPermissionManager.hasContentLevelPermission(user, "Edit", (ContentEntityObject)target);
    }
}

