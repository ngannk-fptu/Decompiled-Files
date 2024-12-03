/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.confluence.security;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermissionSet;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;

public class PermissionUtils {
    public static List<String> convertCommaDelimitedStringToList(String input) {
        String[] groupToAddAsArray = GeneralUtil.splitCommaDelimitedString(input);
        return Arrays.asList(groupToAddAsArray);
    }

    public static boolean isAdminUser(SpacePermissionManager spacePermissionManager, User remoteUser) {
        return PermissionUtils.isAdminUser(spacePermissionManager, remoteUser, null);
    }

    public static boolean isAdminUser(SpacePermissionManager spacePermissionManager, User remoteUser, Space space) {
        return spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, remoteUser);
    }

    public static String getRestrictionsHash(ContentEntityObject content) {
        StringBuilder restrictionsHash = new StringBuilder();
        List<ContentPermissionSet> permissionSets = Arrays.asList(content.getContentPermissionSet("View"), content.getContentPermissionSet("Edit"));
        for (ContentPermissionSet permissionSet : permissionSets) {
            if (permissionSet == null) continue;
            List sortedUserKeys = permissionSet.getUserKeys().stream().map(UserKey::toString).sorted().collect(Collectors.toList());
            List sortedGroupNames = permissionSet.getGroupNames().stream().sorted().collect(Collectors.toList());
            restrictionsHash.append(permissionSet.getType()).append(Objects.hash(sortedUserKeys)).append(Objects.hash(sortedGroupNames));
        }
        return restrictionsHash.length() == 0 ? restrictionsHash.toString() : DigestUtils.md5Hex((String)restrictionsHash.toString());
    }
}

