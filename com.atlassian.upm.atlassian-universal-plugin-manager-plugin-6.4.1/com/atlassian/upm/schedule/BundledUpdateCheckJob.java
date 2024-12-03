/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.schedule;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.BundledUpdateInfo;
import com.atlassian.upm.core.BundledUpdateInfoStore;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.schedule.AbstractUpmScheduledJob;
import com.atlassian.upm.schedule.BundledUpdateTask;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.Serializable;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundledUpdateCheckJob
extends AbstractUpmScheduledJob {
    private static final Logger log = LoggerFactory.getLogger(BundledUpdateCheckJob.class);
    private final ApplicationProperties applicationProperties;
    private final AsynchronousTaskManager asynchronousTaskManager;
    private final AuditLogService auditLogService;
    private final BundledUpdateInfoStore bundledUpdateInfoStore;
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;
    private final PluginDownloadService pluginDownloadService;
    private final PluginInstallationService pluginInstallationService;

    public BundledUpdateCheckJob(ApplicationProperties applicationProperties, AsynchronousTaskManager asynchronousTaskManager, AuditLogService auditLogService, BundledUpdateInfoStore bundledUpdateInfoStore, I18nResolver i18nResolver, PluginAccessor pluginAccessor, PluginDownloadService pluginDownloadService, PluginInstallationService pluginInstallationService, UpmScheduler scheduler) {
        super(scheduler);
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.asynchronousTaskManager = Objects.requireNonNull(asynchronousTaskManager, "asynchronousTaskManager");
        this.auditLogService = Objects.requireNonNull(auditLogService, "auditLogService");
        this.bundledUpdateInfoStore = Objects.requireNonNull(bundledUpdateInfoStore, "bundledUpdateInfoStore");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginDownloadService = Objects.requireNonNull(pluginDownloadService, "pluginDownloadService");
        this.pluginInstallationService = Objects.requireNonNull(pluginInstallationService, "pluginInstallationService");
    }

    @Override
    public DateTime getStartTime() {
        return new DateTime();
    }

    @Override
    public Option<Duration> getInterval() {
        return Option.none();
    }

    @Override
    public void executeInternal(UpmScheduler.RunMode runMode) throws Exception {
        int curBuildNumber = Integer.parseInt(this.applicationProperties.getBuildNumber());
        String curVersion = this.applicationProperties.getVersion();
        for (BundledUpdateInfo info : this.bundledUpdateInfoStore.getUpdateInfo()) {
            String description = this.describeUpdateItems(info);
            if (curBuildNumber == info.getPlatformTargetBuildNumber()) {
                this.auditLogService.logI18nMessage("upm.bundledUpdate.starting", description, this.applicationProperties.getDisplayName(), curVersion);
                log.warn(this.i18nResolver.getText("upm.bundledUpdate.starting", new Serializable[]{description, this.applicationProperties.getDisplayName(), curVersion}));
                this.asynchronousTaskManager.executeAsynchronousTask(new BundledUpdateTask(info, this.bundledUpdateInfoStore, this.i18nResolver, this.pluginAccessor, this.pluginDownloadService, this.pluginInstallationService));
                continue;
            }
            if (curBuildNumber > info.getPlatformTargetBuildNumber()) {
                this.bundledUpdateInfoStore.setUpdateInfo(Option.none(BundledUpdateInfo.class));
                log.warn(this.i18nResolver.getText("upm.bundledUpdate.cancelled.obsolete", new Serializable[]{description, this.applicationProperties.getDisplayName(), Integer.valueOf(info.getPlatformTargetBuildNumber()), Integer.valueOf(curBuildNumber)}));
                continue;
            }
            log.warn(this.i18nResolver.getText("upm.bundledUpdate.deferred", new Serializable[]{description, this.applicationProperties.getDisplayName(), Integer.valueOf(info.getPlatformTargetBuildNumber())}));
        }
    }

    private String describeUpdateItems(BundledUpdateInfo info) {
        return StreamSupport.stream(info.getUpdateItems().spliterator(), false).map(i -> i.getName() + " " + i.getVersion()).collect(Collectors.joining(", "));
    }
}

