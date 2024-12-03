/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.denylist;

import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import java.util.List;
import javax.annotation.Nonnull;

public class RestrictiveExcludedActionsService
implements ExcludedActionsService {
    private final ExcludedActionsService delegate;
    private final PermissionChecker permissionChecker;

    public RestrictiveExcludedActionsService(ExcludedActionsService delegate, PermissionChecker permissionChecker) {
        this.delegate = delegate;
        this.permissionChecker = permissionChecker;
    }

    @Override
    public boolean shouldExclude(@Nonnull AuditEntity auditEntity) {
        return this.delegate.shouldExclude(auditEntity);
    }

    @Override
    @Nonnull
    public List<String> getExcludedActions() {
        if (!this.permissionChecker.hasDenyListViewPermission()) {
            throw new AuthorisationException("The user is not allowed to view deny list configuration");
        }
        return this.delegate.getExcludedActions();
    }

    @Override
    public void updateExcludedActions(List<String> actionToAdd, List<String> actionToDelete) {
        if (!this.permissionChecker.hasDenyListUpdatePermission()) {
            throw new AuthorisationException("The user is not allowed to update deny list configuration");
        }
        this.delegate.updateExcludedActions(actionToAdd, actionToDelete);
    }

    @Override
    public void replaceExcludedActions(List<String> actions) {
        if (!this.permissionChecker.hasDenyListUpdatePermission()) {
            throw new AuthorisationException("The user is not allowed to update deny list configuration");
        }
        this.delegate.replaceExcludedActions(actions);
    }
}

