/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.audit.query.AuditLogQuery
 *  com.atlassian.crowd.manager.audit.AuditLogConfiguration
 *  com.atlassian.crowd.manager.audit.AuditService
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.atlassian.crowd.manager.audit.AuditLogConfiguration;
import com.atlassian.crowd.manager.audit.AuditService;
import java.util.Collections;
import java.util.List;

public class NoOpAuditService
implements AuditService {
    public void saveAudit(AuditLogChangeset changeset) {
    }

    public List<AuditLogChangeset> searchAuditLog(AuditLogQuery query) {
        return Collections.emptyList();
    }

    public boolean isEnabled() {
        return false;
    }

    public void saveConfiguration(AuditLogConfiguration auditLogConfiguration) {
    }

    public AuditLogConfiguration getConfiguration() {
        return AuditLogConfiguration.defaultConfiguration();
    }

    public boolean shouldAuditEvent() {
        return false;
    }
}

