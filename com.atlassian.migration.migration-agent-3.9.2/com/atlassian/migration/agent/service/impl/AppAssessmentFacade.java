/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.migration.app.dto.AppCloudSiteInfo
 *  com.atlassian.migration.app.dto.MigrationPath
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginState
 *  lombok.Generated
 *  okhttp3.internal.Util
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.migration.agent.dto.AppDto;
import com.atlassian.migration.agent.dto.AppInstallInfoRequest;
import com.atlassian.migration.agent.dto.assessment.AppAssessmentStatsResponse;
import com.atlassian.migration.agent.dto.assessment.AppAssessmentUpdateRequest;
import com.atlassian.migration.agent.dto.assessment.AppConsentDto;
import com.atlassian.migration.agent.dto.assessment.AppListResponse;
import com.atlassian.migration.agent.dto.assessment.AppSummaryDto;
import com.atlassian.migration.agent.dto.assessment.AppUsageDto;
import com.atlassian.migration.agent.dto.assessment.CloudAppDto;
import com.atlassian.migration.agent.dto.assessment.ConsentStatus;
import com.atlassian.migration.agent.dto.assessment.FeatureDifferenceState;
import com.atlassian.migration.agent.dto.assessment.ReliabilityState;
import com.atlassian.migration.agent.dto.assessment.UpdateAllAppAssessmentInfoRequest;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.AppAssessmentProperty;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.entity.AssessmentConsent;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.analytics.AppAssessmentAnalyticsEventService;
import com.atlassian.migration.agent.service.app.AppAccessScopeService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.AppUsageService;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.AccessScope;
import com.atlassian.migration.app.AppAssessmentClient;
import com.atlassian.migration.app.AppCloudCapability;
import com.atlassian.migration.app.MigratabliltyInfo;
import com.atlassian.migration.app.dto.AppCloudSiteInfo;
import com.atlassian.migration.app.dto.MigrationPath;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginState;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Generated;
import okhttp3.internal.Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

