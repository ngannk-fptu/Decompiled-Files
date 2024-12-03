/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.RenderContextProvider
 *  com.atlassian.fugue.Function2
 *  com.atlassian.fugue.Functions
 *  com.atlassian.fugue.Pair
 *  com.atlassian.healthcheck.core.Application
 *  com.atlassian.healthcheck.core.HealthCheck
 *  com.atlassian.healthcheck.core.HealthStatus
 *  com.atlassian.healthcheck.core.HealthStatusExtended$Severity
 *  com.atlassian.healthcheck.core.HealthStatusFactory
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.WebResourceModuleDescriptor
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.notifications.content.healthcheck;

import com.atlassian.confluence.notifications.RenderContextProvider;
import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Functions;
import com.atlassian.fugue.Pair;
import com.atlassian.healthcheck.core.Application;
import com.atlassian.healthcheck.core.HealthCheck;
import com.atlassian.healthcheck.core.HealthStatus;
import com.atlassian.healthcheck.core.HealthStatusExtended;
import com.atlassian.healthcheck.core.HealthStatusFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.WebResourceModuleDescriptor;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class NotificationTemplateModuleEnabledHealthCheck
implements HealthCheck {
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-content-notifications-plugin";
    private final PluginAccessor pluginAccessor;
    private final HealthStatusFactory healthStatusFactory;

    public NotificationTemplateModuleEnabledHealthCheck(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
        this.healthStatusFactory = new HealthStatusFactory(Application.Plugin, "");
    }

    public HealthStatus check() {
        Function toModuleStatusFunction = input -> {
            boolean templateDescriptorEnabled;
            String templateModuleKey = input.getCompleteKey();
            String webResourceModuleKey = templateModuleKey + "-body";
            ModuleDescriptor wrm = this.pluginAccessor.getEnabledPluginModule(webResourceModuleKey);
            boolean webResouceModuleEnabled = wrm instanceof WebResourceModuleDescriptor;
            boolean isInSync = webResouceModuleEnabled == (templateDescriptorEnabled = this.pluginAccessor.isPluginModuleEnabled(templateModuleKey));
            return Pair.pair((Object)webResourceModuleKey, (Object)isInSync);
        };
        Function2 combineHeathStatusFunction = (accumulatedStatus, module) -> {
            if (!((Boolean)module.right()).booleanValue()) {
                String failureReason = String.format("%smodule %s is disabled\n", accumulatedStatus.isHealthy() ? "" : accumulatedStatus.failureReason(), module.left());
                return this.healthStatusFactory.failed(failureReason, HealthStatusExtended.Severity.MAJOR);
            }
            return accumulatedStatus;
        };
        Iterable moduleStates = Iterables.transform((Iterable)this.pluginAccessor.getPlugin(PLUGIN_KEY).getModuleDescriptorsByModuleClass(RenderContextProvider.class), (Function)toModuleStatusFunction);
        return (HealthStatus)Functions.fold((Function2)combineHeathStatusFunction, (Object)this.healthStatusFactory.healthy(), (Iterable)moduleStates);
    }
}

