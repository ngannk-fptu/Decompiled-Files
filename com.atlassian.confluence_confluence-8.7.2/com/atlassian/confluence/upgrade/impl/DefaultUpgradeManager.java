/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.upgrade.AbstractUpgradeManager
 *  com.atlassian.confluence.upgrade.DeferredUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.impl;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.config.ConfigurationException;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.event.events.admin.UpgradeFinishedEvent;
import com.atlassian.confluence.event.events.admin.UpgradeStartedEvent;
import com.atlassian.confluence.impl.cache.CacheFlusher;
import com.atlassian.confluence.impl.cluster.ClusterConfigurationHelperInternal;
import com.atlassian.confluence.impl.core.persistence.hibernate.schema.ConfluenceSchemaHelper;
import com.atlassian.confluence.internal.health.JohnsonEventLevel;
import com.atlassian.confluence.internal.health.JohnsonEventType;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.johnson.JohnsonUtils;
import com.atlassian.confluence.upgrade.AbstractUpgradeManager;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeGate;
import com.atlassian.confluence.upgrade.UpgradeTask;
import com.atlassian.confluence.upgrade.recovery.DbDumpException;
import com.atlassian.confluence.upgrade.recovery.RecoveryFileGenerator;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUpgradeManager
extends AbstractUpgradeManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultUpgradeManager.class);
    private static final String CLUSTER_UPGRADE_LOCK = "cluster.upgrade.lock";
    private CacheFlusher cacheFlusher;
    private EventPublisher eventPublisher;
    private VersionHistoryDao versionHistoryDao;
    private ClusterManager clusterManager;
    private UpgradeGate upgradeGate;
    private RecoveryFileGenerator recoveryFileGenerator;
    private ClusterConfigurationHelperInternal clusterConfigurationHelper;
    private FilesystemPath confluenceHome;
    private UpgradeFinalizationManager finalizationManager;
    private ClusterLockService clusterLockService;
    private LicenseService licenseService;
    private Integer initialConfiguredBuildNumber;
    private ConfluenceSchemaHelper schemaHelper;
    private final Supplier<Boolean> permitDatabaseUpgrades = new LazyReference<Boolean>(){

        protected Boolean create() throws Exception {
            Preconditions.checkNotNull((Object)DefaultUpgradeManager.this.initialConfiguredBuildNumber, (Object)"initialConfiguredBuildNumber has not yet been initialised");
            return DefaultUpgradeManager.this.tryAcquireDatabaseUpgradesLock(DefaultUpgradeManager.this.initialConfiguredBuildNumber);
        }
    };

    public void setFinalizationManager(UpgradeFinalizationManager finalizationManager) {
        this.finalizationManager = finalizationManager;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public void setSchemaHelper(ConfluenceSchemaHelper schemaHelper) {
        this.schemaHelper = schemaHelper;
    }

    protected void validateSchemaUpdateIfNeeded() throws ConfigurationException {
        this.schemaHelper.validateSchemaUpdateIfNeeded();
    }

    protected void updateSchemaIfNeeded() throws ConfigurationException {
        this.schemaHelper.updateSchemaIfNeeded();
    }

    protected void releaseSchemaReferences() {
        this.schemaHelper.reset();
    }

    protected void finalizeIfNeeded() throws UpgradeException {
        if (this.permitDatabaseUpgrades()) {
            this.finalizationManager.finalizeIfNeeded();
        } else {
            try {
                this.finalizationManager.markAsFullyFinalized(false);
            }
            catch (ConfigurationException e) {
                throw new UpgradeException((Throwable)e);
            }
        }
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.initialConfiguredBuildNumber = Integer.parseInt(this.getConfiguredBuildNumber());
    }

    protected String getRealBuildNumber() {
        return GeneralUtil.getBuildNumber();
    }

    protected String getDatabaseBuildNumber() {
        int databaseBuildNumber = this.versionHistoryDao.getLatestBuildNumber();
        return databaseBuildNumber == 0 ? this.getConfiguredBuildNumber() : Integer.toString(databaseBuildNumber);
    }

    protected List<UpgradeError> runUpgradePrerequisites() {
        return Collections.emptyList();
    }

    public boolean needUpgrade() {
        boolean needed = super.needUpgrade();
        this.upgradeGate.setUpgradeRequired(needed);
        return needed;
    }

    public void setDatabaseBuildNumber() {
        try {
            this.setDatabaseBuildNumber(this.getConfiguredBuildNumber());
        }
        catch (Exception e) {
            log.warn("Unable to set build number '" + this.getConfiguredBuildNumber() + "' in the database", (Throwable)e);
        }
    }

    protected void setDatabaseBuildNumber(String databaseBuildNumber) throws Exception {
        try {
            int currentBuildNumber = Integer.parseInt(databaseBuildNumber);
            int previousBuildNumber = this.versionHistoryDao.getLatestBuildNumber();
            if (previousBuildNumber < currentBuildNumber && this.permitDatabaseUpgrades()) {
                this.versionHistoryDao.addBuildToHistory(currentBuildNumber);
            } else {
                log.info("Not setting database version on subsequent nodes of cluster. Database upgrades have already been run.");
            }
        }
        catch (NumberFormatException e) {
            log.warn("Unable to write build number to database - build number could not be parsed: " + databaseBuildNumber);
        }
    }

    protected void beforeUpgrade() {
        this.eventPublisher.publish((Object)new UpgradeStartedEvent((Object)this));
        if (this.isUpgradeRecoveryFileEnabled()) {
            log.info("Generating pre-upgrade recovery file...");
            try {
                this.recoveryFileGenerator.generate(this.createUpgradeRecoveryFile("before"));
                log.info("Finished generating pre-upgrade recovery file.");
            }
            catch (DbDumpException e) {
                this.failOnDbDumpException("Pre-Upgrade", e);
            }
        }
    }

    private void failOnDbDumpException(String stage, DbDumpException e) {
        String row1 = stage + " recovery file generation failed.";
        String row2 = "Please refer to https://confluence.atlassian.com/x/ropKGQ for possible solution.";
        log.error(Joiner.on((char)'\n').join((Object)row1, (Object)"Please refer to https://confluence.atlassian.com/x/ropKGQ for possible solution.", new Object[0]), (Throwable)((Object)e));
        JohnsonUtils.raiseJohnsonEvent(JohnsonEventType.STARTUP, Joiner.on((String)"<br>").join((Object)row1, (Object)"Please refer to https://confluence.atlassian.com/x/ropKGQ for possible solution.", new Object[0]), e.getMessage(), JohnsonEventLevel.FATAL);
    }

    private File createUpgradeRecoveryFile(String qualifier) {
        File directory = new File(this.getConfluenceHome(), "recovery");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String prefix = "upgradeRecoveryFile-" + this.initialConfiguredBuildNumber + "-" + this.getRealBuildNumber() + "-" + qualifier;
        String postfix = ".xml.gz";
        File candidate = new File(directory, prefix + postfix);
        int counter = 2;
        while (candidate.exists()) {
            candidate = new File(directory, prefix + "-" + counter + postfix);
            ++counter;
        }
        return candidate;
    }

    private File getConfluenceHome() {
        if (this.confluenceHome != null) {
            return this.confluenceHome.asJavaFile();
        }
        BootstrapManager bootstrapManager = (BootstrapManager)BootstrapUtils.getBootstrapManager();
        return new File(bootstrapManager.getConfluenceHome());
    }

    protected void postUpgrade() {
        this.cacheFlusher.flushCaches();
    }

    protected void initialUpgradeFinished() throws Exception {
        super.initialUpgradeFinished();
        if (this.clusterConfigurationHelper.isClusterHomeConfigured()) {
            long realBuildNumber;
            Optional<String> sharedBuildNumberOpt = this.clusterConfigurationHelper.getSharedBuildNumber();
            long sharedBuildNumber = 0L;
            if (sharedBuildNumberOpt.isPresent()) {
                sharedBuildNumber = NumberUtils.toLong((String)sharedBuildNumberOpt.get(), (long)0L);
            }
            if ((realBuildNumber = NumberUtils.toLong((String)this.getRealBuildNumber(), (long)0L)) > sharedBuildNumber) {
                this.clusterConfigurationHelper.saveSharedBuildNumber(this.getRealBuildNumber());
            }
        }
        if (!this.deferredTasksOutstanding()) {
            this.upgradeGate.setPluginDependentUpgradeComplete(true);
        }
    }

    private boolean deferredTasksOutstanding() {
        for (DeferredUpgradeTask task : this.getPluginDependentUpgradeTasks()) {
            if (!task.isUpgradeRequired()) continue;
            return true;
        }
        return false;
    }

    public void entireUpgradeFinished() {
        if (this.isUpgradeRecoveryFileEnabled()) {
            log.info("Generating post-upgrade recovery file...");
            try {
                this.cacheFlusher.flushCaches();
                this.recoveryFileGenerator.generate(this.createUpgradeRecoveryFile("after"));
                log.info("Finished generating post-upgrade recovery file.");
            }
            catch (DbDumpException e) {
                this.failOnDbDumpException("Post-Upgrade", e);
            }
        }
        super.entireUpgradeFinished();
        this.eventPublisher.publish((Object)new UpgradeFinishedEvent((Object)this));
    }

    protected boolean permitDatabaseUpgrades() {
        return (Boolean)this.permitDatabaseUpgrades.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean tryAcquireDatabaseUpgradesLock(int initialBuildNumber) {
        if (this.clusterManager.isClustered()) {
            ClusterLock upgradeLock = this.clusterLockService.getLockForName(CLUSTER_UPGRADE_LOCK);
            if (upgradeLock.tryLock()) {
                try {
                    if (this.neededSchemaUpgrade()) {
                        this.schemaHelper.updateVersionHistorySchemaIfNeeded();
                    }
                    String upgradeLockTag = "lock_for_upgrade_to_" + GeneralUtil.getBuildNumber();
                    log.debug("Cluster upgrade lock acquired. Attempting to tag build number {}", (Object)initialBuildNumber);
                    boolean tagSuccessful = this.versionHistoryDao.tagBuild(initialBuildNumber, upgradeLockTag);
                    if (tagSuccessful) {
                        log.debug("Successfully tagged build number {} with '{}', database upgrades are permitted", (Object)initialBuildNumber, (Object)upgradeLockTag);
                        boolean bl = true;
                        return bl;
                    }
                    log.debug("Failed to tag build number {} with '{}'", (Object)initialBuildNumber, (Object)upgradeLockTag);
                    boolean bl = false;
                    return bl;
                }
                finally {
                    upgradeLock.unlock();
                }
            }
            log.warn("Cluster upgrade lock could not be acquired. Disallowing database upgrades on this node.");
            return false;
        }
        return true;
    }

    @VisibleForTesting
    protected void runUpgradeTasks(List<UpgradeTask> upgradeTasks) throws UpgradeException {
        super.runUpgradeTasks(upgradeTasks);
    }

    private boolean isUpgradeRecoveryFileEnabled() {
        Boolean isLicensedForDataCenter = this.licenseService.isLicensedForDataCenterOrExempt();
        Boolean enabledByDefault = isLicensedForDataCenter == false && this.permitDatabaseUpgrades();
        if (isLicensedForDataCenter.booleanValue()) {
            log.warn("Upgrade recovery file generation is disabled by default for data center license. To enable it please explicitly set parameter 'confluence.upgrade.recovery.file.enabled' as true.");
        }
        return Boolean.parseBoolean(System.getProperty("confluence.upgrade.recovery.file.enabled", enabledByDefault.toString()));
    }

    public void setCacheFlusher(CacheFlusher cacheFlusher) {
        this.cacheFlusher = cacheFlusher;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setVersionHistoryDao(VersionHistoryDao versionHistoryDao) {
        this.versionHistoryDao = versionHistoryDao;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void setClusterLockService(ClusterLockService clusterLockService) {
        this.clusterLockService = clusterLockService;
    }

    public void setUpgradeGate(UpgradeGate upgradeGate) {
        this.upgradeGate = upgradeGate;
    }

    public void setRecoveryFileGenerator(RecoveryFileGenerator recoveryFileGenerator) {
        this.recoveryFileGenerator = recoveryFileGenerator;
    }

    public void setClusterConfigurationHelper(ClusterConfigurationHelperInternal clusterConfigurationHelper) {
        this.clusterConfigurationHelper = clusterConfigurationHelper;
    }

    public void setConfluenceHome(FilesystemPath confluenceHome) {
        this.confluenceHome = confluenceHome;
    }
}

