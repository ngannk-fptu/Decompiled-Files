/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.annotations.Internal;
import java.util.Set;

@Internal
public interface AuditLogProperties {
    public Set<String> getSanitizedProperties();

    public boolean isAuditLogEnabledForSynchronisation();

    public boolean isAuditLogEnabled();
}

