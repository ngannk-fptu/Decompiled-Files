/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;

public class ActionHelper {
    protected static String extractSpaceKey(String pageString) {
        String[] results;
        if (StringUtils.isNotEmpty((CharSequence)pageString) && (results = pageString.split(":")).length == 2) {
            return results[0];
        }
        return null;
    }

    protected static String extractPageTitle(String pageString) {
        if (!"".equals(pageString) && pageString != null) {
            String[] results = pageString.split(":");
            if (results.length == 2) {
                return results[1];
            }
            return pageString;
        }
        return null;
    }

    public static boolean isSpaceAdmin(Space space, User remoteUser, SpacePermissionManager spacePermissionManager) {
        return spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, remoteUser);
    }
}

