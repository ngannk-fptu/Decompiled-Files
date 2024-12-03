/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.SpaceStatistic;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Queries;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.processor.SpaceStatisticCalculationProcessor;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationAnalyticService;
import com.atlassian.migration.agent.store.SpaceStatisticStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.scheduler.config.JobId;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStatisticCalculationService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticCalculationService.class);
    private final SpaceStatisticStore spaceStatisticStore;
    private final JdbcConfluenceStore confluenceStore;
    private final EntityManagerTemplate entityManagerTemplate;
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final ExecutorService executorService;
    private final PluginTransactionTemplate pluginTransactionTemplate;
    private final SpaceStatisticCalculationAnalyticService analyticService;
    private final TeamCalendarHelper teamCalendarHelper;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;
    private final Supplier<Instant> instantSupplier;

    public SpaceStatisticCalculationService(SpaceStatisticStore spaceStatisticStore, JdbcConfluenceStore confluenceStore, EntityManagerTemplate entityManagerTemplate, PluginTransactionTemplate pluginTransactionTemplate, MigrationAgentConfiguration migrationAgentConfiguration, SpaceStatisticCalculationAnalyticService spaceStatisticCalculationAnalyticService, TeamCalendarHelper teamCalendarHelper, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        this(spaceStatisticStore, confluenceStore, entityManagerTemplate, pluginTransactionTemplate, migrationAgentConfiguration, Executors.newSingleThreadExecutor(), spaceStatisticCalculationAnalyticService, teamCalendarHelper, migrationTimeEstimationUtils, Instant::now);
    }

    @VisibleForTesting
    SpaceStatisticCalculationService(SpaceStatisticStore spaceStatisticStore, JdbcConfluenceStore confluenceStore, EntityManagerTemplate entityManagerTemplate, PluginTransactionTemplate pluginTransactionTemplate, MigrationAgentConfiguration migrationAgentConfiguration, ExecutorService executorService, SpaceStatisticCalculationAnalyticService spaceStatisticCalculationAnalyticService, TeamCalendarHelper teamCalendarHelper, MigrationTimeEstimationUtils migrationTimeEstimationUtils, Supplier<Instant> instantSupplier) {
        this.spaceStatisticStore = spaceStatisticStore;
        this.confluenceStore = confluenceStore;
        this.entityManagerTemplate = entityManagerTemplate;
        this.pluginTransactionTemplate = pluginTransactionTemplate;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.analyticService = spaceStatisticCalculationAnalyticService;
        this.teamCalendarHelper = teamCalendarHelper;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
        this.executorService = executorService;
        this.instantSupplier = instantSupplier;
    }

    private String buildQueryWithoutHistoricalData() {
        String calculateSpaceStatsTemplate = this.includeTeamCalendarStats() ? "select \n     sb.spaceId,\n     COALESCE(csq.sumOfPageBlogDraftCount, 0)  as sumOfPageBlogDraftCount,\n     COALESCE(csq.attachmentCount, 0)  as attachmentCount,\n     COALESCE(asq.attachmentSize, 0) as attachmentSize,\n     COALESCE(tcsq.teamCalendarCount, 0) as teamCalendarCount,\n     CASE\n          WHEN sb.lastUpdated is null then csq.lastUpdated\n          WHEN csq.lastUpdated is null then sb.lastUpdated\n          WHEN csq.lastUpdated > sb.lastUpdated then csq.lastUpdated\n          ELSE sb.lastUpdated\n       END as lastUpdated,\n     CURRENT_TIMESTAMP as lastCalculated\n from ( \n   select LASTMODDATE as lastUpdated, SPACEID as spaceId from SPACES where SPACEID in (:spaceIds)\n ) sb\n left join\n         (select SUM(CASE WHEN c.CONTENTTYPE in ('PAGE', 'BLOGPOST') THEN 1 ELSE 0 END) as sumOfPageBlogDraftCount,\n              SUM(CASE WHEN c.CONTENTTYPE = 'ATTACHMENT' THEN 1 ELSE 0 END)      as attachmentCount,\n              MAX(LASTMODDATE)                                                   as lastUpdated,\n              SPACEID                                                            as spaceId\nCONTENT_TABLE_PLACE_HOLDER ) csq  on csq.spaceId = sb.spaceId\n left join\n      (select SUM(cp.LONGVAL) as attachmentSize, c.SPACEID as spaceId\n          from CONTENT c\n                   join CONTENTPROPERTIES cp on c.CONTENTID = cp.CONTENTID\n          where cp.PROPERTYNAME = 'FILESIZE'\n            and c.CONTENTTYPE = 'ATTACHMENT'\n            and SPACEID in (:spaceIds)\n          group by c.SPACEID) asq on asq.spaceId = sb.spaceId\n left join\n      (TEAM_CALENDAR_TABLE_PLACEHOLDER ) tcsq on tcsq.spaceId = sb.spaceId".replace("TEAM_CALENDAR_TABLE_PLACEHOLDER", Queries.SPACE_TEAM_CALENDAR_STATS_QUERY.query(this.migrationAgentConfiguration.getDBType())) : "select \n     sb.spaceId,\n     COALESCE(csq.sumOfPageBlogDraftCount, 0)  as sumOfPageBlogDraftCount,\n     COALESCE(csq.attachmentCount, 0)  as attachmentCount,\n     COALESCE(asq.attachmentSize, 0) as attachmentSize,\n     CASE\n          WHEN sb.lastUpdated is null then csq.lastUpdated\n          WHEN csq.lastUpdated is null then sb.lastUpdated\n          WHEN csq.lastUpdated > sb.lastUpdated then csq.lastUpdated\n          ELSE sb.lastUpdated\n       END as lastUpdated,\n     CURRENT_TIMESTAMP as lastCalculated\n from ( \n   select LASTMODDATE as lastUpdated, SPACEID as spaceId from SPACES where SPACEID in (:spaceIds)\n ) sb\n left join\n         (select SUM(CASE WHEN c.CONTENTTYPE in ('PAGE', 'BLOGPOST') THEN 1 ELSE 0 END) as sumOfPageBlogDraftCount,\n              SUM(CASE WHEN c.CONTENTTYPE = 'ATTACHMENT' THEN 1 ELSE 0 END)      as attachmentCount,\n              MAX(LASTMODDATE)                                                   as lastUpdated,\n              SPACEID                                                            as spaceId\nCONTENT_TABLE_PLACE_HOLDER ) csq  on csq.spaceId = sb.spaceId\n left join\n      (select SUM(cp.LONGVAL) as attachmentSize, c.SPACEID as spaceId\n          from CONTENT c\n                   join CONTENTPROPERTIES cp on c.CONTENTID = cp.CONTENTID\n          where cp.PROPERTYNAME = 'FILESIZE'\n            and c.CONTENTTYPE = 'ATTACHMENT'\n            and SPACEID in (:spaceIds)\n          group by c.SPACEID) asq on asq.spaceId = sb.spaceId";
        return calculateSpaceStatsTemplate.replace("CONTENT_TABLE_PLACE_HOLDER", " from CONTENT c\n where c.SPACEID in (:spaceIds)\n group by c.SPACEID \n");
    }

    private String buildQueryIncludingHistoricalPages() {
        String calculateSpaceStatsTemplate = this.includeTeamCalendarStats() ? "select \n     sb.spaceId,\n     COALESCE(csq.sumOfPageBlogDraftCount, 0)  as sumOfPageBlogDraftCount,\n     COALESCE(csq.attachmentCount, 0)  as attachmentCount,\n     COALESCE(asq.attachmentSize, 0) as attachmentSize,\n     COALESCE(tcsq.teamCalendarCount, 0) as teamCalendarCount,\n     CASE\n          WHEN sb.lastUpdated is null then csq.lastUpdated\n          WHEN csq.lastUpdated is null then sb.lastUpdated\n          WHEN csq.lastUpdated > sb.lastUpdated then csq.lastUpdated\n          ELSE sb.lastUpdated\n       END as lastUpdated,\n     CURRENT_TIMESTAMP as lastCalculated\n from ( \n   select LASTMODDATE as lastUpdated, SPACEID as spaceId from SPACES where SPACEID in (:spaceIds)\n ) sb\n left join\n         (select SUM(CASE WHEN c.CONTENTTYPE in ('PAGE', 'BLOGPOST') THEN 1 ELSE 0 END) as sumOfPageBlogDraftCount,\n              SUM(CASE WHEN c.CONTENTTYPE = 'ATTACHMENT' THEN 1 ELSE 0 END)      as attachmentCount,\n              MAX(LASTMODDATE)                                                   as lastUpdated,\n              SPACEID                                                            as spaceId\nCONTENT_TABLE_PLACE_HOLDER ) csq  on csq.spaceId = sb.spaceId\n left join\n      (select SUM(cp.LONGVAL) as attachmentSize, c.SPACEID as spaceId\n          from CONTENT c\n                   join CONTENTPROPERTIES cp on c.CONTENTID = cp.CONTENTID\n          where cp.PROPERTYNAME = 'FILESIZE'\n            and c.CONTENTTYPE = 'ATTACHMENT'\n            and SPACEID in (:spaceIds)\n          group by c.SPACEID) asq on asq.spaceId = sb.spaceId\n left join\n      (TEAM_CALENDAR_TABLE_PLACEHOLDER ) tcsq on tcsq.spaceId = sb.spaceId".replace("TEAM_CALENDAR_TABLE_PLACEHOLDER", Queries.SPACE_TEAM_CALENDAR_STATS_QUERY.query(this.migrationAgentConfiguration.getDBType())) : "select \n     sb.spaceId,\n     COALESCE(csq.sumOfPageBlogDraftCount, 0)  as sumOfPageBlogDraftCount,\n     COALESCE(csq.attachmentCount, 0)  as attachmentCount,\n     COALESCE(asq.attachmentSize, 0) as attachmentSize,\n     CASE\n          WHEN sb.lastUpdated is null then csq.lastUpdated\n          WHEN csq.lastUpdated is null then sb.lastUpdated\n          WHEN csq.lastUpdated > sb.lastUpdated then csq.lastUpdated\n          ELSE sb.lastUpdated\n       END as lastUpdated,\n     CURRENT_TIMESTAMP as lastCalculated\n from ( \n   select LASTMODDATE as lastUpdated, SPACEID as spaceId from SPACES where SPACEID in (:spaceIds)\n ) sb\n left join\n         (select SUM(CASE WHEN c.CONTENTTYPE in ('PAGE', 'BLOGPOST') THEN 1 ELSE 0 END) as sumOfPageBlogDraftCount,\n              SUM(CASE WHEN c.CONTENTTYPE = 'ATTACHMENT' THEN 1 ELSE 0 END)      as attachmentCount,\n              MAX(LASTMODDATE)                                                   as lastUpdated,\n              SPACEID                                                            as spaceId\nCONTENT_TABLE_PLACE_HOLDER ) csq  on csq.spaceId = sb.spaceId\n left join\n      (select SUM(cp.LONGVAL) as attachmentSize, c.SPACEID as spaceId\n          from CONTENT c\n                   join CONTENTPROPERTIES cp on c.CONTENTID = cp.CONTENTID\n          where cp.PROPERTYNAME = 'FILESIZE'\n            and c.CONTENTTYPE = 'ATTACHMENT'\n            and SPACEID in (:spaceIds)\n          group by c.SPACEID) asq on asq.spaceId = sb.spaceId";
        String calculateSpaceStatsWithHistoricalPages = this.includeTeamCalendarStats() ? "with ContentBase as (select SPACEID, CONTENTID, LASTMODDATE, PREVVER, CONTENTTYPE\n              from CONTENT\n              where SPACEID in (:spaceIds)\n                and CONTENTTYPE in ('PAGE', 'BLOGPOST', 'ATTACHMENT', 'SPACEDESCRIPTION')),\n     SpacesBase as (select SPACEID, LASTMODDATE from SPACES where SPACEID in (:spaceIds)),\n     AllPageContent as (select SPACEID, CONTENTID, LASTMODDATE\n                        from ContentBase\n                        where CONTENTTYPE in ('PAGE', 'BLOGPOST')\n                        union\n                        select cb.SPACEID, c.CONTENTID, c.LASTMODDATE\n                        from CONTENT c\n                                 inner join ContentBase cb on cb.CONTENTID = c.PREVVER\n                                                      and c.SPACEID is null\n                                                     and c.PREVVER in (select CONTENTID from ContentBase where ContentBase.CONTENTTYPE in ('PAGE', 'BLOGPOST'))),\n     PageStat as (select count(*) as sumOfPageBlogDraftCount, SPACEID\n                  from AllPageContent\n                  group by SPACEID),\n     AttachmentStat as (select SUM(cp.LONGVAL) as attachmentSize, count(SPACEID) as attachmentCount, SPACEID\n                        from ContentBase cb\n                        join CONTENTPROPERTIES cp on cb.CONTENTID = cp.CONTENTID\n                        where cp.PROPERTYNAME = 'FILESIZE' and cb.CONTENTTYPE = 'ATTACHMENT'\n                        group by SPACEID),\n    TeamCalendarStat as (TEAM_CALENDAR_TABLE_PLACEHOLDER),\n     RecentEdit as (select (CASE\n                                WHEN MAX(sb.LASTMODDATE) is null then MAX(cb.LASTMODDATE)\n                                WHEN MAX(cb.LASTMODDATE) is null then  MAX(sb.LASTMODDATE)\n                                WHEN  MAX(sb.LASTMODDATE) > MAX(cb.LASTMODDATE) then MAX(cb.LASTMODDATE)\n                                ELSE MAX(cb.LASTMODDATE)\n         END) as lastUpdated,\n                                  sb.SPACEID as SPACEID\n                           from ContentBase cb\n                           join SpacesBase sb on sb.SPACEID = cb.SPACEID\n                           group by sb.SPACEID)\nselect sb.SPACEID as spaceId, COALESCE(sumOfPageBlogDraftCount, 0) as sumOfPageBlogDraftCount, COALESCE(attachmentCount, 0) as attachmentCount, COALESCE(attachmentSize, 0) as attachmentSize, COALESCE(teamCalendarCount, 0) as teamCalendarCount, lastUpdated, CURRENT_TIMESTAMP as lastCalculated\nfrom SpacesBase sb\n         left join PageStat p on p.SPACEID = sb.SPACEID\n         left join AttachmentStat a on a.SPACEID = sb.SPACEID\n         left join TeamCalendarStat tc on tc.spaceId = sb.SPACEID\n         left join RecentEdit r on r.SPACEID = sb.SPACEID".replace("TEAM_CALENDAR_TABLE_PLACEHOLDER", Queries.SPACE_TEAM_CALENDAR_STATS_QUERY.query(this.migrationAgentConfiguration.getDBType())) : "with ContentBase as (select SPACEID, CONTENTID, LASTMODDATE, PREVVER, CONTENTTYPE\n              from CONTENT\n              where SPACEID in (:spaceIds)\n                and CONTENTTYPE in ('PAGE', 'BLOGPOST', 'ATTACHMENT', 'SPACEDESCRIPTION')),\n     SpacesBase as (select SPACEID, LASTMODDATE from SPACES where SPACEID in (:spaceIds)),\n     AllPageContent as (select SPACEID, CONTENTID, LASTMODDATE\n                        from ContentBase\n                        where CONTENTTYPE in ('PAGE', 'BLOGPOST')\n                        union\n                        select cb.SPACEID, c.CONTENTID, c.LASTMODDATE\n                        from CONTENT c\n                                 inner join ContentBase cb on cb.CONTENTID = c.PREVVER\n                                                      and c.SPACEID is null\n                                                     and c.PREVVER in (select CONTENTID from ContentBase where ContentBase.CONTENTTYPE in ('PAGE', 'BLOGPOST'))),\n     PageStat as (select count(*) as sumOfPageBlogDraftCount, SPACEID\n                  from AllPageContent\n                  group by SPACEID),\n     AttachmentStat as (select SUM(cp.LONGVAL) as attachmentSize, count(SPACEID) as attachmentCount, SPACEID\n                        from ContentBase cb\n                        join CONTENTPROPERTIES cp on cb.CONTENTID = cp.CONTENTID\n                        where cp.PROPERTYNAME = 'FILESIZE' and cb.CONTENTTYPE = 'ATTACHMENT'\n                        group by SPACEID),\n     RecentEdit as (select (CASE\n                                WHEN MAX(sb.LASTMODDATE) is null then MAX(cb.LASTMODDATE)\n                                WHEN MAX(cb.LASTMODDATE) is null then  MAX(sb.LASTMODDATE)\n                                WHEN  MAX(sb.LASTMODDATE) > MAX(cb.LASTMODDATE) then MAX(cb.LASTMODDATE)\n                                ELSE MAX(cb.LASTMODDATE)\n         END) as lastUpdated,\n                                  sb.SPACEID as SPACEID\n                           from ContentBase cb\n                           join SpacesBase sb on sb.SPACEID = cb.SPACEID\n                           group by sb.SPACEID)\nselect sb.SPACEID as spaceId, COALESCE(sumOfPageBlogDraftCount, 0) as sumOfPageBlogDraftCount, COALESCE(attachmentCount, 0) as attachmentCount, COALESCE(attachmentSize, 0) as attachmentSize, lastUpdated, CURRENT_TIMESTAMP as lastCalculated\nfrom SpacesBase sb\n         left join PageStat p on p.SPACEID = sb.SPACEID\n         left join AttachmentStat a on a.SPACEID = sb.SPACEID\n         left join RecentEdit r on r.SPACEID = sb.SPACEID";
        return this.migrationAgentConfiguration.getDBType().equals((Object)DbType.MYSQL) || this.migrationAgentConfiguration.getDBType().equals((Object)DbType.H2) ? calculateSpaceStatsTemplate.replace("CONTENT_TABLE_PLACE_HOLDER", "from (\nselect CONTENTID, CONTENTTYPE, SPACEID, LASTMODDATE\n             from CONTENT\n             where SPACEID in\n                   (:spaceIds)\n             union\n             select historicalContent.contentId , historicalContent.contentType, historicalContent.spaceId, historicalContent.lastModDate\n             from (select a.CONTENTID as contentId, a.CONTENTTYPE as contentType, b.SPACEID as spaceId, a.LASTMODDATE as lastModDate\n                   from CONTENT a\n                   inner join CONTENT b on a.PREVVER = b.CONTENTID\n                   where a.PREVVER in (select CONTENTID\n                                       from CONTENT\n                                       where SPACEID in\n                                             (:spaceIds)\n                                         )\n                         and a.SPACEID is NULL\n                     ) historicalContent) c\n       group by c.SPACEID\n") : calculateSpaceStatsWithHistoricalPages;
    }

    @PreDestroy
    @VisibleForTesting
    void cleanup() {
        this.executorService.shutdown();
    }

    public void runSpaceStatisticCalculationIfEmptyOrMissingSpaces(JobId jobId, boolean awaitResult) {
        List<Long> spaceIds = this.getSpaceIdsForStatisticCalculation();
        if (!spaceIds.isEmpty()) {
            log.info("Running {} spaces without space statistics", (Object)spaceIds.size());
            this.spinupJobsToRunSpaceStaticCalculations(jobId.toString(), true, spaceIds, awaitResult);
        } else {
            log.info("Skipped initial space statistic calculation.");
        }
    }

    public void runSpaceStatisticCalculationForSpaceIds(JobId jobId, boolean includeHistoricalData, List<Long> spaceIds, boolean awaitResult) {
        this.spinupJobsToRunSpaceStaticCalculations(jobId.toString(), includeHistoricalData, spaceIds, awaitResult);
    }

    public void runSpaceStatisticCalculation(JobId jobId, boolean includeHistoricalData, boolean awaitResult) {
        this.spinupJobsToRunSpaceStaticCalculations(jobId.toString(), includeHistoricalData, this.getSpaceIds(), awaitResult);
    }

    public void createSpaceStatistic(Long spaceId, Date lastModified) {
        log.info("Creating statistic for spaceId: {}", (Object)spaceId);
        this.pluginTransactionTemplate.write(() -> this.spaceStatisticStore.upsert(this.createDefaultStatsForNewlyCreatedSpace(spaceId, lastModified)));
    }

    public SpaceStatistic createDefaultStatsForNewlyCreatedSpace(long spaceId, Date lastUpdated) {
        return new SpaceStatistic(spaceId, 2L, 0L, 0L, 0L, lastUpdated != null ? lastUpdated.toInstant() : Instant.now(), Instant.now(), this.migrationTimeEstimationUtils.estimateSpaceMigrationTime(2L, 0L, 0L));
    }

    public void removeStatsForDeletedSpaces() {
        List<Long> deletedIds = this.spaceStatisticStore.getDeletedSpacesWithStatistics();
        if (!deletedIds.isEmpty()) {
            log.info("Deleting space statistics for spaces with ids: {}", deletedIds);
            this.pluginTransactionTemplate.write(() -> this.spaceStatisticStore.deleteSpaceStatisticsForSpacesWithIds(deletedIds));
        }
    }

    @VisibleForTesting
    void spinupJobsToRunSpaceStaticCalculations(String jobId, boolean includeHistoricalData, List<Long> spaceIds, boolean awaitResult) {
        long startTimeEpocMilli = this.instantSupplier.get().toEpochMilli();
        int batchLimit = this.migrationAgentConfiguration.getSpaceStatisticCalculationBatchLimit();
        List partitionedSpaceIds = Lists.partition(spaceIds, (int)batchLimit);
        String executionId = UUID.randomUUID().toString();
        ArrayList results = new ArrayList();
        log.info("submitting tasks");
        IntStream.range(0, partitionedSpaceIds.size()).boxed().collect(Collectors.toMap(index -> "batch" + index, partitionedSpaceIds::get)).forEach((batchIndex, ids) -> results.add(this.executorService.submit(() -> this.calculateAndStore(jobId, executionId, (String)batchIndex, (List<Long>)ids, includeHistoricalData))));
        if (awaitResult) {
            long waitStartTimeEpocMilli = this.instantSupplier.get().toEpochMilli();
            results.forEach(future -> {
                try {
                    future.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    log.error("Error while waiting for the future result of a batch ", (Throwable)e);
                }
            });
            long endTimeEpocMilli = this.instantSupplier.get().toEpochMilli();
            log.info("took {}ms to finish job {}", (Object)(endTimeEpocMilli - startTimeEpocMilli), (Object)jobId);
            this.analyticService.buildAndStoreExecutionCompletedEvent(jobId, executionId, partitionedSpaceIds.size(), batchLimit, spaceIds.size(), includeHistoricalData, startTimeEpocMilli, waitStartTimeEpocMilli, endTimeEpocMilli);
        }
    }

    @VisibleForTesting
    void calculateAndStore(String jobId, String executionId, String batchIndex, List<Long> spaceIds, boolean includeHistoricalData) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        CalculationResult calculationResult = this.calculateStatisticsFor(spaceIds, includeHistoricalData, batchIndex, executionId, jobId);
        long readEndTime = this.instantSupplier.get().toEpochMilli();
        StorageResult storageResult = this.storeStatistics(calculationResult.spaceStatistics, includeHistoricalData, batchIndex, executionId, jobId);
        long endTime = this.instantSupplier.get().toEpochMilli();
        log.info("Took {}ms to calculate and store space statistic for batch {} with {} spaces: {}ms to calculate and {}ms to store. ", new Object[]{endTime - startTime, String.format("%s-%s-%s", jobId, executionId, batchIndex), spaceIds.size(), readEndTime - startTime, endTime - readEndTime});
        this.analyticService.buildAndStoreBatchExecutionCompletedEvent(jobId, executionId, batchIndex, spaceIds.size(), includeHistoricalData, calculationResult, storageResult, startTime, readEndTime, endTime);
    }

    private StorageResult storeStatistics(List<SpaceStatistic> spaceStatistics, boolean includeHistoricalData, String batchIndex, String executionId, String jobId) {
        log.info("Storing statistics for batch {}", (Object)String.format("%s-%s-%s", jobId, executionId, batchIndex));
        int analyticsBatchErrorLimit = this.migrationAgentConfiguration.getSpaceStatisticCalculationAnalyticsBatchErrorLimit();
        AtomicInteger upsertExceptionCount = new AtomicInteger();
        spaceStatistics.forEach(spaceStatistic -> {
            block2: {
                try {
                    this.pluginTransactionTemplate.write(() -> this.spaceStatisticStore.upsert((SpaceStatistic)spaceStatistic));
                }
                catch (Exception e) {
                    log.error("Failed to store statistic for spaceId {} of batch {} ", new Object[]{spaceStatistic.getSpaceId(), String.format("%s-%s-%s", jobId, executionId, batchIndex), e});
                    upsertExceptionCount.getAndIncrement();
                    if (upsertExceptionCount.get() > analyticsBatchErrorLimit) break block2;
                    this.analyticService.buildAndStoreBatchStepExecutionErrorEvent(jobId, executionId, batchIndex, "storage", spaceStatistics.size(), includeHistoricalData, e, String.valueOf(spaceStatistic.getSpaceId()));
                }
            }
        });
        return new StorageResult(upsertExceptionCount.get() == 0, upsertExceptionCount.get());
    }

    private CalculationResult calculateStatisticsFor(List<Long> spaceIds, boolean includeHistoricalData, String batchIndex, String executionId, String jobId) {
        log.info("Calculating space statistics for batch {}", (Object)String.format("%s-%s-%s", jobId, executionId, batchIndex));
        SpaceStatisticCalculationProcessor processor = this.newProcessor();
        try {
            this.confluenceStore.queryAndProcess(this.getSpaceStatisticCalculationQueryForSpaceIds(spaceIds, includeHistoricalData), Collections.emptyMap(), processor);
            return new CalculationResult(true, processor.getResult());
        }
        catch (Exception e) {
            log.error("Failed to Calculate statistics for batch {} with {} spaces.", new Object[]{String.format("%s-%s-%s", jobId, executionId, batchIndex), spaceIds.size(), e});
            this.analyticService.buildAndStoreBatchStepExecutionErrorEvent(jobId, executionId, batchIndex, "calculation", spaceIds.size(), includeHistoricalData, e, null);
            return new CalculationResult(false, Collections.emptyList());
        }
    }

    @VisibleForTesting
    @NotNull
    Query getSpaceStatisticCalculationQueryForSpaceIds(List<Long> spaceIds, boolean includeHistoricalData) {
        String queryTemplate = includeHistoricalData ? this.buildQueryIncludingHistoricalPages() : this.buildQueryWithoutHistoricalData();
        return new Query(queryTemplate.replace(":spaceIds", spaceIds.stream().map(Object::toString).collect(Collectors.joining(","))));
    }

    public boolean hasMigrationsRunning() {
        return this.entityManagerTemplate.query(Long.class, "select count(plan) from Plan plan where plan.progress.status= 'RUNNING'").single() > 0L;
    }

    @VisibleForTesting
    SpaceStatisticCalculationProcessor newProcessor() {
        return new SpaceStatisticCalculationProcessor(spacesAndAttachmentSize -> this.migrationTimeEstimationUtils.estimateSpaceMigrationTime((Long)spacesAndAttachmentSize.getLeft(), (Long)spacesAndAttachmentSize.getMiddle(), (Long)spacesAndAttachmentSize.getRight()), this.includeTeamCalendarStats());
    }

    private List<Long> getSpaceIds() {
        List<Long> spaceIds = this.entityManagerTemplate.query(Long.class, "select s.id as id from Space s").list();
        return spaceIds == null ? Collections.emptyList() : spaceIds;
    }

    private List<Long> getMissingSpaceIds() {
        List<Long> spaceIds = this.entityManagerTemplate.query(Long.class, "select s.id as id from Space s left join SpaceStatistic st on s.id = st.spaceId where st.spaceId is null").list();
        return spaceIds == null ? Collections.emptyList() : spaceIds;
    }

    private List<Long> getSpaceIdsForStatisticCalculation() {
        if (this.spaceStatisticStore.isSpaceStatisticEmpty()) {
            return this.getSpaceIds();
        }
        if (this.includeTeamCalendarStats() && this.spaceStatisticStore.countSpaceEntriesInSpaceStatistic() == this.spaceStatisticStore.countSpaceStatisticWithEmptyTeamCalendarEntry()) {
            this.pluginTransactionTemplate.write(this.spaceStatisticStore::deleteAllSpaceStatisticsRecords);
            return this.getSpaceIds();
        }
        return this.getMissingSpaceIds();
    }

    private boolean includeTeamCalendarStats() {
        try {
            return this.teamCalendarHelper.includeTeamCalendar();
        }
        catch (Exception e) {
            log.error("Failed to check if team calendar is enabled", (Throwable)e);
            return false;
        }
    }

    class StorageResult {
        boolean success;
        int errorCount;

        @Generated
        public StorageResult(boolean success, int errorCount) {
            this.success = success;
            this.errorCount = errorCount;
        }

        @Generated
        public boolean isSuccess() {
            return this.success;
        }

        @Generated
        public int getErrorCount() {
            return this.errorCount;
        }
    }

    class CalculationResult {
        boolean success;
        List<SpaceStatistic> spaceStatistics;

        @Generated
        public CalculationResult(boolean success, List<SpaceStatistic> spaceStatistics) {
            this.success = success;
            this.spaceStatistics = spaceStatistics;
        }

        @Generated
        public boolean isSuccess() {
            return this.success;
        }

        @Generated
        public List<SpaceStatistic> getSpaceStatistics() {
            return this.spaceStatistics;
        }
    }
}

