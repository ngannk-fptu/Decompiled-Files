/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper
 *  com.atlassian.crowd.model.group.Group
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.user.crowd.audit;

import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.manager.audit.mapper.AuditLogGroupMapper;
import com.atlassian.crowd.model.group.Group;
import java.util.List;
import javax.annotation.Nullable;

public final class NoOpCrowdAuditLogGroupMapper
implements AuditLogGroupMapper {
    public List<AuditLogEntry> calculateDifference(@Nullable Group oldValue, @Nullable Group newValue) {
        throw new UnsupportedOperationException("crowd-4.x: auditService disabled");
    }
}

