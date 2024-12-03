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
package com.atlassian.migration.agent.service.check.app.assessmentcomplete;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteChecker;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteContext;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteContextProvider;
import com.atlassian.migration.agent.service.check.app.assessmentcomplete.AppAssessmentCompleteMapper;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppAssessmentCompleteCheckRegistration
implements CheckRegistration<AppAssessmentCompleteContext> {
    private final AppAssessmentCompleteChecker checker = new AppAssessmentCompleteChecker();
    private final AppAssessmentCompleteContextProvider provider;
    private final AppAssessmentCompleteMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public AppAssessmentCompleteCheckRegistration(AppAssessmentFacade appsAssessmentService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.provider = new AppAssessmentCompleteContextProvider(appsAssessmentService);
        this.resultMapper = new AppAssessmentCompleteMapper();
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.APP_ASSESSMENT_COMPLETE;
    }

    @Override
    public Checker<AppAssessmentCompleteContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<AppAssessmentCompleteContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightAppAssessmentComplete(checkResult.success, AppAssessmentCompleteChecker.retrieveAppsWithIncompleteAssessment(checkResult.details).size(), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "appAssessmentCompleteCheck";
    }
}

