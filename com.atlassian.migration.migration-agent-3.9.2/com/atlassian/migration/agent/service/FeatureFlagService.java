/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.FeatureFlagActionSubject;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureFlagService {
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public FeatureFlagService(MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    public List<String> getEnabledMigrationPluginFeatures() {
        return this.migrationDarkFeaturesManager.getAllEnabledFeatures().stream().filter(featureKey -> featureKey.startsWith("migration-assistant.")).collect(Collectors.toList());
    }

    public void saveFeatureFlagAnalyticEvent(FeatureFlagActionSubject actionSubject, String actionSubjectId, String enabledMigrationFeatures) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildFeatureFlagsOperationalEvent(enabledMigrationFeatures, actionSubject, actionSubjectId));
    }
}

