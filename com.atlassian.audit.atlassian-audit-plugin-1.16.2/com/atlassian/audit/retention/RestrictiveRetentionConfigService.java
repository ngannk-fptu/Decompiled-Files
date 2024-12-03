/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.retention;

import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.plugins.rest.common.security.AuthorisationException;
import javax.annotation.Nonnull;

public class RestrictiveRetentionConfigService
implements AuditRetentionConfigService {
    private final PermissionChecker permissionChecker;
    private final AuditRetentionConfigService origin;

    public RestrictiveRetentionConfigService(PermissionChecker permissionChecker, AuditRetentionConfigService origin) {
        this.permissionChecker = permissionChecker;
        this.origin = origin;
    }

    @Nonnull
    public AuditRetentionConfig getConfig() {
        if (!this.permissionChecker.hasRetentionConfigViewPermission()) {
            throw new AuthorisationException("The user is not allowed to view audit retention configuration");
        }
        return this.origin.getConfig();
    }

    public void updateConfig(@Nonnull AuditRetentionConfig config) {
        if (!this.permissionChecker.hasRetentionConfigUpdatePermission()) {
            throw new AuthorisationException("The user is not allowed to update audit retention configuration");
        }
        this.origin.updateConfig(config);
    }
}

