/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.audit.AuditLogEventType
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper
 *  com.atlassian.crowd.model.user.User
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.user.crowd.audit;

import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.audit.AuditLogEventType;
import com.atlassian.crowd.manager.audit.mapper.AuditLogUserMapper;
import com.atlassian.crowd.model.user.User;
import java.util.List;
import javax.annotation.Nullable;

public final class NoOpCrowdAuditLogUserMapper
implements AuditLogUserMapper {
    public AuditLogEntry calculatePasswordDiff() {
        throw new UnsupportedOperationException("crowd-4.x: auditService disabled");
    }

    public List<AuditLogEntry> calculateDifference(AuditLogEventType auditLogEventType, @Nullable User oldUser, @Nullable User newUser) {
        throw new UnsupportedOperationException("crowd-4.x: auditService disabled");
    }
}

