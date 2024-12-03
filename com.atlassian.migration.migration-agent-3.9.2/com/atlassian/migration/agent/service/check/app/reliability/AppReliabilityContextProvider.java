/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.reliability;

import com.atlassian.migration.agent.dto.AppDto;
import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityContext;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppReliabilityContextProvider
implements CheckContextProvider<AppReliabilityContext> {
    private AppAssessmentFacade appAssessmentFacade;

    public AppReliabilityContextProvider(AppAssessmentFacade appAssessmentFacade) {
        this.appAssessmentFacade = appAssessmentFacade;
    }

    @Override
    public AppReliabilityContext apply(Map<String, Object> parameters) {
        String appKeys = (String)parameters.getOrDefault("excludedAppKeys", "");
        Set<String> excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(appKeys);
        Set<String> apps = this.appAssessmentFacade.getAppsNeededInCloud().getApps().stream().map(AppDto::getKey).filter(appKey -> !excludedAppKeys.contains(appKey)).collect(Collectors.toSet());
        return new AppReliabilityContext(apps);
    }
}

