/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceResourceContextPermissionChecker
implements ResourceContextPermissionChecker {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceResourceContextPermissionChecker.class);
    private final SpaceManagerInternal spaceManagerInternal;
    private final PermissionManager permissionManager;
    private final StandardAuditResourceTypes auditResourceTypes;

    public ConfluenceResourceContextPermissionChecker(SpaceManagerInternal spaceManagerInternal, PermissionManager permissionManager, StandardAuditResourceTypes auditResourceTypes) {
        this.spaceManagerInternal = Objects.requireNonNull(spaceManagerInternal);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.auditResourceTypes = Objects.requireNonNull(auditResourceTypes);
    }

    public boolean hasResourceAuditViewPermission(@Nonnull String resourceType, @Nonnull String resourceId) {
        long spaceId;
        if (!this.auditResourceTypes.space().equals(resourceType)) {
            return false;
        }
        try {
            spaceId = Long.parseLong(resourceId);
        }
        catch (NumberFormatException nfe) {
            log.warn("Invalid spaceId {}", (Object)resourceId, (Object)nfe);
            return false;
        }
        return this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.ADMINISTER, this.spaceManagerInternal.getSpace(spaceId));
    }
}

