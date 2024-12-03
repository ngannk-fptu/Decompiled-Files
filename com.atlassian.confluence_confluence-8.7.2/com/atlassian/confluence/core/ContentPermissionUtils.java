/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.ContentPermissionSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentPermissionUtils {
    private ContentPermissionUtils() {
    }

    public static Map<ContentEntityObject, Map<String, ContentPermissionSet>> getPermissionsAsMap(List<ContentPermissionSet> contentPermissionSets) {
        HashMap<ContentEntityObject, Map<String, ContentPermissionSet>> permissionMap = new HashMap<ContentEntityObject, Map<String, ContentPermissionSet>>();
        for (ContentPermissionSet contentPermissionSet : contentPermissionSets) {
            ContentEntityObject owningContent = contentPermissionSet.getOwningContent();
            String contentPermissionType = contentPermissionSet.getType();
            if (!permissionMap.containsKey(owningContent)) {
                permissionMap.put(owningContent, new HashMap());
            }
            ((Map)permissionMap.get(owningContent)).put(contentPermissionType, contentPermissionSet);
        }
        return permissionMap;
    }

    public static List<ContentPermission> createContentPermissionsFromSet(ContentPermissionSet contentPermissionSet) {
        if (contentPermissionSet == null) {
            return Collections.emptyList();
        }
        ArrayList<ContentPermission> contentPermissions = new ArrayList<ContentPermission>();
        for (ContentPermission contentPermission : contentPermissionSet.getAllExcept(Collections.emptyList())) {
            if (contentPermission.isUserPermission()) {
                contentPermissions.add(ContentPermission.createUserPermission(contentPermission.getType(), contentPermission.getUserSubject()));
                continue;
            }
            if (!contentPermission.isGroupPermission()) continue;
            contentPermissions.add(ContentPermission.createGroupPermission(contentPermission.getType(), contentPermission.getGroupName()));
        }
        return contentPermissions;
    }
}

