/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.progress.ProgressMonitor;

public interface TableCreator {
    public void create(DatabaseInformation var1, Iterable<Table> var2, EntityNameProcessor var3, ProgressMonitor var4);
}

