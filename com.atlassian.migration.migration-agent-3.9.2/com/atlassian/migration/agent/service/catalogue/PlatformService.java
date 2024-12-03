/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.migration.app.dto.AppContainerDetails
 *  com.atlassian.migration.app.dto.MigrationPath
 *  com.atlassian.migration.utils.MigrationStatusCalculator$OverallAppMigrationStatus
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.AppListenerIssueType;
import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ExcludeApp;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.MapiPlanMapping;
import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.MigrationStatus;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.NeededInCloudApp;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.SpaceAttachmentsOnlyTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TransferStatus;
import com.atlassian.migration.agent.mapi.entity.MapiTaskStatus;
import com.atlassian.migration.agent.service.FeatureFlagService;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.FeatureFlagActionSubject;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.catalogue.CloudLocation;
import com.atlassian.migration.agent.service.catalogue.ContainerCreateRequest;
import com.atlassian.migration.agent.service.catalogue.ContainersFetchResponse;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.catalogue.MigrationCreateRequest;
import com.atlassian.migration.agent.service.catalogue.MigrationDetails;
import com.atlassian.migration.agent.service.catalogue.MigrationScopeCreateRequest;
import com.atlassian.migration.agent.service.catalogue.ServerLocation;
import com.atlassian.migration.agent.service.catalogue.TransferProgressRequest;
import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.atlassian.migration.agent.service.catalogue.model.AppContainer;
import com.atlassian.migration.agent.service.catalogue.model.ConfluenceLicenseDetails;
import com.atlassian.migration.agent.service.catalogue.model.ConfluenceSpaceContainer;
import com.atlassian.migration.agent.service.catalogue.model.MigrationDomainsAllowlistResponse;
import com.atlassian.migration.agent.service.catalogue.model.SiteContainer;
import com.atlassian.migration.agent.service.catalogue.model.TransferResponseList;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.DefaultPlanService;
import com.atlassian.migration.agent.service.impl.MapiPlanMappingService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.dto.AppContainerDetails;
import com.atlassian.migration.app.dto.MigrationPath;
import com.atlassian.migration.utils.MigrationStatusCalculator;
import com.atlassian.plugin.Plugin;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class PlatformService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PlatformService.class);
    private static final int CONTAINERS_PAGE_SIZE = 250;
    private static final String APP_MIGRATION_DEV_LOOP = "app.migration.dev.loop";
    public static final String LICENSES_PROPERTIES_ENTRY = "Licenses";
    private final SENSupplier senSupplier;
    private final LicenseHandler licenseHandler;
    private final SystemInformationService systemInformationService;
    private final SpaceManager spaceManager;
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final DefaultRegistrar defaultRegistrar;
    private final PluginManager pluginManager;
    private final PluginVersionManager pluginVersionManager;
    private final MigrationAppAggregatorService appAggregatorService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final FeatureFlagService featureFlagService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final MapiTaskMappingService mapiTaskMappingService;
    private final MapiPlanMappingService mapiPlanMappingService;

    public PlatformService(SENSupplier senSupplier, LicenseHandler licenseHandler, SystemInformationService systemInformationService, SpaceManager spaceManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, DefaultRegistrar defaultRegistrar, MigrationAppAggregatorService appAggregatorService, PluginManager pluginManager, PluginVersionManager pluginVersionManager, MigrationDarkFeaturesManager migrationDarkFeaturesManager, FeatureFlagService featureFlagService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MapiTaskMappingService mapiTaskMappingService, MapiPlanMappingService mapiPlanMappingService) {
        this.senSupplier = senSupplier;
        this.licenseHandler = licenseHandler;
        this.systemInformationService = systemInformationService;
        this.spaceManager = spaceManager;
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
        this.defaultRegistrar = defaultRegistrar;
        this.appAggregatorService = appAggregatorService;
        this.pluginManager = pluginManager;
        this.pluginVersionManager = pluginVersionManager;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.featureFlagService = featureFlagService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.mapiTaskMappingService = mapiTaskMappingService;
        this.mapiPlanMappingService = mapiPlanMappingService;
    }

    public MigrationDetails publishMigrationDetailsForAllListeners(Plan plan) {
        String cloudId = plan.getCloudSite().getCloudId();
        MigrationDetails mcsMigrationDetails = this.createMigrationInMcs(cloudId, plan, true);
        return new MigrationDetails(mcsMigrationDetails.migrationScopeId, mcsMigrationDetails.migrationId);
    }

    public Set<AppContainerDetails> getAppContainers(String cloudId, String migrationId) {
        return this.getContainers(cloudId, migrationId, AbstractContainer.Type.App, false).stream().map(container -> {
            AppContainer appContainer = (AppContainer)container;
            return new AppContainerDetails(appContainer.getSourceKey(), appContainer.getDestinationKey(), appContainer.getContainerId());
        }).collect(Collectors.toSet());
    }

    @VisibleForTesting
    List<AbstractContainer> getContainers(String cloudId, String migrationId, AbstractContainer.Type type, boolean expand) {
        ArrayList<AbstractContainer> containers = new ArrayList<AbstractContainer>();
        String nextId = null;
        do {
            ContainersFetchResponse response = this.enterpriseGatekeeperClient.getContainersForMigration(cloudId, migrationId, type, 250, nextId, expand);
            List<AbstractContainer> containersInPage = response.getContainers();
            nextId = response.getNextId();
            if (containersInPage == null) continue;
            containers.addAll(containersInPage);
        } while (nextId != null);
        return containers;
    }

    public Optional<AppsProgressDto> getAppsProgress(Plan plan) {
        try {
            List<AppsProgressDto.App> apps = Collections.emptyList();
            Optional<MigrateAppsTask> maybeMigrateAppsTask = DefaultPlanService.getMigrateAppsTask(plan);
            MigrationStatusCalculator.OverallAppMigrationStatus aggregateStatus = null;
            if (maybeMigrateAppsTask.isPresent()) {
                MigrateAppsTask migrateAppsTask = maybeMigrateAppsTask.get();
                aggregateStatus = this.getAppAggregateStatus(migrateAppsTask.getProgress());
                String migrationId = plan.getMigrationId();
                String cloudId = plan.getCloudSite().getCloudId();
                if (this.shouldRetrieveAppsProgress(plan)) {
                    apps = this.getMigratableAppsProgress(cloudId, migrationId);
                    apps.addAll(this.getInstallOnlyAppsProgress(migrateAppsTask));
                } else {
                    Set<String> appKeys = this.getAutomatedServerAppKeysForMigration(migrateAppsTask, true);
                    apps = appKeys.stream().map(appKey -> this.createAppProgressDto((String)appKey, 0, AbstractContainer.ContainerStatus.READY.name(), "Ready to start migration.")).collect(Collectors.toList());
                }
            }
            apps.sort(Comparator.comparing(AppsProgressDto.App::getServerAppName));
            return Optional.of(new AppsProgressDto(aggregateStatus, apps));
        }
        catch (Exception e) {
            log.error("Error when trying to get app progress for plan {}.", (Object)plan.getId(), (Object)e);
            return Optional.empty();
        }
    }

    private MigrationStatusCalculator.OverallAppMigrationStatus getAppAggregateStatus(@Nullable Progress appProgress) {
        if (appProgress != null && appProgress.getStatus() == ExecutionStatus.FAILED) {
            return MigrationStatusCalculator.OverallAppMigrationStatus.FAILED;
        }
        return null;
    }

    private boolean shouldRetrieveAppsProgress(Plan plan) {
        return plan.getProgress().getStatus().canTriggerAppMigration() && plan.getMigrationId() != null;
    }

    private List<AppsProgressDto.App> getMigratableAppsProgress(String cloudId, String migrationId) {
        List<AppsProgressDto.App> appProgress = this.defaultRegistrar.getAppMigrationServiceClient().getAppProgress(cloudId, migrationId);
        ArrayList<AppsProgressDto.App> result = new ArrayList<AppsProgressDto.App>();
        appProgress.forEach(app -> result.add(new AppsProgressDto.App(this.fixAppNameIfNeeded((AppsProgressDto.App)app), app.getServerAppKey(), app.getContainerId(), app.getCloudAppKey(), app.getCompletionPercent(), app.getStatus(), app.getStatusMessage(), app.getLastUpdatedAt(), app.getAppVendorName(), app.getContactVendorUrl())));
        return result;
    }

    private String fixAppNameIfNeeded(AppsProgressDto.App app) {
        Plugin plugin = this.pluginManager.getPlugin(app.getServerAppKey());
        if (plugin != null && app.getServerAppKey().equals(app.getServerAppName())) {
            return plugin.getName();
        }
        return app.getServerAppName();
    }

    private List<AppsProgressDto.App> getInstallOnlyAppsProgress(MigrateAppsTask appsTask) {
        Set<String> appKeys = this.getInstallOnlyApps(appsTask);
        return appKeys.stream().map(key -> this.createAppProgressDto((String)key, 100, AbstractContainer.ContainerStatus.SUCCESS.name(), "You have successfully migrated this app.")).collect(Collectors.toList());
    }

    public MigrationDetails createMigrationInMcs(String cloudId, Plan plan, Boolean createAppContainersForAllListeners) {
        Optional<MapiPlanMapping> mapiPlanMapping;
        Optional<MapiTaskMapping> mapiTaskMapping = this.mapiTaskMappingService.getTaskMapping(plan.getId(), Optional.of(ImmutableList.of((Object)((Object)MapiTaskStatus.CHECKS_IN_PROGRESS), (Object)((Object)MapiTaskStatus.CHECKS_COMPLETED))), Optional.of(ImmutableList.of((Object)CommandName.MIGRATE.getName())));
        MigrationCreateRequest createMigrationRequest = new MigrationCreateRequest(plan.getName(), new ServerLocation(this.systemInformationService.getConfluenceInfo().getBaseUrl(), this.licenseHandler.getServerId(), Collections.singletonMap("CONFLUENCE", this.senSupplier.get())), new CloudLocation(cloudId, plan.getCloudSite().getCloudUrl()), createAppContainersForAllListeners != false || plan.getMigrationTag() == MigrationTag.TEST, this.migrationDarkFeaturesManager.isForceResetFlagEnabled(), this.createMigrationProperties(createAppContainersForAllListeners, mapiTaskMapping));
        MigrationDetails migrationDetails = this.enterpriseGatekeeperClient.createMigration(cloudId, createMigrationRequest);
        if (mapiTaskMapping.isPresent() && (mapiPlanMapping = this.mapiPlanMappingService.getMapiPlanMapping(mapiTaskMapping.get().getJobId())).isPresent()) {
            mapiPlanMapping.get().setMigrationId(migrationDetails.migrationId);
            this.mapiPlanMappingService.saveMapiPlanMapping(mapiPlanMapping.get());
        }
        log.info("Created migration with ID {}, for cloud {}", (Object)migrationDetails.migrationId, (Object)cloudId);
        List<String> enabledMigrationFeatures = this.featureFlagService.getEnabledMigrationPluginFeatures();
        this.featureFlagService.saveFeatureFlagAnalyticEvent(FeatureFlagActionSubject.PLAN, plan.getId(), enabledMigrationFeatures.toString());
        log.info("Enabled Migration plugin feature flags for migrationId {} are {}", (Object)migrationDetails.migrationId, enabledMigrationFeatures);
        return migrationDetails;
    }

    public String createMigrationScopeInMcs(CloudSite cloudSite) {
        MigrationScopeCreateRequest migrationScopeCreateRequest = new MigrationScopeCreateRequest(new ServerLocation(this.systemInformationService.getConfluenceInfo().getBaseUrl(), this.licenseHandler.getServerId(), Collections.singletonMap("CONFLUENCE", this.senSupplier.get())), new CloudLocation(cloudSite.getCloudId(), cloudSite.getCloudUrl()));
        String migrationScopeId = this.enterpriseGatekeeperClient.createMigrationScope(cloudSite.getCloudId(), migrationScopeCreateRequest).getMigrationScopeId();
        log.info("Received migration scope id: {} for cloudId: {}", (Object)migrationScopeId, (Object)cloudSite.getCloudId());
        return migrationScopeId;
    }

    public void createContainersInMcs(String cloudId, String migrationId, Plan plan) {
        this.registerContainers(cloudId, migrationId, new ArrayList<AbstractContainer>(this.createProductContainers(plan.getTasks())));
    }

    public boolean updateSpaceContainerStatuses(Plan plan) {
        try {
            log.info("Updating the space container statuses for plan {}", (Object)plan.getId());
            Set<ConfluenceSpaceContainer> containerDetails = this.getConfluenceSpaceContainers(plan);
            this.callUpdateContainerStatusAPI(plan, containerDetails);
        }
        catch (Exception e) {
            log.warn("Status Update for space container in given plan {} failed. Exception:  ", (Object)plan.getId(), (Object)e);
            return false;
        }
        return this.pollForContainerStatus(plan);
    }

    public MigrationDomainsAllowlistResponse getDomainAllowList(String cloudId) {
        return this.enterpriseGatekeeperClient.getDomainAllowlist(cloudId);
    }

    private void registerContainers(String cloudId, String migrationId, List<AbstractContainer> allContainers) {
        Lists.partition(allContainers, (int)250).forEach(containers -> this.enterpriseGatekeeperClient.createContainers(cloudId, migrationId, new ContainerCreateRequest((List<AbstractContainer>)containers)));
        log.info("Created containers for migrationId {}", (Object)migrationId);
    }

    public Set<ConfluenceSpaceContainer> getConfluenceSpaceContainers(Plan plan) {
        return this.getContainers(plan.getCloudSite().getCloudId(), plan.getMigrationId(), AbstractContainer.Type.ConfluenceSpace, false).stream().map(ConfluenceSpaceContainer.class::cast).collect(Collectors.toSet());
    }

    public Set<SiteContainer> getSiteContainers(Plan plan) {
        return this.getContainers(plan.getCloudSite().getCloudId(), plan.getMigrationId(), AbstractContainer.Type.Site, false).stream().map(SiteContainer.class::cast).collect(Collectors.toSet());
    }

    private boolean pollForContainerStatus(Plan plan) {
        RetryPolicy retryPolicy = (RetryPolicy)new RetryPolicy().withMaxRetries(5).withDelay(2L, 4L, ChronoUnit.SECONDS).withMaxDuration(Duration.ofMinutes(1L)).handleResultIf(hasContainersWithReadyStatus -> hasContainersWithReadyStatus);
        return (Boolean)Failsafe.with((Policy)retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.hasContainerWithReadyStatusForSuccessfulSpaces(plan)) == false;
    }

    private boolean hasContainerWithReadyStatusForSuccessfulSpaces(Plan plan) {
        ArrayList<AbstractContainer> containers = new ArrayList<AbstractContainer>();
        Set unsuccessfulSpaceKeys = plan.getTasks().stream().filter(ConfluenceSpaceTask.class::isInstance).filter(spaceTask -> spaceTask.getProgress().getStatus().isUnsuccessful()).map(ConfluenceSpaceTask.class::cast).map(AbstractSpaceTask::getSpaceKey).collect(Collectors.toSet());
        String nextId = "";
        do {
            ContainersFetchResponse response = this.enterpriseGatekeeperClient.getContainersByStatusForMigration(plan.getCloudSite().getCloudId(), plan.getMigrationId(), ContainerType.ConfluenceSpace, AbstractContainer.ContainerStatus.READY, 250, nextId);
            List<AbstractContainer> containersInPage = response.getContainers();
            nextId = response.getNextId();
            if (containersInPage == null) continue;
            containers.addAll(containersInPage);
        } while (nextId != null);
        List successfulSpaceContainerWithReadyStatus = containers.stream().filter(ConfluenceSpaceContainer.class::isInstance).map(ConfluenceSpaceContainer.class::cast).filter(confluenceSpaceContainer -> !unsuccessfulSpaceKeys.contains(confluenceSpaceContainer.getKey())).collect(Collectors.toList());
        log.info("Number of successful spaces for plan {} with status READY in MCS: {} ", (Object)plan.getId(), (Object)successfulSpaceContainerWithReadyStatus.size());
        return !successfulSpaceContainerWithReadyStatus.isEmpty();
    }

    public MigrationAppAggregatorService.Hosting getHosting() {
        return this.licenseHandler.getAllProductLicenses().stream().anyMatch(BaseLicenseDetails::isDataCenter) ? MigrationAppAggregatorService.Hosting.datacenter : MigrationAppAggregatorService.Hosting.server;
    }

    public void callUpdateContainerStatusAPI(Plan plan, Set<ConfluenceSpaceContainer> containerDetails) {
        Map<String, Progress> spaceTasks = this.getSpaceProgressMap(plan);
        containerDetails.forEach(containerDetail -> this.updateContainersStatus(plan.getCloudSite().getCloudId(), plan.getMigrationId(), containerDetail.getContainerId(), ((Progress)spaceTasks.get(containerDetail.getKey())).getStatus().getContainerStatus(), ((Progress)spaceTasks.get(containerDetail.getKey())).getMessage()));
    }

    @NotNull
    private Map<String, Progress> getSpaceProgressMap(Plan plan) {
        return plan.getTasks().stream().filter(ConfluenceSpaceTask.class::isInstance).map(ConfluenceSpaceTask.class::cast).collect(Collectors.toMap(AbstractSpaceTask::getSpaceKey, Task::getProgress));
    }

    public void updateContainersStatus(String cloudId, String migrationId, String containerId, AbstractContainer.ContainerStatus status, String statusMessage) {
        this.enterpriseGatekeeperClient.updateMigrationStatusForContainers(cloudId, migrationId, containerId, status, statusMessage);
        log.info("Updated migration status with container ID {}, for migration {} on cloud {}", new Object[]{containerId, migrationId, cloudId});
    }

    private List<AbstractContainer> createProductContainers(List<Task> tasks) {
        ArrayList<AbstractContainer> containers = new ArrayList<AbstractContainer>();
        HashSet<SiteContainer.SiteSelection> siteSelections = new HashSet<SiteContainer.SiteSelection>();
        for (Task task : tasks) {
            Space space;
            AbstractSpaceTask spaceTask;
            if (task instanceof ConfluenceSpaceTask) {
                spaceTask = (ConfluenceSpaceTask)task;
                space = this.spaceManager.getSpace(spaceTask.getSpaceKey());
                if (!Objects.nonNull(space)) continue;
                containers.add(new ConfluenceSpaceContainer(Long.toString(space.getId()), spaceTask.getSpaceKey(), space.getName(), (Set<String>)ImmutableSet.of((Object)"DATA", (Object)"ATTACHMENTS")));
                continue;
            }
            if (task instanceof SpaceAttachmentsOnlyTask) {
                spaceTask = (SpaceAttachmentsOnlyTask)task;
                space = this.spaceManager.getSpace(spaceTask.getSpaceKey());
                if (!Objects.nonNull(space)) continue;
                containers.add(new ConfluenceSpaceContainer(Long.toString(space.getId()), spaceTask.getSpaceKey(), space.getName(), (Set<String>)ImmutableSet.of((Object)"ATTACHMENTS")));
                continue;
            }
            if (task instanceof MigrateUsersTask) {
                siteSelections.add(SiteContainer.SiteSelection.USERS);
                continue;
            }
            if (!(task instanceof MigrateGlobalEntitiesTask)) continue;
            siteSelections.add(SiteContainer.SiteSelection.GLOBAL_ENTITIES);
        }
        if (!siteSelections.isEmpty()) {
            containers.add(new SiteContainer(siteSelections));
        }
        return containers;
    }

    @Deprecated
    public void createAppContainers(String cloudId, String migrationId, List<Task> tasks, boolean createAppContainersForAllListeners) {
        Set<String> appKeys = null;
        if (!createAppContainersForAllListeners) {
            Optional<MigrateAppsTask> migrateTask = tasks.stream().filter(MigrateAppsTask.class::isInstance).map(MigrateAppsTask.class::cast).findAny();
            if (migrateTask.isPresent()) {
                MigrateAppsTask appsTask = migrateTask.get();
                appKeys = this.getAutomatedServerAppKeysForMigration(appsTask, false);
            }
        }
        this.createAppContainers(cloudId, migrationId, appKeys);
    }

    public void createAppContainers(String cloudId, String migrationId, @Nullable Set<String> serverAppKeysFilter) {
        Set<String> appKeys = serverAppKeysFilter == null ? this.defaultRegistrar.getRegisteredServerKeys() : serverAppKeysFilter;
        this.warnAppsWithProblems(appKeys);
        List<AbstractContainer> containers = appKeys.stream().flatMap(serverAppKey -> this.defaultRegistrar.getRegisteredCloudKeys((String)serverAppKey).stream().map(cloudAppKey -> new AppContainer((String)serverAppKey, (String)cloudAppKey))).collect(Collectors.toList());
        this.registerContainers(cloudId, migrationId, containers);
    }

    private Set<String> getAutomatedServerAppKeysForMigration(MigrateAppsTask migrateAppsTask, boolean includeInstallOnly) {
        Sets.SetView<String> difference = this.appsToBeConsidered(migrateAppsTask);
        return difference.stream().filter(neededInCloudApp -> this.pluginManager.isPluginInstalled((String)neededInCloudApp) != false && (includeInstallOnly || this.appAggregatorService.getCachedServerAppData((String)neededInCloudApp).getMigrationPath().equals((Object)MigrationPath.AUTOMATED))).collect(Collectors.toSet());
    }

    @VisibleForTesting
    Set<String> getAppProblemMessages(Set<String> appKeys) {
        return appKeys.stream().flatMap(appKey -> {
            boolean hasRegisteredListener;
            MigrationAppAggregatorResponse maaResponse = this.appAggregatorService.getCachedServerAppData((String)appKey);
            if (maaResponse == null) {
                return Stream.empty();
            }
            ImmutableList messages = Collections.emptyList();
            AppListenerIssueType issueType = null;
            MigrationPath migrationPath = maaResponse.getMigrationPath();
            boolean bl = hasRegisteredListener = !this.defaultRegistrar.getRegisteredCloudKeys((String)appKey).isEmpty();
            if (migrationPath == MigrationPath.INSTALL_ONLY && hasRegisteredListener) {
                messages = ImmutableList.of((Object)(appKey + " may not be install only because a server listener was found. This app will be ignored during production app migrations (i.e. with dev mode turned off). Contact Atlassian support to remove this app from install only list."));
                issueType = AppListenerIssueType.INSTALL_ONLY_WITH_SERVER_LISTENER;
            } else if (migrationPath == MigrationPath.AUTOMATED && !hasRegisteredListener) {
                messages = ImmutableList.of((Object)(appKey + " has an automated path but missing a server listener. This app will be ignored during app migration."));
                issueType = AppListenerIssueType.AUTOMATED_WITHOUT_SERVER_LISTENER;
            }
            if (issueType != null) {
                try {
                    EventDto event = this.analyticsEventBuilder.buildAppServerListenerIssueEvent((String)appKey, issueType);
                    this.analyticsEventService.saveAnalyticsEvent(event);
                }
                catch (Exception e) {
                    log.error("Failed to send analytics event for app server listener issue", (Throwable)e);
                }
            }
            return messages.stream();
        }).collect(Collectors.toSet());
    }

    private void warnAppsWithProblems(Set<String> appKeys) {
        this.getAppProblemMessages(appKeys).forEach(arg_0 -> ((Logger)log).warn(arg_0));
    }

    private Set<String> getInstallOnlyApps(MigrateAppsTask migrateAppsTask) {
        return this.appsToBeConsidered(migrateAppsTask).stream().filter(appKey -> this.pluginManager.isPluginInstalled((String)appKey) != false && this.appAggregatorService.getCachedServerAppData((String)appKey).getMigrationPath().equals((Object)MigrationPath.INSTALL_ONLY)).collect(Collectors.toSet());
    }

    @NotNull
    private Sets.SetView<String> appsToBeConsidered(MigrateAppsTask migrateAppsTask) {
        Set appsNeededInCloud = migrateAppsTask.getNeededInCloudApps().stream().map(NeededInCloudApp::getAppKey).collect(Collectors.toSet());
        Set excludedServerAppKeys = migrateAppsTask.getExcludedApps().stream().map(ExcludeApp::getAppKey).collect(Collectors.toSet());
        return Sets.difference(appsNeededInCloud, excludedServerAppKeys);
    }

    @NotNull
    private Map<String, Object> createMigrationProperties(Boolean appMigDevLoop, Optional<MapiTaskMapping> mapiTaskMapping) {
        HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("PluginVersion", this.pluginVersionManager.getPluginVersion());
        properties.put("Hosting", this.getHosting().toString());
        ConfluenceLicenseDetails confluenceLicenseDetails = this.senSupplier.getLicenseDetails();
        properties.put(LICENSES_PROPERTIES_ENTRY, confluenceLicenseDetails.toMigrationProperties());
        if (Boolean.TRUE.equals(appMigDevLoop)) {
            properties.put(APP_MIGRATION_DEV_LOOP, "true");
        }
        if (mapiTaskMapping.isPresent()) {
            HashMap<String, String> mapiMetadata = new HashMap<String, String>();
            mapiMetadata.put("jobId", mapiTaskMapping.get().getJobId());
            mapiMetadata.put("taskId", mapiTaskMapping.get().getTaskId());
            properties.put("mapi", mapiMetadata);
        }
        return properties;
    }

    private AppsProgressDto.App createAppProgressDto(String appKey, int percentComplete, String status, String statusMessage) {
        MigrationAppAggregatorResponse appAggregatorResponse = this.appAggregatorService.getCachedServerAppData(appKey);
        return AppsProgressDto.App.builder().serverAppKey(appKey).cloudAppKey(appAggregatorResponse != null ? appAggregatorResponse.getCloudKey() : "(Unknown)").serverAppName(AppAssessmentFacade.getAppName(appKey, this.pluginManager, appAggregatorResponse)).completionPercent(percentComplete).status(status).statusMessage(statusMessage).lastUpdatedAt(null).build();
    }

    public void updateMigrationStatusToMcs(Plan plan) {
        ExecutionStatus status = plan.getProgress().getStatus();
        MigrationStatus migrationStatus = MigrationStatus.convertStatusToMigrationStatus(status, Optional.empty());
        String cloudId = plan.getCloudSite().getCloudId();
        try {
            this.enterpriseGatekeeperClient.sendMigrationStatusToMCS(plan.getMigrationId(), cloudId, migrationStatus, plan.getProgress().getMessage());
        }
        catch (Exception e) {
            log.error("Error sending migration status to MCS for migrationID: {}. Error: {}", new Object[]{plan.getMigrationId(), e.getMessage(), e});
        }
    }

    public Optional<TransferResponseList> createTransfers(String cloudId, String migrationId, String containerId, List<String> operationKeys) {
        try {
            return Optional.of(this.enterpriseGatekeeperClient.createTransfers(cloudId, migrationId, containerId, operationKeys));
        }
        catch (Exception e) {
            log.error("Error creating transfers for containerId: {} in migrationId: {}. Error: {}", new Object[]{containerId, migrationId, e.getMessage(), e});
            return Optional.empty();
        }
    }

    public void updateTransferProgress(String cloudId, String migrationId, String transferId, TransferProgressRequest transferProgressRequest) {
        try {
            this.enterpriseGatekeeperClient.updateTransferProgress(cloudId, migrationId, transferId, transferProgressRequest);
        }
        catch (Exception e) {
            log.debug("Error updating transfer progress for transferId: {} in migrationId: {}. Error: {}", new Object[]{transferId, migrationId, e.getMessage(), e});
        }
    }

    public void updateTransferStatus(String cloudId, String migrationId, String transferId, TransferStatus status, String statusMessage) {
        try {
            this.enterpriseGatekeeperClient.updateTransferStatus(cloudId, migrationId, transferId, status, statusMessage);
        }
        catch (Exception e) {
            log.debug("Error updating transfer status for transferId: {} in migrationId: {}. Error: {}", new Object[]{transferId, migrationId, e.getMessage(), e});
        }
    }
}

