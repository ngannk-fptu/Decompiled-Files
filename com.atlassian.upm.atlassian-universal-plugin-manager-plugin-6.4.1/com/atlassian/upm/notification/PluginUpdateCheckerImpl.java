/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Days
 *  org.joda.time.Duration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.SysPersisted;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.license.LicensedAttributes;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.impl.LicensedPlugins;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.notification.ManualUpdateRequiredNotificationService;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.PluginUpdateChecker;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginUpdateCheckerImpl
implements PluginUpdateChecker {
    private static final Logger log = LoggerFactory.getLogger(PluginUpdateCheckerImpl.class);
    private final AuditLogService auditLogService;
    private final NotificationCache cache;
    private final PacClient pacClient;
    private final PluginRetriever pluginRetriever;
    private final PluginDownloadService downloadService;
    private final PluginInstallationService pluginInstaller;
    private final PluginLicenseRepository licenseRepository;
    private final SelfUpdateController selfUpdateController;
    private final SysPersisted sysPersisted;
    private final UpmScheduler scheduler;
    private final UpmInformation upm;
    private final SafeModeAccessor safeMode;
    private final ManualUpdateRequiredNotificationService manualUpdateNotificationService;
    private final HostLicenseInformation hostLicenseInformation;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public PluginUpdateCheckerImpl(AuditLogService auditLogService, NotificationCache cache, PacClient pacClient, PluginRetriever pluginRetriever, PluginDownloadService downloadService, PluginInstallationService pluginInstaller, PluginLicenseRepository licenseRepository, SelfUpdateController selfUpdateController, SysPersisted sysPersisted, UpmScheduler scheduler, UpmInformation upm, SafeModeAccessor safeMode, ManualUpdateRequiredNotificationService manualUpdateNotificationService, HostLicenseInformation hostLicenseInformation, LicensingUsageVerifier licensingUsageVerifier) {
        this.auditLogService = Objects.requireNonNull(auditLogService, "auditLogService");
        this.cache = Objects.requireNonNull(cache, "cache");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.downloadService = Objects.requireNonNull(downloadService, "downloadService");
        this.pluginInstaller = Objects.requireNonNull(pluginInstaller, "pluginInstaller");
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
        this.selfUpdateController = Objects.requireNonNull(selfUpdateController, "selfUpdateController");
        this.sysPersisted = Objects.requireNonNull(sysPersisted, "sysPersisted");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
        this.upm = Objects.requireNonNull(upm, "upm");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.manualUpdateNotificationService = Objects.requireNonNull(manualUpdateNotificationService, "manualUpdateNotificationService");
        this.hostLicenseInformation = Objects.requireNonNull(hostLicenseInformation, "hostLicenseInformation");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterable<AvailableAddonWithVersion> checkForUpdates(PluginUpdateChecker.UpdateCheckOptions options) {
        try {
            Collection<AvailableAddonWithVersion> allUpdates = options.isUserInitiated() ? this.pacClient.getUpdates() : this.pacClient.getUpdatesViaAutomatedJob();
            List<AvailableAddonWithVersion> filteredUpdates = StreamSupport.stream(allUpdates.spliterator(), false).filter(this.nonRecentIncompatibleUpdate().negate()).collect(Collectors.toList());
            List nonUpmUpdates = filteredUpdates.stream().filter(this.isUpm().negate()).collect(Collectors.toList());
            if (options.isUpdateNotifications()) {
                this.cache.setNotifications(NotificationType.PLUGIN_UPDATE_AVAILABLE, Collections.unmodifiableList(nonUpmUpdates.stream().map(AvailableAddonWithVersion.toAddonKey()).collect(Collectors.toList())));
            }
            if (options.isInstallAutoUpdates() && this.sysPersisted.is(UpmSettings.AUTO_UPDATE_ENABLED) && !this.safeMode.isSafeMode()) {
                List autoUpdatable = StreamSupport.stream(allUpdates.spliterator(), false).filter(this.isAutoUpdateAllowed()).collect(Collectors.toList());
                ArrayList<Pair<AvailableAddonWithVersion, Plugin>> freeUpdatableToPaidBuilder = new ArrayList<Pair<AvailableAddonWithVersion, Plugin>>();
                ArrayList<AvailableAddonWithVersion> autoUpdatesBuilder = new ArrayList<AvailableAddonWithVersion>();
                for (AvailableAddonWithVersion p : autoUpdatable) {
                    for (Plugin installedPlugin : this.pluginRetriever.getPlugin(p.getAddon().getKey())) {
                        if (LicensedPlugins.isFreeUpdatableToPaid(installedPlugin, Option.some(p.getVersion()), this.licensingUsageVerifier)) {
                            freeUpdatableToPaidBuilder.add(Pair.pair(p, installedPlugin));
                            continue;
                        }
                        autoUpdatesBuilder.add(p);
                    }
                }
                List<AvailableAddonWithVersion> autoUpdates = Collections.unmodifiableList(autoUpdatesBuilder);
                List<Pair> freeUpdatableToPaid = Collections.unmodifiableList(freeUpdatableToPaidBuilder);
                if (!autoUpdates.isEmpty()) {
                    this.scheduler.triggerRunnable(new InstallAutoUpdatesTask(autoUpdates), Duration.ZERO, "automatic update");
                }
                for (Pair p : freeUpdatableToPaid) {
                    this.logFailedAutoUpdate((AvailableAddonWithVersion)p.first());
                    this.manualUpdateNotificationService.sendFreeToPaidNotification((AvailableAddonWithVersion)p.first());
                }
            }
            List<AvailableAddonWithVersion> list = filteredUpdates;
            return list;
        }
        catch (Exception e) {
            log.warn("Automatic plugin update check failed", (Throwable)e);
            this.cache.setNotifications(NotificationType.PLUGIN_UPDATE_AVAILABLE, Collections.emptyList());
            List<AvailableAddonWithVersion> list = Collections.emptyList();
            return list;
        }
        finally {
            this.pacClient.forgetPacReachableState(false);
        }
    }

    private void logFailedAutoUpdate(AvailableAddonWithVersion update) {
        String name = update.getAddon().getName();
        String version = (String)update.getVersion().getName().getOrElse((Object)"");
        this.auditLogService.logI18nMessage("upm.auditLog.auto.update.plugin", name, version);
        this.auditLogService.logI18nMessage("upm.auditLog.auto.update.plugin.failure.needs.permission", name, version);
    }

    private Predicate<AvailableAddonWithVersion> isAutoUpdateAllowed() {
        return availableUpdate -> availableUpdate.getVersion().isAutoUpdateAllowed() && this.pluginRetriever.getPlugin(availableUpdate.getAddon().getKey()).isDefined();
    }

    private Predicate<AvailableAddonWithVersion> nonRecentIncompatibleUpdate() {
        return plugin -> {
            for (PluginLicense pluginLicense : this.licenseRepository.getPluginLicense(plugin.getAddon().getKey())) {
                Iterator<Days> iterator = PluginLicenses.getDaysSinceMaintenanceExpiry(pluginLicense).iterator();
                if (!iterator.hasNext()) continue;
                Days days = iterator.next();
                return days.getDays() >= LicensedAttributes.RECENTLY_EXPIRED_DAYS;
            }
            return false;
        };
    }

    private Predicate<AvailableAddonWithVersion> isUpm() {
        return addonWithVersion -> this.upm.getPluginKey().equals(addonWithVersion.getAddon().getKey());
    }

    private Comparator<AvailableAddonWithVersion> orderingWithUpmAlwaysLast() {
        return (a, b) -> {
            if (this.upm.getPluginKey().equals(a.getAddon().getKey())) {
                return 1;
            }
            if (this.upm.getPluginKey().equals(b.getAddon().getKey())) {
                return -1;
            }
            return a.getAddon().getKey().compareTo(b.getAddon().getKey());
        };
    }

    private class InstallUpmAutoUpdateTask
    implements Runnable {
        private final URI completionUri;
        private final File upmFile;

        InstallUpmAutoUpdateTask(URI completionUri, File upmFile) {
            this.completionUri = completionUri;
            this.upmFile = upmFile;
        }

        @Override
        public void run() {
            Either<String, File> result = PluginUpdateCheckerImpl.this.selfUpdateController.executeInternalSelfUpdate(this.completionUri, this.upmFile);
            for (String error : result.left()) {
                log.error("Unable to update UPM: {}", (Object)error);
            }
        }
    }

    private class InstallAutoUpdatesTask
    implements Runnable {
        private final List<AvailableAddonWithVersion> updates;

        InstallAutoUpdatesTask(List<AvailableAddonWithVersion> autoUpdates) {
            this.updates = autoUpdates.stream().sorted(PluginUpdateCheckerImpl.this.orderingWithUpmAlwaysLast()).collect(Collectors.toList());
        }

        @Override
        public void run() {
            boolean didUpdate = false;
            for (AvailableAddonWithVersion update : this.updates) {
                for (URI downloadUri : update.getVersion().getArtifactUri().orElse(update.getVersion().getRemoteDescriptorUri())) {
                    String name = update.getAddon().getName();
                    String version = (String)update.getVersion().getName().getOrElse((Object)"");
                    PluginUpdateCheckerImpl.this.auditLogService.logI18nMessage("upm.auditLog.auto.update.plugin", name, version);
                    try {
                        PluginDownloadService.DownloadResult downloadResult = PluginUpdateCheckerImpl.this.downloadService.downloadPlugin(downloadUri, Option.some(MarketplacePlugins.getPluginNameAndVersion(update)), PluginDownloadService.NULL_TRACKER);
                        File pluginFile = downloadResult.getFile();
                        if (PluginUpdateCheckerImpl.this.selfUpdateController.isUpmPlugin(pluginFile)) {
                            log.warn("Performing automatic update of UPM from version {} to version {}", (Object)PluginUpdateCheckerImpl.this.upm.getVersionString(), (Object)version);
                            Either<String, URI> prepared = PluginUpdateCheckerImpl.this.selfUpdateController.prepareSelfUpdate(pluginFile, true);
                            for (String error : prepared.left()) {
                                log.error("Unable to update UPM: {}", (Object)error);
                            }
                            for (URI completionUri : prepared.right()) {
                                PluginUpdateCheckerImpl.this.scheduler.triggerRunnable(new InstallUpmAutoUpdateTask(completionUri, pluginFile), Duration.ZERO, "automatic UPM self-update");
                            }
                            return;
                        }
                        if (MarketplacePlugins.isLicensedToBeUpdated(update, PluginUpdateCheckerImpl.this.licenseRepository, PluginUpdateCheckerImpl.this.hostLicenseInformation)) {
                            log.warn("Performing automatic update of \"{}\" to version {}", (Object)name, (Object)version);
                            try {
                                PluginUpdateCheckerImpl.this.pluginInstaller.update(pluginFile, downloadUri.toString(), downloadResult.getContentType(), false);
                                didUpdate = true;
                                PluginUpdateCheckerImpl.this.cache.addNotificationForPlugin(NotificationType.AUTO_UPDATED_PLUGIN, update.getAddon().getKey());
                            }
                            catch (Exception e) {
                                log.warn("An error occurred while trying to update \"{}\": {}", (Object)name, (Object)e.toString());
                                log.debug(e.toString(), (Throwable)e);
                            }
                            continue;
                        }
                        log.warn("Would have automatically updated \"{}\" to version {}, but current license does not permit it", (Object)name, (Object)version);
                    }
                    catch (Exception e) {
                        log.warn("Unable to download \"{}\" from {}; automatic update cancelled", (Object)name, (Object)downloadUri);
                        log.debug(e.toString(), (Throwable)e);
                    }
                }
            }
            if (didUpdate) {
                PluginUpdateCheckerImpl.this.checkForUpdates(PluginUpdateChecker.UpdateCheckOptions.options().userInitiated(false).updateNotifications(true).installAutoUpdates(false));
            }
        }
    }
}

