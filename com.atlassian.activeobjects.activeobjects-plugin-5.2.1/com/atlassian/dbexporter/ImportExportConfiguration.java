/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.ConnectionProvider;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.progress.ProgressMonitor;

public interface ImportExportConfiguration {
    public ConnectionProvider getConnectionProvider();

    public ProgressMonitor getProgressMonitor();

    public EntityNameProcessor getEntityNameProcessor();
}

