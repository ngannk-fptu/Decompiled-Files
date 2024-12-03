/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogContext
 *  com.atlassian.crowd.audit.AuditLogContextCallback
 *  com.atlassian.crowd.audit.AuditLogEventSource
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogContext;
import com.atlassian.crowd.audit.AuditLogContextCallback;
import com.atlassian.crowd.audit.AuditLogEventSource;

public class NoOpAuditLogContext
implements AuditLogContext {
    public <T> T withAuditLogAuthor(AuditLogAuthor author, AuditLogContextCallback<T> callback) throws Exception {
        return (T)callback.execute();
    }

    public <T> T withAuditLogSource(AuditLogEventSource source, AuditLogContextCallback<T> callback) throws Exception {
        return (T)callback.execute();
    }
}

