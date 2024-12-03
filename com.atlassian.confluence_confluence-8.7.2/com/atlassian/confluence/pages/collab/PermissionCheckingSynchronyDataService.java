/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.SynchronyRowsCount
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.collab;

import com.atlassian.confluence.api.model.SynchronyRowsCount;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Objects;

public class PermissionCheckingSynchronyDataService
implements SynchronyDataService {
    private final SynchronyDataService delegate;
    private final PermissionManager permissionManager;
    private final PageManager pageManager;

    public PermissionCheckingSynchronyDataService(SynchronyDataService delegate, PermissionManager permissionManager, PageManager pageManager) {
        this.delegate = delegate;
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
    }

    public SynchronyRowsCount currentSynchronyDatasetSize(Long contentId) {
        this.failIfNotAdmin();
        return this.delegate.currentSynchronyDatasetSize(contentId);
    }

    public void softRemoveHistoryOlderThan(int thresholdHours, int contentCount) {
        this.failIfNotAdmin();
        this.delegate.softRemoveHistoryOlderThan(thresholdHours, contentCount);
    }

    public void hardRemoveHistoryOlderThan(int thresholdHours) {
        this.failIfNotAdmin();
        this.delegate.hardRemoveHistoryOlderThan(thresholdHours);
    }

    public void removeHistoryFor(ContentId contentId) {
        AbstractPage page;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, page = this.pageManager.getAbstractPage(contentId.asLong())) && !this.permissionManager.isConfluenceAdministrator(user)) {
            throw new NotAuthorizedException(user.getName());
        }
        this.delegate.removeHistoryFor(contentId);
    }

    public void removeApplicationCredentials(String applicationId) {
        this.failIfNotAdmin();
        this.delegate.removeApplicationCredentials(Objects.requireNonNull(applicationId));
    }

    public void dataCleanUpAfterTurningOffCollabEditing(String appId) {
        this.failIfNotAdmin();
        this.delegate.dataCleanUpAfterTurningOffCollabEditing(appId);
    }

    private void failIfNotAdmin() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.isConfluenceAdministrator(user)) {
            throw new NotAuthorizedException(user.getName());
        }
    }
}

