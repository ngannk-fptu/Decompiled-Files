/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator
 *  com.atlassian.sal.api.message.Message
 */
package com.atlassian.audit.plugin.upgrade.task;

import com.atlassian.audit.ao.consumer.DatabaseAuditConsumer;
import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator;
import com.atlassian.sal.api.message.Message;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UpgradeTask1MigrateLegacyEvents
extends AuditUpgradeTask {
    private static final int BUILD_NUMBER = 1;
    private final LegacyAuditEntityMigrator migrator;
    private final DatabaseAuditConsumer dbAuditConsumer;

    public UpgradeTask1MigrateLegacyEvents(LegacyAuditEntityMigrator migrator, DatabaseAuditConsumer dbAuditConsumer) {
        this.migrator = Objects.requireNonNull(migrator);
        this.dbAuditConsumer = Objects.requireNonNull(dbAuditConsumer);
    }

    public Collection<Message> doUpgrade() {
        if (this.dbAuditConsumer.isEnabled()) {
            this.migrator.migrate((AuditConsumer)this.dbAuditConsumer);
        }
        return Collections.emptyList();
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Migrate legacy audit events to Advanced Auditing";
    }
}

