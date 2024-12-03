/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.confluence.upgrade.DeferredUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeError
 *  com.atlassian.confluence.upgrade.UpgradeFinalizationManager
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradeTask
 *  org.apache.commons.collections.CollectionUtils
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.taskrunners;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeError;
import com.atlassian.confluence.upgrade.UpgradeFinalizationManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradeTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostSiteImportUpgrader {
    private static final Logger log = LoggerFactory.getLogger(PostSiteImportUpgrader.class);
    private final UpgradeManager upgradeManager;
    private final List<UpgradeTask> postRestoreUpgradeTasks;
    private final UpgradeFinalizationManager upgradeFinalizationManager;
    private final SessionFactory sessionFactory;

    public PostSiteImportUpgrader(UpgradeManager upgradeManager, List<UpgradeTask> postRestoreUpgradeTasks, UpgradeFinalizationManager upgradeFinalizationManager, SessionFactory sessionFactory) {
        this.upgradeManager = upgradeManager;
        this.postRestoreUpgradeTasks = postRestoreUpgradeTasks;
        this.upgradeFinalizationManager = upgradeFinalizationManager;
        this.sessionFactory = sessionFactory;
    }

    public void runUpgradeTasks(BackupProperties backupProperties) throws BackupRestoreException {
        BuildNumber importBuildNumber = backupProperties.getCreatedByBuildNumber();
        if (importBuildNumber == null) {
            importBuildNumber = backupProperties.getBuildNumber();
        }
        Collection<DeferredUpgradeTask> deferredUpgradeTasks = this.doPostRestoreUpgradeTasks(importBuildNumber);
        this.doDeferredUpgradeTasks(deferredUpgradeTasks);
        if (this.upgradeManager.configuredBuildNumberNewerThan(importBuildNumber.toString())) {
            this.upgradeManager.setDatabaseBuildNumber();
            this.upgradeManager.entireUpgradeFinished();
        }
        try {
            this.upgradeFinalizationManager.markAsFullyFinalized(true);
        }
        catch (ConfigurationException e) {
            throw new BackupRestoreException("Failed to configure finalized build-number", e);
        }
    }

    private Collection<DeferredUpgradeTask> doPostRestoreUpgradeTasks(BuildNumber importBuildNumber) throws BackupRestoreException {
        ArrayList<DeferredUpgradeTask> deferredUpgradeTasks = new ArrayList<DeferredUpgradeTask>();
        int i = 0;
        for (UpgradeTask upgradeTask : this.postRestoreUpgradeTasks) {
            log.debug("Running upgrade task {} of {}", (Object)(++i), (Object)this.postRestoreUpgradeTasks.size());
            if (!upgradeTask.getConstraint().test(Integer.parseInt(importBuildNumber.toString()))) {
                log.debug("Skipping upgrade task: {}", upgradeTask.getClass());
                continue;
            }
            try {
                upgradeTask.doUpgrade();
            }
            catch (Exception e) {
                throw new BackupRestoreException("Error while upgrading imported data " + e.getMessage(), e);
            }
            if (CollectionUtils.isNotEmpty((Collection)upgradeTask.getErrors())) {
                this.logUpgradeErrors(upgradeTask.getErrors());
                throw new BackupRestoreException(upgradeTask.getErrors().size() + " errors occurred while upgrading imported data. See logs for details.");
            }
            if (!(upgradeTask instanceof DeferredUpgradeTask)) continue;
            deferredUpgradeTasks.add((DeferredUpgradeTask)upgradeTask);
        }
        return deferredUpgradeTasks;
    }

    private void doDeferredUpgradeTasks(Collection<DeferredUpgradeTask> deferredUpgradeTasks) throws BackupRestoreException {
        int i = 0;
        for (DeferredUpgradeTask deferredUpgradeTask : deferredUpgradeTasks) {
            log.debug("Running deferred upgrade task {} of {}", (Object)(++i), (Object)deferredUpgradeTasks.size());
            try {
                deferredUpgradeTask.doDeferredUpgrade();
            }
            catch (Exception e) {
                throw new BackupRestoreException("Error while upgrading imported data " + e.getMessage(), e);
            }
        }
    }

    private void logUpgradeErrors(Collection<UpgradeError> errors) {
        log.error(errors.size() + " errors were encountered during upgrade:");
        int i = 1;
        for (UpgradeError error : errors) {
            log.error("{}: {}", (Object)i++, (Object)(error.getError() != null ? error.getError().getMessage() : error.getMessage()));
        }
    }
}

