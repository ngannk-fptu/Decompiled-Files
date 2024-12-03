/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.LifecycleAwareSchedulerService
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.support.TransactionCallback
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.SiteMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.tasks.PageTemplateMigratorTask;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.persistence.dao.PageTemplateDao;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class PageTemplateSiteMigrator
implements SiteMigrator {
    static final Logger log = LoggerFactory.getLogger(PageTemplateSiteMigrator.class);
    public static final int NUM_THREADS = 4;
    private static final String THREAD_NAME_PREFIX = "WikiToXhtmlMigration";
    private final PlatformTransactionManager transactionManager;
    private final ExceptionTolerantMigrator migrator;
    private final PageTemplateDao pageTemplateDao;
    private final PageTemplateManager pageTemplateManager;
    private final LifecycleAwareSchedulerService lifecycleAwareSchedulerService;
    private volatile boolean inProgress = false;
    private final int numberOfThreads;
    private static final String NUM_MIGRATION_THREADS_PROP = "confluence.wiki.migration.threads";
    private final Predicate<PageTemplate> pageTemplateMigrationSelector;

    public PageTemplateSiteMigrator(int numberOfThreads, PlatformTransactionManager transactionManager, ExceptionTolerantMigrator migrator, PageTemplateDao pageTemplateDao, PageTemplateManager pageTemplateManager, LifecycleAwareSchedulerService lifecycleAwareSchedulerService, Predicate<PageTemplate> pageTemplateMigrationSelector) {
        this.numberOfThreads = Integer.getInteger(NUM_MIGRATION_THREADS_PROP, numberOfThreads);
        this.transactionManager = transactionManager;
        this.migrator = migrator;
        this.pageTemplateDao = pageTemplateDao;
        this.pageTemplateManager = pageTemplateManager;
        this.lifecycleAwareSchedulerService = Objects.requireNonNull(lifecycleAwareSchedulerService);
        this.pageTemplateMigrationSelector = pageTemplateMigrationSelector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ExceptionReport migrateSite() throws MigrationException {
        PageTemplateSiteMigrator pageTemplateSiteMigrator = this;
        synchronized (pageTemplateSiteMigrator) {
            if (this.isSiteMigrationInProgress()) {
                throw new IllegalStateException("A site migration is currently in progress.");
            }
            this.setInProgress(true);
        }
        ExecutorService executor = ConfluenceExecutors.newFixedThreadPool(this.numberOfThreads, ThreadFactories.namedThreadFactory((String)THREAD_NAME_PREFIX, (ThreadFactories.Type)ThreadFactories.Type.USER, (int)5));
        ExceptionReport report = new ExceptionReport();
        try {
            ScheduleUtil.pauseAndFlushSchedulerService(this.lifecycleAwareSchedulerService);
            ArrayList migrationResults = new ArrayList();
            PageTemplateMigratorTask pageTemplateMigratorTask = new PageTemplateMigratorTask(this.migrator, this.pageTemplateDao, this.pageTemplateManager, report, this.pageTemplateMigrationSelector);
            migrationResults.add(executor.submit(new TransactionWrappingMigratorRunnable(this.transactionManager, (TransactionCallback)pageTemplateMigratorTask)));
            for (Future future : migrationResults) {
                future.get();
            }
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
            executor.shutdown();
        }
        if (report.isErrored()) {
            log.warn("Completed migration of page templates from wiki to XHTML with some errors.");
        } else {
            log.info("Completed migration of page templates from wiki to XHTML with no errors.");
        }
        return report;
    }

    @Override
    public boolean isSiteMigrationInProgress() {
        return this.inProgress;
    }

    private void setInProgress(boolean value) {
        this.inProgress = value;
    }

    private static final class TransactionWrappingMigratorRunnable
    implements Runnable {
        private final PlatformTransactionManager transactionManager;
        private final TransactionCallback callback;

        private TransactionWrappingMigratorRunnable(PlatformTransactionManager transactionManager, TransactionCallback migratorCallback) {
            this.transactionManager = transactionManager;
            this.callback = migratorCallback;
        }

        @Override
        public void run() {
            TransactionTemplate template = new TransactionTemplate(this.transactionManager);
            template.setPropagationBehavior(0);
            template.setName("tx-template-" + this.callback);
            template.execute(this.callback);
        }
    }
}

