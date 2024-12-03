/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.core.spi;

import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import javax.annotation.Nonnull;

public class NoopResourceContextPermissionChecker
implements ResourceContextPermissionChecker {
    public boolean hasResourceAuditViewPermission(@Nonnull String resourceType, @Nonnull String resourceId) {
        return true;
    }
}

