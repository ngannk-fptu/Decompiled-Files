/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.preupgrade.PreUpgradeDataRetriever;
import com.atlassian.troubleshooting.preupgrade.PreUpgradePlanningManager;
import com.atlassian.troubleshooting.preupgrade.UpgradePathSectionFactory;
import com.atlassian.troubleshooting.preupgrade.accessors.PupEnvironmentAccessor;
import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;
import com.atlassian.troubleshooting.preupgrade.checks.PlatformsChecker;
import com.atlassian.troubleshooting.preupgrade.checks.ZduAvailabilityChecker;
import com.atlassian.troubleshooting.preupgrade.model.MicroservicePreUpgradeDataDTO;
import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import com.atlassian.troubleshooting.preupgrade.model.SupportedPlatformQuery;
import com.atlassian.troubleshooting.preupgrade.modz.Modifications;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.license.ApplicationLicenseInfo;
import com.atlassian.troubleshooting.stp.spi.Version;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultPreUpgradePlanningManager
implements PreUpgradePlanningManager {
    private static final String JSW_UPGRADE_URL = "https://www.atlassian.com/software/jira/update";
    private static final String JC_UPGRADE_URL = "https://www.atlassian.com/software/jira/core/update";
    private static final String CONFLUENCE_UPGRADE_URL = "https://www.atlassian.com/software/confluence/download";
    private static final String BITBUCKET_UPGRADE_URL = "https://www.atlassian.com/software/bitbucket/download";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPreUpgradePlanningManager.class);
    private final SupportApplicationInfo applicationInfo;
    private final PreUpgradeDataRetriever preUpgradeDataRetriever;
    private final ClusterService clusterService;
    private final PupPlatformAccessor pupPlatformAccessor;
    private final PlatformsChecker platformsChecker;
    private final I18nResolver i18n;
    private final List<UpgradePathSectionFactory> factories;
    private final PupEnvironmentAccessor pupEnvironmentAccessor;
    private final ZduAvailabilityChecker zduAvailabilityChecker;

    @Autowired
    public DefaultPreUpgradePlanningManager(SupportApplicationInfo applicationInfo, I18nResolver i18n, PreUpgradeDataRetriever preUpgradeDataRetriever, ClusterService clusterService, PupPlatformAccessor pupPlatformAccessor, PlatformsChecker platformsChecker, Optional<List<UpgradePathSectionFactory>> factories, PupEnvironmentAccessor pupEnvironmentAccessor, ZduAvailabilityChecker zduAvailabilityChecker) {
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.preUpgradeDataRetriever = Objects.requireNonNull(preUpgradeDataRetriever);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.pupPlatformAccessor = Objects.requireNonNull(pupPlatformAccessor);
        this.platformsChecker = Objects.requireNonNull(platformsChecker);
        this.i18n = Objects.requireNonNull(i18n);
        this.factories = factories.orElse(Collections.emptyList());
        this.pupEnvironmentAccessor = Objects.requireNonNull(pupEnvironmentAccessor);
        this.zduAvailabilityChecker = Objects.requireNonNull(zduAvailabilityChecker);
    }

    @Override
    public Optional<PreUpgradeInfoDto> getPreUpgradeInfo(boolean zduRecommendation) {
        Version localVersion = Version.of(this.applicationInfo.getApplicationVersion());
        SupportedPlatformQuery query = this.queryParams(localVersion);
        return this.preUpgradeDataRetriever.getUpgradeInfoDto(query).map(info -> {
            List<PreUpgradeInfoDto.Version> versions = this.transformPlatformInfoToVersions(localVersion, this.pupPlatformAccessor.calculateSubProduct(), this.pupEnvironmentAccessor.getPlatform(), this.pupEnvironmentAccessor.getOperatingSystem(), info.getUpgradeInfo());
            return new PreUpgradeInfoDto(versions, this.getSelectedVersion(zduRecommendation, versions).orElse(null), this.createInstanceData(info.getUpgradeInfo(), localVersion), info.isFromResource());
        });
    }

    private Optional<PreUpgradeInfoDto.Version> getSelectedVersion(boolean zduRecommendation, List<PreUpgradeInfoDto.Version> versions) {
        return zduRecommendation ? versions.stream().filter(v -> v.isZduAvailable).findFirst() : versions.stream().findFirst();
    }

    @Override
    public boolean isPreUpgradePageAvailable() {
        return this.findFactory().isPresent();
    }

    private PreUpgradeInfoDto.InstanceData createInstanceData(MicroservicePreUpgradeDataDTO info, Version localVersion) {
        return new PreUpgradeInfoDto.InstanceData(this.applicationInfo.getPlatformId(), String.format("%s %s", this.applicationInfo.getApplicationName(), this.applicationInfo.getApplicationVersion()), this.chooseApplicationName(), this.chooseUpgradeUrl(), this.findVersion(info, localVersion).map(MicroservicePreUpgradeDataDTO.Version::getReleaseDate).orElse(null), localVersion.getAnalyticsString(), this.pupEnvironmentAccessor.getOperatingSystem());
    }

    private String chooseUpgradeUrl() {
        return this.pupPlatformAccessor.calculateSubProduct().accept(new MicroservicePreUpgradeDataDTO.Version.SubProduct.SubProductVisitor<String>(){

            @Override
            public String visitJSW() {
                return DefaultPreUpgradePlanningManager.JSW_UPGRADE_URL;
            }

            @Override
            public String visitJC() {
                return DefaultPreUpgradePlanningManager.JC_UPGRADE_URL;
            }

            @Override
            public String visitConfluence() {
                return DefaultPreUpgradePlanningManager.CONFLUENCE_UPGRADE_URL;
            }

            @Override
            public String visitBitbucket() {
                return DefaultPreUpgradePlanningManager.BITBUCKET_UPGRADE_URL;
            }
        });
    }

    private String chooseApplicationName() {
        return this.pupPlatformAccessor.calculateSubProduct().accept(new MicroservicePreUpgradeDataDTO.Version.SubProduct.SubProductVisitor<String>(){

            @Override
            public String visitJSW() {
                return "Jira Software";
            }

            @Override
            public String visitJC() {
                return "Jira Core";
            }

            @Override
            public String visitConfluence() {
                return "Confluence";
            }

            @Override
            public String visitBitbucket() {
                return "Bitbucket";
            }
        });
    }

    private Optional<MicroservicePreUpgradeDataDTO.Version> findVersion(MicroservicePreUpgradeDataDTO info, Version localVersion) {
        return info.versions.stream().filter(v -> v.getVersion().equalsVersion(localVersion)).findFirst();
    }

    private List<PreUpgradeInfoDto.Version> transformPlatformInfoToVersions(Version localVersion, MicroservicePreUpgradeDataDTO.Version.SubProduct subProduct, MicroservicePreUpgradeDataDTO.Version.Platform platform, PupEnvironmentAccessor.OperatingSystem operatingSystem, MicroservicePreUpgradeDataDTO upgradeInfoDto) {
        AtomicBoolean first = new AtomicBoolean(true);
        return upgradeInfoDto.versions.stream().filter(version -> !version.getVersion().equalsVersion(localVersion)).filter(version -> !version.getHidden()).map(version -> {
            try {
                return this.createVersionSupportInfo(subProduct, platform, operatingSystem, (MicroservicePreUpgradeDataDTO.Version)version, upgradeInfoDto.supportedPlatforms, first.getAndSet(false), this.zduAvailabilityChecker.isZduAvailable(localVersion, version.getVersion(), upgradeInfoDto.supportedPlatforms));
            }
            catch (RuntimeException re) {
                LOG.info(String.format("Admin only Upgrade Planning Page has noticed version data corruption from external data source. It will try to display the upgrade planning page without version %s. ", version.getVersion()), (Throwable)re);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private PreUpgradeInfoDto.Version createVersionSupportInfo(MicroservicePreUpgradeDataDTO.Version.SubProduct subProduct, MicroservicePreUpgradeDataDTO.Version.Platform platform, PupEnvironmentAccessor.OperatingSystem operatingSystem, MicroservicePreUpgradeDataDTO.Version v, List<MicroservicePreUpgradeDataDTO.SupportedPlatform> supportedPlatforms, boolean firstVersion, boolean isZduAvailable) {
        return new PreUpgradeInfoDto.Version(String.format("%s %s%s%s%s", this.applicationInfo.getApplicationName(), v.getVersion(), v.isEnterprise() ? " " + this.i18n.getText("stp.pup.isenterprise") : "", firstVersion ? " " + this.i18n.getText("stp.pup.recommended") : "", isZduAvailable && this.clusterService.isClustered() ? " " + this.i18n.getText("stp.pup.zdu-upgradable") : ""), String.format("%s %s", this.applicationInfo.getApplicationName(), v.getVersion()), v.getVersion().getAnalyticsString(), v.getUpgradeInstructionsUrl(), this.findInstaller(subProduct, platform, operatingSystem, v.getLinuxInstallerDistribution(), v.getWindowsInstallerDistribution()), this.findArchive(subProduct, operatingSystem, v.getLinuxArchiveDistribution(), v.getWindowsArchiveDistribution()), v.getReleaseDate(), this.findNote(subProduct, v.getReleaseNotes()), this.platformsChecker.checkSupportedPlatforms(v, supportedPlatforms), this.findFactory().orElseThrow(() -> new RuntimeException("No factory found for platform " + this.applicationInfo.getPlatformId() + ", clustered = " + this.clusterService.isClustered())).getSections(v, isZduAvailable), this.pupPlatformAccessor.getModifiedFiles().map(Modifications::getNamesOfModifiedFiles).orElse(Collections.emptyList()), isZduAvailable);
    }

    private Optional<UpgradePathSectionFactory> findFactory() {
        String platformId = this.applicationInfo.getPlatformId();
        boolean clustered = this.clusterService.isClustered();
        List candidates = this.factories.stream().filter(f -> f.getPlatformId().equals(platformId) && f.isClustered() == clustered).collect(Collectors.toList());
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.checkSingletonList(candidates, "Several factories match " + platformId));
    }

    private <T> T checkSingletonList(List<T> list, String message) {
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException(message + ": " + StringUtils.join((Object[])new Object[]{list.stream().map(Object::toString), ", "}));
    }

    private String findInstaller(MicroservicePreUpgradeDataDTO.Version.SubProduct subProduct, MicroservicePreUpgradeDataDTO.Version.Platform platform, PupEnvironmentAccessor.OperatingSystem operatingSystem, List<MicroservicePreUpgradeDataDTO.Version.Installer> linuxInstallers, List<MicroservicePreUpgradeDataDTO.Version.Installer> windowsInstallers) {
        return this.find(operatingSystem, windowsInstallers, linuxInstallers, installer -> installer.platform == platform && installer.subProduct == subProduct, installer -> installer.link);
    }

    private String findArchive(MicroservicePreUpgradeDataDTO.Version.SubProduct subProduct, PupEnvironmentAccessor.OperatingSystem operatingSystem, List<MicroservicePreUpgradeDataDTO.Version.Archive> linuxInstallers, List<MicroservicePreUpgradeDataDTO.Version.Archive> windowsInstallers) {
        return this.find(operatingSystem, windowsInstallers, linuxInstallers, archive -> archive.subProduct == subProduct, archive -> archive.link);
    }

    private <T> String find(PupEnvironmentAccessor.OperatingSystem operatingSystem, List<T> windows, List<T> linux, Predicate<T> filter, Function<T, String> extractor) {
        return (operatingSystem == PupEnvironmentAccessor.OperatingSystem.WINDOWS ? windows : linux).stream().filter(filter).findFirst().map(extractor).orElse(null);
    }

    private String findNote(MicroservicePreUpgradeDataDTO.Version.SubProduct subProduct, List<MicroservicePreUpgradeDataDTO.Version.Note> notes) {
        return notes.stream().filter(n -> n.subProduct == subProduct).findFirst().map(n -> n.link).orElse(null);
    }

    private SupportedPlatformQuery queryParams(Version localVersion) {
        ApplicationLicenseInfo license = this.applicationInfo.getLicenseInfo();
        return new SupportedPlatformQuery(this.applicationInfo.getPlatformId(), localVersion.toString(), this.isLongTermSupportReleaseRecommended(license));
    }

    private boolean isLongTermSupportReleaseRecommended(ApplicationLicenseInfo license) {
        if (license.isEvaluation()) {
            return false;
        }
        boolean isLimitLarge = license.getUserLimits().stream().anyMatch(limit -> limit > 1000 || limit < 0);
        return isLimitLarge || this.clusterService.isClustered();
    }
}

