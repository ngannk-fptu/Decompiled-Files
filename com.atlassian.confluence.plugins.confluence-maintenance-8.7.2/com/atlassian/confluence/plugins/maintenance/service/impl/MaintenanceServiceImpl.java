/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.setup.settings.GlobalSettingsManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.maintenance.service.impl;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.plugins.maintenance.model.Addon;
import com.atlassian.confluence.plugins.maintenance.model.MaintenanceInfo;
import com.atlassian.confluence.plugins.maintenance.service.MaintenanceService;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginState;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceServiceImpl
implements MaintenanceService {
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;
    private final GlobalSettingsManager settingsManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final LicenseService licenseService;

    @Autowired
    public MaintenanceServiceImpl(@ComponentImport PluginAccessor pluginAccessor, @ComponentImport PluginMetadataManager pluginMetadataManager, @ComponentImport GlobalSettingsManager settingsManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LicenseService licenseService) {
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
        this.settingsManager = settingsManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.licenseService = licenseService;
    }

    @Override
    public List<Addon> getUserInstalledAddons() {
        Collection plugins = this.pluginAccessor.getPlugins(arg_0 -> ((PluginMetadataManager)this.pluginMetadataManager).isUserInstalled(arg_0));
        return plugins.stream().map(plugin -> new Addon.Builder().name(plugin.getName()).icon(this.getIcon((Plugin)plugin)).vendorName(plugin.getPluginInformation().getVendorName()).readOnlyModeCompatible(this.isReadOnlyModeCompatible((Plugin)plugin)).disabled(PluginState.ENABLED != plugin.getPluginState()).build()).collect(Collectors.toList());
    }

    @Override
    public void updateMaintenanceInfo(MaintenanceInfo maintenanceInfo) throws ServiceException {
        if (!this.licenseService.isLicensedForDataCenterOrExempt()) {
            throw new ServiceException("The instance is not a Data Center");
        }
        Settings settings = new Settings(this.settingsManager.getGlobalSettings());
        settings.setMaintenanceBannerMessageOn(maintenanceInfo.isBannerMessageOn());
        settings.setMaintenanceBannerMessage(this.processBannerMessage(StringEscapeUtils.escapeHtml4((String)maintenanceInfo.getBannerMessage())));
        this.settingsManager.updateGlobalSettings(settings);
    }

    @Override
    public MaintenanceInfo getMaintenanceInfo() {
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        MaintenanceInfo.Builder maintenanceInfoBuilder = new MaintenanceInfo.Builder().bannerMessageEnabled(globalSettings.isMaintenanceBannerMessageOn()).bannerMessage(this.processBannerMessage(globalSettings.getMaintenanceBannerMessage()));
        return maintenanceInfoBuilder.build();
    }

    private String processBannerMessage(String bannerMessage) {
        return (String)StringUtils.defaultIfBlank((CharSequence)bannerMessage, (CharSequence)this.getI18n().getText("read.only.mode.default.banner.message"));
    }

    private boolean isReadOnlyModeCompatible(Plugin plugin) {
        String param = (String)plugin.getPluginInformation().getParameters().get("read-only-access-mode-compatible");
        return Boolean.valueOf(param);
    }

    private String getIcon(Plugin plugin) {
        return "rest/plugins/1.0/" + plugin.getKey() + "-key/media/plugin-icon";
    }

    private I18NBean getI18n() {
        return this.i18NBeanFactory.getI18NBean();
    }
}

