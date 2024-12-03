/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogChangeset
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogChangeset;

@Internal
public interface AuditLogChangesetPopulator {
    public AuditLogChangeset populateCommonChangesetProperties(AuditLogChangeset var1, boolean var2);
}

