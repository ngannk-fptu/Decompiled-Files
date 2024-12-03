/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.file;

import com.atlassian.audit.file.AuditRetentionFileConfig;
import javax.annotation.Nonnull;

public interface AuditRetentionFileConfigService {
    @Nonnull
    public AuditRetentionFileConfig getConfig();

    public void updateConfig(@Nonnull AuditRetentionFileConfig var1);
}

