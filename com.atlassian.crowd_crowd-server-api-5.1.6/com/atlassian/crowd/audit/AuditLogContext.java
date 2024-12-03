/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogEventSource
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogContextCallback;
import com.atlassian.crowd.audit.AuditLogEventSource;

public interface AuditLogContext {
    public <T> T withAuditLogAuthor(AuditLogAuthor var1, AuditLogContextCallback<T> var2) throws Exception;

    public <T> T withAuditLogSource(AuditLogEventSource var1, AuditLogContextCallback<T> var2) throws Exception;
}

