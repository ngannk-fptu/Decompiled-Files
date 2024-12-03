/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.persistence.Tuple
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.model.stats.AttachmentStats;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.newexport.Queries;
import com.atlassian.migration.agent.newexport.TemplatedQuery;
import com.atlassian.migration.agent.service.TeamCalendarHelper;
import com.atlassian.migration.agent.store.ContentStatisticsStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.Tuple;

@ParametersAreNonnullByDefault
public class ContentStatisticsStoreImpl
implements ContentStatisticsStore {
    private static final String JPQL_ATTACHMENTS_STATS = "select sum(p.longval) as sum, avg(p.longval) as avg, max(p.longval) as max, min(p.longval) as min from Attachment a join a.properties p where p.name='FILESIZE'";
    private static final String JPQL_CONTENT_COUNT_BY_TYPE = "select count(c) as count, c.type as type, c.status as status from SpaceContent c where c.space is not null and c.creationDate is not null group by c.type, c.status";
    private static final TemplatedQuery TEAM_CALENDAR_STATS = new TemplatedQuery("SELECT COUNT(*) AS teamCalendarCount FROM \"AO_950DC3_TC_SUBCALS_IN_SPACE\"", (Map<DbType, String>)ImmutableMap.of((Object)((Object)DbType.MYSQL), (Object)"SELECT COUNT(*) AS teamCalendarCount FROM AO_950DC3_TC_SUBCALS_IN_SPACE"));
    static final String JPQL_SPACE_CONTENT_COUNT = "select count(c) as count, c.type as type, c.status as status, c.space.id as id from SpaceContent c where c.space.id in :spaceIds and c.creationDate is not null group by c.space.id, c.type, c.status";
    static final String JPQL_SPACE_ATTACHMENT_STATS_SELECTED = "select a.space.id as spaceId, sum(p.longval) as sum, avg(p.longval) as avg, max(p.longval) as max, min(p.longval) as min from Attachment a join a.properties p where p.name='FILESIZE' and a.space.id in :spaceIds group by a.space.id";
    static final String JPQL_SPACE_ID_TO_KEY_SELECTED = "select s.id as id, s.key as key from Space s where s.key in :keys";
    private static final String CONTENT_TYPE_PAGE = ContentType.PAGE.getType().toUpperCase(Locale.ENGLISH);
    private static final String CONTENT_TYPE_BLOGPOST = ContentType.BLOG_POST.getType().toUpperCase(Locale.ENGLISH);
    private static final String CONTENT_TYPE_ATTACHMENT = ContentType.ATTACHMENT.getType().toUpperCase(Locale.ENGLISH);
    private static final String PARAM_SPACE_IDS = "spaceIds";
    private final EntityManagerTemplate tmpl;
    private final MigrationAgentConfiguration config;
    private final SpaceManager spaceManager;
    private final TeamCalendarHelper teamCalendarHelper;
    private final MigrationAgentConfiguration migrationAgentConfiguration;

    public ContentStatisticsStoreImpl(EntityManagerTemplate tmpl, MigrationAgentConfiguration config, SpaceManager spaceManager, TeamCalendarHelper teamCalendarHelper, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.tmpl = tmpl;
        this.config = config;
        this.spaceManager = spaceManager;
        this.teamCalendarHelper = teamCalendarHelper;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    @Override
    public ContentSummary loadContentSummary() {
        List<Tuple> queryResult = this.tmpl.query(Tuple.class, JPQL_CONTENT_COUNT_BY_TYPE).list();
        return ContentStatisticsStoreImpl.mapContentSummary(queryResult, this.loadAttachmentStats(), this.getAllTeamCalendarCount());
    }

    @Override
    public Collection<SpaceStats> loadSpaceStatistics(Collection<String> spaceKeys) {
        if (spaceKeys.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<SpaceStats> result = new ArrayList<SpaceStats>();
        Collection missingSpaceKeys = spaceKeys.stream().filter(s -> Objects.isNull(this.spaceManager.getSpace(s))).collect(Collectors.toList());
        result.addAll(missingSpaceKeys.stream().map(s -> new SpaceStats((String)s, ContentSummary.ZERO)).collect(Collectors.toList()));
        if (spaceKeys.size() > missingSpaceKeys.size()) {
            ArrayList<String> existingSpaceKeys = new ArrayList<String>(spaceKeys);
            existingSpaceKeys.removeAll(missingSpaceKeys);
            Map<Long, String> spaceIdToKeyMap = this.getSpaceIdToKeyMap(existingSpaceKeys);
            if (spaceIdToKeyMap.isEmpty()) {
                return Collections.emptyList();
            }
            Map<Long, AttachmentStats> attachmentStats = this.loadSpaceAttachmentStats(spaceIdToKeyMap.keySet());
            Map<Long, Long> teamCalendarCount = this.loadSpaceTeamCalendarStats(spaceIdToKeyMap.keySet());
            ArrayList<Tuple> queryResult = new ArrayList<Tuple>(spaceIdToKeyMap.size());
            List partitionedIds = Lists.partition((List)Lists.newLinkedList(spaceIdToKeyMap.keySet()), (int)this.config.getDBQueryParameterLimit());
            for (List partition : partitionedIds) {
                queryResult.addAll(this.tmpl.query(Tuple.class, JPQL_SPACE_CONTENT_COUNT).param(PARAM_SPACE_IDS, (Object)partition).list());
            }
            result.addAll(ContentStatisticsStoreImpl.mapSpaceStatistics(queryResult, attachmentStats, teamCalendarCount, spaceIdToKeyMap));
        }
        return result;
    }

    @Override
    public SpaceStats loadSpaceStatistics(String spaceKey) {
        return this.loadSpaceStatistics((Collection<String>)ImmutableList.of((Object)spaceKey)).stream().findFirst().orElseThrow(() -> new RuntimeException(String.format("Failed to get space %s statistics", spaceKey)));
    }

    @VisibleForTesting
    static ContentSummary mapContentSummary(List<Tuple> queryResult, @Nullable AttachmentStats attachmentStats, long teamCalendarsCount) {
        Map<String, Long> contentCountByType = queryResult.stream().filter(tuple -> ContentStatisticsStoreImpl.isAttachment(tuple) || ContentStatisticsStoreImpl.isContentInCurrentStatus(tuple)).collect(Collectors.toMap(tuple -> (String)tuple.get("type"), tuple -> (Long)tuple.get("count"), Long::sum));
        long pagesCount = ContentStatisticsStoreImpl.orZero(contentCountByType.get(CONTENT_TYPE_PAGE));
        long blogPostsCount = ContentStatisticsStoreImpl.orZero(contentCountByType.get(CONTENT_TYPE_BLOGPOST));
        long attachmentCount = ContentStatisticsStoreImpl.orZero(contentCountByType.get(CONTENT_TYPE_ATTACHMENT));
        Map<String, Long> draftsCountByType = queryResult.stream().filter(tuple -> ContentStatus.DRAFT.getValue().equals(tuple.get("status"))).collect(Collectors.toMap(tuple -> (String)tuple.get("type"), tuple -> (Long)tuple.get("count")));
        long draftsCount = ContentStatisticsStoreImpl.orZero(draftsCountByType.get(CONTENT_TYPE_PAGE)) + ContentStatisticsStoreImpl.orZero(draftsCountByType.get(CONTENT_TYPE_BLOGPOST));
        return ContentSummary.builder().attachments(attachmentStats == null ? AttachmentStats.ZERO : attachmentStats).numberOfBlogs(blogPostsCount).numberOfDrafts(draftsCount).numberOfAttachments(attachmentCount).numberOfTeamCalendars(teamCalendarsCount).numberOfPages(pagesCount).build();
    }

    private static long orZero(@Nullable Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }

    private static double orZero(@Nullable Double value) {
        if (value == null) {
            return 0.0;
        }
        return value;
    }

    private static AttachmentStats mapAttachmentStats(Tuple queryResult) {
        long sum = ContentStatisticsStoreImpl.orZero((Long)queryResult.get("sum"));
        double avg = ContentStatisticsStoreImpl.orZero((Double)queryResult.get("avg"));
        long max = ContentStatisticsStoreImpl.orZero((Long)queryResult.get("max"));
        long min = ContentStatisticsStoreImpl.orZero((Long)queryResult.get("min"));
        return AttachmentStats.builder().averageSize(Math.round(avg)).maximumSize(max).minimumSize(min).totalSize(sum).build();
    }

    private AttachmentStats loadAttachmentStats() {
        Tuple queryResult = this.tmpl.query(Tuple.class, JPQL_ATTACHMENTS_STATS).single();
        return ContentStatisticsStoreImpl.mapAttachmentStats(queryResult);
    }

    private Long getAllTeamCalendarCount() {
        try {
            if (!this.includeTeamCalendarStats()) {
                return 0L;
            }
            Tuple queryResult = this.tmpl.nativeQuery(Tuple.class, TEAM_CALENDAR_STATS.query(this.migrationAgentConfiguration.getDBType())).single();
            return this.getLongFromNumber(queryResult, "teamCalendarCount");
        }
        catch (Exception e) {
            return 0L;
        }
    }

    @VisibleForTesting
    Map<Long, AttachmentStats> loadSpaceAttachmentStats(Collection<Long> spaceIds) {
        ArrayList<Tuple> queryResult = new ArrayList<Tuple>(spaceIds.size());
        List partitionedIds = Lists.partition((List)Lists.newLinkedList(spaceIds), (int)this.config.getDBQueryParameterLimit());
        for (List partition : partitionedIds) {
            queryResult.addAll(this.tmpl.query(Tuple.class, JPQL_SPACE_ATTACHMENT_STATS_SELECTED).param(PARAM_SPACE_IDS, (Object)partition).list());
        }
        return queryResult.stream().collect(Collectors.toMap(tuple -> (Long)tuple.get("spaceId"), ContentStatisticsStoreImpl::mapAttachmentStats));
    }

    @VisibleForTesting
    Map<Long, Long> loadSpaceTeamCalendarStats(Collection<Long> spaceIds) {
        ArrayList<Tuple> queryResult = new ArrayList<Tuple>(spaceIds.size());
        try {
            List partitionedIds = Lists.partition((List)Lists.newLinkedList(spaceIds), (int)this.config.getDBQueryParameterLimit());
            if (!this.includeTeamCalendarStats()) {
                return Collections.emptyMap();
            }
            for (List partition : partitionedIds) {
                queryResult.addAll(this.tmpl.nativeQuery(Tuple.class, Queries.SPACE_TEAM_CALENDAR_STATS_QUERY.query(this.migrationAgentConfiguration.getDBType())).param(PARAM_SPACE_IDS, (Object)partition).list());
            }
        }
        catch (Exception e) {
            return Collections.emptyMap();
        }
        return queryResult.stream().collect(Collectors.toMap(tuple -> this.getLongFromNumber((Tuple)tuple, "spaceId"), tuple -> this.getLongFromNumber((Tuple)tuple, "teamCalendarCount")));
    }

    @VisibleForTesting
    static Collection<SpaceStats> mapSpaceStatistics(List<Tuple> queryResult, Map<Long, AttachmentStats> attachmentStats, Map<Long, Long> teamCalendarCountMap, Map<Long, String> spaceIdToKeyMap) {
        return spaceIdToKeyMap.keySet().stream().map(spaceId -> {
            String spaceKey = (String)spaceIdToKeyMap.get(spaceId);
            List<Tuple> spaceQueryResult = queryResult.stream().filter(tuple -> ((Long)tuple.get("id")).equals(spaceId)).collect(Collectors.toList());
            return new SpaceStats(spaceKey, ContentStatisticsStoreImpl.mapContentSummary(spaceQueryResult, (AttachmentStats)attachmentStats.get(spaceId), teamCalendarCountMap.getOrDefault(spaceId, 0L)));
        }).collect(Collectors.toList());
    }

    @VisibleForTesting
    Map<Long, String> getSpaceIdToKeyMap(Collection<String> spaceKeys) {
        ArrayList<Tuple> queryResult = new ArrayList<Tuple>(spaceKeys.size());
        List partitionedKeys = Lists.partition((List)Lists.newLinkedList(spaceKeys), (int)this.config.getDBQueryParameterLimit());
        for (List partition : partitionedKeys) {
            queryResult.addAll(this.tmpl.query(Tuple.class, JPQL_SPACE_ID_TO_KEY_SELECTED).param("keys", (Object)partition).list());
        }
        return queryResult.stream().collect(Collectors.toMap(tuple -> (Long)tuple.get("id"), tuple -> (String)tuple.get("key")));
    }

    @VisibleForTesting
    long getLongFromNumber(Tuple tuple, String columnName) {
        return ((Number)tuple.get(columnName)).longValue();
    }

    private static boolean isAttachment(Tuple row) {
        return CONTENT_TYPE_ATTACHMENT.equals(row.get("type"));
    }

    private static boolean isContentInCurrentStatus(Tuple row) {
        return ContentStatus.CURRENT.getValue().equals(row.get("status"));
    }

    private boolean includeTeamCalendarStats() throws Exception {
        return this.teamCalendarHelper.includeTeamCalendar();
    }
}

