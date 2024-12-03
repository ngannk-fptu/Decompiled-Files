/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class LicenseCompatibilityChecker {
    private static final Version JIRA_SERVER_CUTOFF_VER = new Version(9, 13, 0);
    private static final Version CONF_SERVER_CUTOFF_VER = new Version(8, 6, 0);
    private static final Version BITBUCKET_SERVER_CUTOFF_VER = new Version(8, 15, 0);
    private final I18nResolver i18n;
    private final SupportApplicationInfo applicationInfo;
    private final LicenseService licenseService;

    @Autowired
    public LicenseCompatibilityChecker(I18nResolver i18n, SupportApplicationInfo applicationInfo, LicenseService licenseService) {
        this.i18n = Objects.requireNonNull(i18n);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    @Nonnull
    private PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus success(String key) {
        return new PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus(PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus.Status.SUCCESS, this.i18n.getText(key), AnalyticsKey.SUCCESS);
    }

    @Nonnull
    private PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus error(String key, AnalyticsKey analyticsKey, String ... parameters) {
        return new PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus(PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus.Status.ERROR, this.i18n.getText(key, (Serializable[])parameters), analyticsKey);
    }

    @Nonnull
    public PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus checkReleaseDateIsWithinMaintenance(Date releaseDate) {
        Objects.requireNonNull(releaseDate);
        return this.licenseService.isWithinMaintenanceFor(releaseDate) ? this.success("stp.pup.license.is.valid") : this.error("stp.pup.license.expired", AnalyticsKey.NEW_LICENSE_REQUIRED, this.applicationInfo.getBaseURL(UrlMode.RELATIVE));
    }

    @Nonnull
    public PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus checkIsAllowedForDcOnlyUpgrade(MicroservicePreUpgradeDataDTO.Version.VersionNumber productVersionNumber) {
        Version cutOffVersion;
        if (this.licenseService.isLicensedForDataCenter()) {
            return this.success("stp.pup.license.valid.dc.only.check");
        }
        Version productVersion = new Version(productVersionNumber.getMajor(), productVersionNumber.getMinor(), productVersionNumber.getBugfix());
        return productVersion.compareTo(cutOffVersion = this.getCutOffVersionForProduct(this.applicationInfo.getApplicationType())) >= 0 ? this.error("stp.pup.license.not.valid.dc.only.check", AnalyticsKey.UPGRADE_TO_DC_LICENSE_REQUIRED, this.applicationInfo.getApplicationName()) : this.success("stp.pup.license.valid.dc.only.check");
    }

    private Version getCutOffVersionForProduct(ApplicationType applicationType) {
        switch (applicationType) {
            case JIRA: {
                return JIRA_SERVER_CUTOFF_VER;
            }
            case CONFLUENCE: {
                return CONF_SERVER_CUTOFF_VER;
            }
            case BITBUCKET: {
                return BITBUCKET_SERVER_CUTOFF_VER;
            }
        }
        throw new IllegalArgumentException("Unsupported application type: " + (Object)((Object)applicationType));
    }
}

