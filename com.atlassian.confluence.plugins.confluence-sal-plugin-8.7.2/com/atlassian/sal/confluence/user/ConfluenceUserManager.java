/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.user;

import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.persistence.dao.compatibility.FindUserHelper;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.confluence.user.ConfluenceUserProfile;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import javax.servlet.http.HttpServletRequest;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceUserManager
implements UserManager {
    private final UserAccessor userAccessor;
    private final CrowdService crowdService;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final ConfluenceAccessManager confluenceAccessManager;

    public ConfluenceUserManager(UserAccessor userAccessor, CrowdService crowdService, PermissionManager permissionManager, SpacePermissionManager spacePermissionManager, ConfluenceAccessManager confluenceAccessManager) {
        this.userAccessor = userAccessor;
        this.crowdService = crowdService;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.confluenceAccessManager = confluenceAccessManager;
    }

    public String getRemoteUsername() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null) {
            return user.getName();
        }
        return null;
    }

    public @Nullable UserProfile getRemoteUser() {
        return this.getUserProfile(AuthenticatedUserThreadLocal.get());
    }

    public @Nullable UserKey getRemoteUserKey() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null) {
            return user.getKey();
        }
        return null;
    }

    public boolean isSystemAdmin(String username) {
        return this.isSystemAdmin(this.userAccessor.getUserByName(username));
    }

    public boolean isSystemAdmin(@Nullable UserKey userKey) {
        return this.isSystemAdmin(this.userAccessor.getExistingUserByKey(userKey));
    }

    private boolean isSystemAdmin(ConfluenceUser user) {
        return user != null && this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public boolean authenticate(String username, String password) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        return user != null && this.userAccessor.authenticate(user.getName(), password);
    }

    public boolean isUserInGroup(String username, String group) {
        return this.userAccessor.hasMembership(group, username);
    }

    public boolean isUserInGroup(@Nullable UserKey userKey, @Nullable String groupName) {
        if (userKey == null) {
            return false;
        }
        Group group = this.userAccessor.getGroup(groupName);
        ConfluenceUser user = this.userAccessor.getExistingUserByKey(userKey);
        return this.userAccessor.hasMembership(group, (User)user);
    }

    public String getRemoteUsername(HttpServletRequest request) {
        return request.getRemoteUser();
    }

    public @Nullable UserProfile getRemoteUser(HttpServletRequest request) {
        return this.getUserProfile(request.getRemoteUser());
    }

    public @Nullable UserKey getRemoteUserKey(HttpServletRequest request) {
        ConfluenceUser user = this.userAccessor.getUserByName(request.getRemoteUser());
        if (user != null) {
            return user.getKey();
        }
        return null;
    }

    public ConfluenceUser resolve(String username) {
        return this.userAccessor.getUserByName(username);
    }

    public Iterable<String> findGroupNamesByPrefix(String prefix, int startIndex, int maxResults) {
        EntityQuery query = QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).with((SearchRestriction)Restriction.on((Property)GroupTermKeys.NAME).startingWith((Object)prefix)).startingAt(startIndex).returningAtMost(maxResults);
        return this.crowdService.search((Query)query);
    }

    public boolean isAnonymousAccessEnabled() {
        return this.spacePermissionManager.hasPermission("USECONFLUENCE", null, null);
    }

    public boolean isLimitedUnlicensedAccessEnabled() {
        SpacePermission unlicensedGlobalAccessPermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)"LIMITEDUSECONFLUENCE", null);
        return this.spacePermissionManager.permissionExists(unlicensedGlobalAccessPermission);
    }

    public boolean isAdmin(String username) {
        return this.isAdmin(this.userAccessor.getUserByName(username));
    }

    public boolean isAdmin(@Nullable UserKey userKey) {
        return this.isAdmin(this.userAccessor.getExistingUserByKey(userKey));
    }

    private boolean isAdmin(ConfluenceUser user) {
        return user != null && this.permissionManager.hasPermission((User)user, Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public boolean isLicensed(@Nullable UserKey userKey) {
        if (userKey == null) {
            return false;
        }
        return this.confluenceAccessManager.getUserAccessStatus((User)FindUserHelper.getUserByUserKey((UserKey)userKey)).hasLicensedAccess();
    }

    public boolean isLimitedUnlicensedUser(@Nullable UserKey userKey) {
        if (userKey == null) {
            return false;
        }
        return this.confluenceAccessManager.getUserAccessStatus((User)FindUserHelper.getUserByUserKey((UserKey)userKey)).hasUnlicensedAuthenticatedAccess();
    }

    public UserProfile getUserProfile(String username) {
        return this.getUserProfile(this.userAccessor.getUserByName(username));
    }

    public @Nullable UserProfile getUserProfile(@Nullable UserKey userKey) {
        return this.getUserProfile(this.userAccessor.getUserByKey(userKey));
    }

    private UserProfile getUserProfile(@Nullable ConfluenceUser user) {
        return null == user ? null : new ConfluenceUserProfile(user, this.userAccessor.getUserProfilePicture((User)user));
    }
}

