/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceVersion;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public class Permissions {
    public static final List<Permission> ALL_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> VIEW_RESTRICTION_DEPENDENT_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> EDIT_RESTRICTION_DEPENDENT_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN = new ArrayList<Permission>();
    public static final List<Permission> SPACE_ADMIN_DEPENDENT_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> SUPER_USER_DEPENDENT_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> SUPER_USER_DEPENDENT_PERMISSIONS_EXCEPT_EDIT = new ArrayList<Permission>();
    public static final List<Permission> ANONYMOUS_PERMISSIONS = new ArrayList<Permission>();
    public static final List<Permission> VIEW_RESTRICTION_ONLY_DEPENDENT_PERMISSIONS = ImmutableList.of((Object)Permission.VIEW_SPACE_PERMISSION, (Object)Permission.COMMENT_PERMISSION, (Object)Permission.REMOVE_COMMENT_PERMISSION);
    public static final int ALL_FULL_PERMISSIONS_FLAG;

    public static Permission getPermissionByType(String permissionType) {
        for (Permission p : ALL_PERMISSIONS) {
            if (!permissionType.equals(p.getType())) continue;
            return p;
        }
        return null;
    }

    static {
        ALL_PERMISSIONS.add(Permission.VIEW_SPACE_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_OWN_CONTENT_PERMISSION);
        ALL_PERMISSIONS.add(Permission.CREATE_EDIT_PAGE_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_PAGE_PERMISSION);
        ALL_PERMISSIONS.add(Permission.EDIT_BLOG_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_BLOG_PERMISSION);
        ALL_PERMISSIONS.add(Permission.COMMENT_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_COMMENT_PERMISSION);
        ALL_PERMISSIONS.add(Permission.CREATE_ATTACHMENT_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_ATTACHMENT_PERMISSION);
        ALL_PERMISSIONS.add(Permission.REMOVE_MAIL_PERMISSION);
        ALL_PERMISSIONS.add(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
        ALL_PERMISSIONS.add(Permission.EXPORT_SPACE_PERMISSION);
        ALL_PERMISSIONS.add(Permission.ADMINISTER_SPACE_PERMISSION);
        int allPermissionsFlag = 0;
        for (Permission permission : ALL_PERMISSIONS) {
            int flag = permission.getFlag();
            allPermissionsFlag += flag;
        }
        if (!ConfluenceVersion.isDeleteOwnPermissionSupported()) {
            allPermissionsFlag -= Permission.REMOVE_OWN_CONTENT_PERMISSION.getFlag();
        }
        ALL_FULL_PERMISSIONS_FLAG = allPermissionsFlag;
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.REMOVE_OWN_CONTENT_PERMISSION);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.CREATE_EDIT_PAGE_PERMISSION);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.REMOVE_PAGE_PERMISSION);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.CREATE_ATTACHMENT_PERMISSION);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.REMOVE_ATTACHMENT_PERMISSION);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.addAll(VIEW_RESTRICTION_ONLY_DEPENDENT_PERMISSIONS);
        VIEW_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN.add(Permission.CREATE_EDIT_PAGE_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN.add(Permission.CREATE_ATTACHMENT_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN.add(Permission.REMOVE_ATTACHMENT_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN.add(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS.addAll(EDIT_RESTRICTION_DEPENDENT_PERMISSIONS_SPACE_ADMIN);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.REMOVE_OWN_CONTENT_PERMISSION);
        EDIT_RESTRICTION_DEPENDENT_PERMISSIONS.add(Permission.REMOVE_PAGE_PERMISSION);
        SUPER_USER_DEPENDENT_PERMISSIONS.addAll(ALL_PERMISSIONS);
        SUPER_USER_DEPENDENT_PERMISSIONS_EXCEPT_EDIT.addAll(ALL_PERMISSIONS);
        SUPER_USER_DEPENDENT_PERMISSIONS_EXCEPT_EDIT.remove(Permission.CREATE_EDIT_PAGE_PERMISSION);
        SPACE_ADMIN_DEPENDENT_PERMISSIONS.addAll(ALL_PERMISSIONS);
        SPACE_ADMIN_DEPENDENT_PERMISSIONS.remove(Permission.ADMINISTER_SPACE_PERMISSION);
        SPACE_ADMIN_DEPENDENT_PERMISSIONS.remove(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
        ANONYMOUS_PERMISSIONS.addAll(ALL_PERMISSIONS);
        ANONYMOUS_PERMISSIONS.remove(Permission.ADMINISTER_SPACE_PERMISSION);
        ANONYMOUS_PERMISSIONS.remove(Permission.SET_PAGE_PERMISSIONS_PERMISSION);
    }
}

