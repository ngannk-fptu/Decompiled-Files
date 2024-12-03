/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.SpaceDto;
import com.atlassian.migration.agent.dto.SpaceSearchResultDto;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.SortOrder;
import com.atlassian.migration.agent.entity.Space;
import com.atlassian.migration.agent.entity.SpaceStatisticsProgress;
import com.atlassian.migration.agent.entity.SpaceWithStatisticResult;
import com.atlassian.migration.agent.model.stats.AttachmentStats;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.SpaceTypeFilter;
import com.atlassian.migration.agent.store.ConfluenceSpaceTaskStore;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpaceCatalogService {
    private final PluginTransactionTemplate ptx;
    private final SpaceStore spaceStore;
    private final ConfluenceSpaceTaskStore confluenceSpaceTaskStore;
    private final StatisticsService statisticsService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;
    @VisibleForTesting
    public static final long AVG_ESTIMATION_SECONDS = 30L;

    public SpaceCatalogService(PluginTransactionTemplate ptx, SpaceStore spaceStore, ConfluenceSpaceTaskStore confluenceSpaceTaskStore, StatisticsService statisticsService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        this.ptx = ptx;
        this.spaceStore = spaceStore;
        this.confluenceSpaceTaskStore = confluenceSpaceTaskStore;
        this.statisticsService = statisticsService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
    }

    public Collection<SpaceDto> getSpacesSummaryForPlan(String planId) {
        List spaces = this.ptx.read(() -> this.spaceStore.getSpaces(planId));
        List<String> spaceKeys = spaces.stream().map(Space::getKey).collect(Collectors.toList());
        Map<String, ContentSummary> spaceKeyToSummary = this.mapContentSummariesForSpaceKeys(spaceKeys);
        return spaces.stream().map(space -> this.spaceToDto((Space)space, spaceKeyToSummary, Collections.emptyMap())).collect(Collectors.toList());
    }

    public Collection<SpaceDto> getSpacesForPlan(String planId) {
        List spaces = this.ptx.read(() -> this.spaceStore.getSpaces(planId));
        return spaces.stream().map(space -> this.spaceToDto((Space)space, Collections.emptyMap(), Collections.emptyMap())).collect(Collectors.toList());
    }

    public SpaceSearchResultDto getSpaces(String cloudId, String nameQuery, SpaceTypeFilter spaceTypeFilter, List<String> statuses, int startIndex, int pageSize, String sortKey, SortOrder sortOrder, @Nullable Instant lastEditedStartDate, @Nullable Instant lastEditedEndDate) {
        int totalSpaces = this.ptx.read(this.spaceStore::getTotalSpaces);
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled()) {
            List executionStatuses = statuses.stream().map(ExecutionStatus::mapToExecutionStatus).collect(Collectors.toList());
            List spaceWithStatisticResults = this.ptx.read(() -> this.spaceStore.getSpacesWithStatistic(cloudId, nameQuery, spaceTypeFilter, executionStatuses, startIndex, pageSize, sortKey, sortOrder, lastEditedStartDate, lastEditedEndDate));
            int spaceCount = this.ptx.read(() -> this.spaceStore.getSpaceCountByNewSpaceSelector(cloudId, nameQuery, spaceTypeFilter, executionStatuses, lastEditedStartDate, lastEditedEndDate));
            SpaceStatisticsProgress spaceStatisticsProgress = this.spaceStore.getSpaceStatsProgress();
            return new SpaceSearchResultDto(spaceCount, startIndex, pageSize, this.getSpaceDTOsForNewSpaceSelector(spaceWithStatisticResults), totalSpaces, spaceStatisticsProgress.getPercentage(), spaceStatisticsProgress.isCalculating());
        }
        int spaceCount = this.ptx.read(() -> this.spaceStore.getSpacesCount(nameQuery, spaceTypeFilter));
        List pagedSpaces = this.ptx.read(() -> this.spaceStore.getSpacesPaged(cloudId, nameQuery, spaceTypeFilter, startIndex, pageSize));
        if (pageSize == Integer.MAX_VALUE) {
            return new SpaceSearchResultDto(spaceCount, startIndex, pageSize, this.createSpaceDtosWithoutSummaryProgress(pagedSpaces), totalSpaces, 0.0, false);
        }
        if (pagedSpaces.isEmpty()) {
            return new SpaceSearchResultDto(spaceCount, startIndex, pageSize, Collections.emptyList(), totalSpaces, 0.0, false);
        }
        return new SpaceSearchResultDto(spaceCount, startIndex, pageSize, this.getSpaceDtos(cloudId, pagedSpaces), totalSpaces, 0.0, false);
    }

    private Collection<SpaceDto> createSpaceDtosWithoutSummaryProgress(List<Space> spaces) {
        List<String> spaceKeys = spaces.stream().map(Space::getKey).collect(Collectors.toList());
        Map<String, ContentSummary> spaceKeyToSummary = this.mapContentSummariesForSpaceKeys(spaceKeys);
        return spaces.stream().map(space -> this.spaceToDtoWithOutProgress((Space)space, spaceKeyToSummary)).collect(Collectors.toList());
    }

    private SpaceDto spaceToDtoWithOutProgress(Space space, Map<String, ContentSummary> spaceKeyToSummary) {
        String spaceKey = space.getKey();
        return new SpaceDto(spaceKey, space.getId(), space.getName(), spaceKeyToSummary.getOrDefault(spaceKey, ContentSummary.ZERO), null, 30L, Optional.ofNullable(space.getType()).map(SpaceType::forName).orElse(null));
    }

    private Map<String, ContentSummary> mapContentSummariesForSpaceKeys(List<String> spaceKeys) {
        return this.statisticsService.loadSpaceStatistics(spaceKeys).stream().collect(Collectors.toMap(SpaceStats::getSpaceKey, SpaceStats::getSummary));
    }

    private Collection<SpaceDto> getSpaceDtos(String cloudId, List<Space> spaces) {
        List<String> spaceKeys = spaces.stream().map(Space::getKey).collect(Collectors.toList());
        Map spaceKeyToProgress = this.ptx.read(() -> this.confluenceSpaceTaskStore.getLatestSpaceProgress(cloudId, spaceKeys));
        Map<String, ContentSummary> spaceKeyToSummary = this.mapContentSummariesForSpaceKeys(spaceKeys);
        return spaces.stream().map(space -> this.spaceToDto((Space)space, spaceKeyToSummary, spaceKeyToProgress)).collect(Collectors.toList());
    }

    private SpaceDto spaceToDto(Space space, Map<String, ContentSummary> spaceKeyToSummary, Map<String, Progress> spaceKeyToProgress) {
        String spaceKey = space.getKey();
        ContentSummary spaceSummary = spaceKeyToSummary.getOrDefault(spaceKey, ContentSummary.ZERO);
        long estimationSeconds = this.migrationTimeEstimationUtils.estimateSpaceMigrationTime(spaceSummary).getSeconds();
        return new SpaceDto(spaceKey, space.getId(), space.getName(), spaceSummary, Optional.ofNullable(spaceKeyToProgress.get(spaceKey)).map(ProgressDto::fromPlanEntity).orElse(null), estimationSeconds, Optional.ofNullable(space.getType()).map(SpaceType::forName).orElse(null));
    }

    @VisibleForTesting
    SpaceDto mapSpaceWithStatisticResultToSpaceDto(SpaceWithStatisticResult space) {
        AttachmentStats attachmentStats = AttachmentStats.builder().totalSize(space.getAttachmentSize()).build();
        ContentSummary contentSummary = ContentSummary.builder().numberOfPages(space.getPageBlogDraftCount()).numberOfAttachments(space.getAttachmentCount()).numberOfTeamCalendars(space.getTeamCalendarCount()).attachments(attachmentStats).lastModified(space.getLastModified()).build();
        return new SpaceDto(space.getKey(), space.getId(), space.getName(), contentSummary, space.getStatus() != null ? ProgressDto.fromStatus(space.getStatus()) : null, space.getEstimatedMigrationTime() != null ? Long.valueOf(space.getEstimatedMigrationTime() / 1000L) : null, space.getSpaceType() != null ? SpaceType.forName((String)space.getSpaceType()) : null);
    }

    private Collection<SpaceDto> getSpaceDTOsForNewSpaceSelector(List<SpaceWithStatisticResult> spacesWithStatistics) {
        return spacesWithStatistics.stream().map(this::mapSpaceWithStatisticResultToSpaceDto).collect(Collectors.toList());
    }
}

