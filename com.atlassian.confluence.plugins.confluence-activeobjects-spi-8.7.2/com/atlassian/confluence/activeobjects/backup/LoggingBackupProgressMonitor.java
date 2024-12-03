/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.BackupProgressMonitor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.activeobjects.backup;

import com.atlassian.activeobjects.spi.BackupProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingBackupProgressMonitor
implements BackupProgressMonitor {
    private static final Logger log = LoggerFactory.getLogger(LoggingBackupProgressMonitor.class);

    public void beginBackup() {
        log.warn("Begin Active objects backup, change log level to INFO for com.atlassian.confluence.activeobjects.backup for more detailed logging.");
    }

    public void endBackup() {
        log.warn("Completed active objects backup.");
    }

    public void beginDatabaseInformationBackup() {
        log.info("Begin database information backup");
    }

    public void beginTableDefinitionsBackup() {
        log.info("Begin table definition backup");
    }

    public void beginTablesBackup() {
        log.info("Begin tables backup");
    }

    public void beginTableBackup(String tableName) {
        log.info("Begin backup for table : {}", (Object)tableName);
    }

    public void updateTotalNumberOfTablesToBackup(int tableCount) {
        log.info("update total number of tables to backup to : " + tableCount);
    }

    public void endDatabaseInformationBackup() {
        log.info("end database information backup");
    }

    public void endTableDefinitionsBackup() {
        log.info("end table definitions backup");
    }

    public void endTablesBackup() {
        log.info("finished tables backup");
    }

    public void endTableBackup(String tableName) {
        log.info("finished backing up table : {}", (Object)tableName);
    }
}

