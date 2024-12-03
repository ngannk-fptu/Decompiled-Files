/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.RestoreProgressMonitor
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.spi.RestoreProgressMonitor;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import com.google.common.base.Preconditions;

final class ActiveObjectsRestoreProgressMonitor
implements ProgressMonitor {
    private final RestoreProgressMonitor backupProgressMonitor;

    ActiveObjectsRestoreProgressMonitor(RestoreProgressMonitor restoreProgressMonitor) {
        this.backupProgressMonitor = (RestoreProgressMonitor)Preconditions.checkNotNull((Object)restoreProgressMonitor);
    }

    @Override
    public void begin(Object ... args) {
        this.backupProgressMonitor.beginRestore();
    }

    @Override
    public void end(Object ... args) {
        this.backupProgressMonitor.endRestore();
    }

    @Override
    public void begin(ProgressMonitor.Task task, Object ... args) {
        switch (task) {
            case DATABASE_INFORMATION: {
                this.backupProgressMonitor.beginDatabaseInformationRestore();
                break;
            }
            case TABLE_DEFINITION: {
                this.backupProgressMonitor.beginTableDefinitionsRestore();
                break;
            }
            case TABLES_DATA: {
                this.backupProgressMonitor.beginTablesRestore();
                break;
            }
            case TABLE_DATA: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.beginTableDataRestore((String)args[0]);
                break;
            }
            case TABLE_CREATION: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.beginTableCreationRestore((String)args[0]);
                break;
            }
            case TABLE_ROW: {
                this.backupProgressMonitor.beginTableRowRestore();
            }
        }
    }

    @Override
    public void end(ProgressMonitor.Task task, Object ... args) {
        switch (task) {
            case DATABASE_INFORMATION: {
                this.backupProgressMonitor.endDatabaseInformationRestore();
                break;
            }
            case TABLE_DEFINITION: {
                this.backupProgressMonitor.endTableDefinitionsRestore();
                break;
            }
            case TABLES_DATA: {
                this.backupProgressMonitor.endTablesRestore();
                break;
            }
            case TABLE_DATA: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.endTableDataRestore((String)args[0]);
                break;
            }
            case TABLE_CREATION: {
                Preconditions.checkArgument((args.length == 1 ? 1 : 0) != 0);
                Preconditions.checkArgument((boolean)(args[0] instanceof String));
                this.backupProgressMonitor.endTableCreationRestore((String)args[0]);
                break;
            }
            case TABLE_ROW: {
                this.backupProgressMonitor.endTableRowRestore();
            }
        }
    }

    @Override
    public void totalNumberOfTables(int size) {
        this.backupProgressMonitor.updateTotalNumberOfTablesToRestore(size);
    }
}

