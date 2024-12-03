/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.GenericOperationalEvent
 *  com.atlassian.cmpt.analytics.events.GenericOperationalEvent$Builder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.migration.app.dto.MigrationPath
 *  com.atlassian.plugin.Plugin
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.Response$Status
 *  javax.ws.rs.core.Response$Status$Family
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.cmpt.analytics.events.GenericOperationalEvent;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.assessment.AppUsageDto;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.AppAssessmentProperty;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.store.impl.AppAssessmentInfoStore;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.MigratabliltyInfo;
import com.atlassian.migration.app.dto.MigrationPath;
import com.atlassian.plugin.Plugin;
import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class AppAssessmentAnalyticsEventService {
    public static final String AUTOMATIC = "Automatic";
    public static final String INSTALL_ONLY = "Install_Only";
    private final AppAssessmentInfoStore appAssessmentInfoStore;
    private final AnalyticsEventService analyticsEventService;
    private final SENSupplier senSupplier;
    private final MigrationAppAggregatorService aggregatorService;
    private final PluginManager pluginManager;
    private final DefaultRegistrar cloudMigrationRegistrar;
    private static final Map<AppAssessmentProperty, String> PROPERTY_EVENT_MAPPING = ImmutableMap.of((Object)((Object)AppAssessmentProperty.MIGRATION_NOTES), (Object)"appNotesAdded", (Object)((Object)AppAssessmentProperty.MIGRATION_STATUS), (Object)"appStatusChanged", (Object)((Object)AppAssessmentProperty.ALTERNATIVE_APP_KEY), (Object)"alternativeAppChanged");
    private static final Logger log = ContextLoggerFactory.getLogger(AppAssessmentAnalyticsEventService.class);

    public AppAssessmentAnalyticsEventService(AppAssessmentInfoStore appAssessmentInfoStore, AnalyticsEventService analyticsEventService, SENSupplier senSupplier, MigrationAppAggregatorService aggregatorService, PluginManager pluginManager, DefaultRegistrar cloudMigrationRegistrar) {
        this.appAssessmentInfoStore = appAssessmentInfoStore;
        this.analyticsEventService = analyticsEventService;
        this.senSupplier = senSupplier;
        this.aggregatorService = aggregatorService;
        this.pluginManager = pluginManager;
        this.cloudMigrationRegistrar = cloudMigrationRegistrar;
    }

    public void saveAppsFetchedEvent(Collection<String> appKeys, @Nullable ConfluenceUser confluenceUser) {
        this.saveEventsForAction(appKeys, confluenceUser, "appFetched");
    }

    public void saveStartPlanEvent(@Nullable ConfluenceUser confluenceUser) {
        List<String> appKeys = this.pluginManager.getActualUserInstalledPlugins().stream().map(Plugin::getKey).collect(Collectors.toList());
        this.saveEventsForAction(appKeys, confluenceUser, "planStarted");
    }

    public void savePropertyChangedEvent(ConfluenceUser confluenceUser, String appKey, AppAssessmentProperty propName) {
        String sourceEvent = PROPERTY_EVENT_MAPPING.get((Object)propName);
        Optional<Plugin> maybePlugin = this.getPluginByAppKey(appKey);
        if (sourceEvent != null && maybePlugin.isPresent()) {
            this.saveEventsForAction(Collections.singletonList(appKey), confluenceUser, sourceEvent);
        } else {
            log.warn("Unable to generate analytics event for change on property {}", (Object)propName);
        }
    }

    private Optional<Plugin> getPluginByAppKey(String appKey) {
        Plugin plugin = this.pluginManager.getPlugin(appKey);
        if (plugin != null) {
            return Optional.of(plugin);
        }
        return Optional.empty();
    }

    public void saveAppUsageAnalytics(ConfluenceUser confluenceUser, AppUsageDto appUsageDto) {
        long timestamp = System.currentTimeMillis();
        String batchId = UUID.randomUUID().toString();
        HashMap<String, Number> attributes = new HashMap<String, Number>();
        attributes.put("pages", appUsageDto.getPages());
        attributes.put("users", appUsageDto.getUsers());
        attributes.put("totaltime", appUsageDto.getTimeToCalculate());
        GenericOperationalEvent event = ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(timestamp).contextContainer("appBatch", batchId)).actionSubject("pluginUsageStatistics", appUsageDto.getKey())).action("reported")).email(confluenceUser == null ? null : confluenceUser.getEmail())).sen(this.senSupplier.get())).withAttributes(attributes)).build();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> event);
    }

    private void saveEventsForAction(Collection<String> appKeys, @Nullable ConfluenceUser confluenceUser, String sourceEvent) {
        String batchId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        for (String appKey : appKeys) {
            HashMap<String, Object> attributes = new HashMap<String, Object>();
            this.addAttributesFromLocalPluginAccessor(appKey, attributes);
            this.addAttributesFromAggregator(appKey, attributes);
            this.addAttributesFromUserInput(appKey, attributes);
            GenericOperationalEvent event = ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(timestamp).source(sourceEvent)).contextContainer("appBatch", batchId)).actionSubject("pluginStatistics", appKey)).action("reported")).email(confluenceUser == null ? null : confluenceUser.getEmail())).sen(this.senSupplier.get())).withAttributes(attributes)).build();
            this.analyticsEventService.saveAnalyticsEventAsync(() -> event);
        }
    }

    private void addAttributesFromLocalPluginAccessor(String appKey, Map<String, Object> attributes) {
        Plugin plugin = this.pluginManager.getPlugin(appKey);
        attributes.put("appName", plugin.getName());
        attributes.put("appServerInstalledVersion", plugin.getPluginInformation().getVersion());
        attributes.put("appEnabled", AppAssessmentFacade.isPluginEnabled(plugin));
    }

    private void addAttributesFromUserInput(String appKey, Map<String, Object> attributes) {
        AppAssessmentInfo appAssessmentInfo = this.appAssessmentInfoStore.getByAppKey(appKey).orElse(AppAssessmentInfo.empty(appKey));
        attributes.put("appAssessmentStatus", (Object)appAssessmentInfo.getMigrationStatus());
        attributes.put("appAssessmentNotesPresent", StringUtils.isNotEmpty((CharSequence)appAssessmentInfo.getMigrationNotes()));
        attributes.put("alternativeAppKey", StringUtils.defaultIfBlank((CharSequence)appAssessmentInfo.getAlternativeAppKey(), (CharSequence)""));
    }

    private void addAttributesFromAggregator(String appKey, Map<String, Object> attributes) {
        Integer aggregatorHttpErrorCode;
        MigrationAppAggregatorResponse cachedAppData = this.aggregatorService.getCachedServerAppData(appKey);
        if (cachedAppData != null && ((aggregatorHttpErrorCode = cachedAppData.getAggregatorHttpErrorCode()) == null || Response.Status.Family.CLIENT_ERROR.equals((Object)Response.Status.fromStatusCode((int)aggregatorHttpErrorCode).getFamily()))) {
            String migrationState = this.getMigrationState(cachedAppData.getMigrationPath());
            attributes.put("appAvailableCloud", cachedAppData.hasCloud());
            attributes.put("foundMarketplace", StringUtils.isNotEmpty((CharSequence)cachedAppData.getLatestVersion()));
            attributes.put("appLatestAvailableVersion", StringUtils.defaultIfBlank((CharSequence)cachedAppData.getLatestVersion(), (CharSequence)""));
            attributes.put("canBeMigrated", StringUtils.defaultIfBlank((CharSequence)cachedAppData.getMigratable(), (CharSequence)""));
            attributes.put("migrationState", StringUtils.defaultIfBlank((CharSequence)migrationState, (CharSequence)""));
            attributes.put("minVersionMigration", this.getCloudMigrationAssistantCompatibility(cachedAppData));
        }
    }

    private String getCloudMigrationAssistantCompatibility(MigrationAppAggregatorResponse cachedAppData) {
        if (MigrationPath.INSTALL_ONLY.equals((Object)cachedAppData.getMigrationPath()) || MigrationPath.AUTOMATED.equals((Object)cachedAppData.getMigrationPath())) {
            String cloudMigrationAssistantCompatibility = cachedAppData.getCloudMigrationAssistantCompatibility();
            List<MigratabliltyInfo.VersionRange> versionRangeList = cachedAppData.getCloudMigrationAssistantCompatibilityRangeList();
            Optional<String> minimumVersionFromList = Optional.empty();
            if (versionRangeList != null) {
                minimumVersionFromList = versionRangeList.stream().min(Comparator.comparing(MigratabliltyInfo.VersionRange::getStart)).map(MigratabliltyInfo.VersionRange::getStart);
            }
            String minimumVersion = cloudMigrationAssistantCompatibility;
            return minimumVersionFromList.orElse(minimumVersion);
        }
        return "";
    }

    private String getMigrationState(MigrationPath migrationPath) {
        if (migrationPath == null) {
            return "No";
        }
        switch (migrationPath) {
            case AUTOMATED: {
                return AUTOMATIC;
            }
            case MANUAL: {
                return "Manual";
            }
            case INSTALL_ONLY: {
                return INSTALL_ONLY;
            }
            case UNKNOWN: {
                return "Unknown";
            }
        }
        return "";
    }
}

