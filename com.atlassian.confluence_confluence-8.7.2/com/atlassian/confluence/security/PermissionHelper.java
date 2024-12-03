/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionHelper {
    private static final Logger log = LoggerFactory.getLogger(PermissionHelper.class);
    private final PermissionManager permissionManager;
    private final PersonalInformationManager personalInformationManager;
    private final PageManager pageManager;

    public PermissionHelper(PermissionManager permissionManager, PersonalInformationManager personalInformationManager, PageManager pageManager) {
        this.permissionManager = permissionManager;
        this.personalInformationManager = personalInformationManager;
        this.pageManager = pageManager;
    }

    public boolean canEdit(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.EDIT, object);
    }

    public boolean canView(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, object);
    }

    public boolean canRemove(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.REMOVE, object);
    }

    public boolean canRemoveHierarchy(User user, Object target) {
        return this.permissionManager.hasRemoveHierarchyPermission(user, target);
    }

    public boolean canExport(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.EXPORT, object);
    }

    public boolean canComment(User user, Object object) {
        return this.permissionManager.hasCreatePermission(user, object, Comment.class);
    }

    public boolean canAttachFile(User user, Object object) {
        return this.permissionManager.hasCreatePermission(user, object, Attachment.class);
    }

    public boolean isConfluenceAdministrator(User user) {
        return this.permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public boolean isSystemAdministrator(User user) {
        return this.permissionManager.hasPermission(user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public boolean canCreatePage(User user, Space space) {
        return this.permissionManager.hasCreatePermission(user, (Object)space, Page.class);
    }

    public boolean canCreateBlogPost(User user, Space space) {
        return this.permissionManager.hasCreatePermission(user, (Object)space, BlogPost.class);
    }

    public boolean canAdminister(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.ADMINISTER, object);
    }

    public boolean canSetPermissions(User user, Object object) {
        return this.permissionManager.hasPermission(user, Permission.SET_PERMISSIONS, object);
    }

    public boolean canCreateSpace(User user) {
        return this.permissionManager.hasCreatePermission(user, PermissionManager.TARGET_APPLICATION, Space.class);
    }

    public boolean canViewPage(User user, String spaceKey, String pageTitle) {
        return this.canView(user, this.pageManager.getPage(spaceKey, pageTitle));
    }

    public boolean canViewPage(User user, long id) {
        return this.canView(user, this.pageManager.getPage(id));
    }

    public boolean canCreatePersonalSpace(User user) {
        if (user == null) {
            return false;
        }
        return this.permissionManager.hasCreatePermission(user, (Object)this.personalInformationManager.getOrCreatePersonalInformation(user), Space.class);
    }

    public boolean isGlobalAnonymousAccessEnabled() {
        return this.canView(null, PermissionManager.TARGET_APPLICATION);
    }
}

