/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;

public interface PermissionService {
    public boolean canInitiateSpaceCopy(User var1, Space var2);

    public void copySpacePermissions(Space var1, Space var2, Boolean var3);

    public boolean checkIfWatcherHasViewPermission(ConfluenceUser var1, ContentId var2);

    public boolean canViewSpace(User var1, Space var2);
}

