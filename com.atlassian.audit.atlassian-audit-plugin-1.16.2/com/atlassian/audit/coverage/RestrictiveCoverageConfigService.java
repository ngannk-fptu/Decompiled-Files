/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.coverage.InternalAuditCoverageConfigService;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.plugins.rest.common.security.AuthorisationException;

public class RestrictiveCoverageConfigService
implements InternalAuditCoverageConfigService {
    private final PermissionChecker permissionChecker;
    private final InternalAuditCoverageConfigService origin;

    public RestrictiveCoverageConfigService(PermissionChecker permissionChecker, InternalAuditCoverageConfigService origin) {
        this.permissionChecker = permissionChecker;
        this.origin = origin;
    }

    public AuditCoverageConfig getConfig() {
        if (!this.permissionChecker.hasCoverageConfigViewPermission()) {
            throw new AuthorisationException("The user is not allowed to view audit coverage configuration");
        }
        return this.origin.getConfig();
    }

    @Override
    public void updateConfig(AuditCoverageConfig config) {
        if (!this.permissionChecker.hasCoverageConfigUpdatePermission()) {
            throw new AuthorisationException("The user is not allowed to update audit coverage configuration");
        }
        this.origin.updateConfig(config);
    }
}

