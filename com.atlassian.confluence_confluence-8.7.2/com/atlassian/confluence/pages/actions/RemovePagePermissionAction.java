/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.ActionHelper;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.Permission;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RemovePagePermissionAction
extends AbstractPageAwareAction {
    private long permissionId;
    private boolean removeAll;
    private String returnPath;

    public void setPermissionId(long permissionId) {
        this.permissionId = permissionId;
    }

    public void setRemoveAll(boolean removeAll) {
        this.removeAll = removeAll;
    }

    public String getReturnPath() {
        return this.returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String execute() {
        if (this.removeAll) {
            this.contentPermissionManager.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)"View", (Object)Collections.EMPTY_LIST, (Object)"Edit", (Object)Collections.EMPTY_LIST), this.getPage());
        } else {
            ContentPermission permission = this.getPermissionFromPage(this.getPage(), this.permissionId);
            if (permission != null) {
                this.contentPermissionManager.removeContentPermission(permission);
            }
        }
        if (StringUtils.isNotEmpty((CharSequence)this.returnPath)) {
            return "returnPath";
        }
        return "success";
    }

    private ContentPermission getPermissionFromPage(AbstractPage page, long permissionId) {
        for (ContentPermission o : page.getPermissions()) {
            ContentPermission permission = o;
            if (permission.getId() != permissionId) continue;
            return permission;
        }
        return null;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    @Override
    public boolean isPermitted() {
        return this.isSpaceAdmin() || this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, this.getPage()) && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getPage());
    }

    private boolean isSpaceAdmin() {
        return ActionHelper.isSpaceAdmin(this.getSpace(), this.getAuthenticatedUser(), this.spacePermissionManager);
    }
}

