/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.audit.AuditLogAuthorType
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit;

import com.atlassian.crowd.audit.AuditLogAuthorType;
import javax.annotation.Nullable;

public interface AuditLogAuthor {
    @Nullable
    public Long getId();

    @Nullable
    public String getName();

    public AuditLogAuthorType getType();
}

