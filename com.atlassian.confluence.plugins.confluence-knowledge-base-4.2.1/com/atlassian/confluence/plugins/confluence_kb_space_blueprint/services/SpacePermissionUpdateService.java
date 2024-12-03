/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SpacePermissionUpdateResult;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;

@Internal
public interface SpacePermissionUpdateService {
    public SpacePermissionUpdateResult setEnableAnonymousViewSpace(ConfluenceUser var1, Space var2, boolean var3, boolean var4);

    public SpacePermissionUpdateResult setEnableUnlicensedViewSpace(ConfluenceUser var1, Space var2, boolean var3, boolean var4);

    public SpacePermissionUpdateResult setEnableGlobalUnlicensedAccess(ConfluenceUser var1, boolean var2, boolean var3);
}

