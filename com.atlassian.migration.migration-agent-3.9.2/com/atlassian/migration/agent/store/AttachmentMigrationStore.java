/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.AttachmentMigration;
import java.util.Optional;

public interface AttachmentMigrationStore {
    public void saveMigration(AttachmentMigration var1);

    public Optional<AttachmentMigration> findMigration(long var1, String var3);

    public int deleteMigrationsByCloudId(String var1);

    public long countRetrievedAttachmentMigrationsBySpaceKeyAndCloudId(String var1, String var2);

    public long countUnRetrievableAttachmentMigrationsBySpaceKeyAndCloudId(String var1, String var2);

    public long countAlreadyMigratedAttachmentsBySpaceKeyAndCloudId(String var1, String var2);
}

