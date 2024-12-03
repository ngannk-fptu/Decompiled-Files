/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.core.SchedulerServiceController
 *  com.google.common.annotations.VisibleForTesting
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.DefaultTransactionDefinition
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.BackupRestoreSettings;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.helpers.PluginTemporaryDisabler;
import com.atlassian.confluence.impl.backuprestore.restore.HiLoGeneratorInitialiserOnSiteRestore;
import com.atlassian.confluence.impl.backuprestore.restore.events.OnRestoreEventsSender;
import com.atlassian.confluence.impl.backuprestore.restore.taskrunners.SiteRestoreJobResurrector;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceSchemaCreator;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.SchedulerServiceController;
import com.google.common.annotations.VisibleForTesting;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class PreImportTaskRunner {
    private static final Logger log = LoggerFactory.getLogger(PreImportTaskRunner.class);
    @VisibleForTesting
    static final List<String> PREDEFINED_PLUGIN_KEYS = List.of("com.atlassian.confluence.plugins.synchrony-interop", "com.atlassian.confluence.plugins.collaborative-editing-feedback-plugin", "com.atlassian.confluence.plugins.confluence-collaborative-editor-plugin", "com.atlassian.zdu.confluence-zdu-plugin", "com.addonengine.analytics", "com.atlassian.whisper.atlassian-whisper-plugin");
    @VisibleForTesting
    static final String CUSTOM_PLUGIN_KEYS = "confluence.backuprestore.restore.custom.disabled.plugin.keys";
    private final SessionFactory sessionFactory;
    private final IndexManager indexManager;
    private final SchedulerServiceController clusterSchedulerServiceController;
    private final PlatformTransactionManager transactionManager;
    private final ConfluenceSchemaCreator confluenceSchemaCreator;
    private final CacheFlusher cacheFlusher;
    private final SiteRestoreJobResurrector siteRestoreJobResurrector;
    private final PluginTemporaryDisabler pluginTemporaryDisabler;
    private Cleanup pluginDisabler;
    private final OnRestoreEventsSender onRestoreEventsSender;

    public PreImportTaskRunner(SessionFactory sessionFactory, IndexManager indexManager, SchedulerServiceController clusterSchedulerServiceController, PlatformTransactionManager transactionManager, ConfluenceSchemaCreator confluenceSchemaCreator, CacheFlusher cacheFlusher, SiteRestoreJobResurrector siteRestoreJobResurrector, PluginTemporaryDisabler pluginTemporaryDisabler, OnRestoreEventsSender onRestoreEventsSender) {
        this.sessionFactory = sessionFactory;
        this.indexManager = indexManager;
        this.clusterSchedulerServiceController = clusterSchedulerServiceController;
        this.transactionManager = transactionManager;
        this.confluenceSchemaCreator = confluenceSchemaCreator;
        this.cacheFlusher = cacheFlusher;
        this.siteRestoreJobResurrector = siteRestoreJobResurrector;
        this.pluginTemporaryDisabler = pluginTemporaryDisabler;
        this.onRestoreEventsSender = onRestoreEventsSender;
    }

    public void unIndexAll() {
        log.debug("Deleting search index");
        this.indexManager.unIndexAll();
    }

    public void pauseSchedulerAndFlushJobs() throws BackupRestoreException {
        log.debug("Switching scheduler to standby mode");
        try {
            this.clusterSchedulerServiceController.standby();
            this.clusterSchedulerServiceController.waitUntilIdle(ScheduleUtil.getSchedulerFlushTimeout(), TimeUnit.SECONDS);
        }
        catch (SchedulerServiceException e) {
            throw new BackupRestoreException("Failed to switch off scheduler", e);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void resumeScheduler() {
        try {
            log.debug("Restarting the scheduler");
            this.clusterSchedulerServiceController.start();
            log.debug("The scheduler was successfully restarted");
        }
        catch (SchedulerServiceException e) {
            log.error("Could not restart atlassian-scheduler after the import completed", (Throwable)e);
        }
    }

    public void deleteAllDatabaseContent() {
        this.onRestoreEventsSender.sendLockDatabaseEvent();
        TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager, (TransactionDefinition)new DefaultTransactionDefinition(3));
        transactionTemplate.execute(status -> {
            log.info("Dropping and recreating Confluence Schema");
            this.confluenceSchemaCreator.createSchema(true);
            this.flushCaches();
            AuthenticatedUserThreadLocal.reset();
            return null;
        });
    }

    public void runDatabaseConstraintsTask() {
        log.info("Creating additional DB constraints");
        this.confluenceSchemaCreator.createAdditionalDatabaseConstraints();
        log.info("Additional DB constraints created");
    }

    public void flushCaches() {
        log.info("Flushing all caches");
        this.cacheFlusher.flushCaches();
    }

    public void flushCommitClearSession() {
        Session session;
        try {
            session = this.sessionFactory.getCurrentSession();
        }
        catch (HibernateException e) {
            log.debug("No open session found");
            return;
        }
        if (session != null) {
            try {
                log.info("Flushing session, committing pending transactions and clearing session");
                if (session.getTransaction().isActive()) {
                    session.flush();
                }
                ((SessionImplementor)session).connection().commit();
                log.info("Session flush and commit complete");
                session.clear();
                log.info("Session cleared");
            }
            catch (HibernateException e) {
                log.error("error flushing session", (Throwable)e);
            }
            catch (RuntimeException | SQLException e) {
                log.error("error committing connection", (Throwable)e);
            }
        }
    }

    public void restoreSiteJobRecord(BackupRestoreJob job, BackupRestoreSettings settings, HiLoGeneratorInitialiserOnSiteRestore hiLoGeneratorInitialiserOnSiteRestore) {
        this.siteRestoreJobResurrector.resurrectSiteRestoreJob(job, settings, hiLoGeneratorInitialiserOnSiteRestore);
    }

    public void disablePlugins() {
        log.info("Temporarily disabling plugins during site restore");
        this.pluginDisabler = this.pluginTemporaryDisabler.temporarilyShutdownInterferingPlugins(PreImportTaskRunner.getPluginKeys());
    }

    public void enablePlugins() {
        if (this.pluginDisabler != null) {
            log.info("Enabling the temporarily disabled plugins during site restore");
            this.pluginDisabler.close();
            this.pluginDisabler = null;
        }
    }

    public static List<String> getPluginKeys() {
        List<String> allPluginKeys = Arrays.stream(System.getProperty(CUSTOM_PLUGIN_KEYS, "").split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        allPluginKeys.addAll(PREDEFINED_PLUGIN_KEYS);
        return allPluginKeys;
    }
}

