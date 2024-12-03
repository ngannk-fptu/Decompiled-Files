/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 */
package com.atlassian.confluence.plugins.createcontent.spacecreationsteps;

import com.atlassian.confluence.plugins.createcontent.spacecreationsteps.AbstractSpaceCreationStep;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import java.util.Map;

public class RestrictedUserSpaceCreationStep
extends AbstractSpaceCreationStep {
    private final SpacePermissionManager spacePermissionsManager;
    private final UserAccessor userAccessor;
    public static final String CONTEXT_KEY = "restrictedUsers";

    public RestrictedUserSpaceCreationStep(SpacePermissionManager spacePermissionsManager, UserAccessor userAccessor) {
        this.spacePermissionsManager = spacePermissionsManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public void posthandle(Space space, Map<String, Object> context) {
        String[] split;
        String users = (String)context.get(CONTEXT_KEY);
        if (users == null) {
            return;
        }
        this.spacePermissionsManager.createPrivateSpacePermissions(space);
        for (String username : split = users.split(",")) {
            ConfluenceUser user = this.userAccessor.getUserByName(username);
            if (user == null) continue;
            for (String permission : SpacePermission.GENERIC_SPACE_PERMISSIONS) {
                if (permission.equals("SETSPACEPERMISSIONS") || permission.equals("EXPORTSPACE")) continue;
                this.spacePermissionsManager.savePermission(SpacePermission.createUserSpacePermission((String)permission, (Space)space, (ConfluenceUser)user));
            }
        }
    }
}

