/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditCoverageConfigService
 *  com.atlassian.audit.entity.AuditCoverageConfig
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.api.AuditCoverageConfigService;
import com.atlassian.audit.entity.AuditCoverageConfig;

public interface InternalAuditCoverageConfigService
extends AuditCoverageConfigService {
    public void updateConfig(AuditCoverageConfig var1);
}

