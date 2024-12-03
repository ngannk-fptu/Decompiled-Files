/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.DateUtils
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.util.concurrent.LazyReference
 *  com.atlassian.util.concurrent.Supplier
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.core.util.DateUtils;
import com.atlassian.plugin.Plugin;
import com.atlassian.util.concurrent.LazyReference;
import com.atlassian.util.concurrent.Supplier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ConfluenceInfo {
    private String home;
    private String buildNumber;
    private String version;
    private String supportEntitlementNumber;
    private long startTime;
    private String baseUrl;
    private String serverId;
    private Date installationDate;
    private int maxUsers;
    private List<Plugin> enabledPlugins = Collections.emptyList();
    private Settings globalSettings;
    private Supplier<ResourceBundle> resourceBundleSupplier;

    public ConfluenceInfo(final I18NBean i18NBean) {
        this((Supplier<ResourceBundle>)new LazyReference<ResourceBundle>(){

            protected ResourceBundle create() throws Exception {
                return i18NBean.getResourceBundle();
            }
        });
    }

    public ConfluenceInfo(Supplier<ResourceBundle> resourceBundleSupplier) {
        this.resourceBundleSupplier = resourceBundleSupplier;
    }

    public String getHome() {
        return this.home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getBuildNumber() {
        return this.buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Settings getGlobalSettings() {
        return this.globalSettings;
    }

    public void setGlobalSettings(Settings globalSettings) {
        this.globalSettings = globalSettings;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getUpTime() {
        long currentTime = System.currentTimeMillis();
        return DateUtils.dateDifference((long)this.getStartTime(), (long)currentTime, (long)4L, (ResourceBundle)((ResourceBundle)this.resourceBundleSupplier.get()));
    }

    public List<Plugin> getEnabledPlugins() {
        return this.enabledPlugins;
    }

    public void setEnabledPlugins(Collection<Plugin> plugins) {
        if (plugins == null) {
            this.enabledPlugins = Collections.emptyList();
            return;
        }
        this.enabledPlugins = new ArrayList<Plugin>(plugins);
        Collections.sort(this.enabledPlugins, (p1, p2) -> p1.getName().compareTo(p2.getName()));
    }

    public boolean isDevMode() {
        return ConfluenceSystemProperties.isDevMode();
    }

    public String getSupportEntitlementNumber() {
        return this.supportEntitlementNumber;
    }

    public void setSupportEntitlementNumber(String supportEntitlementNumber) {
        this.supportEntitlementNumber = supportEntitlementNumber;
    }

    public Date getInstallationDate() {
        return this.installationDate;
    }

    public void setInstallationDate(Date installationDate) {
        this.installationDate = installationDate;
    }

    public int getMaxUsers() {
        return this.maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }
}

