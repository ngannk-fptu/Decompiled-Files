/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.PagePermissionsActionHelper;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class SetPagePermissionsAction
extends AbstractPageAwareAction
implements Beanable {
    protected Map<String, Object> bean = new HashMap<String, Object>();
    private List<String> viewPermissionsGroupList;
    private List<String> editPermissionsGroupList;
    private List<String> viewPermissionsUserList;
    private List<String> editPermissionsUserList;
    private String viewPermissionsGroups;
    private String viewPermissionsUsers;
    private String editPermissionsGroups;
    private String editPermissionsUsers;
    private ContentEntityManager contentEntityManager;
    private long contentId;

    public String setPagePermissions() throws Exception {
        return this.execute(this.getPage());
    }

    public String setContentPermissions() throws Exception {
        return this.execute(this.contentEntityManager.getById(this.contentId));
    }

    private String execute(ContentEntityObject content) {
        if (this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, content)) {
            List<ContentPermission> viewPermissions;
            PagePermissionsActionHelper permissionsHelper = new PagePermissionsActionHelper(this.getAuthenticatedUser(), this.getUserAccessor());
            if (StringUtils.isNotEmpty((CharSequence)this.viewPermissionsGroups) || StringUtils.isNotEmpty((CharSequence)this.viewPermissionsUsers) || StringUtils.isNotEmpty((CharSequence)this.editPermissionsGroups) || StringUtils.isNotEmpty((CharSequence)this.editPermissionsUsers)) {
                viewPermissions = permissionsHelper.createPermissions("View", this.viewPermissionsGroups, this.viewPermissionsUsers);
                List<ContentPermission> editPermissions = permissionsHelper.createPermissions("Edit", this.editPermissionsGroups, this.editPermissionsUsers);
                this.contentPermissionManager.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)"View", viewPermissions, (Object)"Edit", editPermissions), content);
            } else {
                viewPermissions = permissionsHelper.createPermissions("View", this.viewPermissionsGroupList, this.viewPermissionsUserList);
                List<ContentPermission> editPermissions = permissionsHelper.createPermissions("Edit", this.editPermissionsGroupList, this.editPermissionsUserList);
                this.contentPermissionManager.setContentPermissions((Map<String, Collection<ContentPermission>>)ImmutableMap.of((Object)"View", viewPermissions, (Object)"Edit", editPermissions), content);
            }
            boolean hasPermissions = content.hasContentPermissions() || !this.contentPermissionManager.getInheritedContentPermissionSets(content).isEmpty();
            this.bean.put("hasPermissions", hasPermissions);
            this.bean.put("restrictionsHash", PermissionUtils.getRestrictionsHash(content));
            return "success";
        }
        this.bean.put("errorMessage", this.getI18n().getText("permissions.edit.content.not.allowed"));
        return "error";
    }

    @Override
    public Map<String, Object> getBean() {
        return this.bean;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    public void setViewPermissionsGroups(String viewPermissionsGroups) {
        this.viewPermissionsGroups = viewPermissionsGroups;
    }

    public void setViewPermissionsUsers(String viewPermissionsUsers) {
        this.viewPermissionsUsers = viewPermissionsUsers;
    }

    public void setEditPermissionsGroups(String editPermissionsGroups) {
        this.editPermissionsGroups = editPermissionsGroups;
    }

    public void setEditPermissionsUsers(String editPermissionsUsers) {
        this.editPermissionsUsers = editPermissionsUsers;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public List<String> getViewPermissionsGroupList() {
        return this.viewPermissionsGroupList;
    }

    public void setViewPermissionsGroupList(List<String> viewPermissionsGroupList) {
        this.viewPermissionsGroupList = viewPermissionsGroupList;
    }

    public List<String> getEditPermissionsGroupList() {
        return this.editPermissionsGroupList;
    }

    public void setEditPermissionsGroupList(List<String> editPermissionsGroupList) {
        this.editPermissionsGroupList = editPermissionsGroupList;
    }

    public List<String> getViewPermissionsUserList() {
        return this.viewPermissionsUserList;
    }

    public void setViewPermissionsUserList(List<String> viewPermissionsUserList) {
        this.viewPermissionsUserList = viewPermissionsUserList;
    }

    public List<String> getEditPermissionsUserList() {
        return this.editPermissionsUserList;
    }

    public void setEditPermissionsUserList(List<String> editPermissionsUserList) {
        this.editPermissionsUserList = editPermissionsUserList;
    }
}

