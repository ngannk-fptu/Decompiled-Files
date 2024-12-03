/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 */
package com.atlassian.audit.permission;

import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;

public interface PermissionChecker
extends ResourceContextPermissionChecker {
    public boolean hasUnrestrictedAuditViewPermission();

    public boolean hasCacheRebuildPermission();

    public boolean hasCoverageConfigViewPermission();

    public boolean hasCoverageConfigUpdatePermission();

    public boolean hasRetentionConfigViewPermission();

    public boolean hasRetentionConfigUpdatePermission();

    public boolean hasDenyListViewPermission();

    public boolean hasDenyListUpdatePermission();
}

