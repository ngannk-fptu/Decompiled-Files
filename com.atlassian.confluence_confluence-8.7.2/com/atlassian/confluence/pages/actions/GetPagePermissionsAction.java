/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Entity
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultGroup
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PermittedUserFinder;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Entity;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultGroup;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetPagePermissionsAction
extends AbstractPageAwareAction
implements Beanable,
SpaceAware {
    private static final Logger log = LoggerFactory.getLogger(GetPagePermissionsAction.class);
    private static final String PERMISSION_ENTITY_USER = "user";
    private static final String PERMISSION_ENTITY_GROUP = "group";
    protected Map<String, Object> bean = Maps.newHashMap();
    private Set<UserKey> userKeys = Sets.newHashSet();
    private Set<String> groupNames = Sets.newHashSet();
    private PageManager pageManager;
    private Space space;
    private long parentPageId;
    private String parentPageTitle;
    private ContentEntityManager contentEntityManager;
    private long contentId;

    @Override
    public boolean isPermitted() {
        return !this.isAnonymousUser() && super.isPermitted();
    }

    @Deprecated
    @PermittedMethods(value={HttpMethod.GET})
    public String getPagePermissions() throws Exception {
        if (this.getPage() == null) {
            this.bean.put("errorMessage", this.getI18n().getText("permissions.content.not.found"));
            return "error";
        }
        return this.execute(this.getPage());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String getContentPermissions() throws Exception {
        ContentEntityObject content = this.contentEntityManager.getById(this.contentId);
        if (content == null) {
            this.bean.put("errorMessage", this.getI18n().getText("permissions.content.not.found"));
            return "error";
        }
        return this.execute(content);
    }

    @Deprecated
    public String getEditPagePermissions() throws Exception {
        return this.getPagePermissions();
    }

    private String execute(ContentEntityObject content) throws Exception {
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, content)) {
            this.bean.put("error", this.getI18n().getText("permissions.view.content.not.allowed"));
            return "error";
        }
        Page parentPage = this.getParentPage();
        PermittedUserFinder finder = new PermittedUserFinder(this.pageManager, this.permissionManager, this.spacePermissionManager, parentPage, this.space);
        this.bean.put("permissions", this.getPermissions(content));
        this.bean.put("users", this.getUsers(content, finder));
        this.bean.put("groups", this.getGroups(content, finder));
        this.bean.put("userCanEditRestrictions", this.hasSetPagePermissionsPermission(content));
        this.bean.put("restrictionsHash", PermissionUtils.getRestrictionsHash(content));
        return "success";
    }

    private Map<String, PermittedUserFinder.SearchResult> getGroups(ContentEntityObject content, PermittedUserFinder finder) {
        HashMap groups = Maps.newHashMap();
        for (String groupName : this.groupNames) {
            Group group = this.userAccessor.getGroup(groupName);
            if (group == null) {
                log.warn("Group with name '" + groupName + "' not found, but page permissions still exist on: " + content);
                groups.put(groupName, new PermittedUserFinder.SearchResult(null, (Entity)new DefaultGroup(groupName)));
                continue;
            }
            groups.put(groupName, finder.makeResult((Entity)group));
        }
        return groups;
    }

    private Map<String, PermittedUserFinder.SearchResult> getUsers(ContentEntityObject content, PermittedUserFinder finder) {
        HashMap users = Maps.newHashMap();
        for (UserKey userKey : this.userKeys) {
            ConfluenceUser user = this.userAccessor.getUserByKey(userKey);
            if (user == null) {
                log.warn("User with key '" + userKey + "' not found; page permissions for that user will be removed from: " + content);
                continue;
            }
            users.put(userKey.getStringValue(), finder.makeResult((Entity)user));
        }
        return users;
    }

    private List<List<String>> getPermissions(ContentEntityObject content) {
        ArrayList<List<String>> permissions = new ArrayList<List<String>>();
        permissions.addAll(this.getContentPermissions("View", content));
        if (content.isDraft() && content.isUnpublished()) {
            permissions.addAll(this.getContentPermissions("View", this.getParentPage()));
        }
        permissions.addAll(this.getContentPermissions("Edit", content));
        return permissions;
    }

    private Page getParentPage() {
        if (StringUtils.isNotBlank((CharSequence)this.parentPageTitle)) {
            return this.pageManager.getPage(this.space.getKey(), this.parentPageTitle);
        }
        if (this.parentPageId != 0L) {
            return this.pageManager.getPage(this.parentPageId);
        }
        return null;
    }

    private List<List<String>> getContentPermissions(String permissionType, ContentEntityObject content) {
        ArrayList<List<String>> permissions = new ArrayList<List<String>>();
        List<ContentPermissionSet> permissionSets = this.contentPermissionManager.getContentPermissionSets(content, permissionType);
        for (ContentPermissionSet permissionSet : permissionSets) {
            ContentEntityObject owningContent = permissionSet.getOwningContent();
            String owningContentId = owningContent.getIdAsString();
            String owningContentTitle = owningContent.getTitle();
            for (String name : permissionSet.getGroupNames()) {
                permissions.add(Arrays.asList(permissionType, PERMISSION_ENTITY_GROUP, name, owningContentId, owningContentTitle));
                this.groupNames.add(name);
            }
            for (UserKey userKey : permissionSet.getUserKeys()) {
                permissions.add(Arrays.asList(permissionType, PERMISSION_ENTITY_USER, userKey.getStringValue(), owningContentId, owningContentTitle));
                this.userKeys.add(userKey);
            }
        }
        return permissions;
    }

    private boolean hasSetPagePermissionsPermission(ContentEntityObject content) {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.SET_PERMISSIONS, content);
    }

    @Override
    public Map<String, Object> getBean() {
        return this.bean;
    }

    @Override
    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    public void setParentPageId(long parentPageId) {
        this.parentPageId = parentPageId;
    }

    public void setParentPageTitle(String parentPageTitle) {
        this.parentPageTitle = parentPageTitle;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
}

