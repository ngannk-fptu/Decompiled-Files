/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.rest.filter;

import com.atlassian.confluence.plugins.rest.filter.CanUseFilter;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.user.User;

public final class CanUseWithAnyConfluenceAccessFilter
extends CanUseFilter {
    private final ConfluenceAccessManager confluenceAccessManager;

    public CanUseWithAnyConfluenceAccessFilter(PermissionManager permissionManager, ConfluenceAccessManager confluenceAccessManager) {
        super(permissionManager);
        this.confluenceAccessManager = confluenceAccessManager;
    }

    @Override
    protected boolean canUseConfluenceCheck(User remoteUser) {
        AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus(remoteUser);
        return userAccessStatus.canUseConfluence();
    }
}

