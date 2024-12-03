/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.google.common.annotations.VisibleForTesting
 *  org.hibernate.FlushMode
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.TransactionStatus
 *  org.springframework.transaction.support.TransactionCallback
 */
package com.atlassian.confluence.content.render.xhtml.migration.tasks;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.content.render.xhtml.migration.BatchException;
import com.atlassian.confluence.content.render.xhtml.migration.BatchTask;
import com.atlassian.confluence.content.render.xhtml.migration.BatchableWorkSource;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.migration.ExceptionTolerantMigrator;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ContentMigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.ExceptionReport;
import com.atlassian.confluence.content.render.xhtml.migration.exceptions.MigrationException;
import com.atlassian.confluence.content.render.xhtml.migration.macro.ContentEntityMigrationBatchTask;
import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class ContentEntityMigrationTaskWrapper
implements TransactionCallback {
    private static final Logger log = LoggerFactory.getLogger(ContentEntityMigrationTaskWrapper.class);
    private final ExceptionReport report;
    private final ContentDao contentDao;
    private final BatchableWorkSource<ContentEntityObject> workSource;
    private final BatchTask<ContentEntityObject> batchTask;
    private final LoggingCallback loggingCallback;
    private final SessionFactory sessionFactory;

    @Deprecated
    public ContentEntityMigrationTaskWrapper(ExceptionTolerantMigrator migrator, ExceptionReport report, ContentDao contentDao, BatchableWorkSource<ContentEntityObject> workSource, CacheManager ignoredCacheManager, String versionComment, LoggingCallback loggingCallback) {
        this(report, contentDao, workSource, new ContentEntityMigrationBatchTask(migrator, contentDao, versionComment), loggingCallback, null);
    }

    @Deprecated
    public ContentEntityMigrationTaskWrapper(ExceptionTolerantMigrator migrator, ExceptionReport report, ContentDao contentDao, BatchableWorkSource<ContentEntityObject> workSource, String versionComment, LoggingCallback loggingCallback) {
        this(migrator, report, contentDao, workSource, versionComment, loggingCallback, null);
    }

    public ContentEntityMigrationTaskWrapper(ExceptionTolerantMigrator migrator, ExceptionReport report, ContentDao contentDao, BatchableWorkSource<ContentEntityObject> workSource, String versionComment, LoggingCallback loggingCallback, SessionFactory sessionFactory) {
        this(report, contentDao, workSource, new ContentEntityMigrationBatchTask(migrator, contentDao, versionComment), loggingCallback, sessionFactory);
    }

    @VisibleForTesting
    ContentEntityMigrationTaskWrapper(ExceptionReport report, ContentDao contentDao, BatchableWorkSource<ContentEntityObject> workSource, ContentEntityMigrationBatchTask batchTask, LoggingCallback loggingCallback, SessionFactory sessionFactory) {
        this.report = report;
        this.contentDao = contentDao;
        this.workSource = workSource;
        this.batchTask = batchTask;
        this.loggingCallback = loggingCallback;
        this.sessionFactory = sessionFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer doInTransaction(TransactionStatus status) {
        FlushMode originalFlushMode = this.getSessionFlushMode();
        try {
            List<ContentEntityObject> batch = this.workSource.getBatch();
            this.setSessionFlushMode(FlushMode.COMMIT);
            int migrationCount = this.migrateBatch(batch);
            Integer n = migrationCount;
            return n;
        }
        catch (RuntimeException ex) {
            log.error("{}: Exception while fetching batch from work source. Batch will be skipped, and the content in that batch will be left unmigrated. Exception message: {}", (Object)Thread.currentThread().getName(), (Object)ex.getMessage());
            this.report.addUncategorizedMigrationException(new MigrationException("Error while fetching batched entities from work source", ex));
            Integer n = 0;
            return n;
        }
        finally {
            this.setSessionFlushMode(originalFlushMode);
        }
    }

    private int migrateBatch(List<ContentEntityObject> batch) {
        try {
            return this.migrateEntities(batch);
        }
        catch (RuntimeException ex) {
            log.info("Exception while migrating batch with ids " + batch.get(0).getId() + " to " + batch.get(batch.size() - 1) + ". At some position within this batch the remaining content will be unmighrated.", (Throwable)ex);
            return 0;
        }
    }

    private int migrateEntities(List<ContentEntityObject> entities) {
        String threadName = Thread.currentThread().getName();
        log.debug("{}: Beginning migration of {} {} to XHTML.", new Object[]{threadName, entities.size(), "contentEntityObject"});
        ArrayList<Exception> exceptions = new ArrayList<Exception>(1);
        int index = 0;
        int migratedEntityCount = 0;
        for (ContentEntityObject entity : entities) {
            try {
                boolean workWasDone = this.batchTask.apply(entity, index++, entities.size());
                if (workWasDone) {
                    ++migratedEntityCount;
                }
            }
            catch (BatchException be) {
                log.debug("Batch exceptions: {}", be.getBatchExceptions(), (Object)be);
                exceptions.addAll(be.getBatchExceptions());
            }
            catch (Exception e) {
                log.error("{}: Unable to set body for entity: {} - With Exception Message: {}", new Object[]{threadName, entity.toString(), e.getMessage()});
                exceptions.add(e);
            }
            if (exceptions.isEmpty()) continue;
            for (Exception ex : exceptions) {
                this.report.addException(new ContentMigrationException(entity, (Throwable)ex));
            }
            exceptions.clear();
        }
        if (this.loggingCallback != null) {
            this.loggingCallback.logProgress(threadName, entities.size(), migratedEntityCount);
        }
        return migratedEntityCount;
    }

    private FlushMode getSessionFlushMode() {
        if (this.sessionFactory != null) {
            return this.sessionFactory.getCurrentSession().getHibernateFlushMode();
        }
        log.warn("Session.getFlushMode attempted on ContentEntityMigrationTaskWrapper without SessionFactory");
        return null;
    }

    private void setSessionFlushMode(FlushMode flushMode) {
        if (this.sessionFactory != null) {
            this.sessionFactory.getCurrentSession().setHibernateFlushMode(flushMode);
        } else {
            log.warn("Session.setFlushMode attempted on ContentEntityMigrationTaskWrapper without SessionFactory");
        }
    }

    public static class LoggingCallback {
        private final int total;
        private volatile int progress;
        private Lock lock = new ReentrantLock();
        private static final int LOCK_WAIT_TIME_SEC = 5;

        public LoggingCallback(int total) {
            this.total = total;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void logProgress(String threadName, int batchSize, int migratedEntityCount) {
            block5: {
                try {
                    if (!this.lock.tryLock(5L, TimeUnit.SECONDS)) break block5;
                    try {
                        this.progress += batchSize;
                        int pctComplete = this.total == 0 ? 100 : this.progress * 100 / this.total;
                        log.info(String.format("%s- Migration progress %,d of %,d pages (%d%%); %,d/%,d in this batch required migration", threadName, this.progress, this.total, pctComplete, migratedEntityCount, batchSize));
                    }
                    finally {
                        this.lock.unlock();
                    }
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            }
        }
    }
}

