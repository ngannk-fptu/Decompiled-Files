/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.ao.service;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RestrictiveSearchService
implements AuditSearchService {
    private final PermissionChecker permissionChecker;
    private final AuditSearchService origin;

    public RestrictiveSearchService(PermissionChecker permissionChecker, AuditSearchService origin) {
        this.permissionChecker = permissionChecker;
        this.origin = origin;
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) throws TimeoutException {
        if (!this.permitted(query)) {
            throw new AuthorisationException("The user is not allowed to view audit events");
        }
        return this.origin.findBy(query, pageRequest, scanLimit);
    }

    public void stream(@Nonnull AuditQuery query, int offset, int limit, @Nonnull Consumer<AuditEntity> consumer) throws TimeoutException {
        if (!this.permitted(query)) {
            throw new AuthorisationException("The user is not allowed to view audit events");
        }
        this.origin.stream(query, offset, limit, consumer);
    }

    public long count(@Nullable AuditQuery query) throws TimeoutException {
        if (!this.permitted(query)) {
            throw new AuthorisationException("The user is not allowed to view audit events");
        }
        return this.origin.count(query);
    }

    private boolean permitted(@Nullable AuditQuery query) {
        if (this.permissionChecker.hasUnrestrictedAuditViewPermission()) {
            return true;
        }
        if (query == null) {
            return false;
        }
        if (query.getResources().size() > 0) {
            return query.getResources().stream().allMatch(r -> this.permissionChecker.hasResourceAuditViewPermission(r.getType(), r.getId()));
        }
        return false;
    }
}

