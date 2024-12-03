/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.licensebanner.support;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.plugins.licensebanner.support.ExperienceOverrideService;
import com.atlassian.confluence.plugins.licensebanner.support.LicenseBannerService;
import com.atlassian.confluence.plugins.licensebanner.support.LicenseDetails;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultLicenseBannerService
implements LicenseBannerService {
    @VisibleForTesting
    static final int END_OF_TIME = -3650000;
    private final LicenseService licenseService;
    private final PluginSettings pluginSettings;
    private final I18nResolver i18nResolver;
    private final ExperienceOverrideService experienceOverrideService;

    @Autowired
    public DefaultLicenseBannerService(@ComponentImport LicenseService licenseService, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport I18nResolver i18nResolver, ExperienceOverrideService experienceOverrideService) {
        this.licenseService = licenseService;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.i18nResolver = i18nResolver;
        this.experienceOverrideService = experienceOverrideService;
    }

    @Override
    @Nonnull
    public LicenseDetails retrieveLicenseDetails(UserKey userKey) {
        Integer expiry;
        ConfluenceLicense license = this.licenseService.retrieve();
        LicenseDetails result = new LicenseDetails(license.getNumberOfDaysBeforeExpiry(), license.getNumberOfDaysBeforeMaintenanceExpiry());
        if (this.bannerless(license)) {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
            this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
            return result;
        }
        if (!this.experienceOverrideService.isOverridden(userKey, "license-expiry-banner")) {
            if (license.getNumberOfDaysBeforeExpiry() > 45) {
                this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
            } else if (license.getNumberOfDaysBeforeExpiry() <= 7) {
                result.setShowLicenseExpiryBanner(true);
                this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
            } else {
                expiry = this.getSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
                result.setShowLicenseExpiryBanner(expiry == null || license.getNumberOfDaysBeforeExpiry() <= expiry);
            }
        }
        if (!this.experienceOverrideService.isOverridden(userKey, "license-maintenance-banner")) {
            if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 45) {
                this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
            } else {
                expiry = this.getSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
                result.setShowMaintenanceExpiryBanner(expiry == null || license.getNumberOfDaysBeforeMaintenanceExpiry() <= expiry);
            }
        }
        if (result.isShowLicenseExpiryBanner() || result.isShowMaintenanceExpiryBanner()) {
            result.setRenewUrl(this.resolveMACUrl(result));
            result.setSalesUrl(this.resolveSalesContactUrl());
        }
        return result;
    }

    @Override
    public void remindNever(UserKey userKey) {
        ConfluenceLicense license = this.licenseService.retrieve();
        if (this.bannerless(license)) {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
            this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
            return;
        }
        if (license.getNumberOfDaysBeforeExpiry() > 45) {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
        }
        if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 45) {
            this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
        } else {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), -3650000);
        }
    }

    @Override
    public void remindLater(UserKey userKey) {
        ConfluenceLicense license = this.licenseService.retrieve();
        if (this.bannerless(license)) {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
            this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
            return;
        }
        if (license.getNumberOfDaysBeforeExpiry() > 45) {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
        } else if (license.getNumberOfDaysBeforeExpiry() > 30) {
            this.putSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey), 30);
        } else if (license.getNumberOfDaysBeforeExpiry() > 15) {
            this.putSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey), 15);
        } else if (license.getNumberOfDaysBeforeExpiry() > 7) {
            this.putSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey), 7);
        } else {
            this.removeSetting(DefaultLicenseBannerService.hideLicenseExpiryKey(userKey));
        }
        if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 45) {
            this.removeSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey));
        } else if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 30) {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), 30);
        } else if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 15) {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), 15);
        } else if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 7) {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), 7);
        } else if (license.getNumberOfDaysBeforeMaintenanceExpiry() > 0) {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), 0);
        } else {
            this.putSetting(DefaultLicenseBannerService.hideMaintenanceExpiryKey(userKey), license.getNumberOfDaysBeforeMaintenanceExpiry() - 7);
        }
    }

    private boolean bannerless(ConfluenceLicense license) {
        return license.isEvaluation();
    }

    private void removeSetting(String key) {
        this.pluginSettings.remove(key);
    }

    private void putSetting(String key, int value) {
        this.pluginSettings.put(key, (Object)Integer.toString(value));
    }

    private Integer getSetting(String key) {
        String valueStr = (String)this.pluginSettings.get(key);
        return valueStr == null ? null : Integer.valueOf(valueStr);
    }

    @VisibleForTesting
    static String hideLicenseExpiryKey(UserKey userKey) {
        return userKey + ".HIDE_LICENSE_EXPIRY";
    }

    @VisibleForTesting
    static String hideMaintenanceExpiryKey(UserKey userKey) {
        return userKey + ".HIDE_MAINTENANCE_EXPIRY";
    }

    private String resolveMACUrl(LicenseDetails details) {
        int days;
        StringBuilder builder = new StringBuilder(this.i18nResolver.getText("external.link.confluence.license.renew"));
        builder.append("?utm_source=confluence_banner&utm_medium=renewals_reminder&utm_campaign=");
        int n = days = details.isShowLicenseExpiryBanner() ? details.getDaysBeforeLicenseExpiry() : details.getDaysBeforeMaintenanceExpiry();
        if (days <= 7) {
            builder.append("renewals_7_reminder");
        } else if (days <= 15) {
            builder.append("renewals_15_reminder");
        } else if (days <= 30) {
            builder.append("renewals_30_reminder");
        } else {
            builder.append("renewals_45_reminder");
        }
        return builder.toString();
    }

    private String resolveSalesContactUrl() {
        return this.i18nResolver.getText("external.link.atlassian.sales.contact.page");
    }
}

