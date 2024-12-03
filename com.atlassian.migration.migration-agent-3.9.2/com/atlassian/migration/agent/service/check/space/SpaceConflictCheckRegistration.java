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
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.space.SpaceConflictCheckContextProvider;
import com.atlassian.migration.agent.service.check.space.SpaceConflictChecker;
import com.atlassian.migration.agent.service.check.space.SpaceConflictContext;
import com.atlassian.migration.agent.service.check.space.SpaceConflictMapper;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpaceConflictCheckRegistration
implements CheckRegistration<SpaceConflictContext> {
    private final SpaceConflictCheckContextProvider provider;
    private final SpaceConflictChecker checker;
    private final SpaceConflictMapper resultMapper;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public SpaceConflictCheckRegistration(SpaceConflictCheckContextProvider provider, SpaceConflictChecker checker, SpaceConflictMapper spaceConflictMapper, AnalyticsEventBuilder analyticsEventBuilder) {
        this.provider = provider;
        this.checker = checker;
        this.resultMapper = spaceConflictMapper;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.SPACE_KEYS_CONFLICT;
    }

    @Override
    public Checker<SpaceConflictContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<SpaceConflictContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightSpaceKeysConflict(checkResult.success, SpaceConflictChecker.retrieveConflictingSpaces(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "spaceConflictCheck";
    }
}

