/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginController
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.PluginController;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.log.AuditLogService;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Objects;

public final class PluginRestartRequiredServiceImpl
implements PluginRestartRequiredService {
    private final PluginRetriever pluginRetriever;
    private final PluginController pluginController;
    private final AuditLogService auditLogger;

    public PluginRestartRequiredServiceImpl(PluginRetriever pluginRetriever, PluginController pluginController, AuditLogService auditLogger) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.pluginController = Objects.requireNonNull(pluginController, "pluginController");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
    }

    @Override
    public Iterable<Change> getRestartRequiredChanges() {
        return ImmutableSet.copyOf((Iterable)Iterables.concat((Iterable)Iterables.transform(this.pluginRetriever.getPlugins(), (Function)new Function<Plugin, Iterable<Change>>(){

            public Iterable<Change> apply(Plugin plugin) {
                return PluginRestartRequiredServiceImpl.this.getRestartRequiredChange(plugin);
            }
        })));
    }

    @Override
    public boolean hasChangesRequiringRestart() {
        return !Iterables.isEmpty(this.getRestartRequiredChanges());
    }

    @Override
    public Option<Change> getRestartRequiredChange(Plugin plugin) {
        return Plugins.hasRestartRequiredChange(plugin) ? Option.some(new Change(plugin, plugin.getRestartState())) : Option.none(Change.class);
    }

    @Override
    public void revertRestartRequiredChange(Plugin plugin) {
        String pluginKey = plugin.getKey();
        try {
            this.pluginController.revertRestartRequiredChange(pluginKey);
            this.auditLogger.logI18nMessage("upm.auditLog.cancelChange.success", pluginKey);
        }
        catch (RuntimeException re) {
            this.auditLogger.logI18nMessage("upm.auditLog.cancelChange.failure", pluginKey);
            throw re;
        }
    }
}

