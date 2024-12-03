/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.outdated;

import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedContext;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ServerAppsOutdatedContextProvider
implements CheckContextProvider<ServerAppsOutdatedContext> {
    private final AppAssessmentInfoService appAssessmentInfoService;
    private final MigrationAppAggregatorService migrationAppAggregatorService;
    private static final String EXCLUDED_APPS_KEY = "excludedAppKeys";

    public ServerAppsOutdatedContextProvider(AppAssessmentInfoService appAssessmentInfoService, MigrationAppAggregatorService migrationAppAggregatorService) {
        this.appAssessmentInfoService = appAssessmentInfoService;
        this.migrationAppAggregatorService = migrationAppAggregatorService;
    }

    @Override
    public ServerAppsOutdatedContext apply(Map<String, Object> parameters) {
        String excludedAppKeysJoined = (String)parameters.get(EXCLUDED_APPS_KEY);
        if (excludedAppKeysJoined == null) {
            throw new IllegalArgumentException("Unable to retrieve excludedAppKeys in parameters");
        }
        Set<String> excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(excludedAppKeysJoined);
        List<String> appKeys = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud().stream().map(AppAssessmentInfo::getAppKey).filter(appKey -> !excludedAppKeys.contains(appKey)).filter(appKey -> {
            MigrationAppAggregatorResponse response = this.migrationAppAggregatorService.getCachedServerAppData((String)appKey);
            return "YES".equalsIgnoreCase(response.getMigratable());
        }).collect(Collectors.toList());
        return new ServerAppsOutdatedContext(appKeys);
    }
}

