/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckRequest
 *  com.atlassian.cmpt.check.base.CheckStatus
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.function.TriFunction
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.base.CheckRequest;
import com.atlassian.cmpt.check.base.CheckStatus;
import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.dto.MigrateGlobalEntitiesTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.SpaceTaskDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.dto.util.PlanDtoUtil;
import com.atlassian.migration.agent.entity.AppAssessmentInfo;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.app.AppAssessmentInfoService;
import com.atlassian.migration.agent.service.check.CheckRegistry;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictChecker;
import com.atlassian.migration.agent.service.version.ConfluenceServerVersion;
import com.atlassian.migration.app.DefaultRegistrar;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class CheckTransformerService {
    private static final String CLOUD_ID_KEY = "cloudId";
    private static final String SPACES_KEY = "spaceKeys";
    private static final String EXCLUDED_APPS_KEY = "excludedAppKeys";
    private static final String EXECUTION_ID = "executionId";
    private static final String APPS_KEY = "appsKey";
    private static final String PLAN_ID_KEY = "planId";
    private static final String PLAN_NAME_KEY = "planName";
    private static final String PLAN_MIGRATION_TAG_KEY = "planMigrationTag";
    private final CheckRegistry checkerRegistry;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final AppAssessmentInfoService appAssessmentInfoService;
    private final SystemInformationService systemInformationService;
    private final MigrationAgentConfiguration configuration;
    private final DefaultRegistrar defaultRegistrar;
    private final TeamCalendarHelper teamCalendarHelper;
    private final Set<CheckType> appMigrationCheckList = new HashSet<CheckType>(Arrays.asList(CheckType.APP_ASSESSMENT_COMPLETE, CheckType.APP_DATA_MIGRATION_CONSENT, CheckType.APPS_NOT_INSTALLED_ON_CLOUD, CheckType.SERVER_APPS_OUTDATED, CheckType.APP_RELIABILITY, CheckType.APP_WEBHOOK_ENDPOINT_CHECK, CheckType.APP_LICENSE_CHECK));
    private Map<CheckType, TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>> checkBuilderMap;
    private static final Logger log = ContextLoggerFactory.getLogger(CheckTransformerService.class);

    public CheckTransformerService(CheckRegistry checkRegistry, MigrationDarkFeaturesManager darkFeaturesManager, AppAssessmentInfoService appAssessmentInfoService, SystemInformationService systemInformationService, MigrationAgentConfiguration migrationAgentConfiguration, DefaultRegistrar defaultRegistrar, TeamCalendarHelper teamCalendarHelper) {
        this.checkerRegistry = checkRegistry;
        this.darkFeaturesManager = darkFeaturesManager;
        this.appAssessmentInfoService = appAssessmentInfoService;
        this.systemInformationService = systemInformationService;
        this.configuration = migrationAgentConfiguration;
        this.defaultRegistrar = defaultRegistrar;
        this.teamCalendarHelper = teamCalendarHelper;
        this.initializeCheckBuilderMap();
    }

    public List<CheckRequest> getCheckRequests(PlanDto planDto) {
        return this.getCheckRequests(planDto, Collections.emptySet(), planDto.getId());
    }

    public List<CheckRequest> getCheckRequests(PlanDto planDto, Set<CheckType> requestedChecks, String executionId) {
        Set<CheckType> checksToPerform = CollectionUtils.isEmpty(requestedChecks) ? CheckType.getStaticCheckTypes() : requestedChecks;
        List checkRequests = checksToPerform.stream().map(checkType -> (Optional)this.resolveCheckRequest((CheckType)checkType).apply((Object)planDto, checkType, (Object)executionId)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));
        return checkRequests;
    }

    public List<CheckResultDto> toCheckResultDtos(List<CheckStatus> statuses) {
        List results = statuses.stream().map(this::convertToCheckResultDto).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toCollection(ArrayList::new));
        return results;
    }

    private Optional<CheckResultDto> convertToCheckResultDto(CheckStatus checkStatus) {
        CheckType checkType = CheckType.fromString(checkStatus.checkType);
        return checkType == CheckType.UNKNOWN_CHECK_TYPE ? Optional.empty() : Optional.ofNullable(this.checkerRegistry.getResultMapper(checkType).map(checkStatus));
    }

    private void initializeCheckBuilderMap() {
        this.checkBuilderMap = new HashMap<CheckType, TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>>();
        this.checkBuilderMap.put(CheckType.CONFLUENCE_SUPPORTED_VERSION, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildUnsupportedConfluenceCheck));
        this.checkBuilderMap.put(CheckType.APP_OUTDATED, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildAppOutdatedCheck));
        this.checkBuilderMap.put(CheckType.GROUP_NAMES_CONFLICT, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildGroupNamesCheck));
        this.checkBuilderMap.put(CheckType.INVALID_EMAILS, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildEmailsCheck));
        this.checkBuilderMap.put(CheckType.SHARED_EMAILS, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildSharedEmailsCheck));
        this.checkBuilderMap.put(CheckType.CLOUD_FREE_USERS_CONFLICT, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildCloudFreeCheck));
        this.checkBuilderMap.put(CheckType.SPACE_KEYS_CONFLICT, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildSpaceKeysCheck));
        this.checkBuilderMap.put(CheckType.SPACE_ANONYMOUS_PERMISSIONS, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildSpaceAnonymousCheck));
        this.checkBuilderMap.put(CheckType.MISSING_ATTACHMENTS, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildSpaceKeysCheckForMissingAttachments));
        this.checkBuilderMap.put(CheckType.TRUSTED_DOMAINS, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildTrustedDomainCheck));
        this.checkBuilderMap.put(CheckType.APP_VENDOR_CHECK, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildAppVendorCheck));
        this.checkBuilderMap.put(CheckType.TEAM_CALENDARS_APP_VERSION, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildTcAppVersionCheck));
        this.checkBuilderMap.put(CheckType.CONTAINER_TOKEN_EXPIRATION, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildContainerTokenExpirationCheck));
        this.checkBuilderMap.put(CheckType.UNKNOWN_CHECK_TYPE, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildUnknownCheckType));
        this.checkBuilderMap.put(CheckType.GLOBAL_DATA_TEMPLATE, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildGlobalDataTemplatesCheck));
        this.checkBuilderMap.put(CheckType.NETWORK_HEALTH, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildNetworkHealthCheck));
        this.checkBuilderMap.put(CheckType.MIGRATION_ORCHESTRATOR_MAINTENANCE, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildMigrationOrchestratorCheck));
        this.checkBuilderMap.put(CheckType.CLOUD_PREMIUM_EDITION, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildCloudPremiumEditionCheck));
        this.appMigrationCheckList.forEach(check -> this.checkBuilderMap.put((CheckType)check, (TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>>)((TriFunction)this::buildAppMigrationCheck)));
    }

    private Optional<CheckRequest> buildSharedEmailsCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.shouldSkipEmailCheck(planDto)) {
            return Optional.empty();
        }
        Map<String, Object> parameters = this.createParametersForCheckRequest(planDto);
        parameters.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
        return Optional.of(new CheckRequest(checkType.value(), parameters));
    }

    private Optional<CheckRequest> buildEmailsCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.shouldSkipEmailCheck(planDto)) {
            return Optional.empty();
        }
        Map<String, Object> parameters = this.createParametersForCheckRequest(planDto);
        parameters.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
        parameters.put(EXECUTION_ID, executionId);
        return Optional.of(new CheckRequest(checkType.value(), parameters));
    }

    private boolean shouldSkipEmailCheck(PlanDto planDto) {
        if (this.isAttachmentOnly(planDto)) {
            return true;
        }
        return !PlanDtoUtil.containsUsersGroupsTask(planDto);
    }

    private TriFunction<PlanDto, CheckType, String, Optional<CheckRequest>> resolveCheckRequest(CheckType checkType) {
        if (this.appMigrationCheckList.contains(checkType) && this.darkFeaturesManager.appMigrationDevMode()) {
            return (planDto, type, executionId) -> Optional.empty();
        }
        if (this.checkBuilderMap.containsKey(checkType)) {
            return this.checkBuilderMap.get(checkType);
        }
        throw new IllegalArgumentException(String.format("Check type %s is not supported yet", checkType));
    }

    private Optional<CheckRequest> buildUnsupportedConfluenceCheck(PlanDto planDto, CheckType checkType, String executionId) {
        return Optional.of(new CheckRequest(checkType.value(), Collections.emptyMap()));
    }

    private Optional<CheckRequest> buildAppOutdatedCheck(PlanDto planDto, CheckType checkType, String executionId) {
        return Optional.of(new CheckRequest(checkType.value(), Collections.singletonMap(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId())));
    }

    private Optional<CheckRequest> buildTrustedDomainCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.isAttachmentOnly(planDto)) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), Collections.emptyMap()));
    }

    private Optional<CheckRequest> buildGroupNamesCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.isAttachmentOnly(planDto)) {
            return Optional.empty();
        }
        if (!PlanDtoUtil.containsUsersGroupsTask(planDto)) {
            return Optional.empty();
        }
        Map<String, Object> parameters = this.createParametersForCheckRequest(planDto);
        parameters.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
        return Optional.of(new CheckRequest(checkType.value(), parameters));
    }

    private boolean isAttachmentOnly(PlanDto planDto) {
        return PlanDtoUtil.isAttachmentOnlyPlan(planDto);
    }

    private Optional<CheckRequest> buildSpaceKeysCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (!PlanDtoUtil.hasConfluenceSpaceTask(planDto)) {
            return Optional.empty();
        }
        if (this.isAttachmentOnly(planDto)) {
            return Optional.empty();
        }
        return this.getCheckRequest(planDto, checkType);
    }

    private Optional<CheckRequest> buildCloudPremiumEditionCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (!this.teamCalendarHelper.includeTeamCalendar()) {
            return Optional.empty();
        }
        if (this.isAttachmentOnly(planDto) || !PlanDtoUtil.hasConfluenceSpaceTask(planDto)) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), Collections.singletonMap(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId())));
    }

    private Optional<CheckRequest> buildSpaceKeysCheckForMissingAttachments(PlanDto planDto, CheckType checkType, String executionId) {
        String commaSeparatedSpaceKeys = this.getCommaSeparatedSpaceKeys(planDto);
        if (commaSeparatedSpaceKeys.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), (Map)ImmutableMap.of((Object)SPACES_KEY, (Object)commaSeparatedSpaceKeys)));
    }

    private Optional<CheckRequest> buildSpaceAnonymousCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (!PlanDtoUtil.hasConfluenceSpaceTask(planDto) || this.isAttachmentOnly(planDto)) {
            return Optional.empty();
        }
        if (!PlanDtoUtil.containsUsersGroupsTask(planDto) && !PlanDtoUtil.hasConfluenceSpaceTask(planDto)) {
            return Optional.empty();
        }
        return this.getCheckRequest(planDto, checkType);
    }

    private Optional<CheckRequest> buildContainerTokenExpirationCheck(PlanDto planDto, CheckType checkType, String executionId) {
        return Optional.of(new CheckRequest(checkType.value(), Collections.singletonMap(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId())));
    }

    private Optional<CheckRequest> buildUnknownCheckType(PlanDto planDto, CheckType checkType, String s) {
        return Optional.empty();
    }

    private Optional<CheckRequest> getCheckRequest(PlanDto planDto, CheckType checkType) {
        String commaSeparatedSpaceKeys = this.getCommaSeparatedSpaceKeys(planDto);
        if (commaSeparatedSpaceKeys.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), (Map)ImmutableMap.of((Object)CLOUD_ID_KEY, (Object)planDto.getCloudSite().getCloudId(), (Object)SPACES_KEY, (Object)commaSeparatedSpaceKeys)));
    }

    private Map<String, Object> createParametersForCheckRequest(PlanDto planDto) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        if (PlanDtoUtil.hasScopedUserTask(planDto)) {
            String commaSeparatedSpaceKeys = this.getCommaSeparatedSpaceKeys(planDto);
            parameters.put(SPACES_KEY, commaSeparatedSpaceKeys);
            Optional<GlobalEntityType> globalEntityType = this.getGlobalEntityTaskType(planDto);
            globalEntityType.ifPresent(entityType -> parameters.put("templateTypes", entityType.toString()));
        }
        return parameters;
    }

    private Optional<GlobalEntityType> getGlobalEntityTaskType(PlanDto planDto) {
        Optional<MigrateGlobalEntitiesTaskDto> globalEntityTaskDto = planDto.getTasks().stream().filter(MigrateGlobalEntitiesTaskDto.class::isInstance).map(x -> (MigrateGlobalEntitiesTaskDto)x).findFirst();
        if (!globalEntityTaskDto.isPresent() || !GlobalDataTemplateConflictChecker.isApplicable(globalEntityTaskDto.get().getGlobalEntityType())) {
            return Optional.empty();
        }
        return Optional.of(globalEntityTaskDto.get().getGlobalEntityType());
    }

    private String getCommaSeparatedSpaceKeys(PlanDto planDto) {
        return planDto.getTasks().stream().filter(task -> task instanceof SpaceTaskDto).map(taskDto -> ((SpaceTaskDto)((Object)taskDto)).getSpace()).collect(Collectors.joining(","));
    }

    private Optional<CheckRequest> buildCloudFreeCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.darkFeaturesManager.isLicenceCheckDisabled()) {
            return Optional.empty();
        }
        if (this.isAttachmentOnly(planDto)) {
            return Optional.empty();
        }
        if (!PlanDtoUtil.containsUsersGroupsTask(planDto)) {
            return Optional.empty();
        }
        Map<String, Object> params = this.createParametersForCheckRequest(planDto);
        params.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
        params.put(EXECUTION_ID, executionId);
        return Optional.of(new CheckRequest(checkType.value(), params));
    }

    private Optional<CheckRequest> buildAppMigrationCheck(PlanDto planDto, CheckType checkType, String executionId) {
        Optional<TaskDto> maybeAppsTaskDto = planDto.getTasks().stream().filter(MigrateAppsTaskDto.class::isInstance).findFirst();
        if (maybeAppsTaskDto.isPresent()) {
            MigrateAppsTaskDto migrateAppsTaskDto = (MigrateAppsTaskDto)maybeAppsTaskDto.get();
            Map<String, Object> parameters = this.createParametersForCheckRequest(planDto);
            String excludedAppKeysJoined = migrateAppsTaskDto.getExcludedAppKeysJoined();
            parameters.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
            parameters.put(EXCLUDED_APPS_KEY, excludedAppKeysJoined);
            List<AppAssessmentInfo> appsNeededInCloud = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud();
            Set<String> excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(excludedAppKeysJoined);
            List appKeys = appsNeededInCloud.stream().map(AppAssessmentInfo::getAppKey).filter(appKey -> !excludedAppKeys.contains(appKey)).collect(Collectors.toList());
            parameters.put(APPS_KEY, String.join((CharSequence)",", appKeys));
            return Optional.of(new CheckRequest(checkType.value(), parameters));
        }
        return Optional.empty();
    }

    private Optional<CheckRequest> buildAppVendorCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.darkFeaturesManager.appVendorCheckDisabled()) {
            log.warn("Skipping to execute App vendor check as feature flag: {} is not enabled.", (Object)"migration-assistant.disable.app-vendor-check");
        } else {
            Optional<TaskDto> maybeAppsTaskDto = planDto.getTasks().stream().filter(MigrateAppsTaskDto.class::isInstance).findFirst();
            if (maybeAppsTaskDto.isPresent() || this.darkFeaturesManager.appMigrationDevMode()) {
                Set<Object> excludedAppKeys;
                Set<Object> neededInCloudApps = new HashSet();
                if (maybeAppsTaskDto.isPresent()) {
                    neededInCloudApps = ((MigrateAppsTaskDto)maybeAppsTaskDto.get()).getNeededInCloudApps();
                    excludedAppKeys = MigrateAppsTaskDto.getExcludedAppKeysAsSet(((MigrateAppsTaskDto)maybeAppsTaskDto.get()).getExcludedAppKeysJoined());
                } else {
                    excludedAppKeys = Collections.emptySet();
                }
                Map<String, Object> parameters = this.createParametersForCheckRequest(planDto);
                parameters.put(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId());
                if (planDto.getId() != null) {
                    parameters.put(PLAN_ID_KEY, planDto.getId());
                } else {
                    parameters.put(PLAN_ID_KEY, "");
                }
                parameters.put(PLAN_NAME_KEY, planDto.getName());
                parameters.put(SPACES_KEY, this.getCommaSeparatedSpaceKeys(planDto));
                parameters.put(PLAN_MIGRATION_TAG_KEY, planDto.getMigrationTag().name());
                if (!neededInCloudApps.isEmpty()) {
                    parameters.put(APPS_KEY, String.join((CharSequence)",", neededInCloudApps));
                } else if (this.darkFeaturesManager.appMigrationDevMode()) {
                    parameters.put(APPS_KEY, String.join((CharSequence)",", this.defaultRegistrar.getRegisteredServerKeys()));
                } else {
                    String appKeys = this.appAssessmentInfoService.getAppAssessmentInfosNeededInCloud().stream().map(AppAssessmentInfo::getAppKey).filter(appKey -> !excludedAppKeys.contains(appKey)).collect(Collectors.joining(","));
                    parameters.put(APPS_KEY, appKeys);
                }
                return Optional.of(new CheckRequest(checkType.value(), parameters));
            }
        }
        return Optional.empty();
    }

    private Optional<CheckRequest> buildGlobalDataTemplatesCheck(PlanDto planDto, CheckType checkType, String executionId) {
        Optional<MigrateGlobalEntitiesTaskDto> globalEntityTaskDto = planDto.getTasks().stream().filter(MigrateGlobalEntitiesTaskDto.class::isInstance).map(x -> (MigrateGlobalEntitiesTaskDto)x).findFirst();
        if (!globalEntityTaskDto.isPresent() || !GlobalDataTemplateConflictChecker.isApplicable(globalEntityTaskDto.get().getGlobalEntityType())) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), (Map)ImmutableMap.of((Object)CLOUD_ID_KEY, (Object)planDto.getCloudSite().getCloudId(), (Object)"templateTypes", (Object)globalEntityTaskDto.get().getGlobalEntityType().toString())));
    }

    private Optional<CheckRequest> buildTcAppVersionCheck(PlanDto planDto, CheckType checkType, String executionId) {
        ConfluenceServerVersion confluenceVersion = ConfluenceServerVersion.of(this.systemInformationService.getConfluenceInfo().getVersion());
        String minTCConfluenceSupportedVersion = this.configuration.getMinTCSupportedConfluenceVersion();
        if (!this.teamCalendarHelper.includeTeamCalendar() || confluenceVersion.greaterOrEqual(minTCConfluenceSupportedVersion)) {
            log.warn("Skipping to execute TC App Version check as feature flag: {} is enabled or TC is not present.", (Object)"migration-assistant.disable.team-calendars-migration");
            return Optional.empty();
        }
        if (this.isAttachmentOnly(planDto) || !PlanDtoUtil.hasConfluenceSpaceTask(planDto)) {
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(CheckType.TEAM_CALENDARS_APP_VERSION.value(), Collections.emptyMap()));
    }

    private Optional<CheckRequest> buildNetworkHealthCheck(PlanDto planDto, CheckType checkType, String executionId) {
        if (this.darkFeaturesManager.networkHealthCheckDisabled()) {
            log.warn("Skipping to execute Network Health check as feature flag: {} is enabled.", (Object)"migration-assistant.disable.network-health-check");
            return Optional.empty();
        }
        return Optional.of(new CheckRequest(checkType.value(), Collections.singletonMap(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId())));
    }

    public Optional<CheckRequest> buildMigrationOrchestratorCheck(PlanDto planDto, CheckType checkType, String executionId) {
        return Optional.of(new CheckRequest(checkType.value(), Collections.singletonMap(CLOUD_ID_KEY, planDto.getCloudSite().getCloudId())));
    }
}

