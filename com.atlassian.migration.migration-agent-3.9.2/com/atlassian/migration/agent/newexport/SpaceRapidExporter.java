/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.newexport.DescriptorBuilder;
import com.atlassian.migration.agent.newexport.Queries;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.RapidExporter;
import com.atlassian.migration.agent.newexport.SpaceCSVExportTaskContext;
import com.atlassian.migration.agent.newexport.processor.RowProcessor;
import com.atlassian.migration.agent.newexport.processor.UserKeyXmlExtractor;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.newexport.util.FileUtil;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.UserMappingsManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.google.common.collect.ImmutableMap;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceRapidExporter
extends RapidExporter<SpaceCSVExportTaskContext> {
    private static final Logger log = LoggerFactory.getLogger(SpaceRapidExporter.class);
    @VisibleForTesting
    static final String MIG_CONTENT_ID_TABLE_NAME_PREFIX = "mig_contentid_";
    @VisibleForTesting
    static final String MIG_CALENDAR_ID_TABLE_NAME_PREFIX = "mig_calendarid_";
    private static final int MAX_CHARS_ALLOWED_TABLE_NAME = 30;
    private static final String ENTITY_NAME = "space";
    private static final String SPACE_ID = "spaceid";
    private static final String CLOUD_ID = "cloudid";
    private static final String SPACE_KEY = "spaceKey";
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final TeamCalendarHelper teamCalendarHelper;

    public SpaceRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, TeamCalendarHelper teamCalendarHelper, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        super(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder);
        this.teamCalendarHelper = teamCalendarHelper;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @VisibleForTesting
    public SpaceRapidExporter(JdbcConfluenceStore confluenceStore, DescriptorBuilder descriptorBuilder, MigrationAgentConfiguration migrationAgentConfiguration, UserMappingsFileManager userMappingsFileManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Supplier<Instant> instantSupplier, TeamCalendarHelper teamCalendarHelper, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        super(confluenceStore, descriptorBuilder, migrationAgentConfiguration, userMappingsFileManager, analyticsEventService, analyticsEventBuilder, instantSupplier);
        this.teamCalendarHelper = teamCalendarHelper;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @Override
    protected void logResults(String tableName, SpaceCSVExportTaskContext taskContext, long totalTime) {
        log.info("Serialized results of query on table [{}] for space [{}] in [{} ms].", new Object[]{tableName, taskContext.getSpaceKey(), totalTime});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String export(SpaceCSVExportTaskContext taskContext) throws AccessDeniedException {
        String exportDir = FileUtil.createExportDirectory(taskContext.getSpaceKey(), taskContext.getTempDirFilePath());
        log.info("Starting CSV export of space: {} to path: {}", (Object)taskContext.getSpaceKey(), (Object)exportDir);
        long startTime = ((Instant)this.instantSupplier.get()).toEpochMilli();
        HashSet<String> discoveredUserKeys = new HashSet<String>();
        String contentIdTable = this.createAndPopulateContentIdsTable(taskContext.getSpaceId());
        Optional<Object> optionalTeamCalendarIdTable = Optional.empty();
        log.info("Try to get user mappings from file for planId: {}", (Object)taskContext.getPlanId());
        Map<String, String> mappings = this.userMappingsFileManager.getUserMappingsFromFile(taskContext.getPlanId());
        UserMappingsManager userMappingsManager = new UserMappingsManager(this.migrationDarkFeaturesManager, mappings);
        try {
            this.exportEntitiesDirectlyRelatedToSpace(exportDir, discoveredUserKeys, taskContext);
            this.exportBodyContents(exportDir, discoveredUserKeys, taskContext, contentIdTable);
            this.exportContentRelatedEntities(exportDir, discoveredUserKeys, taskContext, contentIdTable);
            this.exportOsProperties(exportDir, taskContext, contentIdTable);
            if (this.teamCalendarHelper.includeTeamCalendar()) {
                long tcExportStartTime = Instant.now().toEpochMilli();
                String teamCalendarIdTable = this.createAndPopulateTeamCalendarIdsTable(taskContext.getSpaceKey());
                optionalTeamCalendarIdTable = Optional.of(teamCalendarIdTable);
                this.exportTeamCalendars(exportDir, discoveredUserKeys, taskContext, teamCalendarIdTable);
                long tcExportEndTime = Instant.now().toEpochMilli();
                EventDto timerEvent = this.analyticsEventBuilder.buildTeamCalendarExportTimeEvent(tcExportEndTime - tcExportStartTime, taskContext.getSpaceId(), taskContext.getPlanId(), taskContext.getTaskId());
                this.analyticsEventService.saveAnalyticsEventAsync(() -> timerEvent);
            }
            this.exportUserMappings(exportDir, discoveredUserKeys, taskContext, userMappingsManager);
            this.exportFileCount(exportDir, taskContext, ENTITY_NAME);
            this.descriptorBuilder.generateSpaceDescriptor(taskContext.getSpaceKey(), exportDir, taskContext.isUsersCreatedInUMS(), taskContext.getTotalRowCount());
        }
        finally {
            this.tryDropIdsTable(contentIdTable);
            optionalTeamCalendarIdTable.ifPresent(this::tryDropIdsTable);
        }
        log.info("Completed CSV export of space: {} in {}ms", (Object)taskContext.getSpaceKey(), (Object)(((Instant)this.instantSupplier.get()).toEpochMilli() - startTime));
        log.info("Exported CSV files of space: {} are located in directory {}", (Object)taskContext.getSpaceKey(), (Object)exportDir);
        return exportDir;
    }

    private String createAndPopulateContentIdsTable(long spaceId) {
        String contentIdsTableName = this.defaultNameGenerator(MIG_CONTENT_ID_TABLE_NAME_PREFIX);
        this.confluenceStore.mutate(Queries.CREATE_CONTENT_ID_TABLE_QUERY.toQuery(this.getDbType(), contentIdsTableName));
        log.info("Created contentId table {}", (Object)contentIdsTableName);
        int numRows = this.confluenceStore.mutate(Queries.POPULATE_CONTENT_ID_TABLE_QUERY.toQuery(this.getDbType(), contentIdsTableName), Collections.singletonMap(SPACE_ID, spaceId));
        log.info("Populated contentId table {} with {} rows", (Object)contentIdsTableName, (Object)numRows);
        return contentIdsTableName;
    }

    private String createAndPopulateTeamCalendarIdsTable(String spaceKey) {
        String teamCalendarIdsTableName = this.defaultNameGenerator(MIG_CALENDAR_ID_TABLE_NAME_PREFIX);
        this.confluenceStore.mutate(Queries.CREATE_TABLE_FOR_TEAM_CALENDAR_IDS_QUERY.toQuery(this.getDbType(), teamCalendarIdsTableName));
        log.info("Created calendarIds table {}", (Object)teamCalendarIdsTableName);
        int numRows = this.confluenceStore.mutate(Queries.POPULATE_TEAM_CALENDAR_ID_TABLE_QUERY.toQuery(this.getDbType(), teamCalendarIdsTableName), Collections.singletonMap(SPACE_KEY, spaceKey));
        log.info("Populated team calendar ids table {} with {} rows", (Object)teamCalendarIdsTableName, (Object)numRows);
        return teamCalendarIdsTableName;
    }

    @VisibleForTesting
    String defaultNameGenerator(String tableName) {
        return tableName + RandomStringUtils.random((int)(30 - tableName.length()), (boolean)true, (boolean)true);
    }

    private void tryDropIdsTable(String tableName) {
        try {
            this.confluenceStore.mutate(Queries.DROP_TABLE_FOR_IDS_QUERY.toQuery(this.getDbType(), tableName, tableName));
        }
        catch (Exception e) {
            log.warn("Failed to drop CSV export temp table: {}", (Object)tableName, (Object)e);
        }
    }

    private void exportOsProperties(String exportDir, SpaceCSVExportTaskContext taskConfig, String contentIdTable) {
        this.runQueries(Queries.osPropertyQuery.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId())), exportDir, taskConfig);
    }

    private void exportContentRelatedEntities(String exportDir, Set<String> extractedUserKeys, SpaceCSVExportTaskContext taskConfig, String contentIdTable) {
        this.runQueries(Queries.contentQueries.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner((Map<String, ?>)ImmutableMap.of((Object)CLOUD_ID, (Object)taskConfig.getCloudId(), (Object)SPACE_ID, (Object)taskConfig.getSpaceId())), exportDir, taskConfig);
        if (this.confluenceBefore7_0_1()) {
            this.runQueries(Queries.contentQueriesLegacyTables.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId())), exportDir, taskConfig);
        }
        this.runQueries(Queries.contentQueriesWithUserKeys.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        if (this.migrationDarkFeaturesManager.disableScopedGroupMigration()) {
            this.runQueries(Queries.CONTENT_PERM_ONLY_USER.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        } else {
            this.runQueries(Queries.CONTENT_PERM_ALL.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        }
        this.runQueries(Queries.AOBAF3AA_QUERY.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
    }

    private void exportEntitiesDirectlyRelatedToSpace(String exportDir, Set<String> extractedUserKeys, SpaceCSVExportTaskContext taskConfig) {
        this.runQueries(Queries.spaceIdQueries, this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.spaceKeyQueries.toQueries(this.getDbType(), new String[0]), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        if (this.migrationDarkFeaturesManager.disableScopedGroupMigration()) {
            this.runQueries(Queries.NO_GROUPS_AND_USERS_SPACE_PERMISSION_QUERIES, this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        } else {
            this.runQueries(Queries.ALL_SPACE_PERMISSION_QUERIES, this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getSpaceId()), extractedUserKeys), exportDir, taskConfig);
        }
        this.runQueries(Queries.AO187_QUERY.toQueries(this.getDbType(), new String[0]), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
    }

    private void exportBodyContents(String exportDir, Set<String> extractedUserKeys, SpaceCSVExportTaskContext taskConfig, String contentIdTable) {
        this.runQueries(Queries.bodycontentQueriesWithUsersInContent.toQueries(this.getDbType(), contentIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_ID, taskConfig.getTaskId()), (Query query, RowProcessor processor) -> new UserKeyXmlExtractor((RowProcessor)processor, extractedUserKeys)), exportDir, taskConfig);
    }

    private void exportTeamCalendars(String exportDir, Set<String> extractedUserKeys, SpaceCSVExportTaskContext taskConfig, String teamCalendarIdTable) {
        this.runQueries(Queries.TC_SUBCALS_IN_SPACE.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_SUBCALS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_SUBCALS_PROPS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.exportTCEventEntities(exportDir, extractedUserKeys, taskConfig, teamCalendarIdTable);
        this.runQueries(Queries.TC_SUBCALS_PRIV_USR.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_SUBCALS_PRIV_GRP.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
    }

    private void exportTCEventEntities(String exportDir, Set<String> extractedUserKeys, SpaceCSVExportTaskContext taskConfig, String teamCalendarIdTable) {
        this.runQueries(Queries.TC_EVENTS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_EVENTS_EXCL.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_EVENTS_INVITEES.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_CUSTOM_EV_TYPES.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_DISABLE_EV_TYPES.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_JIRA_REMI_EVENTS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_REMINDER_SETTINGS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
        this.runQueries(Queries.TC_REMINDER_USERS.toQueries(this.getDbType(), teamCalendarIdTable), this.createQueryRunner(Collections.singletonMap(SPACE_KEY, taskConfig.getSpaceKey()), extractedUserKeys), exportDir, taskConfig);
    }

    private boolean confluenceBefore7_0_1() {
        return this.descriptorBuilder.getBuildNumber() < 8201;
    }

    @Override
    public void reportExportTablePerformance(SpaceCSVExportTaskContext taskContext, boolean success, String tableName, String query, String dbType, long totalTimeTaken, long timeToFirstRow, long rowsExported, long totalContentChars) {
        EventDto tableExportEvent = this.analyticsEventBuilder.buildSpaceTableExportedToCSVTimerEvent(success, totalTimeTaken, taskContext.getSpaceKey(), taskContext.getPlanId(), taskContext.getTaskId(), tableName, query, dbType, timeToFirstRow, rowsExported, totalContentChars);
        this.analyticsEventService.saveAnalyticsEventAsync(() -> tableExportEvent);
    }
}

