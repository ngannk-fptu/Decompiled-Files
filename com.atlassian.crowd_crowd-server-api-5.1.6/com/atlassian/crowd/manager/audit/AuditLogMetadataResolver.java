/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogEventSource
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogEventSource;
import javax.annotation.Nullable;

@Internal
public interface AuditLogMetadataResolver {
    public AuditLogAuthor resolveAuthor();

    @Nullable
    public String resolveIpAddress();

    public AuditLogEventSource resolveSource();
}

