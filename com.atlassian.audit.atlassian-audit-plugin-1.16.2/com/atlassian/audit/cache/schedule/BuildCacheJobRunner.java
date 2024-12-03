/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.cache.schedule;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.audit.ao.service.CachedActionsService;
import com.atlassian.audit.ao.service.CachedCategoriesService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class BuildCacheJobRunner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(BuildCacheJobRunner.class);
    private final ActiveObjects ao;
    private final CachedActionsService cachedActionsService;
    private final CachedCategoriesService cachedCategoriesService;

    public BuildCacheJobRunner(ActiveObjects ao, CachedActionsService cachedActionsService, CachedCategoriesService cachedCategoriesService) {
        this.ao = Objects.requireNonNull(ao, "ao");
        this.cachedActionsService = Objects.requireNonNull(cachedActionsService, "cachedActionsService");
        this.cachedCategoriesService = Objects.requireNonNull(cachedCategoriesService, "cachedCategoriesService");
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        try {
            this.ao.moduleMetaData().awaitInitialization();
        }
        catch (Exception exception) {
            log.error("Failed to schedule a job to build the audit categories and audit summaries (AKA actions) cache because an exception was thrown while waiting for Active Objects to initialize.", (Throwable)exception);
            return JobRunnerResponse.failed((Throwable)exception);
        }
        try {
            this.cachedActionsService.rebuildCache();
        }
        catch (Exception exception) {
            log.error("Failed to rebuild the summaries (AKA actions) cache", (Throwable)exception);
            return JobRunnerResponse.failed((Throwable)exception);
        }
        if (jobRunnerRequest.isCancellationRequested()) {
            return JobRunnerResponse.aborted((String)"Scheduled job aborted after building the summaries (AKA actions) cache, but before building categories cache");
        }
        try {
            this.cachedCategoriesService.rebuildCache();
        }
        catch (Exception exception) {
            log.error("Failed to rebuild the categories cache", (Throwable)exception);
            return JobRunnerResponse.failed((Throwable)exception);
        }
        log.info("Successfully rebuilt the audit categories and summaries (AKA actions) cache");
        return JobRunnerResponse.success();
    }
}

