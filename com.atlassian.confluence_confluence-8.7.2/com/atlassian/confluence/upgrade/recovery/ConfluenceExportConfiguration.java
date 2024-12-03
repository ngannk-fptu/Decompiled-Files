/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dbexporter.ConnectionProvider
 *  com.atlassian.dbexporter.EntityNameProcessor
 *  com.atlassian.dbexporter.exporter.ExportConfiguration
 *  com.atlassian.dbexporter.progress.ProgressMonitor
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.progress.ProgressMonitor;

public class ConfluenceExportConfiguration
implements ExportConfiguration {
    private final ConnectionProvider connectionProvider;
    private final ProgressMonitor progressMonitor;
    private final EntityNameProcessor entityNameProcessor;

    public ConfluenceExportConfiguration(ConnectionProvider connectionProvider, ProgressMonitor progressMonitor, EntityNameProcessor entityNameProcessor) {
        this.connectionProvider = connectionProvider;
        this.progressMonitor = progressMonitor;
        this.entityNameProcessor = entityNameProcessor;
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connectionProvider;
    }

    public ProgressMonitor getProgressMonitor() {
        return this.progressMonitor;
    }

    public EntityNameProcessor getEntityNameProcessor() {
        return this.entityNameProcessor;
    }
}

