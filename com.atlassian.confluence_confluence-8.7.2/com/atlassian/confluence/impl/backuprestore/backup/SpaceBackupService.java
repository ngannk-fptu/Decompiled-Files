/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Stopwatch
 *  javax.annotation.Nonnull
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
import com.atlassian.confluence.impl.backuprestore.backup.exporters.Exporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.ExporterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SpaceDatabaseDataExporter;
import com.atlassian.confluence.impl.backuprestore.backup.exporters.SpaceExportersFactory;
import com.atlassian.confluence.impl.backuprestore.dao.BackupRestoreJobDao;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfoFactory;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.statistics.JobStatisticsInfo;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollector;
import com.atlassian.confluence.impl.backuprestore.statistics.StatisticsCollectorFactory;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Stopwatch;
import java.io.File;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;

public class SpaceBackupService
extends AbstractBackupService {
    private static final Logger log = LoggerFactory.getLogger(SpaceBackupService.class);
    public static final int BACKUP_THREADS_NUMBER = Integer.getInteger("confluence.xmlbackup.space.number-of-threads", 16);
    private static final boolean KEEP_CONTENT_PROPERTY_COLLECTIONS = true;
    private final ExportableEntityInfoFactory exportableEntityInfoFactory;
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager platformTransactionManager;
    private final BackupContainerWriterFactory backupContainerWriterFactory;
    private final SpaceExportersFactory spaceExportersFactory;
    private final EventPublisher eventPublisher;
    private final BackupRestoreFilesystemManager backupRestoreFilesystemManager;
    private final StatisticsCollectorFactory statisticsCollectorFactory;
    private final ParallelTasksExecutorFactory parallelTasksExecutorFactory;

    public SpaceBackupService(@Nonnull SessionFactory sessionFactory, @Nonnull PlatformTransactionManager platformTransactionManager, @Nonnull BackupContainerWriterFactory backupContainerWriterFactory, @Nonnull SpaceExportersFactory spaceExportersFactory, @Nonnull EventPublisher eventPublisher, @Nonnull ExportableEntityInfoFactory exportableEntityInfoFactory, @Nonnull BackupRestoreJobDao backupRestoreJobDao, @Nonnull BackupRestoreFilesystemManager backupRestoreFilesystemManager, @Nonnull StatisticsCollectorFactory statisticsCollectorFactory, @Nonnull ParallelTasksExecutorFactory parallelTasksExecutorFactory) {
        super(backupRestoreJobDao);
        this.exportableEntityInfoFactory = exportableEntityInfoFactory;
        this.sessionFactory = sessionFactory;
        this.platformTransactionManager = platformTransactionManager;
        this.backupContainerWriterFactory = backupContainerWriterFactory;
        this.spaceExportersFactory = spaceExportersFactory;
        this.eventPublisher = eventPublisher;
        this.backupRestoreFilesystemManager = backupRestoreFilesystemManager;
        this.statisticsCollectorFactory = statisticsCollectorFactory;
        this.parallelTasksExecutorFactory = parallelTasksExecutorFactory;
    }

    @Override
    public void doBackupSynchronously(BackupRestoreJob job, BackupRestoreSettings settings) throws BackupRestoreException, InterruptedException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("SPACE backup [{}] with keys {} has started.", (Object)job.getId(), settings.getSpaceKeys());
        this.validateSpaceBackupJob(job, settings);
        try (ParallelTasksExecutor parallelTasksExecutor = this.parallelTasksExecutorFactory.create(job, BACKUP_THREADS_NUMBER);){
            File outputFile;
            try (BackupContainerWriter containerWriter = this.backupContainerWriterFactory.createBackupContainerWriter(settings.getFileName());){
                outputFile = this.performBackup(job, settings, parallelTasksExecutor, containerWriter);
            }
            log.info("SPACE backup [{}] is now moving the backup zip to the restore directory.", (Object)job.getId());
            String movedFileName = this.backupRestoreFilesystemManager.moveExistingLocalFileToRestoreDir(outputFile, settings.getJobScope());
            this.postBackupJobUpdate(movedFileName, settings, job.getId(), parallelTasksExecutor);
            log.info("SPACE backup [{}] backup zip {} is now available in the restore directory.", (Object)job.getId(), (Object)movedFileName);
            log.info("SPACE backup [{}] with keys {} finished in {}.", new Object[]{job.getId(), settings.getSpaceKeys(), stopwatch});
            settings.getSpaceKeys().forEach(spaceKey -> this.eventPublisher.publish((Object)new AsyncExportFinishedEvent(this, "TYPE_XML", ExportScope.SPACE.toString(), (String)spaceKey)));
        }
        catch (InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BackupRestoreException(e);
        }
    }

    protected void validateSpaceBackupJob(BackupRestoreJob job, BackupRestoreSettings settings) {
        super.validateBackupJob(job, settings, JobScope.SPACE);
        if (settings.getSpaceKeys().isEmpty()) {
            throw new IllegalArgumentException("Spaces to backup were not set.");
        }
    }

    @Override
    protected JobStatisticsInfo backupAllEntitiesAndAttachments(BackupRestoreJob job, BackupRestoreSettings settings, ParallelTasksExecutor parallelTasksExecutor, BackupContainerWriter containerWriter) throws ExecutionException, InterruptedException, TimeoutException, BackupRestoreException {
        try (StatisticsCollector statisticsCollector = this.statisticsCollectorFactory.createStatisticsCollector(job.getId(), job.getJobScope(), job.getJobOperation(), this.eventPublisher, this.backupRestoreJobDao, parallelTasksExecutor);){
            statisticsCollector.createEmptyStatisticsRecord();
            HibernateMetadataHelper hibernateMetadataHelper = new HibernateMetadataHelper(this.exportableEntityInfoFactory, this.sessionFactory, true);
            DatabaseExporterHelper databaseExporterHelper = new DatabaseExporterHelper(containerWriter, hibernateMetadataHelper, parallelTasksExecutor, this.platformTransactionManager, this.sessionFactory, statisticsCollector);
            Set<Exporter> exporters = this.spaceExportersFactory.createExporters(databaseExporterHelper, new ExporterFactory(databaseExporterHelper, statisticsCollector), true);
            databaseExporterHelper.setAllExporters(exporters);
            SpaceDatabaseDataExporter spaceDatabaseDataExporter = databaseExporterHelper.findSpaceDatabaseExporters();
            spaceDatabaseDataExporter.export(settings.getSpaceKeys());
            parallelTasksExecutor.waitUntilAllStageJobsComplete();
            statisticsCollector.setTotalNumberOfObjects(statisticsCollector.getPersistedObjectsCount());
            StatisticsCollector statisticsCollector2 = statisticsCollector;
            return statisticsCollector2;
        }
    }
}

