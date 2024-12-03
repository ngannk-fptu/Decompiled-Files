/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.Entity
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.Entity;
import com.atlassian.user.Group;
import com.atlassian.user.User;

public class PermittedUserFinder {
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final PageManager pageManager;
    private final Page parentPage;
    private final Space space;

    public PermittedUserFinder(PageManager pageManager, PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, Page parentPage, Space space) {
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.parentPage = parentPage;
        this.space = space;
    }

    public SearchResult makeResult(Entity entity) {
        String permissionString = entity instanceof ConfluenceUser ? this.getPermissionForUser((ConfluenceUser)entity) : this.checkGroupExplicitlyPermitted((Group)entity);
        return new SearchResult(permissionString, entity);
    }

    private String checkGroupExplicitlyPermitted(Group group) {
        boolean isPermitted = this.spacePermissionManager.groupHasPermission("VIEWSPACE", this.space, group.getName());
        return isPermitted ? null : "This group does not have explicit permission to view the space, but some or all members may still have permission.";
    }

    private String getPermissionForUser(ConfluenceUser user) {
        boolean canNotViewParent;
        boolean canSeeSpace = this.spacePermissionManager.hasPermission("VIEWSPACE", this.space, user);
        if (!canSeeSpace) {
            return user.getName() + " cannot view the space '" + this.space.getName() + "'";
        }
        boolean bl = canNotViewParent = this.parentPage != null && !this.permissionManager.hasPermission((User)user, Permission.VIEW, this.parentPage);
        if (canNotViewParent) {
            return user.getName() + " cannot view the parent page '" + this.parentPage.getTitle() + "'";
        }
        boolean canEditSpace = this.spacePermissionManager.hasPermission("EDITSPACE", this.space, user);
        if (!canEditSpace) {
            return user.getName() + " can view the page but cannot edit the space '" + this.space.getName() + "'";
        }
        return null;
    }

    public static final class SearchResult {
        private final Entity entity;
        private final String report;

        public SearchResult(String report, Entity entity) {
            this.report = report;
            this.entity = entity;
        }

        public Entity getEntity() {
            return this.entity;
        }

        public String getReport() {
            return this.report;
        }
    }
}

