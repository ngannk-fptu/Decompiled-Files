/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.migration.agent.entity.Stats;
import com.atlassian.migration.agent.entity.StatsKey;
import com.atlassian.migration.agent.entity.StatsType;
import com.atlassian.migration.agent.model.stats.AttachmentStats;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.InstanceStats;
import com.atlassian.migration.agent.model.stats.ServerStats;
import com.atlassian.migration.agent.service.StatsStoringService;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.store.StatsStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultStatsStoringService
implements StatsStoringService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultStatsStoringService.class);
    private static final String CACHE_KEY_ATTACHMENT_MIN_SIZE = "ATTACHMENT_MIN_SIZE";
    private static final String CACHE_KEY_ATTACHMENT_MAX_SIZE = "ATTACHMENT_MAX_SIZE";
    private static final String CACHE_KEY_ATTACHMENT_AVG_SIZE = "ATTACHMENT_AVG_SIZE";
    private static final String CACHE_KEY_ATTACHMENT_TOTAL_SIZE = "ATTACHMENT_TOTAL_SIZE";
    private static final String CACHE_KEY_PAGES = "PAGES";
    private static final String CACHE_KEY_BLOGPOSTS = "BLOGPOSTS";
    private static final String CACHE_KEY_DRAFTS = "DRAFTS";
    private static final String CACHE_KEY_ATTACHMENTS = "ATTACHMENTS";
    private static final String CACHE_KEY_TEAM_CALENDARS = "TEAM_CALENDARS";
    private static final String CACHE_KEY_SPACES = "SPACES";
    private static final String CACHE_KEY_USERS = "USERS";
    private static final String CACHE_KEY_GROUPS = "GROUPS";
    private static final String CACHE_KEY_ESTIMATED_USER_GROUP_TIME = "ESTIMATED_USER_GROUP_TIME";
    private static final String CACHE_KEY_ESTIMATED_TOTAL_SPACE_TIME = "ESTIMATED_TOTAL_SPACE_TIME";
    private static final String CACHE_KEY_ESTIMATED_BANDWIDTH_KBPS = "ESTIMATED_BANDWIDTH_KBPS";
    private final PluginTransactionTemplate ptx;
    private final StatsStore statsStore;

    public DefaultStatsStoringService(PluginTransactionTemplate ptx, StatsStore statsStore) {
        this.ptx = ptx;
        this.statsStore = statsStore;
    }

    private static long getOrThrow(Map<String, Long> storedMap, String key) {
        Long ret = storedMap.get(key);
        if (ret == null) {
            throw new MissingStoredStatException("Missing stored stats with key " + key);
        }
        return ret;
    }

    @Override
    @Nonnull
    public Optional<StatsStoringService.Stored<ServerStats>> loadServerStats(Supplier<ConfluenceInfo> confluenceInfoSupplier) {
        List stats = this.ptx.read(() -> this.statsStore.getByType(StatsType.SITE));
        if (stats.isEmpty()) {
            return Optional.empty();
        }
        try {
            Map<String, Long> nameToValueMap = stats.stream().collect(Collectors.toMap(Stats::getName, Stats::getValue));
            AttachmentStats attachmentStats = AttachmentStats.builder().minimumSize(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ATTACHMENT_MIN_SIZE)).maximumSize(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ATTACHMENT_MAX_SIZE)).averageSize(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ATTACHMENT_AVG_SIZE)).totalSize(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ATTACHMENT_TOTAL_SIZE)).build();
            ConfluenceInfo confluenceInfo = confluenceInfoSupplier.get();
            InstanceStats instanceStats = InstanceStats.builder().version(confluenceInfo.getVersion()).buildNumber(confluenceInfo.getBuildNumber()).numberOfSpaces((int)DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_SPACES)).numberOfUsers((int)DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_USERS)).numberOfGroups((int)DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_GROUPS)).build();
            ContentSummary contentSummary = ContentSummary.builder().attachments(attachmentStats).numberOfPages(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_PAGES)).numberOfBlogs(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_BLOGPOSTS)).numberOfDrafts(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_DRAFTS)).numberOfAttachments(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ATTACHMENTS)).numberOfTeamCalendars(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_TEAM_CALENDARS)).build();
            Duration userGroupMigrationTime = Duration.ofMillis(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ESTIMATED_USER_GROUP_TIME));
            Duration totalSpaceMigrationTime = Duration.ofMillis(DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ESTIMATED_TOTAL_SPACE_TIME));
            long bandwidthKBS = DefaultStatsStoringService.getOrThrow(nameToValueMap, CACHE_KEY_ESTIMATED_BANDWIDTH_KBPS);
            ServerStats serverStats = new ServerStats(instanceStats, contentSummary, userGroupMigrationTime, totalSpaceMigrationTime, MigrationTimeEstimationUtils.getBaseMigrationTime(), bandwidthKBS);
            return Optional.of(new StatsStoringService.Stored<ServerStats>(serverStats, ((Stats)stats.get(1)).getCollectedTime()));
        }
        catch (MissingStoredStatException e) {
            log.warn("Failed to load stored server statistics", (Throwable)e);
            return Optional.empty();
        }
    }

    @Override
    public void storeServerStats(ServerStats serverStats) {
        ContentSummary contentSummary = serverStats.getContentSummary();
        ArrayList<Stats> stats = new ArrayList<Stats>();
        stats.add(new Stats(CACHE_KEY_PAGES, contentSummary.getNumberOfPages() != null ? contentSummary.getNumberOfPages() : 0L));
        stats.add(new Stats(CACHE_KEY_BLOGPOSTS, contentSummary.getNumberOfBlogs() != null ? contentSummary.getNumberOfBlogs() : 0L));
        stats.add(new Stats(CACHE_KEY_DRAFTS, contentSummary.getNumberOfDrafts() != null ? contentSummary.getNumberOfDrafts() : 0L));
        stats.add(new Stats(CACHE_KEY_ATTACHMENTS, contentSummary.getNumberOfAttachments() != null ? contentSummary.getNumberOfAttachments() : 0L));
        stats.add(new Stats(CACHE_KEY_TEAM_CALENDARS, contentSummary.getNumberOfTeamCalendars() != null ? contentSummary.getNumberOfTeamCalendars() : 0L));
        AttachmentStats attachmentStats = contentSummary.getAttachments();
        stats.add(new Stats(CACHE_KEY_ATTACHMENT_AVG_SIZE, attachmentStats.getAverageSize()));
        stats.add(new Stats(CACHE_KEY_ATTACHMENT_MAX_SIZE, attachmentStats.getMaximumSize()));
        stats.add(new Stats(CACHE_KEY_ATTACHMENT_MIN_SIZE, attachmentStats.getMinimumSize()));
        stats.add(new Stats(CACHE_KEY_ATTACHMENT_TOTAL_SIZE, attachmentStats.getTotalSize() != null ? attachmentStats.getTotalSize() : 0L));
        InstanceStats instanceStats = serverStats.getInstanceStats();
        stats.add(new Stats(CACHE_KEY_SPACES, instanceStats.getNumberOfSpaces()));
        stats.add(new Stats(CACHE_KEY_USERS, instanceStats.getNumberOfUsers()));
        stats.add(new Stats(CACHE_KEY_GROUPS, instanceStats.getNumberOfGroups()));
        stats.add(new Stats(CACHE_KEY_ESTIMATED_USER_GROUP_TIME, serverStats.getTotalUserGroupMigrationTime().toMillis()));
        stats.add(new Stats(CACHE_KEY_ESTIMATED_TOTAL_SPACE_TIME, serverStats.getTotalSpaceMigrationTime().toMillis()));
        stats.add(new Stats(CACHE_KEY_ESTIMATED_BANDWIDTH_KBPS, serverStats.getBandwidthKBS()));
        this.ptx.write(() -> {
            this.statsStore.clearByType(StatsType.SITE);
            this.statsStore.persist(stats);
        });
    }

    @Override
    public void storeBandwidthKBS(long bandwidth) {
        Stats bandwidthStat = new Stats(CACHE_KEY_ESTIMATED_BANDWIDTH_KBPS, bandwidth);
        this.ptx.write(() -> {
            this.statsStore.clearByKey(new StatsKey(bandwidthStat.getType(), bandwidthStat.getName()));
            this.statsStore.persist(bandwidthStat);
        });
    }

    private static class MissingStoredStatException
    extends RuntimeException {
        MissingStoredStatException(String message) {
            super(message);
        }
    }
}

