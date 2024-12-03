/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api;

import com.atlassian.audit.entity.AuditEvent;
import javax.annotation.Nonnull;

public interface AuditService {
    public void audit(@Nonnull AuditEvent var1);
}

