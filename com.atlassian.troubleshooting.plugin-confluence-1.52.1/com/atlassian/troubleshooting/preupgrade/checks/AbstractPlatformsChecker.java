/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.troubleshooting.preupgrade.checks;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.healthcheck.accessors.DbPlatform;
import com.atlassian.troubleshooting.preupgrade.AnalyticsKey;
import com.atlassian.troubleshooting.preupgrade.accessors.LicenseCompatibilityChecker;
import com.atlassian.troubleshooting.preupgrade.accessors.PupEnvironmentAccessor;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.checks.PlatformsChecker;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.util.RendererUtils;
import com.google.common.collect.ImmutableList;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AbstractPlatformsChecker
implements PlatformsChecker {
    public static final String STP_PUP_DEFAULT_HELP_LINK_TITLE = "stp.pup.default.help.link.title";
    protected final I18nResolver i18n;
    private final PupEnvironmentAccessor pupEnvironmentAccessor;
    private final PupPlatformAccessor pupPlatformAccessor;
    private final LicenseCompatibilityChecker licenseCompatibilityChecker;

    protected AbstractPlatformsChecker(I18nResolver i18n, PupEnvironmentAccessor pupEnvironmentAccessor, PupPlatformAccessor pupPlatformAccessor, LicenseCompatibilityChecker licenseCompatibilityChecker) {
        this.i18n = Objects.requireNonNull(i18n);
        this.pupEnvironmentAccessor = Objects.requireNonNull(pupEnvironmentAccessor);
        this.pupPlatformAccessor = Objects.requireNonNull(pupPlatformAccessor);
        this.licenseCompatibilityChecker = Objects.requireNonNull(licenseCompatibilityChecker);
    }

    @Override
    public List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> checkSupportedPlatforms(MicroservicePreUpgradeDataDTO.Version productVersion, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> supportedPlatforms) {
        return supportedPlatforms.stream().filter(p -> p.getVersion().isSameMajorAndMinorVersion(productVersion.getVersion())).findFirst().map(platform -> this.calculateFinalStatus(this.getAllPlatformStatuses(productVersion, (MicroservicePreUpgradeDataDTO.SupportedPlatform)platform))).orElse((List)ImmutableList.of((Object)this.warning(AnalyticsKey.NO_SUPPORTED_PLATFORMS, "stp.pup.no.information.available.for.version", new Serializable[]{productVersion.getVersion().toString()})));
    }

    public List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> getAllPlatformStatuses(MicroservicePreUpgradeDataDTO.Version productVersion, MicroservicePreUpgradeDataDTO.SupportedPlatform platform) {
        return ImmutableList.of((Object)this.calculateDBStatus(platform), (Object)this.calculateJavaStatus(platform), (Object)this.licenseCompatibilityChecker.checkReleaseDateIsWithinMaintenance(productVersion.getReleaseDate()), (Object)this.licenseCompatibilityChecker.checkIsAllowedForDcOnlyUpgrade(productVersion.getVersion()));
    }

    private List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> calculateFinalStatus(List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> statuses) {
        List<PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus> unsuccessful = statuses.stream().filter(s -> s.getStatus() != PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus.Status.SUCCESS).collect(Collectors.toList());
        if (!unsuccessful.isEmpty()) {
            return unsuccessful;
        }
        return ImmutableList.of();
    }

    private PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus calculateJavaStatus(MicroservicePreUpgradeDataDTO.SupportedPlatform platform) {
        String currentJavaVersion = this.pupEnvironmentAccessor.getJavaSpecificationVersion();
        return platform.getJava().stream().filter(currentJavaVersion::equals).findFirst().map(supportedJavaVersion -> this.success("stp.pup.java.version.is.supported", new Serializable[]{currentJavaVersion})).orElse(this.warning(AnalyticsKey.UNSUPPORTED_JAVA, "stp.pup.java.version.is.not.supported", new Serializable[]{RendererUtils.renderLink(platform.getDocUrl(), this.i18n.getText(STP_PUP_DEFAULT_HELP_LINK_TITLE))}));
    }

    private PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus calculateDBStatus(MicroservicePreUpgradeDataDTO.SupportedPlatform platform) {
        return this.pupPlatformAccessor.getCurrentDbPlatform().map(currentDb -> this.checkDb(platform, (DbPlatform)currentDb)).orElse(this.success("stp.pup.no.database.configured", new Serializable[0]));
    }

    private PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus checkDb(MicroservicePreUpgradeDataDTO.SupportedPlatform platform, DbPlatform currentDb) {
        List<String> supportedDbVersions = platform.getDatabases().get((Object)currentDb.getDbType());
        if (supportedDbVersions == null) {
            return this.warning(AnalyticsKey.UNSUPPORTED_DATABASE, "stp.pup.database.type.or.version.is.not.supported", new Serializable[]{RendererUtils.renderLink(platform.getDocUrl(), this.i18n.getText(STP_PUP_DEFAULT_HELP_LINK_TITLE))});
        }
        return supportedDbVersions.stream().filter(currentDb::versionEquals).findFirst().map(version -> this.success("stp.pup.database.is.supported", new Serializable[]{currentDb.getDbType()})).orElse(this.warning(AnalyticsKey.UNSUPPORTED_DATABASE, "stp.pup.database.type.or.version.is.not.supported", new Serializable[]{RendererUtils.renderLink(platform.getDocUrl(), this.i18n.getText(STP_PUP_DEFAULT_HELP_LINK_TITLE))}));
    }

    protected PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus success(String key, Serializable ... arguments) {
        return new PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus(PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus.Status.SUCCESS, this.i18n.getText(key, arguments), AnalyticsKey.SUCCESS);
    }

    protected PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus warning(AnalyticsKey analyticsKey, String key, Serializable ... arguments) {
        return new PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus(PreUpgradeInfoDto.Version.SupportedPlatformComponentStatus.Status.WARNING, this.i18n.getText(key, arguments), analyticsKey);
    }
}

