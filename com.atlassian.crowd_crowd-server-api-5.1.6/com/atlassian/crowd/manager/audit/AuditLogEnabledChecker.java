/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.annotations.Internal;

@Internal
public interface AuditLogEnabledChecker {
    public boolean shouldAuditEvent();
}

