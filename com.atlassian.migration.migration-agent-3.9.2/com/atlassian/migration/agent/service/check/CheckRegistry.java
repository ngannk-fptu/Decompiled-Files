/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckContext
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.CheckStatus
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.CheckStatus;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CheckRegistry {
    private final Map<CheckType, CheckRegistration<CheckContext>> checksByCheckType;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public CheckRegistry(List<CheckRegistration> checkRegistrations, AnalyticsEventBuilder analyticsEventBuilder) {
        this.checksByCheckType = checkRegistrations.stream().collect(Collectors.toMap(CheckRegistration::getCheckType, reg -> reg));
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    public CheckContextProvider<CheckContext> getCheckContextProvider(CheckType checkType) {
        return this.checksByCheckType.get(checkType).getCheckContextProvider();
    }

    public Checker<CheckContext> getChecker(CheckType checkType) {
        return this.checksByCheckType.get(checkType).getChecker();
    }

    public CheckResultMapper getResultMapper(CheckType checkType) {
        return this.checksByCheckType.get(checkType).getCheckResultMapper();
    }

    public boolean shouldBlockMigration(CheckStatus checkStatus) {
        CheckType checkType = CheckType.fromString(checkStatus.checkType);
        return checkType.blocksMigration();
    }

    EventDto getAnalyticsEventModel(CheckType checkType, CheckResult checkResult, String executionId, long totalTime) {
        EventDto analyticsEventModel;
        Integer errorCode = Checker.retrieveExecutionErrorCode((CheckResult)checkResult);
        if (errorCode != null) {
            String actionSubject = this.checksByCheckType.get(checkType).getFailedToExecuteAnalyticsEventName();
            analyticsEventModel = this.analyticsEventBuilder.buildPreflightFailed(actionSubject, executionId, errorCode);
        } else {
            analyticsEventModel = this.checksByCheckType.get(checkType).getAnalyticsEventModel(checkResult, totalTime);
        }
        return analyticsEventModel;
    }
}

