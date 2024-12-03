/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckExecutionStatus
 *  com.atlassian.cmpt.check.base.CheckRequest
 *  com.atlassian.cmpt.check.base.CheckStatus
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  org.apache.commons.collections.CollectionUtils
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.base.CheckExecutionStatus;
import com.atlassian.cmpt.check.base.CheckRequest;
import com.atlassian.cmpt.check.base.CheckStatus;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.check.AsyncCheckExecutor;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.check.CheckTransformerService;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckResultDto;
import com.atlassian.migration.agent.service.check.app.vendorcheck.SerializableCsvFileContentDto;
import com.atlassian.migration.agent.service.check.attachment.MissingAttachmentDto;
import com.atlassian.migration.agent.service.check.csv.AppVendorCheckCSVContainer;
import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.migration.agent.service.check.csv.GlobalDataTemplateCSVBean;
import com.atlassian.migration.agent.service.check.csv.GlobalDataTemplateCSVContainer;
import com.atlassian.migration.agent.service.check.csv.MissingAttachmentCSVBean;
import com.atlassian.migration.agent.service.check.csv.MissingAttachmentCSVContainer;
import com.atlassian.migration.agent.service.check.csv.SpaceAnonymousCSVBean;
import com.atlassian.migration.agent.service.check.csv.SpaceAnonymousCSVContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

public class PreflightService {
    private static final Logger log = ContextLoggerFactory.getLogger(PreflightService.class);
    private final AsyncCheckExecutor checkExecutor;
    private final CheckTransformerService checkTransformerService;
    private final CheckResultsService checkResultService;
    private final CheckOverrideService checkOverrideService;
    private final PlanService planService;

    public PreflightService(AsyncCheckExecutor checkExecutor, CheckTransformerService checkTransformerService, CheckResultsService checkResultService, CheckOverrideService checkOverrideService, PlanService planService) {
        this.checkExecutor = checkExecutor;
        this.checkTransformerService = checkTransformerService;
        this.checkResultService = checkResultService;
        this.checkOverrideService = checkOverrideService;
        this.planService = planService;
    }

    public List<CheckResultDto> getCheckExecutionStatus(String executionId) {
        ArrayList<CheckResultDto> results = new ArrayList<CheckResultDto>();
        Optional<CheckExecutionStatus> maybeStatus = this.checkExecutor.getStatus(executionId);
        maybeStatus.ifPresent(checkExecutionStatus -> results.addAll(this.checkTransformerService.toCheckResultDtos(checkExecutionStatus.statuses)));
        return this.checkOverrideService.applyAndOverride(executionId, results);
    }

    public List<CheckResultDto> getCheckExecutionStatus(String executionId, CheckType checkType) {
        ArrayList<CheckResultDto> results = new ArrayList<CheckResultDto>();
        Optional<CheckExecutionStatus> maybeStatus = this.checkExecutor.getStatus(executionId, checkType);
        maybeStatus.ifPresent(checkExecutionStatus -> results.addAll(this.checkTransformerService.toCheckResultDtos(checkExecutionStatus.statuses)));
        return this.checkOverrideService.applyAndOverride(executionId, results);
    }

    public void executeChecks(String executionId, PlanDto planDto, Set<CheckType> types) {
        log.info("Execute checks execution id: {} types: {}", (Object)executionId, (Object)types.stream().map(CheckType::value).collect(Collectors.joining(",")));
        List<CheckRequest> checkRequests = this.checkTransformerService.getCheckRequests(planDto, types, executionId);
        if (CollectionUtils.isEmpty(types)) {
            log.info("Re-executing all checks with executionId: {}", (Object)executionId);
            this.checkExecutor.unscheduleCheckJobs(executionId);
            this.checkResultService.deleteCheckResultsByExecutionId(executionId);
        }
        this.checkExecutor.executeNonOverriddenChecks(executionId, planDto.getPreflightChecksToOverride(), checkRequests);
        this.checkOverrideService.createOverrides(executionId, planDto.getPreflightChecksToOverride());
        log.info("Execution Id: {} scheduled checks {} ", (Object)executionId, (Object)checkRequests.stream().map(ch -> ch.checkType).collect(Collectors.joining(", ")));
    }

    public CheckResultCSVContainer createCheckResultCSVContainer(String executionId, CheckType checkType) {
        Optional<CheckStatus> maybeCheckStatus = this.getCheckStatus(executionId, checkType);
        if (checkType.equals(CheckType.SPACE_ANONYMOUS_PERMISSIONS)) {
            List<SpaceAnonymousCSVBean> beans = maybeCheckStatus.map(this::createSpaceAnonymousCSVBeans).orElse(Collections.emptyList());
            return new SpaceAnonymousCSVContainer(beans);
        }
        if (checkType.equals(CheckType.GLOBAL_DATA_TEMPLATE)) {
            List<GlobalDataTemplateCSVBean> beans = maybeCheckStatus.map(this::createGlobalDataTemplateCSVBeans).orElse(Collections.emptyList());
            return new GlobalDataTemplateCSVContainer(beans);
        }
        List<MissingAttachmentCSVBean> beans = maybeCheckStatus.map(this::createMissingAttachmentCSVBeans).orElse(Collections.emptyList());
        return new MissingAttachmentCSVContainer(beans);
    }

