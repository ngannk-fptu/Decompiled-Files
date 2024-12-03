/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfig
 *  com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider
 */
package com.atlassian.audit.core.spi;

import com.atlassian.audit.api.AuditRetentionConfig;
import com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider;
import java.util.Optional;

public class NoopLegacyRetentionConfigProvider
implements LegacyRetentionConfigProvider {
    public Optional<AuditRetentionConfig> get() {
        return Optional.empty();
    }
}

