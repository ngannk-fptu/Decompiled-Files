/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckExecutionStatus
 *  com.atlassian.cmpt.check.base.CheckRequest
 *  com.atlassian.cmpt.check.base.CheckStatus
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.migration.utils.MigrationStatusCalculator
 *  com.atlassian.migration.utils.MigrationStatusCalculator$CoreMigrationStatus
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallAppMigrationStatus
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallMigrationStatus
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cmpt.check.base.CheckExecutionStatus;
import com.atlassian.cmpt.check.base.CheckRequest;
import com.atlassian.cmpt.check.base.CheckStatus;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.dto.MigrationDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.PreflightCheckPlanDto;
import com.atlassian.migration.agent.dto.PreflightCheckProgressDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.SpaceDto;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.check.AsyncCheckExecutor;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.check.CheckRegistry;
import com.atlassian.migration.agent.service.check.CheckTransformerService;
import com.atlassian.migration.agent.service.impl.SpaceCatalogService;
import com.atlassian.migration.utils.MigrationStatusCalculator;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class PlanDecoratorService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PlanDecoratorService.class);
    private final PlanService planService;
    private final CheckTransformerService checkTransformerService;
    private final AsyncCheckExecutor checkExecutor;
    private final ExecutorService executorService;
    private final CheckRegistry checkRegistry;
    private final CheckOverrideService checkOverrideService;
    private final SpaceCatalogService spaceCatalogService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public PlanDecoratorService(PlanService planService, CheckTransformerService checkTransformerService, AsyncCheckExecutor checkExecutor, CheckRegistry checkRegistry, CheckOverrideService checkOverrideService, SpaceCatalogService spaceCatalogService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this(planService, checkTransformerService, checkExecutor, checkRegistry, checkOverrideService, Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)PlanDecoratorService.class.getName())), spaceCatalogService, migrationDarkFeaturesManager);
    }

    @VisibleForTesting
    PlanDecoratorService(PlanService planService, CheckTransformerService checkTransformerService, AsyncCheckExecutor checkExecutor, CheckRegistry checkRegistry, CheckOverrideService checkOverrideService, ExecutorService executorService, SpaceCatalogService spaceCatalogService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.planService = planService;
        this.checkTransformerService = checkTransformerService;
        this.checkExecutor = checkExecutor;
        this.executorService = executorService;
        this.checkRegistry = checkRegistry;
        this.checkOverrideService = checkOverrideService;
        this.spaceCatalogService = spaceCatalogService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @PreDestroy
    @VisibleForTesting
    void preDestroy() {
        this.executorService.shutdownNow();
    }

    public PreflightCheckPlanDto getPlan(String planId) {
        PlanDto planDto = this.planService.getPlan(planId);
        return this.getPreflightCheckPlanDto(planDto, true);
    }

    public PlanDto getPlanDto(String planId) {
        return this.planService.getPlan(planId);
    }

    public List<PreflightCheckPlanDto> getAllPlans() {
        List<PlanDto> plans = this.planService.getAllPlans();
        if (plans.isEmpty()) {
            return Collections.emptyList();
        }
        return plans.stream().map(planDto -> this.getPreflightCheckPlanDto((PlanDto)planDto, false)).collect(Collectors.toList());
    }

    public PlanDto createPlan(PlanDto plan) {
        return this.planService.createPlan(plan, false);
    }

    public PlanDto createPlan(PlanDto plan, boolean shouldOmitTasks) {
        return this.planService.createPlan(plan, shouldOmitTasks);
    }

    public PlanDto updatePlan(PlanDto plan) {
        return this.planService.updatePlan(plan);
    }

    public boolean deletePlan(String planId) {
        return this.planService.deletePlan(planId);
    }

    public PreflightCheckProgressDto getPlanProgress(String planId) {
        ProgressDto progressDto = this.planService.getPlanProgress(planId);
        AppsProgressDto appsProgressDto = this.getAppsProgressDto(planId);
        MigrationDto migrationStatus = this.calculateMigration(progressDto, appsProgressDto);
        return new PreflightCheckProgressDto(progressDto, this.getCheckResultDtos(planId), migrationStatus);
    }

    public boolean stop(String planId) {
        return this.planService.stop(planId);
    }

    public boolean planNameExists(String planName, String planId) {
        return this.planService.planNameExists(planName, planId);
    }

    public PreflightCheckPlanDto verifyAndStart(String planId) {
        log.info("Verify and start plan {}", (Object)planId);
        PlanDto planDto = this.planService.verifyPlan(planId);
        this.executorService.submit(() -> this.startPreflightChecksAndPoll(planDto));
        return this.getPreflightCheckPlanDto(planDto, true);
    }

    public PlanDto copy(String planId) {
        return this.planService.copyPlan(planId);
    }

    public PlanDto updateActiveStatus(String planId, PlanActiveStatus activeStatus) {
        return this.planService.updateActiveStatus(planId, activeStatus);
    }

    public Collection<SpaceDto> getSpaces(String planId) {
        return this.spaceCatalogService.getSpacesSummaryForPlan(planId);
    }

    public Collection<SpaceDto> getSpacesForPlan(String planId) {
        return this.spaceCatalogService.getSpacesForPlan(planId);
    }

    public PreflightCheckPlanDto getPreflightCheckPlanDto(PlanDto planDto, Boolean getCheckResultDetails) {
        List<CheckResultDto> checkResultDtos = getCheckResultDetails != false || !planDto.hasFinishedExecuting() ? this.getCheckResultDtos(planDto.getId()) : Collections.emptyList();
        AppsProgressDto appsProgressDto = this.getAppsProgressDto(planDto.getId());
        MigrationDto migrationStatus = this.calculateMigration(planDto.getProgress(), appsProgressDto);
        return new PreflightCheckPlanDto(planDto, checkResultDtos, appsProgressDto, migrationStatus);
    }

    @VisibleForTesting
    void startPreflightChecksAndPoll(PlanDto planDto) {
        if (this.migrationDarkFeaturesManager.isPreflightChecksDisabledBeforeRun()) {
            log.info("Preflight check is disabled before run for {}", (Object)planDto.getId());
            this.planService.startPlan(planDto.getId());
        } else {
            this.startPreflightCheckAndPoll(planDto);
        }
    }

    private void startPreflightCheckAndPoll(PlanDto planDto) {
        log.info("Start Preflight check and poll {}", (Object)planDto.getId());
        List<CheckRequest> checkRequests = this.checkTransformerService.getCheckRequests(planDto);
        this.checkExecutor.executeNonOverriddenChecks(planDto.getId(), planDto.getPreflightChecksToOverride(), checkRequests);
        while (true) {
            Optional<CheckExecutionStatus> maybeCheckExecutionStatus;
            if (!(maybeCheckExecutionStatus = this.checkExecutor.getStatus(planDto.getId())).isPresent()) {
                this.planService.setCreatedStatus(planDto.getId());
                return;
            }
            CheckExecutionStatus checkExecutionStatus = maybeCheckExecutionStatus.get();
            boolean hasRunningChecks = checkExecutionStatus.statuses.stream().anyMatch(checkStatus -> checkStatus.checkResult == null);
            if (!hasRunningChecks) {
                boolean hasErrors = checkExecutionStatus.statuses.stream().anyMatch(checkStatus -> !checkStatus.checkResult.success && this.isBlocked(planDto, (CheckStatus)checkStatus));
                if (hasErrors) {
                    this.planService.setCreatedStatus(planDto.getId());
                } else {
                    this.planService.startPlan(planDto.getId());
                }
                return;
            }
            this.doSleep(100);
        }
    }

    @VisibleForTesting
    void doSleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            log.debug("Failed to sleep. Sleep was interrupted.", (Throwable)e);
            Thread.currentThread().interrupt();
        }
    }

    private List<CheckResultDto> getCheckResultDtos(String planId) {
        Optional<CheckExecutionStatus> maybeStatus = this.checkExecutor.getStatus(planId);
        return maybeStatus.map(status -> this.applyOverrides(planId, this.checkTransformerService.toCheckResultDtos(status.statuses))).orElse(Collections.emptyList());
    }

    private AppsProgressDto getAppsProgressDto(String planId) {
        return this.planService.getAppsProgress(planId).orElse(AppsProgressDto.empty());
    }

    private List<CheckResultDto> applyOverrides(String planId, List<CheckResultDto> results) {
        return this.checkOverrideService.applyAndOverride(planId, results);
    }

    private boolean isBlocked(PlanDto planDto, CheckStatus checkStatus) {
        boolean isOverridden = this.checkOverrideService.isOverriddenByExecutionIdAndCheckType(planDto.getId(), checkStatus.checkType);
        if (isOverridden) {
            return !this.checkRegistry.shouldBlockMigration(checkStatus);
        }
        return this.checkRegistry.shouldBlockMigration(checkStatus);
    }

    private MigrationDto calculateMigration(ProgressDto progressDto, AppsProgressDto appsProgress) {
        MigrationStatusCalculator.CoreMigrationStatus coreMigrationStatus = this.convertStatusToCoreMigrationStatus(progressDto.getStatus());
        MigrationStatusCalculator.OverallAppMigrationStatus overallAppMigrationStatus = this.getOverallAppsStatus(appsProgress);
        MigrationStatusCalculator.OverallMigrationStatus overallMigrationStatus = this.getOverallStatus(coreMigrationStatus, appsProgress);
        return new MigrationDto(overallMigrationStatus, coreMigrationStatus, overallAppMigrationStatus);
    }

    private MigrationStatusCalculator.OverallAppMigrationStatus getOverallAppsStatus(AppsProgressDto appsProgress) {
        MigrationStatusCalculator.OverallAppMigrationStatus status = appsProgress.getAggregateStatus();
        if (status == null) {
            Set appProgressStatus = appsProgress.getApps().stream().map(AppsProgressDto.App::getStatus).collect(Collectors.toSet());
            status = MigrationStatusCalculator.calculateAndGetOverallAppsStatus(appProgressStatus);
        }
        return status;
    }

    private MigrationStatusCalculator.OverallMigrationStatus getOverallStatus(MigrationStatusCalculator.CoreMigrationStatus coreMigrationStatus, AppsProgressDto appsProgress) {
        MigrationStatusCalculator.OverallAppMigrationStatus status = appsProgress.getAggregateStatus();
        Object appProgressStatus = status == MigrationStatusCalculator.OverallAppMigrationStatus.FAILED ? ImmutableSet.of((Object)MigrationStatusCalculator.OverallAppMigrationStatus.FAILED.name()) : appsProgress.getApps().stream().map(AppsProgressDto.App::getStatus).collect(Collectors.toSet());
        return MigrationStatusCalculator.calculateAndGetOverallStatus((MigrationStatusCalculator.CoreMigrationStatus)coreMigrationStatus, (Set)appProgressStatus);
    }

    private MigrationStatusCalculator.CoreMigrationStatus convertStatusToCoreMigrationStatus(ProgressDto.Status status) {
        switch (status) {
            case READY: {
                return MigrationStatusCalculator.CoreMigrationStatus.SAVED_READY;
            }
            case RUNNING: {
                return MigrationStatusCalculator.CoreMigrationStatus.RUNNING;
            }
            case STOPPING: 
            case STOPPED: 
            case INCOMPLETE: {
                return MigrationStatusCalculator.CoreMigrationStatus.INCOMPLETE;
            }
            case FINISHED: {
                return MigrationStatusCalculator.CoreMigrationStatus.COMPLETE;
            }
            case FAILED: {
                return MigrationStatusCalculator.CoreMigrationStatus.FAILED;
            }
        }
        throw new IllegalArgumentException("Unknown status " + status.name());
    }

    public boolean hasAppMigrationInProgress() {
        List plans = this.planService.getAllPlans().stream().filter(plan -> plan.getProgress().getStatus() == ProgressDto.Status.FINISHED).collect(Collectors.toList());
        if (plans.isEmpty()) {
            return false;
        }
        return plans.stream().map(plan -> this.getOverallAppsStatus(this.getAppsProgressDto(plan.getId()))).anyMatch(status -> status == MigrationStatusCalculator.OverallAppMigrationStatus.RUNNING || status == MigrationStatusCalculator.OverallAppMigrationStatus.READY);
    }
}

