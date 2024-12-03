/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.plugin.AsyncPluginDisableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginEnableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginInstallEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginModuleDisableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginModuleEnableEvent;
import com.atlassian.confluence.event.events.plugin.AsyncPluginUninstallEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import java.util.Optional;

public class PluginAuditListener
extends AbstractAuditListener {
    public static final String PLUGIN_INSTALLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.installed");
    public static final String PLUGIN_UNINSTALLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.uninstalled");
    public static final String PLUGIN_ENABLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.enabled");
    public static final String PLUGIN_DISABLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.disabled");
    public static final String PLUGIN_MODULE_ENABLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.module.enabled");
    public static final String PLUGIN_MODULE_DISABLED_SUMMARY = AuditHelper.buildSummaryTextKey("plugin.module.disabled");
    private final PluginAccessor pluginAccessor;

    public PluginAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, PluginAccessor pluginAccessor, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
        this.pluginAccessor = pluginAccessor;
    }

    @EventListener
    public void pluginInstallEvent(AsyncPluginInstallEvent event) {
        this.saveIfPresent(() -> this.buildPluginRecord(event, PLUGIN_INSTALLED_SUMMARY));
    }

    @EventListener
    public void pluginUninstallEvent(AsyncPluginUninstallEvent event) {
        this.saveIfPresent(() -> this.buildPluginRecord(event.getPluginName(), PLUGIN_UNINSTALLED_SUMMARY));
    }

    @EventListener
    public void pluginEnableEvent(AsyncPluginEnableEvent event) {
        this.saveIfPresent(() -> this.buildPluginRecord(event, PLUGIN_ENABLED_SUMMARY));
    }

    @EventListener
    public void pluginDisableEvent(AsyncPluginDisableEvent event) {
        this.saveIfPresent(() -> this.buildPluginRecord(event, PLUGIN_DISABLED_SUMMARY));
    }

    @EventListener
    public void pluginModuleEnableEvent(AsyncPluginModuleEnableEvent event) {
        this.saveIfPresent(() -> this.buildPluginModuleEvent(event.getCompleteModuleKey(), PLUGIN_MODULE_ENABLED_SUMMARY));
    }

    @EventListener
    public void pluginModuleDisableEvent(AsyncPluginModuleDisableEvent event) {
        this.saveIfPresent(() -> this.buildPluginModuleEvent(event.getCompleteModuleKey(), PLUGIN_MODULE_DISABLED_SUMMARY));
    }

    private Optional<AuditEvent> buildPluginRecord(AsyncPluginEvent event, String summary) {
        Plugin plugin = this.pluginAccessor.getPlugin(event.getPluginKey());
        if (plugin != null) {
            return this.buildPluginRecord(plugin.getName(), summary);
        }
        return Optional.empty();
    }

    private Optional<AuditEvent> buildPluginRecord(String pluginName, String summary) {
        return Optional.of(AuditEvent.fromI18nKeys((String)AuditCategories.PLUGINS, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(pluginName, this.resourceTypes.plugin())).build());
    }

    private Optional<AuditEvent> buildPluginModuleEvent(String completeModuleKey, String summary) {
        ModuleDescriptor pluginModule = this.pluginAccessor.getPluginModule(completeModuleKey);
        if (pluginModule != null) {
            return Optional.of(AuditEvent.fromI18nKeys((String)AuditCategories.PLUGINS, (String)summary, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION).affectedObject(this.buildResourceWithoutId(pluginModule.getName(), this.resourceTypes.pluginModule())).affectedObject(this.buildResourceWithoutId(pluginModule.getPlugin().getName(), this.resourceTypes.plugin())).build());
        }
        return Optional.empty();
    }
}

