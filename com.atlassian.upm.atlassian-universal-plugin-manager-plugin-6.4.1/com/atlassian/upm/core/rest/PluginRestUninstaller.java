/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginRestartState
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.atlassian.upm.core.rest;

import com.atlassian.plugin.PluginRestartState;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginInstallationService;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.SafeModeAccessor;
import com.atlassian.upm.core.async.AsyncTaskErrorInfo;
import com.atlassian.upm.core.async.AsyncTaskStatus;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class PluginRestUninstaller {
    private final PluginInstallationService pluginInstallationService;
    private final PluginRetriever pluginRetriever;
    private final PluginMetadataAccessor metadata;
    private final SafeModeAccessor safeMode;
    private final PermissionService permissionService;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final ApplicationPluginsManager applicationPluginsManager;
    private static Set<String> applicationRelatedPluginKeys = ImmutableSet.of();

    public PluginRestUninstaller(PluginInstallationService pluginInstallationService, PluginRetriever pluginRetriever, PluginMetadataAccessor metadata, SafeModeAccessor safeMode, PermissionService permissionService, UserManager userManager, I18nResolver i18nResolver, ApplicationPluginsManager applicationPluginsManager) {
        this.permissionService = Objects.requireNonNull(permissionService, "permissionService");
        this.pluginInstallationService = Objects.requireNonNull(pluginInstallationService, "pluginAccessorAndController");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.metadata = Objects.requireNonNull(metadata, "metadata");
        this.safeMode = Objects.requireNonNull(safeMode, "safeMode");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.applicationPluginsManager = Objects.requireNonNull(applicationPluginsManager, "applicationPluginsManager");
    }

    public AsyncTaskStatus uninstall(Iterable<Plugin> plugins, BulkUninstallProgressTracker tracker) {
        applicationRelatedPluginKeys = this.applicationPluginsManager.getApplicationRelatedPluginKeys();
        int completed = 0;
        int total = Iterables.size(plugins);
        for (Plugin p : plugins) {
            Iterator<UninstallError> iterator = this.uninstall(p).iterator();
            if (iterator.hasNext()) {
                UninstallError e = iterator.next();
                String code = e.getType().getErrorCode();
                String message = this.i18nResolver.getText(code, new Serializable[]{e.getPlugin().getName()});
                return AsyncTaskStatus.builder().error(Option.some(new AsyncTaskErrorInfo(code, message))).build();
            }
            tracker.notify(new BulkUninstallProgress(total, ++completed));
        }
        applicationRelatedPluginKeys = ImmutableSet.of();
        return AsyncTaskStatus.builder().completedProgress().build();
    }

    public Option<UninstallError> uninstall(Plugin plugin) {
        String pluginKey = plugin.getKey();
        Iterator<UninstallError> iterator = this.getUninstallationPreconditionError(plugin).iterator();
        if (iterator.hasNext()) {
            UninstallError e1 = iterator.next();
            return Option.some(e1);
        }
        this.pluginInstallationService.uninstall(plugin);
        return this.getUninstallationPostconditionError(pluginKey);
    }

    private Option<UninstallError> getUninstallationPreconditionError(Plugin plugin) {
        Permission permission = Permission.MANAGE_PLUGIN_UNINSTALL;
        Iterator<PermissionService.PermissionError> iterator = this.permissionService.getPermissionError(UserAttributes.fromCurrentUser(this.userManager), permission, plugin).iterator();
        if (iterator.hasNext()) {
            PermissionService.PermissionError error = iterator.next();
            return Option.some(UninstallError.create(error, plugin));
        }
        if (plugin.isUpmPlugin()) {
            return Option.some(UninstallError.create(UninstallError.Type.PLUGIN_IS_UPM, plugin));
        }
        if (!this.metadata.isUserInstalled(plugin)) {
            return Option.some(UninstallError.create(UninstallError.Type.SYSTEM_PLUGIN, plugin));
        }
        if (plugin.isStaticPlugin()) {
            return Option.some(UninstallError.create(UninstallError.Type.STATIC_PLUGIN, plugin));
        }
        if (!plugin.isUninstallable()) {
            return Option.some(UninstallError.create(UninstallError.Type.NOT_UNINSTALLABLE, plugin));
        }
        if (this.safeMode.isSafeMode() && !applicationRelatedPluginKeys.contains(plugin.getKey())) {
            return Option.some(UninstallError.create(UninstallError.Type.SAFE_MODE, plugin));
        }
        return Option.none();
    }

    private Option<UninstallError> getUninstallationPostconditionError(String pluginKey) {
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin remainingPlugin = iterator.next();
            if (PluginRestartState.REMOVE.equals((Object)remainingPlugin.getRestartState())) {
                return Option.some(UninstallError.create(UninstallError.Type.REQUIRES_RESTART, remainingPlugin));
            }
            return Option.some(UninstallError.create(UninstallError.Type.UNKNOWN_FAILURE, remainingPlugin));
        }
        return Option.none();
    }

    public static interface BulkUninstallProgressTracker {
        public void notify(BulkUninstallProgress var1);
    }

    public static final class BulkUninstallProgress {
        private final int total;
        private final int completed;

        BulkUninstallProgress(int total, int completed) {
            this.total = total;
            this.completed = completed;
        }

        public int getTotal() {
            return this.total;
        }

        public int getCompleted() {
            return this.completed;
        }
    }

    public static class UninstallError {
        private final Type type;
        private final Plugin plugin;
        private final Option<PermissionService.PermissionError> permissionError;

        private UninstallError(Type type, Plugin plugin, Option<PermissionService.PermissionError> permissionError) {
            this.type = type;
            this.plugin = plugin;
            this.permissionError = permissionError;
        }

        public static UninstallError create(PermissionService.PermissionError e, Plugin plugin) {
            return new UninstallError(Type.PERMISSION_ERROR, plugin, Option.some(e));
        }

        public static UninstallError create(Type type, Plugin plugin) {
            return new UninstallError(type, plugin, Option.none(PermissionService.PermissionError.class));
        }

        public Type getType() {
            return this.type;
        }

        public Option<PermissionService.PermissionError> getPermissionError() {
            return this.permissionError;
        }

        public Plugin getPlugin() {
            return this.plugin;
        }

        public static enum Type {
            PERMISSION_ERROR,
            PLUGIN_IS_UPM,
            SYSTEM_PLUGIN,
            STATIC_PLUGIN,
            NOT_UNINSTALLABLE,
            SAFE_MODE,
            REQUIRES_RESTART,
            UNKNOWN_FAILURE;


            public String getErrorCode() {
                switch (this) {
                    case PERMISSION_ERROR: {
                        return "upm.pluginUninstall.error.insufficient.permission";
                    }
                    case PLUGIN_IS_UPM: {
                        return "upm.plugin.error.invalid.upm.plugin.action";
                    }
                    case SYSTEM_PLUGIN: {
                        return "upm.pluginUninstall.error.plugin.is.system";
                    }
                    case STATIC_PLUGIN: {
                        return "upm.pluginUninstall.error.plugin.is.static";
                    }
                    case NOT_UNINSTALLABLE: {
                        return "upm.pluginUninstall.error.plugin.not.uninstallable";
                    }
                    case SAFE_MODE: {
                        return "upm.pluginUninstall.error.safe.mode";
                    }
                    case REQUIRES_RESTART: {
                        return "upm.messages.uninstall.requiresRestart";
                    }
                }
                return "upm.pluginUninstall.error.failed.to.uninstall";
            }
        }
    }
}

