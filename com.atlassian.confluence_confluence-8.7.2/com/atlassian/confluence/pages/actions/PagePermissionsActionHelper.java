/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class PagePermissionsActionHelper {
    private final UserAccessor userAccessor;
    private final ConfluenceUser currentAuthenticatedUser;

    public PagePermissionsActionHelper(ConfluenceUser currentAuthenticatedUser, UserAccessor userAccessor) {
        this.currentAuthenticatedUser = currentAuthenticatedUser;
        this.userAccessor = userAccessor;
    }

    @Deprecated
    public List<ContentPermission> createPermissions(String contentPermissionType, String permissionsGroups, String permissionsUsers) {
        return this.createPermissions(contentPermissionType, Arrays.stream(GeneralUtil.splitCommaDelimitedString(permissionsGroups)).filter(StringUtils::isNotEmpty).collect(Collectors.toList()), Arrays.stream(GeneralUtil.splitCommaDelimitedString(permissionsUsers)).filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
    }

    public List<ContentPermission> createPermissions(String contentPermissionType, List<String> permissionsGroupList, List<String> permissionsUserList) {
        ArrayList<ContentPermission> permissions = new ArrayList<ContentPermission>(this.convertGroupNamesToPermissions(contentPermissionType, permissionsGroupList));
        if (permissionsUserList != null) {
            List userIdentifiers = permissionsUserList.stream().map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            permissions.addAll(this.getUserKeysAsPermissions(contentPermissionType, userIdentifiers.iterator()));
            permissions.addAll(this.getUserNamesAsPermissions(contentPermissionType, userIdentifiers.iterator()));
        }
        if (this.currentAuthenticatedUser != null && this.isUserLockedOut(permissions, this.currentAuthenticatedUser)) {
            permissions.add(ContentPermission.createUserPermission(contentPermissionType, this.currentAuthenticatedUser));
        }
        return permissions;
    }

    private List<ContentPermission> convertGroupNamesToPermissions(String type, List<String> permissionsGroupList) {
        if (permissionsGroupList == null) {
            return Collections.emptyList();
        }
        return permissionsGroupList.stream().map(StringUtils::trim).filter(StringUtils::isNotEmpty).map(groupName -> ContentPermission.createGroupPermission(type, groupName)).collect(Collectors.toList());
    }

    @Deprecated
    private List<ContentPermission> getUserNamesAsPermissions(String type, Iterator<String> userIdentifiers) {
        ArrayList<ContentPermission> result = new ArrayList<ContentPermission>();
        while (userIdentifiers.hasNext()) {
            String possibleUserName = userIdentifiers.next();
            if (!StringUtils.isNotEmpty((CharSequence)possibleUserName)) continue;
            try {
                ContentPermission userPermission = ContentPermission.createUserPermission(type, possibleUserName);
                result.add(userPermission);
                userIdentifiers.remove();
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        return result;
    }

    private List<ContentPermission> getUserKeysAsPermissions(String type, Iterator<String> userIdentifiers) {
        ArrayList<ContentPermission> result = new ArrayList<ContentPermission>();
        while (userIdentifiers.hasNext()) {
            ConfluenceUser user;
            String possibleUserKey = userIdentifiers.next();
            if (!StringUtils.isNotEmpty((CharSequence)possibleUserKey) || (user = this.userAccessor.getUserByKey(new UserKey(possibleUserKey))) == null) continue;
            ContentPermission userPermission = ContentPermission.createUserPermission(type, user);
            result.add(userPermission);
            userIdentifiers.remove();
        }
        return result;
    }

    private boolean isUserLockedOut(Collection<ContentPermission> contentPermissions, User user) {
        if (user == null) {
            throw new IllegalArgumentException("This method can only be called for a valid user.");
        }
        if (contentPermissions == null || contentPermissions.isEmpty()) {
            return false;
        }
        for (ContentPermission contentPermission : contentPermissions) {
            if (!contentPermission.isPermitted(user)) continue;
            return false;
        }
        return true;
    }
}

