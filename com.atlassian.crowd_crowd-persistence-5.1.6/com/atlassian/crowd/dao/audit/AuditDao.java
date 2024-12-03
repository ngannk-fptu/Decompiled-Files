/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.query.AuditLogQuery
 */
package com.atlassian.crowd.dao.audit;

import com.atlassian.crowd.audit.query.AuditLogQuery;
import com.atlassian.crowd.model.audit.AuditLogChangesetEntity;
import java.util.List;

public interface AuditDao {
    public void add(AuditLogChangesetEntity var1);

    public <RESULT> List<RESULT> search(AuditLogQuery<RESULT> var1);

    public int removeChangesetsOlderThan(long var1);

    public long getAuditLogSize();
}

