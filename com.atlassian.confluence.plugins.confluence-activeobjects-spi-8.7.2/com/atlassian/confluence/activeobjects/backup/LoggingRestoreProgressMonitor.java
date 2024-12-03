/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.RestoreProgressMonitor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.activeobjects.backup;

import com.atlassian.activeobjects.spi.RestoreProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingRestoreProgressMonitor
implements RestoreProgressMonitor {
    private static final Logger log = LoggerFactory.getLogger(LoggingRestoreProgressMonitor.class);

    public void beginRestore() {
        log.warn("Begin restoring Active Objects backup, adjust log level for com.atlassian.confluence.activeobjects.backup for more detailed logging.");
    }

    public void endRestore() {
        log.warn("Completed restoring Active Objects Backup.");
    }

    public void beginDatabaseInformationRestore() {
        log.info("Begin restoring database information");
    }

    public void beginTableDefinitionsRestore() {
        log.info("Begin restoring table definitions");
    }

    public void beginTablesRestore() {
        log.info("Begin restoring tables");
    }

    public void beginTableDataRestore(String tableName) {
        log.info("Begin restoring table data for : {}", (Object)tableName);
    }

    public void beginTableCreationRestore(String tableName) {
        log.info("Begin table creation for : {}", (Object)tableName);
    }

    public void beginTableRowRestore() {
    }

    public void endDatabaseInformationRestore() {
        log.info("Completed database information restore");
    }

    public void endTableDefinitionsRestore() {
        log.info("Completed table definitions restore");
    }

    public void endTablesRestore() {
        log.info("Completed restoring tables");
    }

    public void endTableDataRestore(String tableName) {
        log.info("Completed table data restore for : {}", (Object)tableName);
    }

    public void endTableCreationRestore(String tableName) {
        log.info("Completed table creation for : {}", (Object)tableName);
    }

    public void endTableRowRestore() {
    }

    public void updateTotalNumberOfTablesToRestore(int tableCount) {
        log.info("Update total number of tables to restore to : {}", (Object)tableCount);
    }
}

