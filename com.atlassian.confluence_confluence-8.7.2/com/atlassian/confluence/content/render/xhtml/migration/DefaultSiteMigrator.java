/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.apache.commons.lang3.time.StopWatch
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.tasks.ContentEntityMigrationTaskWrapper;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.commons.lang3.time.StopWatch;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class DefaultSiteMigrator
implements SiteMigrator,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultSiteMigrator.class);
    public static final int DEFAULT_NUMBER_OF_THREADS = 4;
    public static final int DEFAULT_BATCH_SIZE = 500;
    private static final String THREAD_NAME_PREFIX = DefaultSiteMigrator.class.getSimpleName();
    private final SessionFactory sessionFactory;
    private final PlatformTransactionManager transactionManager;
    private final ExecutorService executor;
    private final ContentDao contentDao;
    private final ExceptionTolerantMigrator migrator;
    private final CacheFlusher cacheFlusher;
    private final BatchableWorkSource<ContentEntityObject> workSource;
    private final String versionCommentProperty;
    private final String defaultVersionComment;
    private final LifecycleAwareSchedulerService lifecycleAwareSchedulerService;
    private volatile boolean inProgress = false;
    public static final String BATCH_SIZE_PROP = "confluence.wiki.migration.batch.size";
    private static final String NUM_MIGRATION_THREADS_PROP = "confluence.wiki.migration.threads";
    public static final String WIKI_MIGRATION_VERSION_COMMENT_PROP = "confluence.wiki.migration.versioncomment";
    public static final String DEFAULT_WIKI_MIGRATION_VERSION_COMMENT = "Migrated to Confluence 4.0";
    public static final String XHTML_MIGRATION_VERSION_COMMENT_PROP = "confluence.xhtml.migration.versioncomment";
    public static final String DEFAULT_XHTML_MIGRATION_VERSION_COMMENT = "Migrated to Confluence 5.3";

    public DefaultSiteMigrator(int numberOfThreads, SessionFactory sessionFactory, PlatformTransactionManager transactionManager, ContentDao contentDao, ExceptionTolerantMigrator migrator, CacheFlusher cacheFlusher, BatchableWorkSource<ContentEntityObject> workSource, String versionCommentProperty, String defaultVersionCommment, LifecycleAwareSchedulerService lifecycleAwareSchedulerService) {
        this.cacheFlusher = cacheFlusher;
        this.sessionFactory = sessionFactory;
        this.transactionManager = transactionManager;
        this.contentDao = contentDao;
        this.migrator = migrator;
        this.workSource = workSource;
        this.executor = ConfluenceExecutors.newFixedThreadPool(numberOfThreads, ThreadFactories.namedThreadFactory((String)THREAD_NAME_PREFIX, (ThreadFactories.Type)ThreadFactories.Type.USER, (int)5));
        this.versionCommentProperty = versionCommentProperty;
        this.defaultVersionComment = defaultVersionCommment;
        this.lifecycleAwareSchedulerService = Objects.requireNonNull(lifecycleAwareSchedulerService);
    }

    public static int getNumberOfThreads() {
        return Integer.getInteger(NUM_MIGRATION_THREADS_PROP, 4);
    }

    public static int getBatchSize() {
        return Integer.getInteger(BATCH_SIZE_PROP, 500);
    }

    @Override
    public ExceptionReport migrateSite() throws MigrationException {
        this.assertMigrationNotAlreadyInProgress();
        ExceptionReport report = new ExceptionReport();
        try {
            ScheduleUtil.pauseAndFlushSchedulerService(this.lifecycleAwareSchedulerService);
            ArrayList<Future<Integer>> migrationResults = new ArrayList<Future<Integer>>();
            TransactionTemplate template = this.createTxTemplate();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            int totalContentCount = this.getTotalContentCount(template);
            this.workSource.reset(totalContentCount);
            ContentEntityMigrationTaskWrapper.LoggingCallback loggingCallback = new ContentEntityMigrationTaskWrapper.LoggingCallback(totalContentCount);
            for (int i = 0; i < this.workSource.numberOfBatches(); ++i) {
                TransactionWrappingMigratorRunnable migratorRunnable = this.createMigratorTask(report, loggingCallback);
                Future<Integer> migrationResult = this.executor.submit(migratorRunnable);
                migrationResults.add(migrationResult);
            }
            int migrationCount = DefaultSiteMigrator.waitForAllTasksToComplete(migrationResults);
            stopWatch.split();
            int pct = totalContentCount == 0 ? 0 : migrationCount * 100 / totalContentCount;
            log.info(String.format("%,d content items out of a total of %,d required migration (%d%%). Elapsed time was %s", migrationCount, totalContentCount, pct, stopWatch.toSplitString()));
        }
        catch (InterruptedException ex) {
            log.warn("Interrupted while in the process of migrating wiki content. Please be aware that the migration is probably still running but an exception must be thrown since there is now no way of knowing when it is complete.");
            throw new MigrationException(ex);
        }
        catch (SchedulerServiceException | RuntimeException | ExecutionException e) {
            throw new MigrationException(e);
        }
        finally {
            try {
                this.lifecycleAwareSchedulerService.start();
            }
            catch (SchedulerServiceException e) {
                log.error("The scheduler service could not be resumed", (Throwable)e);
            }
            this.setInProgress(false);
        }
        if (report.isErrored()) {
            log.warn("Completed migration of the site from wiki to XHTML with some errors.");
        } else {
            log.info("Completed site migration with no errors.");
        }
        return report;
    }

    private static int waitForAllTasksToComplete(List<Future<Integer>> migrationResults) throws InterruptedException, ExecutionException {
        int migrationCount = 0;
        for (Future<Integer> migrationResult : migrationResults) {
            Integer taskResult = migrationResult.get();
            if (taskResult == null) continue;
            migrationCount += taskResult.intValue();
        }
        return migrationCount;
    }

    private TransactionWrappingMigratorRunnable createMigratorTask(ExceptionReport report, ContentEntityMigrationTaskWrapper.LoggingCallback loggingCallback) {
        ContentEntityMigrationTaskWrapper migratorCallback = new ContentEntityMigrationTaskWrapper(this.migrator, report, this.contentDao, this.workSource, this.getNewVersionComment(), loggingCallback, this.sessionFactory);
        return new TransactionWrappingMigratorRunnable(this.transactionManager, migratorCallback);
    }

    private String getNewVersionComment() {
        return System.getProperty(this.versionCommentProperty, this.defaultVersionComment);
    }

    private int getTotalContentCount(TransactionTemplate template) {
        return Objects.requireNonNull((Integer)template.execute((TransactionCallback)new TransactionCallback<Integer>(){

            public Integer doInTransaction(TransactionStatus status) {
                return DefaultSiteMigrator.this.workSource.getTotalSize();
            }
        }));
    }

    private TransactionTemplate createTxTemplate() {
        TransactionTemplate template = new TransactionTemplate(this.transactionManager);
        template.setPropagationBehavior(0);
        template.setName("tx-template-content-counting");
        template.setReadOnly(true);
        return template;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void assertMigrationNotAlreadyInProgress() {
        DefaultSiteMigrator defaultSiteMigrator = this;
        synchronized (defaultSiteMigrator) {
            if (this.isSiteMigrationInProgress()) {
                throw new IllegalStateException("A site migration is currently in progress.");
            }
            this.setInProgress(true);
        }
    }

    @Override
    public boolean isSiteMigrationInProgress() {
        return this.inProgress;
    }

    private void setInProgress(boolean value) {
        this.inProgress = value;
    }

    public void destroy() throws Exception {
        this.executor.shutdownNow();
    }

    private final class TransactionWrappingMigratorRunnable
    implements Callable<Integer> {
        private final PlatformTransactionManager transactionManager;
        private final TransactionCallback callback;

        private TransactionWrappingMigratorRunnable(PlatformTransactionManager transactionManager, TransactionCallback migratorCallback) {
            this.transactionManager = transactionManager;
            this.callback = migratorCallback;
        }

        @Override
        public Integer call() {
            TransactionTemplate template = new TransactionTemplate(this.transactionManager);
            template.setPropagationBehavior(0);
            template.setName("tx-template-" + this.callback);
            try {
                Integer n = (Integer)template.execute(this.callback);
                return n;
            }
            finally {
                DefaultSiteMigrator.this.cacheFlusher.flushCaches();
            }
        }
    }
}

