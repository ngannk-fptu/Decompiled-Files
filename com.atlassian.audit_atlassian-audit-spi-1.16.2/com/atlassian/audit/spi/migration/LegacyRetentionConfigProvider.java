/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 */
package com.atlassian.audit.spi.migration;

import com.atlassian.audit.api.AuditRetentionConfig;
import java.util.Optional;

public interface LegacyRetentionConfigProvider {
    public Optional<AuditRetentionConfig> get();
}

