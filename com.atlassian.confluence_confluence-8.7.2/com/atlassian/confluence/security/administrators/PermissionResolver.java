/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.administrators;

import com.atlassian.confluence.security.SpacePermission;
import java.util.List;

public interface PermissionResolver {
    public List<SpacePermission> getPermissions();
}