public class AppAssessmentFacade {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppAssessmentFacade.class);
    private final MigrationAppAggregatorService appAggregatorService;
    private final PluginManager pluginManager;
    private final AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService;
    private final AppUsageService appUsageService;
    private final AppAssessmentClient appAssessmentClient;
    private final CloudSiteService cloudSiteService;
    private final AppAccessScopeService appConsentService;
    private final AppAssessmentInfoService appAssessmentInfoService;
    private final SystemInformationService systemInformationService;
    private static final String UPGRADE_APP_PATH = "/plugins/servlet/upm";
    private static final String EMPTY_STRING = "";

    public AppAssessmentFacade(MigrationAppAggregatorService appAggregatorService, PluginManager pluginManager, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, AppUsageService appUsageService, AppAssessmentClient appAssessmentClient, CloudSiteService cloudSiteService, AppAccessScopeService appConsentService, AppAssessmentInfoService appAssessmentInfoService, SystemInformationService systemInformationService) {
        this.appAggregatorService = appAggregatorService;
        this.pluginManager = pluginManager;
        this.appAssessmentAnalyticsEventService = appAssessmentAnalyticsEventService;
        this.appUsageService = appUsageService;
        this.appAssessmentClient = appAssessmentClient;
        this.cloudSiteService = cloudSiteService;
        this.appConsentService = appConsentService;
        this.appAssessmentInfoService = appAssessmentInfoService;
        this.systemInformationService = systemInformationService;
    }

    public AppListResponse<AppSummaryDto> getPlugins() {
        return new AppListResponse<AppSummaryDto>(this.collectAppInfo());
    }

    public AppAssessmentStatsResponse getPluginStats() {
        return new AppAssessmentStatsResponse(this.getAppCount());
    }

    public void updateAppAssessmentInfo(String appKey, AppAssessmentUpdateRequest request) {
        this.appAssessmentInfoService.updateAppAssessmentInfo(appKey, request);
        this.appAssessmentAnalyticsEventService.savePropertyChangedEvent(AuthenticatedUserThreadLocal.get(), appKey, AppAssessmentProperty.getAppAssessmentPropertyByName(request.getAppProperty()));
    }

    public void updateAllAppAssessmentInfo(UpdateAllAppAssessmentInfoRequest request) {
        this.appAssessmentInfoService.updateAllAppAssessmentInfo(request);
    }

    public AppListResponse<AppUsageDto> getAppUsageStats() {
        return this.appUsageService.getAppUsageStats();
    }

    public String clearAppUsageCache() {
        return this.appUsageService.clearAppUsageCache();
    }

    public AppListResponse<CloudAppDto> getCloudAppsInfo(AppInstallInfoRequest request) {
        String cloudId = request.getCloudId();
        CloudSite cloudSite = this.cloudSiteService.getByCloudId(cloudId).orElseThrow(() -> new IllegalStateException(String.format("Failed to find cloudSite entry for requested cloudId: %s", cloudId)));
        return new AppListResponse<CloudAppDto>(this.appAssessmentClient.getAppInfoForSite(cloudId, request.getAppKeys()).getApps().parallelStream().map(amsApp -> this.buildCloudAppDto((AppCloudSiteInfo)amsApp, cloudSite.getCloudUrl(), this.appAggregatorService.getCachedCloudAppData(amsApp.getKey()))).collect(Collectors.toList()));
    }

    public Optional<AppConsentDto> updateAppConsent(String appKey, AppAssessmentUpdateRequest request) {
        Optional<AppAssessmentInfo> maybeAppAssessmentInfo = this.appAssessmentInfoService.getAppAssessmentInfoByAppKey(appKey);
        if (maybeAppAssessmentInfo.isPresent()) {
            this.updateAppAssessmentInfo(appKey, request);
            this.appConsentService.updatedAppAssessScopes(appKey);
            AppAssessmentInfo appAssessmentInfo = this.appAssessmentInfoService.getAppAssessmentInfoByAppKey(appKey).get();
            return Optional.of(this.buildAppConsentDto(appAssessmentInfo));
        }
        return Optional.empty();
    }

    public AppListResponse<AppConsentDto> getAllConsentApps() {
        return this.getConsentApps(false);
    }

    public AppListResponse<AppConsentDto> getRequiredConsentApps() {
        return this.getConsentApps(true);
    }

    private String getVendorName(String appKey) {
        Plugin plugin = this.pluginManager.getPlugin(appKey);
        return plugin != null ? plugin.getPluginInformation().getVendorName() : "(Unknown)";
    }

    public static boolean isPluginEnabled(Plugin plugin) {
        return PluginState.ENABLED == plugin.getPluginState();
    }

    public AppListResponse<AppDto> getAppsNeededInCloud() {
        List apps = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud().stream().map(app -> {
            MigrationAppAggregatorResponse appAggregatorResponse = this.appAggregatorService.getCachedServerAppData(app.getAppKey());
            Plugin plugin = this.pluginManager.getPlugin(app.getAppKey());
            if (plugin == null) {
                return null;
            }
            if (this.isAutomatic(appAggregatorResponse.getMigrationPath())) {
                String pluginName = this.getAppName(app.getAppKey(), appAggregatorResponse);
                return new AppDto(app.getAppKey(), pluginName);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return new AppListResponse<AppDto>(apps);
    }

    public String getUpgradeAppUrl() {
        String serverBaseUrl = this.systemInformationService.getConfluenceInfo().getBaseUrl();
        return UriComponentsBuilder.fromHttpUrl((String)serverBaseUrl).path(UPGRADE_APP_PATH).toUriString();
    }

    public String getAppName(String appKey, MigrationAppAggregatorResponse appAggregatorResponse) {
        return AppAssessmentFacade.getAppName(appKey, this.pluginManager, appAggregatorResponse);
    }

    public static String getAppName(String appKey, PluginManager pluginManager, MigrationAppAggregatorResponse appAggregatorResponse) {
        Plugin plugin;
        String appName;
        String string = appName = appAggregatorResponse != null ? appAggregatorResponse.getName() : EMPTY_STRING;
        if (StringUtils.isEmpty((String)appName) && (plugin = pluginManager.getPlugin(appKey)) != null) {
            appName = plugin.getName();
        }
        if (StringUtils.isEmpty((String)appName)) {
            appName = appKey;
        }
        return appName;
    }

    private boolean isAutomatic(MigrationPath migrationPath) {
        return Util.immutableListOf((Object[])new MigrationPath[]{MigrationPath.AUTOMATED, MigrationPath.INSTALL_ONLY}).contains(migrationPath);
    }

    private AppListResponse<AppConsentDto> getConsentApps(boolean excludeConsentNotRequired) {
        List<AppAssessmentInfo> appsNeededInCloud = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud();
        List apps = appsNeededInCloud.stream().map(this::buildAppConsentDto).filter(app -> !excludeConsentNotRequired || AppAssessmentFacade.requiresUserConsent(app.getStatus())).collect(Collectors.toList());
        return new AppListResponse<AppConsentDto>(apps);
    }

    private List<AppSummaryDto> collectAppInfo() {
        Map assessedPlugins = this.appAssessmentInfoService.getAllAppAssessmentInfos().stream().collect(Collectors.toMap(AppAssessmentInfo::getAppKey, Function.identity()));
        List<AppSummaryDto> collect = this.pluginManager.getActualUserInstalledPlugins().parallelStream().map(plugin -> this.buildPluginData((Plugin)plugin, assessedPlugins)).collect(Collectors.toList());
        this.appAssessmentAnalyticsEventService.saveAppsFetchedEvent(collect.stream().map(AppSummaryDto::getKey).collect(Collectors.toList()), AuthenticatedUserThreadLocal.get());
        return collect;
    }

    private long getAppCount() {
        return this.pluginManager.getActualUserInstalledPlugins().size();
    }

    private AppSummaryDto buildPluginData(Plugin plugin, Map<String, AppAssessmentInfo> assessedPlugins) {
        String appKey = plugin.getKey();
        AppAssessmentInfo appAssessmentInfo = assessedPlugins.get(appKey);
        AppAssessmentUserAttributedStatus migrationStatus = null;
        String migrationNotes = null;
        String alternativeAppKey = null;
        if (appAssessmentInfo != null) {
            migrationStatus = appAssessmentInfo.getMigrationStatus();
            migrationNotes = appAssessmentInfo.getMigrationNotes();
            alternativeAppKey = appAssessmentInfo.getAlternativeAppKey();
        }
        MigrationAppAggregatorResponse appAggregatorResponse = this.appAggregatorService.getCachedServerAppData(appKey);
        boolean needsUpgrade = AppAssessmentFacade.needsUpgrade(appAggregatorResponse, plugin);
        return new AppSummaryDto(plugin.getKey(), plugin.getName(), appAggregatorResponse.getIcon(), appAggregatorResponse.getCloudUrl(), appAggregatorResponse.getCloudKey(), AppAssessmentFacade.isPluginEnabled(plugin), appAggregatorResponse.hasCloud(), FeatureDifferenceState.fromFeatureDifference(appAggregatorResponse.getFeatureDifference()), AppCloudCapability.fromMigrationPath(appAggregatorResponse.getMigrationPath(), needsUpgrade), appAggregatorResponse.getFeatureDifferenceUrl(), appAggregatorResponse.getMigrationPathInstructions(), appAggregatorResponse.getContactSupportUrl(), Optional.ofNullable(migrationStatus).orElse(AppAssessmentUserAttributedStatus.Unassigned), Optional.ofNullable(migrationNotes).orElse(EMPTY_STRING), Optional.ofNullable(alternativeAppKey).orElse(EMPTY_STRING), this.getUpgradeAppUrl(), this.getReliabilityState(appKey, appAggregatorResponse), appAggregatorResponse.getMigrationRoadmapRequest(), plugin.getPluginInformation().getVendorName());
    }

    private ReliabilityState getReliabilityState(String appKey, MigrationAppAggregatorResponse appAggregatorResponse) {
        MigrationPath migrationPath = appAggregatorResponse.getMigrationPath();
        if (migrationPath == MigrationPath.DISCARDED || migrationPath == MigrationPath.UNKNOWN) {
            return null;
        }
        if (this.appAggregatorService.isAppReliable(appKey)) {
            return ReliabilityState.beta;
        }
        return ReliabilityState.alpha;
    }

    private ConsentStatus getConsentStatus(AppAssessmentInfo appAssessmentInfo, MigrationAppAggregatorResponse appAggregatorResponse) {
        Plugin plugin = this.pluginManager.getPlugin(appAssessmentInfo.getAppKey());
        boolean needsUpgrade = AppAssessmentFacade.needsUpgrade(appAggregatorResponse, plugin);
        switch (appAggregatorResponse.getMigrationPath()) {
            case INSTALL_ONLY: {
                return ConsentStatus.NO_MIGRATION_NEEDED;
            }
            case AUTOMATED: {
                if (needsUpgrade) {
                    return ConsentStatus.SERVER_APP_OUTDATED;
                }
                return this.getConsentStatusIfMigratable(appAssessmentInfo);
            }
            case DISCARDED: 
            case UNKNOWN: 
            case MANUAL: {
                return ConsentStatus.NO_AUTOMATED_MIGRATION_PATH;
            }
        }
        throw new IllegalArgumentException("Unsupported migratable state.");
    }

    public static boolean needsUpgrade(MigrationAppAggregatorResponse appAggregatorResponse, Plugin plugin) {
        boolean needsUpgrade = false;
        try {
            if ((appAggregatorResponse.getCloudMigrationAssistantCompatibility() == null || appAggregatorResponse.getCloudMigrationAssistantCompatibility().isEmpty()) && (appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList() == null || appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList().isEmpty())) {
                return needsUpgrade;
            }
            needsUpgrade = appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList() != null && !appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList().isEmpty() ? MigratabliltyInfo.Companion.needsUpgrade(plugin.getPluginInformation().getVersion(), appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList()) : MigratabliltyInfo.Companion.needsUpgrade(plugin.getPluginInformation().getVersion(), appAggregatorResponse.getCloudMigrationAssistantCompatibility());
        }
        catch (MigratabliltyInfo.MigrabilityVersionException e) {
            needsUpgrade = true;
            log.debug("Failed to check if app needs upgrade pluginVersion={}, cloudMigrationAssistantCompatibility={}, cloudMigrationAssistantCompatibilityRangeList={}", new Object[]{plugin.getPluginInformation().getVersion(), appAggregatorResponse.getCloudMigrationAssistantCompatibility(), appAggregatorResponse.getCloudMigrationAssistantCompatibilityRangeList()});
        }
        return needsUpgrade;
    }

    private ConsentStatus getConsentStatusIfMigratable(AppAssessmentInfo appAssessmentInfo) {
        boolean updatedAccessScopes = this.appConsentService.savedAccessScopesAreCurrent(appAssessmentInfo.getAppKey());
        if (!updatedAccessScopes) {
            return ConsentStatus.CONSENT_OUTDATED;
        }
        if (AssessmentConsent.NotGiven.equals((Object)appAssessmentInfo.getConsent())) {
            return ConsentStatus.CONSENT_NOT_GIVEN;
        }
        if (AssessmentConsent.Given.equals((Object)appAssessmentInfo.getConsent())) {
            return ConsentStatus.CONSENT_GIVEN;
        }
        return ConsentStatus.NO_AUTOMATED_MIGRATION_PATH;
    }

    private static boolean requiresUserConsent(ConsentStatus consentStatus) {
        switch (consentStatus) {
            case CONSENT_GIVEN: 
            case CONSENT_NOT_GIVEN: 
            case CONSENT_OUTDATED: 
            case SERVER_APP_OUTDATED: {
                return true;
            }
            case NO_MIGRATION_NEEDED: 
            case NO_MIGRATING_ALTERNATIVE: 
            case NO_AUTOMATED_MIGRATION_PATH: {
                return false;
            }
        }
        throw new IllegalArgumentException("Unsupported consent status.");
    }

    private AppConsentDto buildAppConsentDto(AppAssessmentInfo appAssessmentInfo) {
        MigrationAppAggregatorResponse response = this.appAggregatorService.getCachedServerAppData(appAssessmentInfo.getAppKey());
        return AppConsentDto.builder().key(appAssessmentInfo.getAppKey()).cloudKey(response.getCloudKey()).name(this.getAppName(appAssessmentInfo.getAppKey(), response)).vendorName(this.getVendorName(appAssessmentInfo.getAppKey())).status(this.getConsentStatus(appAssessmentInfo, response)).contactVendorUrl(response.getContactSupportUrl()).privacyPolicyUrl(response.getPrivacyPolicyUrl()).logoUrl(response.getIcon()).isVendorHighlighted(response.isTopVendor()).dataScopes(new ArrayList<AccessScope>(this.appConsentService.getAccessScopesDeclaredByApp(appAssessmentInfo.getAppKey()))).upgradeAppUrl(this.getUpgradeAppUrl()).build();
    }

    private CloudAppDto buildCloudAppDto(AppCloudSiteInfo amsApp, String cloudUrl, MigrationAppAggregatorResponse maaApp) {
        return new CloudAppDto(amsApp.getKey(), this.getAppName(amsApp.getKey(), maaApp), maaApp.getIcon(), maaApp.getCloudUrl(), AppAssessmentFacade.buildSiteAppInstallationUrl(cloudUrl, maaApp.getRelativeInstallUrl()), maaApp.getPrivacyPolicyUrl(), maaApp.getContactSupportUrl(), amsApp.getInstalled());
    }

    private static String buildSiteAppInstallationUrl(String siteUrl, String appInstallationSuffix) {
        return siteUrl + "/" + appInstallationSuffix;
    }
}

