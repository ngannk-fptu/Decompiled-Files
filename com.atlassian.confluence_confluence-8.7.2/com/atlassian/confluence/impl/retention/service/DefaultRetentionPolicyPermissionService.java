/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 */
package com.atlassian.confluence.impl.retention.service;

import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.retention.RetentionPolicyPermissionManager;
import com.atlassian.confluence.retention.RetentionPolicyPermissionService;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

public class DefaultRetentionPolicyPermissionService
implements RetentionPolicyPermissionService {
    private final RetentionPolicyPermissionManager retentionPolicyPermissionManager;
    private final SpaceManagerInternal spaceManagerInternal;

    public DefaultRetentionPolicyPermissionService(RetentionPolicyPermissionManager retentionPolicyPermissionManager, SpaceManagerInternal spaceManagerInternal) {
        this.retentionPolicyPermissionManager = retentionPolicyPermissionManager;
        this.spaceManagerInternal = spaceManagerInternal;
    }

    @Override
    public boolean getUserRetentionPolicyPermissionForSpace(String spaceKey) {
        Space space = this.spaceManagerInternal.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException("Space with given key not found when fetching retention policy permission.");
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.retentionPolicyPermissionManager.canViewSpacePolicy(user, space)) {
            throw new PermissionException("User not permitted to check retention policy permission.");
        }
        return this.retentionPolicyPermissionManager.canEditSpacePolicy(user, space);
    }
}

