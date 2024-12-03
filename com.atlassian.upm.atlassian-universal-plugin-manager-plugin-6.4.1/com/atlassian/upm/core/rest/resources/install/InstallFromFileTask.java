/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.upm.core.rest.resources.install;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.SelfUpdateController;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.async.AsyncTaskStatusUpdater;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.resources.install.InstallTask;
import java.io.File;
import java.util.Objects;

public class InstallFromFileTask
extends InstallTask {
    private final File plugin;

    public InstallFromFileTask(Option<String> fileName, File plugin, PluginInstallationService pluginInstaller, SelfUpdateController selfUpdateController, BaseUriBuilder uriBuilder, ApplicationPluginsManager applicationPluginsManager, I18nResolver i18nResolver) {
        super(fileName, pluginInstaller, selfUpdateController, uriBuilder, applicationPluginsManager, i18nResolver);
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    @Override
    public AsyncTaskStatus getInitialStatus() {
        return this.installingStatus(this.getSource());
    }

    @Override
    protected AsyncTaskStatus executeTask(AsyncTaskStatusUpdater statusUpdater) throws Exception {
        return this.installFromFile(this.plugin, this.getSource(), Option.none(String.class), this.getInitialStatus(), statusUpdater);
    }
}

