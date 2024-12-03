/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.spi.migration;

import com.atlassian.audit.api.AuditConsumer;
import javax.annotation.Nonnull;

public interface LegacyAuditEntityMigrator {
    public void migrate(@Nonnull AuditConsumer var1);
}

