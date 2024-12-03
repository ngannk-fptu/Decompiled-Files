/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.PluginDetails
 *  com.atlassian.diagnostics.util.CallingBundleResolver
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.osgi.util.OsgiHeaderUtil
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.annotation.Nonnull
 *  org.osgi.framework.Bundle
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.diagnostics.internal.PluginHelper;
import com.atlassian.diagnostics.util.CallingBundleResolver;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.osgi.framework.Bundle;

public class SimplePluginHelper
implements PluginHelper {
    private final CallingBundleResolver callingBundleResolver;
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;
    private final PluginMetadataManager pluginMetadataManager;

    public SimplePluginHelper(CallingBundleResolver callingBundleResolver, I18nResolver i18nResolver, PluginAccessor pluginAccessor, PluginMetadataManager pluginMetadataManager) {
        this.callingBundleResolver = callingBundleResolver;
        this.i18nResolver = i18nResolver;
        this.pluginAccessor = pluginAccessor;
        this.pluginMetadataManager = pluginMetadataManager;
    }

    @Override
    @Nonnull
    public Optional<Bundle> getCallingBundle() {
        return this.callingBundleResolver.getCallingBundle();
    }

    @Override
    @Nonnull
    public PluginDetails getPluginDetails(@Nonnull String pluginKey, String pluginVersion) {
        return new PluginDetails(pluginKey, this.getPluginName(pluginKey), pluginVersion);
    }

    @Override
    @Nonnull
    public String getPluginName(@Nonnull String pluginKey) {
        Objects.requireNonNull(pluginKey, "pluginKey");
        if ("not-detected".equals(pluginKey)) {
            return this.i18nResolver.getText("diagnostics.plugin.not.detected");
        }
        Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
        return plugin == null ? pluginKey : plugin.getName();
    }

    @Override
    public boolean isUserInstalled(Bundle bundle) {
        if (bundle != null) {
            if (bundle.getBundleId() == 0L) {
                return false;
            }
            String pluginKey = OsgiHeaderUtil.getPluginKey((Bundle)bundle);
            Plugin plugin = this.pluginAccessor.getPlugin(pluginKey);
            if (plugin != null) {
                return this.pluginMetadataManager.isUserInstalled(plugin);
            }
        }
        return false;
    }
}

