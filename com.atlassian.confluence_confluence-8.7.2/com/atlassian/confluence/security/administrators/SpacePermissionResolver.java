/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.administrators.PermissionResolver;
import com.atlassian.confluence.spaces.Space;
import java.util.List;

class SpacePermissionResolver
implements PermissionResolver {
    private final Space space;

    SpacePermissionResolver(Space space) {
        this.space = space;
    }

    @Override
    public List<SpacePermission> getPermissions() {
        return this.space.getPermissions();
    }
}

