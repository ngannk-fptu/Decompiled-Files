/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.audit;

import javax.annotation.Nullable;

public interface AuditLogEntry {
    public String getPropertyName();

    @Nullable
    public String getOldValue();

    @Nullable
    public String getNewValue();
}

