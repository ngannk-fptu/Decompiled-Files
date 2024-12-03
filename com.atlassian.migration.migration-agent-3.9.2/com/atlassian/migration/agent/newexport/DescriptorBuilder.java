/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.util.GeneralUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DescriptorBuilder {
    private static final Logger log = LoggerFactory.getLogger(DescriptorBuilder.class);
    private final BootstrapManager bootstrapManager;
    private final SENSupplier senSupplier;
    private final PluginVersionManager pluginVersionManager;
    private final String serverUrl;
    private final MigrationAgentConfiguration migrationAgentConfiguration;

    public DescriptorBuilder(BootstrapManager bootstrapManager, SENSupplier senSupplier, PluginVersionManager pluginVersionManager, SystemInformationService sysInfoService, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.bootstrapManager = bootstrapManager;
        this.senSupplier = senSupplier;
        this.pluginVersionManager = pluginVersionManager;
        this.serverUrl = sysInfoService.getConfluenceInfo().getBaseUrl();
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    public void generateNonSpaceDescriptor(String exportDir, long totalRowCount) {
        Properties properties = this.buildNonSpaceProperties(totalRowCount);
        this.writeToFile(exportDir, properties);
    }

    public void generateSpaceDescriptor(String spaceKey, String exportDir, boolean usersCreatedInUMS, long totalRowCount) {
        Properties properties = this.buildSpaceProperties(spaceKey, usersCreatedInUMS, totalRowCount);
        this.writeToFile(exportDir, properties);
    }

    private Properties buildNonSpaceProperties(long totalRowCount) {
        Properties properties = new Properties();
        properties.setProperty("exportType", "nonspace");
        return this.buildProperties(properties, totalRowCount);
    }

    private Properties buildSpaceProperties(String spaceKey, boolean usersCreatedInUMS, long totalRowCount) {
        Properties properties = new Properties();
        properties.setProperty("spaceKey", spaceKey);
        properties.setProperty("exportType", "space");
        properties.setProperty("usersCreatedInUMS", Boolean.toString(usersCreatedInUMS));
        return this.buildProperties(properties, totalRowCount);
    }

    private Properties buildProperties(Properties properties, long totalRowCount) {
        properties.setProperty("source", "server");
        properties.setProperty("exportFormat", "csv");
        properties.setProperty("createdByBuildNumber", this.bootstrapManager.getBuildNumber());
        properties.setProperty("buildNumber", "6452");
        properties.setProperty("createdByVersionNumber", GeneralUtil.getVersionNumber());
        properties.setProperty("supportEntitlementNumber", this.senSupplier.get());
        properties.setProperty("cmac", this.pluginVersionManager.getPluginVersion());
        properties.setProperty("confluence.server.url", this.serverUrl);
        properties.setProperty("totalExportedId", String.valueOf(totalRowCount));
        properties.setProperty("timezone", this.migrationAgentConfiguration.getServerTimezone());
        return properties;
    }

    public int getBuildNumber() {
        return Integer.parseInt(this.bootstrapManager.getBuildNumber());
    }

    @VisibleForTesting
    void writeToFile(String exportDir, Properties properties) {
        try (FileOutputStream output = new FileOutputStream(exportDir + "exportDescriptor.properties", true);
             OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new GZIPOutputStream(output), "UTF-8");){
            String comments = null;
            properties.store(writer, comments);
        }
        catch (IOException e) {
            log.error("Failed to create exportDescriptor.properties", (Throwable)e);
        }
    }
}

