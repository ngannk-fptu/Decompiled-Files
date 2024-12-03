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
package com.atlassian.migration.agent.service.check.app.outdated;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedChecker;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedContext;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedContextProvider;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedMapper;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ServerAppsOutdatedCheckRegistration
implements CheckRegistration<ServerAppsOutdatedContext> {
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final ServerAppsOutdatedChecker checker;
    private final ServerAppsOutdatedContextProvider contextProvider;
    private final ServerAppsOutdatedMapper mapper;

    public ServerAppsOutdatedCheckRegistration(AnalyticsEventBuilder analyticsEventBuilder, AppAssessmentInfoService appAssessmentInfoService, PluginManager pluginManager, MigrationAppAggregatorService appAggregatorService, AppAssessmentFacade appAssessmentFacade) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.checker = new ServerAppsOutdatedChecker(pluginManager, appAggregatorService, appAssessmentFacade);
        this.contextProvider = new ServerAppsOutdatedContextProvider(appAssessmentInfoService, appAggregatorService);
        this.mapper = new ServerAppsOutdatedMapper();
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SERVER_APPS_OUTDATED;
    }

    @Override
    public Checker<ServerAppsOutdatedContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<ServerAppsOutdatedContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightServerAppOutdated(checkResult.success, ServerAppsOutdatedChecker.retrieveOutdatedServerApps(checkResult.details).size(), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "serverAppsOutdatedCheck";
    }
}

