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
package com.atlassian.migration.agent.service.check.template;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictCheckContextProvider;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictChecker;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictContext;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictMapper;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GlobalDataTemplateConflictCheckRegistration
implements CheckRegistration<GlobalDataTemplateConflictContext> {
    private final GlobalDataTemplateConflictCheckContextProvider checkContextProvider;
    private final GlobalDataTemplateConflictChecker checker;
    private final GlobalDataTemplateConflictMapper checkMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public GlobalDataTemplateConflictCheckRegistration(GlobalDataTemplateConflictCheckContextProvider checkContextProvider, GlobalDataTemplateConflictChecker checker, GlobalDataTemplateConflictMapper checkMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checkContextProvider = checkContextProvider;
        this.checker = checker;
        this.checkMapper = checkMapper;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.GLOBAL_DATA_TEMPLATE;
    }

    @Override
    public Checker<GlobalDataTemplateConflictContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<GlobalDataTemplateConflictContext> getCheckContextProvider() {
        return this.checkContextProvider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.checkMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightGlobalDataTemplatesConflict(checkResult.success, GlobalDataTemplateConflictMapper.retrieveConflictingTemplates(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "globalDataTemplateCheck";
    }
}

