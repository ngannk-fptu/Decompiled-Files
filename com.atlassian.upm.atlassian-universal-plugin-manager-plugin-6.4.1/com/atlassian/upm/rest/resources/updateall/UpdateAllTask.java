/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources.updateall;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.marketplace.client.model.AddonVersion;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.UpmFugueConverters;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.api.license.HostLicenseInformation;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStage;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.async.TaskSubitemFailure;
import com.atlassian.upm.core.async.TaskSubitemSuccess;
import com.atlassian.upm.core.install.AccessDeniedException;
import com.atlassian.upm.core.install.LegacyPluginsUnsupportedException;
import com.atlassian.upm.core.install.RelativeURIException;
import com.atlassian.upm.core.install.UnknownPluginTypeException;
import com.atlassian.upm.core.install.UnrecognisedPluginVersionException;
import com.atlassian.upm.core.install.UnsupportedProtocolException;
import com.atlassian.upm.core.install.XmlPluginsUnsupportedException;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.license.PluginLicenses;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginService;
import com.atlassian.upm.license.internal.impl.role.RoleBasedLicensingPluginServiceUtil;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmLinkBuilder;
import com.atlassian.upm.schedule.PluginUpdateCheckJob;
import com.atlassian.upm.schedule.UpmScheduler;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UpdateAllTask
implements AsyncTask {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PacClient pacClient;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRetriever pluginRetriever;
    private final PluginInstallationService pluginInstaller;
    private final PluginDownloadService pluginDownloadService;
    private final UpmScheduler upmScheduler;
    private final AuditLogService auditLogger;
    private final UpmLinkBuilder linkBuilder;
    private final UpmUriBuilder uriBuilder;
    private final PluginLicenseRepository licenseRepository;
    private final UpmInformation upm;
    private final PluginMetadataAccessor metadata;
    private final RoleBasedLicensingPluginService roleBasedService;
    private final HostLicenseInformation hostLicenseInformation;
    private final UserKey userKey;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public UpdateAllTask(PacClient pacClient, PermissionEnforcer permissionEnforcer, PluginDownloadService pluginDownloadService, PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, PluginInstallationService pluginInstaller, UpmScheduler upmScheduler, AuditLogService auditLogger, UpmLinkBuilder linkBuilder, UpmUriBuilder uriBuilder, PluginLicenseRepository licenseRepository, RoleBasedLicensingPluginService roleBasedService, UpmInformation upm, HostLicenseInformation hostLicenseInformation, UserKey userKey, LicensingUsageVerifier licensingUsageVerifier) {
        this.permissionEnforcer = permissionEnforcer;
        this.pluginDownloadService = pluginDownloadService;
        this.pluginRetriever = pluginRetriever;
        this.metadata = metadata;
        this.pluginInstaller = pluginInstaller;
        this.upmScheduler = upmScheduler;
        this.pacClient = pacClient;
        this.auditLogger = auditLogger;
        this.linkBuilder = linkBuilder;
        this.uriBuilder = uriBuilder;
        this.licenseRepository = licenseRepository;
        this.roleBasedService = roleBasedService;
        this.upm = upm;
        this.hostLicenseInformation = hostLicenseInformation;
        this.userKey = userKey;
        this.licensingUsageVerifier = licensingUsageVerifier;
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.FINDING)).build();
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.UPDATE_ALL;
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) {
        try {
            return this.update(this.download(this.findUpdates(), statusUpdater), statusUpdater);
        }
        catch (MpacException pe) {
            this.logger.error("Failed to find available updates: " + pe);
            return AsyncTaskStatus.builder().errorByCode("err.finding.updates").build();
        }
        catch (Throwable t) {
            this.logger.error("Failed to update all plugins", t);
            return AsyncTaskStatus.builder().errorByCode("unexpected.exception").build();
        }
    }

    private List<AvailableAddonWithVersion> findUpdates() throws MpacException {
        Collection<AvailableAddonWithVersion> allUpdates = this.pacClient.getUpdates();
        List pluginPairs = allUpdates.stream().map(this::lookupInstalledPlugins).filter(Objects::nonNull).collect(Collectors.toList());
        return Collections.unmodifiableList(pluginPairs.stream().filter(pair -> !this.isUpmPlugin((Plugin)pair.second())).filter(this::isNotFreeUpdatableToPaid).map(Pair::first).collect(Collectors.toList()));
    }

    private boolean isUpmPlugin(Plugin plugin) {
        return this.upm.getPluginKey().equals(plugin.getKey());
    }

    private boolean isNotFreeUpdatableToPaid(Pair<AvailableAddonWithVersion, Plugin> pair) {
        return !com.atlassian.upm.license.impl.LicensedPlugins.isFreeUpdatableToPaid(pair.second(), Option.some(pair.first().getVersion()), this.licensingUsageVerifier);
    }

    private Pair<AvailableAddonWithVersion, Plugin> lookupInstalledPlugins(AvailableAddonWithVersion update) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(update.getAddon().getKey()).iterator();
        if (iterator.hasNext()) {
            Plugin installedPlugin = iterator.next();
            return Pair.pair(update, installedPlugin);
        }
        return null;
    }

    static AsyncTaskStatus downloading(AvailableAddonWithVersion update, Option<PluginDownloadService.Progress> progress, int numberComplete, int totalUpdates) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.DOWNLOADING)).resourceName(Option.some(update.getAddon().getName())).resourceVersion(UpmFugueConverters.toUpmOption(update.getVersion().getName())).itemsDone(Option.some(numberComplete)).itemsTotal(Option.some(totalUpdates)).progressForDownload(progress, 0.0f, 1.0f).build();
    }

    private List<Pair<AvailableAddonWithVersion, Either<PluginDownloadService.DownloadResult, TaskSubitemFailure>>> download(List<AvailableAddonWithVersion> updates, AsyncTaskStatusUpdater statusUpdater) {
        ArrayList<Pair<AvailableAddonWithVersion, Object>> updateFiles = new ArrayList<Pair<AvailableAddonWithVersion, Object>>();
        for (AvailableAddonWithVersion update : updates) {
            int numberComplete = updateFiles.size();
            Either<Object, TaskSubitemFailure> result = null;
            Addon addon = update.getAddon();
            AddonVersion version = update.getVersion();
            Option<Object> maybeUri = Option.none();
            if (version.isDeployable()) {
                maybeUri = UpmFugueConverters.toUpmOption(version.getArtifactUri());
            }
            for (URI uri : maybeUri) {
                try {
                    if (this.permissionEnforcer.hasInProcessInstallationFromUriPermission(this.userKey, uri)) {
                        statusUpdater.updateStatus(UpdateAllTask.downloading(update, Option.none(PluginDownloadService.Progress.class), numberComplete, updates.size()));
                        result = Either.left(this.pluginDownloadService.downloadPlugin(uri, Option.some(MarketplacePlugins.getPluginNameAndVersion(update)), this.newProgressTracker(statusUpdater, update, numberComplete, updates.size())));
                        continue;
                    }
                    this.logger.warn("Unpermitted to install update: " + uri);
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "install.failed", update));
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
                catch (AccessDeniedException e) {
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.DOWNLOAD, "access.denied", update));
                    this.logger.error("Access denied when downloading " + uri.toASCIIString(), (Throwable)((Object)e));
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
                catch (UnsupportedProtocolException e) {
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.DOWNLOAD, "unsupported.protocol", update, e.getMessage()));
                    this.logger.error("Failed to download plugin " + uri.toASCIIString(), (Throwable)((Object)e));
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
                catch (RelativeURIException e) {
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.DOWNLOAD, "invalid.relative.uri", update, e.getMessage()));
                    this.logger.error("Failed to download plugin " + uri.toASCIIString(), (Throwable)((Object)e));
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
                catch (FileNotFoundException e) {
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.DOWNLOAD, "file.not.found", update, e.getMessage()));
                    this.logger.error("Failed to download plugin " + uri.toASCIIString(), (Throwable)e);
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
                catch (ResponseException e) {
                    result = Either.right(this.makeFailure(TaskSubitemFailure.Type.DOWNLOAD, "response.exception", update, e.getMessage()));
                    this.logger.error("Failed to download " + uri.toASCIIString(), (Throwable)e);
                    this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), uri.toASCIIString());
                }
            }
            if (result == null) {
                this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", addon.getName(), "");
                result = Either.right(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "not.deployable", update));
            }
            updateFiles.add(Pair.pair(update, result));
        }
        return Collections.unmodifiableList(updateFiles);
    }

    private PluginDownloadService.ProgressTracker newProgressTracker(final AsyncTaskStatusUpdater statusUpdater, final AvailableAddonWithVersion plugin, final int numberComplete, final int totalUpdates) {
        return new PluginDownloadService.ProgressTracker(){

            @Override
            public void notify(PluginDownloadService.Progress progress) {
                statusUpdater.updateStatus(UpdateAllTask.downloading(plugin, Option.some(progress), numberComplete, totalUpdates));
            }

            @Override
            public void redirectedTo(URI newUri) {
            }
        };
    }

    private TaskSubitemFailure makeFailure(TaskSubitemFailure.Type type, String errorCode, AvailableAddonWithVersion update) {
        return this.makeFailure(type, errorCode, update, null);
    }

    private TaskSubitemFailure makeFailure(TaskSubitemFailure.Type type, String errorCode, AvailableAddonWithVersion update, String message) {
        return new TaskSubitemFailure(type.name(), update.getAddon().getName(), update.getAddon().getKey(), (String)update.getVersion().getName().getOrElse((Object)""), errorCode, message, update.getVersion().getArtifactUri().isDefined() ? ((URI)update.getVersion().getArtifactUri().get()).toASCIIString() : "");
    }

    static AsyncTaskStatus updating(AvailableAddonWithVersion update, int numberComplete, int totalUpdates) {
        return AsyncTaskStatus.builder().stage(Option.some(AsyncTaskStage.APPLYING_ALL)).resourceName(Option.some(update.getAddon().getName())).resourceVersion(UpmFugueConverters.toUpmOption(update.getVersion().getName())).itemsDone(Option.some(numberComplete)).itemsTotal(Option.some(totalUpdates)).build();
    }

    private AsyncTaskStatus update(List<Pair<AvailableAddonWithVersion, Either<PluginDownloadService.DownloadResult, TaskSubitemFailure>>> updates, AsyncTaskStatusUpdater statusUpdater) {
        ArrayList<TaskSubitemSuccess> successes = new ArrayList<TaskSubitemSuccess>();
        ArrayList<TaskSubitemFailure> failures = new ArrayList<TaskSubitemFailure>();
        for (Pair<AvailableAddonWithVersion, Either<PluginDownloadService.DownloadResult, TaskSubitemFailure>> update : updates) {
            AvailableAddonWithVersion mpacPlugin = update.first();
            if (!MarketplacePlugins.isLicensedToBeUpdated(mpacPlugin, this.licenseRepository, this.hostLicenseInformation)) {
                failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "not.licensed.to.be.updated", mpacPlugin));
                this.auditLogger.logI18nMessage("upm.auditLog.update.plugin.failure", mpacPlugin.getAddon().getName());
                continue;
            }
            for (PluginDownloadService.DownloadResult downloaded : update.second().left()) {
                try {
                    int numberComplete = successes.size() + failures.size();
                    int totalUpdates = updates.size();
                    statusUpdater.updateStatus(UpdateAllTask.updating(mpacPlugin, numberComplete, totalUpdates));
                    Plugin plugin = this.pluginInstaller.update(downloaded.getFile(), MarketplacePlugins.getPluginNameAndVersion(mpacPlugin), downloaded.getContentType(), false).getPlugin();
                    String pluginKey = mpacPlugin.getAddon().getKey();
                    LinksMapBuilder links = this.linkBuilder.buildLinkForSelf(this.uriBuilder.buildPluginUri(pluginKey));
                    if (LicensedPlugins.usesLicensing(plugin.getPlugin(), this.licensingUsageVerifier)) {
                        try {
                            for (URI postUpdateUrl : this.metadata.getPostUpdateUri(plugin)) {
                                links.put("post-update", postUpdateUrl);
                            }
                        }
                        catch (IllegalArgumentException illegalArgumentException) {
                            // empty catch block
                        }
                        if (this.permissionEnforcer.hasPermission(Permission.MANAGE_PLUGIN_LICENSE, plugin)) {
                            Option<PluginLicense> license = this.licenseRepository.getPluginLicense(pluginKey);
                            Option<Integer> roleCount = RoleBasedLicensingPluginServiceUtil.getRoleCount(this.roleBasedService, Option.some(plugin.getPlugin()), license);
                            boolean isDataCenter = (Boolean)license.map(l -> l.isDataCenter()).getOrElse(false);
                            boolean carebearSpecificPlugin = this.licensingUsageVerifier.isCarebearSpecificPlugin(plugin.getPlugin());
                            if (PluginLicenses.isPluginTryable(license, carebearSpecificPlugin)) {
                                links.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, Option.some(plugin), "try", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, plugin.getKey(), "try"));
                            }
                            if (PluginLicenses.isPluginBuyable(license, carebearSpecificPlugin)) {
                                links.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, Option.some(plugin), "new", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, plugin.getKey(), "new"));
                            } else if (PluginLicenses.isPluginUpgradable(license, roleCount)) {
                                links.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, Option.some(plugin), "upgrade", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, plugin.getKey(), "upgrade"));
                            } else if (PluginLicenses.isPluginRenewable(license, roleCount)) {
                                links.putIfPermitted(Permission.MANAGE_PLUGIN_LICENSE, Option.some(plugin), "renew", this.uriBuilder.buildMacPluginLicenseUri(isDataCenter, plugin.getKey(), "renew"));
                            }
                        }
                    }
                    successes.add(new TaskSubitemSuccess(mpacPlugin.getAddon().getName(), pluginKey, (String)mpacPlugin.getVersion().getName().getOrElse((Object)""), links.build()));
                }
                catch (LegacyPluginsUnsupportedException lpue) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "legacy.plugins.unsupported", mpacPlugin));
                }
                catch (XmlPluginsUnsupportedException lpue) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "xml.plugins.unsupported", mpacPlugin));
                }
                catch (UnrecognisedPluginVersionException upve) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "unrecognised.plugin.version", mpacPlugin));
                }
                catch (UnknownPluginTypeException upte) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "unknown.plugin.type", mpacPlugin));
                }
                catch (SafeModeException sme) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "safe.mode", mpacPlugin));
                }
                catch (RuntimeException re) {
                    failures.add(this.makeFailure(TaskSubitemFailure.Type.INSTALL, "install.failed", mpacPlugin, re.getMessage()));
                }
            }
            for (TaskSubitemFailure err : update.second().right()) {
                failures.add(err);
            }
        }
        if (!successes.isEmpty()) {
            this.upmScheduler.triggerJob(PluginUpdateCheckJob.class, UpmScheduler.RunMode.TRIGGERED_BY_USER);
        }
        return AsyncTaskStatus.builder().successes(Option.some(Collections.unmodifiableList(successes))).failures(Option.some(Collections.unmodifiableList(failures))).build();
    }
}

