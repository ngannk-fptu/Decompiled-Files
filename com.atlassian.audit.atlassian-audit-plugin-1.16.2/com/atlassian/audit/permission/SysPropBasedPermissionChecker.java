/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.permission;

import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import com.atlassian.sal.api.user.UserManager;
import javax.annotation.Nonnull;

public class SysPropBasedPermissionChecker
implements PermissionChecker {
    @Deprecated
    private static final String LEGACY_SYS_ADMIN_ONLY_SYS_PROP_KEY = "audit.log.view.sysadmin.only";
    private static final String SYS_ADMIN_ONLY_SYS_PROP_KEY = "plugin.audit.log.view.sysadmin.only";
    private static final String INTERNAL_TEST_NO_PERMISSIONS_KEY = "plugin.audit.internal.testing.no.permissions";
    private final UserManager userManager;
    private final ResourceContextPermissionChecker resourcePermissionChecker;
    private final PropertiesProvider propertiesProvider;

    public SysPropBasedPermissionChecker(@Nonnull UserManager userManager, @Nonnull ResourceContextPermissionChecker resourcePermissionChecker, PropertiesProvider propertiesProvider) {
        this.userManager = userManager;
        this.resourcePermissionChecker = resourcePermissionChecker;
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    public boolean hasUnrestrictedAuditViewPermission() {
        return this.isSysAdmin() || this.allowAdminToSeeLogs() && this.isAdmin();
    }

    @Override
    public boolean hasCacheRebuildPermission() {
        return this.isSysAdmin();
    }

    public boolean hasResourceAuditViewPermission(@Nonnull String resourceType, @Nonnull String resourceId) {
        return this.resourcePermissionChecker.hasResourceAuditViewPermission(resourceType, resourceId) || this.hasUnrestrictedAuditViewPermission() || Boolean.getBoolean(INTERNAL_TEST_NO_PERMISSIONS_KEY);
    }

    @Override
    public boolean hasCoverageConfigViewPermission() {
        return this.isSysAdmin() || this.allowAdminToSeeLogs() && this.isAdmin();
    }

    @Override
    public boolean hasCoverageConfigUpdatePermission() {
        return this.isSysAdmin();
    }

    @Override
    public boolean hasRetentionConfigViewPermission() {
        return this.isSysAdmin() || this.allowAdminToSeeLogs() && this.isAdmin();
    }

    @Override
    public boolean hasRetentionConfigUpdatePermission() {
        return this.isSysAdmin();
    }

    @Override
    public boolean hasDenyListViewPermission() {
        return this.isSysAdmin() || this.allowAdminToSeeLogs() && this.isAdmin();
    }

    @Override
    public boolean hasDenyListUpdatePermission() {
        return this.isSysAdmin();
    }

    private boolean isAdmin() {
        return this.userManager.isAdmin(this.userManager.getRemoteUserKey());
    }

    private boolean isSysAdmin() {
        return this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());
    }

    private boolean allowAdminToSeeLogs() {
        return !this.propertiesProvider.getBoolean(SYS_ADMIN_ONLY_SYS_PROP_KEY) && !Boolean.getBoolean(LEGACY_SYS_ADMIN_ONLY_SYS_PROP_KEY);
    }
}

