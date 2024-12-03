/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.status.service.systeminfo.UsageInfo
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.util.concurrent.UncheckedExecutionException
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.UsageInfo;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import com.atlassian.migration.agent.model.stats.GlobalEntitiesStats;
import com.atlassian.migration.agent.model.stats.InstanceStats;
import com.atlassian.migration.agent.model.stats.ServerStats;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.model.stats.UsersGroupsStats;
import com.atlassian.migration.agent.service.FailedToLoadStatsException;
import com.atlassian.migration.agent.service.NetworkStatisticsService;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.StatsStoringService;
import com.atlassian.migration.agent.service.catalogue.model.GlobalEntitiesExecutionState;
import com.atlassian.migration.agent.service.cloud.LegalService;
import com.atlassian.migration.agent.service.extract.GlobalEntityExtractionService;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.SingleJobExecutor;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.store.ContentStatisticsStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultStatisticsService
implements StatisticsService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultStatisticsService.class);
    private final SystemInformationService systemInformationService;
    private final StatsStoringService statsStoringService;
    private final ContentStatisticsStore contentStatisticsStore;
    private final PluginTransactionTemplate ptx;
    private final SingleJobExecutor<ServerStats> executor;
    private final SpaceManager spaceManager;
    private final NetworkStatisticsService networkStatisticsService;
    private final LegalService legalService;
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final StepStore stepStore;
    private final GlobalEntityExtractionService globalEntityExtractionService;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;
    private EventPublisher eventPublisher;
    private MigrationAgentConfiguration migrationAgentConfiguration;
    private static final Duration SPACE_STATS_CACHE_TIME = Duration.of(3L, ChronoUnit.DAYS);
    private final LoadingCache<String, SpaceStats> spaceStatisticsLoadingCache;

    public DefaultStatisticsService(SystemInformationService systemInformationService, StatsStoringService statsStoringService, ContentStatisticsStore contentStatisticsStore, PluginTransactionTemplate ptx, SpaceManager spaceManager, NetworkStatisticsService networkStatisticsService, LegalService legalService, UserGroupExtractFacade userGroupExtractFacade, StepStore stepStore, GlobalEntityExtractionService globalEntityExtractionService, MigrationTimeEstimationUtils migrationTimeEstimationUtils, EventPublisher eventPublisher, MigrationAgentConfiguration migrationAgentConfiguration) {
        this(systemInformationService, statsStoringService, contentStatisticsStore, ptx, new SingleJobExecutor<ServerStats>("SiteSummaryCalculator"), spaceManager, networkStatisticsService, legalService, userGroupExtractFacade, stepStore, globalEntityExtractionService, migrationTimeEstimationUtils, eventPublisher, migrationAgentConfiguration);
    }

    @VisibleForTesting
    DefaultStatisticsService(SystemInformationService systemInformationService, StatsStoringService statsStoringService, final ContentStatisticsStore contentStatisticsStore, final PluginTransactionTemplate ptx, SingleJobExecutor<ServerStats> singleJobExecutor, SpaceManager spaceManager, NetworkStatisticsService networkStatisticsService, LegalService legalService, UserGroupExtractFacade userGroupExtractFacade, StepStore stepStore, GlobalEntityExtractionService globalEntityExtractionService, MigrationTimeEstimationUtils migrationTimeEstimationUtils, EventPublisher eventPublisher, MigrationAgentConfiguration migrationAgentConfiguration) {
        this.systemInformationService = systemInformationService;
        this.statsStoringService = statsStoringService;
        this.contentStatisticsStore = contentStatisticsStore;
        this.ptx = ptx;
        this.executor = singleJobExecutor;
        this.spaceManager = spaceManager;
        this.networkStatisticsService = networkStatisticsService;
        this.legalService = legalService;
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
        this.stepStore = stepStore;
        this.globalEntityExtractionService = globalEntityExtractionService;
        this.spaceStatisticsLoadingCache = CacheBuilder.newBuilder().expireAfterWrite(SPACE_STATS_CACHE_TIME.toMillis(), TimeUnit.MILLISECONDS).build((CacheLoader)new CacheLoader<String, SpaceStats>(){

            public SpaceStats load(String spaceKey) {
                return ptx.read(() -> contentStatisticsStore.loadSpaceStatistics(spaceKey));
            }

            public Map<String, SpaceStats> loadAll(Iterable<? extends String> spaceKeys) throws Exception {
                return ptx.read(() -> contentStatisticsStore.loadSpaceStatistics((Collection<String>)ImmutableList.copyOf((Iterable)spaceKeys))).stream().collect(Collectors.toMap(SpaceStats::getSpaceKey, spaceStats -> spaceStats));
            }
        });
        this.eventPublisher = eventPublisher;
        this.migrationAgentConfiguration = migrationAgentConfiguration;
    }

    private static boolean nonZeroSpaces(StatsStoringService.Stored<ServerStats> serverStepStored) {
        return serverStepStored.getData().getInstanceStats().getNumberOfSpaces() != 0;
    }

    private static boolean isStoredStatsStale(Instant storedTime) {
        return storedTime.isBefore(Instant.now().minus(5L, ChronoUnit.MINUTES));
    }

    @PostConstruct
    public void initialize() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void cleanUp() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        try {
            if (event.getPlugin().getKey().equals(this.migrationAgentConfiguration.getPluginKey())) {
                log.info("CCMA Plugin enabled, calculating server statistics");
                this.executor.execute(this::calculateServerStats);
            }
        }
        catch (Exception e) {
            log.error("Failed to calculate server statistics after plugin is enabled due to :", (Throwable)e);
        }
    }

    @Override
    public ServerStats loadServerStatistics() {
        ServerStats serverStats;
        Optional<StatsStoringService.Stored<ServerStats>> maybeStoredStats = this.statsStoringService.loadServerStats(() -> ((SystemInformationService)this.systemInformationService).getConfluenceInfo());
        if (maybeStoredStats.isPresent() && DefaultStatisticsService.nonZeroSpaces(maybeStoredStats.get())) {
            StatsStoringService.Stored<ServerStats> storedStats = maybeStoredStats.get();
            if (DefaultStatisticsService.isStoredStatsStale(storedStats.getStoredTime())) {
                this.executor.execute(this::calculateServerStats);
            }
            serverStats = storedStats.getData();
        } else {
            CompletableFuture<ServerStats> future = this.executor.execute(this::calculateServerStats);
            try {
                serverStats = future.get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new FailedToLoadStatsException("Failed to load server statistics", e);
            }
        }
        return serverStats;
    }

    @Override
    public Collection<SpaceStats> loadSpaceStatistics(Collection<String> spaceKeys) {
        try {
            return this.spaceStatisticsLoadingCache.getAll(spaceKeys).values().asList();
        }
        catch (ExecutionException e) {
            throw new UncheckedExecutionException((Throwable)e);
        }
    }

    @Override
    public SpaceStats loadSpaceStatistics(String spaceKey) {
        return (SpaceStats)this.spaceStatisticsLoadingCache.getUnchecked((Object)spaceKey);
    }

    @Override
    public UsersGroupsStats getUsersGroupsStatistics(UserMigrationType userMigrationType, Collection<String> spaceKeys) {
        int numGroups;
        int numUsers;
        if (userMigrationType.equals((Object)UserMigrationType.NONE)) {
            return new UsersGroupsStats(0, 0, Duration.ofMillis(0L));
        }
        if (userMigrationType.equals((Object)UserMigrationType.ALL)) {
            UsageInfo usageInfo = this.systemInformationService.getUsageInfo();
            numUsers = usageInfo.getLocalUsers();
            numGroups = this.userGroupExtractFacade.getAllGroupNames().size();
        } else {
            numUsers = this.userGroupExtractFacade.getUsersFromSpaces(spaceKeys).size();
            numGroups = this.userGroupExtractFacade.getGroupsFromSpaces(spaceKeys).size();
        }
        Duration totalMigrationTime = MigrationTimeEstimationUtils.estimateTotalUserGroupMigrationTime(numUsers, numGroups);
        return new UsersGroupsStats(numUsers, numGroups, totalMigrationTime);
    }

    @Override
    public GlobalEntitiesStats getGlobalEntitiesStatistics(String planId) {
        Optional<GlobalEntitiesStats> planGlobalEntitiesStats = this.getGlobalEntitiesStatsForPlan(planId);
        return planGlobalEntitiesStats.orElseGet(() -> new GlobalEntitiesStats(this.globalEntityExtractionService.getGlobalTemplatesCount(), this.globalEntityExtractionService.getSystemTemplatesCount()));
    }

    private Optional<GlobalEntitiesStats> getGlobalEntitiesStatsForPlan(String planId) {
        if (Objects.nonNull(planId)) {
            try {
                Optional step = this.ptx.read(() -> this.stepStore.getStep(planId, StepType.GLOBAL_ENTITIES_EXPORT));
                if (step.isPresent() && ((Step)step.get()).getProgress().getStatus().isCompleted() && !((Step)step.get()).getExecutionState().isEmpty()) {
                    GlobalEntitiesExecutionState file = Jsons.readValue(((Step)step.get()).getExecutionState(), GlobalEntitiesExecutionState.class);
                    return Optional.of(new GlobalEntitiesStats(file.getTotalGlobalPageTemplates(), file.getTotalEditedSystemTemplates()));
                }
            }
            catch (Exception e) {
                log.error("Error while fetching plan stats from DB: {}", (Object)e.getMessage(), (Object)e);
            }
        }
        return Optional.empty();
    }

    @VisibleForTesting
    ServerStats calculateServerStats() {
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        UsageInfo usageInfo = this.systemInformationService.getUsageInfo();
        InstanceStats instanceStats = InstanceStats.builder().version(confluenceInfo.getVersion()).buildNumber(confluenceInfo.getBuildNumber()).numberOfSpaces(usageInfo.getTotalSpaces()).numberOfUsers(usageInfo.getLocalUsers()).numberOfGroups(usageInfo.getLocalGroups()).build();
        Collection<SpaceStats> spaceStats = this.loadSpaceStatistics(this.spaceManager.getAllSpaces().stream().map(Space::getKey).collect(Collectors.toList()));
        List<ContentSummary> spaceSummaries = spaceStats.stream().map(SpaceStats::getSummary).collect(Collectors.toList());
        ContentSummary contentSummary = this.ptx.read(this.contentStatisticsStore::loadContentSummary);
        Duration userGroupMigrationTime = MigrationTimeEstimationUtils.estimateTotalUserGroupMigrationTime(instanceStats.getNumberOfUsers(), instanceStats.getNumberOfGroups());
        Duration spaceMigrationTime = this.migrationTimeEstimationUtils.estimateTotalSpaceMigrationTime(spaceSummaries);
        long bandwidthKBPS = NetworkStatisticsService.getUncalculatedBandwidthKBPS();
        if (this.legalService.getRememberLegalOptIn()) {
            bandwidthKBPS = this.networkStatisticsService.measureBandwidthKBPS();
        }
        ServerStats serverStats = new ServerStats(instanceStats, contentSummary, userGroupMigrationTime, spaceMigrationTime, MigrationTimeEstimationUtils.getBaseMigrationTime(), bandwidthKBPS);
        this.statsStoringService.storeServerStats(serverStats);
        return serverStats;
    }
}

