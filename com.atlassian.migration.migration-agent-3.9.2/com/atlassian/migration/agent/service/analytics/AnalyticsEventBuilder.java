/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.CounterMetricEvent$Builder
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.cmpt.analytics.events.GenericOperationalEvent
 *  com.atlassian.cmpt.analytics.events.GenericOperationalEvent$Builder
 *  com.atlassian.cmpt.analytics.events.GenericScreenEvent$Builder
 *  com.atlassian.cmpt.analytics.events.GenericTrackEvent$Builder
 *  com.atlassian.cmpt.analytics.events.GenericUiEvent$Builder
 *  com.atlassian.cmpt.analytics.events.TimerMetricEvent$Builder
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.email.EmailData
 *  com.atlassian.cmpt.check.email.EmailDuplicate
 *  com.atlassian.confluence.cluster.ClusterInformation
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  com.atlassian.confluence.status.service.systeminfo.UsageInfo
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent
 *  com.atlassian.migration.app.dto.check.CheckDetail
 *  com.atlassian.migration.app.dto.check.CheckStatus
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.cmpt.analytics.events.CounterMetricEvent;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.cmpt.analytics.events.GenericOperationalEvent;
import com.atlassian.cmpt.analytics.events.GenericScreenEvent;
import com.atlassian.cmpt.analytics.events.GenericTrackEvent;
import com.atlassian.cmpt.analytics.events.GenericUiEvent;
import com.atlassian.cmpt.analytics.events.TimerMetricEvent;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.cmpt.check.email.EmailDuplicate;
import com.atlassian.confluence.cluster.ClusterInformation;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.AppListenerIssueType;
import com.atlassian.migration.agent.dto.ConfluenceSpaceTaskDto;
import com.atlassian.migration.agent.dto.MigrateGlobalEntitiesTaskDto;
import com.atlassian.migration.agent.dto.MigrateUsersTaskDto;
import com.atlassian.migration.agent.dto.MigrationDetailsDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.analytics.ScreenAnalyticsEventDto;
import com.atlassian.migration.agent.dto.analytics.TrackAnalyticsEventDto;
import com.atlassian.migration.agent.dto.analytics.UIAnalyticsEventDto;
import com.atlassian.migration.agent.dto.util.PlanDtoUtil;
import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.ExportType;
import com.atlassian.migration.agent.entity.ImportType;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.UploadDestinationType;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.mapi.MigrationCreator;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.GlobalEntitiesStats;
import com.atlassian.migration.agent.model.stats.InstanceStats;
import com.atlassian.migration.agent.model.stats.ServerStats;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.model.stats.UsersGroupsStats;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.MigrationMetric;
import com.atlassian.migration.agent.service.NetworkStatisticsService;
import com.atlassian.migration.agent.service.SpaceAttachmentCount;
import com.atlassian.migration.agent.service.SpaceConflict;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.UploadState;
import com.atlassian.migration.agent.service.analytics.AnalyticsMessageHandler;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.analytics.FeatureFlagActionSubject;
import com.atlassian.migration.agent.service.analytics.ProgressStatus;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.network.NetworkCheckResult;
import com.atlassian.migration.agent.service.encryption.EncryptionConfigHandler;
import com.atlassian.migration.agent.service.event.StepAllocation;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.app.analytics.MultPartUploadAnalyticEvent;
import com.atlassian.migration.app.dto.check.CheckDetail;
import com.atlassian.migration.app.dto.check.CheckStatus;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class AnalyticsEventBuilder {
    private static final EnumSet<DirectoryType> LDAP_DIRECTORY_TYPES = EnumSet.of(DirectoryType.CONNECTOR, DirectoryType.DELEGATING, DirectoryType.CUSTOM);
    private static final String CONTAINER_TYPE_KEY = "containerType";
    private static final String CONFLUENCE_SPACE = "CONFLUENCE_SPACE";
    private static final String GLOBAL_ENTITIES = "GLOBAL_ENTITIES";
    private static final String COMPONENT_KEY = "component";
    private static final String PHASE_KEY = "phase";
    private static final String OPERATION_KEY = "operationKey";
    private static final String SPACE_COMPONENT = "Space";
    private static final String GLOBAL_ENTITIES_COMPONENT = "GlobalEntities";
    private static final String STEP_SUCCESSFUL = "stepSuccessful";
    private static final String IMPORT = "Import";
    private static final String EXPORT = "Export";
    private static final Logger log = ContextLoggerFactory.getLogger(AnalyticsEventBuilder.class);
    private static final Set<String> KEYS = new HashSet<String>(Arrays.asList("containerType", "component", "phase", "operationKey"));
    private static final Map<String, String> mediaUploadEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"CONFLUENCE_SPACE", (Object)"component", (Object)"Media", (Object)"phase", (Object)"Upload", (Object)"operationKey", (Object)"confluenceAttachmentUpload");
    private static final Map<String, String> spaceImportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"CONFLUENCE_SPACE", (Object)"component", (Object)"Space", (Object)"phase", (Object)"Import", (Object)"operationKey", (Object)"confluenceImport");
    private static final Map<String, String> spaceExportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"CONFLUENCE_SPACE", (Object)"component", (Object)"Space", (Object)"phase", (Object)"Export", (Object)"operationKey", (Object)"confluenceSpaceUpload");
    private static final Map<String, String> userImportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"SITE", (Object)"component", (Object)"User", (Object)"phase", (Object)"Import", (Object)"operationKey", (Object)"allUserImport");
    private static final Map<String, String> userExportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"SITE", (Object)"component", (Object)"User", (Object)"phase", (Object)"Export", (Object)"operationKey", (Object)"allUserExport");
    private static final Map<String, String> globalEntitiesExportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"GLOBAL_ENTITIES", (Object)"component", (Object)"GlobalEntities", (Object)"phase", (Object)"Export", (Object)"operationKey", (Object)"globalEntitiesUpload");
    private static final Map<String, String> globalEntitiesImportEventAttributesMap = ImmutableMap.of((Object)"containerType", (Object)"GLOBAL_ENTITIES", (Object)"component", (Object)"GlobalEntities", (Object)"phase", (Object)"Import", (Object)"operationKey", (Object)"globalEntitiesImport");
    private static final Map<String, Map<String, String>> stepCompletionEventAttributesMap = AnalyticsEventBuilder.populateStepCompletionEventAttributesMap();
    private static final String STOPPED = "stopped";
    private static final String RESULT = "result";
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String PRODUCT = "product";
    private static final String VERSION = "version";
    private static final String IS_INTERNAL_SEN = "isInternalSen";
    private static final String PLUGIN_VERSION = "pluginVersion";
    private static final String IS_INTERNAL_CONTEXT = "isInternalContext";
    private static final String REASON = "reason";
    private static final String CONFIGURED_HEAP_SIZE = "configuredHeapSize";
    private static final String FREE_HEAP_SIZE = "freeHeapSize";
    private static final String FREE_HEAP_SIZE_AT_START = "freeHeapSizeAtStart";
    private static final String USED_HEAP_SIZE = "usedHeapSize";
    private static final String ERROR_CODES = "errorCodes";
    private static final String ESTIMATED_TIME = "estimatedTime";
    private static final String TOTAL_ATTACHMENTS_SIZE = "totalAttachmentsSize";
    private static final String STATUS = "status";
    private static final String EXECUTED = "executed";
    private static final String CHECK_SCREEN = "checkScreen";
    private static final String UDC2 = "user-data-compatibility-plugin-2";
    private static final String USER_BASE_SCAN = "userBaseScan";
    private static final String NEW_EMAIL_SUGGESTING = "newEmailSuggesting";
    private static final String NUMBER_OF_APPS = "numberOfApps";
    private static final String NUMBER_OF_SPACES = "numberOfSpaces";
    private static final String NUMBER_OF_TEMPLATES = "numberOfTemplates";
    private static final String MIGRATION = "migration";
    private static final String MIGRATION_ID = "migrationId";
    private static final String MIGRATION_TAG = "migrationTag";
    private static final String MIGRATION_CREATOR = "migrationCreator";
    private static final String TENANT_ID = "tenantId";
    private static final String TIME_TAKEN = "timeTaken";
    private static final String SPACE_EXPORT_TYPE = "spaceExportType";
    private static final String SPACE_IMPORT_TYPE = "spaceImportType";
    private static final String SPACE_EXPORT_ACTION_NAME = "exportedAndPostProcessed";
    private static final String ATTACHMENT_UPLOAD_ACTION_NAME = "attachmentsUploaded";
    private static final String LOGGED_ACTION_NAME = "logged";
    private static final String SPACE_IMPORTED_ACTION_NAME = "spaceImported";
    private static final String EXPORT_POST_PROCESSED_ACTION_NAME = "postProcessed";
    private static final String SPACE_UPLOADED_ACTION_NAME = "spaceUploaded";
    private static final String GLOBAL_ENTITIES_EXPORT_ACTION_NAME = "globalEntitiesExported";
    private static final String GLOBAL_ENTITIES_CSV_EXPORT_ACTION_NAME = "globalEntitiesCSVExported";
    private static final String GLOBAL_ENTITIES_IMPORT_ACTION_NAME = "globalEntitiesImported";
    private static final String GLOBAL_ENTITIES_UPLOAD_ACTION_NAME = "globalEntitiesUploaded";
    private static final String MCS_FILE_UPLOADED_ACTION_NAME = "fileUploadedToMCS";
    private static final String CLOUD_TYPE_SETTINGS_ACTION_NAME = "cloudTypeSettingsUpdated";
    private static final String MCS_GLOBAL_ENTITIES_FILE_UPLOADED_ACTION_NAME = "globalEntitiesFileUploadedToMCS";
    private static final String TABLE_EXPORTED_TO_CSV_ACTION_NAME = "tableExportedToCSV";
    private static final String MAPI_JOB_ACTION_SUBJECT = "mapiJob";
    private static final String FEATURE_FLAG_ACTION_ITEM = "featureFlag";
    private static final String UPLOAD_SIZE_ATTRIBUTE_NAME = "uploadSize";
    private static final String UPLOAD_TYPE_ATTRIBUTE_NAME = "uploadDestinationType";
    private static final String FILE_NAME_ATTRIBUTE_NAME = "fileName";
    private static final String TABLE_NAME_ATTRIBUTE_NAME = "tableName";
    private static final String TIME_TO_FIRST_RECORD_ATTRIBUTE_NAME = "timeToFirstRecord";
    private static final String ROWS_EXPORTED_ATTRIBUTE_NAME = "rowsExported";
    private static final String CHARS_EXPORTED_ATTRIBUTE_NAME = "charactersExported";
    private static final String EXPORT_QUERY_HASH_ATTRIBUTE_NAME = "exportQueryHash";
    private static final String ERROR_CODE_ATTRIBUTE_NAME = "errorCode";
    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String SPACE_UPLOAD_TYPE = "spaceUploadType";
    private static final String DB_TYPE = "dbType";
    private static final String OBJECT_FAILURE_SUBJECT_NAME = "objectFailure";
    private static final String SKIPPED_STEP_SUBJECT_NAME = "skippedStep";
    private static final String FEATURE_FLAG_ATTRIBUTE_NAME = "featureFlagKey";
    private static final String KNOWN_SOURCE = "knownSource";
    private static final String NUMBER_OF_USERS = "numberOfUsers";
    private static final String NUMBER_OF_PAGES = "numberOfPages";
    private static final String NUMBER_OF_BLOGS = "numberOfBlogs";
    private static final String NUMBER_OF_TEAM_CALENDARS = "numberOfTeamCalendars";
    private static final String NUMBER_OF_GROUPS = "numberOfGroups";
    private static final String MAPI_JOB_ID = "mapiJobId";
    private static final String PLAN_ID = "planId";
    private static final String SPACE_ID = "spaceId";
    private static final String TC_EXPORT_TIME = "tcExportTime";
    private static final String NODE_ID = "nodeId";
    private static final String NODE_EXECUTION_ID = "nodeExecutionId";
    private static final String EXECUTION_STATE = "executionState";
    private static final String NODE_HEARTBEAT = "nodeHeartBeat";
    private static final String OLD_STEP_ALLOCATION_NODE_ID = "oldStepAllocationNodeId";
    private static final String OLD_STEP_ALLOCATION_NODE_EXECUTION_ID = "oldStepAllocationNodeExecutionId";
    private static final String USER_DOMAIN_RULE = "userDomainRule";
    private static final String REVIEW_SCREEN = "reviewScreen";
    private static final String UNKNOWN = "Unknown";
    protected static final String IMPORT_TASK_ID = "importTaskId";
    protected static final String STATUS_CODE = "statusCode";
    private static final String SCAN_ID = "scanId";
    protected static final String TASK_ID = "taskId";
    protected static final String START_TIME = "startTime";
    protected static final String STOP_TIME = "stopTime";
    protected static final String EXPORT_TYPE = "exportType";
    protected static final String FILE_ID = "fileId";
    protected static final String STEP_TYPE = "stepType";
    protected static final String STEP_ID = "stepId";
    protected static final String TASK_TYPE = "taskType";
    protected static final String CONFLUENCE = "confluence";
    protected static final String CONFLUENCE_PRODUCT_FAMILY = "CONFLUENCE";
    protected static final String MIGRATION_TYPE = "migrationType";
    protected static final String PRODUCT_FAMILY = "productFamily";
    protected static final String S2C_MIGRATION = "S2C_MIGRATION";
    protected static final String CREATED = "created";
    protected static final String STARTED = "started";
    protected static final String COMPLETED = "completed";
    protected static final String FAILED = "failed";
    protected static final String FINISHED = "finished";
    protected static final String STEP = "step";
    protected static final String PLAN = "plan";
    protected static final String SPACE = "space";
    protected static final String GLOBAL_ENTITY = "globalEntity";
    protected static final String PLATFORM_EVENT = "platformEvent";
    protected static final String NUMBER_OF_FAILED_DOMAINS = "numberOfFailedDomains";
    protected static final String FAILED_DOMAINS_BY_SERVICE_NAME = "failedDomainsByServiceName";
    protected static final String NETWORK_HEALTH_CHECK_EVENT = "networkHealthCheck";
    protected static final String NETWORK_HEALTH_PRE_MIGRATION_CHECK_EVENT = "networkHealthPreMigrationCheck";
    protected static final String RELATIONS_ANALYSER = "relationsAnalyser";
    protected static final String RELATIONS_ANALYSIS_JOB = "relationsAnalysisJob";
    public static final String USER_EXPORT = "USER_EXPORT";
    @VisibleForTesting
    static final String INTERNAL_SEN = "SEN-500";
    public static final String EXECUTOR_CONCURRENCY_CLUSTER = "executorConcurrencyCluster";
    public static final String TC_APP_VERSION = "tcAppVersion";
    public static final String EDITION = "edition";
    public static final String EXECUTOR_CONCURRENCY_NODE = "executorConcurrencyNode";
    public static final String CLOUD_PREMIUM_EDITION_CHECK_EVENT = "cloudPremiumEditionCheck";
    public static final String SPACE_STATISTIC_CALCULATION = "spaceStatisticCalculation";
    public static final String ENCRYPTION_KEY_LOCATION = "encryptionKeyLocation";
    private static final String TOKEN_ENCRYPTION_ACTION_NAME = "tokenEncryption";
    private final SENSupplier senSupplier;
    private final Supplier<Instant> instantSupplier;
    private final StatisticsService statisticsService;
    private final SystemInformationService systemInformationService;
    private final PluginMetadataManager pluginMetadataManager;
    private final CrowdDirectoryService crowdDirectoryService;
    private final SpaceManager spaceManager;
    private final PluginVersionManager pluginVersionManager;
    private final ClusterManager clusterManager;
    private final LicenseHandler licenseHandler;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;
    private final PluginManager pluginManager;
    private final EncryptionConfigHandler encryptionConfigHandler;

    public AnalyticsEventBuilder(SENSupplier senSupplier, StatisticsService statisticsService, SystemInformationService systemInformationService, PluginMetadataManager pluginMetadataManager, CrowdDirectoryService crowdDirectoryService, SpaceManager spaceManager, PluginVersionManager pluginVersionManager, ClusterManager clusterManager, LicenseHandler licenseHandler, MigrationAgentConfiguration migrationAgentConfiguration, MigrationTimeEstimationUtils migrationTimeEstimationUtils, PluginManager pluginManager, EncryptionConfigHandler encryptionConfigHandler) {
        this(senSupplier, Instant::now, statisticsService, systemInformationService, pluginMetadataManager, crowdDirectoryService, spaceManager, pluginVersionManager, clusterManager, licenseHandler, migrationAgentConfiguration, migrationTimeEstimationUtils, pluginManager, encryptionConfigHandler);
    }

    @VisibleForTesting
    AnalyticsEventBuilder(SENSupplier senSupplier, Supplier<Instant> instantSupplier, StatisticsService statisticsService, SystemInformationService systemInformationService, PluginMetadataManager pluginMetadataManager, CrowdDirectoryService crowdDirectoryService, SpaceManager spaceManager, PluginVersionManager pluginVersionManager, ClusterManager clusterManager, LicenseHandler licenseHandler, MigrationAgentConfiguration migrationAgentConfiguration, MigrationTimeEstimationUtils migrationTimeEstimationUtils, PluginManager pluginManager, EncryptionConfigHandler encryptionConfigHandler) {
        this.senSupplier = senSupplier;
        this.instantSupplier = instantSupplier;
        this.statisticsService = statisticsService;
        this.systemInformationService = systemInformationService;
        this.pluginMetadataManager = pluginMetadataManager;
        this.crowdDirectoryService = crowdDirectoryService;
        this.spaceManager = spaceManager;
        this.pluginVersionManager = pluginVersionManager;
        this.clusterManager = clusterManager;
        this.licenseHandler = licenseHandler;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
        this.pluginManager = pluginManager;
        this.encryptionConfigHandler = encryptionConfigHandler;
    }

    private static Map<String, Map<String, String>> populateStepCompletionEventAttributesMap() {
        HashMap<String, Map<String, String>> stepCompletionEventAttributesMap = new HashMap<String, Map<String, String>>();
        stepCompletionEventAttributesMap.put(StepType.ATTACHMENT_UPLOAD.name(), mediaUploadEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.DATA_UPLOAD.name(), spaceExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.CONFLUENCE_IMPORT.name(), spaceImportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.SPACE_USERS_MIGRATION.name(), spaceExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.CONFLUENCE_EXPORT.name(), spaceExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.USERS_MIGRATION.name(), userImportEventAttributesMap);
        stepCompletionEventAttributesMap.put(USER_EXPORT, userExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.GLOBAL_ENTITIES_EXPORT.name(), globalEntitiesExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.GLOBAL_ENTITIES_DATA_UPLOAD.name(), globalEntitiesExportEventAttributesMap);
        stepCompletionEventAttributesMap.put(StepType.GLOBAL_ENTITIES_IMPORT.name(), globalEntitiesImportEventAttributesMap);
        return stepCompletionEventAttributesMap;
    }

    public EventDto buildUIAnalyticsEvent(UIAnalyticsEventDto eventDto, ConfluenceUser confluenceUser) {
        return ((GenericUiEvent.Builder)((GenericUiEvent.Builder)((GenericUiEvent.Builder)((GenericUiEvent.Builder)((GenericUiEvent.Builder)((GenericUiEvent.Builder)((GenericUiEvent.Builder)new GenericUiEvent.Builder(eventDto.getTimestamp()).email(confluenceUser == null ? null : confluenceUser.getEmail())).sen(this.senSupplier.get())).source(eventDto.getSource())).contextContainer(eventDto.getContainerType(), eventDto.getContainerId())).actionSubject(eventDto.getActionSubject(), eventDto.getActionSubjectId())).action(eventDto.getAction())).withAttributes(this.addCommonFrontendAttributes(eventDto.getAttributes()))).build();
    }

    public EventDto buildScreenAnalyticsEvent(ScreenAnalyticsEventDto eventDto, ConfluenceUser confluenceUser) {
        return ((GenericScreenEvent.Builder)((GenericScreenEvent.Builder)((GenericScreenEvent.Builder)((GenericScreenEvent.Builder)new GenericScreenEvent.Builder(eventDto.getTimestamp()).name(eventDto.getName())).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).withAttributes(this.addCommonFrontendAttributes(eventDto.getAttributes()))).build();
    }

    public EventDto buildCompletedInstanceAnalysisAnalyticsEvent(ServerStats serverStats, ConfluenceUser confluenceUser) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        attributes.put((Object)"database", (Object)ImmutableMap.of((Object)TYPE_ATTRIBUTE_NAME, (Object)databaseInfo.getName(), (Object)VERSION, (Object)databaseInfo.getVersion()));
        long configuredHeapSize = Runtime.getRuntime().maxMemory();
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        attributes.put((Object)"apps", (Object)confluenceInfo.getEnabledPlugins().stream().filter(arg_0 -> ((PluginMetadataManager)this.pluginMetadataManager).isUserInstalled(arg_0)).map(Plugin::getKey).collect(Collectors.toCollection(LinkedList::new)));
        List directories = this.crowdDirectoryService.findAllDirectories();
        boolean ldap = directories.stream().filter(Directory::isActive).map(Directory::getType).anyMatch(LDAP_DIRECTORY_TYPES::contains);
        attributes.put((Object)"LDAP", (Object)ldap);
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(serverStats.getTotalSpaceMigrationTime().plus(serverStats.getTotalUserGroupMigrationTime()).plus(MigrationTimeEstimationUtils.getBaseMigrationTime()).getSeconds()));
        attributes.put((Object)"bandwidthKBS", (Object)(serverStats.getBandwidthKBS() == NetworkStatisticsService.getUncalculatedBandwidthKBPS() ? "" : Long.toString(serverStats.getBandwidthKBS())));
        InstanceStats instanceStats = serverStats.getInstanceStats();
        attributes.put((Object)VERSION, (Object)instanceStats.getVersion());
        attributes.put((Object)NUMBER_OF_SPACES, (Object)Long.toString(instanceStats.getNumberOfSpaces()));
        attributes.put((Object)NUMBER_OF_USERS, (Object)Long.toString(instanceStats.getNumberOfUsers()));
        attributes.put((Object)NUMBER_OF_GROUPS, (Object)Long.toString(instanceStats.getNumberOfGroups()));
        ContentSummary contentSummary = serverStats.getContentSummary();
        attributes.put((Object)TOTAL_ATTACHMENTS_SIZE, (Object)Long.toString(contentSummary.getAttachments().getTotalSize() != null ? contentSummary.getAttachments().getTotalSize() : 0L));
        attributes.put((Object)NUMBER_OF_PAGES, (Object)Long.toString(contentSummary.getNumberOfPages() != null ? contentSummary.getNumberOfPages() : 0L));
        attributes.put((Object)NUMBER_OF_BLOGS, (Object)Long.toString(contentSummary.getNumberOfBlogs() != null ? contentSummary.getNumberOfBlogs() : 0L));
        attributes.put((Object)NUMBER_OF_TEAM_CALENDARS, (Object)Long.toString(contentSummary.getNumberOfTeamCalendars() != null ? contentSummary.getNumberOfTeamCalendars() : 0L));
        ClusterInformation clusterInformation = this.clusterManager.getClusterInformation();
        attributes.put((Object)"clustered", (Object)this.clusterManager.isClustered());
        attributes.put((Object)"numberOfNodes", (Object)Integer.toString(clusterInformation.getMemberCount()));
        attributes.put((Object)CONFIGURED_HEAP_SIZE, (Object)Long.toString(configuredHeapSize));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)ENCRYPTION_KEY_LOCATION, (Object)this.encryptionConfigHandler.getKeyLocationFromConfig());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).email(confluenceUser == null ? null : confluenceUser.getEmail())).sen(this.senSupplier.get())).action(COMPLETED)).actionSubject("instanceAnalysis")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildCompletedPlanAnalyticsEvent(PlanDto planDto) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        long configuredHeapSize = Runtime.getRuntime().maxMemory();
        long freeHeapSize = Runtime.getRuntime().freeMemory();
        attributes.put((Object)STATUS, (Object)planDto.getProgress().getStatus());
        attributes.put((Object)"numberOfObjects", (Object)Long.toString(planDto.getTasks().size()));
        Map<String, Long> taskCounts = this.getTaskCounts(planDto);
        attributes.putAll(taskCounts);
        Duration userGroupMigrationTime = this.getUserGroupMigrationTime(planDto);
        long spaceMigrationTime = PlanDtoUtil.calculateTotalSpaceTaskDuration(planDto);
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(userGroupMigrationTime.plus(MigrationTimeEstimationUtils.getBaseMigrationTime()).getSeconds() + spaceMigrationTime));
        attributes.put((Object)"actualTime", (Object)Long.toString(PlanDtoUtil.totalElapsedTimeInSeconds(planDto)));
        attributes.put((Object)START_TIME, (Object)planDto.getProgress().getStartTime());
        attributes.put((Object)STOP_TIME, (Object)planDto.getProgress().getEndTime());
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)"migrateAllUsers", (Object)(!PlanDtoUtil.hasScopedUserTask(planDto) ? 1 : 0));
        attributes.put((Object)"attachmentsOnly", (Object)PlanDtoUtil.containsAttachmentsOnlyTask(planDto));
        attributes.put((Object)"migrateUserType", (Object)PlanDtoUtil.userMigrationType(planDto).name());
        attributes.put((Object)MIGRATION_ID, (Object)planDto.getMigrationId());
        attributes.put((Object)TENANT_ID, (Object)planDto.getCloudSite().getCloudId());
        attributes.put((Object)CONFIGURED_HEAP_SIZE, (Object)Long.toString(configuredHeapSize));
        attributes.put((Object)FREE_HEAP_SIZE, (Object)Long.toString(freeHeapSize));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).sen(this.senSupplier.get())).actionSubject(PLAN, planDto.getId())).source("monitorScreen")).withAttributes((Map)attributes.build())).build();
    }

    private Map<String, Long> getTaskCounts(PlanDto planDto) {
        return planDto.getTasks().stream().collect(Collectors.groupingBy(task -> String.format("%s%sTaskCount", task.isSuccessful() ? "successful" : FAILED, task.getTaskTypeSingularTitleCase()), Collectors.counting()));
    }

    public EventDto buildPlatformPlanCompletionOperationalEvent(PlanDto planDto, String migrationScopeId, ExecutionStatus status) {
        ImmutableMap.Builder<String, Object> attributes = this.getSourceDestinationLocation(planDto.getCloudSite().getCloudId());
        attributes.put((Object)PRODUCT_FAMILY, (Object)CONFLUENCE_PRODUCT_FAMILY);
        attributes.put((Object)MIGRATION_TYPE, (Object)S2C_MIGRATION);
        attributes.put((Object)PLATFORM_EVENT, (Object)true);
        attributes.put((Object)MIGRATION_ID, (Object)planDto.getMigrationId());
        attributes.put((Object)"migrationScopeId", (Object)migrationScopeId);
        attributes.put((Object)PLAN_ID, (Object)planDto.getId());
        attributes.put((Object)MIGRATION_TAG, (Object)planDto.getMigrationTag());
        attributes.put((Object)MIGRATION_CREATOR, (Object)planDto.getMigrationCreator());
        attributes.put((Object)VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)STATUS, (Object)this.convertToProgressStatus(status).getStatusName());
        this.addConfluenceAndDBVersionInAttributes(attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).actionSubject(MIGRATION, planDto.getMigrationId())).contextContainer(MIGRATION, planDto.getMigrationId())).cloudId(planDto.getCloudSite().getCloudId())).withAttributes((Map)attributes.build())).build();
    }

    @NotNull
    private ImmutableMap.Builder<String, Object> getSourceDestinationLocation(String cloudId) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        HashMap<String, String> sourceLocation = new HashMap<String, String>();
        sourceLocation.put("serverId", this.licenseHandler.getServerId());
        sourceLocation.put("confluenceSen", this.senSupplier.get());
        attributes.put((Object)"sourceLocation", sourceLocation);
        HashMap<String, String> destinationLocation = new HashMap<String, String>();
        destinationLocation.put("cloudId", cloudId);
        attributes.put((Object)"destinationLocation", destinationLocation);
        return attributes;
    }

    public EventDto buildUpdatedPlanStatusAnalyticEvent(PlanDto planDto, ProgressDto.Status fromStatus) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"oldStatus", (Object)fromStatus.name());
        attributes.put((Object)"newStatus", (Object)planDto.getProgress().getStatus().name());
        attributes.put((Object)MIGRATION_ID, (Object)planDto.getMigrationId());
        attributes.put((Object)TENANT_ID, (Object)planDto.getCloudSite().getCloudId());
        List<String> spaceKeys = this.getSpaceKeysByPlan(planDto);
        this.feedSpaceStatsCollectionIfPresent(spaceKeys, spaceStatsCollection -> {
            long totalAttachmentsSize = spaceStatsCollection.stream().mapToLong(spaceStat -> {
                Long totalSize = spaceStat.getSummary().getAttachments().getTotalSize();
                if (totalSize != null) {
                    return totalSize;
                }
                return 0L;
            }).sum();
            attributes.put((Object)TOTAL_ATTACHMENTS_SIZE, (Object)totalAttachmentsSize);
        });
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("updated")).sen(this.senSupplier.get())).actionSubject("planStatus", planDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildStartPlanAnalyticsEvent(PlanDto planDto) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        long configuredHeapSize = Runtime.getRuntime().maxMemory();
        long freeHeapSize = Runtime.getRuntime().freeMemory();
        attributes.put((Object)STATUS, (Object)"RUNNING");
        attributes.put((Object)"numberOfObjects", (Object)Long.toString(planDto.getTasks().size()));
        attributes.put((Object)START_TIME, (Object)planDto.getProgress().getStartTime());
        attributes.put((Object)MIGRATION_ID, (Object)planDto.getMigrationId());
        attributes.put((Object)TENANT_ID, (Object)planDto.getCloudSite().getCloudId());
        UsageInfo usageInfo = this.systemInformationService.getUsageInfo();
        Duration userGroupMigrationTime = MigrationTimeEstimationUtils.estimateTotalUserGroupMigrationTime(usageInfo.getLocalUsers(), usageInfo.getLocalGroups());
        List<String> spaceKeys = this.getSpaceKeysByPlan(planDto);
        this.feedSpaceStatsCollectionIfPresent(spaceKeys, spaceStatsCollection -> {
            long spaceMigrationTime = this.calculateSpaceMigrationTime((Collection<SpaceStats>)spaceStatsCollection);
            attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(userGroupMigrationTime.getSeconds() + spaceMigrationTime));
        });
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)CONFIGURED_HEAP_SIZE, (Object)Long.toString(configuredHeapSize));
        attributes.put((Object)FREE_HEAP_SIZE, (Object)Long.toString(freeHeapSize));
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(STARTED)).sen(this.senSupplier.get())).actionSubject(PLAN, planDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUpdatedTaskStatusAnalyticEvent(String planId, String taskId, String type, ProgressDto.Status fromStatus, ProgressDto.Status toStatus, String spaceKey) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)"oldStatus", (Object)fromStatus.name());
        attributes.put((Object)"newStatus", (Object)toStatus);
        attributes.put((Object)TYPE_ATTRIBUTE_NAME, (Object)type);
        attributes.put((Object)PLAN_ID, (Object)planId);
        if (StringUtils.isNotEmpty((String)spaceKey)) {
            this.feedSpaceIdIfPresent(spaceKey, spaceId -> attributes.put((Object)SPACE_ID, (Object)Long.toString(spaceId)));
            this.feedSpaceStatsIfPresent(spaceKey, spaceStats -> attributes.put((Object)TOTAL_ATTACHMENTS_SIZE, (Object)(spaceStats.getSummary().getAttachments().getTotalSize() != null ? spaceStats.getSummary().getAttachments().getTotalSize() : 0L)));
        }
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("updated")).sen(this.senSupplier.get())).actionSubject("objectStatus", taskId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildStepSkipAnalyticEvent(Step step, String reason) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)STEP_TYPE, (Object)step.getType());
        attributes.put((Object)STEP_ID, (Object)step.getId());
        attributes.put((Object)REASON, (Object)reason);
        attributes.put((Object)MIGRATION_ID, (Object)step.getPlan().getMigrationId());
        attributes.put((Object)TASK_ID, (Object)step.getTask().getId());
        attributes.put((Object)TASK_TYPE, (Object)step.getTask().getAnalyticsEventType());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(LOGGED_ACTION_NAME)).sen(this.senSupplier.get())).actionSubject(SKIPPED_STEP_SUBJECT_NAME)).cloudId(step.getPlan().getCloudSite().getCloudId())).withAttributes((Map)attributes.build())).build();
    }

    public List<EventDto> buildCreatePlanAndTasksAnalyticsEvents(PlanDto planDto, ConfluenceUser confluenceUser, Optional<String> mapiJobId) {
        long now = this.instantSupplier.get().toEpochMilli();
        LongAdder numberOfSpaces = new LongAdder();
        LongAdder estimatedTime = new LongAdder();
        ArrayList<EventDto> analyticsEventModels = new ArrayList<EventDto>();
        String planId = planDto.getId();
        String cloudId = planDto.getCloudSite().getCloudId();
        planDto.getTasks().forEach(task -> {
            if (task instanceof ConfluenceSpaceTaskDto) {
                ConfluenceSpaceTaskDto confluenceSpaceTaskDto = (ConfluenceSpaceTaskDto)task;
                this.feedSpaceStatsIfPresent(confluenceSpaceTaskDto.getSpace(), spaceStats -> {
                    numberOfSpaces.increment();
                    long thisEstimatedTime = this.migrationTimeEstimationUtils.estimateSpaceMigrationTime(spaceStats.getSummary()).getSeconds();
                    estimatedTime.add(thisEstimatedTime);
                    analyticsEventModels.add(this.buildAddedSpaceAnalyticsEvent(now, planId, confluenceSpaceTaskDto, thisEstimatedTime, (SpaceStats)spaceStats, confluenceUser));
                });
            } else if (task instanceof MigrateUsersTaskDto) {
                MigrateUsersTaskDto migrateUsersTaskDto = (MigrateUsersTaskDto)task;
                List<String> spaceKeys = PlanDtoUtil.getSpaceKeysForScope(planDto);
                UserMigrationType userMigrationType = migrateUsersTaskDto.isScoped() ? UserMigrationType.SCOPED : UserMigrationType.ALL;
                UsersGroupsStats usersStats = this.statisticsService.getUsersGroupsStatistics(userMigrationType, spaceKeys);
                estimatedTime.add(usersStats.getTotalMigrationTime().getSeconds());
                analyticsEventModels.add(this.buildAddedUserGroupsAnalyticsEvent(now, planId, migrateUsersTaskDto, usersStats.getTotalMigrationTime().getSeconds(), usersStats, confluenceUser));
            } else if (task instanceof MigrateGlobalEntitiesTaskDto) {
                MigrateGlobalEntitiesTaskDto migrateGlobalEntitiesTaskDto = (MigrateGlobalEntitiesTaskDto)task;
                GlobalEntitiesStats globalEntitiesStats = this.statisticsService.getGlobalEntitiesStatistics(planId);
                estimatedTime.add(migrateGlobalEntitiesTaskDto.getMigrationEstimateSeconds());
                analyticsEventModels.add(this.buildAddedGlobalEntitiesAnalyticsEvent(now, planId, migrateGlobalEntitiesTaskDto, migrateGlobalEntitiesTaskDto.getMigrationEstimateSeconds(), globalEntitiesStats, confluenceUser));
            }
        });
        analyticsEventModels.add(0, this.buildCreatedPlanAnalyticsEvent(now, planDto, numberOfSpaces.longValue(), estimatedTime.longValue(), confluenceUser, mapiJobId));
        return analyticsEventModels;
    }

    EventDto buildCreatedPlanAnalyticsEvent(long now, PlanDto planDto, long numberOfSpaces, long estimatedTime, ConfluenceUser confluenceUser, Optional<String> mapiJobId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)STATUS, (Object)planDto.getProgress().getStatus());
        attributes.put((Object)NUMBER_OF_SPACES, (Object)Long.toString(numberOfSpaces));
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(estimatedTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)MIGRATION_TAG, (Object)planDto.getMigrationTag());
        attributes.put((Object)MIGRATION_CREATOR, (Object)planDto.getMigrationCreator());
        if (mapiJobId.isPresent()) {
            attributes.put((Object)MAPI_JOB_ID, (Object)mapiJobId.get());
        }
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(now).action(CREATED)).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).source(REVIEW_SCREEN)).actionSubject(PLAN, planDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    EventDto buildAddedSpaceAnalyticsEvent(long now, String planId, ConfluenceSpaceTaskDto confluenceSpaceTaskDto, long estimatedTime, SpaceStats spaceStats, ConfluenceUser confluenceUser) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        this.feedSpaceIdIfPresent(spaceStats.getSpaceKey(), spaceId -> attributes.put((Object)SPACE_ID, (Object)Long.toString(spaceId)));
        attributes.put((Object)PLAN_ID, (Object)planId);
        attributes.put((Object)STATUS, (Object)confluenceSpaceTaskDto.getProgress().getStatus());
        attributes.put((Object)TYPE_ATTRIBUTE_NAME, (Object)"confluence-space");
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(estimatedTime));
        ContentSummary contentSummary = spaceStats.getSummary();
        attributes.put((Object)NUMBER_OF_PAGES, (Object)Long.toString(contentSummary.getNumberOfPages()));
        attributes.put((Object)NUMBER_OF_BLOGS, (Object)Long.toString(contentSummary.getNumberOfBlogs()));
        attributes.put((Object)NUMBER_OF_TEAM_CALENDARS, (Object)Long.toString(contentSummary.getNumberOfTeamCalendars() != null ? contentSummary.getNumberOfTeamCalendars() : 0L));
        attributes.put((Object)"numberOfDrafts", (Object)Long.toString(contentSummary.getNumberOfDrafts()));
        attributes.put((Object)"numberOfAttachments", (Object)Long.toString(contentSummary.getNumberOfAttachments()));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(now).action("added")).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).source(REVIEW_SCREEN)).actionSubject("object", confluenceSpaceTaskDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    EventDto buildAddedUserGroupsAnalyticsEvent(long now, String planId, MigrateUsersTaskDto migrateUsersTaskDto, long estimatedTime, UsersGroupsStats usersStats, ConfluenceUser confluenceUser) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLAN_ID, (Object)planId);
        attributes.put((Object)STATUS, (Object)migrateUsersTaskDto.getProgress().getStatus());
        attributes.put((Object)TYPE_ATTRIBUTE_NAME, (Object)"users-and-groups");
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(estimatedTime));
        attributes.put((Object)NUMBER_OF_USERS, (Object)Long.toString(usersStats.getNumberOfUsers()));
        attributes.put((Object)NUMBER_OF_GROUPS, (Object)Long.toString(usersStats.getNumberOfGroups()));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(now).action("added")).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).source(REVIEW_SCREEN)).actionSubject("object", migrateUsersTaskDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    private EventDto buildAddedGlobalEntitiesAnalyticsEvent(long now, String planId, MigrateGlobalEntitiesTaskDto migrateGlobalEntitiesTaskDto, long estimatedTime, GlobalEntitiesStats globalEntitiesStats, ConfluenceUser confluenceUser) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLAN_ID, (Object)planId);
        attributes.put((Object)STATUS, (Object)migrateGlobalEntitiesTaskDto.getProgress().getStatus());
        attributes.put((Object)TYPE_ATTRIBUTE_NAME, (Object)GLOBAL_ENTITIES);
        this.addGlobalEntitiesStatsToAttributes(attributes, globalEntitiesStats);
        attributes.put((Object)ESTIMATED_TIME, (Object)Long.toString(estimatedTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(now).action("added")).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).source(REVIEW_SCREEN)).actionSubject("object", migrateGlobalEntitiesTaskDto.getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildTrackAnalyticsEvent(TrackAnalyticsEventDto eventDto, PlanDto planDto, ConfluenceUser confluenceUser) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        String action = eventDto.getAction();
        if (STOPPED.equals(action)) {
            attributes.put((Object)STATUS, (Object)planDto.getProgress().getStatus());
            attributes.put((Object)STOP_TIME, (Object)planDto.getProgress().getEndTime());
        }
        return ((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)((GenericTrackEvent.Builder)new GenericTrackEvent.Builder(eventDto.getTimestamp()).action(eventDto.getAction())).sen(this.senSupplier.get())).email(confluenceUser == null ? null : confluenceUser.getEmail())).source(eventDto.getSource())).actionSubject(eventDto.getActionSubject(), eventDto.getActionSubjectId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserBaseScanStartedEvent(String scanId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SCAN_ID, (Object)scanId);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(STARTED)).sen(this.senSupplier.get())).source(UDC2)).actionSubject(USER_BASE_SCAN)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserBaseScanErrorEvent(String reason, String cloudId, String scanId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SCAN_ID, (Object)scanId);
        attributes.put((Object)REASON, (Object)reason);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(UDC2)).cloudId(cloudId)).actionSubject(USER_BASE_SCAN)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserBaseScanFinishedEvent(String scanId, int totalUsersCount, int invalidUsersCount, int duplicateUsersCount, long timeToComplete) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SCAN_ID, (Object)scanId);
        attributes.put((Object)STATUS, (Object)SUCCESS);
        attributes.put((Object)"totalUsersCount", (Object)totalUsersCount);
        attributes.put((Object)"invalidUsersCount", (Object)invalidUsersCount);
        attributes.put((Object)"duplicateUsersCount", (Object)duplicateUsersCount);
        attributes.put((Object)"timeToComplete", (Object)timeToComplete);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).sen(this.senSupplier.get())).source(UDC2)).actionSubject(USER_BASE_SCAN)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildNewEmailSuggestingFinishedEvent(String cloudId, int emailsFetchedCount, int uniqueDomainsFetchedCount, int blockedDomainsCount, long blockedDomainsLookupTime, long executionTime, String emailsSource) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)STATUS, (Object)SUCCESS);
        attributes.put((Object)"emailsFetchedCount", (Object)emailsFetchedCount);
        attributes.put((Object)"uniqueDomainsFetchedCount", (Object)uniqueDomainsFetchedCount);
        attributes.put((Object)"blockedDomainsCount", (Object)blockedDomainsCount);
        attributes.put((Object)"blockedDomainsLookupTime", (Object)blockedDomainsLookupTime);
        attributes.put((Object)"executionTime", (Object)executionTime);
        attributes.put((Object)"emailsSource", (Object)emailsSource);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).sen(this.senSupplier.get())).source(UDC2)).cloudId(cloudId)).actionSubject(NEW_EMAIL_SUGGESTING)).action(FINISHED)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightDuplicateEmails(boolean success, List<EmailDuplicate> duplicateEmails, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        LongAdder numberOfEmails = new LongAdder();
        LongAdder numberOfUsers = new LongAdder();
        duplicateEmails.forEach(duplicateEmail -> {
            numberOfEmails.increment();
            numberOfUsers.add(duplicateEmail.ids.size());
        });
        attributes.put((Object)"numberOfSharedEmails", (Object)Long.toString(numberOfEmails.longValue()));
        attributes.put((Object)NUMBER_OF_USERS, (Object)Long.toString(numberOfUsers.longValue()));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("sharedEmailsCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightInvalidEmails(boolean success, List<EmailData> invalidEmails, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"numberOfInvalidEmails", (Object)Long.toString(invalidEmails.size()));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("invalidEmailsCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildAppPartUploadEvent(MultPartUploadAnalyticEvent multPartUploadAnalyticEvent, Long time) {
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(time.longValue()).action("uploaded")).actionSubject("appDataPart")).source("appDataPartUploaded")).cloudId(multPartUploadAnalyticEvent.getCloudId())).contextContainer(MIGRATION_ID, multPartUploadAnalyticEvent.getMigrationId())).withAttributes(this.getAttributesMap(multPartUploadAnalyticEvent))).cloudId(multPartUploadAnalyticEvent.getCloudId())).sen(this.senSupplier.get())).build();
    }

    public EventDto buildAppServerListenerIssueEvent(String appKey, AppListenerIssueType issueType) {
        PluginInformation pluginInfo = this.pluginManager.getPlugin(appKey).getPluginInformation();
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)"appKey", (Object)appKey);
        attributes.put((Object)"appVersion", (Object)pluginInfo.getVersion());
        attributes.put((Object)"issueType", (Object)issueType.name());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("occurred")).sen(this.senSupplier.get())).actionSubject("appServerListenerIssue")).withAttributes((Map)attributes.build())).build();
    }

    @NotNull
    private HashMap<String, Object> getAttributesMap(@NotNull MultPartUploadAnalyticEvent multPartUploadAnalyticEvent) {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.putAll((Map<String, Object>)this.getBuilderWithCommonAttributes().build());
        attributes.put("transferId", multPartUploadAnalyticEvent.getTransferId());
        attributes.put("s3Key", multPartUploadAnalyticEvent.getS3Key());
        attributes.put("appKey", multPartUploadAnalyticEvent.getAppKey());
        attributes.put("contentMD5", multPartUploadAnalyticEvent.getMd5Sum());
        attributes.put("partIndex", multPartUploadAnalyticEvent.getIndex());
        attributes.put("sizeKb", multPartUploadAnalyticEvent.getSize());
        attributes.put("partUploadTimeSeconds", multPartUploadAnalyticEvent.getUploadToS3ElapsedTime());
        attributes.put("urlGenerateTimeSeconds", multPartUploadAnalyticEvent.getGetUrlElapsedTime());
        attributes.put("uploadTotalTimeSeconds", multPartUploadAnalyticEvent.getTotalElapsedTime());
        return attributes;
    }

    public EventDto buildPreflightGroupNamesConflict(boolean success, List<String> groups, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_GROUPS, (Object)Long.toString(groups.size()));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("cloudExistantGroupsCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightSpaceKeysConflict(boolean success, List<SpaceConflict> conflicts, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_SPACES, (Object)Long.toString(conflicts.size()));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("spaceConflictCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightGlobalDataTemplatesConflict(boolean success, int numOfConflicts, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_TEMPLATES, (Object)Integer.toString(numOfConflicts));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("globalDataTemplateConflictCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightAppOutdated(boolean success, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("migrationAssistantOutdatedCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightConfluenceSupportedVersionCheck(boolean success, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject(CheckType.CONFLUENCE_SUPPORTED_VERSION.value())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightCloudFreeUsersCheck(boolean success, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("cloudFreeUsersConflictCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightCloudPremiumEditionCheck(boolean success, long totalTime, String edition) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        if (edition != null) {
            attributes.put((Object)EDITION, (Object)edition);
        }
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject(CLOUD_PREMIUM_EDITION_CHECK_EVENT)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightSpaceAnonymousAccess(boolean success, Integer numberOfSpaces, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_SPACES, (Object)Integer.toString(numberOfSpaces));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("spaceAnonymousPermissionCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightTcAppVersion(boolean success, long totalTime, String appVersion) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        if (appVersion != null) {
            attributes.put((Object)TC_APP_VERSION, (Object)appVersion);
        }
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("teamCalendarsVersionCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreFlightContainerTokenExpiration(boolean success, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("containerTokenExpirationCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreFlightMigrationOrchestratorMaintenance(boolean success, long totalTime) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        this.addCommonAttributesInPreflightCheck(success, totalTime, (ImmutableMap.Builder<String, Object>)attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("migrationOrchestratorMaintenanceCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightAppAssessmentComplete(boolean success, int apps, long totalTime) {
        return this.buildPreflightAppsDefault("appAssessmentCompleteCheck", success, apps, totalTime);
    }

    public EventDto buildPreflightAppDataConsent(boolean success, int apps, long totalTime) {
        return this.buildPreflightAppsDefault("appDataMigrationConsentCheck", success, apps, totalTime);
    }

    public EventDto buildPreflightAppReliability(boolean success, int apps, long totalTime) {
        return this.buildPreflightAppsDefault("appReliabilityCheck", success, apps, totalTime);
    }

    public EventDto buildPreflightServerAppOutdated(boolean success, int apps, long totalTime) {
        return this.buildPreflightAppsDefault("serverAppOutdatedCheck", success, apps, totalTime);
    }

    public EventDto buildPreflightAppsNotInstalledOnCloud(boolean success, int apps, long totalTime) {
        return this.buildPreflightAppsDefault("appsNotInstalledOnCloudCheck", success, apps, totalTime);
    }

    public EventDto buildPreflightAppLicenseCheck(boolean success, Set<String> appsSucceeded, Set<String> appsFailed, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        int numberOfAppsChecked = appsSucceeded.size() + appsFailed.size();
        attributes.put((Object)NUMBER_OF_APPS, (Object)Integer.toString(numberOfAppsChecked));
        attributes.put((Object)"numberOfAppsChecked", (Object)Integer.toString(numberOfAppsChecked));
        attributes.put((Object)"numberOfAppsFailed", (Object)Integer.toString(appsFailed.size()));
        attributes.put((Object)"appsWithSuccessfulCheck", appsSucceeded);
        attributes.put((Object)"appsWithFailedCheck", appsFailed);
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("appLicenseCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightAppsDefault(String subject, boolean success, int apps, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_APPS, (Object)Integer.toString(apps));
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject(subject)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightAppVendorCheck(CheckResult checkResult, long totalTime) {
        if (Checker.retrieveExecutionErrorCode((CheckResult)checkResult) != null) {
            ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
            attributes.put((Object)"atlassianException", (Object)true);
            this.addCommonAttributesInPreflightCheck(checkResult.success, totalTime, attributes);
            return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("appVendorCheck")).withAttributes((Map)attributes.build())).build();
        }
        Map appVendorCheckResult = checkResult.details;
        int totalNumberOfApps = appVendorCheckResult.size();
        Set resultDtos = appVendorCheckResult.values().stream().flatMap(checkIdToCheckResultMap -> ((HashMap)checkIdToCheckResultMap).values().stream()).collect(Collectors.toSet());
        int totalChecksExecuted = resultDtos.size();
        int totalFailedChecks = (int)resultDtos.stream().filter(checkResultDto -> checkResultDto.status != CheckStatus.SUCCESS).count();
        List checksWithStatusWarning = resultDtos.stream().filter(checkResultDto -> checkResultDto.status == CheckStatus.WARNING).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List checksWithStatusBlocked = resultDtos.stream().filter(checkResultDto -> checkResultDto.status == CheckStatus.BLOCKING).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List checksWithStatusCheckExecutionError = resultDtos.stream().filter(checkResultDto -> checkResultDto.status == CheckStatus.CHECK_EXECUTION_ERROR).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List unsuccessfulChecksWithCsvContent = resultDtos.stream().filter(checkResultDto -> checkResultDto.showCsv).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List checksWithException = resultDtos.stream().filter(checkResultDto -> checkResultDto.getCheckDetails() != null && checkResultDto.getCheckDetails().contains(CheckDetail.EXCEPTION)).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List checksWithTimeout = resultDtos.stream().filter(checkResultDto -> checkResultDto.getCheckDetails() != null && checkResultDto.getCheckDetails().contains(CheckDetail.TIMEOUT)).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        List checksWithCsvTruncation = resultDtos.stream().filter(checkResultDto -> checkResultDto.getCheckDetails() != null && checkResultDto.getCheckDetails().contains(CheckDetail.CSV_TRUNCATED)).map(checkResultDto -> checkResultDto.checkId).collect(Collectors.toList());
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_APPS, (Object)Integer.toString(totalNumberOfApps));
        attributes.put((Object)"numberOfChecksExecuted", (Object)Integer.toString(totalChecksExecuted));
        attributes.put((Object)"numberOfFailedChecks", (Object)Integer.toString(totalFailedChecks));
        attributes.put((Object)"unsuccessfulChecksAsWarning", checksWithStatusWarning);
        attributes.put((Object)"unsuccessfulChecksAsBlocking", checksWithStatusBlocked);
        attributes.put((Object)"unsuccessfulChecksAsCheckExecutionError", checksWithStatusCheckExecutionError);
        attributes.put((Object)"unsuccessfulChecksWithCSVContent", unsuccessfulChecksWithCsvContent);
        attributes.put((Object)"checksWithException", checksWithException);
        attributes.put((Object)"checksWithTimeout", checksWithTimeout);
        attributes.put((Object)"checksWithCsvTruncation", checksWithCsvTruncation);
        this.addCommonAttributesInPreflightCheck(checkResult.success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("appVendorCheck")).withAttributes((Map)attributes.build())).build();
    }

    public List<EventDto> buildPreflightAppVendorCheckIndividual(Map<String, Object> appVendorCheckResult, boolean devModeFFEnabled, @Nullable String planId, @Nullable String planMigrationTag, long totalTime) {
        return appVendorCheckResult.keySet().stream().flatMap(appKey -> {
            Map checkIdToCheckResultMap = (Map)appVendorCheckResult.get(appKey);
            return checkIdToCheckResultMap.values().stream().map(checkResultDto -> {
                boolean executed = checkResultDto.status != CheckStatus.CHECK_EXECUTION_ERROR;
                boolean success = checkResultDto.status == CheckStatus.SUCCESS;
                boolean csvContentTruncated = false;
                boolean exception = false;
                boolean timeout = false;
                if (checkResultDto.getCheckDetails() != null) {
                    csvContentTruncated = checkResultDto.getCheckDetails().contains(CheckDetail.CSV_TRUNCATED);
                    exception = checkResultDto.getCheckDetails().contains(CheckDetail.EXCEPTION);
                    timeout = checkResultDto.getCheckDetails().contains(CheckDetail.TIMEOUT);
                }
                ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
                this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
                attributes.put((Object)"appKey", appKey);
                attributes.put((Object)"csvContentTruncated", (Object)csvContentTruncated);
                attributes.put((Object)"devModeFFEnabled", (Object)devModeFFEnabled);
                attributes.put((Object)"csvContent", (Object)checkResultDto.showCsv);
                attributes.put((Object)"exception", (Object)exception);
                attributes.put((Object)EXECUTED, (Object)executed);
                attributes.put((Object)"timeout", (Object)timeout);
                if (checkResultDto.checkId != null) {
                    attributes.put((Object)"checkId", (Object)checkResultDto.checkId);
                }
                if (planId != null) {
                    attributes.put((Object)PLAN_ID, (Object)planId);
                }
                if (planMigrationTag != null) {
                    attributes.put((Object)MIGRATION_TAG, (Object)planMigrationTag);
                }
                if (checkResultDto.status != null) {
                    attributes.put((Object)STATUS, (Object)checkResultDto.status);
                }
                return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).actionSubject("appVendorCheckIndividual")).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).withAttributes((Map)attributes.build())).build();
            });
        }).collect(Collectors.toList());
    }

    public EventDto buildPreflightAppWebhookEndpointCheck(CheckResult checkResult, long totalTime, Set<String> appsList, String cloudId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        Set appWebhookCheckResult = checkResult.details.getOrDefault("appKeysMissingWebhooks", Collections.emptySet());
        Set appsWithFailedCheck = appWebhookCheckResult.stream().map(it -> it.key).collect(Collectors.toSet());
        attributes.put((Object)NUMBER_OF_APPS, (Object)Integer.toString(appsList.size()));
        attributes.put((Object)"numberOfAppsChecked", (Object)Integer.toString(appsList.size()));
        attributes.put((Object)"numberOfAppsFailed", (Object)Integer.toString(appsWithFailedCheck.size()));
        attributes.put((Object)"appsWithSuccessfulCheck", (Object)CollectionUtils.subtract(appsList, appsWithFailedCheck));
        attributes.put((Object)"appsWithFailedCheck", appsWithFailedCheck);
        attributes.put((Object)"cloudId", (Object)cloudId);
        this.addCommonAttributesInPreflightCheck(checkResult.success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("appWebhookEndpointCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceExportStepTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step, ExportType exportType) {
        return this.buildSpaceExportStepTimerEvent(stepSuccessful, totalTime, spaceKey, step, exportType, Collections.emptyMap());
    }

    public EventDto buildGlobalEntitiesExportStepTimerEvent(boolean stepSuccessful, long totalTime, String planId, String cloudId, String taskId) {
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, GLOBAL_ENTITIES_EXPORT_ACTION_NAME, planId, cloudId, taskId);
    }

    public EventDto buildGlobalEntitiesUploadStepTimerEvent(boolean stepSuccessful, long totalTime, String planId, String cloudId, String taskId, long uploadSize) {
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, GLOBAL_ENTITIES_UPLOAD_ACTION_NAME, planId, cloudId, taskId, (Map<String, Object>)ImmutableMap.of((Object)UPLOAD_SIZE_ATTRIBUTE_NAME, (Object)uploadSize));
    }

    public EventDto buildGlobalEntitiesImportStepTimerEvent(boolean stepSuccessful, long totalTime, String planId, String cloudId, String taskId) {
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, GLOBAL_ENTITIES_IMPORT_ACTION_NAME, planId, cloudId, taskId);
    }

    public EventDto buildSpaceExportStepTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step, ExportType exportType, Map<String, Object> attributes) {
        HashMap<String, Object> eventAttributes = new HashMap<String, Object>();
        eventAttributes.put(SPACE_EXPORT_TYPE, exportType.name());
        eventAttributes.putAll(attributes);
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, SPACE_EXPORT_ACTION_NAME, spaceKey, step, null, eventAttributes);
    }

    public EventDto buildAttachmentStepTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, ATTACHMENT_UPLOAD_ACTION_NAME, spaceKey, step);
    }

    public EventDto buildSpaceImportTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, SPACE_IMPORTED_ACTION_NAME, spaceKey, step);
    }

    public EventDto buildExportPostProcessedTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, String planId, String taskId, Boolean isGDPRReady) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, EXPORT_POST_PROCESSED_ACTION_NAME, spaceKey, planId, taskId, isGDPRReady, Collections.emptyMap());
    }

    public EventDto buildSpaceUploadStepTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step, long uploadSize, UploadDestinationType uploadType) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, SPACE_UPLOADED_ACTION_NAME, spaceKey, step, (Map<String, Object>)ImmutableMap.of((Object)UPLOAD_SIZE_ATTRIBUTE_NAME, (Object)uploadSize, (Object)UPLOAD_TYPE_ATTRIBUTE_NAME, (Object)uploadType.name()));
    }

    public EventDto buildMCSFileUploadTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, Step step, long uploadSize, String filename) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, MCS_FILE_UPLOADED_ACTION_NAME, spaceKey, step, (Map<String, Object>)ImmutableMap.of((Object)UPLOAD_SIZE_ATTRIBUTE_NAME, (Object)uploadSize, (Object)FILE_NAME_ATTRIBUTE_NAME, (Object)filename));
    }

    public EventDto buildGlobalEntitiesMCSFileUploadTimerEvent(boolean stepSuccessful, long totalTime, String planId, String cloudId, String taskId, long uploadSize, String filename) {
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, MCS_GLOBAL_ENTITIES_FILE_UPLOADED_ACTION_NAME, planId, cloudId, taskId, (Map<String, Object>)ImmutableMap.of((Object)UPLOAD_SIZE_ATTRIBUTE_NAME, (Object)uploadSize, (Object)FILE_NAME_ATTRIBUTE_NAME, (Object)filename));
    }

    private Map<String, Object> buildTableExportedToCSVTimerEvent(String tableName, String query, String dbType, long timeToFirstRecord, long rowsExported, long charactersExported) {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(TABLE_NAME_ATTRIBUTE_NAME, tableName);
        attributes.put(TIME_TO_FIRST_RECORD_ATTRIBUTE_NAME, timeToFirstRecord);
        attributes.put(ROWS_EXPORTED_ATTRIBUTE_NAME, rowsExported);
        attributes.put(CHARS_EXPORTED_ATTRIBUTE_NAME, charactersExported);
        attributes.put(EXPORT_QUERY_HASH_ATTRIBUTE_NAME, DigestUtils.sha256Hex((String)query));
        attributes.put(DB_TYPE, dbType);
        return attributes;
    }

    public EventDto buildSpaceTableExportedToCSVTimerEvent(boolean stepSuccessful, long totalTime, String spaceKey, String planId, String taskId, String tableName, String query, String dbType, long timeToFirstRecord, long rowsExported, long charactersExported) {
        Map<String, Object> attributes = this.buildTableExportedToCSVTimerEvent(tableName, query, dbType, timeToFirstRecord, rowsExported, charactersExported);
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, TABLE_EXPORTED_TO_CSV_ACTION_NAME, spaceKey, planId, taskId, null, attributes);
    }

    public EventDto buildGlobalEntitiesTableExportedToCSVTimerEvent(boolean stepSuccessful, long totalTime, String planId, String cloudId, String taskId, String tableName, String query, String dbType, long timeToFirstRecord, long rowsExported, long charactersExported) {
        Map<String, Object> attributes = this.buildTableExportedToCSVTimerEvent(tableName, query, dbType, timeToFirstRecord, rowsExported, charactersExported);
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, TABLE_EXPORTED_TO_CSV_ACTION_NAME, planId, cloudId, taskId, attributes);
    }

    public EventDto buildSpaceStatisticCalculationBatchStepExecutionErrorEvent(String jobId, String executionId, String batchId, String batchStep, int batchSize, boolean includeHistoricalData, Exception e, String spaceId) {
        String calculationExceptionMessage = e == null ? "" : e.getMessage().trim();
        ImmutableMap.Builder<String, Object> attributes = this.getSpaceStatisticBuilderWithCommonAttributes();
        attributes.put((Object)"jobId", (Object)jobId);
        attributes.put((Object)"executionId", (Object)executionId);
        attributes.put((Object)"batchSize", (Object)batchSize);
        attributes.put((Object)"includeHistoricalData", (Object)includeHistoricalData);
        attributes.put((Object)SPACE_ID, (Object)(spaceId == null ? "" : spaceId));
        attributes.put((Object)"error", (Object)calculationExceptionMessage.substring(0, Math.min(calculationExceptionMessage.length(), this.migrationAgentConfiguration.getAnalyticsSenderMaxEventLength() / 2)));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).sen(this.senSupplier.get())).source(SPACE_STATISTIC_CALCULATION)).action(FAILED)).actionSubject(batchStep)).contextContainer("batch", batchId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceStatisticCalculationBatchExecutionCompletedEvent(String jobId, String executionId, String batchId, int batchSize, boolean includeHistoricalData, boolean overallSuccess, Map<String, Boolean> executionStepsSuccess, int errorCount, long startTimeEpochMilli, long readEndTimeEpochMilli, long endTimeEpochMilli) {
        ImmutableMap.Builder<String, Object> attributes = this.getSpaceStatisticBuilderWithCommonAttributes();
        attributes.put((Object)"jobId", (Object)jobId);
        attributes.put((Object)"executionId", (Object)executionId);
        attributes.put((Object)"batchSize", (Object)batchSize);
        attributes.put((Object)"includeHistoricalData", (Object)includeHistoricalData);
        attributes.put((Object)STATUS, (Object)(overallSuccess ? SUCCESS : FAIL));
        attributes.put((Object)"executionStepsSuccess", executionStepsSuccess);
        attributes.put((Object)"errorCount", (Object)errorCount);
        attributes.put((Object)START_TIME, (Object)Instant.ofEpochMilli(startTimeEpochMilli));
        attributes.put((Object)STOP_TIME, (Object)Instant.ofEpochMilli(endTimeEpochMilli));
        attributes.put((Object)"totalTimeTaken", (Object)(endTimeEpochMilli - startTimeEpochMilli));
        attributes.put((Object)"timeTakenToCalculate", (Object)(readEndTimeEpochMilli - startTimeEpochMilli));
        attributes.put((Object)"timeTakenToStore", (Object)(endTimeEpochMilli - readEndTimeEpochMilli));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).sen(this.senSupplier.get())).source(SPACE_STATISTIC_CALCULATION)).action(COMPLETED)).actionSubject("batchExecution")).contextContainer("batch", batchId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceStatisticCalculationJobExecutionCompletedEvent(String jobId, String executionId, int spaceCount, int numberOfBatches, int batchSizeLimit, boolean includeHistoricalData, long startTimeEpocMilli, long waitStartTimeEpocMilli, long endTimeEpocMilli) {
        ImmutableMap.Builder<String, Object> attributes = this.getSpaceStatisticBuilderWithCommonAttributes();
        attributes.put((Object)"jobId", (Object)jobId);
        attributes.put((Object)NUMBER_OF_SPACES, (Object)spaceCount);
        attributes.put((Object)"numberOfBatches", (Object)numberOfBatches);
        attributes.put((Object)"batchSizeLimit", (Object)batchSizeLimit);
        attributes.put((Object)"includeHistoricalData", (Object)includeHistoricalData);
        attributes.put((Object)START_TIME, (Object)Instant.ofEpochMilli(startTimeEpocMilli));
        attributes.put((Object)STOP_TIME, (Object)Instant.ofEpochMilli(endTimeEpocMilli));
        attributes.put((Object)"totalTimeTaken", (Object)(endTimeEpocMilli - startTimeEpocMilli));
        attributes.put((Object)"timeTakenToSubmit", (Object)(waitStartTimeEpocMilli - startTimeEpocMilli));
        attributes.put((Object)"timeTakenToExecute", (Object)(endTimeEpocMilli - waitStartTimeEpocMilli));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).sen(this.senSupplier.get())).source(SPACE_STATISTIC_CALCULATION)).action(COMPLETED)).actionSubject("jobExecution")).contextContainer("execution", executionId)).withAttributes((Map)attributes.build())).build();
    }

    @VisibleForTesting
    public EventDto buildSpaceStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String spaceKey, Step step) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, action, spaceKey, step, null, Collections.emptyMap());
    }

    @VisibleForTesting
    public EventDto buildSpaceStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String spaceKey, Step step, Map<String, Object> additionalAttributes) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, action, spaceKey, step, null, additionalAttributes);
    }

    @VisibleForTesting
    public EventDto buildSpaceStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String spaceKey, Step step, Boolean isGDPRReady) {
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, action, spaceKey, step, isGDPRReady, Collections.emptyMap());
    }

    @VisibleForTesting
    public EventDto buildSpaceStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String spaceKey, String planId, String taskId, Boolean isGDPRReady, Map<String, Object> additionalAttributes) {
        ImmutableMap.Builder<String, Object> attributes = this.getSpaceStepTimeEventCommonAttributes(stepSuccessful, totalTime, spaceKey, isGDPRReady, additionalAttributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(SPACE, taskId)).withAttributes((Map)attributes.build())).contextContainer(PLAN, planId)).build();
    }

    @VisibleForTesting
    public EventDto buildSpaceStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String spaceKey, Step step, Boolean isGDPRReady, Map<String, Object> additionalAttributes) {
        String planId = step.getPlan().getId();
        String taskId = step.getTask().getId();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(STEP_ID, step.getId());
        attributes.putAll(additionalAttributes);
        return this.buildSpaceStepTimerEvent(stepSuccessful, totalTime, action, spaceKey, planId, taskId, isGDPRReady, attributes);
    }

    private ImmutableMap.Builder<String, Object> getSpaceStepTimeEventCommonAttributes(boolean stepSuccessful, long totalTime, String spaceKey, Boolean isGDPRReady, Map<String, Object> additionalAttributes) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        this.feedSpaceIdIfPresent(spaceKey, spaceId -> attributes.put((Object)SPACE_ID, (Object)Long.toString(spaceId)));
        attributes.put((Object)STEP_SUCCESSFUL, (Object)stepSuccessful);
        this.feedSpaceStatsIfPresent(spaceKey, spaceStats -> {
            ContentSummary contentSummary = spaceStats.getSummary();
            attributes.put((Object)"numberOfAttachments", (Object)Long.toString(contentSummary.getNumberOfAttachments() != null ? contentSummary.getNumberOfAttachments() : 0L));
            attributes.put((Object)NUMBER_OF_BLOGS, (Object)Long.toString(contentSummary.getNumberOfBlogs() != null ? contentSummary.getNumberOfBlogs() : 0L));
            attributes.put((Object)"numberOfDrafts", (Object)Long.toString(contentSummary.getNumberOfDrafts() != null ? contentSummary.getNumberOfDrafts() : 0L));
            attributes.put((Object)NUMBER_OF_PAGES, (Object)Long.toString(contentSummary.getNumberOfPages() != null ? contentSummary.getNumberOfPages() : 0L));
            attributes.put((Object)NUMBER_OF_TEAM_CALENDARS, (Object)Long.toString(contentSummary.getNumberOfTeamCalendars() != null ? contentSummary.getNumberOfTeamCalendars() : 0L));
            Long totalSize = contentSummary.getAttachments().getTotalSize();
            if (totalSize != null) {
                attributes.put((Object)TOTAL_ATTACHMENTS_SIZE, (Object)Long.toString(totalSize));
            } else {
                attributes.put((Object)TOTAL_ATTACHMENTS_SIZE, (Object)Long.toString(0L));
            }
        });
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.putAll(additionalAttributes);
        if (isGDPRReady != null) {
            attributes.put((Object)"isGDPRReady", (Object)Boolean.toString(isGDPRReady));
        }
        return attributes;
    }

    @VisibleForTesting
    public EventDto buildGlobalEntitiesStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String planId, String cloudId, String taskId) {
        return this.buildGlobalEntitiesStepTimerEvent(stepSuccessful, totalTime, action, planId, cloudId, taskId, Collections.emptyMap());
    }

    @VisibleForTesting
    public EventDto buildGlobalEntitiesStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String planId, String cloudId, String taskId, Map<String, Object> additionalAttributes) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)STEP_SUCCESSFUL, (Object)stepSuccessful);
        this.addGlobalEntitiesStatsToAttributes((ImmutableMap.Builder<String, Object>)attributes, this.statisticsService.getGlobalEntitiesStatistics(planId));
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.putAll(additionalAttributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(GLOBAL_ENTITY, taskId)).withAttributes((Map)attributes.build())).cloudId(cloudId)).contextContainer(PLAN, planId)).build();
    }

    private void addGlobalEntitiesStatsToAttributes(ImmutableMap.Builder<String, Object> attributes, GlobalEntitiesStats globalEntitiesStats) {
        attributes.put((Object)"totalGlobalPageTemplates", (Object)Long.toString(globalEntitiesStats.getNumberOfGlobalPageTemplates()));
        attributes.put((Object)"totalCustomSystemTemplates", (Object)Long.toString(globalEntitiesStats.getNumberOfEditedSystemTemplates()));
    }

    public EventDto buildUserDomainCountsFetchedEvent(long timeTaken, long userCount, int domainCount, boolean success) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"totalUserDomainCount", (Object)domainCount);
        attributes.put((Object)"totalUserCount", (Object)userCount);
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(timeTaken));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("fetched")).sen(this.senSupplier.get())).actionSubject("userDomainCounts")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserDomainRuleUpdatedEvent(DomainRuleBehaviour rule, boolean success) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"rule", (Object)String.valueOf((Object)rule));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("updated")).sen(this.senSupplier.get())).actionSubject(USER_DOMAIN_RULE)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserDomainRuleDeletedEvent(boolean success) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("deleted")).sen(this.senSupplier.get())).actionSubject(USER_DOMAIN_RULE)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildAllUserDomainRuleDeletedEvent(boolean success, int count) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"count", (Object)count);
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("deleted-all")).sen(this.senSupplier.get())).actionSubject(USER_DOMAIN_RULE)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserDomainRulesDeletedEvent(boolean success, int count) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"count", (Object)count);
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("deleted")).sen(this.senSupplier.get())).actionSubject(USER_DOMAIN_RULE)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceUsersMigratedEvent(int userWithValidEmails, int usersToTombstone, long spaceId, String migrationId) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)"usersWithValidEmails", (Object)userWithValidEmails);
        attributes.put((Object)"usersToTombstone", (Object)usersToTombstone);
        attributes.put((Object)SPACE_ID, (Object)String.valueOf(spaceId));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("usersForSpaceUsersMigration")).sen(this.senSupplier.get())).actionSubject(MIGRATION, migrationId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildUserStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String planId, boolean scoped) {
        return this.buildUserStepTimerEvent(stepSuccessful, totalTime, action, planId, scoped, -1, -1);
    }

    public EventDto buildSpaceUserStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String migrationId, long spaceId) {
        return this.buildSpaceUserStepTimerEvent(stepSuccessful, totalTime, action, migrationId, -1, spaceId);
    }

    public EventDto buildUserStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String planId, boolean scoped, int userCount, int groupCount) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)STEP_SUCCESSFUL, (Object)stepSuccessful);
        attributes.put((Object)"userCount", (Object)String.valueOf(userCount));
        attributes.put((Object)"groupCount", (Object)String.valueOf(groupCount));
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)"scoped", (Object)scoped);
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(PLAN, planId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceUserStepTimerEvent(boolean stepSuccessful, long totalTime, String action, String migrationId, int userCount, long spaceId) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)STEP_SUCCESSFUL, (Object)stepSuccessful);
        attributes.put((Object)"userCount", (Object)String.valueOf(userCount));
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)SPACE_ID, (Object)Long.toString(spaceId));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(MIGRATION, migrationId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildAttachmentMigrationEvent(long totalTime, SpaceAttachmentCount spaceAttachmentCount, UploadState uploadState) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)"spaceKey", (Object)spaceAttachmentCount.spaceKey);
        attributes.put((Object)"cloudId", (Object)spaceAttachmentCount.cloudId);
        attributes.put((Object)"contentAttachmentCount", (Object)spaceAttachmentCount.contentAttachmentCount);
        attributes.put((Object)"migAttachmentCount", (Object)spaceAttachmentCount.retrievedMigAttachmentCount);
        attributes.put((Object)"unRetrievableMigAttachmentCount", (Object)spaceAttachmentCount.unRetrievableMigAttachmentCount);
        attributes.put((Object)"allAttachmentsMigrated", (Object)spaceAttachmentCount.hasAllAttachmentsMigrated());
        attributes.put((Object)"numOfAttachments", (Object)uploadState.numOfAttachments);
        attributes.put((Object)"numOfUploadedAttachments", (Object)uploadState.numOfUploadedAttachments);
        attributes.put((Object)"numOfFailedAttachments", (Object)uploadState.numOfFailedAttachments);
        attributes.put((Object)"uploadedBytes", (Object)uploadState.uploadedBytes);
        attributes.put((Object)"totalBytes", (Object)uploadState.totalBytes);
        attributes.put((Object)"numOfAttachmentsAlreadyMigrated", (Object)uploadState.numOfAttachmentsAlreadyMigrated);
        attributes.put((Object)TIME_TAKEN, (Object)totalTime);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("migrated")).sen(this.senSupplier.get())).actionSubject("attachmentMigration")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildMetricEventForConfluenceMigration(boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_PLAN_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildPlatformPlanCompletionMetricEvent(ExecutionStatus status, MigrationTag migrationTag, MigrationCreator migrationCreator) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        Map<String, String> tagsMap = this.buildPlatformCounterMetricCommonTags(status, migrationTag);
        attributes.put((Object)PLATFORM_EVENT, (Object)true);
        tagsMap.put(MIGRATION_CREATOR, migrationCreator.name());
        tagsMap.put(VERSION, this.pluginVersionManager.getPluginVersion());
        return ((CounterMetricEvent.Builder)((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATIONS_OVERALL_SUCCESS_RATE.metricName).withAttributes((Map)attributes.build())).tags(tagsMap)).build();
    }

    public EventDto buildPlatformStepCompletionMetricEvent(String stepKey, ExecutionStatus status, MigrationTag migrationTag) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        Map<String, String> tagsMap = this.buildPlatformCounterMetricCommonTags(status, migrationTag);
        attributes.put((Object)PLATFORM_EVENT, (Object)true);
        Map<String, String> eventAttributesMap = stepCompletionEventAttributesMap.get(stepKey);
        tagsMap.put(COMPONENT_KEY, this.getValueFromEventAttributesMap(eventAttributesMap, COMPONENT_KEY));
        tagsMap.put(PHASE_KEY, this.getValueFromEventAttributesMap(eventAttributesMap, PHASE_KEY));
        return ((CounterMetricEvent.Builder)((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATIONS_COMPONENT_LEVEL_SUCCESS_RATE.metricName).withAttributes((Map)attributes.build())).tags(tagsMap)).build();
    }

    public EventDto buildPlatformStepCompletionExtendedMetricEvent(String stepKey, ExecutionStatus status, MigrationTag migrationTag) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        Map<String, String> tagsMap = this.buildPlatformCounterMetricCommonTags(status, migrationTag);
        attributes.put((Object)PLATFORM_EVENT, (Object)true);
        tagsMap.put(VERSION, this.pluginVersionManager.getPluginVersion());
        Map<String, String> eventAttributesMap = stepCompletionEventAttributesMap.get(stepKey);
        String component = this.getValueFromEventAttributesMap(eventAttributesMap, COMPONENT_KEY);
        tagsMap.put(PHASE_KEY, this.getValueFromEventAttributesMap(eventAttributesMap, PHASE_KEY));
        String metricName = MigrationMetric.MIGRATIONS_COMPONENT_LEVEL_SUCCESS_RATE.metricName + "." + component.toLowerCase();
        return ((CounterMetricEvent.Builder)((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(metricName).withAttributes((Map)attributes.build())).tags(tagsMap)).build();
    }

    public EventDto buildFailedSpaceMigration(StepType stepType, MigrationErrorCode migrationErrorCode) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, stepType.name());
        if (migrationErrorCode.shouldBeTreatedAsGoodEventInReliabilitySlo()) {
            tagsMap.put(RESULT, SUCCESS);
            return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.SPACE_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
        }
        tagsMap.put(RESULT, FAIL);
        tagsMap.put("error_code", String.valueOf(migrationErrorCode.getCode()));
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.SPACE_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildMigrationStepMetrics(StepType stepType, boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, stepType.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildExportStepCounterEvent(ExportType exportType, boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.CONFLUENCE_EXPORT.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        tagsMap.put(SPACE_EXPORT_TYPE, exportType.name());
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildImportStepCounterEvent(ImportType importType, boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.CONFLUENCE_IMPORT.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        tagsMap.put(SPACE_IMPORT_TYPE, importType.name());
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildUploadStepCounterEvent(UploadDestinationType uploadDestinationType, boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.DATA_UPLOAD.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        tagsMap.put(UPLOAD_TYPE_ATTRIBUTE_NAME, uploadDestinationType.name());
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildSuccessfulGlobalEntitiesMigration() {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(RESULT, SUCCESS);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.GLOBAL_ENTITIES_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildFailedGlobalEntitiesMigration(StepType stepType, MigrationErrorCode migrationErrorCode) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, stepType.name());
        if (migrationErrorCode.shouldBeTreatedAsGoodEventInReliabilitySlo()) {
            tagsMap.put(RESULT, SUCCESS);
            return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.GLOBAL_ENTITIES_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
        }
        tagsMap.put(RESULT, FAIL);
        tagsMap.put("error_code", String.valueOf(migrationErrorCode.getCode()));
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.GLOBAL_ENTITIES_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildGlobalEntitiesExportStepCounterEvent(boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.GLOBAL_ENTITIES_EXPORT.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildGlobalEntitiesImportStepCounterEvent(boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.GLOBAL_ENTITIES_IMPORT.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildGlobalEntitiesUploadStepCounterEvent(boolean success) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(STEP, StepType.GLOBAL_ENTITIES_DATA_UPLOAD.name());
        tagsMap.put(RESULT, success ? SUCCESS : FAIL);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_STEP.getMetricName()).tags(tagsMap)).build();
    }

    public EventDto buildGlobalEntitiesExportImportStartEvent(Step step, long startTime) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        return stepBuilder.build();
    }

    public EventDto buildGlobalEntitiesUploadStartEvent(Step step, long startTime, String fileId) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        stepBuilder.addAttribute(FILE_ID, (Object)fileId);
        return stepBuilder.build();
    }

    public EventDto buildSuccessfulSpaceMigration() {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(RESULT, SUCCESS);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.SPACE_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildFailedUserMigration(List<Integer> errorCodes, boolean isSloGoodEvent) {
        if (isSloGoodEvent) {
            return this.buildSuccessfulUserMigration();
        }
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(RESULT, FAIL);
        tagsMap.put(ERROR_CODES, errorCodes.stream().map(Object::toString).collect(Collectors.joining(",")));
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.USERS_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    public EventDto buildSuccessfulUserMigration() {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put(RESULT, SUCCESS);
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.USERS_MIGRATION_COUNT.metricName).tags(tagsMap)).build();
    }

    @Deprecated
    public EventDto buildMigrationErrorMetric(MigrationErrorCode migrationErrorCode) {
        Map<String, String> tagsMap = this.buildCounterMetricCommonTags();
        tagsMap.put("error_code", String.valueOf(migrationErrorCode.getCode()));
        return ((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.MIGRATION_ERRORS.metricName).tags(tagsMap)).build();
    }

    private void addCommonAttributesInPreflightCheck(boolean success, long totalTime, ImmutableMap.Builder<String, Object> attributes) {
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"totalTime", (Object)String.format(Locale.US, "%.1f", (double)totalTime / 1000.0));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
    }

    public EventDto buildPreflightFailed(String actionSubject, String executionId, int executionErrorCode) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)"executionErrorCode", (Object)executionErrorCode);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(FAILED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject(actionSubject)).contextContainer("preflightCheck", executionId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightMissingAttachments(boolean success, int size, long totalTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"numberOfMissingAttachments", (Object)size);
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("missingAttachmentsCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildPreflightTrustedDomain(boolean success, long totalTime) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        this.addCommonAttributesInPreflightCheck(success, totalTime, (ImmutableMap.Builder<String, Object>)attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(EXECUTED)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject("trustedDomainCheck")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildTeamCalendarExportTimeEvent(long totalTime, Long spaceId, String planId, String taskId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)TC_EXPORT_TIME, (Object)totalTime);
        attributes.put((Object)PLAN_ID, (Object)planId);
        attributes.put((Object)TASK_ID, (Object)taskId);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("teamCalendarExportTime")).sen(this.senSupplier.get())).actionSubject(SPACE_ID, Long.toString(spaceId))).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildMigrationLogTimerEvent(boolean success, long totalTime, String action, String migrationId) {
        return this.buildMigrationLogTimerEvent(success, totalTime, action, migrationId, null, Optional.empty(), Optional.empty());
    }

    public EventDto buildMigrationLogTimerEvent(boolean success, long totalTime, String action, String migrationId, Optional<String> errorReason) {
        return this.buildMigrationLogTimerEvent(success, totalTime, action, migrationId, null, errorReason, Optional.empty());
    }

    public EventDto buildMigrationLogTimerEvent(boolean success, long totalTime, String action, String migrationId, String cloudId, Optional<String> reason, Optional<String> fileName) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        if (StringUtils.isNotEmpty((String)cloudId)) {
            attributes.put((Object)TENANT_ID, (Object)cloudId);
        }
        if (reason.isPresent()) {
            attributes.put((Object)REASON, (Object)reason.get());
        }
        if (fileName.isPresent()) {
            attributes.put((Object)FILE_NAME_ATTRIBUTE_NAME, (Object)fileName.get());
        }
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        attributes.put((Object)"confluenceVersion", (Object)confluenceInfo.getVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(MIGRATION, migrationId)).withAttributes((Map)attributes.build())).build();
    }

    private List<String> getSpaceKeysByPlan(PlanDto planDto) {
        return planDto.getTasks().stream().filter(ConfluenceSpaceTaskDto.class::isInstance).map(taskDto -> ((ConfluenceSpaceTaskDto)taskDto).getSpace()).collect(Collectors.toList());
    }

    private long calculateSpaceMigrationTime(Collection<SpaceStats> spaceStatsCollection) {
        List<ContentSummary> spaceSummaries = spaceStatsCollection.stream().map(SpaceStats::getSummary).collect(Collectors.toList());
        return this.migrationTimeEstimationUtils.estimateTotalSpaceMigrationTime(spaceSummaries).getSeconds();
    }

    private Map<String, Object> addCommonFrontendAttributes(Map<String, Object> attributes) {
        HashMap<String, Object> map = new HashMap<String, Object>(attributes);
        map.put(PLUGIN_VERSION, this.pluginVersionManager.getPluginVersion());
        map.put(IS_INTERNAL_CONTEXT, this.pluginVersionManager.isTestVersion() != false || this.isInternalSen() != false);
        return map;
    }

    private Duration getUserGroupMigrationTime(PlanDto planDto) {
        if (PlanDtoUtil.containsUsersGroupsTask(planDto)) {
            return this.getUserGroupMigrationTimeWithUserTask(planDto);
        }
        return Duration.ZERO;
    }

    private Duration getUserGroupMigrationTimeWithUserTask(PlanDto planDto) {
        if (PlanDtoUtil.hasScopedUserTask(planDto)) {
            UsersGroupsStats stats = this.statisticsService.getUsersGroupsStatistics(PlanDtoUtil.userMigrationType(planDto), PlanDtoUtil.getSpaceKeys(planDto));
            return MigrationTimeEstimationUtils.estimateTotalUserGroupMigrationTime(stats.getNumberOfUsers(), stats.getNumberOfGroups());
        }
        UsageInfo usageInfo = this.systemInformationService.getUsageInfo();
        return MigrationTimeEstimationUtils.estimateTotalUserGroupMigrationTime(usageInfo.getLocalUsers(), usageInfo.getLocalGroups());
    }

    public EventDto buildErrorOperationalEvent(ErrorEvent errorEvent) {
        return this.buildErrorOperationalEvent(errorEvent, this.getBuilderWithCommonAttributes());
    }

    public EventDto buildErrorOperationalEventForSpaceExport(ErrorEvent errorEvent, ExportType exportType) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SPACE_EXPORT_TYPE, (Object)exportType);
        return this.buildErrorOperationalEvent(errorEvent, attributes);
    }

    public EventDto buildErrorOperationalEventForSpaceImport(ErrorEvent errorEvent, ImportType importType, Optional<String> optionalImportTaskId, Optional<Integer> optionalStatusCode) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SPACE_IMPORT_TYPE, (Object)importType);
        optionalStatusCode.ifPresent(statusCode -> attributes.put((Object)STATUS_CODE, statusCode));
        optionalImportTaskId.ifPresent(importTaskId -> attributes.put((Object)IMPORT_TASK_ID, importTaskId));
        return this.buildErrorOperationalEvent(errorEvent, attributes);
    }

    public EventDto buildErrorOperationalEventForSpaceUpload(ErrorEvent errorEvent, UploadDestinationType uploadDestinationType) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)SPACE_UPLOAD_TYPE, (Object)uploadDestinationType);
        return this.buildErrorOperationalEvent(errorEvent, attributes);
    }

    public EventDto buildErrorOperationalEventWithImportTaskId(ErrorEvent errorEvent, Optional<String> optionalImportTaskId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        optionalImportTaskId.ifPresent(importTaskId -> attributes.put((Object)IMPORT_TASK_ID, importTaskId));
        return this.buildErrorOperationalEvent(errorEvent, attributes);
    }

    private EventDto buildErrorOperationalEvent(ErrorEvent errorEvent, ImmutableMap.Builder<String, Object> initialAttributes) {
        ImmutableMap.Builder finalAttributes = new ImmutableMap.Builder();
        finalAttributes.putAll((Map)initialAttributes.build());
        long configuredHeapSize = Runtime.getRuntime().maxMemory();
        finalAttributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        finalAttributes.put((Object)ERROR_CODE_ATTRIBUTE_NAME, (Object)errorEvent.getErrorCode().getCode());
        finalAttributes.put((Object)REASON, (Object)AnalyticsMessageHandler.messageHandler(errorEvent.getReason()));
        finalAttributes.put((Object)CONFIGURED_HEAP_SIZE, (Object)Long.toString(configuredHeapSize));
        finalAttributes.put((Object)TYPE_ATTRIBUTE_NAME, (Object)errorEvent.getType().name());
        this.feedSpaceIdIfPresent(errorEvent.getSpaceKey(), spaceId -> finalAttributes.put((Object)SPACE_ID, (Object)Long.toString(spaceId)));
        this.addConfluenceAndDBVersionInAttributes((ImmutableMap.Builder<String, Object>)finalAttributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(LOGGED_ACTION_NAME)).sen(this.senSupplier.get())).source(KNOWN_SOURCE)).cloudId(errorEvent.getCloudId())).actionSubject(OBJECT_FAILURE_SUBJECT_NAME)).contextContainer(errorEvent.getContainerType().getName(), errorEvent.getContainerId())).withAttributes((Map)finalAttributes.build())).build();
    }

    public EventDto buildFeatureFlagsOperationalEvent(String enabledMigrationFeatures, FeatureFlagActionSubject actionSubject, String actionSubjectId) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)FEATURE_FLAG_ATTRIBUTE_NAME, (Object)enabledMigrationFeatures);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(FEATURE_FLAG_ACTION_ITEM)).sen(this.senSupplier.get())).actionSubject(actionSubject.getValue(), actionSubjectId)).withAttributes((Map)attributes.build())).build();
    }

    private Map<String, String> buildCounterMetricCommonTags() {
        HashMap<String, String> commonTagsMap = new HashMap<String, String>();
        commonTagsMap.put(PRODUCT_FAMILY, CONFLUENCE);
        commonTagsMap.put(MIGRATION_TYPE, S2C_MIGRATION);
        commonTagsMap.put(PRODUCT, CONFLUENCE);
        commonTagsMap.put(VERSION, this.pluginVersionManager.getPluginVersion());
        commonTagsMap.put(IS_INTERNAL_SEN, this.isInternalSenStr());
        return commonTagsMap;
    }

    private Map<String, String> buildPlatformCounterMetricCommonTags(ExecutionStatus status, MigrationTag migrationTag) {
        HashMap<String, String> commonTagsMap = new HashMap<String, String>();
        commonTagsMap.put(STATUS, this.convertToProgressStatus(status).getStatusName());
        commonTagsMap.put(IS_INTERNAL_SEN, this.isInternalSenStr());
        commonTagsMap.put(MIGRATION_TAG, migrationTag.name());
        return commonTagsMap;
    }

    public EventDto buildCreatedStepAnalyticsEvent(Step step) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderStepAnalyticsEvent(step, this.instantSupplier.get().toEpochMilli());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(CREATED)).sen(this.senSupplier.get())).actionSubject(STEP, step.getId())).contextContainer(PLAN, step.getPlan().getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildCompletedStepAnalyticsEvent(Step step) {
        return this.buildCompletedStepAnalyticsEvent(step, Collections::emptyMap);
    }

    public EventDto buildCompletedStepAnalyticsEvent(Step step, Supplier<Map<String, String>> additionalAttributes) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        Optional<Instant> optionalStartTime = step.getProgress().getStartTime();
        if (optionalStartTime.isPresent()) {
            startTime = optionalStartTime.get().toEpochMilli();
        }
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderStepAnalyticsEvent(step, startTime);
        attributes.put((Object)STATUS, (Object)step.getProgress().getStatus());
        attributes.put((Object)STOP_TIME, (Object)this.instantSupplier.get().toEpochMilli());
        additionalAttributes.get().entrySet().stream().forEach(entry -> attributes.put(entry.getKey(), entry.getValue()));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).sen(this.senSupplier.get())).actionSubject(STEP, step.getId())).contextContainer(PLAN, step.getPlan().getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildStuckStepAnalyticsEvent(Step step, Optional<StepAllocation> oldStepAllocation) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)STEP_TYPE, (Object)step.getType());
        attributes.put((Object)MIGRATION_ID, (Object)step.getPlan().getMigrationId());
        attributes.put((Object)TASK_ID, (Object)step.getTask().getId());
        attributes.put((Object)STATUS, (Object)step.getProgress().getStatus());
        attributes.put((Object)NODE_ID, (Object)step.getNodeId());
        attributes.put((Object)NODE_EXECUTION_ID, (Object)step.getNodeExecutionId());
        attributes.put((Object)EXECUTION_STATE, (Object)step.getExecutionState());
        attributes.put((Object)NODE_HEARTBEAT, (Object)step.getNodeHeartbeat().toString());
        if (oldStepAllocation.isPresent()) {
            StepAllocation stepAllocation = oldStepAllocation.get();
            attributes.put((Object)OLD_STEP_ALLOCATION_NODE_ID, (Object)stepAllocation.getNodeId());
            attributes.put((Object)OLD_STEP_ALLOCATION_NODE_EXECUTION_ID, (Object)stepAllocation.getNodeExecutionId());
        }
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("stuck")).sen(this.senSupplier.get())).actionSubject(STEP, step.getId())).contextContainer(PLAN, step.getPlan().getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildStepLevelHeapSizeAnalyticsEvent(Step step, long freeHeapSizeAtStart, int clusterConcurrency, int nodeConcurrency) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        long configuredHeapSize = Runtime.getRuntime().maxMemory();
        long freeHeapSize = Runtime.getRuntime().freeMemory();
        attributes.put((Object)STEP_TYPE, (Object)step.getType());
        attributes.put((Object)MIGRATION_ID, (Object)step.getPlan().getMigrationId());
        attributes.put((Object)TASK_ID, (Object)step.getTask().getId());
        attributes.put((Object)CONFIGURED_HEAP_SIZE, (Object)Long.toString(configuredHeapSize));
        attributes.put((Object)FREE_HEAP_SIZE_AT_START, (Object)Long.toString(freeHeapSizeAtStart));
        attributes.put((Object)USED_HEAP_SIZE, (Object)Long.toString(freeHeapSizeAtStart - freeHeapSize));
        attributes.put((Object)EXECUTOR_CONCURRENCY_CLUSTER, (Object)Integer.toString(clusterConcurrency));
        attributes.put((Object)EXECUTOR_CONCURRENCY_NODE, (Object)Integer.toString(nodeConcurrency));
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("fetched")).sen(this.senSupplier.get())).actionSubject("stepHeapSizeAnalytics", step.getId())).contextContainer(PLAN, step.getPlan().getId())).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildSpaceExportStartEvent(Step step, ExportType exportType, long spaceId, long startTime) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        stepBuilder.addAttribute(EXPORT_TYPE, (Object)exportType.name());
        stepBuilder.addAttribute(SPACE, (Object)spaceId);
        return stepBuilder.build();
    }

    public EventDto buildPlatformStepCompletionOperationalEvent(Step step, ExecutionStatus status) {
        String spaceKey;
        Space space;
        Plan plan = step.getPlan();
        boolean confluenceSpaceTask = step.getTask() instanceof ConfluenceSpaceTask;
        String stepType = step.getType();
        Optional<String> containerId = Optional.empty();
        String migrationId = plan.getMigrationId();
        if (confluenceSpaceTask && (space = this.spaceManager.getSpace(spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey())) != null) {
            containerId = Optional.of(String.valueOf(space.getId()));
        }
        String stepId = step.getId();
        String migrationScopeId = plan.getMigrationScopeId();
        String planId = plan.getId();
        String cloudId = plan.getCloudSite().getCloudId();
        MigrationTag migrationTag = plan.getMigrationTag();
        return this.buildPlatformStepCompletionOperationalEventUtil(new MigrationDetailsDto(migrationId, migrationScopeId, planId, cloudId, stepId), containerId, stepType, status, migrationTag);
    }

    public EventDto buildPlatformStepCompletionOperationalEventUtil(MigrationDetailsDto migrationDetailsDto, Optional<String> containerId, String stepType, ExecutionStatus status, MigrationTag migrationTag) {
        ImmutableMap.Builder<String, Object> attributes = this.getSourceDestinationLocation(migrationDetailsDto.cloudId);
        attributes.put((Object)PRODUCT_FAMILY, (Object)CONFLUENCE_PRODUCT_FAMILY);
        attributes.put((Object)MIGRATION_TYPE, (Object)S2C_MIGRATION);
        attributes.put((Object)PLATFORM_EVENT, (Object)true);
        attributes.put((Object)MIGRATION_ID, (Object)migrationDetailsDto.getMigrationId());
        if (containerId.isPresent()) {
            attributes.put((Object)"containerId", (Object)containerId.get());
        }
        attributes.put((Object)STEP_TYPE, (Object)stepType);
        attributes.put((Object)MIGRATION_TAG, (Object)migrationTag.name());
        attributes.put((Object)"transferId", (Object)migrationDetailsDto.getStepId());
        attributes.put((Object)"migrationScopeId", (Object)migrationDetailsDto.getMigrationScopeId());
        attributes.put((Object)PLAN_ID, (Object)migrationDetailsDto.getPlanId());
        attributes.put((Object)VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)STATUS, (Object)this.convertToProgressStatus(status).getStatusName());
        Map<String, String> eventAttributesMap = stepCompletionEventAttributesMap.get(stepType);
        for (String key : KEYS) {
            attributes.put((Object)key, (Object)this.getValueFromEventAttributesMap(eventAttributesMap, key));
        }
        this.addConfluenceAndDBVersionInAttributes(attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).actionSubject("transfer", migrationDetailsDto.getStepId())).contextContainer(MIGRATION, migrationDetailsDto.getMigrationId())).cloudId(migrationDetailsDto.getCloudId())).withAttributes((Map)attributes.build())).build();
    }

    private void addConfluenceAndDBVersionInAttributes(ImmutableMap.Builder<String, Object> attributes) {
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        attributes.put((Object)"database", (Object)ImmutableMap.of((Object)TYPE_ATTRIBUTE_NAME, (Object)databaseInfo.getName(), (Object)VERSION, (Object)databaseInfo.getVersion()));
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        attributes.put((Object)"confluenceVersion", (Object)confluenceInfo.getVersion());
    }

    private String getValueFromEventAttributesMap(Map<String, String> eventAttributesMap, String key) {
        if (eventAttributesMap != null) {
            return eventAttributesMap.getOrDefault(key, UNKNOWN);
        }
        return UNKNOWN;
    }

    public EventDto buildSpaceImportStartEvent(Step step, long startTime) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        return stepBuilder.build();
    }

    public EventDto buildAttachmentMigrationStartEvent(Step step, long startTime, int batchSize, long totalCountOfAttachments, long totalSizeOfAttachments) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        stepBuilder.addAttribute("batchSize", (Object)batchSize);
        stepBuilder.addAttribute("totalCountOfAttachments", (Object)totalCountOfAttachments);
        stepBuilder.addAttribute("totalSizeOfAttachments", (Object)totalSizeOfAttachments);
        return stepBuilder.build();
    }

    public EventDto buildUserMigrationStartEvent(Step step, long startTime, ImmutableMap.Builder<String, Object> attributes) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        stepBuilder.withAttributes((Map)attributes.build());
        return stepBuilder.build();
    }

    public EventDto buildSpaceUploadStartEvent(Step step, long startTime, UploadDestinationType uploadType, String fileId, String spaceKey) {
        GenericOperationalEvent.Builder stepBuilder = this.getBuilderStartedEvent(step, startTime);
        this.feedSpaceIdIfPresent(spaceKey, spaceId -> {
            GenericOperationalEvent.Builder cfr_ignored_0 = (GenericOperationalEvent.Builder)stepBuilder.addAttribute(SPACE, (Object)spaceId);
        });
        stepBuilder.addAttribute(FILE_ID, (Object)fileId);
        stepBuilder.addAttribute("uploadType", (Object)uploadType.name());
        return stepBuilder.build();
    }

    public EventDto buildPreflightNetworkHealth(boolean success, List<NetworkCheckResult> networkCheckResults, long totalTime) {
        return this.buildPreflightNetworkHealthEvent(success, networkCheckResults, totalTime, EXECUTED, NETWORK_HEALTH_CHECK_EVENT);
    }

    public EventDto buildPreMigrationPreflightNetworkHealth(boolean success, List<NetworkCheckResult> networkCheckResults, long totalTime) {
        String action = success ? EXECUTED : FAILED;
        return this.buildPreflightNetworkHealthEvent(success, networkCheckResults, totalTime, action, NETWORK_HEALTH_PRE_MIGRATION_CHECK_EVENT);
    }

    public EventDto buildUserDomainRulesCreatedEvent(int numRulesCreated, boolean success) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)SUCCESS, (Object)success);
        attributes.put((Object)"numRulesCreated", (Object)numRulesCreated);
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action("batchUserDomainRulesCreated")).sen(this.senSupplier.get())).actionSubject(USER_DOMAIN_RULE)).withAttributes((Map)attributes.build())).build();
    }

    @NotNull
    private GenericOperationalEvent buildPreflightNetworkHealthEvent(boolean success, List<NetworkCheckResult> networkCheckResults, long totalTime, String action, String actionSubject) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)NUMBER_OF_FAILED_DOMAINS, (Object)Long.toString(networkCheckResults.size()));
        Map<String, List> failedUrlsByServiceNameMap = networkCheckResults.stream().collect(Collectors.toMap(NetworkCheckResult::getName, NetworkCheckResult::getFailedDomains));
        attributes.put((Object)FAILED_DOMAINS_BY_SERVICE_NAME, failedUrlsByServiceNameMap);
        this.addCommonAttributesInPreflightCheck(success, totalTime, attributes);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).source(CHECK_SCREEN)).actionSubject(actionSubject)).withAttributes((Map)attributes.build())).build();
    }

    private GenericOperationalEvent.Builder getBuilderStartedEvent(Step step, long startTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderStepAnalyticsEvent(step, startTime);
        return (GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(STARTED)).sen(this.senSupplier.get())).actionSubject(STEP, step.getId())).contextContainer(PLAN, step.getPlan().getId())).withAttributes((Map)attributes.build());
    }

    private ImmutableMap.Builder<String, Object> getBuilderStepAnalyticsEvent(Step step, long startTime) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)STEP_TYPE, (Object)step.getType());
        attributes.put((Object)TASK_ID, (Object)step.getTask().getId());
        attributes.put((Object)MIGRATION_ID, (Object)step.getPlan().getMigrationId());
        attributes.put((Object)START_TIME, (Object)startTime);
        return attributes;
    }

    private ImmutableMap.Builder<String, Object> getSpaceStatisticBuilderWithCommonAttributes() {
        DatabaseInfo databaseInfo = this.systemInformationService.getDatabaseInfo();
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"database", (Object)ImmutableMap.of((Object)TYPE_ATTRIBUTE_NAME, (Object)databaseInfo.getName(), (Object)VERSION, (Object)databaseInfo.getVersion()));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)"confluenceVersion", (Object)this.systemInformationService.getConfluenceInfo().getVersion());
        return attributes;
    }

    private ImmutableMap.Builder<String, Object> getBuilderWithCommonAttributes() {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)PRODUCT_FAMILY, (Object)CONFLUENCE);
        attributes.put((Object)MIGRATION_TYPE, (Object)S2C_MIGRATION);
        return attributes;
    }

    private ProgressStatus convertToProgressStatus(ExecutionStatus execStatus) {
        switch (execStatus) {
            case DONE: {
                return ProgressStatus.SUCCESS;
            }
            case FAILED: {
                return ProgressStatus.FAILED;
            }
            case STOPPED: {
                return ProgressStatus.CANCELLED;
            }
            case INCOMPLETE: 
            case CREATED: 
            case VALIDATING: 
            case RUNNING: 
            case STOPPING: {
                return ProgressStatus.INCOMPLETE;
            }
        }
        throw new IllegalArgumentException("Unknown execution status " + execStatus.name());
    }

    private Boolean isInternalSen() {
        String sen = this.senSupplier.get();
        return StringUtils.isEmpty((String)sen) || sen.equals(INTERNAL_SEN);
    }

    private String isInternalSenStr() {
        return Boolean.toString(this.isInternalSen());
    }

    private void feedSpaceIdIfPresent(String spaceKey, LongConsumer spaceIdConsumer) {
        Space space = this.spaceManager.getSpace(spaceKey);
        if (Objects.nonNull(space)) {
            spaceIdConsumer.accept(space.getId());
        }
    }

    private void feedSpaceStatsIfPresent(String spaceKey, Consumer<SpaceStats> spaceStatsConsumer) {
        try {
            SpaceStats spaceStats = this.statisticsService.loadSpaceStatistics(spaceKey);
            if (Objects.nonNull(spaceStats)) {
                spaceStatsConsumer.accept(spaceStats);
            }
        }
        catch (Exception e) {
            log.error("Unable to retrieve space stats: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    private void feedSpaceStatsCollectionIfPresent(List<String> spaceKeys, Consumer<Collection<SpaceStats>> spaceStatsCollectionConsumer) {
        try {
            Collection<SpaceStats> spaceStatsCollection = this.statisticsService.loadSpaceStatistics(spaceKeys);
            if (Objects.nonNull(spaceStatsCollection)) {
                spaceStatsCollectionConsumer.accept(spaceStatsCollection);
            }
        }
        catch (Exception e) {
            log.error("Unable to retrieve space stats collections: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    public EventDto buildGlobalEntitiesConflictingExportStepEvent(Long numOfGlobalPageTemplatesMigrated, Long numOfSystemTemplatesMigrated, String planId, String taskId, String migrationId) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)"numOfSystemTemplatesMigrated", (Object)Long.toString(numOfSystemTemplatesMigrated));
        attributes.put((Object)"numOfGlobalPageTemplatesMigrated", (Object)Long.toString(numOfGlobalPageTemplatesMigrated));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        attributes.put((Object)MIGRATION_ID, (Object)migrationId);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(GLOBAL_ENTITIES_CSV_EXPORT_ACTION_NAME)).sen(this.senSupplier.get())).actionSubject(GLOBAL_ENTITY, taskId)).withAttributes((Map)attributes.build())).contextContainer(PLAN, planId)).build();
    }

    public EventDto buildMapiJobOperationalEvent(String mapiJobId, Optional<PlanDto> planDto, int statusCode, String errorReason, long totalTime, String action) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)PLAN_ID, (Object)planDto.map(PlanDto::getId).orElse(""));
        attributes.put((Object)MIGRATION_ID, (Object)planDto.map(PlanDto::getMigrationId).orElse(""));
        attributes.put((Object)STATUS_CODE, (Object)statusCode);
        attributes.put((Object)REASON, (Object)AnalyticsMessageHandler.messageHandler(errorReason));
        attributes.put((Object)TIME_TAKEN, (Object)Long.toString(totalTime));
        attributes.put((Object)PLUGIN_VERSION, (Object)this.pluginVersionManager.getPluginVersion());
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(action)).sen(this.senSupplier.get())).actionSubject(MAPI_JOB_ACTION_SUBJECT, mapiJobId)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildMapiJobTimerMetricEvent(int statusCode, Long totalTime, String metricName) {
        HashMap<String, String> tagsMap = new HashMap<String, String>();
        tagsMap.put(STATUS_CODE, Integer.toString(statusCode));
        tagsMap.put(IS_INTERNAL_SEN, this.isInternalSenStr());
        tagsMap.put(PLUGIN_VERSION, this.pluginVersionManager.getPluginVersion());
        return ((TimerMetricEvent.Builder)new TimerMetricEvent.Builder(metricName, totalTime).tags(tagsMap)).build();
    }

    public EventDto buildUpdatedCloudTypeSettingsAnalyticEvent(String settings) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)"cloudTypeSettings", (Object)settings);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(CLOUD_TYPE_SETTINGS_ACTION_NAME)).sen(this.senSupplier.get())).actionSubject("migrationSettings")).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildRelationsAnalysisJobFinishedEvent(long durationMs, int numberOfNodes, int numberOfRelations) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"durationMs", (Object)durationMs);
        attributes.put((Object)"numberOfNodes", (Object)numberOfNodes);
        attributes.put((Object)"numberOfRelations", (Object)numberOfRelations);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(COMPLETED)).sen(this.senSupplier.get())).source(RELATIONS_ANALYSER)).actionSubject(RELATIONS_ANALYSIS_JOB)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildRelationsAnalysisJobFailedEvent(long durationMs) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)"durationMs", (Object)durationMs);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(FAILED)).sen(this.senSupplier.get())).source(RELATIONS_ANALYSER)).actionSubject(RELATIONS_ANALYSIS_JOB)).withAttributes((Map)attributes.build())).build();
    }

    public EventDto buildTokenEncryptionFailureAnalyticEvent(String reason) {
        ImmutableMap.Builder<String, Object> attributes = this.getBuilderWithCommonAttributes();
        attributes.put((Object)REASON, (Object)reason);
        return ((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)((GenericOperationalEvent.Builder)new GenericOperationalEvent.Builder(this.instantSupplier.get().toEpochMilli()).action(TOKEN_ENCRYPTION_ACTION_NAME)).sen(this.senSupplier.get())).actionSubject(FAILED)).withAttributes((Map)attributes.build())).build();
    }
}

