/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.accessmode.AccessMode
 */
package com.atlassian.confluence.status.service;

import com.atlassian.confluence.api.model.accessmode.AccessMode;
import com.atlassian.confluence.status.service.systeminfo.AttachmentStorageInfo;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.CloudPlatformType;
import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformMetadata;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.HardwareInfo;
import com.atlassian.confluence.status.service.systeminfo.MemoryInfo;
import com.atlassian.confluence.status.service.systeminfo.SecurityInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfo;
import com.atlassian.confluence.status.service.systeminfo.SystemInfoFromDb;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public interface SystemInformationService {
    public static final String JDBC_DRIVER_SYSTEM_PROPERTY = "confluence.status.service.jdbc.driver.name";

    public DatabaseInfo getDatabaseInfo();

    public DatabaseInfo getSafeDatabaseInfo();

    public ConfluenceInfo getConfluenceInfo();

    public SystemInfo getSystemProperties();

    public AttachmentStorageInfo getAttachmentStorageProperties();

    public MemoryInfo getMemoryInfo();

    public UsageInfo getUsageInfo();

    @Deprecated
    public SystemInfoFromDb getSystemInfoFromDb();

    public Map<String, String> getModifications();

    public boolean isShowInfoOn500();

    public AccessMode getAccessMode();

    public Integer getMaxHTTPThreads();

    public Properties getHibernateProperties();

    public HardwareInfo getHardwareInfo();

    public Optional<CloudPlatformMetadata> getCloudPlatformMetadata();

    public Optional<ClusteredDatabasePlatformMetadata> getClusteredDatabaseInformation(CloudPlatformType var1);

    public SecurityInfo getSecurityInfo();
}

