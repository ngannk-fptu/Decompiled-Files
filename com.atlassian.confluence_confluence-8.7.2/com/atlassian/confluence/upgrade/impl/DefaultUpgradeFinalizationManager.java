/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.upgrade.AbstractUpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager$Run
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.Functions$FailableConsumer
 *  org.apache.commons.lang3.mutable.Mutable
 */
package com.atlassian.confluence.upgrade.impl;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.confluence.cluster.shareddata.SharedDataManager;
import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ZduFinalizationRequestEvent;
import com.atlassian.confluence.internal.persistence.ZduStatusDao;
import com.atlassian.confluence.upgrade.AbstractUpgradeManager;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Functions;
import org.apache.commons.lang3.mutable.Mutable;

public class DefaultUpgradeFinalizationManager
implements UpgradeFinalizationManager {
    @VisibleForTesting
    public static final String FINALIZED_BUILD_NUMBER_CONFIG_KEY = "finalizedBuildNumber";
    @VisibleForTesting
    static final String DB_LOCK_NAME = "finalize-upgrade-db";
    private final ZduStatusDao zduStatusDao;
    private final ClusterLockService clusterLockService;
    private final VersionHistoryDao versionHistoryDao;
    private final ApplicationConfiguration applicationConfig;
    private final EventPublisher eventPublisher;
    private final Supplier<List<UpgradeTask>> finalizeUpgradeTasksSupplier;
    private final int productBuildNumber;
    private final Mutable<Long> lastRequestTimestamp;
    private UpgradeFinalizationManager.Run lastRun;

    public DefaultUpgradeFinalizationManager(ZduStatusDao zduStatusDao, ClusterLockService clusterLockService, VersionHistoryDao versionHistoryDao, ApplicationConfiguration applicationConfig, EventPublisher eventPublisher, SharedDataManager sharedDataManager, List<String> finalizeUpgradeTasks) {
        this(zduStatusDao, clusterLockService, versionHistoryDao, applicationConfig, eventPublisher, sharedDataManager, DefaultUpgradeFinalizationManager.getFinalizeUpgradeTasksSupplier(finalizeUpgradeTasks), Integer.parseInt(GeneralUtil.getBuildNumber()));
    }

    @VisibleForTesting
    public DefaultUpgradeFinalizationManager(ZduStatusDao zduStatusDao, ClusterLockService clusterLockService, VersionHistoryDao versionHistoryDao, ApplicationConfiguration applicationConfig, EventPublisher eventPublisher, SharedDataManager sharedDataManager, Supplier<List<UpgradeTask>> finalizeUpgradeTasksSupplier, int productBuildNumber) {
        this.zduStatusDao = zduStatusDao;
        this.clusterLockService = clusterLockService;
        this.versionHistoryDao = versionHistoryDao;
        this.applicationConfig = applicationConfig;
        this.eventPublisher = eventPublisher;
        this.productBuildNumber = productBuildNumber;
        this.lastRequestTimestamp = sharedDataManager.getSharedData(this.getClass().getName()).getMutable("lastRequestTimestamp", 0L);
        this.finalizeUpgradeTasksSupplier = finalizeUpgradeTasksSupplier;
    }

    public boolean isPendingDatabaseFinalization() {
        int databaseFinalizedBuildNumber = this.versionHistoryDao.getFinalizedBuildNumber();
        return databaseFinalizedBuildNumber < this.productBuildNumber && databaseFinalizedBuildNumber != 0;
    }

    public boolean isPendingLocalFinalization() {
        return this.getFinalizedConfiguredBuildNumber() < this.productBuildNumber;
    }

    public void finalizeIfNeeded() throws UpgradeException {
        this.finalizeIfNeeded(new RunImpl(System.currentTimeMillis()));
    }

    private synchronized void finalizeIfNeeded(RunImpl run) throws UpgradeException {
        this.lastRun = run;
        ClusterLock dbLock = this.clusterLockService.getLockForName(DB_LOCK_NAME);
        try {
            UpgradeTask upgradeTask;
            run.databaseUpgrade = dbLock.tryLock();
            int maxBuildNumber = this.getMaxBuildNumber();
            Iterable<UpgradeTask> pendingUpgradeTasks = this.getPendingUpgradeTasks(run.databaseUpgrade, maxBuildNumber);
            Iterator<UpgradeTask> iterator = pendingUpgradeTasks.iterator();
            while (iterator.hasNext()) {
                run.lastTask = upgradeTask = iterator.next();
                this.actionOnUpgradeTask(upgradeTask, (Functions.FailableConsumer<UpgradeTask, Exception>)((Functions.FailableConsumer)UpgradeTask::validate));
            }
            iterator = pendingUpgradeTasks.iterator();
            while (iterator.hasNext()) {
                run.lastTask = upgradeTask = iterator.next();
                this.actionOnUpgradeTask(upgradeTask, (Functions.FailableConsumer<UpgradeTask, Exception>)((Functions.FailableConsumer)UpgradeTask::doUpgrade));
                this.finalizeBuildNumber(Integer.parseInt(upgradeTask.getBuildNumber()), upgradeTask.isDatabaseUpgrade());
            }
            this.finalizeBuildNumber(maxBuildNumber, run.databaseUpgrade);
        }
        catch (UpgradeException e) {
            run.exception = e;
            throw e;
        }
        catch (Exception e) {
            UpgradeException upgradeException;
            run.exception = upgradeException = new UpgradeException((Throwable)e);
            throw upgradeException;
        }
        finally {
            run.completedTimestamp = System.currentTimeMillis();
            if (run.databaseUpgrade) {
                dbLock.unlock();
            }
        }
    }

    public Optional<UpgradeFinalizationManager.Run> getLastRun() {
        return Optional.ofNullable(this.lastRun).filter(r -> r.getRequestTimestamp() >= (Long)this.lastRequestTimestamp.getValue());
    }

    public synchronized void markAsFullyFinalized(boolean updateBuildNumber) throws ConfigurationException {
        this.finalizeBuildNumber(this.productBuildNumber, updateBuildNumber);
        RunImpl run = new RunImpl(System.currentTimeMillis());
        run.completedTimestamp = System.currentTimeMillis();
        this.lastRun = run;
    }

    private void actionOnUpgradeTask(UpgradeTask upgradeTask, Functions.FailableConsumer<UpgradeTask, Exception> upgradeTaskFunction) throws Exception {
        upgradeTaskFunction.accept((Object)upgradeTask);
        if (!CollectionUtils.isEmpty((Collection)upgradeTask.getErrors())) {
            throw new UpgradeException("Upgrade task " + upgradeTask.getName() + " failed during the Finalization phase", (Collection)ImmutableList.copyOf((Collection)upgradeTask.getErrors()));
        }
    }

    private Iterable<UpgradeTask> getPendingUpgradeTasks(boolean upgradeDatabase, int maxBuildNumber) {
        Predicate<UpgradeTask> predicate = this.pendingLocal();
        if (upgradeDatabase) {
            predicate = predicate.or(this.pendingDatabase());
        }
        return this.finalizeUpgradeTasksSupplier.get().stream().filter(predicate).filter(this.upToBuildNumber(maxBuildNumber)).sorted(AbstractUpgradeManager.UPGRADE_TASK_COMPARATOR).collect(Collectors.toList());
    }

    private int getMaxBuildNumber() {
        return this.zduStatusDao.getStatus().filter(s -> s.getState() == ZduStatus.State.ENABLED).map(s -> s.getOriginalBuildNumber()).orElse(this.productBuildNumber);
    }

    private Predicate<UpgradeTask> upToBuildNumber(int maxBuildNumber) {
        return task -> !task.getConstraint().test(maxBuildNumber);
    }

    private Predicate<UpgradeTask> pendingLocal() {
        int buildNumber = this.getFinalizedConfiguredBuildNumber();
        return task -> !task.isDatabaseUpgrade() && task.getConstraint().test(buildNumber);
    }

    private Predicate<UpgradeTask> pendingDatabase() {
        int buildNumber = this.versionHistoryDao.getFinalizedBuildNumber();
        return task -> task.isDatabaseUpgrade() && task.getConstraint().test(buildNumber);
    }

    protected int getFinalizedConfiguredBuildNumber() {
        return this.applicationConfig.getIntegerProperty((Object)FINALIZED_BUILD_NUMBER_CONFIG_KEY);
    }

    protected void finalizeConfigureBuildNumber(int buildNumber) throws ConfigurationException {
        if (buildNumber > this.getFinalizedConfiguredBuildNumber()) {
            this.applicationConfig.setProperty((Object)FINALIZED_BUILD_NUMBER_CONFIG_KEY, buildNumber);
            this.applicationConfig.save();
        }
    }

    private void finalizeBuildNumber(int buildNumber, boolean upgradeDatabase) throws ConfigurationException {
        if (upgradeDatabase) {
            this.versionHistoryDao.finalizeBuild(buildNumber);
        }
        this.finalizeConfigureBuildNumber(buildNumber);
    }

    @PostConstruct
    public void init() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onRequested(ZduFinalizationRequestEvent e) throws UpgradeException {
        this.lastRequestTimestamp.setValue((Object)e.getTimestamp());
        this.finalizeIfNeeded(new RunImpl(e.getTimestamp()));
    }

    @EventListener
    public void onRemotelyRequested(ClusterEventWrapper e) throws UpgradeException {
        if (e.getEvent() instanceof ZduFinalizationRequestEvent) {
            this.finalizeIfNeeded(new RunImpl(e.getEvent().getTimestamp()));
        }
    }

    private static Supplier<List<UpgradeTask>> getFinalizeUpgradeTasksSupplier(List<String> finalizeUpgradeTasks) {
        return () -> {
            List tasks = finalizeUpgradeTasks.stream().map(beanName -> (UpgradeTask)ContainerManager.getComponent((String)beanName)).collect(Collectors.toList());
            AbstractUpgradeManager.assertNoDuplicateBuildNumbers(tasks);
            return tasks;
        };
    }

    public static class RunImpl
    implements UpgradeFinalizationManager.Run {
        final long requestTimestamp;
        Long completedTimestamp;
        UpgradeException exception;
        UpgradeTask lastTask;
        boolean databaseUpgrade;

        public RunImpl(long requestTimestamp) {
            this.requestTimestamp = requestTimestamp;
        }

        public long getRequestTimestamp() {
            return this.requestTimestamp;
        }

        @Nullable
        public Long completedTimestamp() {
            return this.completedTimestamp;
        }

        public boolean isDatabaseUpgrade() {
            return this.databaseUpgrade;
        }

        public UpgradeException getException() {
            return this.exception;
        }

        public UpgradeTask getLastTask() {
            return this.lastTask;
        }
    }
}

