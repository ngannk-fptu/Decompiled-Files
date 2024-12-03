/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.security;

import com.atlassian.confluence.internal.security.SpacePermissionContext;
import com.atlassian.confluence.internal.security.SpacePermissionSaverInternal;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SpacePermissionManagerInternal
extends SpacePermissionManager,
SpacePermissionSaverInternal {
    public void removeAllPermissions(Space var1, SpacePermissionContext var2);

    public void removePermission(SpacePermission var1, SpacePermissionContext var2);

    public void removeAllUserPermissions(ConfluenceUser var1, SpacePermissionContext var2);

    public void removeGlobalPermissionForUser(ConfluenceUser var1, String var2, SpacePermissionContext var3);

    public void removeAllPermissionsForGroup(String var1, SpacePermissionContext var2);
}

