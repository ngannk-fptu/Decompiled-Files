/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.consent;

import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.dto.assessment.AppConsentDto;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentContext;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppDataMigrationConsentContextProvider
implements CheckContextProvider<AppDataMigrationConsentContext> {
    private final AppAssessmentFacade appAssessmentFacade;
    private static final String EXCLUDED_APPS_KEY = "excludedAppKeys";

    public AppDataMigrationConsentContextProvider(AppAssessmentFacade appAssessmentFacade) {
        this.appAssessmentFacade = appAssessmentFacade;
    }

    @Override
    public AppDataMigrationConsentContext apply(Map<String, Object> parameters) {
        String excludedAppKeysJoined = (String)parameters.get(EXCLUDED_APPS_KEY);
        if (excludedAppKeysJoined == null) {
            throw new IllegalArgumentException("Unable to retrieve excludedAppKeys in parameters");
        }
        Set<String> excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(excludedAppKeysJoined);
        List<String> appKeys = this.appAssessmentFacade.getRequiredConsentApps().getApps().stream().map(AppConsentDto::getKey).filter(appKey -> !excludedAppKeys.contains(appKey)).collect(Collectors.toList());
        return new AppDataMigrationConsentContext(appKeys);
    }
}

