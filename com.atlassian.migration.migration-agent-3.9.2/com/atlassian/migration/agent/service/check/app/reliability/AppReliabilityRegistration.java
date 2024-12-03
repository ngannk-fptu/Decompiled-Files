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
package com.atlassian.migration.agent.service.check.app.reliability;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityContext;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityContextProvider;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityMapper;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabiltityChecker;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppReliabilityRegistration
implements CheckRegistration<AppReliabilityContext> {
    private final AppReliabiltityChecker checker;
    private final AppReliabilityContextProvider contextProvider;
    private final AppReliabilityMapper mapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppReliabilityRegistration(AppReliabiltityChecker appReliabiltityChecker, AppReliabilityContextProvider contextProvider, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checker = appReliabiltityChecker;
        this.contextProvider = contextProvider;
        this.mapper = new AppReliabilityMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_RELIABILITY;
    }

    @Override
    public Checker<AppReliabilityContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppReliabilityContext> getCheckContextProvider() {
        return this.contextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.mapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppReliability(checkResult.success, 0, totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appReliabilityCheck";
    }
}

