/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.base.Preconditions
 *  javax.servlet.ServletContext
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.schedule.ScheduleUtil;
import com.atlassian.confluence.schedule.managers.ScheduledJobManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.status.service.SystemInformationHelper;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.MemoryInfo;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@AdminOnly
public class ViewSystemInfoAction
extends ConfluenceActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(ViewSystemInfoAction.class);
    private SystemInformationService sysInfoService;
    private ServletContext servletContext;
    private boolean gc = false;
    private SystemInformationHelper helper;
    private ClusterManager clusterManager;
    private ScheduledJobManager scheduledJobManager;
    private ConfluenceSidManager sidManager;
    private List<Plugin> userInstalledPlugins;
    private LicenseService licenseService;
    private PluginMetadataManager pluginMetadataManager;

    public ViewSystemInfoAction() {
    }

    public ViewSystemInfoAction(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.gc) {
            System.gc();
            Thread.sleep(500L);
        }
        return super.execute();
    }

    public ServletContext getServletContext() {
        if (this.servletContext != null) {
            return this.servletContext;
        }
        return ServletActionContext.getServletContext();
    }

    private SystemInformationHelper getInfoHelper() {
        if (this.helper == null) {
            this.helper = new SystemInformationHelper(this.getI18n(), this.sysInfoService);
        }
        return this.helper;
    }

    public Map<String, String> getSummaryInfo() {
        return this.getInfoHelper().getSystemSummary();
    }

    public Map<String, String> getEnvironmentVariables() {
        return System.getenv();
    }

    public Map<String, String> getRuntimeEnvironment() {
        return this.getInfoHelper().getRuntimeEnvironment(this.getServletContext());
    }

    public Map<String, String> getDatabaseInfo() {
        return this.getInfoHelper().getDatabaseInfo();
    }

    public Map<String, String> getAttachmentStorageInfo() {
        return this.getInfoHelper().getAttachmentStorageInfo();
    }

    public Map<String, String> getUsageInfo() {
        return this.getInfoHelper().getUsageInfo();
    }

    public Map<String, String> getModifications() {
        return this.sysInfoService.getModifications();
    }

    public MemoryInfo getMemoryStatistics() {
        MemoryInfo bean = this.sysInfoService.getMemoryInfo();
        if (bean == null) {
            logger.warn("Cannot retrieve memory statistics for display.");
            bean = new MemoryInfo();
        }
        return bean;
    }

    public Map<String, Object> getBuildStats() {
        ConfluenceInfo bean = this.sysInfoService.getConfluenceInfo();
        if (bean == null) {
            logger.warn("Cannot retrieve Confluence build properties for display.");
            return Collections.emptyMap();
        }
        LinkedHashMap<String, Object> buildstats = new LinkedHashMap<String, Object>(6);
        buildstats.put("confluence.home", bean.getHome());
        buildstats.put("license.support.entitlement.number.label", bean.getSupportEntitlementNumber());
        if (bean.isDevMode()) {
            buildstats.put("developer.mode", "Enabled");
        }
        return buildstats;
    }

    public Collection<Plugin> getPlugins() {
        if (this.userInstalledPlugins == null) {
            this.userInstalledPlugins = new ArrayList<Plugin>();
            if (this.pluginAccessor.getPlugins() != null) {
                for (Plugin plugin : this.pluginAccessor.getPlugins()) {
                    if (!this.pluginMetadataManager.isUserInstalled(plugin)) continue;
                    this.userInstalledPlugins.add(plugin);
                }
            }
        }
        Comparator<Plugin> pluginComparator = Comparator.comparing(p -> StringUtils.defaultString((String)p.getName()));
        this.userInstalledPlugins.sort(pluginComparator);
        return this.userInstalledPlugins;
    }

    public boolean isPluginEnabled(String pluginKey) {
        if (this.pluginAccessor == null) {
            return false;
        }
        return this.pluginAccessor.isPluginEnabled(pluginKey);
    }

    public String getPluginEnabledAsEnglish(String pluginKey) {
        return this.isPluginEnabled(pluginKey) ? "enabled" : "disabled";
    }

    public String getPluginEnabledAsI18nLabel(String pluginKey) {
        String enabledKey = "raise.support.request.plugin.enabled";
        String disabledKey = "raise.support.request.plugin.disabled";
        return this.getText(this.isPluginEnabled(pluginKey) ? enabledKey : disabledKey);
    }

    public boolean isClustered() {
        if (this.clusterManager == null) {
            return false;
        }
        return this.clusterManager.isClustered();
    }

    public boolean isDailyBackupEnabled() {
        return ScheduleUtil.isBackupEnabled(this.scheduledJobManager, this.settingsManager);
    }

    public String getServerId() {
        if (this.sidManager == null) {
            return null;
        }
        String sid = null;
        try {
            sid = this.sidManager.getSid();
        }
        catch (ConfigurationException ex) {
            logger.warn("Unable to access server Id.", (Throwable)ex);
        }
        return sid;
    }

    public String getServerIdOrEnglishNone() {
        String sid = this.getServerId();
        return sid == null ? "none" : sid;
    }

    public String getServerIdOrI18nNone() {
        String sid = this.getServerId();
        return sid == null ? this.getText("none.word") : sid;
    }

    public String getSupportEntitlementNumberOrEnglishNone() {
        ConfluenceLicense license = this.licenseService.retrieve();
        String supportEntitlementNumber = license.getSupportEntitlementNumber();
        return supportEntitlementNumber == null ? "none" : supportEntitlementNumber;
    }

    public String getSupportEntitlementNumberOrI18nNone() {
        ConfluenceLicense license = this.licenseService.retrieve();
        String supportEntitlementNumber = license.getSupportEntitlementNumber();
        return supportEntitlementNumber == null ? this.getText("none.word") : supportEntitlementNumber;
    }

    public void setSidManager(ConfluenceSidManager sidManager) {
        this.sidManager = sidManager;
    }

    public Map<Object, Object> getSystemProperties() {
        return System.getProperties();
    }

    public Map getSystemPropertiesHtml() {
        Properties systemProps = System.getProperties();
        HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<Object, Object> property : systemProps.entrySet()) {
            String key = String.valueOf(property.getKey());
            String value = String.valueOf(property.getValue());
            if (key.endsWith(".path") || key.endsWith(".dirs")) {
                result.put(key, value.replace(File.pathSeparator, "<br>"));
                continue;
            }
            if (key.endsWith(".loader")) {
                result.put(key, value.replace(",", "<br>"));
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    public void setClusterManager(ClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    public void setScheduledJobManager(ScheduledJobManager scheduledJobManager) {
        this.scheduledJobManager = scheduledJobManager;
    }

    public List<Plugin> getEnabledPlugins() {
        ConfluenceInfo bean = this.sysInfoService.getConfluenceInfo();
        if (bean == null) {
            logger.warn("Cannot retrieve Confluence plugin information for display.");
            return Collections.emptyList();
        }
        return bean.getEnabledPlugins();
    }

    public void setGc(boolean gc) {
        this.gc = gc;
    }

    @Override
    public void setSystemInformationService(SystemInformationService sysInfoService) {
        this.sysInfoService = sysInfoService;
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = (LicenseService)Preconditions.checkNotNull((Object)licenseService);
    }

    public void setPluginMetadataManager(PluginMetadataManager pluginMetadataManager) {
        this.pluginMetadataManager = pluginMetadataManager;
    }
}

