/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.BackupProgressMonitor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.spi.BackupProgressMonitor;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.base.Preconditions;

final class ActiveObjectsBackupProgressMonitor
implements ProgressMonitor {
    private final BackupProgressMonitor backupProgressMonitor;

    ActiveObjectsBackupProgressMonitor(BackupProgressMonitor backupProgressMonitor) {
        this.backupProgressMonitor = (BackupProgressMonitor)Preconditions.checkNotNull((Object)backupProgressMonitor);
    }

    @Override
    public void begin(Object ... args) {
        this.backupProgressMonitor.beginBackup();
    }

    @Override
    public void end(Object ... args) {
        this.backupProgressMonitor.endBackup();
    }

    @Override
    public void begin(ProgressMonitor.Task task, Object ... args) {
        switch (task) {
            case DATABASE_INFORMATION: {
                this.backupProgressMonitor.beginDatabaseInformationBackup();
                break;
            }
            case TABLE_DEFINITION: {
                this.backupProgressMonitor.beginTableDefinitionsBackup();
                break;
            }
            case TABLES_DATA: {
                this.backupProgressMonitor.beginTablesBackup();
                break;
            }
            case TABLE_DATA: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.beginTableBackup((String)args[0]);
            }
        }
    }

    @Override
    public void end(ProgressMonitor.Task task, Object ... args) {
        switch (task) {
            case DATABASE_INFORMATION: {
                this.backupProgressMonitor.endDatabaseInformationBackup();
                break;
            }
            case TABLE_DEFINITION: {
                this.backupProgressMonitor.endTableDefinitionsBackup();
                break;
            }
            case TABLES_DATA: {
                this.backupProgressMonitor.endTablesBackup();
                break;
            }
            case TABLE_DATA: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.endTableBackup((String)args[0]);
            }
        }
    }

    @Override
    public void totalNumberOfTables(int size) {
        this.backupProgressMonitor.updateTotalNumberOfTablesToBackup(size);
    }
}