    public Optional<AppVendorCheckCSVContainer> createAppVendorCheckResultCSVContainer(String executionId, String appKey, String checkId) {
        Optional<CheckStatus> maybeCheckStatus = this.getCheckStatus(executionId, CheckType.APP_VENDOR_CHECK);
        return maybeCheckStatus.map(checkStatus -> {
            Optional<SerializableCsvFileContentDto> maybeContent = this.getAppVendorCheckCsvContent((CheckStatus)checkStatus, appKey, checkId);
            return maybeContent.map(content -> {
                String[] headers = (String[])content.columnHeaders.stream().toArray(String[]::new);
                return Optional.of(new AppVendorCheckCSVContainer(headers, content.rows));
            }).orElse(Optional.empty());
        }).orElse(Optional.empty());
    }

    private Optional<CheckStatus> getCheckStatus(String executionId, CheckType checkType) {
        return this.checkExecutor.getStatus(executionId).flatMap(ces -> this.flatMapCheckStatus(checkType, (CheckExecutionStatus)ces));
    }

    private Optional<CheckStatus> flatMapCheckStatus(CheckType checkType, CheckExecutionStatus ces) {
        return ces.statuses.stream().filter(checkStatus -> checkStatus.checkType.equals(checkType.value())).findAny();
    }

    private List<MissingAttachmentCSVBean> createMissingAttachmentCSVBeans(CheckStatus checkStatus) {
        return this.checkResultService.retrieveStoredViolations(checkStatus.checkResult, new TypeReference<List<MissingAttachmentDto>>(){}).stream().map(MissingAttachmentCSVBean::new).collect(Collectors.toList());
    }

    private List<SpaceAnonymousCSVBean> createSpaceAnonymousCSVBeans(CheckStatus checkStatus) {
        return this.checkResultService.retrieveStoredViolations(checkStatus.checkResult, new TypeReference<List<String>>(){}).stream().map(SpaceAnonymousCSVBean::new).collect(Collectors.toList());
    }

    private List<GlobalDataTemplateCSVBean> createGlobalDataTemplateCSVBeans(CheckStatus checkStatus) {
        return this.checkResultService.retrieveStoredViolations(checkStatus.checkResult, new TypeReference<List<NonSpaceTemplateConflictsInfo.Conflict>>(){}).stream().map(GlobalDataTemplateCSVBean::new).collect(Collectors.toList());
    }

    public void executeChecksForApp(String executionId, PlanDto planDto, CheckType checkType, String serverAppKey) {
        log.info("Execute checks execution id: {} type: {} for app: {}", new Object[]{executionId, checkType, serverAppKey});
        HashSet<String> updatedAppList = new HashSet<String>(Collections.singletonList(serverAppKey));
        planDto.getTasks().stream().filter(MigrateAppsTaskDto.class::isInstance).findFirst().ifPresent(task -> ((MigrateAppsTaskDto)task).setNeededInCloudApps(updatedAppList));
        List<CheckRequest> checkRequests = this.checkTransformerService.getCheckRequests(planDto, Collections.singleton(checkType), executionId);
        this.checkExecutor.executeChecks(executionId, checkRequests);
        this.checkOverrideService.createOverrides(executionId, planDto.getPreflightChecksToOverride());
        String scheduledChecks = checkRequests.stream().map(ch -> ch.checkType).collect(Collectors.joining(", "));
        log.info("Execution Id: {} scheduled checks {} for app {}", new Object[]{executionId, scheduledChecks, serverAppKey});
    }

    public void executeAllAppVendorChecksForApp(String planId, String serverAppKey) {
        PlanDto planDto = this.planService.getPlan(planId);
        this.executeChecksForApp(planId, planDto, CheckType.APP_VENDOR_CHECK, serverAppKey);
    }

    private Optional<SerializableCsvFileContentDto> getAppVendorCheckCsvContent(CheckStatus checkStatus, String appKey, String checkId) {
        Map checkIdMap = (Map)checkStatus.checkResult.details.get(appKey);
        if (checkIdMap == null) {
            log.warn("Unable to generate AppVendorCheck csv map for non-existent appKey: {}", (Object)appKey);
            return Optional.empty();
        }
        AppVendorCheckResultDto appCheckResult = (AppVendorCheckResultDto)checkIdMap.get(checkId);
        if (appCheckResult == null) {
            log.warn("Unable to generate AppVendorCheck csv map for appKey: {} with non-existent checkId: {}", (Object)appKey, (Object)checkId);
            return Optional.empty();
        }
        return Optional.of(appCheckResult.csvContent);
    }
}

