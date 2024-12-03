/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogChangeset
 *  com.atlassian.crowd.manager.audit.AuditService
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogChangeset;
import com.atlassian.crowd.manager.audit.AuditService;
import java.time.Instant;
import java.util.Collection;

@Internal
public interface InternalAuditService
extends AuditService {
    public int removeStaleEntries();

    public int removeEntriesOlderThan(Instant var1);

    public void saveAudits(Collection<AuditLogChangeset> var1);

    public long getAuditLogSize();
}

