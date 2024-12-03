/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.service.AddonGlobal;
import com.atlassian.confluence.plugins.gatekeeper.service.Configuration;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.plugin.osgi.bridge.external.PluginRetrievalService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;

public class ConfigurationAction
extends ConfluenceActionSupport {
    private boolean licensed;
    private String pluginKey;
    private AddonGlobal addonGlobal;
    private AddonLicenseManager licenseManager;
    private BandanaManager bandanaManager;
    private TransactionTemplate transactionTemplate;
    private boolean allowWhoCanViewButton = false;

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public String showConfiguration() throws Exception {
        this.checkLicense();
        Configuration configuration = new Configuration(this.bandanaManager, this.licenseManager);
        this.allowWhoCanViewButton = configuration.isWhoCanViewButtonAllowed();
        return "success";
    }

    public String saveConfiguration() throws Exception {
        this.checkLicense();
        Configuration configuration = new Configuration(this.bandanaManager, this.licenseManager);
        configuration.setWhoCanViewButtonAllowed(this.allowWhoCanViewButton);
        return "success";
    }

    protected boolean checkLicense() {
        this.licensed = this.licenseManager.getLicenseInfo().isValid() && this.licenseManager.getLicenseInfo().isDCFeatureLicensed();
        return this.licensed;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setPluginRetrievalService(PluginRetrievalService pluginRetrievalService) {
        this.pluginKey = pluginRetrievalService.getPlugin().getKey();
    }

    public void setAddonGlobal(AddonGlobal addonGlobal) {
        this.addonGlobal = addonGlobal;
    }

    public void setLicenseManager(AddonLicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public boolean isLicensed() {
        return this.licensed;
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public boolean isAllowWhoCanViewButton() {
        return this.allowWhoCanViewButton;
    }

    public void setAllowWhoCanViewButton(boolean allowWhoCanViewButton) {
        this.allowWhoCanViewButton = allowWhoCanViewButton;
    }
}

