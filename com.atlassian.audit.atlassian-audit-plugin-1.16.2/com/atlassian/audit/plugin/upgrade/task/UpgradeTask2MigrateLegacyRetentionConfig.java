/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditRetentionConfigService
 *  com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.audit.plugin.upgrade.task;

import com.atlassian.audit.api.AuditRetentionConfigService;
import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider;
import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UpgradeTask2MigrateLegacyRetentionConfig
extends AuditUpgradeTask {
    private static final int BUILD_NUMBER = 2;
    private final AuditRetentionConfigService auditRetentionConfigService;
    private final LegacyRetentionConfigProvider configProvider;

    public UpgradeTask2MigrateLegacyRetentionConfig(AuditRetentionConfigService auditRetentionConfigService, LegacyRetentionConfigProvider configProvider) {
        this.auditRetentionConfigService = Objects.requireNonNull(auditRetentionConfigService);
        this.configProvider = Objects.requireNonNull(configProvider);
    }

    public int getBuildNumber() {
        return 2;
    }

    public String getShortDescription() {
        return "Migrate existing auditing retention configurations";
    }

    public Collection<Message> doUpgrade() throws Exception {
        this.configProvider.get().ifPresent(arg_0 -> ((AuditRetentionConfigService)this.auditRetentionConfigService).updateConfig(arg_0));
        return Collections.emptySet();
    }
}

