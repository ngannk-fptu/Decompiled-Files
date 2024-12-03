/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.scheduler.SchedulerService
 *  com.google.common.collect.ImmutableMap
 *  javax.persistence.Tuple
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.function.TriFunction
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.Space;
import com.atlassian.migration.agent.entity.SpaceStatisticsProgress;
import com.atlassian.migration.agent.entity.SpaceWithStatisticResult;
import com.atlassian.migration.agent.mma.model.SpaceMetadata;
import com.atlassian.migration.agent.mma.model.processor.SpaceMetadataRowProcessor;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Query;
import com.atlassian.migration.agent.newexport.TemplatedQuery;
import com.atlassian.migration.agent.newexport.processor.SpaceWithStatisticResultProcessor;
import com.atlassian.migration.agent.newexport.store.JdbcConfluenceStore;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationInitialExecutor;
import com.atlassian.migration.agent.service.impl.SpaceTypeFilter;
import com.atlassian.migration.agent.store.impl.MigratedSpaceStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.atlassian.migration.agent.store.jpa.QueryBuilder;
import com.atlassian.scheduler.SchedulerService;
import com.google.common.collect.ImmutableMap;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import javax.persistence.Tuple;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceStore {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStore.class);
    public static final String CONTENT_ID = "select contentid from content where spaceid = :spaceid and lastmoddate > :recent";
    private static final String CLOUD_ID_PARAM = "cloudId";
    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String START_INDEX_PARAM = "startIndex";
    private static final String NAME_QUERY_PARAM = "nameQuery";
    private static final String STATUSES_PARAM = "statuses";
    private static final String LAST_EDITED_START_DATE_PARAM = "lastEditedStartDate";
    private static final String LAST_EDITED_END_DATE_PARAM = "lastEditedEndDate";
    private final EntityManagerTemplate tmpl;
    private final MigratedSpaceStore migratedSpaceStore;
    private final JdbcConfluenceStore confluenceStore;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final SchedulerService schedulerService;
    private static final Map<SpaceTypeFilter, String> spaceFilterToType = ImmutableMap.of((Object)((Object)SpaceTypeFilter.PERSONAL), (Object)SpaceType.PERSONAL.toString(), (Object)((Object)SpaceTypeFilter.SITE), (Object)SpaceType.GLOBAL.toString());
    private final Map<SpaceTypeFilter, BiPredicate<Space, String>> postFiltered = ImmutableMap.of((Object)((Object)SpaceTypeFilter.TOMIGRATE), (s, c) -> this.hasNotBeenMigrated((Space)s, (String)c), (Object)((Object)SpaceTypeFilter.UNUSED30), (s, c) -> this.isOlderThan((Space)s, 30), (Object)((Object)SpaceTypeFilter.UNUSED90), (s, c) -> this.isOlderThan((Space)s, 90));
    private final BinaryOperator<String> buildLikeQuery = (column, query) -> "LOWER(s." + column + ") LIKE LOWER(:nameQuery) ";
    private static final TemplatedQuery SPACE_COUNT_SELECT_QUERY = new TemplatedQuery("SELECT COUNT(DISTINCT s.SPACEID) as spaceCount FROM SPACES s LEFT JOIN (    SELECT MIG_TASK.SPACEKEY, MIN(MIG_TASK.EXECUTIONSTATUS) AS LATEST_EXECUTION_STATUS     FROM MIG_TASK     INNER JOIN MIG_PLAN ON MIG_PLAN.id = MIG_TASK.PLANID     WHERE MIG_PLAN.CLOUDID = :cloudId    GROUP BY MIG_TASK.SPACEKEY     ORDER BY MIN(MIG_PLAN.LASTUPDATE) desc ) MIG_TASK ON MIG_TASK.SPACEKEY = s.SPACEKEY LEFT OUTER JOIN MIG_SPACE_STATISTIC mss ON s.SPACEID = mss.spaceId", (Map<DbType, String>)ImmutableMap.of((Object)((Object)DbType.MSSQL), (Object)"SELECT COUNT(DISTINCT s.SPACEID) as spaceCount  FROM SPACES s  LEFT JOIN (    SELECT *    FROM (      SELECT        mt.spaceKey as spaceKey,        mt.executionStatus as LATEST_EXECUTION_STATUS,        ROW_NUMBER() OVER (PARTITION BY mt.spaceKey ORDER BY MIG_PLAN.lastUpdate DESC) AS row_num      FROM MIG_TASK mt      INNER JOIN MIG_PLAN ON MIG_PLAN.id = mt.planId      WHERE        mt.spaceKey IS NOT NULL        AND        MIG_PLAN.cloudId = :cloudId      ) AS subquery      WHERE row_num = 1  )  MIG_TASK ON MIG_TASK.spaceKey = s.SPACEKEY  LEFT OUTER JOIN MIG_SPACE_STATISTIC mss ON s.SPACEID = mss.spaceId"));
    private static final TemplatedQuery SPACE_SELECT_QUERY = new TemplatedQuery("SELECT s.SPACEID, s.SPACEKEY, s.SPACENAME, s.SPACETYPE,  CASE MIG_TASK.LATEST_EXECUTION_STATUS  WHEN 'DONE' THEN 'MIGRATED'  WHEN 'RUNNING' THEN 'RUNNING'  WHEN 'STOPPED' THEN 'STOPPED'  WHEN 'FAILED' THEN 'FAILED'  WHEN 'CREATED' THEN 'QUEUED'  WHEN 'STOPPING' THEN 'STOPPING'  ELSE 'NOT_IN_ANY_PLAN'  END AS LATEST_EXECUTION_STATUS,  mss.SUMOFPAGEBLOGDRAFTCOUNT, mss.ATTACHMENTSIZE, mss.ATTACHMENTCOUNT, mss.TEAMCALENDARCOUNT, mss.ESTIMATEDMIGRATIONTIME, mss.LASTUPDATED as LASTMODIFIED  FROM SPACES s LEFT JOIN (     SELECT MIG_TASK.SPACEKEY, MIN(MIG_TASK.EXECUTIONSTATUS) AS LATEST_EXECUTION_STATUS      FROM MIG_TASK      INNER JOIN MIG_PLAN ON MIG_PLAN.id = MIG_TASK.PLANID      WHERE MIG_PLAN.CLOUDID = :cloudId      GROUP BY MIG_TASK.SPACEKEY      ORDER BY MIN(MIG_PLAN.LASTUPDATE) desc  ) MIG_TASK ON MIG_TASK.SPACEKEY = s.SPACEKEY  LEFT JOIN MIG_SPACE_STATISTIC mss ON s.SPACEID = mss.spaceId", (Map<DbType, String>)ImmutableMap.of((Object)((Object)DbType.MSSQL), (Object)"SELECT s.SPACEID, s.SPACEKEY, s.SPACENAME, s.SPACETYPE,  CASE MIG_TASK.LATEST_EXECUTION_STATUS  WHEN 'DONE' THEN 'MIGRATED'  WHEN 'RUNNING' THEN 'RUNNING'  WHEN 'STOPPED' THEN 'STOPPED'  WHEN 'FAILED' THEN 'FAILED'  WHEN 'CREATED' THEN 'QUEUED'  WHEN 'STOPPING' THEN 'STOPPING'  ELSE 'NOT_IN_ANY_PLAN'  END AS LATEST_EXECUTION_STATUS,  mss.sumOfPageBlogDraftCount, mss.attachmentSize, mss.attachmentCount, mss.teamCalendarCount, mss.estimatedMigrationTime, mss.lastUpdated as LASTMODIFIED  FROM SPACES s  LEFT JOIN (    SELECT *    FROM (      SELECT        mt.spaceKey as spaceKey,        mt.executionStatus as LATEST_EXECUTION_STATUS,        ROW_NUMBER() OVER (PARTITION BY mt.spaceKey ORDER BY MIG_PLAN.lastUpdate DESC) AS row_num      FROM MIG_TASK mt      INNER JOIN MIG_PLAN ON MIG_PLAN.id = mt.planId      WHERE        mt.spaceKey IS NOT NULL        AND        MIG_PLAN.cloudId = :cloudId      ) AS subquery      WHERE row_num = 1  )  MIG_TASK ON MIG_TASK.spaceKey = s.SPACEKEY  LEFT JOIN MIG_SPACE_STATISTIC mss ON s.SPACEID = mss.spaceId"));
    private final Query spaceMetadataQuery = new Query("SELECT s.spaceid, s.spaceName, s.spaceKey, s.spaceType, sumofpageblogdraftcount, attachmentsize, attachmentcount, estimatedmigrationtime, LASTUPDATED as LASTMODIFIED, lastcalculated FROM MIG_SPACE_STATISTIC mss RIGHT JOIN SPACES s ON mss.spaceId = s.spaceId");

    private boolean isOlderThan(Space space, int days) {
        Instant now = Instant.now();
        Instant then = now.minus(Duration.ofDays(days));
        List<Long> ids = this.confluenceStore.findContentIds(CONTENT_ID, (Map)ImmutableMap.of((Object)"spaceid", (Object)space.getId(), (Object)"recent", (Object)new Date(then.toEpochMilli())));
        return ids.isEmpty();
    }

    private boolean hasNotBeenMigrated(Space space, String cloudId) {
        return !this.migratedSpaceStore.hasSpace(space, cloudId);
    }

    public SpaceStore(EntityManagerTemplate tmpl, MigratedSpaceStore migratedSpaceStore, JdbcConfluenceStore confluenceStore, MigrationDarkFeaturesManager migrationDarkFeaturesManager, SchedulerService schedulerService) {
        this.tmpl = tmpl;
        this.migratedSpaceStore = migratedSpaceStore;
        this.confluenceStore = confluenceStore;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.schedulerService = schedulerService;
    }

    public int getSpacesCount(String nameQuery, SpaceTypeFilter spaceTypeFilter) {
        String query = "select count(*) from Space s" + this.whereClause(nameQuery, spaceTypeFilter, Collections.emptyList());
        QueryBuilder<Long> builder = this.tmpl.query(Long.class, query);
        this.queryParam(builder, nameQuery);
        return builder.single().intValue();
    }

    public List<Space> getSpaces(String planId) {
        String query = "select space from Task task inner join Space space on space.key = task.spaceKey where task.plan.id=:planId";
        QueryBuilder<Space> builder = this.tmpl.query(Space.class, query);
        builder.param("planId", (Object)planId);
        return builder.list();
    }

    public Map<String, String> getSpaceKeyNamePairsForSpaceTasks(String planId) {
        String query = "select task.spaceKey as spaceKey, space.name as spaceName from AbstractSpaceTask task left join Space space on task.spaceKey = space.key where task.plan.id =: planId and TYPE(task) in (SpaceAttachmentsOnlyTask,ConfluenceSpaceTask)";
        QueryBuilder<Tuple> builder = this.tmpl.query(Tuple.class, query);
        builder.param("planId", (Object)planId);
        return builder.stream().collect(HashMap::new, (map, tuple) -> map.put((String)tuple.get("spaceKey"), (String)tuple.get("spaceName")), HashMap::putAll);
    }

    public List<Space> getSpacesPaged(String cloudId, String nameQuery, SpaceTypeFilter spaceTypeFilter, int startIndex, int pageSize) {
        return this.postFiltered.containsKey((Object)spaceTypeFilter) ? this.getFilterSpaces(nameQuery, spaceTypeFilter).stream().filter(s -> this.postFiltered.get((Object)spaceTypeFilter).test((Space)s, cloudId)).skip(startIndex).limit(pageSize).collect(Collectors.toList()) : this.getFilterSpaces(nameQuery, spaceTypeFilter, startIndex, pageSize);
    }

    @VisibleForTesting
    List<Space> getFilterSpaces(String nameQuery, SpaceTypeFilter spaceTypeFilter, int startIndex, int pageSize) {
        return this.getQueryBuilder(nameQuery, spaceTypeFilter).first(startIndex).max(pageSize).list();
    }

    @VisibleForTesting
    List<Space> getFilterSpaces(String nameQuery, SpaceTypeFilter spaceTypeFilter) {
        return this.getQueryBuilder(nameQuery, spaceTypeFilter).list();
    }

    public int getTotalSpaces() {
        return this.tmpl.query(Long.class, "select count(*) from Space s").single().intValue();
    }

    @NotNull
    private QueryBuilder<Space> getQueryBuilder(String nameQuery, SpaceTypeFilter spaceTypeFilter) {
        String query = "from Space s" + this.whereClause(nameQuery, spaceTypeFilter, Collections.emptyList()) + " order by s.key";
        QueryBuilder<Space> builder = this.tmpl.query(Space.class, query);
        this.queryParam(builder, nameQuery);
        return builder;
    }

    public int getSpaceCountByNewSpaceSelector(String cloudId, String nameQuery, SpaceTypeFilter spaceTypeFilter, List<ExecutionStatus> statuses, Instant lastEditedStartDate, Instant lastEditedEndDate) {
        String whereClause = this.whereClause(nameQuery, spaceTypeFilter, statuses, lastEditedStartDate, lastEditedEndDate);
        Map<String, Object> params = this.mapParametersForNewSpaceSelectorSpaceCountQuery(nameQuery, cloudId, statuses, lastEditedStartDate, lastEditedEndDate);
        Optional<Integer> count = this.confluenceStore.fetchInteger((Query)this.buildSpaceSelectQuery().apply((Object)SPACE_COUNT_SELECT_QUERY, (Object)whereClause, (Object)this.buildPagedQuery(false, "", SortOrder.ASC)), params);
        return count.orElse(0);
    }

    public List<SpaceWithStatisticResult> getSpacesWithStatistic(String cloudId, String query, SpaceTypeFilter spaceTypeFilter, List<ExecutionStatus> statuses, int startIndex, int pageSize, String sortKey, SortOrder sortOrder, Instant lastEditedStartDate, Instant lastEditedEndDate) {
        SpaceWithStatisticResultProcessor spaceSelectorProcessor = new SpaceWithStatisticResultProcessor();
        String whereClause = this.whereClause(query, spaceTypeFilter, statuses, lastEditedStartDate, lastEditedEndDate);
        Map<String, Object> params = this.mapParametersForSpaceStatisticsQuery(cloudId, query, statuses, startIndex, pageSize, lastEditedStartDate, lastEditedEndDate);
        this.confluenceStore.queryAndProcess((Query)this.buildSpaceSelectQuery().apply((Object)SPACE_SELECT_QUERY, (Object)whereClause, (Object)this.buildPagedQuery(true, sortKey, sortOrder)), params, spaceSelectorProcessor);
        return spaceSelectorProcessor.getResult();
    }

    public List<SpaceMetadata> getSpaceMetadata() {
        SpaceMetadataRowProcessor rowProcessor = new SpaceMetadataRowProcessor();
        this.confluenceStore.queryAndProcess(this.spaceMetadataQuery, Collections.emptyMap(), rowProcessor);
        return rowProcessor.getResult();
    }

    public SpaceStatisticsProgress getSpaceStatsProgress() {
        return new SpaceStatisticsProgress(this.confluenceStore.fetchInteger(new Query("SELECT COUNT(*) FROM SPACES")).orElse(0), this.confluenceStore.fetchInteger(new Query("SELECT COUNT(*) FROM MIG_SPACE_STATISTIC mss JOIN SPACES s ON s.SPACEID = mss.spaceId")).orElse(0), this.schedulerService.getJobDetails(SpaceStatisticCalculationInitialExecutor.JOB_ID) != null);
    }

    private TriFunction<TemplatedQuery, String, String, Query> buildSpaceSelectQuery() {
        return (templatedQuery, whereClause, paginationAndSorting) -> {
            StringBuilder sb = new StringBuilder(templatedQuery.query(this.confluenceStore.getDbType()));
            sb.append((String)whereClause);
            sb.append((String)paginationAndSorting);
            return new Query(sb.toString());
        };
    }

    @VisibleForTesting
    String buildPagedQuery(boolean isPaged, String sortKey, SortOrder sortOrder) {
        if (!isPaged) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ORDER BY ");
        sb.append(SpaceStore.getColumnNumberBySortKey(sortKey));
        sb.append(" ");
        sb.append(sortOrder.name());
        if (DbType.POSTGRES.equals((Object)this.confluenceStore.getDbType()) || DbType.ORACLE.equals((Object)this.confluenceStore.getDbType())) {
            if (SortOrder.DESC.equals((Object)sortOrder)) {
                sb.append(" NULLS LAST");
            } else {
                sb.append(" NULLS FIRST");
            }
        }
        if (!"spaceKey".equals(sortKey)) {
            sb.append(", ");
            sb.append(SpaceStore.getColumnNumberBySortKey("spaceKey"));
            sb.append(" ASC");
        }
        sb.append(this.getPaginationClause());
        return sb.toString();
    }

    String getPaginationClause() {
        if (DbType.MSSQL.equals((Object)this.confluenceStore.getDbType())) {
            return " OFFSET :startIndex ROWS FETCH FIRST :pageSize ROWS ONLY ";
        }
        if (DbType.ORACLE.equals((Object)this.confluenceStore.getDbType())) {
            return " OFFSET :startIndex ROWS FETCH NEXT :pageSize ROWS ONLY ";
        }
        return " LIMIT :pageSize OFFSET :startIndex ";
    }

    static int getColumnNumberBySortKey(String sortKey) {
        if (sortKey == null) {
            return 3;
        }
        switch (sortKey) {
            case "spaceKey": {
                return 2;
            }
            case "spaceType": {
                return 4;
            }
            case "executionStatus": {
                return 5;
            }
            case "pageCount": {
                return 6;
            }
            case "attachmentSize": {
                return 7;
            }
            case "teamCalendarCount": {
                return 9;
            }
            case "estimate": {
                return 10;
            }
            case "lastEdited": {
                return 11;
            }
        }
        return 3;
    }

    @VisibleForTesting
    String whereClause(String nameQuery, SpaceTypeFilter spaceTypeFilter, List<ExecutionStatus> statuses) {
        return this.whereClause(nameQuery, spaceTypeFilter, statuses, null, null);
    }

    @VisibleForTesting
    String whereClause(String nameQuery, SpaceTypeFilter spaceTypeFilter, List<ExecutionStatus> statuses, Instant lastEditedStartDate, Instant lastEditedEndDate) {
        if (StringUtils.isEmpty((CharSequence)nameQuery) && !spaceFilterToType.containsKey((Object)spaceTypeFilter) && statuses.isEmpty() && lastEditedStartDate == null && lastEditedEndDate == null) {
            return "";
        }
        CharSequence[] filterClauses = (String[])Arrays.stream(new String[]{this.buildNameQueryFilter(nameQuery), this.buildSpaceTypeFilter(spaceTypeFilter), this.buildExecutionStatusFilter(statuses), this.buildLastUpdatedFilter(lastEditedStartDate, lastEditedEndDate)}).filter(clause -> !StringUtils.isEmpty((CharSequence)clause)).toArray(String[]::new);
        return " where " + String.join((CharSequence)" and ", filterClauses);
    }

    private String buildNameQueryFilter(String nameQuery) {
        if (StringUtils.isEmpty((CharSequence)nameQuery)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            sb.append("(");
            sb.append((String)this.buildLikeQuery.apply("SPACENAME", nameQuery));
            sb.append(" OR ");
            sb.append((String)this.buildLikeQuery.apply("SPACEKEY", nameQuery));
            sb.append(")");
        } else {
            sb.append("lower(s.name) like lower(:nameQuery)");
        }
        return sb.toString();
    }

    private String buildSpaceTypeFilter(SpaceTypeFilter spaceTypeFilter) {
        if (!spaceFilterToType.containsKey((Object)spaceTypeFilter)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            sb.append("s.SPACETYPE = '").append(spaceFilterToType.get((Object)spaceTypeFilter)).append("'");
        } else {
            sb.append("s.type = '").append(spaceFilterToType.get((Object)spaceTypeFilter)).append("'");
        }
        return sb.toString();
    }

    @VisibleForTesting
    String buildExecutionStatusFilter(List<ExecutionStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        boolean containsNotNull = statuses.stream().anyMatch(Objects::nonNull);
        if (containsNotNull) {
            sb.append("MIG_TASK.LATEST_EXECUTION_STATUS in (:statuses)");
        }
        boolean containsNull = statuses.stream().anyMatch(Objects::isNull);
        if (containsNotNull && containsNull) {
            sb.append(" OR ");
        }
        if (statuses.contains(null)) {
            sb.append(" MIG_TASK.LATEST_EXECUTION_STATUS is NULL ");
        }
        sb.append(")");
        return sb.toString();
    }

    private String buildLastUpdatedFilter(Instant lastEditedStartDate, Instant lastEditedEndDate) {
        if (!this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (lastEditedStartDate != null) {
            sb.append(String.format("mss.%s >= :%s", this.mapLastUpdatedColumnNameByDatabase(), LAST_EDITED_START_DATE_PARAM));
        }
        if (lastEditedStartDate != null && lastEditedEndDate != null) {
            sb.append(" AND ");
        }
        if (lastEditedEndDate != null) {
            sb.append(String.format("mss.%s <= :%s", this.mapLastUpdatedColumnNameByDatabase(), LAST_EDITED_END_DATE_PARAM));
        }
        return sb.toString();
    }

    private String mapLastUpdatedColumnNameByDatabase() {
        if (this.confluenceStore.getDbType() == DbType.MSSQL) {
            return "lastUpdated";
        }
        return "LASTUPDATED";
    }

    private void queryParam(QueryBuilder builder, String nameQuery) {
        if (StringUtils.isNotEmpty((CharSequence)nameQuery)) {
            builder.param(NAME_QUERY_PARAM, (Object)this.surroundStringWithSQLWildcardSymbols(nameQuery));
        }
    }

    private String surroundStringWithSQLWildcardSymbols(String nameQuery) {
        return "%" + nameQuery + "%";
    }

    @VisibleForTesting
    Map<String, Object> mapParametersForSpaceStatisticsQuery(String cloudId, String query, List<ExecutionStatus> statuses, int startIndex, int pageSize, Instant lastEditedStartDate, Instant lastEditedEndDate) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CLOUD_ID_PARAM, cloudId);
        params.put(PAGE_SIZE_PARAM, pageSize);
        params.put(START_INDEX_PARAM, startIndex);
        if (StringUtils.isNotEmpty((CharSequence)query)) {
            params.put(NAME_QUERY_PARAM, this.surroundStringWithSQLWildcardSymbols(query));
        }
        this.statusesInQueryParamMap(params, this.buildStatusesList(statuses));
        if (lastEditedStartDate != null) {
            params.put(LAST_EDITED_START_DATE_PARAM, Timestamp.from(lastEditedStartDate));
        }
        if (lastEditedEndDate != null) {
            params.put(LAST_EDITED_END_DATE_PARAM, Timestamp.from(lastEditedEndDate));
        }
        return params;
    }

    private Map<String, Object> mapParametersForNewSpaceSelectorSpaceCountQuery(String nameQuery, String cloudId, List<ExecutionStatus> statuses, Instant lastEditedStartDate, Instant lastEditedEndDate) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put(CLOUD_ID_PARAM, cloudId);
        params.put(NAME_QUERY_PARAM, this.surroundStringWithSQLWildcardSymbols(nameQuery));
        this.statusesInQueryParamMap(params, this.buildStatusesList(statuses));
        if (lastEditedStartDate != null) {
            params.put(LAST_EDITED_START_DATE_PARAM, Timestamp.from(lastEditedStartDate));
        }
        if (lastEditedEndDate != null) {
            params.put(LAST_EDITED_END_DATE_PARAM, Timestamp.from(lastEditedEndDate));
        }
        return params;
    }

    private void statusesInQueryParamMap(Map<String, Object> params, List<String> statusParam) {
        if (!statusParam.isEmpty()) {
            params.put(STATUSES_PARAM, statusParam);
        }
    }

    private List<String> buildStatusesList(List<ExecutionStatus> statuses) {
        return statuses.stream().filter(Objects::nonNull).map(s -> s.name().toUpperCase()).collect(Collectors.toList());
    }
}

