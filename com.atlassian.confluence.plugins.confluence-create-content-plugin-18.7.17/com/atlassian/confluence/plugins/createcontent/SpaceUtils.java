/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;

public class SpaceUtils {
    public static boolean hasEditableSpaces(ConfluenceUser user, SpaceManager spaceManager) {
        return !SpaceUtils.getSpacesForPermission(user, 1, "EDITSPACE", spaceManager).isEmpty() || !SpaceUtils.getSpacesForPermission(user, 1, "EDITBLOG", spaceManager).isEmpty();
    }

    public static boolean hasCreatePagePermission(ConfluenceUser user, Space space, SpaceManager spaceManager, SpacePermissionManager spacePermissionManager) {
        if (space != null) {
            return spacePermissionManager.hasPermission("EDITSPACE", space, (User)user);
        }
        return !SpaceUtils.getSpacesForPermission(user, 1, "EDITSPACE", spaceManager).isEmpty();
    }

    public static Predicate<Space> editableSpaceFilter(ConfluenceUser user, SpacePermissionManager spacePermissionManager, String spacePermission) {
        return space -> {
            if (spacePermission != null) {
                return space != null && spacePermissionManager.hasPermission(spacePermission, space, (User)user);
            }
            return space != null && spacePermissionManager.hasPermission("EDITSPACE", space, (User)user) || spacePermissionManager.hasPermission("EDITBLOG", space, (User)user);
        };
    }

    public static Collection<Space> getEditableSpaces(ConfluenceUser user, int limit, SpaceManager spaceManager, String spacePermission) {
        if (spacePermission != null) {
            return SpaceUtils.getSpacesForPermission(user, limit, spacePermission, spaceManager);
        }
        return Sets.union(SpaceUtils.getSpacesForPermission(user, limit, "EDITSPACE", spaceManager), SpaceUtils.getSpacesForPermission(user, limit, "EDITBLOG", spaceManager));
    }

    private static Set<Space> getSpacesForPermission(ConfluenceUser user, int limit, String permission, SpaceManager spaceManager) {
        SpacesQuery spacesQuery = SpacesQuery.newQuery().forUser((User)user).withSpaceStatus(SpaceStatus.CURRENT).withPermission(permission).unsorted().build();
        return Sets.newLinkedHashSet((Iterable)spaceManager.getSpaces(spacesQuery).getPage(0, limit));
    }
}

