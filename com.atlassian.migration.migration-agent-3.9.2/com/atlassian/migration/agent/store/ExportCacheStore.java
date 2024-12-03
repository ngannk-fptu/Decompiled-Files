/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.ExportCacheEntry;
import com.atlassian.migration.agent.entity.ExportType;
import java.util.List;
import java.util.Optional;

public interface ExportCacheStore {
    public void createExportCacheEntry(ExportCacheEntry var1);

    public Optional<ExportCacheEntry> getExportCacheEntry(String var1, ExportType var2, boolean var3, String var4);

    public void deleteExportCacheEntry(String var1);

    public List<ExportCacheEntry> deleteExportCacheEntriesOlderThan(long var1);
}

