/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest.representations;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.async.AsynchronousTaskManager;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.atlassian.upm.core.rest.representations.BaseLinkBuilder;
import com.atlassian.upm.core.rest.representations.LinksMapBuilder;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.net.URI;
import java.util.Objects;

public class DefaultLinkBuilder
extends BaseLinkBuilder {
    private final BaseUriBuilder uriBuilder;
    private final PluginRestartRequiredService restartRequiredService;
    private final AsynchronousTaskManager asynchronousTaskManager;
    private final PluginMetadataAccessor metadata;
    protected final UpmAppManager appManager;

    public DefaultLinkBuilder(BaseUriBuilder uriBuilder, PluginRestartRequiredService restartRequiredService, AsynchronousTaskManager asynchronousTaskManager, PermissionEnforcer permissionEnforcer, PluginMetadataAccessor metadata, UpmAppManager appManager) {
        super(permissionEnforcer);
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.restartRequiredService = Objects.requireNonNull(restartRequiredService, "restartRequiredService");
        this.asynchronousTaskManager = Objects.requireNonNull(asynchronousTaskManager, "asynchronousTaskManager");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.appManager = Objects.requireNonNull(appManager, "appManager");
    }

    public LinksMapBuilder buildLinksFor(URI selfLink) {
        return this.buildLinksFor(selfLink, true);
    }

    public LinksMapBuilder buildLinksFor(URI selfLink, boolean addConditionalLinks) {
        LinksMapBuilder builder = this.buildLinkForSelf(selfLink);
        if (addConditionalLinks) {
            this.addPendingTaskLinkIfAble(builder);
            this.addChangesRequiringRestartLinkIfAble(builder);
        }
        return builder;
    }

    private LinksMapBuilder addChangesRequiringRestartLinkIfAble(LinksMapBuilder builder) {
        if (this.restartRequiredService.hasChangesRequiringRestart()) {
            builder.put("changes-requiring-restart", this.uriBuilder.buildChangesRequiringRestartUri());
        }
        return builder;
    }

    private LinksMapBuilder addPendingTaskLinkIfAble(LinksMapBuilder builder) {
        if (this.asynchronousTaskManager.hasPendingTasks()) {
            builder.put("pending-tasks", this.uriBuilder.buildLegacyPendingTasksUri());
        }
        return builder;
    }

    public LinksMapBuilder buildLinksForInstalledPlugin(Plugin plugin) {
        Option<Plugin> pluginOption = Option.some(plugin);
        LinksMapBuilder builder = this.buildLinkForSelf(this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-summary", this.uriBuilder.buildPluginSummaryUri(plugin.getKey())).putIfPermitted(Permission.MANAGE_PLUGIN_ENABLEMENT, pluginOption, "modify", this.uriBuilder.buildPluginUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-icon", this.uriBuilder.buildPluginIconLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "plugin-logo", this.uriBuilder.buildPluginLogoLocationUri(plugin.getKey())).putIfPermitted(Permission.GET_INSTALLED_PLUGINS, pluginOption, "manage", this.uriBuilder.buildUpmUri(plugin.getKey()));
        builder.putIfPermittedAndConditioned(Permission.MANAGE_PLUGIN_UNINSTALL, pluginOption, plugin.isUninstallable(), "delete", this.uriBuilder.buildPluginUri(plugin.getKey()));
        builder.put("configure", this.metadata.getConfigureUrl(plugin));
        if (Plugins.hasRestartRequiredChange(plugin)) {
            builder.put("change-requiring-restart", this.uriBuilder.buildChangeRequiringRestart(plugin.getKey()));
        }
        return builder;
    }
}

