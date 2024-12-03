/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

public interface BackupProgressMonitor {
    public void beginBackup();

    public void endBackup();

    public void beginDatabaseInformationBackup();

    public void beginTableDefinitionsBackup();

    public void beginTablesBackup();

    public void beginTableBackup(String var1);

    public void updateTotalNumberOfTablesToBackup(int var1);

    public void endDatabaseInformationBackup();

    public void endTableDefinitionsBackup();

    public void endTablesBackup();

    public void endTableBackup(String var1);
}

