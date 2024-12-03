/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogAuthor
 *  com.atlassian.crowd.audit.AuditLogContext
 *  com.atlassian.crowd.audit.AuditLogEventSource
 */
package com.atlassian.crowd.audit;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogAuthor;
import com.atlassian.crowd.audit.AuditLogContext;
import com.atlassian.crowd.audit.AuditLogEventSource;
import java.util.Optional;

@Internal
public interface AuditLogContextInternal
extends AuditLogContext {
    public Optional<AuditLogAuthor> getAuthor();

    public Optional<AuditLogEventSource> getSource();
}

