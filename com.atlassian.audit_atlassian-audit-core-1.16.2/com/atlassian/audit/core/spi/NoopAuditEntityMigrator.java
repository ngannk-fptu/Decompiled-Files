/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core.spi;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopAuditEntityMigrator
implements LegacyAuditEntityMigrator {
    private static final Logger log = LoggerFactory.getLogger(NoopAuditEntityMigrator.class);

    public void migrate(@Nonnull AuditConsumer consumer) {
        log.debug("Using Noop Audit Entity Migration Service, no migration scheduled");
    }
}

