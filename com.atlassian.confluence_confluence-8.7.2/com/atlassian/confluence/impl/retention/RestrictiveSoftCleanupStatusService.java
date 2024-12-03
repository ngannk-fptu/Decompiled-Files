/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus
 *  com.atlassian.confluence.api.service.retention.SoftCleanupStatusService
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus;
import com.atlassian.confluence.api.service.retention.SoftCleanupStatusService;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

public class RestrictiveSoftCleanupStatusService
implements SoftCleanupStatusService {
    private final SoftCleanupStatusService delegate;
    private final PermissionManager permissionManager;

    public RestrictiveSoftCleanupStatusService(SoftCleanupStatusService delegate, PermissionManager permissionManager) {
        this.delegate = delegate;
        this.permissionManager = permissionManager;
    }

    public SoftCleanupJobStatus getCurrentStatus() {
        this.failIfNotAdmin();
        return this.delegate.getCurrentStatus();
    }

    public void setCurrentStatus(SoftCleanupJobStatus status) {
        this.failIfNotAdmin();
        this.delegate.setCurrentStatus(status);
    }

    private void failIfNotAdmin() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.isSystemAdministrator(user)) {
            throw new NotAuthorizedException(user.getName());
        }
    }
}

