/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.atlassian.crowd.manager.audit.AuditLogConfiguration;
import java.util.List;

public interface AuditService {
    public void saveAudit(AuditLogChangeset var1);

    public <RESULT> List<RESULT> searchAuditLog(AuditLogQuery<RESULT> var1);

    @Deprecated
    public boolean isEnabled();

    public void saveConfiguration(AuditLogConfiguration var1);

    public AuditLogConfiguration getConfiguration();

    public boolean shouldAuditEvent();
}

