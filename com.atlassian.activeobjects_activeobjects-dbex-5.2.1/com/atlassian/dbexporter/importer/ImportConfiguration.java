/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.BatchMode;
import com.atlassian.dbexporter.CleanupMode;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.ImportExportConfiguration;

public interface ImportConfiguration
extends ImportExportConfiguration {
    public DatabaseInformation getDatabaseInformation();

    public CleanupMode getCleanupMode();

    public BatchMode getBatchMode();
}

