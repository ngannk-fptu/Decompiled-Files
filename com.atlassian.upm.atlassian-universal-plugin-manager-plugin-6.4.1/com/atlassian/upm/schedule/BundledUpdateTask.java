/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.ResponseException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.schedule;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.upm.api.util.Either;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.BundledUpdateInfo;
import com.atlassian.upm.core.BundledUpdateInfoStore;
import com.atlassian.upm.core.PluginDownloadService;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.SafeModeException;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.async.TaskSubitemFailure;
import com.atlassian.upm.core.async.TaskSubitemSuccess;
import com.atlassian.upm.spi.PluginInstallException;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundledUpdateTask
implements AsyncTask {
    private static final Logger log = LoggerFactory.getLogger(BundledUpdateTask.class);
    private final BundledUpdateInfo bundledUpdateInfo;
    private final BundledUpdateInfoStore bundledUpdateInfoStore;
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;
    private final PluginDownloadService pluginDownloadService;
    private final PluginInstallationService pluginInstallationService;

    public BundledUpdateTask(BundledUpdateInfo bundledUpdateInfo, BundledUpdateInfoStore bundledUpdateInfoStore, I18nResolver i18nResolver, PluginAccessor pluginAccessor, PluginDownloadService pluginDownloadService, PluginInstallationService pluginInstallationService) {
        this.bundledUpdateInfo = Objects.requireNonNull(bundledUpdateInfo, "bundledUpdateInfo");
        this.bundledUpdateInfoStore = Objects.requireNonNull(bundledUpdateInfoStore, "bundledUpdateInfoStore");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor, "pluginAccessor");
        this.pluginDownloadService = Objects.requireNonNull(pluginDownloadService, "pluginDownloadService");
        this.pluginInstallationService = Objects.requireNonNull(pluginInstallationService, "pluginInstallationService");
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.empty();
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.UPDATE_BUNDLED;
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        int index = 0;
        int count = (int)StreamSupport.stream(this.bundledUpdateInfo.getUpdateItems().spliterator(), false).count();
        AsyncTaskStatus.Builder statusBuilder = AsyncTaskStatus.builder().itemsTotal(Option.some(count));
        ArrayList<TaskSubitemSuccess> successes = new ArrayList<TaskSubitemSuccess>();
        ArrayList<TaskSubitemFailure> failures = new ArrayList<TaskSubitemFailure>();
        for (BundledUpdateInfo.UpdateItem updateItem : this.bundledUpdateInfo.getUpdateItems()) {
            statusBuilder.itemsDone(Option.some(index));
            Option<Plugin> maybePlugin = Option.option(this.pluginAccessor.getPlugin(updateItem.getPluginKey()));
            for (Plugin plugin : maybePlugin) {
                Either<TaskSubitemFailure, TaskSubitemSuccess> result = this.doUpdateItem(updateItem, plugin, statusBuilder.build(), statusUpdater);
                for (TaskSubitemSuccess s : result.right()) {
                    successes.add(s);
                    statusBuilder.successes(Option.some(Collections.unmodifiableList(successes)));
                }
                for (TaskSubitemFailure f : result.left()) {
                    failures.add(f);
                    statusBuilder.failures(Option.some(Collections.unmodifiableList(failures)));
                }
            }
            ++index;
        }
        List<TaskSubitemFailure> fs = Collections.unmodifiableList(failures);
        if (fs.isEmpty()) {
            this.bundledUpdateInfoStore.setUpdateInfo(Option.none(BundledUpdateInfo.class));
        } else {
            this.bundledUpdateInfoStore.setUpdateInfo(Option.some(this.retainOnlyFailedUpdates(this.bundledUpdateInfo, fs)));
        }
        return statusBuilder.itemsDone(Option.some(count)).build();
    }

    private Either<TaskSubitemFailure, TaskSubitemSuccess> doUpdateItem(BundledUpdateInfo.UpdateItem updateItem, Plugin plugin, AsyncTaskStatus baseStatus, AsyncTaskStatusUpdater statusUpdater) {
        if (plugin.getPluginInformation().getVersion().equals(updateItem.getVersion())) {
            log.warn(this.i18nResolver.getText("upm.bundledUpdate.status.skipped", new Serializable[]{updateItem.getName(), updateItem.getVersion()}));
            return Either.right(this.makeSuccess(updateItem));
        }
        try {
            AsyncTaskStatus.Builder downloadingStatusBuilder = AsyncTaskStatus.builder(baseStatus).description(Option.some(this.i18nResolver.getText("upm.bundledUpdate.status.downloading", new Serializable[]{updateItem.getName(), updateItem.getVersion()})));
            statusUpdater.updateStatus(downloadingStatusBuilder.build());
            PluginDownloadService.DownloadResult downloadResult = this.pluginDownloadService.downloadPlugin(updateItem.getUri(), Option.some(updateItem.getName()), this.downloadProgressTracker(downloadingStatusBuilder, statusUpdater));
            AsyncTaskStatus.Builder updatingStatusBuilder = AsyncTaskStatus.builder(baseStatus).description(Option.some(this.i18nResolver.getText("upm.bundledUpdate.status.installing", new Serializable[]{updateItem.getName(), updateItem.getVersion()})));
            statusUpdater.updateStatus(updatingStatusBuilder.build());
            this.pluginInstallationService.update(downloadResult.getFile(), updateItem.getName() + " " + updateItem.getVersion(), downloadResult.getContentType(), false);
            statusUpdater.updateStatus(updatingStatusBuilder.progress(Option.some(Float.valueOf(1.0f))).build());
            log.warn(this.i18nResolver.getText("upm.bundledUpdate.status.done", new Serializable[]{updateItem.getName(), updateItem.getVersion()}));
            return Either.right(this.makeSuccess(updateItem));
        }
        catch (FileNotFoundException e) {
            return Either.left(this.makeFailure(updateItem, TaskSubitemFailure.Type.DOWNLOAD));
        }
        catch (PluginInstallException e) {
            return Either.left(this.makeFailure(updateItem, TaskSubitemFailure.Type.INSTALL));
        }
        catch (SafeModeException e) {
            return Either.left(this.makeFailure(updateItem, TaskSubitemFailure.Type.INSTALL));
        }
        catch (ResponseException e) {
            return Either.left(this.makeFailure(updateItem, TaskSubitemFailure.Type.DOWNLOAD));
        }
    }

    private PluginDownloadService.ProgressTracker downloadProgressTracker(final AsyncTaskStatus.Builder statusBuilder, final AsyncTaskStatusUpdater statusUpdater) {
        return new PluginDownloadService.ProgressTracker(){

            @Override
            public void notify(PluginDownloadService.Progress progress) {
                statusUpdater.updateStatus(statusBuilder.progressForDownload(Option.some(progress), 0.0f, 1.0f).build());
            }

            @Override
            public void redirectedTo(URI newUri) {
            }
        };
    }

    private TaskSubitemFailure makeFailure(BundledUpdateInfo.UpdateItem updateItem, TaskSubitemFailure.Type type) {
        return new TaskSubitemFailure(type, updateItem.getName(), updateItem.getPluginKey(), updateItem.getVersion(), null, null, updateItem.getUri().toString());
    }

    private TaskSubitemSuccess makeSuccess(BundledUpdateInfo.UpdateItem updateItem) {
        return new TaskSubitemSuccess(updateItem.getName(), updateItem.getPluginKey(), updateItem.getVersion(), null);
    }

    private BundledUpdateInfo retainOnlyFailedUpdates(BundledUpdateInfo bundledUpdateInfo, Iterable<TaskSubitemFailure> failures) {
        return new BundledUpdateInfo(bundledUpdateInfo.getPlatformTargetBuildNumber(), StreamSupport.stream(bundledUpdateInfo.getUpdateItems().spliterator(), false).filter(item -> {
            for (TaskSubitemFailure f : failures) {
                if (!f.getKey().equals(item.getPluginKey())) continue;
                return true;
            }
            return false;
        }).collect(Collectors.toList()));
    }
}

