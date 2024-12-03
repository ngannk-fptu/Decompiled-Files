/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Stopwatch
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 */
package com.atlassian.confluence.impl.backuprestore.backup;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.event.events.admin.AsyncExportFinishedEvent;
import com.atlassian.confluence.impl.backuprestore.BackupRestoreFilesystemManager;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutor;
import com.atlassian.confluence.impl.backuprestore.ParallelTasksExecutorFactory;
import com.atlassian.confluence.impl.backuprestore.backup.AbstractBackupService;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.DatabaseExporterHelper;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.SiteExportersCreator;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.site.WholeTableExporter;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfoFactory;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.JobStatisticsInfo;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollectorFactory;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.plugin.BackupRestoreProviderManager;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Stopwatch;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class SiteBackupService
extends AbstractBackupService {
    private static final Logger log = LoggerFactory.getLogger(SiteBackupService.class);
    public static final int BACKUP_THREADS_NUMBER = Integer.getInteger("confluence.xmlbackup.site.number-of-threads", 16);
    private final ExportableEntityInfoFactory exportableEntityInfoFactory;
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager platformTransactionManager;
    private final BackupContainerWriterFactory backupContainerWriterFactory;
    private final BackupRestoreProviderManager backupRestoreProviderManager;
    private final EventPublisher eventPublisher;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;
    private final StatisticsCollectorFactory statisticsCollectorFactory;
    private final ParallelTasksExecutorFactory parallelTasksExecutorFactory;

    public SiteBackupService(SessionFactory sessionFactory, PlatformTransactionManager platformTransactionManager, BackupContainerWriterFactory backupContainerWriterFactory, BackupRestoreProviderManager backupRestoreProviderManager, EventPublisher eventPublisher, ExportableEntityInfoFactory exportableEntityInfoFactory, BackupRestoreJobDao backupRestoreJobDao, BackupRestoreFilesystemManager backupRestoreFilesystemManager, StatisticsCollectorFactory statisticsCollectorFactory, ParallelTasksExecutorFactory parallelTasksExecutorFactory) {
        super(backupRestoreJobDao);
        this.exportableEntityInfoFactory = exportableEntityInfoFactory;
        this.sessionFactory = sessionFactory;
        this.platformTransactionManager = platformTransactionManager;
        this.backupContainerWriterFactory = backupContainerWriterFactory;
        this.backupRestoreProviderManager = backupRestoreProviderManager;
        this.eventPublisher = eventPublisher;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
        this.statisticsCollectorFactory = statisticsCollectorFactory;
        this.parallelTasksExecutorFactory = parallelTasksExecutorFactory;
    }

    @Override
    public void doBackupSynchronously(BackupRestoreJob job, BackupRestoreSettings settings) throws BackupRestoreException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("SITE backup [{}] has started.", (Object)job.getId());
        this.validateBackupJob(job, settings, JobScope.SITE);
        try (ParallelTasksExecutor parallelTasksExecutor = this.parallelTasksExecutorFactory.create(job, BACKUP_THREADS_NUMBER);){
            File outputFile;
            try (BackupContainerWriter containerWriter = this.backupContainerWriterFactory.createBackupContainerWriter(settings.getFileName());){
                outputFile = this.performBackup(job, settings, parallelTasksExecutor, containerWriter);
            }
            log.info("SITE backup [{}] is now moving the backup zip to the restore directory.", (Object)job.getId());
            String movedFileName = this.backupRestoreFilesystemManager.moveExistingLocalFileToRestoreDir(outputFile, settings.getJobScope());
            this.postBackupJobUpdate(movedFileName, settings, job.getId(), parallelTasksExecutor);
            log.info("SITE backup [{}] backup zip {} is now available in the restore directory.", (Object)job.getId(), (Object)movedFileName);
            log.info("SITE backup [{}] finished in {}.", (Object)job.getId(), (Object)stopwatch);
            this.eventPublisher.publish((Object)new AsyncExportFinishedEvent(this, "TYPE_ALL_DATA", ExportScope.ALL.name(), null));
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BackupRestoreException(e);
        }
    }

    @Override
    protected JobStatisticsInfo backupAllEntitiesAndAttachments(BackupRestoreJob job, BackupRestoreSettings settings, ParallelTasksExecutor parallelTasksExecutor, BackupContainerWriter containerWriter) throws ExecutionException, InterruptedException, TimeoutException {
        try (StatisticsCollector statisticsCollector = this.statisticsCollectorFactory.createStatisticsCollector(job.getId(), job.getJobScope(), job.getJobOperation(), this.eventPublisher, this.backupRestoreJobDao, parallelTasksExecutor);){
            statisticsCollector.createEmptyStatisticsRecord();
            HibernateMetadataHelper hibernateMetadataHelper = new HibernateMetadataHelper(this.exportableEntityInfoFactory, this.sessionFactory, false);
            DatabaseExporterHelper databaseExporterHelper = new DatabaseExporterHelper(containerWriter, hibernateMetadataHelper, parallelTasksExecutor, this.platformTransactionManager, this.sessionFactory, statisticsCollector);
            boolean includeAttachments = !settings.isSkipAttachments();
            SiteExportersCreator siteExportersCreator = new SiteExportersCreator(new ExporterFactory(databaseExporterHelper, statisticsCollector), databaseExporterHelper.getHibernateMetadataHelper(), includeAttachments);
            databaseExporterHelper.setAllExporters(siteExportersCreator.getSiteExporters());
            for (WholeTableExporter exporter : siteExportersCreator.getWholeTableExporters()) {
                databaseExporterHelper.runTaskAsync(() -> {
                    exporter.exportAllRecords();
                    return null;
                }, "exporting exporter " + exporter.getExporterName());
            }
            databaseExporterHelper.runTaskAsync(() -> {
                this.writePluginData(containerWriter, databaseExporterHelper);
                return null;
            }, "exporting plugin data");
            parallelTasksExecutor.waitUntilAllStageJobsComplete();
            statisticsCollector.setTotalNumberOfObjects(statisticsCollector.getPersistedObjectsCount());
            StatisticsCollector statisticsCollector2 = statisticsCollector;
            return statisticsCollector2;
        }
    }

    protected void writePluginData(BackupContainerWriter containerWriter, DatabaseExporterHelper databaseExporterHelper) {
        databaseExporterHelper.doInReadOnlyTransaction(tx -> {
            try {
                containerWriter.addPluginModuleData(this.backupRestoreProviderManager.getModuleDescriptors());
            }
            catch (BackupRestoreException e) {
                throw new RuntimeException("Unable to backup plugin data: " + e.getMessage(), e);
            }
            return null;
        });
    }
}

