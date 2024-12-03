/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.migration.agent.service.check.group;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.group.GroupNamesConflictContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.GroupConflictsCheckRequest;
import com.atlassian.migration.agent.service.user.GroupsConflictCheckResponse;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.springframework.util.CollectionUtils;

public class GroupNamesConflictChecker
implements Checker<GroupNamesConflictContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(GroupNamesConflictChecker.class);
    private static final String VIOLATIONS_KEY = "violations";
    private final CloudSiteService cloudSiteService;
    private final ExecutorService executorService;
    private final RetryingUsersMigrationService usersMigrationService;

    GroupNamesConflictChecker(ExecutorService executorService, CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService) {
        this.cloudSiteService = cloudSiteService;
        this.usersMigrationService = usersMigrationService;
        this.executorService = executorService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CheckResult check(GroupNamesConflictContext ctx) {
        String taskId;
        if (CollectionUtils.isEmpty(ctx.groups)) {
            return new CheckResult(true);
        }
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(ctx.cloudId);
        if (!cloudSite.isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), ctx.cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        String containerToken = cloudSite.get().getContainerToken();
        Future<GroupsConflictCheckResponse> future = null;
        try {
            taskId = this.usersMigrationService.startGroupConflictsCheck(containerToken, new GroupConflictsCheckRequest(ctx.groups));
        }
        catch (Exception e) {
            PreflightErrorCode errorCode = PreflightErrorCode.GROUP_CONFLICT_CHECK_ERROR;
            log.error("Error code- {}: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), e});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        try {
            future = this.executorService.submit(() -> {
                GroupsConflictCheckResponse response;
                while (!(response = this.usersMigrationService.getGroupConflictsCheckStatus(containerToken, taskId)).isComplete()) {
                    try {
                        this.doSleep(2500);
                    }
                    catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return response;
            });
            GroupsConflictCheckResponse checkResponse = future.get(5L, TimeUnit.MINUTES);
            List conflictingGroups = checkResponse.getConflictingGroups().stream().sorted().collect(Collectors.toList());
            CheckResult checkResult = new CheckResult(checkResponse.isSuccessful(), Collections.singletonMap(VIOLATIONS_KEY, conflictingGroups));
            return checkResult;
        }
        catch (Exception e) {
            log.error("An error occurred during group names conflict check.", (Throwable)e);
            CheckResult checkResult = Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
            return checkResult;
        }
        finally {
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    static List<String> retrieveDuplicateGroupNames(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }

    @VisibleForTesting
    void doSleep(int milliseconds) throws InterruptedException {
        Thread.sleep(milliseconds);
    }
}

