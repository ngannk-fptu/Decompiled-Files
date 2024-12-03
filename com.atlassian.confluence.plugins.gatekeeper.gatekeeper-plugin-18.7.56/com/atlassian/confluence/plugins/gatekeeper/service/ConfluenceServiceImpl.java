/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.ContentPermission
 *  com.atlassian.confluence.security.ContentPermissionSet
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SetSpacePermissionChecker
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.administrators.EditPermissionsAdministrator
 *  com.atlassian.confluence.security.administrators.PermissionsAdministratorBuilder
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.actions.ProfilePictureInfo
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.gatekeeper.exception.PermissionModificationException;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.page.RestrictionType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SetSpacePermissionChecker;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.EditPermissionsAdministrator;
import com.atlassian.confluence.security.administrators.PermissionsAdministratorBuilder;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="confluenceService")
public class ConfluenceServiceImpl
implements ConfluenceService {
    private static final Logger logger = LoggerFactory.getLogger(ConfluenceServiceImpl.class);
    private final CrowdService crowdService;
    private final ContentPermissionManager contentPermissionManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;
    private final HelpPathResolver helpPathResolver;
    private PermissionsAdministratorBuilder permissionsAdministratorBuilder;
    private SetSpacePermissionChecker setSpacePermissionChecker;

    @Autowired
    public ConfluenceServiceImpl(@ComponentImport CrowdService crowdService, @ComponentImport ContentPermissionManager contentPermissionManager, @ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport UserAccessor userAccessor, @ComponentImport HelpPathResolver helpPathResolver) {
        this.crowdService = crowdService;
        this.contentPermissionManager = contentPermissionManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.userAccessor = userAccessor;
        this.helpPathResolver = helpPathResolver;
        ContainerManager.autowireComponent((Object)this);
    }

    @Override
    public boolean canCurrentUserViewSpace(String spaceKey) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(spaceKey);
        return space != null && this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)currentUser);
    }

    @Override
    public boolean canCurrentUserViewPage(String spaceKey, long pageId) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Page page = this.pageManager.getPage(pageId);
        return page != null && page.getSpaceKey().equals(spaceKey) && this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, (Object)page);
    }

    @Override
    public boolean canCurrentUserSetPermissions(String spaceKey) {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return false;
        }
        SpacePermission spacePermission = new SpacePermission("VIEWSPACE", space);
        return this.setSpacePermissionChecker.canSetPermission((User)currentUser, spacePermission);
    }

    @Override
    public List<PageRestriction> getViewRestrictions(long id) {
        Page page = this.pageManager.getPage(id);
        return this.getPageRestrictions(page, "View");
    }

    @Override
    public List<PageRestriction> getEditRestrictions(long id) {
        Page page = this.pageManager.getPage(id);
        return this.getPageRestrictions(page, "Edit");
    }

    @Override
    public List<PageRestriction> getPageRestrictions(Page page, String type) {
        List permissionSetList = this.contentPermissionManager.getContentPermissionSets((ContentEntityObject)page, type);
        ArrayList<PageRestriction> result = new ArrayList<PageRestriction>(0);
        for (ContentPermissionSet permissionSet : permissionSetList) {
            boolean inheritedRestriction;
            ArrayList<String> groups = new ArrayList<String>(0);
            groups.addAll(permissionSet.getGroupNames());
            ArrayList<String> users = new ArrayList<String>(0);
            for (ContentPermission contentPermission : permissionSet) {
                if (!contentPermission.isUserPermission()) continue;
                String username = contentPermission.getUserSubject().getName().toLowerCase();
                users.add(username);
            }
            ContentEntityObject ceo = permissionSet.getOwningContent();
            TinyPage ownerPage = this.fromPage(ceo);
            boolean bl = inheritedRestriction = ownerPage.getId() != page.getId();
            RestrictionType restrictionType = "Edit".equals(type) ? RestrictionType.EDIT_RESTRICTION : (inheritedRestriction ? RestrictionType.INHERITED_VIEW_RESTRICTION : RestrictionType.EXPLICIT_VIEW_RESTRICTION);
            result.add(new PageRestriction(restrictionType, ownerPage, users, groups));
        }
        return result;
    }

    @Override
    public TinyPage getPage(String spaceKey, long id) {
        if (id == 0L) {
            return null;
        }
        Page page = this.pageManager.getPage(id);
        if (page == null) {
            logger.error("The page with id {} is not found", (Object)id);
            return null;
        }
        if (!spaceKey.equals(page.getSpaceKey())) {
            logger.error("Get page {} space mismatch: {} in space: {}", new Object[]{id, page.getSpaceKey(), spaceKey});
            return null;
        }
        return this.fromPage((ContentEntityObject)page);
    }

    @Override
    public TinyPage getPage(String spaceKey, String title) {
        Page page = this.pageManager.getPage(spaceKey, title.trim());
        return page != null ? this.fromPage((ContentEntityObject)page) : null;
    }

    private TinyPage fromPage(ContentEntityObject contentEntityObject) {
        if (!(contentEntityObject instanceof Page)) {
            logger.error("The provided CEO is not a page");
            return new TinyPage(0L, "", "", 0);
        }
        Page page = (Page)contentEntityObject;
        List ancestors = page.getAncestors();
        return new TinyPage(page.getId(), page.getTitle(), page.getCreator() != null ? page.getCreator().getName() : null, ancestors != null ? ancestors.size() : 0);
    }

    @Override
    public TinySpace getSpace(String key) {
        if (StringUtils.isNotBlank((CharSequence)key)) {
            Space space = this.spaceManager.getSpace(key);
            if (space != null) {
                return new TinySpace(space.getKey(), space.getName(), space.getSpaceStatus() == SpaceStatus.CURRENT);
            }
            return null;
        }
        return null;
    }

    @Override
    public List<SpacePermission> getPermissions(String spaceKey) {
        Space space = this.spaceManager.getSpace(spaceKey);
        return space != null ? space.getPermissions() : Collections.emptyList();
    }

    @Override
    public void setPermissions(String spaceKey, Map<TinyOwner, PermissionSet> permissions) throws PermissionModificationException {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return;
        }
        HashSet<SpacePermission> currentPermissions = new HashSet<SpacePermission>();
        HashSet<String> newUsers = new HashSet<String>();
        HashSet<String> newGroups = new HashSet<String>();
        boolean newAnonymous = false;
        HashSet<SpacePermission> newPermissions = new HashSet<SpacePermission>();
        for (Map.Entry<TinyOwner, PermissionSet> entry : permissions.entrySet()) {
            TinyOwner owner = entry.getKey();
            if (owner.isGroup()) {
                newGroups.add(owner.getName());
            } else if (owner.isUser()) {
                newUsers.add(owner.getName());
            } else if (owner.isAnonymous()) {
                newAnonymous = true;
            }
            PermissionSet permissionSet = entry.getValue();
            for (com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission permission : permissionSet) {
                if (!permissionSet.isPermitted(permission)) continue;
                if (owner.isGroup()) {
                    newPermissions.add(SpacePermission.createGroupSpacePermission((String)permission.getType(), (Space)space, (String)owner.getName()));
                    continue;
                }
                if (owner.isUser()) {
                    ConfluenceUser user = this.userAccessor.getUserByName(owner.getName());
                    newPermissions.add(SpacePermission.createUserSpacePermission((String)permission.getType(), (Space)space, (ConfluenceUser)user));
                    continue;
                }
                if (!owner.isAnonymous()) continue;
                newPermissions.add(SpacePermission.createAnonymousSpacePermission((String)permission.getType(), (Space)space));
            }
        }
        for (SpacePermission permission : space.getPermissions()) {
            boolean isMatchingUser;
            boolean isMatchingAnonymous = newAnonymous && permission.isAnonymousPermission();
            boolean isMatchingGroup = permission.isGroupPermission() && newGroups.contains(permission.getGroup());
            boolean bl = isMatchingUser = permission.isUserPermission() && newUsers.contains(permission.getUserSubject().getName().toLowerCase());
            if (!isMatchingAnonymous && !isMatchingGroup && !isMatchingUser) continue;
            currentPermissions.add(permission);
        }
        EditPermissionsAdministrator permissionsAdministrator = this.permissionsAdministratorBuilder.buildEditSpaceAdministrator(space, (User)AuthenticatedUserThreadLocal.get(), new ArrayList(newUsers), new ArrayList(newGroups));
        try {
            permissionsAdministrator.applyPermissionChanges(currentPermissions, newPermissions);
        }
        catch (IllegalArgumentException iea) {
            throw new PermissionModificationException(iea.getMessage(), iea);
        }
    }

    @Override
    public String getUserAvatarUrl(String username) {
        ProfilePictureInfo avatar = this.userAccessor.getUserProfilePicture((User)this.userAccessor.getUserByName(username));
        return avatar.getUriReference();
    }

    @Override
    public String getHelpLink(String key) {
        HelpPath path = this.helpPathResolver.getHelpPath(key);
        if (path == null) {
            logger.error("Help link was not found for (help.) " + key);
            return null;
        }
        return path.getUrl();
    }

    @Override
    public boolean hasGroupParents(String groupName) {
        return this.crowdService.search((Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.group()).withName(groupName).returningAtMost(1)).iterator().hasNext();
    }

    public void setPermissionsAdministratorBuilder(PermissionsAdministratorBuilder permissionsAdministratorBuilder) {
        this.permissionsAdministratorBuilder = permissionsAdministratorBuilder;
    }

    public void setSetSpacePermissionChecker(SetSpacePermissionChecker setSpacePermissionChecker) {
        this.setSpacePermissionChecker = setSpacePermissionChecker;
    }
}

