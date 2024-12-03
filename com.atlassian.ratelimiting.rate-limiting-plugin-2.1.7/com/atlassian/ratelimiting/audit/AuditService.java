/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.audit;

import com.atlassian.ratelimiting.audit.AuditEntry;

public interface AuditService {
    public void store(AuditEntry var1);
}

