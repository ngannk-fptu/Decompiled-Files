/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.mapper.CheckResultMapper
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PreDestroy
 */
package com.atlassian.migration.agent.service.check.group;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.mapper.CheckResultMapper;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistration;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.group.GroupNamesCheckContextProvider;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictChecker;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictContext;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictMapper;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PreDestroy;

@ParametersAreNonnullByDefault
public class GroupNamesConflictCheckRegistration
implements CheckRegistration<GroupNamesConflictContext> {
    private final GroupNamesConflictChecker checker;
    private final GroupNamesConflictMapper resultMapper;
    private final GroupNamesCheckContextProvider provider;
    private final ExecutorService executorService;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public GroupNamesConflictCheckRegistration(CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService, AnalyticsEventBuilder analyticsEventBuilder, UserGroupExtractFacade userGroupExtractFacade) {
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.executorService = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getName()));
        this.checker = new GroupNamesConflictChecker(this.executorService, cloudSiteService, usersMigrationService);
        this.resultMapper = new GroupNamesConflictMapper();
        this.provider = new GroupNamesCheckContextProvider(userGroupExtractFacade);
    }

    @PreDestroy
    public void destroy() {
        this.executorService.shutdownNow();
    }

    @Override
    public CheckType getCheckType() {
        return CheckType.GROUP_NAMES_CONFLICT;
    }

    @Override
    public Checker<GroupNamesConflictContext> getChecker() {
        return this.checker;
    }

    @Override
    public CheckContextProvider<GroupNamesConflictContext> getCheckContextProvider() {
        return this.provider;
    }

    @Override
    public CheckResultMapper getCheckResultMapper() {
        return this.resultMapper;
    }

    @Override
    public EventDto getAnalyticsEventModel(CheckResult checkResult, long totalTime) {
        return this.analyticsEventBuilder.buildPreflightGroupNamesConflict(checkResult.success, GroupNamesConflictChecker.retrieveDuplicateGroupNames(checkResult.details), totalTime);
    }

    @Override
    public String getFailedToExecuteAnalyticsEventName() {
        return "cloudExistantGroupsCheck";
    }
}

