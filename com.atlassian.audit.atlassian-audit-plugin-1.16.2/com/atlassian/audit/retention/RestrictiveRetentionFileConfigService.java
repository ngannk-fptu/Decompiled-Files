/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.retention;

import com.atlassian.audit.csv.LicenseException;
import com.atlassian.audit.file.AuditRetentionFileConfig;
import com.atlassian.audit.file.AuditRetentionFileConfigLicenseChecker;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import javax.annotation.Nonnull;

public class RestrictiveRetentionFileConfigService
implements AuditRetentionFileConfigService {
    private final PermissionChecker permissionChecker;
    private final AuditRetentionFileConfigService origin;
    private final AuditRetentionFileConfigLicenseChecker auditRetentionFileConfigLicenseChecker;

    public RestrictiveRetentionFileConfigService(PermissionChecker permissionChecker, @PermissionsNotEnforced AuditRetentionFileConfigService origin, AuditRetentionFileConfigLicenseChecker auditRetentionFileConfigLicenseChecker) {
        this.permissionChecker = permissionChecker;
        this.origin = origin;
        this.auditRetentionFileConfigLicenseChecker = auditRetentionFileConfigLicenseChecker;
    }

    @Override
    @Nonnull
    public AuditRetentionFileConfig getConfig() {
        if (!this.permissionChecker.hasRetentionConfigViewPermission()) {
            throw new AuthorisationException("The user is not allowed to view audit retention configuration");
        }
        return this.origin.getConfig();
    }

    @Override
    public void updateConfig(@Nonnull AuditRetentionFileConfig config) {
        if (!this.permissionChecker.hasRetentionConfigUpdatePermission()) {
            throw new AuthorisationException("The user is not allowed to update audit retention configuration");
        }
        if (!this.auditRetentionFileConfigLicenseChecker.allowUpdate()) {
            throw new LicenseException("Attempted to update file retention configuration without correct license to do so");
        }
        this.origin.updateConfig(config);
    }
}

