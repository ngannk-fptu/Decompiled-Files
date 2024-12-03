/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.app.notinstalled;

import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.app.notinstalled.AppsNotInstalledOnCloudContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AppsNotInstalledOnCloudContextProvider
implements CheckContextProvider<AppsNotInstalledOnCloudContext> {
    private final AppAssessmentInfoService appAssessmentInfoService;
    private static final String EXCLUDED_APPS_KEY = "excludedAppKeys";

    public AppsNotInstalledOnCloudContextProvider(AppAssessmentInfoService appAssessmentInfoService) {
        this.appAssessmentInfoService = appAssessmentInfoService;
    }

    @Override
    public AppsNotInstalledOnCloudContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        String excludedAppKeysJoined = (String)parameters.get(EXCLUDED_APPS_KEY);
        if (excludedAppKeysJoined == null) {
            throw new IllegalArgumentException("Unable to retrieve excludedAppKeys in parameters");
        }
        Set<String> excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(excludedAppKeysJoined);
        List<AppAssessmentInfo> appsNeededInCloud = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud();
        List<String> appKeys = appsNeededInCloud.stream().map(AppAssessmentInfo::getAppKey).filter(appKey -> !excludedAppKeys.contains(appKey)).collect(Collectors.toList());
        return new AppsNotInstalledOnCloudContext(cloudId, appKeys);
    }
}

