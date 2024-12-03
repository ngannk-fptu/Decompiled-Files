/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.api;

import com.atlassian.audit.api.AuditRetentionConfig;
import javax.annotation.Nonnull;

public interface AuditRetentionConfigService {
    @Nonnull
    public AuditRetentionConfig getConfig();

    public void updateConfig(@Nonnull AuditRetentionConfig var1);
}

