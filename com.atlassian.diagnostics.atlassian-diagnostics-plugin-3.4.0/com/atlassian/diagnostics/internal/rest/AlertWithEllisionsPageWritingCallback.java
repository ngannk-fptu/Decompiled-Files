/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Alert
 *  com.atlassian.diagnostics.AlertTrigger
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.PluginDetails
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  javax.ws.rs.core.UriBuilder
 *  org.codehaus.jackson.JsonGenerator
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Alert;
import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.diagnostics.internal.rest.AlertPageWritingCallback;
import com.atlassian.diagnostics.internal.rest.RestIssue;
import com.atlassian.diagnostics.internal.rest.RestPluginDetails;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.ws.rs.core.UriBuilder;
import org.codehaus.jackson.JsonGenerator;

class AlertWithEllisionsPageWritingCallback
extends AlertPageWritingCallback {
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;
    private Alert firstAlert;

    AlertWithEllisionsPageWritingCallback(JsonGenerator generator, Supplier<UriBuilder> uriBuilderSupplier, I18nResolver i18nResolver, PluginAccessor pluginAccessor) {
        super(generator, uriBuilderSupplier, "issue");
        this.i18nResolver = i18nResolver;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    @Nonnull
    public CallbackResult onItem(@Nonnull Alert alert) {
        if (this.firstAlert == null) {
            this.firstAlert = alert;
        }
        return super.onItem(alert);
    }

    @Override
    protected void writeAdditionalEntities() {
        if (this.firstAlert != null) {
            this.generator.writeObjectField("issue", new RestIssue(this.firstAlert.getIssue()));
            this.generator.writeObjectField("plugin", new RestPluginDetails(this.getPluginDetails(this.firstAlert.getTrigger())));
        }
    }

    private PluginDetails getPluginDetails(AlertTrigger trigger) {
        Plugin plugin;
        String key = trigger.getPluginKey();
        String pluginName = "not-detected".equals(key) ? this.i18nResolver.getText("diagnostics.plugin.not.detected") : ((plugin = this.pluginAccessor.getPlugin(key)) == null ? key : plugin.getName());
        return new PluginDetails(key, pluginName, (String)trigger.getPluginVersion().orElse(null));
    }
}

