/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.Backup
 *  com.atlassian.activeobjects.spi.BackupProgressMonitor
 *  com.atlassian.activeobjects.spi.HotRestartService
 *  com.atlassian.activeobjects.spi.RestoreProgressMonitor
 *  com.atlassian.activeobjects.spi.TransactionSynchronisationManager
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.plugin.BackupRestoreProvider
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.activeobjects.backup;

import com.atlassian.activeobjects.spi.Backup;
import com.atlassian.activeobjects.spi.BackupProgressMonitor;
import com.atlassian.activeobjects.spi.HotRestartService;
import com.atlassian.activeobjects.spi.RestoreProgressMonitor;
import com.atlassian.activeobjects.spi.TransactionSynchronisationManager;
import com.atlassian.confluence.activeobjects.backup.LoggingBackupProgressMonitor;
import com.atlassian.confluence.activeobjects.backup.LoggingRestoreProgressMonitor;
import com.atlassian.confluence.activeobjects.exception.ActiveObjectsRestoreTimeoutException;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProvider;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveObjectsBackupRestoreProvider
implements BackupRestoreProvider {
    private Backup backup;
    private TransactionSynchronisationManager transactionSyncManager;
    private HotRestartService hotRestartService;
    private static final Logger log = LoggerFactory.getLogger(ActiveObjectsBackupRestoreProvider.class);

    public void backup(OutputStream os) throws ImportExportException {
        try {
            this.backup.save(os, (BackupProgressMonitor)new LoggingBackupProgressMonitor());
        }
        catch (Exception ex) {
            throw new ImportExportException((Throwable)ex);
        }
    }

    public void restore(InputStream is) throws ImportExportException {
        try {
            Runnable restartAoCallback = new Runnable(){

                @Override
                public void run() {
                    log.info("Calling active objects hot restart event.");
                    Future initialisePromises = ActiveObjectsBackupRestoreProvider.this.hotRestartService.doHotRestart();
                    try {
                        initialisePromises.get(180L, TimeUnit.SECONDS);
                        log.info("Calling active objects hot restart event. Done");
                    }
                    catch (Exception e) {
                        throw new ActiveObjectsRestoreTimeoutException("Could not initialise AO, will stop the process", e);
                    }
                }
            };
            this.transactionSyncManager.runOnSuccessfulCommit(restartAoCallback);
            this.transactionSyncManager.runOnRollBack(restartAoCallback);
            this.backup.restore(is, (RestoreProgressMonitor)new LoggingRestoreProgressMonitor());
        }
        catch (Exception ex) {
            throw new ImportExportException((Throwable)ex);
        }
    }

    public void setBackup(Backup backup) {
        this.backup = backup;
    }

    public void setHotRestartService(HotRestartService hotRestartService) {
        this.hotRestartService = hotRestartService;
    }

    public void setTransactionSynchManager(TransactionSynchronisationManager tranSyncManager) {
        this.transactionSyncManager = tranSyncManager;
    }
}

