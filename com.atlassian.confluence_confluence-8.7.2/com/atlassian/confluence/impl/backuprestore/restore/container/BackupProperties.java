/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.backuprestore.restore.container;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.upgrade.BuildNumber;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class BackupProperties {
    public static final String EXPORT_TYPE_PROPERTY_NAME = "exportType";
    public static final String SPACE_KEYS_PROPERTY_NAME = "spaceKeys";
    public static final String LEGACY_SPACE_KEY_PROPERTY_NAME = "spaceKey";
    public static final String EXPORT_SOURCE_PROPERTY_NAME = "source";
    public static final String TOTAL_OBJECTS_COUNT = "totalObjectsCount";
    public static final String BACKUP_ATTACHMENTS = "backupAttachments";
    private final Properties properties;

    public BackupProperties(Properties properties) {
        this.properties = properties;
    }

    public JobScope getJobScope() throws BackupRestoreException {
        String exportType = (String)this.properties.get(EXPORT_TYPE_PROPERTY_NAME);
        if (exportType == null) {
            throw new BackupRestoreException("Property exportType in the backup description was not found. It should be either site or space");
        }
        switch (exportType.toLowerCase()) {
            case "site": 
            case "all": {
                return JobScope.SITE;
            }
            case "space": {
                return JobScope.SPACE;
            }
        }
        throw new IllegalArgumentException("Not supported export type in the backup file: '" + exportType + "'. Expected either site or space");
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Collection<String> getSpaceKeys() {
        String spaceKeys = (String)this.properties.get(SPACE_KEYS_PROPERTY_NAME);
        if (spaceKeys != null) {
            return Arrays.asList(spaceKeys.split(","));
        }
        String spaceKey = (String)this.properties.get(LEGACY_SPACE_KEY_PROPERTY_NAME);
        return spaceKey != null ? Collections.singleton(spaceKey) : Collections.emptyList();
    }

    public BuildNumber getCreatedByBuildNumber() {
        String value = (String)this.properties.get("createdByBuildNumber");
        return StringUtils.isNotBlank((CharSequence)value) ? new BuildNumber(value) : null;
    }

    public BuildNumber getBuildNumber() {
        String value = (String)this.properties.get("buildNumber");
        return StringUtils.isNotBlank((CharSequence)value) ? new BuildNumber(value) : null;
    }

    public JobSource getJobSource() throws BackupRestoreException {
        String exportSource = (String)this.properties.get(EXPORT_SOURCE_PROPERTY_NAME);
        if (exportSource == null) {
            throw new BackupRestoreException("Property source in the backup description was not found. It should be either server or cloud");
        }
        switch (exportSource.toLowerCase()) {
            case "server": {
                return JobSource.SERVER;
            }
            case "cloud": {
                return JobSource.CLOUD;
            }
        }
        throw new IllegalArgumentException("Not supported export type in the backup file: '" + exportSource + "'. Expected either server or cloud");
    }

    public Optional<Long> getTotalNumberOfObjects() {
        String value = (String)this.properties.get(TOTAL_OBJECTS_COUNT);
        return StringUtils.isNotBlank((CharSequence)value) ? Optional.of(Long.parseLong(value)) : Optional.empty();
    }

    public Boolean getBackupAttachments() {
        String value = (String)this.properties.get(BACKUP_ATTACHMENTS);
        return StringUtils.isBlank((CharSequence)value) || Boolean.parseBoolean(value);
    }
}

