/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.api.impl.service.audit.migration;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator;
import com.atlassian.confluence.api.impl.service.audit.migration.MigrationStatusManager;
import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import com.atlassian.confluence.internal.audit.AuditFormatConverter;
import com.atlassian.confluence.internal.audit.persistence.dao.AuditRecordDao;
import com.atlassian.confluence.util.LoggingUncaughtExceptionHandler;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ConfluenceAuditEntityMigrator
implements LegacyAuditEntityMigrator {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceAuditEntityMigrator.class);
    private static final int DEFAULT_NUM_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private static final int NUM_THREADS = Integer.getInteger("legacy.audit.migrator.num.threads", DEFAULT_NUM_THREADS);
    private static final int BATCH_SIZE = Integer.getInteger("legacy.audit.migrator.batch.size", 1000);
    private static final int WAIT_TIME_MS = 3000;
    private final AuditRecordDao auditRecordDao;
    private final AuditFormatConverter auditFormatConverter;
    private final PlatformTransactionManager transactionManager;
    private final BlockingQueue<List<Long>> entitiesQueue = new ArrayBlockingQueue<List<Long>>(NUM_THREADS);

    public ConfluenceAuditEntityMigrator(AuditRecordDao auditRecordDao, AuditFormatConverter auditFormatConverter, PlatformTransactionManager transactionManager) {
        this.auditRecordDao = Objects.requireNonNull(auditRecordDao);
        this.auditFormatConverter = Objects.requireNonNull(auditFormatConverter);
        this.transactionManager = Objects.requireNonNull(transactionManager);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void migrate(@Nonnull AuditConsumer auditConsumer) {
        List<Long> outstandingIds = this.auditRecordDao.fetchAllRecordIds();
        if (outstandingIds.size() > 0) {
            log.info("Found {} audit records to migrate.", (Object)outstandingIds.size());
            ExecutorService migrationExecutor = Executors.newFixedThreadPool(NUM_THREADS, ThreadFactories.named((String)"audit-migrator").type(ThreadFactories.Type.DAEMON).uncaughtExceptionHandler((Thread.UncaughtExceptionHandler)LoggingUncaughtExceptionHandler.INSTANCE).build());
            MigrationStatusManager statusUpdater = new MigrationStatusManager(outstandingIds.size(), arg_0 -> ((Logger)log).info(arg_0));
            IntStream.rangeClosed(1, NUM_THREADS).forEach(tid -> migrationExecutor.execute(new Migrator(auditConsumer, statusUpdater)));
            try {
                for (List ids : Lists.partition(outstandingIds, (int)BATCH_SIZE)) {
                    try {
                        this.entitiesQueue.put(ids);
                    }
                    catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Interrupted while migrating", (Throwable)e);
                        migrationExecutor.shutdownNow();
                        return;
                    }
                }
                statusUpdater.waitUntilCompletion(3000L);
            }
            finally {
                migrationExecutor.shutdownNow();
            }
        }
        log.info("Found nothing to migrate. Proceeding.");
    }

    private class Migrator
    implements Runnable {
        private final AuditConsumer auditConsumer;
        private final BiConsumer<Integer, Integer> statusUpdater;

        private Migrator(AuditConsumer auditConsumer, BiConsumer<Integer, Integer> statusUpdater) {
            this.auditConsumer = Objects.requireNonNull(auditConsumer);
            this.statusUpdater = Objects.requireNonNull(statusUpdater);
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.doMigrate();
                }
                catch (RuntimeException rte) {
                    log.warn("Error migrating some audit events", (Throwable)rte);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void doMigrate() {
            try {
                int successCount;
                List<Long> entityIds = ConfluenceAuditEntityMigrator.this.entitiesQueue.take();
                Integer numMigrated = null;
                try {
                    numMigrated = (Integer)this.getTransactionTemplate().execute(status -> {
                        List<AuditRecordEntity> oldEntities = ConfluenceAuditEntityMigrator.this.auditRecordDao.fetchByIds(entityIds);
                        ArrayList<AuditRecordEntity> convertedEntities = new ArrayList<AuditRecordEntity>(oldEntities.size());
                        ArrayList<AuditEntity> newEntities = new ArrayList<AuditEntity>(oldEntities.size());
                        for (AuditRecordEntity oldEntity : oldEntities) {
                            try {
                                AuditEntity newEntity = ConfluenceAuditEntityMigrator.this.auditFormatConverter.toAuditEntity(oldEntity, CoverageArea.END_USER_ACTIVITY, CoverageLevel.BASE);
                                newEntities.add(newEntity);
                                convertedEntities.add(oldEntity);
                            }
                            catch (RuntimeException rte) {
                                log.warn("Error converting legacy audit record to Atlassian Audit", (Throwable)rte);
                            }
                        }
                        this.auditConsumer.accept(newEntities);
                        ConfluenceAuditEntityMigrator.this.auditRecordDao.deleteRecords(convertedEntities);
                        return newEntities.size();
                    });
                    successCount = numMigrated != null ? numMigrated : 0;
                }
                catch (Throwable throwable) {
                    int successCount2 = numMigrated != null ? numMigrated : 0;
                    this.statusUpdater.accept(successCount2, entityIds.size() - successCount2);
                    throw throwable;
                }
                this.statusUpdater.accept(successCount, entityIds.size() - successCount);
            }
            catch (InterruptedException e) {
                log.debug("Interrupted", (Throwable)e);
                Thread.currentThread().interrupt();
            }
        }

        private TransactionTemplate getTransactionTemplate() {
            TransactionTemplate tt = new TransactionTemplate(ConfluenceAuditEntityMigrator.this.transactionManager);
            tt.setPropagationBehavior(0);
            return tt;
        }
    }
}

