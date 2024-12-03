/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.upm.core.rest.resources.install;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.async.AsyncTask;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.async.AsyncTaskType;
import com.atlassian.upm.core.rest.PluginRestUninstaller;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BulkUninstallTask
implements AsyncTask {
    private final Iterable<String> pluginKeys;
    private final PluginRetriever pluginRetriever;
    private final PluginRestUninstaller uninstaller;

    public BulkUninstallTask(Iterable<String> pluginKeys, PluginRetriever pluginRetriever, PluginRestUninstaller uninstaller) {
        this.pluginKeys = ImmutableList.copyOf(Objects.requireNonNull(pluginKeys, "pluginKeys"));
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.uninstaller = Objects.requireNonNull(uninstaller, "installer");
    }

    @Override
    public AsyncTaskType getType() {
        return AsyncTaskType.UNINSTALL;
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return AsyncTaskStatus.empty();
    }

    @Override
    public AsyncTaskStatus run(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        Iterable plugins = StreamSupport.stream(this.pluginKeys.spliterator(), false).map(this.pluginRetriever::getPlugin).filter(Option::isDefined).map(Option::get).collect(Collectors.toList());
        PluginRestUninstaller.BulkUninstallProgressTracker tracker = progress -> statusUpdater.updateStatus(AsyncTaskStatus.builder().progress(Option.some(Float.valueOf((float)progress.getCompleted() / (float)progress.getTotal()))).build());
        return this.uninstaller.uninstall(plugins, tracker);
    }
}

