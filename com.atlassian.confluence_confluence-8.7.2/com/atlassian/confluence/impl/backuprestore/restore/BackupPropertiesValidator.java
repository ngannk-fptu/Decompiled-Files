/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.backuprestore.BackupRestoreJob;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.restore.container.BackupProperties;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.upgrade.UpgradeManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupPropertiesValidator {
    private static final Logger log = LoggerFactory.getLogger(BackupPropertiesValidator.class);
    public static final int MINIMUM_RESTORE_BUILD_NUMBER = 7103;
    private final UpgradeManager upgradeManager;

    public BackupPropertiesValidator(UpgradeManager upgradeManager) {
        this.upgradeManager = upgradeManager;
    }

    public BackupProperties validatePropertiesAgainstBackupJob(BackupRestoreJob job, BackupProperties backupProperties) throws BackupRestoreException {
        this.validateJobScope(job.getJobScope(), backupProperties.getJobScope());
        this.validateBackupSupportedVersion(backupProperties);
        return backupProperties;
    }

    private void validateJobScope(JobScope expectedJobScope, JobScope jobScopeFromBackup) throws BackupRestoreException {
        if (!expectedJobScope.equals((Object)jobScopeFromBackup)) {
            throw new BackupRestoreException("Unable to restore backup. Expected " + expectedJobScope + " but the backup file has " + jobScopeFromBackup);
        }
    }

    private void validateBackupSupportedVersion(BackupProperties backupProperties) throws BackupRestoreException {
        Integer buildNumberOfImport = this.convertBuildNumberToInteger(backupProperties.getBuildNumber());
        Integer createdByBuildNumberOfImport = this.convertBuildNumberToInteger(backupProperties.getCreatedByBuildNumber());
        JobScope jobScope = backupProperties.getJobScope();
        int oldestBackupBuild = this.getOldestBackupBuildNumberAllowed(jobScope);
        if (!BackupPropertiesValidator.isBackupSupportedVersion(buildNumberOfImport, oldestBackupBuild) && !BackupPropertiesValidator.isBackupSupportedVersion(createdByBuildNumberOfImport, oldestBackupBuild)) {
            throw new BackupRestoreException(String.format("Unable to restore %s backups from versions of Confluence prior to build number: %s. Build number of backup: %s. CreatedBy Build Number of backup: %s.", jobScope, oldestBackupBuild, buildNumberOfImport, createdByBuildNumberOfImport));
        }
    }

    public static boolean isBackupSupportedVersion(Integer buildNumberOfImport, Integer minBuildNumber) {
        if (buildNumberOfImport == null || minBuildNumber == null) {
            return false;
        }
        if (buildNumberOfImport == 0) {
            return true;
        }
        return buildNumberOfImport.compareTo(minBuildNumber) >= 0;
    }

    private int getOldestBackupBuildNumberAllowed(JobScope jobScope) {
        if (JobScope.SITE.equals((Object)jobScope)) {
            return 7103;
        }
        Integer oldestSpaceImportBuildNumber = this.getBuildNumberIntegerFromString(this.upgradeManager.getOldestSpaceImportAllowed());
        if (oldestSpaceImportBuildNumber == null) {
            return 7103;
        }
        return Math.max(7103, oldestSpaceImportBuildNumber);
    }

    private Integer convertBuildNumberToInteger(BuildNumber buildNumber) {
        if (buildNumber == null) {
            return null;
        }
        return this.getBuildNumberIntegerFromString(buildNumber.toString());
    }

    private Integer getBuildNumberIntegerFromString(String buildNumberString) {
        if (StringUtils.isBlank((CharSequence)buildNumberString)) {
            return null;
        }
        try {
            return Integer.valueOf(buildNumberString);
        }
        catch (NumberFormatException e) {
            log.warn("Invalid import build number, {}", (Object)buildNumberString, (Object)e);
            return null;
        }
    }
}

