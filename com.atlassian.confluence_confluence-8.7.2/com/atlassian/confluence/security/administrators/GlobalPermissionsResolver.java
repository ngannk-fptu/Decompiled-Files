/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import java.util.List;

class GlobalPermissionsResolver
implements PermissionResolver {
    private SpacePermissionManager spacePermissionManager;

    GlobalPermissionsResolver(SpacePermissionManager spacePermissionManager) {
        this.spacePermissionManager = spacePermissionManager;
    }

    @Override
    public List<SpacePermission> getPermissions() {
        return this.spacePermissionManager.getGlobalPermissions();
    }
}

