/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginInformation
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.version;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.version.AppOutdatedInfoProvider;
import com.atlassian.migration.agent.service.version.PluginStatus;
import com.atlassian.migration.agent.service.version.PluginVersionInfo;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginInformation;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.google.common.annotations.VisibleForTesting;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class DefaultPluginVersionManager
implements PluginVersionManager,
JobRunner {
    private static final Logger log = ContextLoggerFactory.getLogger(DefaultPluginVersionManager.class);
    private static final JobRunnerKey PLUGIN_VERSION_CHECK_JOB_KEY = JobRunnerKey.of((String)"migration-plugin:plugin-version-checker");
    private final MigrationAgentConfiguration migrationAgentConfiguration;
    private final AppOutdatedInfoProvider appOutdatedInfoProvider;
    private final SchedulerService schedulerService;
    private final Supplier<PluginSettings> pluginSettingsSupplier;
    private final Clock clock;
    private final PluginAccessor pluginAccessor;
    private final Duration pluginStatusTtl;
    private final TransactionTemplate transactionTemplate;
    private String pluginVersion;
    private String pluginStatusKey;

    public DefaultPluginVersionManager(MigrationAgentConfiguration migrationAgentConfiguration, PluginAccessor pluginAccessor, AppOutdatedInfoProvider appOutdatedInfoProvider, SchedulerService schedulerService, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate) {
        this(migrationAgentConfiguration, pluginAccessor, appOutdatedInfoProvider, schedulerService, () -> ((PluginSettingsFactory)pluginSettingsFactory).createGlobalSettings(), Clock.systemUTC(), Duration.ofHours(1L), transactionTemplate);
    }

    @VisibleForTesting
    DefaultPluginVersionManager(MigrationAgentConfiguration migrationAgentConfiguration, PluginAccessor pluginAccessor, AppOutdatedInfoProvider appOutdatedInfoProvider, SchedulerService schedulerService, Supplier<PluginSettings> pluginSettingsSupplier, Clock clock, Duration pluginStatusTtl, TransactionTemplate transactionTemplate) {
        this.migrationAgentConfiguration = migrationAgentConfiguration;
        this.appOutdatedInfoProvider = appOutdatedInfoProvider;
        this.schedulerService = schedulerService;
        this.pluginSettingsSupplier = pluginSettingsSupplier;
        this.clock = clock;
        this.pluginStatusTtl = pluginStatusTtl;
        this.pluginAccessor = pluginAccessor;
        this.transactionTemplate = transactionTemplate;
    }

    @PostConstruct
    public void initialize() {
        this.pluginVersion = this.findPluginVersion();
        this.pluginStatusKey = String.format("%s:%s", this.migrationAgentConfiguration.getPluginKey(), "mp-status");
        this.schedulerService.registerJobRunner(PLUGIN_VERSION_CHECK_JOB_KEY, (JobRunner)this);
        this.getPluginVersionInfo("CCMA-STARTUP-VERSION-CHECK");
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(PLUGIN_VERSION_CHECK_JOB_KEY);
    }

    @Override
    public String getPluginVersion() {
        return this.pluginVersion;
    }

    @Override
    public Boolean isTestVersion() {
        return this.pluginVersion.toLowerCase().endsWith("-snapshot");
    }

    @Override
    public Optional<PluginVersionInfo> getPluginVersionInfo(String cloudId) {
        if (this.isPluginVersionUnknown()) {
            log.warn("Current version of the plugin is unknown. Will assume that plugin version is not outdated.");
            return Optional.empty();
        }
        PluginSettings pluginSettings = this.pluginSettingsSupplier.get();
        Object pluginStatusObject = pluginSettings.get(this.pluginStatusKey);
        if (pluginStatusObject == null) {
            this.schedulePluginStatusUpdate(cloudId);
            return Optional.empty();
        }
        String pluginStatusAsString = Objects.toString(pluginStatusObject);
        try {
            PluginStatus pluginStatus = Jsons.readValue(pluginStatusAsString, PluginStatus.class);
            if (!this.getPluginVersion().equals(pluginStatus.pluginVersionLastChecked)) {
                this.schedulePluginStatusUpdate(cloudId);
                return Optional.empty();
            }
            Duration statusAge = Duration.between(Instant.ofEpochMilli(pluginStatus.timestamp), this.clock.instant());
            if (statusAge.toMillis() > this.pluginStatusTtl.toMillis()) {
                this.schedulePluginStatusUpdate(cloudId);
            }
            return Optional.of(new PluginVersionInfo(pluginStatus.outdated, pluginStatus.upgradeBy));
        }
        catch (JsonSerializingException ex) {
            log.error("Plugin Settings object contains un-parsable value [{}] under the key {}. Going to delete it.", new Object[]{pluginStatusAsString, this.pluginStatusKey, ex});
            pluginSettings.remove(this.pluginStatusKey);
            this.schedulePluginStatusUpdate(cloudId);
            return Optional.empty();
        }
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        String cloudId = (String)request.getJobConfig().getParameters().get("cloudId");
        Optional<AppOutdatedInfoProvider.IsOutdatedResponse> result = this.appOutdatedInfoProvider.getPluginOutdatedVersionInfo(cloudId, this.pluginVersion);
        if (!result.isPresent()) {
            return JobRunnerResponse.failed((String)"Failed to check plugin version");
        }
        AppOutdatedInfoProvider.IsOutdatedResponse response = result.get();
        boolean isOutdated = response.isAppOutdated();
        LocalDate upgradeBy = this.getPluginUpgradeBy(response, isOutdated);
        PluginStatus pluginStatus = new PluginStatus(this.pluginVersion, isOutdated, upgradeBy, this.clock.instant().toEpochMilli());
        this.pluginSettingsSupplier.get().put(this.pluginStatusKey, (Object)Jsons.valueAsString(pluginStatus));
        log.debug("Plugin status updated. New value: {}", (Object)pluginStatus);
        return JobRunnerResponse.success();
    }

    private LocalDate getPluginUpgradeBy(AppOutdatedInfoProvider.IsOutdatedResponse response, boolean isOutdated) {
        if (!isOutdated && response.getNextRelease() != null) {
            return response.getNextRelease().getUpgradeBy();
        }
        return null;
    }

    private void schedulePluginStatusUpdate(String cloudId) {
        this.transactionTemplate.execute(() -> {
            if (CollectionUtils.isNotEmpty((Collection)this.schedulerService.getJobsByJobRunnerKey(PLUGIN_VERSION_CHECK_JOB_KEY))) {
                log.debug("Plugin version check job already scheduled. Will skip this plugin version check request.");
                return null;
            }
            try {
                this.schedulerService.scheduleJobWithGeneratedId(JobConfig.forJobRunnerKey((JobRunnerKey)PLUGIN_VERSION_CHECK_JOB_KEY).withParameters(Collections.singletonMap("cloudId", cloudId)));
                log.debug("Scheduled job {} to check in Marketplace if plugin is outdated", (Object)PLUGIN_VERSION_CHECK_JOB_KEY);
            }
            catch (SchedulerServiceException e) {
                log.error("Failed to schedule plugin version check job", (Throwable)e);
            }
            return null;
        });
    }

    private String findPluginVersion() {
        try {
            String pluginVersionOverride = this.migrationAgentConfiguration.getPluginVersion();
            if (StringUtils.isNotEmpty((String)pluginVersionOverride)) {
                log.warn("Using overridden plugin version: {}", (Object)pluginVersionOverride);
                return pluginVersionOverride;
            }
            String version = this.findPluginVersionOrThrow(this.pluginAccessor);
            log.debug("Found version {} for plugin with key {}.", (Object)version, (Object)this.migrationAgentConfiguration.getPluginKey());
            return version;
        }
        catch (RuntimeException e) {
            log.warn("Plugin version could not be determined for plugin with key {}. Reason={}.", new Object[]{this.migrationAgentConfiguration.getPluginKey(), e.getMessage(), e});
            return "UNKNOWN";
        }
    }

    private String findPluginVersionOrThrow(PluginAccessor pluginAccessor) {
        Plugin plugin = pluginAccessor.getPlugin(this.migrationAgentConfiguration.getPluginKey());
        if (plugin == null) {
            throw new IllegalStateException("Plugin accessor failed to find plugin with key " + this.migrationAgentConfiguration.getPluginKey());
        }
        return Optional.ofNullable(plugin.getPluginInformation()).map(PluginInformation::getVersion).filter(StringUtils::isNotBlank).orElseThrow(() -> new IllegalStateException("Found plugin, but failed to find its version."));
    }

    private boolean isPluginVersionUnknown() {
        return "UNKNOWN".equals(this.pluginVersion);
    }
}

