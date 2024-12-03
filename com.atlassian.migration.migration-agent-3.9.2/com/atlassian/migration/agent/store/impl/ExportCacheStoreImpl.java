/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.ExportCacheEntry;
import com.atlassian.migration.agent.entity.ExportType;
import com.atlassian.migration.agent.entity.WithId;
import com.atlassian.migration.agent.store.ExportCacheStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExportCacheStoreImpl
implements ExportCacheStore {
    private final EntityManagerTemplate tmpl;

    public ExportCacheStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public void createExportCacheEntry(ExportCacheEntry exportCacheEntry) {
        this.tmpl.persist(exportCacheEntry);
    }

    @Override
    public Optional<ExportCacheEntry> getExportCacheEntry(String spaceKey, ExportType exportType, boolean containsUserMigrationTask, String cloudId) {
        return this.tmpl.query(ExportCacheEntry.class, "SELECT entry FROM ExportCacheEntry entry WHERE entry.spaceKey = :spaceKey AND entry.exportType = :exportType AND entry.containsUserMigrationTask = :containsUserMigrationTask AND entry.cloudId = :cloudId ORDER BY entry.snapshotTime").param("spaceKey", (Object)spaceKey).param("exportType", (Object)exportType).param("containsUserMigrationTask", (Object)containsUserMigrationTask).param("cloudId", (Object)cloudId).first();
    }

    @Override
    public void deleteExportCacheEntry(String id) {
        this.tmpl.query("DELETE FROM ExportCacheEntry WHERE id = :idToDelete").param("idToDelete", (Object)id).update();
    }

    @Override
    public List<ExportCacheEntry> deleteExportCacheEntriesOlderThan(long snapshotTime) {
        List<ExportCacheEntry> entries = this.tmpl.query(ExportCacheEntry.class, "SELECT entry FROM ExportCacheEntry entry WHERE entry.snapshotTime < :snapshotTime").param("snapshotTime", (Object)snapshotTime).list();
        if (!entries.isEmpty()) {
            this.tmpl.query("DELETE FROM ExportCacheEntry WHERE id in :idsToDelete").param("idsToDelete", entries.stream().map(WithId::getId).collect(Collectors.toList())).update();
        }
        return entries;
    }
}

