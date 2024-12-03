/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.BuildNumber
 *  com.atlassian.confluence.upgrade.PluginExportCompatibility
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.PropertyUtils
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.upgrade.BuildNumber;
import com.atlassian.confluence.upgrade.PluginExportCompatibility;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.confluence.util.zip.Unzipper;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.PropertyUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportDescriptor {
    private static final Logger log = LoggerFactory.getLogger(ExportDescriptor.class);
    private final Properties properties;
    private static final String PROP_CREATED_BY_VERSION_NUMBER = "createdByVersionNumber";
    private static final String PROP_CREATED_BY_SOURCE = "source";

    public ExportDescriptor(Properties properties) throws UnexpectedImportZipFileContents, ImportExportException {
        this.properties = properties;
    }

    public ExportDescriptor() {
        this.properties = new Properties();
    }

    public static ExportDescriptor getExportDescriptor(File exportZip) throws UnexpectedImportZipFileContents, ImportExportException {
        return new ExportDescriptor(ExportDescriptor.readExportDescriptor(exportZip));
    }

    public static ExportDescriptor getExportDescriptor(Unzipper unzipper) throws UnexpectedImportZipFileContents, ImportExportException, IOException {
        File extractedDirectory = GeneralUtil.createTempDirectoryInConfluenceTemp("import");
        return new ExportDescriptor(ExportDescriptor.readExportDescriptor(unzipper, extractedDirectory));
    }

    @VisibleForTesting
    public Properties getProperties() {
        return this.properties;
    }

    public ExportScope getScope() throws ExportScope.IllegalExportScopeException {
        return ExportScope.getScopeFromPropertyValue(this.properties.getProperty("exportType"));
    }

    public void setScope(ExportScope scope) {
        this.properties.setProperty("exportType", scope.getString());
    }

    public BuildNumber getBuildNumber() {
        String property = this.properties.getProperty("buildNumber");
        return StringUtils.isNotBlank((CharSequence)property) ? new BuildNumber(property) : null;
    }

    public void setBuildNumber(String buildNumber) {
        this.properties.setProperty("buildNumber", buildNumber);
    }

    public void setPluginExportCompatibility(Map<String, PluginExportCompatibility> compatibility) {
        for (Map.Entry<String, PluginExportCompatibility> pluginInfo : compatibility.entrySet()) {
            String currentVersion = pluginInfo.getValue().getCurrentVersion();
            String earliestVersion = pluginInfo.getValue().getEarliestVersion();
            this.properties.setProperty("ao.data.version." + pluginInfo.getKey(), currentVersion);
            this.properties.setProperty("ao.data.version.min." + pluginInfo.getKey(), earliestVersion);
        }
        String pluginList = StringUtils.join(compatibility.keySet(), (String)", ");
        this.properties.setProperty("ao.data.list", pluginList);
    }

    public static Map<String, PluginExportCompatibility> getPluginExportCompatibility(Properties properties) {
        if (properties == null) {
            return null;
        }
        String pluginsExportingData = properties.getProperty("ao.data.list");
        if (StringUtils.isBlank((CharSequence)pluginsExportingData)) {
            return Collections.emptyMap();
        }
        HashMap result = Maps.newHashMap();
        ArrayList pluginList = Lists.newArrayList((Object[])StringUtils.split((String)pluginsExportingData, (String)","));
        for (String pluginKeyUntrimmed : pluginList) {
            String pluginKey = pluginKeyUntrimmed.trim();
            if (!StringUtils.isNotBlank((CharSequence)pluginKey)) continue;
            String createdByVersion = properties.getProperty("ao.data.version." + pluginKey);
            String earliestVersion = properties.getProperty("ao.data.version.min." + pluginKey);
            if (!StringUtils.isNotBlank((CharSequence)createdByVersion) || !StringUtils.isNotBlank((CharSequence)earliestVersion)) continue;
            result.put(pluginKey, new PluginExportCompatibility(earliestVersion, createdByVersion));
        }
        return result;
    }

    public String getSpaceKey() {
        return this.properties.getProperty("spaceKey");
    }

    public void setSpaceKey(String spaceKey) {
        this.properties.setProperty("spaceKey", spaceKey);
    }

    public void setSpaceKeys(Collection<String> spaceKeys) {
        this.properties.setProperty("spaceKeys", StringUtils.join(spaceKeys, (String)","));
    }

    public boolean isSpaceImport() {
        try {
            return ExportScope.SPACE == this.getScope();
        }
        catch (ExportScope.IllegalExportScopeException e) {
            return false;
        }
    }

    public boolean isSiteImport() {
        try {
            return ExportScope.ALL == this.getScope();
        }
        catch (ExportScope.IllegalExportScopeException e) {
            return false;
        }
    }

    public void setBackupAttachments(boolean exportAttachments) {
        this.properties.setProperty("backupAttachments", Boolean.toString(exportAttachments));
    }

    public boolean getBackupAttachments() {
        return Boolean.parseBoolean(this.properties.getProperty("backupAttachments"));
    }

    static Properties readExportDescriptor(File exportZip) throws UnexpectedImportZipFileContents, ImportExportException {
        try {
            File extractedDirectory = GeneralUtil.createTempDirectoryInConfluenceTemp("import");
            FileUnzipper fileUnzipper = new FileUnzipper(exportZip, extractedDirectory);
            return ExportDescriptor.readExportDescriptor(fileUnzipper, extractedDirectory);
        }
        catch (IOException e) {
            log.error("Error determining export type from export zip: " + exportZip.getPath(), (Throwable)e);
            throw new ImportExportException("Error unzipping file (This may be due to a large zip file): " + e.getMessage(), e);
        }
    }

    private static Properties readExportDescriptor(Unzipper unzipper, File extractedDirectory) throws IOException, UnexpectedImportZipFileContents {
        File exportDescriptorFile = unzipper.unzipFileInArchive("exportDescriptor.properties");
        if (exportDescriptorFile == null) {
            throw new UnexpectedImportZipFileContents("exportDescriptor.properties", unzipper);
        }
        Properties result = PropertyUtils.getPropertiesFromFile((File)exportDescriptorFile);
        if (!FileUtils.deleteDir((File)extractedDirectory)) {
            log.warn("Could not cleanup contents of temp/import within conf.home. Directory was [" + extractedDirectory + "]");
        }
        return result;
    }

    public void saveToOutputStream(OutputStream outputStream) throws IOException {
        this.properties.store(outputStream, null);
    }

    public void setCreatedByBuildNumber(String buildNumber) {
        this.properties.setProperty("createdByBuildNumber", buildNumber);
    }

    public BuildNumber getCreatedByBuildNumber() {
        String property = this.properties.getProperty("createdByBuildNumber");
        return StringUtils.isNotBlank((CharSequence)property) ? new BuildNumber(property) : null;
    }

    public void setDefaultUserGroup(String defaultUsersGroup) {
        this.properties.setProperty("defaultUsersGroup", defaultUsersGroup);
    }

    public String getDefaultUserGroup() {
        return this.properties.getProperty("defaultUsersGroup", "confluence-users");
    }

    public void setVersionNumber(String versionNumber) {
        this.properties.setProperty(PROP_CREATED_BY_VERSION_NUMBER, versionNumber);
    }

    public String getVersionNumber() {
        return this.properties.getProperty(PROP_CREATED_BY_VERSION_NUMBER);
    }

    public void setSource(Source source) {
        this.properties.setProperty(PROP_CREATED_BY_SOURCE, source.toString());
    }

    public void setObjectsCount(Long objectsCount) {
        if (objectsCount != null) {
            this.properties.setProperty("totalObjectsCount", Long.toString(objectsCount));
        }
    }

    public Long getObjectsCount() {
        String value = this.properties.getProperty("totalObjectsCount");
        return value != null ? Long.valueOf(Long.parseLong(value)) : null;
    }

    public Source getSource() {
        return Source.fromString(this.properties.getProperty(PROP_CREATED_BY_SOURCE));
    }

    public void setSupportEntitlementNumber(String supportEntitlementNumber) {
        this.properties.setProperty("supportEntitlementNumber", supportEntitlementNumber);
    }

    public String getSupportEntitlementNumber() {
        return this.properties.getProperty("supportEntitlementNumber");
    }

    public static enum Source {
        CLOUD,
        SERVER;


        public static Source fromString(String src) {
            return StringUtils.isNotBlank((CharSequence)src) ? Source.valueOf(src.toUpperCase()) : null;
        }

        public String toString() {
            return this.name().toLowerCase();
        }
    }
}

