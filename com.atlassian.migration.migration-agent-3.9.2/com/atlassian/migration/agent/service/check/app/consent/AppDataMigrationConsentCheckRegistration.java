/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.consent;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.app.AppAccessScopeService;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentChecker;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentContext;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentContextProvider;
import com.atlassian.migration.agent.service.check.app.consent.AppDataMigrationConsentMapper;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppDataMigrationConsentCheckRegistration
implements CheckRegistration<AppDataMigrationConsentContext> {
    private final AppDataMigrationConsentMapper mapper = new AppDataMigrationConsentMapper();
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AppDataMigrationConsentChecker checker;
    private final AppDataMigrationConsentContextProvider contextProvider;

    public AppDataMigrationConsentCheckRegistration(AppAssessmentInfoService appAssessmentInfoService, AppAssessmentFacade appAssessmentFacade, AnalyticsEventBuilder analyticsEventBuilder, AppAccessScopeService appConsentService, PluginManager pluginManager) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.contextProvider = new AppDataMigrationConsentContextProvider(appAssessmentFacade);
        this.checker = new AppDataMigrationConsentChecker(appConsentService, appAssessmentInfoService, pluginManager);
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_DATA_MIGRATION_CONSENT;
    }

    @Override
    public Checker<AppDataMigrationConsentContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppDataMigrationConsentContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppDataConsent(checkResult.success, AppDataMigrationConsentChecker.retrieveNotConsentedApps(checkResult.details).size(), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appDataMigrationConsentCheck";
    }
}

